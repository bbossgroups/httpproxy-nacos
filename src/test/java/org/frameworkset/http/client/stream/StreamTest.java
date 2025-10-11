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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        String message = "介绍一下bboss jobflow";
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
        HttpRequestProxy.streamChatCompletion("/chat/completions",requestMap)
                .doOnSubscribe(subscription -> logger.info("开始订阅流..."))
                .doOnNext(chunk -> System.out.print(chunk))
                .doOnComplete(() -> logger.info("\n=== 流完成 ==="))
                .doOnError(error -> logger.error("错误: " + error.getMessage(),error))
                .subscribe();

        // 等待异步操作完成
        Thread.sleep(100000000);
    }

}
