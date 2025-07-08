package me.flyray.bsin.blockchain.utils;

import foundation.identity.did.DIDDocument;
import foundation.identity.did.Service;
import foundation.identity.did.VerificationMethod;
import org.bitcoinj.core.Base58;

import java.net.URI;
import java.security.*;
import java.util.*;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * DID随机生成和签名工具类 (Java 17版本)
 * 提供DID文档生成、密钥对生成、签名验证等功能
 * 使用Java 17原生Ed25519支持
 */
public class DIDGeneratorUtil {

    private static final String DID_METHOD = "s11e"; // 可以替换为其他方法如 "key", "web" 等
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
    public static DIDDocumentResult generateDIDDocument() throws NoSuchAlgorithmException {
        return generateDIDDocument(null, null, null);
    }

    /**
     * 生成指定参数的DID文档
     */
    public static DIDDocumentResult generateDIDDocument(String didId, String controllerId, String serviceEndpoint)
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
        VerificationMethod verificationMethod = VerificationMethod.builder()
                .id(URI.create(keyId))
                .types(List.of(KEY_TYPE))
                .publicKeyBase58(publicKeyBase58)
                .build();

        List<VerificationMethod> verificationMethods = new ArrayList<>();
        verificationMethods.add(verificationMethod);

        // 创建服务端点
        List<Service> services = new ArrayList<>();
        if (serviceEndpoint != null) {
            Service service = Service.builder()
                    .type("DIDCommService")
                    .serviceEndpoint(serviceEndpoint)
                    .build();
            services.add(service);
        }

        // 构建DID文档
        DIDDocument.Builder builder = DIDDocument.builder()
                .id(URI.create(didId))
                .verificationMethods(verificationMethods)
                .authenticationVerificationMethods(Collections.singletonList(
                        VerificationMethod.builder().id(verificationMethod.getId()).build()))
                .assertionMethodVerificationMethods(Collections.singletonList(
                        VerificationMethod.builder().id(verificationMethod.getId()).build()));

        // 添加控制器（如果指定）
        if (controllerId != null) {
            builder.controller(URI.create(controllerId));
        }

        // 添加服务
        if (!services.isEmpty()) {
            builder.services(services);
        }

        DIDDocument didDocument = builder.build();

        return new DIDDocumentResult(didDocument, keyPair, publicKeyBase58, privateKeyBase58);
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
    public static class DIDDocumentResult {
        private final DIDDocument didDocument;
        private final KeyPair keyPair;
        private final String publicKeyBase58;
        private final String privateKeyBase58;

        public DIDDocumentResult(DIDDocument didDocument, KeyPair keyPair,
                                 String publicKeyBase58, String privateKeyBase58) {
            this.didDocument = didDocument;
            this.keyPair = keyPair;
            this.publicKeyBase58 = publicKeyBase58;
            this.privateKeyBase58 = privateKeyBase58;
        }

        public DIDDocument getDIDDocument() { return didDocument; }
        public KeyPair getKeyPair() { return keyPair; }
        public String getPublicKeyBase58() { return publicKeyBase58; }
        public String getPrivateKeyBase58() { return privateKeyBase58; }
        public String getDID() { return didDocument.getId().toString(); }

        /**
         * 获取JSON格式的DID文档
         */
        public String toJson() {
            return didDocument.toJson();
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
            System.out.println(toJson());
        }
    }

    /**
     * 示例：创建带有自定义参数的DID
     */
    public static DIDDocumentResult generateCustomDID(String method, String serviceEndpoint)
            throws NoSuchAlgorithmException {
        String didId = generateRandomDID(method);
        String controllerId = generateRandomDID(method); // 生成控制器DID
        return generateDIDDocument(didId, controllerId, serviceEndpoint);
    }

}
