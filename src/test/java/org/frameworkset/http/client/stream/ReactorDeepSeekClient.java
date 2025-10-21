package org.frameworkset.http.client.stream;
/**
 * Copyright 2025 bboss
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author biaoping.yin
 * @Date 2025/10/10
 */


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReactorDeepSeekClient {

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public ReactorDeepSeekClient(String apiKey) {
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * 创建流式调用的Flux
     */
    public Flux<String> streamChatCompletion(String message) {
        return Flux.<String>create(sink -> {
                    try {
                        executeStreamRequest(message, sink);
                    } catch (Exception e) {
                        sink.error(new RuntimeException("流式请求失败", e));
                    }
                }, FluxSink.OverflowStrategy.BUFFER)
                .subscribeOn(Schedulers.boundedElastic()) // 在弹性线程池中执行阻塞IO
                .timeout(Duration.ofSeconds(60)) // 设置超时
                .onErrorResume(throwable -> {
                    System.err.println("流式处理错误: " + throwable.getMessage());
                    return Flux.empty();
                });
    }

    /**
     * 批量流式调用 - 处理多个消息
     */
    public Flux<StreamResult> streamMultipleCompletions(List<String> messages) {
        return Flux.fromIterable(messages)
                .index()
                .flatMap(tuple -> {
                    long index = tuple.getT1();
                    String message = tuple.getT2();
                    return streamChatCompletion(message)
                            .collectList()
                            .map(chunks -> new StreamResult(index, message, String.join("", chunks)))
                            .flux();
                }, 2); // 控制并发数
    }

    /**
     * 带背压控制的流式调用
     */
    public Flux<String> streamWithBackpressure(String message, int bufferSize) {
        return Flux.<String>create(sink -> {
                    final AtomicBoolean cancelled = new AtomicBoolean(false);

                    sink.onCancel(() -> {
                        cancelled.set(true);
                        System.out.println("流被取消");
                    });

                    sink.onRequest(n -> {
                        System.out.println("请求数据量: " + n);
                    });

                    try {
                        executeStreamRequestWithCancellation(message, sink, cancelled);
                    } catch (Exception e) {
                        if (!cancelled.get()) {
                            sink.error(e);
                        }
                    }
                }, FluxSink.OverflowStrategy.BUFFER)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private void executeStreamRequest(String message, FluxSink<String> sink) throws IOException {
        HttpPost httpPost = createHttpPost(message);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getCode() != 200) {
                sink.error(new RuntimeException("API请求失败，状态码: " + response.getCode()));
                return;
            }
            processStreamResponse(response, sink);
        }
    }

    private void executeStreamRequestWithCancellation(String message,
                                                      FluxSink<String> sink,
                                                      AtomicBoolean cancelled) throws IOException {
        HttpPost httpPost = createHttpPost(message);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getCode() != 200) {
                sink.error(new RuntimeException("API请求失败，状态码: " + response.getCode()));
                return;
            }
            processStreamResponseWithCancellation(response, sink, cancelled);
        }
    }

    private HttpPost createHttpPost(String message) throws IOException {
        HttpPost httpPost = new HttpPost("https://api.deepseek.com/chat/completions");
        httpPost.setHeader("Authorization", "Bearer " + apiKey);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "text/event-stream");

        String requestBody = buildRequestBody(message);
        httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
        return httpPost;
    }

    private String buildRequestBody(String message) throws IOException {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", "deepseek-chat");

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);
        messages.add(userMessage);

        requestMap.put("messages", messages);
        requestMap.put("stream", true);
        requestMap.put("max_tokens", 2048);
        requestMap.put("temperature", 0.7);

        return objectMapper.writeValueAsString(requestMap);
    }

    private void processStreamResponse(CloseableHttpResponse response, FluxSink<String> sink) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()))) {

            String line;
            while ((line = reader.readLine()) != null && !sink.isCancelled()) {
                if (line.startsWith("data: ")) {
                    String data = line.substring(6).trim();

                    if ("[DONE]".equals(data)) {
                        sink.complete();
                        break;
                    }

                    if (!data.isEmpty()) {
                        String content = parseContentFromData(data);
                        if (content != null && !content.isEmpty()) {
                            sink.next(content);
                        }
                    }
                }
            }
        }
    }

    private void processStreamResponseWithCancellation(CloseableHttpResponse response,
                                                       FluxSink<String> sink,
                                                       AtomicBoolean cancelled) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()))) {

            String line;
            while ((line = reader.readLine()) != null && !cancelled.get()) {
                if (line.startsWith("data: ")) {
                    String data = line.substring(6).trim();

                    if ("[DONE]".equals(data)) {
                        if (!cancelled.get()) {
                            sink.complete();
                        }
                        break;
                    }

                    if (!data.isEmpty()) {
                        String content = parseContentFromData(data);
                        if (content != null && !content.isEmpty()) {
                            sink.next(content);
                        }
                    }
                }
            }
        }
    }

    private String parseContentFromData(String data) {
        try {
            JsonNode jsonNode = objectMapper.readTree(data);
            JsonNode choices = jsonNode.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode delta = choices.get(0).get("delta");
                if (delta != null && delta.has("content")) {
                    return delta.get("content").asText();
                }
            }
        } catch (Exception e) {
            System.err.println("解析数据块失败: " + e.getMessage());
        }
        return null;
    }

    public void close() throws IOException {
        httpClient.close();
    }

    // 结果封装类
    public static class StreamResult {
        private final long index;
        private final String originalMessage;
        private final String response;

        public StreamResult(long index, String originalMessage, String response) {
            this.index = index;
            this.originalMessage = originalMessage;
            this.response = response;
        }

        // getters
        public long getIndex() { return index; }
        public String getOriginalMessage() { return originalMessage; }
        public String getResponse() { return response; }

        @Override
        public String toString() {
            return String.format("StreamResult{index=%d, originalMessage='%s', response='%s'}",
                    index, originalMessage, response);
        }
    }
}
