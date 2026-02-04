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

import com.frameworkset.util.FileUtil;
import com.frameworkset.util.SimpleStringUtil;
import org.frameworkset.spi.ai.AIAgent;
import org.frameworkset.spi.ai.model.*;
import org.frameworkset.spi.ai.util.AIAgentUtil;
import org.frameworkset.spi.ai.util.AIResponseUtil;
import org.frameworkset.spi.ai.util.MessageBuilder;
import org.frameworkset.spi.reactor.BaseStreamDataHandler;
import org.frameworkset.spi.remote.http.HttpRequestProxy;
import org.frameworkset.spi.remote.http.ResponseUtil;
import org.frameworkset.util.concurrent.BooleanWrapperInf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author biaoping.yin
 * @Date 2025/10/11
 */
public class StreamTest {
    private static Logger logger = LoggerFactory.getLogger(StreamTest.class);
    public static void main(String[] args) throws InterruptedException, IOException {
        //加载配置文件，启动负载均衡器,应用中只需要执行一次
        HttpRequestProxy.startHttpPools("application-stream.properties");
//        callDeepseekSimple();
//        callChatDeepseekSimple();
//        testCustom();
//        callguijiSimple();
//        qwenvl();
//        videovl();
        videovlEvent();
//        qwenvlCompareStream();
//        qwenvlCompare();
//        callChatDeepseekSimple();
//        qwenvJiutian();
//        chatByJiutian();
//        audioFileRecognition();
    }
    public static void callDeepseekSimple() throws InterruptedException {
        //定义问题变量
        String message = "介绍一下bboss jobflow";
        //设置模型调用参数，
        ChatAgentMessage chatAgentMessage = new ChatAgentMessage();
        chatAgentMessage.setModel("deepseek-chat");
        chatAgentMessage.setPrompt(message);

        chatAgentMessage.setStream( true).setTemperature(0.7).addParameter("max_tokens", 2048);
        
        //通过bboss httpproxy响应式异步交互接口，请求Deepseek模型服务，提交问题
        AIAgentUtil.streamChatCompletion("deepseek",chatAgentMessage)
                .doOnSubscribe(subscription -> logger.info("开始订阅流..."))
                .doOnNext(chunk -> System.out.print(chunk)) //打印流式调用返回的问题答案片段
                .doOnComplete(() -> logger.info("\n=== 流完成 ==="))
                .doOnError(error -> logger.error("错误: " + error.getMessage(),error))
                .subscribe();

        // 等待异步操作完成，否则流式异步方法执行后会因为主线程的退出而退出，看不到后续响应的报文
        Thread.sleep(100000000);
    }


    public static void callChatDeepseekSimple() throws InterruptedException {
        //定义问题变量
        String message = "介绍一下bboss jobflow";
        //设置模型调用参数，
        ChatAgentMessage chatAgentMessage = new ChatAgentMessage();
        chatAgentMessage.setModel("deepseek-chat");
        chatAgentMessage.setPrompt(message);

        chatAgentMessage.setStream( false).setTemperature(0.7).addParameter("max_tokens", 2048);
        //通过bboss httpproxy响应式异步交互接口，请求Deepseek模型服务，提交问题
        ServerEvent serverEvent = AIAgentUtil.chatCompletionEvent("deepseek",chatAgentMessage);
        logger.info(serverEvent.getData());
        // 等待异步操作完成，否则流式异步方法执行后会因为主线程的退出而退出，看不到后续响应的报文
        Thread.sleep(100000000);
    }

    public static void callguijiSimple() throws InterruptedException {
        //定义问题变量
        String message = "介绍一下bboss jobflow";
        //设置模型调用参数，
        ChatAgentMessage chatAgentMessage = new ChatAgentMessage();
        chatAgentMessage.setModel("Qwen/Qwen3-Next-80B-A3B-Instruct");
        chatAgentMessage.setPrompt(message);

        chatAgentMessage.setStream( true).setTemperature(0.7).addParameter("max_tokens", 2048);
      
        //通过bboss httpproxy响应式异步交互接口，请求Deepseek模型服务，提交问题
        AIAgentUtil.streamChatCompletion("guiji",chatAgentMessage)
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
        ChatAgentMessage chatAgentMessage = new ChatAgentMessage();
        chatAgentMessage.setModel("deepseek-chat");
        chatAgentMessage.setPrompt(message);

        chatAgentMessage.setStream( true).setTemperature(0.7).addParameter("max_tokens", 2048);
        
        
        //通过bboss httpproxy响应式异步交互接口，请求Deepseek模型服务，提交问题，可以自定义每次返回的片段解析方法
        //处理数据行,如果数据已经返回完毕，则返回true，指示关闭对话，否则返回false
        AIAgentUtil.streamChatCompletion("deepseek",chatAgentMessage,new BaseStreamDataHandler<String>() {
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
                                StreamData content = AIResponseUtil.parseStreamContentFromData(data);
                                if (content != null && !content.isEmpty()) {
                                    if (firstEventTag.get()) {
                                        firstEventTag.set(false);
                                    }
                                    sink.next(content.getData());
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
                    public boolean handleException(Object requestBody, Throwable throwable, FluxSink<String> sink, BooleanWrapperInf firstEventTag) {
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


        ImageVLAgentMessage imageVLAgentMessage = new ImageVLAgentMessage();
        imageVLAgentMessage.setModel( "qwen3-vl-plus");
        imageVLAgentMessage.setPrompt( message);
        imageVLAgentMessage.addImageUrl("https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg");
        imageVLAgentMessage.setStream(true);



        // enable_thinking 参数开启思考过程，thinking_budget 参数设置最大推理过程 Token 数
        imageVLAgentMessage.addParameter("enable_thinking", true);
        imageVLAgentMessage.addParameter("thinking_budget", 81920);
         
        Flux<ServerEvent> flux = AIAgentUtil.streamChatCompletionEvent("qwenvlplus",imageVLAgentMessage);
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

    /**
     * 同步视频解析，多轮对话
     * @throws InterruptedException
     * @throws IOException
     */
    public static void videovlEvent() throws InterruptedException, IOException {
        String message  = "识别视频内容,并判断视频是否包含动物叫声";
        List<Map<String,Object>> sessions = new ArrayList<>();

        
        VideoVLAgentMessage videoVLAgentMessage = new VideoVLAgentMessage();
        videoVLAgentMessage.setModel( "kimi-k2.5");
        
        videoVLAgentMessage.setPrompt( message);
        String base64 = FileUtil.getBase64Video("C:\\data\\ai\\aigenfiles\\video\\a7afc105e4df4742814f472bcd517e03.mp4");
        videoVLAgentMessage.addVideoUrl(base64);
        videoVLAgentMessage.setStream(false);
        videoVLAgentMessage.setSessionMemory(sessions).setSessionSize(50);//多轮会话


        // 禁止思考链
        videoVLAgentMessage.addMapParameter("thinking","type","disabled");

        AIAgent aiAgent = new AIAgent();
        //识别视频
        ServerEvent serverEvent = aiAgent.videoParser("kimi",videoVLAgentMessage);
        logger.info(serverEvent.getData());

        //继续追问
        videoVLAgentMessage.setPrompt("猫的颜色是什么");
        serverEvent = aiAgent.videoParser("kimi",videoVLAgentMessage);
        logger.info(serverEvent.getData());

    }

    /**
     * 流式视频内容解析
     * @throws InterruptedException
     * @throws IOException
     */
    public static void videovl() throws InterruptedException, IOException {
        String message  = "识别视频内容";
        VideoVLAgentMessage videoVLAgentMessage = new VideoVLAgentMessage  ();
        videoVLAgentMessage.setModel( "kimi-k2.5");
        videoVLAgentMessage.setPrompt( message);
        String base64 = FileUtil.getBase64Video("C:\\data\\ai\\aigenfiles\\video\\e3245f84e86945a18a5757fe01113585.mp4");
        videoVLAgentMessage.addVideoUrl(base64);
        videoVLAgentMessage.setStream(true);        

        // 禁止思考链
        videoVLAgentMessage.addMapParameter("thinking","type","disabled");

        AIAgent aiAgent = new AIAgent();
        Flux<ServerEvent> flux = aiAgent.streamVideoParser("kimi",videoVLAgentMessage);
        flux.doOnSubscribe(subscription -> logger.info("开始订阅流..."))
                .doOnNext(chunk -> {
                    if(!chunk.isDone() && chunk.getData() != null) {
                        System.out.print(chunk.getData());
                    }
                    if(chunk.isDone()){
                        System.out.println();
                    }
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


    public static void qwenvJiutian() throws InterruptedException, IOException {
        String message  = "识别图片内容";
        AIAgent aiAgent = new AIAgent();
        ImageVLAgentMessage imageVLAgentMessage = new ImageVLAgentMessage();
        imageVLAgentMessage.setPrompt(message);

         
        //九天模型参考文档：https://jiutian.10086.cn/portal/common-helpcenter#/document/1160?platformCode=DMX_TYZX
        String model = "LLMImage2Text";
        imageVLAgentMessage.setModel( model);
    // 构建消息历史列表，包含之前的会话记忆

        String imageUrl = FileUtil.getFileContent("C:\\workspace\\bbossgroups\\bboss-demos\\etl-elasticsearch\\httpproxy-nacos\\src\\main\\resources\\image.txt");
 


   

        if(SimpleStringUtil.isNotEmpty(imageUrl)) {
            imageVLAgentMessage.addImageUrl(imageUrl);
        }
         

        ServerEvent serverEvent = aiAgent.imageParser("jiutian",imageVLAgentMessage);
         logger.info(serverEvent.getData());

    }

    public static void chatByJiutian() throws InterruptedException, IOException {
        String message  = "介绍bboss";
        AIAgent aiAgent = new AIAgent();
        ChatAgentMessage chatAgentMessage = new ChatAgentMessage();
        chatAgentMessage.setPrompt(message);


        //九天模型参考文档：https://jiutian.10086.cn/portal/common-helpcenter#/document/1160?platformCode=DMX_TYZX
        String model = "jiutian-lan-comv3";
        chatAgentMessage.setModel( model);
        chatAgentMessage.setTemperature(0.7d);
        // 构建消息历史列表，包含之前的会话记忆


        ServerEvent serverEvent = aiAgent.chatCompletionEvent("jiutian",chatAgentMessage);
        logger.info(serverEvent.getData());

    }
    public static void qwenvlCompareStream() throws InterruptedException {
        String message  = "介绍两个图片内容并比对相似度,以json格式返回结果";
        String[] images = new String[2];
        images[0] = "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg";
        images[1] = "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg";



        ImageVLAgentMessage imageVLAgentMessage = new ImageVLAgentMessage();
        imageVLAgentMessage.setModel( "qwen3-vl-plus");
        imageVLAgentMessage.setPrompt( message);
        imageVLAgentMessage.addImageUrl(images[0]);
        imageVLAgentMessage.addImageUrl(images[1]);
        imageVLAgentMessage.setStream(true);



        // enable_thinking 参数开启思考过程，thinking_budget 参数设置最大推理过程 Token 数
        imageVLAgentMessage.addParameter("enable_thinking", true);
        imageVLAgentMessage.addParameter("thinking_budget", 81920);
        Flux<ServerEvent> flux = AIAgentUtil.streamChatCompletionEvent("qwenvlplus",imageVLAgentMessage);
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
        
        String[] images = new String[2];
        images[0] = "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg";
        images[1] = "https://img.alicdn.com/imgextra/i1/O1CN01gDEY8M1W114Hi3XcN_!!6000000002727-0-tps-1024-406.jpg";
        


        ImageVLAgentMessage imageVLAgentMessage = new ImageVLAgentMessage();
        imageVLAgentMessage.setModel( "qwen3-vl-plus");
        imageVLAgentMessage.setPrompt( message);
        imageVLAgentMessage.addImageUrl(images[0]);
        imageVLAgentMessage.addImageUrl(images[1]);
        imageVLAgentMessage.setStream(false);

 

        // enable_thinking 参数开启思考过程，thinking_budget 参数设置最大推理过程 Token 数
        imageVLAgentMessage.addParameter("enable_thinking", true);
        imageVLAgentMessage.addParameter("thinking_budget", 81920);

        
        ServerEvent serverEvent = AIAgentUtil.chatCompletionEvent("qwenvlplus",imageVLAgentMessage);
        logger.info(SimpleStringUtil.object2json( serverEvent));
        
         
    }

    /**
     * 音频识别功能
     * https://bailian.console.aliyun.com/?spm=5176.29597918.J_SEsSjsNv72yRuRFS2VknO.2.74ba7b08ig5jxD&tab=doc#/doc/?type=model&url=2979031
 
     * @return
     */
    public static void audioFileRecognition() throws IOException {
//        String selectedModel = "zhipu";        
        String selectedModel = "qwenvlplus"; 
        Boolean enableStream = false;
        
        String message = "介绍音频内容";
        List sessionMemory = new ArrayList<>();

        AudioSTTAgentMessage audioSTTAgentMessage = new AudioSTTAgentMessage();
        audioSTTAgentMessage.setStream(enableStream);
        String model = null;
        File audio = new File("C:\\data\\ai\\aigenfiles\\audio\\de40c4238b3b48bfb4e5224122eff4c4.wav");
        audioSTTAgentMessage.setAudio(audio);
        audioSTTAgentMessage.setContentType("audio/wav");
        if(selectedModel.equals("qwenvlplus")){
            model = "qwen3-asr-flash";
            //设置音频文件内容

            //直接设置音频url地址
//       audioSTTAgentMessage.setAudio("https://dashscope.oss-cn-beijing.aliyuncs.com/audios/welcome.mp3");


            audioSTTAgentMessage.addMapParameter("asr_options","enable_itn",true);
            audioSTTAgentMessage.addParameter("incremental_output", true);
            audioSTTAgentMessage.setResultFormat( "message");

        }
        else if(selectedModel.equals("zhipu")){
            model = "glm-asr-2512";
        }
        audioSTTAgentMessage.setModel(model);
        // 构建消息历史列表，包含之前的会话记忆,语音识别模型本身无法实现多轮会话，如果要多轮会话，需切换支持多轮会话的模型，例如LLM和千问图片识别模型
        audioSTTAgentMessage.setSessionMemory(sessionMemory);
        audioSTTAgentMessage.setSessionSize(50);
        // 添加当前用户消息
        audioSTTAgentMessage.setPrompt( message);

       
        AIAgent aiAgent = new AIAgent();
        ServerEvent serverEvent = aiAgent.audioParser(selectedModel,
                        audioSTTAgentMessage);

        logger.info(serverEvent.getData());
    }


}
