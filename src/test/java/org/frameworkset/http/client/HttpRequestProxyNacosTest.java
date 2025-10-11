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
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2018</p>
 * @Date 2019/6/19 15:25
 * @author biaoping.yin
 * @version 1.0
 */
public class HttpRequestProxyNacosTest {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestProxyNacosTest.class);
	@Before
	public void startPool(){
//		HttpRequestProxy.startHttpPools("application.properties");
		/**
		 * 1.服务健康检查
		 * 2.服务负载均衡
		 * 3.服务容灾故障恢复
		 * 4.服务自动发现（nacos,apollo，zk，etcd，consul，eureka，db，其他第三方注册中心）
		 * 配置了两个连接池：default,report
		 * 本示例演示基于apollo提供配置管理、服务自动发现以及灰度/生产，主备切换功能
		 */

//		HttpRequestProxy.startHttpPoolsFromApollo("application","org.frameworkset.http.client.AddressConfigChangeListener");
        /**
         * nacosNamespace="application" 
         *             serverAddr="localhost:8848" 
         *             dataId="redis" 
         *             group="DEFAULT_GROUP" 
         *             timeOut="5000" 
         *             changeReload="false"
         */
        Map<String,String> config = new HashMap<>();
        config.put("remote-first","false");

        config.put("auto-refresh","true");
        config.put("max-retry","10");
		HttpRequestProxy.startHttpPoolsFromNacosAwaredChange("test","localhost:8848","http-proxy","DEFAULT_GROUP" ,5000L,config);
	}
	@Test
	public void testGet(){
		String data = null;
		try {
			data = HttpRequestProxy.httpGetforString("schedule", "/testBBossIndexCrud");
		}
		catch (Exception e){
            logger.error(e.getMessage(),e);
		}
		logger.info(data);
		do {
			try {
				data = HttpRequestProxy.httpGetforString("schedule","/testBBossIndexCrud");
                logger.info(data);
			} catch (Exception e) {
                logger.error(e.getMessage(),e);
			}
			try {
				Thread.sleep(3000l);
			} catch (Exception e) {
				break;
			}

		}
		while(true);
	}

	@Test
	public void testGetMap(){
		Map data = HttpRequestProxy.httpGetforObject("schedule","/testBBossIndexCrud",Map.class);
		logger.info(SimpleStringUtil.object2json(data));
		do {
			try {
				data = HttpRequestProxy.httpGetforObject("schedule","/testBBossIndexCrud",Map.class);
                logger.info(SimpleStringUtil.object2json(data));
//				data = HttpRequestProxy.httpPostForObject("report","/testBBossIndexCrud",(Map)null,Map.class);
//				List<Map> datas = HttpRequestProxy.httpPostForList("report","/testBBossIndexCrud",(Map)null,Map.class);
//				Set<Map> dataSet = HttpRequestProxy.httpPostForSet("report","/testBBossIndexCrud",(Map)null,Map.class);
//				Map<String,Object> dataMap = HttpRequestProxy.httpPostForMap("report","/testBBossIndexCrud",(Map)null,String.class,Object.class);
			} catch (Exception e) {
                logger.error(e.getMessage(),e);
			}
			try {
				Thread.sleep(3000l);
			} catch (Exception e) {
				break;
			}

		}
		while(true);
	}

    @Test
    public void testGetdefault(){
        String data = null;
        try {
            data = HttpRequestProxy.httpGetforString("default", "/testBBossIndexCrud");
        }
        catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        logger.info(data);
        do {
            try {
                data = HttpRequestProxy.httpGetforString("default","/testBBossIndexCrud");
                logger.info(data);
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
            try {
                Thread.sleep(3000l);
            } catch (Exception e) {
                break;
            }

        }
        while(true);
    }

    @Test
    public void testGetMapDefault(){
        Map data = HttpRequestProxy.httpGetforObject("default","/testBBossIndexCrud",Map.class);
        logger.info(SimpleStringUtil.object2json(data));
        do {
            try {
                data = HttpRequestProxy.httpGetforObject("default","/testBBossIndexCrud",Map.class);
                logger.info(SimpleStringUtil.object2json(data));
//				data = HttpRequestProxy.httpPostForObject("report","/testBBossIndexCrud",(Map)null,Map.class);
//				List<Map> datas = HttpRequestProxy.httpPostForList("report","/testBBossIndexCrud",(Map)null,Map.class);
//				Set<Map> dataSet = HttpRequestProxy.httpPostForSet("report","/testBBossIndexCrud",(Map)null,Map.class);
//				Map<String,Object> dataMap = HttpRequestProxy.httpPostForMap("report","/testBBossIndexCrud",(Map)null,String.class,Object.class);
            } catch (Exception e) {
               logger.error(e.getMessage(),e);
            }
            try {
                Thread.sleep(3000l);
            } catch (Exception e) {
                break;
            }

        }
        while(true);
    }
}
