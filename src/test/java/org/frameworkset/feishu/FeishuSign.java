package org.frameworkset.feishu;


/**
 * @author biaoping.yin
 * @Date 2026/1/7
 */

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

public class FeishuSign {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {

        String secret = "2X3bTtX6JQXuDTQ9y7ZBug";
        long timestamp = System.currentTimeMillis();
        System.out.printf("sign: %s", genSign(secret, timestamp));

    }
    public static String genSign(String secret, long timestamp) throws SignException {
        //把timestamp+"\n"+密钥当做签名字符串
        String stringToSign = timestamp + "\n" + secret;

        try {
            //使用HmacSHA256算法计算签名
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(new byte[]{});
            return new String(Base64.encodeBase64(signData));
        }
        catch (Exception e) {
            throw new SignException(e);
        }
        
    }

}
