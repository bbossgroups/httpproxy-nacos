package org.frameworkset.http.client;
/**
 * Copyright 2024 bboss
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
import org.frameworkset.spi.remote.http.proxy.InvokeContext;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/3/1
 */
public class MoonshotAI {
    private static Logger logger = LoggerFactory.getLogger(MoonshotAI.class);
    @Before
    public void test(){
        //启动连接池
//        HttpRequestUtil.startHttpPools("application-ai.properties");
        Map<String,Object> configs = new HashMap<String,Object>();
//如果指定hosts那么就会采用配置的地址作为初始化地址清单
        configs.put("http.hosts","https://api.moonshot.cn");
        configs.put("http.maxTotal",100);
        configs.put("http.defaultMaxPerRoute",100);     

        HttpRequestProxy.startHttpPools(configs);
    }
    @Test
    public void testChat(){


        /**
         * {
         *     "id": "cmpl-afcdbbecc9764d0d92a39d97ab89f6c2",
         *     "object": "chat.completion",
         *     "created": 3972399,
         *     "model": "moonshot-v1-8k",
         *     "choices": [
         *         {
         *             "index": 0,
         *             "message": {
         *                 "role": "assistant",
         *                 "content": " BBoss是一个开源的Java企业应用开发框架，它提供了丰富的功能，包括数据采集、数据处理、数据展示等。在数据采集方面，BBoss支持从多种数据源采集数据，包括数据库、Excel文件等。\n\n如果你想使用BBoss框架来采集Excel文件中的数据，你可能需要使用BBoss提供的Excel插件或者相关的API。这通常涉及到以下几个步骤：\n\n1. **添加依赖**：首先，你需要在你的项目中添加BBoss Excel插件的依赖。这通常通过在项目的`pom.xml`文件中添加相应的依赖项来实现。\n\n2. **读取Excel**：使用BBoss提供的API来读取Excel文件。这可能涉及到创建一个Excel文件的读取器，然后指定要读取的文件路径。\n\n3. **处理数据**：读取Excel文件后，你可以遍历文件中的数据，进行你需要的处理。BBoss可能提供了一些工具类来帮助你解析和处理Excel文件中的数据。\n\n4. **存储数据**：处理完数据后，你可以将数据存储到数据库或其他数据存储系统中。\n\n请注意，具体的API使用方法和步骤可能会随着BBoss版本的更新而有所变化。为了获取最准确的信息，建议查阅BBoss的官方文档或者查看相关的示例代码。如果你有具体的代码问题或者需要进一  步的帮助，可以提供更多的信息，我会尽力为你提供帮助。"
         *             },
         *             "finish_reason": "stop"
         *         }
         *     ],
         *     "usage": {
         *         "prompt_tokens": 88,
         *         "completion_tokens": 275,
         *         "total_tokens": 363
         *     }
         * }
         */

        Map<String,Object> headers = new LinkedHashMap<String,Object>();
        headers.put("Authorization","Bearer sk-NmYophVVNhAyq7Op25UNMKxQj3O1ht6i0hT8IZJnthNsnis2");
        InvokeContext invokeContext = new InvokeContext();
        invokeContext.setHeaders(headers);
        invokeContext.setResponseCharset(StandardCharsets.UTF_8);
        String res = HttpRequestProxy.sendJsonBody("{\n" +
                "     \"model\": \"moonshot-v1-8k\",\n" +
                "     \"messages\": [\n" +
                "        {\"role\": \"system\", \"content\": \"你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一些涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。\"},\n" +
                "        {\"role\": \"user\", \"content\": \"https://esdoc.bbossgroups.com/#/filelog-guide\"}\n" +
              
                "     ],\n" +
                "     \"temperature\": 0.3\n" +
                "     ,\n" +
                "     \"stream\": false\n" +
                "   }","v1/chat/completions",invokeContext);
        logger.info(res);

        res = HttpRequestProxy.sendJsonBody("{\n" +
                "     \"model\": \"moonshot-v1-8k\",\n" +
                "     \"messages\": [\n" +
                "        {\"role\": \"system\", \"content\": \"你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一些涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。\"},\n" +
                "        {\"role\": \"user\", \"content\": \"https://esdoc.bbossgroups.com/#/filelog-guide\"},\n" +
                "        {\"role\": \"user\", \"content\": \"bboss采集excel\"}\n" +
                "     ],\n" +
                "     \"temperature\": 0.3\n" +
                "     ,\n" +
                "     \"stream\": false\n" +
                "   }","v1/chat/completions",invokeContext);
        logger.info(res);


        res = HttpRequestProxy.sendJsonBody("{\n" +
                "     \"model\": \"moonshot-v1-8k\",\n" +
                "     \"messages\": [\n" +
                "        {\"role\": \"system\", \"content\": \"你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一些涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。\"},\n" +
                "        {\"role\": \"user\", \"content\": \"https://esdoc.bbossgroups.com/#/filelog-guide\"}\n" +
                "       , {\"role\": \"user\", \"content\": \"bboss采集excel\"}\n" +
                "       , {\"role\": \"user\", \"content\": \"bboss采集pdf\"}\n" +
       
                "     ],\n" +
                "     \"temperature\": 0.3\n" +
                "     ,\n" +
                "     \"stream\": false\n" +
                "   }","v1/chat/completions",invokeContext);
        logger.info(res);

   
        res = HttpRequestProxy.sendJsonBody("{\n" +
                "     \"model\": \"moonshot-v1-8k\",\n" +
                "     \"messages\": [\n" +
                "        {\"role\": \"system\", \"content\": \"你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一些涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。\"},\n" +
                "        {\"role\": \"user\", \"content\": \"https://esdoc.bbossgroups.com/#/filelog-guide\"}\n" +
                "       , {\"role\": \"user\", \"content\": \"bboss采集excel\"}\n" +
                "       , {\"role\": \"user\", \"content\": \"bboss采集pdf\"}\n" +
                "       , {\"role\": \"user\", \"content\": \"从FTP采集excel\"}\n" +
             
                "     ],\n" +
                "     \"temperature\": 0.3\n" +
                "     ,\n" +
                "     \"stream\": false\n" +
                "   }","v1/chat/completions",invokeContext);
        logger.info(res);

        res = HttpRequestProxy.sendJsonBody("{\n" +
                "     \"model\": \"moonshot-v1-8k\",\n" +
                "     \"messages\": [\n" +
                "        {\"role\": \"system\", \"content\": \"你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一些涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。\"},\n" +
                "        {\"role\": \"user\", \"content\": \"https://esdoc.bbossgroups.com/#/filelog-guide\"}\n" +
                "       , {\"role\": \"user\", \"content\": \"bboss采集excel\"}\n" +
                "       , {\"role\": \"user\", \"content\": \"bboss采集pdf\"}\n" +
                "       , {\"role\": \"user\", \"content\": \"从FTP采集excel\"}\n" +
                "       , {\"role\": \"user\", \"content\": \"从FTP采集pdf\"}\n" +
                "     ],\n" +
                "     \"temperature\": 0.3\n" +
                "     ,\n" +
                "     \"stream\": false\n" +
                "   }","v1/chat/completions",invokeContext);
        logger.info(res);
 

    }

    @Test
    public void testStream(){



        Map<String,Object> headers = new LinkedHashMap<String,Object>();
        headers.put("Authorization","Bearer sk-NmYophVVNhAyq7Op25UNMKxQj3O1ht6i0hT8IZJnthNsnis2");
        String res = HttpRequestProxy.sendJsonBody("{\n" +
                "     \"model\": \"moonshot-v1-8k\",\n" +
                "     \"messages\": [\n" +
                "        {\"role\": \"system\", \"content\": \"你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一些涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。\"},\n" +
                "        {\"role\": \"user\", \"content\": \"https://esdoc.bbossgroups.com/#/filelog-guide\"}\n" +
                "        ,{\"role\": \"user\", \"content\": \"bboss采集excel\"}\n" +
                "     ],\n" +
                "     \"temperature\": 0.3\n" +
                "     ,\n" +
                "     \"stream\": true\n" +
                "   }","v1/chat/completions",headers);
        logger.info(res);




    }

    @Test
    public void testStream1(){


        Map<String,Object> headers = new LinkedHashMap<String,Object>();
        headers.put("Authorization","Bearer sk-NmYophVVNhAyq7Op25UNMKxQj3O1ht6i0hT8IZJnthNsnis2");
//        sendBody( "default", requestBody,   url,   headers, ContentType.APPLICATION_JSON);
        String res = HttpRequestProxy.sendJsonBody( "{\n" +
                "     \"model\": \"moonshot-v1-8k\",\n" +
                "     \"messages\": [\n" +
                "        {\"role\": \"system\", \"content\": \"你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一些涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。\"},\n" +
                "        {\"role\": \"user\", \"content\": \"https://esdoc.bbossgroups.com/#/filelog-guide\"},\n" +
                "        {\"role\": \"user\", \"content\": \"bboss采集excel\"}\n" +
                "     ],\n" +
                "     \"temperature\": 0.3\n" +
                "     ,\n" +
                "     \"stream\": true\n" +
                "   }","v1/chat/completions",headers);
        logger.info(res);




    }

    @Test
    public void testStream2(){


        Map<String,Object> headers = new LinkedHashMap<String,Object>();
        headers.put("Authorization","Bearer sk-NmYophVVNhAyq7Op25UNMKxQj3O1ht6i0hT8IZJnthNsnis2");
//        sendBody( "default", requestBody,   url,   headers, ContentType.APPLICATION_JSON);
        Map res = HttpRequestProxy.sendJsonBody( "{\n" +
                "     \"model\": \"moonshot-v1-8k\",\n" +
                "     \"messages\": [\n" +
                "        {\"role\": \"system\", \"content\": \"你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一些涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。\"},\n" +
                "        {\"role\": \"user\", \"content\": \"https://esdoc.bbossgroups.com/#/filelog-guide\"},\n" +
                "        {\"role\": \"user\", \"content\": \"bboss采集excel\"}\n" +
                "     ],\n" +
                "     \"temperature\": 0.3\n" +
                "     ,\n" +
                "     \"stream\": true\n" +
                "   }","v1/chat/completions",headers,Map.class);
        logger.info(SimpleStringUtil.object2json(res));




    }

    @Test
    public void testListModel(){


        /**
         * {
         *     "data": [
         *         {
         *             "created": 1709149142,
         *             "id": "moonshot-v1-128k",
         *             "object": "model",
         *             "owned_by": "moonshot",
         *             "permission": [
         *                 {
         *                     "created": 0,
         *                     "id": "",
         *                     "object": "",
         *                     "allow_create_engine": false,
         *                     "allow_sampling": false,
         *                     "allow_logprobs": false,
         *                     "allow_search_indices": false,
         *                     "allow_view": false,
         *                     "allow_fine_tuning": false,
         *                     "organization": "public",
         *                     "group": "public",
         *                     "is_blocking": false
         *                 }
         *             ],
         *             "root": "",
         *             "parent": ""
         *         },
         *         {
         *             "created": 1709149142,
         *             "id": "moonshot-v1-8k",
         *             "object": "model",
         *             "owned_by": "moonshot",
         *             "permission": [
         *                 {
         *                     "created": 0,
         *                     "id": "",
         *                     "object": "",
         *                     "allow_create_engine": false,
         *                     "allow_sampling": false,
         *                     "allow_logprobs": false,
         *                     "allow_search_indices": false,
         *                     "allow_view": false,
         *                     "allow_fine_tuning": false,
         *                     "organization": "public",
         *                     "group": "public",
         *                     "is_blocking": false
         *                 }
         *             ],
         *             "root": "",
         *             "parent": ""
         *         },
         *         {
         *             "created": 1709149142,
         *             "id": "moonshot-v1-32k",
         *             "object": "model",
         *             "owned_by": "moonshot",
         *             "permission": [
         *                 {
         *                     "created": 0,
         *                     "id": "",
         *                     "object": "",
         *                     "allow_create_engine": false,
         *                     "allow_sampling": false,
         *                     "allow_logprobs": false,
         *                     "allow_search_indices": false,
         *                     "allow_view": false,
         *                     "allow_fine_tuning": false,
         *                     "organization": "public",
         *                     "group": "public",
         *                     "is_blocking": false
         *                 }
         *             ],
         *             "root": "",
         *             "parent": ""
         *         }
         *     ]
         * }
         */
        Map<String,Object> headers = new LinkedHashMap<String,Object>();
        headers.put("Authorization","Bearer sk-NmYophVVNhAyq7Op25UNMKxQj3O1ht6i0hT8IZJnthNsnis2");
        String res = HttpRequestProxy.httpGetforString("/v1/models",headers);
        logger.info(res);




    }
}
