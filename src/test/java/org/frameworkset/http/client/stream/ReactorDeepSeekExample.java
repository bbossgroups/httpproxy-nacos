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

import org.frameworkset.spi.remote.http.reactor.ReactorDeepSeekClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ReactorDeepSeekExample {

    public static void main(String[] args) throws InterruptedException {
        String apiKey = "sk-9fca957909d94ed5a9f7852be1aefa2b";
        ReactorDeepSeekClient client = new ReactorDeepSeekClient(apiKey);

        // 示例1: 基本流式调用
        basicStreamExample(client);
//
        // 示例2: 带背压控制的流式调用
         backpressureStreamExample(client);
//
//        // 示例3: 批量流式调用
//         batchStreamExample(client);

        // 等待异步操作完成
//        Thread.sleep(100000000);

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void basicStreamExample(ReactorDeepSeekClient client) throws InterruptedException {
        System.out.println("=== 基本流式调用示例 ===");
        CountDownLatch latch = new CountDownLatch(1);
        client.streamChatCompletion("用Java解释Reactor编程模式")
                .doOnSubscribe(subscription -> System.out.println("开始订阅流..."))
                .doOnNext(chunk -> System.out.print(chunk))
                .doOnComplete(() -> {
                    System.out.println("\n=== 流完成 ===");
                    latch.countDown();
                })
                .doOnError(error ->{ 
                    System.err.println("错误: " + error.getMessage());
                    latch.countDown();})
                .subscribe();
        latch.await();
    }

    private static void backpressureStreamExample(ReactorDeepSeekClient client) throws InterruptedException {
        System.out.println("\n=== 带背压控制的流式调用示例 ===");

        CountDownLatch latch = new CountDownLatch(1);

        client.streamWithBackpressure("讲述一个关于人工智能的短故事", 10)
                .limitRate(5) // 限制请求速率
                .buffer(3)    // 每3个元素缓冲一次
                .doOnNext(buffer -> {
                    System.out.println("\n收到缓冲数据: " + buffer);
                    System.out.println("内容: " + String.join("", buffer));
                })
                .doOnComplete(() -> {
                    System.out.println("背压流完成");
                    latch.countDown();
                })
                .subscribe();

        latch.await();
    }

    private static void batchStreamExample(ReactorDeepSeekClient client) {
        System.out.println("\n=== 批量流式调用示例 ===");

        List<String> messages = Arrays.asList(
                "什么是机器学习？",
                "解释一下深度学习",
                "自然语言处理有哪些应用？"
        );

        client.streamMultipleCompletions(messages)
                .doOnNext(result -> {
                    System.out.println("\n=== 结果 " + result.getIndex() + " ===");
                    System.out.println("问题: " + result.getOriginalMessage());
                    System.out.println("回答: " + result.getResponse());
                })
                .doOnComplete(() -> System.out.println("\n所有批量处理完成"))
                .subscribe();
    }

    private static void advancedOperatorsExample(ReactorDeepSeekClient client) {
        System.out.println("\n=== 高级操作符示例 ===");

        // 使用各种Reactor操作符
        client.streamChatCompletion("用Java写一个快速排序算法")
                .take(Duration.ofSeconds(10)) // 限制处理时间
                .filter(chunk -> chunk != null && !chunk.trim().isEmpty()) // 过滤空内容
                .map(String::trim) // 清理数据
                .window(Duration.ofSeconds(2)) // 按时间窗口分组
                .flatMap(Flux::collectList) // 将窗口内的数据收集为列表
                .index()
                .doOnNext(tuple -> {
                    System.out.printf("\n窗口 %d: %s%n", tuple.getT1(), tuple.getT2());
                })
                .subscribe();
    }

    private static void errorHandlingExample(ReactorDeepSeekClient client) {
        System.out.println("\n=== 错误处理示例 ===");

        // 模拟错误场景
        client.streamChatCompletion("")
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(throwable -> {
                    System.err.println("发生超时，返回默认值");
                    return Flux.just("请求超时，请重试");
                })
                .retry(2) // 重试2次
                .doOnNext(System.out::print)
                .subscribe();
    }
}
