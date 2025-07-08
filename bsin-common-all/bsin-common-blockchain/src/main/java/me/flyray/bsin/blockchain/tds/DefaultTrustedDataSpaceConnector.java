package me.flyray.bsin.blockchain.tds;


import foundation.identity.did.DIDDocument;
import foundation.identity.did.Service;
import foundation.identity.did.VerificationMethod;
import me.flyray.bsin.blockchain.utils.DIDGeneratorUtil;
import me.flyray.bsin.blockchain.utils.KeyEncryptionUtil;
import org.apache.commons.collections4.MapUtils;

import java.net.URI;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link TrustedDataSpaceConnector} 的一个默认/内部实现。
 * 这个类是私有的，外部用户只能通过 Builder 来创建。
 */

public class DefaultTrustedDataSpaceConnector implements TrustedDataSpaceConnector {

    private final String endpointUrl;
    private final String apiKey;
    private final String clientId;
    private final char[] clientSecret;
    private final byte[] clientCertificate;
    private final char[] clientCertificatePassword;
    private final byte[] trustStore;
    private final char[] trustStorePassword;
    private final long connectTimeoutMillis;
    private final long readTimeoutMillis;
    private final int maxRetries;
    private final String proxyHost;
    private final int proxyPort;
    private final Map<String, String> customHeaders;

    // 存储DID对应的密钥信息（JSON字符串格式）
    private final Map<String, String> didKeyDataMap = new ConcurrentHashMap<>();
    
    // Jackson对象映射器，用于JSON序列化/反序列化
    private final ObjectMapper objectMapper = new ObjectMapper();

    private boolean connected = false; // 示例连接状态

    /**
     * 密钥数据的JSON存储格式
     */
    public static class KeyData {
        @JsonProperty("did")
        private String did;
        
        @JsonProperty("publicKeyBase58")
        private String publicKeyBase58;
        
        @JsonProperty("privateKeyBase58Encrypted")
        private String privateKeyBase58Encrypted;
        
        @JsonProperty("keySalt")
        private String keySalt;
        
        @JsonProperty("keyId")
        private String keyId;
        
        @JsonProperty("createTime")
        private long createTime;

        // 默认构造函数
        public KeyData() {
        }

        // 带参数构造函数
        public KeyData(String did, String publicKeyBase58, String privateKeyBase58Encrypted, 
                      String keySalt, String keyId) {
            this.did = did;
            this.publicKeyBase58 = publicKeyBase58;
            this.privateKeyBase58Encrypted = privateKeyBase58Encrypted;
            this.keySalt = keySalt;
            this.keyId = keyId;
            this.createTime = System.currentTimeMillis();
        }

        // Getters and Setters
        public String getDid() { return did; }
        public void setDid(String did) { this.did = did; }

        public String getPublicKeyBase58() { return publicKeyBase58; }
        public void setPublicKeyBase58(String publicKeyBase58) { this.publicKeyBase58 = publicKeyBase58; }

        public String getPrivateKeyBase58Encrypted() { return privateKeyBase58Encrypted; }
        public void setPrivateKeyBase58Encrypted(String privateKeyBase58Encrypted) { this.privateKeyBase58Encrypted = privateKeyBase58Encrypted; }

        public String getKeySalt() { return keySalt; }
        public void setKeySalt(String keySalt) { this.keySalt = keySalt; }

        public String getKeyId() { return keyId; }
        public void setKeyId(String keyId) { this.keyId = keyId; }

        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
    }

    // 包私有构造函数，接收 Builder 实例
    DefaultTrustedDataSpaceConnector(Builder builder) {
        this.endpointUrl = builder.endpointUrl;
        this.apiKey = builder.apiKey;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret != null ? builder.clientSecret.clone() : null;
        this.clientCertificate = builder.clientCertificate != null ? builder.clientCertificate.clone() : null;
        this.clientCertificatePassword = builder.clientCertificatePassword != null ? builder.clientCertificatePassword.clone() : null;
        this.trustStore = builder.trustStore != null ? builder.trustStore.clone() : null;
        this.trustStorePassword = builder.trustStorePassword != null ? builder.trustStorePassword.clone() : null;
        this.connectTimeoutMillis = builder.connectTimeoutMillis;
        this.readTimeoutMillis = builder.readTimeoutMillis;
        this.maxRetries = builder.maxRetries;
        this.proxyHost = builder.proxyHost;
        this.proxyPort = builder.proxyPort;
        // 确保 customHeaders 是不可变的或至少是一个安全的副本
        this.customHeaders = Collections.unmodifiableMap(new HashMap<>(builder.customHeaders));

        // 在实际应用中，这里会初始化 HTTP 客户端或其他网络组件
        System.out.println("TrustedDataSpaceConnector configured for endpoint: " + this.endpointUrl);
        if (this.apiKey != null) System.out.println("Using API Key authentication.");
        // ... 其他配置信息的日志输出
    }

    @Override
    public Map<String, String> createDidProfile(Map<String, Object> requestMap) {
        try {
            // 姓名
            String name = MapUtils.getString(requestMap, "name");
            // 身份证号
            String idNumber = MapUtils.getString(requestMap, "idNumber");

            // 根据用户信息生成唯一的DID标识符
            String didIdentifier = generateDidIdentifier(name, idNumber);
            String customDid = "did:s11e:" + didIdentifier;
            
            // 构建服务端点URL
            String serviceEndpoint = "https://s11e.network/endpoint/" + didIdentifier;

            // 使用工具类生成完整的DID文档
            DIDGeneratorUtil.DIDDocumentResult didResult = DIDGeneratorUtil.generateDIDDocument(
                    customDid,  // 使用我们生成的DID标识符
                    null,         // 无控制器
                    serviceEndpoint // 服务端点
            );

            // 获取生成的DID文档
            DIDDocument didDocument = didResult.getDIDDocument();
            
            // 处理密钥信息为JSON字符串格式
            String keyDataJson = createKeyDataJson(customDid, didResult);
            
            System.out.println("Generated DID for user: " + name + ", DID: " + customDid);
            System.out.println("Public Key (Base58): " + didResult.getPublicKeyBase58());
            System.out.println("DID Document JSON:");
            System.out.println(didDocument.toJson(true));
            System.out.println("Key data stored as JSON in memory for DID: " + customDid);

            // 返回包含完整 DID 信息的 Map
            Map<String, String> result = new HashMap<>();
            result.put("did", customDid);
            result.put("didDocument", didDocument.toJson());
            result.put("serviceEndpoint", serviceEndpoint);
            result.put("status", "success");
            result.put("name", name);
            result.put("idNumber", idNumber);
            result.put("keyId", customDid + "#key-1");

            // 添加密钥数据JSON字符串
            result.put("keyDataJson", keyDataJson);
            result.put("dbStorableData", "prepared"); // 标识已准备好数据库存储数据

            return result;
            
        } catch (Exception e) {
            System.err.println("Error generating DID document: " + e.getMessage());
            
            // 返回错误结果
            Map<String, String> errorResult = new HashMap<>();
            errorResult.put("status", "error");
            errorResult.put("message", "Failed to generate DID: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 创建密钥数据的JSON字符串
     */
    private String createKeyDataJson(String did, DIDGeneratorUtil.DIDDocumentResult didResult) {
        try {
            // 生成加密盐值
            String salt = KeyEncryptionUtil.generateSalt();
            
            // 加密私钥
            String encryptedPrivateKey = KeyEncryptionUtil.encryptPrivateKey(
                didResult.getPrivateKeyBase58(), did, salt);
            
            // 创建密钥数据对象
            KeyData keyData = new KeyData(
                did,
                didResult.getPublicKeyBase58(),
                encryptedPrivateKey,
                salt,
                did + "#key-1"
            );
            
            // 转换为JSON字符串
            return objectMapper.writeValueAsString(keyData);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create key data JSON", e);
        }
    }

    /**
     * 从JSON字符串恢复KeyPair对象
     */
    private KeyPair rebuildKeyPairFromJson(String keyDataJson) {
        try {
            // 解析JSON
            KeyData keyData = objectMapper.readValue(keyDataJson, KeyData.class);
            
            // 解密私钥
            String privateKeyBase58 = KeyEncryptionUtil.decryptPrivateKey(
                keyData.getPrivateKeyBase58Encrypted(),
                keyData.getDid(),
                keyData.getKeySalt()
            );
            
            // 从Base58重建密钥对
            PrivateKey privateKey = DIDGeneratorUtil.decodePrivateKeyFromBase58(privateKeyBase58);
            PublicKey publicKey = DIDGeneratorUtil.decodePublicKeyFromBase58(keyData.getPublicKeyBase58());
            
            return new KeyPair(publicKey, privateKey);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to rebuild KeyPair from JSON", e);
        }
    }

    /**
     * 从JSON字符串加载DID密钥数据到内存
     */
    public void loadDidFromJsonString(String did, String keyDataJson) {
        try {
            didKeyDataMap.put(did, keyDataJson);
            System.out.println("Loaded DID key data from JSON: " + did);
        } catch (Exception e) {
            System.err.println("Failed to load DID from JSON: " + e.getMessage());
        }
    }

    /**
     * 获取DID对应的密钥数据JSON字符串
     */
    public String getDidKeyDataJson(String did) {
        return didKeyDataMap.get(did);
    }

    /**
     * 从keyDataJson中解析出publicKeyBase58（明文）
     * @param keyDataJson 密钥数据JSON字符串
     * @return publicKeyBase58，解析失败返回null
     */
    public static String parsePublicKeyBase58FromJson(String keyDataJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            KeyData keyData = mapper.readValue(keyDataJson, KeyData.class);
            return keyData.getPublicKeyBase58();
        } catch (Exception e) {
            System.err.println("Failed to parse publicKeyBase58 from JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * 从keyDataJson中解析出privateKeyBase58（需要解密）
     * @param keyDataJson 密钥数据JSON字符串
     * @return privateKeyBase58明文，解析或解密失败返回null
     */
    public static String parsePrivateKeyBase58FromJson(String keyDataJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            KeyData keyData = mapper.readValue(keyDataJson, KeyData.class);
            
            // 解密私钥
            String privateKeyBase58 = KeyEncryptionUtil.decryptPrivateKey(
                keyData.getPrivateKeyBase58Encrypted(),
                keyData.getDid(),
                keyData.getKeySalt()
            );
            
            return privateKeyBase58;
        } catch (Exception e) {
            System.err.println("Failed to parse privateKeyBase58 from JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * 从keyDataJson中解析出完整的密钥信息
     * @param keyDataJson 密钥数据JSON字符串
     * @return 包含解析结果的Map，包含publicKeyBase58和privateKeyBase58
     */
    public static Map<String, String> parseKeyDataFromJson(String keyDataJson) {
        Map<String, String> result = new HashMap<>();
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            KeyData keyData = mapper.readValue(keyDataJson, KeyData.class);
            
            // 解析公钥（明文）
            result.put("publicKeyBase58", keyData.getPublicKeyBase58());
            result.put("did", keyData.getDid());
            result.put("keyId", keyData.getKeyId());
            result.put("createTime", String.valueOf(keyData.getCreateTime()));
            
            // 解密私钥
            String privateKeyBase58 = KeyEncryptionUtil.decryptPrivateKey(
                keyData.getPrivateKeyBase58Encrypted(),
                keyData.getDid(),
                keyData.getKeySalt()
            );
            result.put("privateKeyBase58", privateKeyBase58);
            
            // 加密相关信息
            result.put("privateKeyBase58Encrypted", keyData.getPrivateKeyBase58Encrypted());
            result.put("keySalt", keyData.getKeySalt());
            
            result.put("status", "success");
            
        } catch (Exception e) {
            System.err.println("Failed to parse key data from JSON: " + e.getMessage());
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        
        return result;
    }

    /**
     * 用DID签名数据
     * @param did DID标识符
     * @param data 要签名的数据
     * @return 签名结果的Base64编码字符串，失败返回null
     */
    private String signData(String did, String keyDataJson,  String data) {
        try {
            // 获取DID对应的密钥数据JSON
            if (keyDataJson == null) {
                System.err.println("No key data found for DID: " + did + ". Please load from database first.");
                return null;
            }

            // 从JSON恢复KeyPair
            KeyPair keyPair = rebuildKeyPairFromJson(keyDataJson);

            // 使用工具类进行签名
            byte[] signature = DIDGeneratorUtil.signData(data.getBytes("UTF-8"), keyPair.getPrivate());
            
            // 将签名转换为Base64编码
            return java.util.Base64.getEncoder().encodeToString(signature);
            
        } catch (Exception e) {
            System.err.println("Error signing data for DID " + did + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * 用DID验证签名
     * @param did DID标识符
     * @param data 原始数据
     * @param signature Base64编码的签名数据
     * @return 验证结果，true表示验证成功，false表示验证失败
     */
    private boolean verifySign(String did, String keyDataJson, String data, String signature) {
        try {
            // 获取DID对应的密钥数据JSON
            if (keyDataJson == null) {
                System.err.println("No key data found for DID: " + did + ". Please load from database first.");
                return false;
            }

            // 从JSON恢复KeyPair
            KeyPair keyPair = rebuildKeyPairFromJson(keyDataJson);

            // 将Base64签名解码为字节数组
            byte[] signatureBytes = java.util.Base64.getDecoder().decode(signature);
            
            // 使用工具类进行验证
            return DIDGeneratorUtil.verifySignature(
                    data.getBytes("UTF-8"), 
                    signatureBytes, 
                    keyPair.getPublic()
            );
            
        } catch (Exception e) {
            System.err.println("Error verifying signature for DID " + did + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取DID对应的公钥（Base58编码）
     * @param did DID标识符
     * @return Base58编码的公钥，如果不存在返回null
     */
    public String getPublicKeyBase58(String did) {
        try {
            String keyDataJson = didKeyDataMap.get(did);
            if (keyDataJson != null) {
                KeyData keyData = objectMapper.readValue(keyDataJson, KeyData.class);
                return keyData.getPublicKeyBase58();
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error getting public key for DID " + did + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * 检查DID是否存在
     * @param did DID标识符
     * @return 如果DID存在返回true，否则返回false
     */
    public boolean didExists(String did) {
        return didKeyDataMap.containsKey(did);
    }

    /**
     * 根据用户信息生成唯一的DID标识符
     * @param name 用户姓名
     * @param idNumber 身份证号
     * @return 生成的DID标识符
     */
    private String generateDidIdentifier(String name, String idNumber) {
        try {
            // 组合用户信息
            String userInfo = (name != null ? name : "") + ":" + (idNumber != null ? idNumber : "");
            
            // 使用SHA-256生成哈希值
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(userInfo.getBytes("UTF-8"));
            
            // 转换为十六进制字符串并截取前16位作为DID标识符
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            // 返回前16位十六进制字符作为DID标识符
            return hexString.toString().substring(0, 16);
            
        } catch (Exception e) {
            // 如果发生异常，使用时间戳作为备用方案
            System.err.println("Error generating DID identifier: " + e.getMessage());
            return "fallback" + System.currentTimeMillis();
        }
    }

    @Override
    public String uploadData(String dataId, Object payload) {
        System.out.println("Uploading data with ID '" + dataId + "' to " + endpointUrl);
        // 实际的上传逻辑
        return "upload-receipt-" + dataId + "-" + System.currentTimeMillis();
    }

    @Override
    public Object downloadData(String dataId) {
        System.out.println("Downloading data with ID '" + dataId + "' from " + endpointUrl);
        // 实际的下载逻辑
        return "Downloaded data for " + dataId;
    }

    // --- Getter 实现 (用于演示) ---
    @Override
    public String getEndpointUrl() {
        return this.endpointUrl;
    }

    @Override
    public String getApiKey() {
        return this.apiKey;
    }

    @Override
    public Map<String, String> getCustomHeaders() {
        return this.customHeaders; // 已是不可变 Map
    }

    @Override
    public long getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }

    // 在这里可以添加其他 getter 方法来暴露配置信息，如果需要的话。
    // 例如: getClientId(), getConnectTimeoutMillis() 等。
    // 密码和证书内容通常不应通过 getter 直接暴露。

}
