package me.flyray.bsin.blockchain.utils;

import me.flyray.bsin.blockchain.did.SimpleDIDDocument;
import org.bitcoinj.core.Base58;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * 简化的 DID 生成器工具类
 * 替代有问题的 DIDGeneratorUtil
 */
public class SimpleDIDGeneratorUtil {

    private static final String DID_METHOD = "s11e";
    private static final String KEY_TYPE = "Ed25519VerificationKey2018";
    private static final String SIGNATURE_ALGORITHM = "Ed25519";
    private static final String KEY_ALGORITHM = "Ed25519";

    /**
     * 生成随机DID标识符
     */
    public static String generateRandomDID() {
        return generateRandomDID(DID_METHOD);
    }

    /**
     * 生成指定方法的随机DID标识符
     */
    public static String generateRandomDID(String method) {
        String identifier = generateRandomIdentifier();
        return String.format("did:%s:%s", method, identifier);
    }

    /**
     * 生成随机标识符（使用UUID）
     */
    private static String generateRandomIdentifier() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成Ed25519密钥对 (Java 17原生支持)
     */
    public static KeyPair generateEd25519KeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 将公钥转换为Base58编码
     */
    public static String encodePublicKeyToBase58(PublicKey publicKey) {
        return Base58.encode(publicKey.getEncoded());
    }

    /**
     * 将私钥转换为Base58编码
     */
    public static String encodePrivateKeyToBase58(PrivateKey privateKey) {
        return Base58.encode(privateKey.getEncoded());
    }

    /**
     * 生成完整的DID文档对象
     */
    public static SimpleDIDDocumentResult generateDIDDocument() throws NoSuchAlgorithmException {
        return generateDIDDocument(null, null, null);
    }

    /**
     * 生成指定参数的DID文档
     */
    public static SimpleDIDDocumentResult generateDIDDocument(String didId, String controllerId, String serviceEndpoint)
            throws NoSuchAlgorithmException {

        // 生成DID标识符
        if (didId == null) {
            didId = generateRandomDID();
        }

        // 生成密钥对
        KeyPair keyPair = generateEd25519KeyPair();
        String publicKeyBase58 = encodePublicKeyToBase58(keyPair.getPublic());
        String privateKeyBase58 = encodePrivateKeyToBase58(keyPair.getPrivate());

        // 创建验证方法
        String keyId = didId + "#key-1";
        SimpleDIDDocument.SimpleVerificationMethod verificationMethod = 
            new SimpleDIDDocument.SimpleVerificationMethod(keyId, KEY_TYPE, didId, publicKeyBase58);

        List<SimpleDIDDocument.SimpleVerificationMethod> verificationMethods = 
            Arrays.asList(verificationMethod);

        // 创建服务端点
        List<SimpleDIDDocument.SimpleService> services = new ArrayList<>();
        if (serviceEndpoint != null) {
            SimpleDIDDocument.SimpleService service = 
                new SimpleDIDDocument.SimpleService(didId + "#service-1", "DIDCommService", serviceEndpoint);
            services.add(service);
        }

        // 构建DID文档
        SimpleDIDDocument didDocument = new SimpleDIDDocument();
        didDocument.setId(didId);
        didDocument.setVerificationMethod(verificationMethods);
        didDocument.setAuthentication(Arrays.asList(keyId));
        didDocument.setAssertionMethod(Arrays.asList(keyId));
        
        // 添加控制器（如果指定）
        if (controllerId != null) {
            didDocument.setController(controllerId);
        }

        // 添加服务
        if (!services.isEmpty()) {
            didDocument.setService(services);
        }

        return new SimpleDIDDocumentResult(didDocument, keyPair, publicKeyBase58, privateKeyBase58);
    }

    /**
     * 使用私钥对数据进行签名 (Java 17原生)
     */
    public static byte[] signData(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    /**
     * 使用公钥验证签名 (Java 17原生)
     */
    public static boolean verifySignature(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }

    /**
     * 从Base58编码恢复公钥 (Java 17原生)
     */
    public static PublicKey decodePublicKeyFromBase58(String base58Key) throws Exception {
        byte[] keyBytes = Base58.decode(base58Key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 从Base58编码恢复私钥 (Java 17原生)
     */
    public static PrivateKey decodePrivateKeyFromBase58(String base58Key) throws Exception {
        byte[] keyBytes = Base58.decode(base58Key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * DID文档生成结果包装类
     */
    public static class SimpleDIDDocumentResult {
        private final SimpleDIDDocument didDocument;
        private final KeyPair keyPair;
        private final String publicKeyBase58;
        private final String privateKeyBase58;

        public SimpleDIDDocumentResult(SimpleDIDDocument didDocument, KeyPair keyPair,
                                 String publicKeyBase58, String privateKeyBase58) {
            this.didDocument = didDocument;
            this.keyPair = keyPair;
            this.publicKeyBase58 = publicKeyBase58;
            this.privateKeyBase58 = privateKeyBase58;
        }

        public SimpleDIDDocument getDIDDocument() { return didDocument; }
        public KeyPair getKeyPair() { return keyPair; }
        public String getPublicKeyBase58() { return publicKeyBase58; }
        public String getPrivateKeyBase58() { return privateKeyBase58; }
        public String getDID() { return didDocument.getId(); }

        /**
         * 获取JSON格式的DID文档
         */
        public String toJson() {
            return didDocument.toJson();
        }

        /**
         * 获取格式化的JSON格式的DID文档
         */
        public String toJson(boolean prettyPrint) {
            return didDocument.toJson(prettyPrint);
        }

        /**
         * 打印详细信息
         */
        public void printDetails() {
            System.out.println("=== DID Document Details ===");
            System.out.println("DID: " + getDID());
            System.out.println("Public Key (Base58): " + publicKeyBase58);
            System.out.println("Private Key (Base58): " + privateKeyBase58);
            System.out.println("DID Document JSON:");
            System.out.println(toJson(true));
        }
    }

    /**
     * 示例：创建带有自定义参数的DID
     */
    public static SimpleDIDDocumentResult generateCustomDID(String method, String serviceEndpoint)
            throws NoSuchAlgorithmException {
        String didId = generateRandomDID(method);
        String controllerId = generateRandomDID(method); // 生成控制器DID
        return generateDIDDocument(didId, controllerId, serviceEndpoint);
    }
}
