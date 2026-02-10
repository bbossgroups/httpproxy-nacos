package org.frameworkset.feishu;
/**
 * Copyright 2026 bboss
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

import java.util.HashMap;
import java.util.Map;

/**
 * @author biaoping.yin
 * @Date 2026/2/6
 */
public class FeishuUtil {
    /**
     * 初始化一个http微服务客户端，名称为：feishu
     * startPool方法在单个进程中只要执行一次即可
     */
    public static void startPool(){
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
        configs.put("http.poolNames","feishu");
        //九天模型服务组
        //如果指定hosts那么就会采用配置的地址作为初始化地址清单
        configs.put("feishu.http.hosts","https://open.feishu.cn");
        configs.put("feishu.http.maxTotal",100);
        configs.put("feishu.http.defaultMaxPerRoute",100);

        HttpRequestProxy.startHttpPools(configs);
    }
}
