package me.flyray.bsin.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

public class AESUtils {

    /**
     * base64格式的默认秘钥
     * 也可以每次生成一个随机的秘钥,使用如下代码
     * byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
     * String secret = Base64.encode(key);
     */
    private static final String BASE64_SECRET = "aEsva0zDHECg47P8SuPzmw==";

    /**
     * aes用来加密解密的byte[]
     */
    private final static byte[] SECRET_BYTES = Base64.decode(BASE64_SECRET);

    /**
     * 根据这个秘钥得到一个aes对象
     */
    private final static AES aes = SecureUtil.aes(SECRET_BYTES);

    /**
     * 使用aes加密
     * @param content
     * @return
     */
    public static String AESEnCode(String content){
        //加密完以后是十六进制的
        return aes.encryptHex(content);
    }

    /**
     * 使用aes算法,进行解密
     * @param ciphertext
     * @return
     */
    public static String AESDeCode(String ciphertext){
        return  aes.decryptStr(ciphertext);
    }

    public static void main(String[] args) {
        String string = "hello world";
        String enCode = AESEnCode(string);
        String dnCode = AESDeCode(enCode);
    }
}

