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

import com.frameworkset.util.SimpleStringUtil;
import org.frameworkset.spi.remote.http.HttpRequestProxy;
import org.frameworkset.spi.remote.http.ResponseUtil;
import org.frameworkset.spi.remote.http.reactor.BaseStreamDataHandler;
import org.frameworkset.spi.remote.http.reactor.ServerEvent;
import org.frameworkset.spi.remote.http.reactor.StreamDataHandler;
import org.frameworkset.util.concurrent.BooleanWrapperInf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.*;

/**
 * @author biaoping.yin
 * @Date 2025/10/11
 */
public class StreamTest {
    private static Logger logger = LoggerFactory.getLogger(StreamTest.class);
    public static void main(String[] args) throws InterruptedException {
        //加载配置文件，启动负载均衡器,应用中只需要执行一次
        HttpRequestProxy.startHttpPools("application-stream.properties");
//        callDeepseekSimple();
//        callguijiSimple();
//        qwenvl();
        qwenvlCompare();
//        qwenvl();
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

    public static void callguijiSimple() throws InterruptedException {
        //定义问题变量
        String message = "介绍一下bboss jobflow";
        //设置模型调用参数，
        Map<String, Object> requestMap = new HashMap<>();
//        requestMap.put("model", "deepseek-ai/DeepSeek-V3.2-Exp");//指定模型
        requestMap.put("model", "Qwen/Qwen3-Next-80B-A3B-Instruct");//指定模型
        
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
        HttpRequestProxy.streamChatCompletion("guiji","/v1/chat/completions",requestMap)
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
        HttpRequestProxy.streamChatCompletion("/chat/completions",requestMap,new BaseStreamDataHandler<String>() {
                    /**
                     * 处理数据行
                     * @param line 数据行
                     * @param sink 数据行处理结果
                     * @param firstEventTag 是否是第一个事件标记，需要具体实现设置，如果为true，则表示当前数据行是第一个事件标记，否则不是第一个事件标记。
                     *                      在接口方法实现中，在发送消息时，需检测是否为true，如果为true，需设置标记为false，同时将ServerEvent的first标记设置为true
                     * @return
                     */
                    @Override
                    public boolean handle(String line, FluxSink<String> sink, BooleanWrapperInf firstEventTag) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6).trim();

                            if ("[DONE]".equals(data)) {
                                return true;
                            }
                            if (!data.isEmpty()) {
                                String content = ResponseUtil.parseStreamContentFromData(data);
                                if (content != null && !content.isEmpty()) {
                                    if (firstEventTag.get()) {
                                        firstEventTag.set(false);
                                    }
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

                    /**
                     * 处理异常
                     * @param throwable 异常
                     * @param sink 数据行处理结果
                     * @param firstEventTag 是否是第一个事件标记，需要具体实现设置，如果为true，则表示当前数据行是第一个事件标记，否则不是第一个事件标记。
                     *                      在接口方法实现中，在发送消息时，需检测是否为true，如果为true，需设置标记为false，同时将ServerEvent的first标记设置为true
                     * @return
                     */
                    @Override
                    public boolean handleException(Throwable throwable, FluxSink<String> sink, BooleanWrapperInf firstEventTag) {
                        logger.error("错误: " + throwable.getMessage(),throwable);
                        if(firstEventTag.get()){
                            firstEventTag.set(false);
                        }
                        sink.next(SimpleStringUtil.exceptionToString(throwable));
                        sink.complete();
                        return true;
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

    
    public static void qwenvl() throws InterruptedException {
        String message  = "介绍图片内容并计算结果";
//		
//		[
//		{
//			"type": "image_url",
//				"image_url": {
//			"url": "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg"
//		},
//		},
//		{"type": "text", "text": "这道题怎么解答？"},
//            ]
        List content = new ArrayList<>();
        Map contentData = new LinkedHashMap();
        contentData.put("type", "image_url");
        contentData.put("image_url", new HashMap<String, String>(){{
            put("url", "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg");
        }});
        content.add(contentData);
//		content.add(new HashMap<String, Object>(){{
//			put("type", "image_url");
//			put("image_url", new HashMap<String, String>(){{
//				put("url", "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg");
//			}});
//		}});
        contentData = new LinkedHashMap();
        contentData.put("type", "text");
        contentData.put("text", message);;
        content.add(contentData);


        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", "qwen3-vl-plus");

        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", content);
        messages.add(userMessage);



        requestMap.put("messages", messages);
        requestMap.put("stream", true);

        // enable_thinking 参数开启思考过程，thinking_budget 参数设置最大推理过程 Token 数
        Map extra_body = new LinkedHashMap();
        extra_body.put("enable_thinking",true);
        extra_body.put("thinking_budget",81920);
        requestMap.put("extra_body",extra_body);

//		{
//				'enable_thinking': True,
//				"thinking_budget": 81920},
//		requestMap.put("max_tokens", 2048);
//		requestMap.put("temperature", 0.7);
        Flux<ServerEvent> flux = HttpRequestProxy.streamChatCompletionEvent("qwenvlplus","/compatible-mode/v1/chat/completions",requestMap);
        flux.doOnSubscribe(subscription -> logger.info("开始订阅流..."))
                .doOnNext(chunk -> {
                    if(!chunk.isDone())
                        System.out.print(chunk.getData());
                    if(chunk.isFirst()){
                        logger.info("ServerEvent is first event.");
                    }
                }) //打印流式调用返回的问题答案片段
                .doOnComplete(() -> logger.info("\n=== 流完成 ==="))
                .doOnError(error -> logger.error("错误: " + error.getMessage(),error))
                .subscribe();

        // 等待异步操作完成，否则流式异步方法执行后会因为主线程的退出而退出，看不到后续响应的报文
        Thread.sleep(100000000);

    }
    public static void qwenvlCompareStream() throws InterruptedException {
        String message  = "介绍两个图片内容并比对相似度,以json格式返回结果";
//		
//		[
//		{
//			"type": "image_url",
//				"image_url": {
//			"url": "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg"
//		},
//		},
//		{"type": "text", "text": "这道题怎么解答？"},
//            ]
        List content = new ArrayList<>();
        Map contentData = new LinkedHashMap();
        contentData.put("type", "image_url");
        contentData.put("image_url", new HashMap<String, String>(){{
            put("url", "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg");
        }});
        content.add(contentData);

        contentData = new LinkedHashMap();
        contentData.put("type", "image_url");
        contentData.put("image_url", new HashMap<String, String>(){{
            put("url", "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg");
        }});
        content.add(contentData);
//		content.add(new HashMap<String, Object>(){{
//			put("type", "image_url");
//			put("image_url", new HashMap<String, String>(){{
//				put("url", "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg");
//			}});
//		}});
        contentData = new LinkedHashMap();
        contentData.put("type", "text");
        contentData.put("text", message);;
        content.add(contentData);


        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", "qwen3-vl-plus");

        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", content);
        messages.add(userMessage);



        requestMap.put("messages", messages);
        requestMap.put("stream", true);

        // enable_thinking 参数开启思考过程，thinking_budget 参数设置最大推理过程 Token 数
        Map extra_body = new LinkedHashMap();
        extra_body.put("enable_thinking",true);
        extra_body.put("thinking_budget",81920);
        requestMap.put("extra_body",extra_body);

//		{
//				'enable_thinking': True,
//				"thinking_budget": 81920},
//		requestMap.put("max_tokens", 2048);
//		requestMap.put("temperature", 0.7);
        Flux<ServerEvent> flux = HttpRequestProxy.streamChatCompletionEvent("qwenvlplus","/compatible-mode/v1/chat/completions",requestMap);
        flux.doOnSubscribe(subscription -> logger.info("开始订阅流..."))
                .doOnNext(chunk -> {
                    if(!chunk.isDone())
                        System.out.print(chunk.getData());
                }) //打印流式调用返回的问题答案片段
                .doOnComplete(() -> logger.info("\n=== 流完成 ==="))
                .doOnError(error -> logger.error("错误: " + error.getMessage(),error))
                .subscribe();

        // 等待异步操作完成，否则流式异步方法执行后会因为主线程的退出而退出，看不到后续响应的报文
        Thread.sleep(100000000);

    }

    public static void qwenvlCompare() throws InterruptedException {
        String message  = "介绍两个图片内容并比对相似度,以json格式返回结果";
//		
//		[
//		{
//			"type": "image_url",
//				"image_url": {
//			"url": "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg"
//		},
//		},
//		{"type": "text", "text": "这道题怎么解答？"},
//            ]
        List content = new ArrayList<>();
        Map contentData = new LinkedHashMap();
        contentData.put("type", "image_url");
        contentData.put("image_url", new HashMap<String, String>(){{
            put("url", "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg");
        }});
        content.add(contentData);

        contentData = new LinkedHashMap();
        contentData.put("type", "image_url");
        contentData.put("image_url", new HashMap<String, String>(){{
            put("url", "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg");
        }});
        content.add(contentData);
//		content.add(new HashMap<String, Object>(){{
//			put("type", "image_url");
//			put("image_url", new HashMap<String, String>(){{
//				put("url", "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg");
//			}});
//		}});
        contentData = new LinkedHashMap();
        contentData.put("type", "text");
        contentData.put("text", message);;
        content.add(contentData);


        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", "qwen3-vl-plus");

        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", content);
        messages.add(userMessage);



        requestMap.put("messages", messages);
        requestMap.put("stream", false);

        // enable_thinking 参数开启思考过程，thinking_budget 参数设置最大推理过程 Token 数
        Map extra_body = new LinkedHashMap();
        extra_body.put("enable_thinking",true);
        extra_body.put("thinking_budget",81920);
        requestMap.put("extra_body",extra_body);

 
        Map flux = HttpRequestProxy.sendJsonBody("qwenvlplus",requestMap,"/compatible-mode/v1/chat/completions",Map.class);
        logger.info(SimpleStringUtil.object2json( flux));

        String data = HttpRequestProxy.sendJsonBody("qwenvlplus",requestMap,"/compatible-mode/v1/chat/completions",String.class);
        logger.info(SimpleStringUtil.object2json( data));
    }
    
}
