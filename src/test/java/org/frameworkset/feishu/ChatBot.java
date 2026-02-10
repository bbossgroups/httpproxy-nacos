package org.frameworkset.feishu;


import org.frameworkset.spi.remote.http.HttpRequestProxy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 飞书机器人，帮助文档：https://open.feishu.cn/document/client-docs/bot-v3/add-custom-bot?lang=zh-CN
 * @author biaoping.yin
 * @Date 2026/1/7
 */
public class ChatBot {

   
    /**
     * https://open.feishu.cn/open-apis/im/v1/chats/oc_721984891be22d62f8be11a4960a9f4d/members?member_id_type=open_id&page_size=10
     * 
     */
    public Map queryChatMembers(){
        Map headers = new HashMap();
        headers.put("Authorization","Bearer u-f44LVWpfd2zby44ehjYbFgg14nEA4kUhoWyaJN400wbb");
        Map members = HttpRequestProxy.httpGetforObject("feishu",
                "/open-apis/im/v1/chats/oc_721984891be22d62f8be11a4960a9f4d/members?member_id_type=open_id&page_size=10",headers,Map.class);
        return members;
    }
    
    public void sendMessage(){
        Map data = new LinkedHashMap();

        Map members = queryChatMembers();
        //webhook地址，实际运行时，需调整为对应机器人的地址
        String webhookUri = "/open-apis/bot/v2/hook/56317612-80a6-4a66-bd0f-3f15dd967914";
        //1. 结合签名密钥和时间戳，生成消息签名信息
        long timestamp = System.currentTimeMillis() / 1000; // 转换为10位时间戳,单位为：秒
        String sign = FeishuSign.genSign("2X3bTtX6JQXuDTQ9y7ZBug",//签名密钥，实际运行时，需调整为对应机器人的签名密钥
                timestamp);//生成签名
        String stmp = String.valueOf(timestamp);
        
        //2. 构建消息报文，发送文本消息
        data.put("timestamp",stmp);

        data.put("sign",sign);
        data.put("msg_type","text");
        data.put("content",new HashMap<String,String>(){
            {
                put("text","request example");
            }
        });
        //3. 向飞书群发消息
        String result = HttpRequestProxy.sendJsonBody("feishu",
                data,
                webhookUri //webhook地址，实际运行时，需调整为对应机器人的地址
                );
        System.out.println("1.文本消息发送:"+result);
 
        //4. 向特定用户发消息，可以发给多个用户
        /**
         * // @ 单个用户
         * <at user_id="ou_xxx">名字</at>xxxx
         * // @ 多个用户
         * <at user_id="ou_xxx">名字</at><at user_id="ou_xxx1">名字1</at>xxxx
         * // @ 所有人
         * <at user_id="all">所有人</at>xxxx
         */
        data.put("content",new HashMap<String,String>(){
            {
                put("text","<at user_id=\"bboss\">bboss</at>,你好！");
            }
        });
        result = HttpRequestProxy.sendJsonBody("feishu",data,webhookUri);
        System.out.println("2.向特定用户发消息:"+result);

        data.put("content",new HashMap<String,String>(){
            {
                put("text","<at user_id=\"all\">所有人</at>,你好！");
            }
        });
        result = HttpRequestProxy.sendJsonBody("feishu",data,webhookUri);
        System.out.println("3.向所有用户发消息:"+result);

        //5. 发送富文本消息：包含文本、超链接、图片内容
        // 其中user_id为飞书群成员用户id，实际运行需调整为自己的id
        String json = """
        {
            "timestamp": "%s",  
            "sign": "%s", 
            "msg_type": "post",
            "content": {
                "post": {
                    "zh_cn": {
                        "title": "项目更新通知",
                        "img": "项目更新通知",
                        "content": [
                            [{
                                "tag": "text",
                                "text": "bboss版本有更新: "
                            }, {
                                "tag": "a",
                                "text": "请查看",
                                "href": "https://esdoc.bbossgroups.com/#/changelog"
                            }, {
                                "tag": "at",
                                "user_id": "ou_6e9c6a6dcaf86f2d35ea97a9b8e365ec" 
                            },
                            {
                                "tag": "img",
                                "image_key": "img_v3_02to_f0986ab2-fb4f-45de-a99e-322cc97ddaeg"
                            }]
                        ]
                    }
                }
            }
        }
        
               
        """;
        //需要参考飞书官方文档获取open_id或者user_id
        //open_id ou_6e9c6a6dcaf86f2d35ea97a9b8e365ec
        //user_id 26d5d63a
        json = String.format(json, stmp, sign);

        result = HttpRequestProxy.sendJsonBody("feishu",json,webhookUri);
        System.out.println("4.向特定用户发富文本消息:"+result);
        
        //6. 单独发送图片消息
        // 参考文档上传图片并获取image_key:https://open.feishu.cn/document/client-docs/bot-v3/add-custom-bot?lang=zh-CN
        json = """
                {
                    "timestamp": "%s",  
                    "sign": "%s",             
                    "msg_type":"image",
                    "content":{
                        "image_key": "img_ecffc3b9-8f14-400f-a014-05eca1a4310g"
                    }
                }
                """;
        
        json = String.format(json, stmp, sign);
        result = HttpRequestProxy.sendJsonBody("feishu",json,webhookUri);
        System.out.println("5.发送图片消息:"+result);
        
        //7. 发送群名片
        // 参考文档获取share_chat_id:https://open.feishu.cn/document/client-docs/bot-v3/add-custom-bot?lang=zh-CN
        json = """
                {
                    "timestamp": "%s",  
                    "sign": "%s", 
                     "msg_type": "share_chat",
                     "content":{
                         "share_chat_id": "oc_721984891be22d62f8be11a4960a9f4d"
                     }
                 }
                """;
        json = String.format(json, stmp, sign);
        result = HttpRequestProxy.sendJsonBody("feishu",json,webhookUri);
        System.out.println("6.发送群名片:"+result);
        
        //8. 发送飞书卡片,需提前通过搭建工具搭建并发布一张卡片，参考文档：
        // 创建卡片参考文档：https://open.feishu.cn/document/feishu-cards/quick-start/send-message-cards-with-custom-bot
        // 发送卡片参考文档：https://open.feishu.cn/document/feishu-cards/send-feishu-card#718fe26b
        
        
        
        String jsonTemplate = """
                {
                    "timestamp": "${TIMESTAMP}",  
                    "sign": "${SIGN}", 
                    "msg_type": "interactive",
                    "card":{
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
                                      "content": "亲爱的同事：<at id=\\"${USER_ID}\\"></at>\\n\\n热烈祝贺您入职【亚信科技】10周年！十年风雨同舟，感恩一路相伴。您的专业与坚守是公司最宝贵的财富，愿未来继续携手同行，共筑辉煌！",
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
               }
                """;
        json = jsonTemplate.replace("${TIMESTAMP}", stmp).replace("${SIGN}", sign).replace("${USER_ID}","ou_6e9c6a6dcaf86f2d35ea97a9b8e365ec");
        result = HttpRequestProxy.sendJsonBody("feishu",json,webhookUri);
        System.out.println("7.发送飞书卡片:"+result);
    }
    public static void main(String[] args){
        FeishuUtil.startPool();
        ChatBot chatBot = new ChatBot();
       
        chatBot.sendMessage();
    }

}
