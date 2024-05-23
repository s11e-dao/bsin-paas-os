package me.flyray.bsin.infrastructure.biz;

import com.cobo.custody.api.client.impl.LocalSigner;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.mybatis.utils.StringUtils;
import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.springframework.stereotype.Service;
import org.bitcoinj.core.ECKey;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.cobo.custody.api.client.impl.Utils.bytesToHex;
import static com.cobo.custody.api.client.impl.Utils.hexToBytes;

@Service
public class ApiSignerBiz {

    private ECKey newEckey;
    private ECPrivateKey oldEckey;

//    /**
//     * 初始化secretKey
//     */
//    private String getSecretKey() {
//        if(secretKey != null) {
////            secretKey = AESUtils.AESDeCode(secretKey);
//        }else {
//            throw new BusinessException("secretKey is null");
//        }
//        return secretKey;
//    }

    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException {
        String[] key = LocalSigner.generateKeyPair();

        // 加密
        String content="1111111111";
        Signature dsa = Signature.getInstance("SHA256withECDSA");
        ECPrivateKey oldEckey = generatePrivateKey(hexToBytes(key[0]));
        dsa.initSign(oldEckey);
        dsa.update(sha256(content.getBytes()));
        String signature = bytesToHex(dsa.sign());
        System.out.println(signature);

        // 验签
        Boolean verifyResult = LocalSigner.verifyEcdsaSignature(content, signature, key[1]);
        System.out.println(verifyResult);
    }

    /**
     * 生成秘钥对
     * @return
     */
    public static Map<String,Object> generateKeyPair(){
        Map<String,Object> keyPairMap = new TreeMap<>();
        String[] key = LocalSigner.generateKeyPair();
        keyPairMap.put("secretKey", key[0]);
        keyPairMap.put("publicKey", key[1]);
        return keyPairMap;
    }

    /**
     * 签名
     * @return
     */
    public Map<String,Object> appendParam(String content,String secretKey){
        String sig = sign(content.getBytes(),secretKey);
        Map<String,Object> headerMap = new HashMap<>();
        headerMap.put("BIZ-API-KEY",secretKey);
        headerMap.put("BIZ-API-SIGNATURE",sig);
        return headerMap;
    }

    /**
     * 签名
     * @return
     */
    public Map<String,Object> appendParam(Map paramMap, String method, String path,String secretKey){
        TreeMap<String, Object> treeMap = new TreeMap<>();
        treeMap.putAll(paramMap);
        String body = getUrlParamsByMap(treeMap);
        String nonce = String.valueOf(System.currentTimeMillis());
        String content = method + "|" + path + "|" + nonce + "|" + body;
        String sig = sign(content.getBytes(), secretKey);
        Map<String,Object> headerMap = new HashMap<>();
        headerMap.put("BIZ-API-KEY",secretKey);
        headerMap.put("BIZ-API-SIGNATURE",sig);
        headerMap.put("BIZ-API-NONCE",nonce);
        return headerMap;
    }

    public String sign(byte[] message,String secretKey) {
        try {
            newEckey = ECKey.fromPrivate(hexToBytes(secretKey));
            return bytesToHex(newEckey.sign(Sha256Hash.wrap(Sha256Hash.hashTwice(message))).encodeToDER());
        }catch (Exception e) {
            e.printStackTrace();
        }

       try {
           Signature dsa = Signature.getInstance("SHA256withECDSA");
           oldEckey = generatePrivateKey(hexToBytes(secretKey));
           dsa.initSign(oldEckey);
           dsa.update(sha256(message));
           return bytesToHex(dsa.sign());
       }catch (Exception e) {
           e.printStackTrace();
       }
        return null;
    }


    public static byte[] hexToBytes(String hex) {
        hex = hex.length() % 2 != 0 ? "0" + hex : hex;

        byte[] b = new byte[hex.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(hex.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public static byte[] sha256(byte[] message) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return digest.digest(message);
    }

    public static ECPrivateKey generatePrivateKey(byte[] keyBin) throws InvalidKeySpecException, NoSuchAlgorithmException {
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
        KeyFactory kf = KeyFactory.getInstance("EC", new BouncyCastleProvider());
        ECNamedCurveSpec params = new ECNamedCurveSpec("secp256k1", spec.getCurve(), spec.getG(), spec.getN());
        ECPrivateKeySpec privKeySpec = new ECPrivateKeySpec(new BigInteger(keyBin), params);
        return (ECPrivateKey) kf.generatePrivate(privKeySpec);
    }

    public static String getUrlParamsByMap(TreeMap<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }
}
