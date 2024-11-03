package org.frameworkset.llm;
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

import org.frameworkset.spi.remote.http.HttpRequestProxy;
import org.junit.Before;
import org.junit.Test;

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
public class HttpLLMTest {
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
        Map<String,Object> configs = new HashMap<String,Object>();
        configs.put("http.poolNames","jiutian,jiutiantoken");
        //九天模型服务组
        //如果指定hosts那么就会采用配置的地址作为初始化地址清单
        configs.put("jiutian.http.hosts","jiutian.hn.10086.cn");
        configs.put("jiutian.http.maxTotal",100);
        configs.put("jiutian.http.defaultMaxPerRoute",100);
        //九天模型认证服务组
        //如果指定hosts那么就会采用配置的地址作为初始化地址清单
        configs.put("jiutiantoken.http.hosts","jiutian.hn.10086.cn");
        configs.put("jiutiantoken.http.maxTotal",100);
        configs.put("jiutiantoken.http.defaultMaxPerRoute",100);




        HttpRequestProxy.startHttpPools(configs);
	}
    private String getAccessToken() {
        /**
         * def get_kc_act():
         *     url = 'http://jiutian.hn.10086.cn/auth/realms/TechnicalMiddlePlatform/protocol/openid-connect/token'  # kc获取access_token的url
         *     # user = 'kechuang'  # kunlun 用户名
         *     # password = 'HN@kechuang*#'  # kunlun 密码
         *     user = 'oedingchan'  # kunlun 用户名
         *     password = 'HN#ding*!'  # kunlun 密码
         *     client_id = 'kunlun-front'
         *     headers = {
         *         'Content-Type': 'application/x-www-form-urlencoded'
         *     }
         *     payload = 'grant_type=' + 'password&username=' + user + '&password=' + password + '&scope=openid&client_id=' + client_id
         *     response = requests.request("POST", url, headers=headers, data=payload)
         *     access_keys = {}
         *     access_keys['access_token'] = response.json()["access_token"]
         *     access_keys['refresh_token'] = response.json()["refresh_token"]
         *     # 生成的access_keys需记录。尤其是refresh_token。
         *     print(access_keys)
         *     return access_keys
         */
        String user = "oedingchan" ; //# kunlun 用户名
        String password = "HN#ding*!";  //# kunlun 密码
        String client_id = "kunlun-front";
        Map<String, String> headerMap = new HashMap();
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        String bodyParam = "grant_type=password&username=" + user + "&password=" + password + "&scope=openid&client_id=" + client_id;
//        JSONObject resultJson = HttpHuToolUtil.postRequest(this.clientApiProperties.getTokenServiceUrl(), headerMap, bodyParam);
//        return resultJson.getString("access_token");
        return "";
    }
	@Test
	public void testGet(){
		String data = null;
		try {
			data = HttpRequestProxy.httpGetforString("schedule", "/testBBossIndexCrud");
		}
		catch (Exception e){
			e.printStackTrace();
		}
		System.out.println(data);
		do {
			try {
				data = HttpRequestProxy.httpGetforString("schedule","/testBBossIndexCrud");
                System.out.println(data);
			} catch (Exception e) {
				e.printStackTrace();
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
		System.out.println(data);
		do {
			try {
				data = HttpRequestProxy.httpGetforObject("schedule","/testBBossIndexCrud",Map.class);
//				data = HttpRequestProxy.httpPostForObject("report","/testBBossIndexCrud",(Map)null,Map.class);
//				List<Map> datas = HttpRequestProxy.httpPostForList("report","/testBBossIndexCrud",(Map)null,Map.class);
//				Set<Map> dataSet = HttpRequestProxy.httpPostForSet("report","/testBBossIndexCrud",(Map)null,Map.class);
//				Map<String,Object> dataMap = HttpRequestProxy.httpPostForMap("report","/testBBossIndexCrud",(Map)null,String.class,Object.class);
			} catch (Exception e) {
				e.printStackTrace();
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
            e.printStackTrace();
        }
        System.out.println(data);
        do {
            try {
                data = HttpRequestProxy.httpGetforString("default","/testBBossIndexCrud");
                System.out.println(data);
            } catch (Exception e) {
                e.printStackTrace();
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
        System.out.println(data);
        do {
            try {
                data = HttpRequestProxy.httpGetforObject("default","/testBBossIndexCrud",Map.class);
                System.out.println(data);
//				data = HttpRequestProxy.httpPostForObject("report","/testBBossIndexCrud",(Map)null,Map.class);
//				List<Map> datas = HttpRequestProxy.httpPostForList("report","/testBBossIndexCrud",(Map)null,Map.class);
//				Set<Map> dataSet = HttpRequestProxy.httpPostForSet("report","/testBBossIndexCrud",(Map)null,Map.class);
//				Map<String,Object> dataMap = HttpRequestProxy.httpPostForMap("report","/testBBossIndexCrud",(Map)null,String.class,Object.class);
            } catch (Exception e) {
                e.printStackTrace();
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
