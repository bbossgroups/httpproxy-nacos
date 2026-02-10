package org.frameworkset.feishu;


import org.frameworkset.spi.remote.http.HttpRequestProxy;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * https://asiainfo.feishu.cn/wiki/Sxh5wtX9eirZOEkz2HEczErWnLf
 * @author biaoping.yin
 * @Date 2026/2/6
 */
public class CMBEmploeeHelloWorld {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(CMBEmploeeHelloWorld.class);
    public String getTenantAccessToken(String appId, String appSecret){
        String url = "/open-apis/auth/v3/tenant_access_token/internal";
       Map<String,Object> params = new LinkedHashMap<>();
       params.put("app_id",appId);
       params.put("app_secret",appSecret);
//        String data = """
//                {
//                    "app_id": "${appId}",
//                    "app_secret": "${appSecret}"
//                }
//            """;
      
        Map tenantAccessToken = HttpRequestProxy.sendJsonBody("feishu",params,url,Map.class);
        return (String)tenantAccessToken.get("tenant_access_token");
    }
    
    public void sendCardMessage(String tenantAccessToken) {

        String user_id = "yinbp@asiainfo.com";
        // # user_id或 "open_id"/"email"
//        String params = """
//                {
//                    "receive_id_type": "email"
//                }
//                """;
      
        Map headers = new LinkedHashMap();
        headers.put("Authorization","Bearer "+tenantAccessToken);
       
        String card_content = """
        {
         "elements": [{
             "tag": "div",
             "text": {
                 "content": "员工关怀通知\\n今天是您的入职纪念日，感谢您为公司做出的贡献！",
                 "tag": "lark_md"
             }
         }],
         "header": {
             "title": {
                 "content": "亚信科技 - 员工关怀",
                 "tag": "plain_text"
             }
         }
     }
    """;
        
        
         // # 卡片消息内容
        Map cardMessage = new LinkedHashMap();
        cardMessage.put("receive_id",user_id);        
        cardMessage.put("msg_type","interactive");
        cardMessage.put("content",card_content);
        String url = "/open-apis/im/v1/messages?receive_id_type=email";
        Map message = HttpRequestProxy.sendJsonBody("feishu",cardMessage,url,headers,Map.class);
        logger.info(message.toString());

        card_content = """
                {
                      "schema": "2.0",
                      "config": {
                          "update_multi": true,
                          "style": {
                              "text_size": {
                                  "normal_v2": {
                                      "default": "normal",
                                      "pc": "normal",
                                      "mobile": "heading"
                                  }
                              }
                          }
                      },
                      "body": {
                          "direction": "vertical",
                          "elements": [
                              {
                                  "tag": "img",
                                  "img_key": "img_v3_02to_edfe91b8-7cfa-4eda-af4e-0b96819bb97g",
                                  "scale_type": "fit_horizontal",
                                  "corner_radius": "12px",
                                  "margin": "0px 0px 0px 0px"
                              },
                              {
                                  "tag": "markdown",
                                  "content": "亲爱的同事：\\n\\n热烈祝贺您入职【亚信科技】10周年！十年风雨同舟，感恩一路相伴。您的专业与坚守是公司最宝贵的财富，愿未来继续携手同行，共筑辉煌！",
                                  "text_align": "center",
                                  "text_size": "normal_v2",
                                  "margin": "0px 0px 0px 0px"
                              }
                          ]
                      },
                      "header": {
                          "title": {
                              "tag": "plain_text",
                              "content": "司龄10周年祝贺"
                          },
                          "subtitle": {
                              "tag": "plain_text",
                              "content": ""
                          },
                          "template": "blue",
                          "padding": "12px 8px 12px 8px"
                      }
                }
    """;

        card_content = card_content.replace("${USER_ID}",user_id);
        cardMessage.put("content",card_content);
        message = HttpRequestProxy.sendJsonBody("feishu",cardMessage,url,headers,Map.class);
        logger.info(message.toString());
    }
    
    public static void main(String[] args) {
        FeishuUtil.startPool();
        String appId = "cli_a90feb5dbcb89bc2";
        String appSecret = "RNhMgNhysTgV5tmK21J6Q5LPtGeKZIsB";
        CMBEmploeeHelloWorld cmbEmploeeHelloWorld = new CMBEmploeeHelloWorld();
        String tenantAccessToken = cmbEmploeeHelloWorld.getTenantAccessToken(appId,appSecret);
        logger.info(tenantAccessToken);

        cmbEmploeeHelloWorld.sendCardMessage(tenantAccessToken);
       
    }

}
