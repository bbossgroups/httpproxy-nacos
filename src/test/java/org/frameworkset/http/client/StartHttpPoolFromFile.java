package org.frameworkset.http.client;
/**
 * Copyright 2008 biaoping.yin
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
import org.frameworkset.spi.remote.http.HttpRequestUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2018</p>
 * @Date 2019/6/17 22:03
 * @author biaoping.yin
 * @version 1.0
 */
public class StartHttpPoolFromFile {
    private static Logger logger = LoggerFactory.getLogger(StartHttpPoolFromFile.class);
	@Before
	public void test(){
		//启动连接池
		HttpRequestUtil.startHttpPools("application-demo.properties");
	}
    @Test
    public void testSendJsonBody(){



        ExampleBean exampleBean = new ExampleBean();
        exampleBean.setName("张三");
        exampleBean.setAge(20);
        exampleBean.setBirthDay(new Date());
        CustomContainer<ExampleBean> customContainer = HttpRequestProxy.sendJsonBodyTypeObject("/demoproject/examples/sayHelloBodyHttp.page",
                                                        exampleBean,CustomContainer.class,ExampleBean.class);
        logger.info(SimpleStringUtil.object2json(customContainer));

        List<ExampleBean> datas = new ArrayList<ExampleBean>();
        datas.add(exampleBean);

        exampleBean = new ExampleBean();
        exampleBean.setName("王五");
        exampleBean.setAge(32);
        exampleBean.setBirthDay(new Date());
        datas.add(exampleBean);
        customContainer = HttpRequestProxy.sendJsonBodyTypeObject("/demoproject/examples/sayHelloBodyHttps.page",
                datas,CustomContainer.class,ExampleBean.class);
        logger.info(SimpleStringUtil.object2json(customContainer));



    }
    @Test
    public void testGet(){
        Map params = new LinkedHashMap();
        params.put("name",java.net.URLEncoder.encode("大河"));//get请求中文字符要进行编码处理
        params.put("age",20);
        params.put("birthDay",new Date());

        String data = HttpRequestProxy.httpGetforStringWithParams("/demoproject/examples/sayHelloBeanHttp.page",params);
        logger.info(data);


        ExampleBean exampleBean = HttpRequestProxy.httpGetforObjectWithParams("/demoproject/examples/sayHelloBeanHttp.page",params,ExampleBean.class);
        logger.info(SimpleStringUtil.object2json(exampleBean));
        params.put("name","大河");//post请求中文字符无需编码处理
        data = HttpRequestProxy.httpPostforStringWithParams("/demoproject/examples/sayHelloBeanHttp.page",params);
        logger.info(data);
        exampleBean = HttpRequestProxy.httpPostForObject("/demoproject/examples/sayHelloBeanHttp.page",params,ExampleBean.class);
        logger.info(SimpleStringUtil.object2json(exampleBean));


        data = HttpRequestProxy.httpGetforStringWithParams("/demoproject/examples/sayHelloBeanHttp.page",params);
        logger.info(data);


        exampleBean.setName(java.net.URLEncoder.encode("大河"));
        exampleBean = HttpRequestProxy.httpGetforObjectWithParams("/demoproject/examples/sayHelloBeanHttp.page",exampleBean,ExampleBean.class);
        logger.info(SimpleStringUtil.object2json(exampleBean));
//        params.put("name","大河");//post请求中文字符无需编码处理
        data = HttpRequestProxy.httpPostforStringWithParams("/demoproject/examples/sayHelloBeanHttp.page",exampleBean);
        logger.info(data);
        exampleBean = HttpRequestProxy.httpPostForObject("/demoproject/examples/sayHelloBeanHttp.page",exampleBean,ExampleBean.class);
        logger.info(SimpleStringUtil.object2json(exampleBean));






    }
}
