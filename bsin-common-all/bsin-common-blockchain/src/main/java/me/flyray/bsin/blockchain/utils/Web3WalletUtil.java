package me.flyray.bsin.blockchain.utils;

import com.alibaba.fastjson.JSONObject;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bolei
 * @date 2023/7/29 14:43
 * @desc web3.eth.sign()方法使用指定的账户对数据进行签名，该账户必须先解锁
 * web3.eth.sign("Hello world", "0x11f4d0A3c12e86B4b5F39B213F7E19D048276DAe")
 * .then(console.log);
 * > "0x30755ed65396facf86c53e6217c52b4daebe72aa4941d89635409de4c9c7f9466d4e9aaec7977f05e923889b33c0d0dd27d7226b6e6f56ce737465c5cfd04be400"
 * web3.value.eth.personal.ecRecover('abc', sign).then(res => {
 *             console.log(res)//获得该签名者的地址
 *       })
 */

@Slf4j
public class Web3WalletUtil {

    /**
     * 以太坊自定义的签名消息都以以下字符开头
     * 参考 eth_sign in https://github.com/ethereum/execution-apis
     */
    public static final String PERSONAL_MESSAGE_PREFIX = "u0019Ethereum Signed Message:n";

    /**
     * 对签名消息，原始消息，账号地址三项信息进行认证，判断签名是否有效
     * @param signature
     * @param message
     * @param address
     * @return
     */
    public static boolean validate(String signature, String message, String address) {
        //参考 eth_sign in https://github.com/ethereum/wiki/wiki/JSON-RPC
        // eth_sign
        // The sign method calculates an Ethereum specific signature with:
        //    sign(keccak256("x19Ethereum Signed Message:n" + len(message) + message))).
        //
        // By adding a prefix to the message makes the calculated signature recognisable as an Ethereum specific signature.
        // This prevents misuse where a malicious DApp can sign arbitrary data (e.g. transaction) and use the signature to
        // impersonate the victim.
        String prefix = PERSONAL_MESSAGE_PREFIX + message.length();
        byte[] msgHash = Hash.sha3((prefix + message).getBytes());

        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        byte v = signatureBytes[64];
        if (v < 27) {
            v += 27;
        }

        Sign.SignatureData sd = new Sign.SignatureData(
                v,
                Arrays.copyOfRange(signatureBytes, 0, 32),
                Arrays.copyOfRange(signatureBytes, 32, 64));

        String addressRecovered = null;
        boolean match = false;

        List<String> result = new ArrayList<>();

        // Iterate for each possible key to recover
        for (int i = 0; i < 4; i++) {
            BigInteger publicKey =
                    Sign.recoverFromSignature((byte) i,
                            new ECDSASignature(new BigInteger(1, sd.getR()), new BigInteger(1, sd.getS())),
                            msgHash);

            if (publicKey != null) {
                result.add("0x" + Keys.getAddress(publicKey));
            }
        }

        System.out.println(result.toString());

        // Iterate for each possible key to recover
        for (int i = 0; i < 4; i++) {
            BigInteger publicKey = Sign.recoverFromSignature(
                    (byte) i,
                    new ECDSASignature(new BigInteger(1, sd.getR()), new BigInteger(1, sd.getS())),
                    msgHash);
            if (publicKey != null) {
                addressRecovered = "0x" + Keys.getAddress(publicKey);
                if (addressRecovered.equals(address)) {
                    match = true;
                    break;
                }
            }
        }
        return match;
    }

    /**
     * verify data
     * get public key and get wallet address with sign
     *
     * @param data          明文数据
     * @param walletAddress 钱包地址
     * @param strSign       签名数据
     * @return boolean
     * @throws Exception e
     */
    public static boolean verifyTransaction(String data, String strSign, String walletAddress) throws SignatureException {
        if (StrUtil.isBlank(data)) {
            return false;
        }
        JSONObject jsonSign = JSONObject.parseObject(strSign);
        if (jsonSign == null) {
            return false;
        }

        byte v = jsonSign.getByte("v");
        byte[] r = Numeric.toBytesPadded(jsonSign.getBigInteger("r"), 32);
        byte[] s = Numeric.toBytesPadded(jsonSign.getBigInteger("s"), 32);

        Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);

        BigInteger publicKey = Sign.signedMessageToKey(data.getBytes(), signatureData);
        return StrUtil.equalsIgnoreCase("0x" + Keys.getAddress(publicKey), walletAddress);

    }

    /**
     * 签名
     *
     * @param content    原文信息
     * @param privateKey 私钥
     */
    public static String web3jSignPrefixedMessage(String content, String privateKey) {

        // todo 如果验签不成功，就不需要Hash.sha3 直接content.getBytes()就可以了
        // 原文信息字节数组
//        byte[] contentHashBytes = Hash.sha3(content.getBytes());
        byte[] contentHashBytes = content.getBytes();
        // 根据私钥获取凭证对象
        Credentials credentials = Credentials.create(privateKey);
        //
        Sign.SignatureData signMessage = Sign.signPrefixedMessage(contentHashBytes, credentials.getEcKeyPair());

        byte[] r = signMessage.getR();
        byte[] s = signMessage.getS();
        byte[] v = signMessage.getV();

        byte[] signByte = Arrays.copyOf(r, v.length + r.length + s.length);
        System.arraycopy(s, 0, signByte, r.length, s.length);
        System.arraycopy(v, 0, signByte, r.length + s.length, v.length);

        return Numeric.toHexString(signByte);
    }

    /**
     * 验证签名
     *
     * @param signature     验签数据
     * @param content       原文数据
     * @param walletAddress 钱包地址
     * @return 结果
     */
    public static Boolean web3jValidate(String signature, String content, String walletAddress) throws SignatureException {
        if (content == null) {
            return false;
        }
        // todo 如果验签不成功，就不需要Hash.sha3 直接content.getBytes()就可以了
        // 原文字节数组
//        byte[] msgHash = Hash.sha3(content.getBytes());
        byte[] msgHash = content.getBytes();
        // 签名数据
        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        byte v = signatureBytes[64];
        if (v < 27) {
            v += 27;
        }

        //通过摘要和签名后的数据，还原公钥
        Sign.SignatureData signatureData = new Sign.SignatureData(
                v,
                Arrays.copyOfRange(signatureBytes, 0, 32),
                Arrays.copyOfRange(signatureBytes, 32, 64));
        // 签名的前缀消息到密钥
        BigInteger publicKey = Sign.signedPrefixedMessageToKey(msgHash, signatureData);
        // 得到公钥(私钥对应的钱包地址)
        String parseAddress = "0x" + Keys.getAddress(publicKey);
        // 将钱包地址进行比对
        return parseAddress.equalsIgnoreCase(walletAddress);
    }

    public static void main(String[] args) throws SignatureException {
        //签名原文
        String message="gm wagmi frens";
        // 前端签名后的数据
        String signature="0xd5d4b0f6c77e253e73a12fa3e4dfd1850cc59e0bd38e1558e372ffd1d9c20cd834dccb3f77966d765790acbde036bba53ae2f96c1e41eea609e0bbf79f4050cb1c";
        //签名的钱包地址 0x5FDE75e7df280316E1c8763AcFBA5a275bbEfA16
        String address="0x362919Fa458d7a2282d88C77500810654741241b";
        Boolean result = validate(signature,message,address);
        log.info(result.toString());
        String pri = "";
        String signMessage = web3jSignPrefixedMessage(message,pri);
        log.info(signMessage);
        Boolean validateFlag = web3jValidate(signMessage,message,address);
        log.info(validateFlag.toString());
        System.out.println(validateFlag);
    }

}
