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

import org.frameworkset.spi.remote.http.HttpRequestProxy;
import org.frameworkset.spi.remote.http.ResponseUtil;
import org.frameworkset.spi.remote.http.reactor.StreamDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author biaoping.yin
 * @Date 2025/10/11
 */
public class StreamTest {
    private static Logger logger = LoggerFactory.getLogger(StreamTest.class);
    public static void main(String[] args) throws InterruptedException {
        //加载配置文件，启动负载均衡器,应用中只需要执行一次
        HttpRequestProxy.startHttpPools("application-stream.properties");
        callDeepseekSimple();
    }
    public static void callDeepseekSimple() throws InterruptedException {
        //定义问题变量
        String message = "介绍一下bboss jobflow";
        //设置模型调用参数，
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", "deepseek-chat");//指定模型

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);
        messages.add(userMessage);

        requestMap.put("messages", messages);
        requestMap.put("stream", true);
        requestMap.put("max_tokens", 2048);
        requestMap.put("temperature", 0.7);
        //通过bboss httpproxy响应式异步交互接口，请求Deepseek模型服务，提交问题
        HttpRequestProxy.streamChatCompletion("/chat/completions",requestMap)
                .doOnSubscribe(subscription -> logger.info("开始订阅流..."))
                .doOnNext(chunk -> System.out.print(chunk)) //打印流式调用返回的问题答案片段
                .doOnComplete(() -> logger.info("\n=== 流完成 ==="))
                .doOnError(error -> logger.error("错误: " + error.getMessage(),error))
                .subscribe();

        // 等待异步操作完成，否则流式异步方法执行后会因为主线程的退出而退出，看不到后续响应的报文
        Thread.sleep(100000000);
    }
    public static void testCustom() throws InterruptedException {
        //定义问题变量
        String message = "介绍一下bboss jobflow";
        //设置模型调用参数，
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", "deepseek-chat");//指定模型

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);
        messages.add(userMessage);

        requestMap.put("messages", messages);
        requestMap.put("stream", true);
        requestMap.put("max_tokens", 2048);
        requestMap.put("temperature", 0.7);
        //通过bboss httpproxy响应式异步交互接口，请求Deepseek模型服务，提交问题，可以自定义每次返回的片段解析方法
        //处理数据行,如果数据已经返回完毕，则返回true，指示关闭对话，否则返回false
        HttpRequestProxy.streamChatCompletion("/chat/completions",requestMap,new StreamDataHandler<String>() {
                    @Override
                    public boolean handle(String line, FluxSink<String> sink) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6).trim();

                            if ("[DONE]".equals(data)) {
                                return true;
                            }
                            if (!data.isEmpty()) {
                                String content = ResponseUtil.parseStreamContentFromData(data);
                                if (content != null && !content.isEmpty()) {
                                    sink.next(content);
                                }
                            }
                        }
                        else{
                            if(logger.isDebugEnabled()) {
                                logger.debug("streamChatCompletion: " + line);
                            }
                        }
                        return false;

                    }
                })
                .doOnSubscribe(subscription -> logger.info("开始订阅流..."))
                .doOnNext(chunk -> System.out.print(chunk)) //打印流式调用返回的问题答案片段
                .doOnComplete(() -> logger.info("\n=== 流完成 ==="))
                .doOnError(error -> logger.error("错误: " + error.getMessage(),error))
                .subscribe();

        // 等待异步操作完成，否则流式异步方法执行后会因为主线程的退出而退出，看不到后续响应的报文
        Thread.sleep(100000000);
    }

}
