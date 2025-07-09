package me.flyray.bsin.blockchain.tds;

import foundation.identity.did.DIDDocument;
import me.flyray.bsin.blockchain.utils.DIDGeneratorUtil;
import me.flyray.bsin.blockchain.utils.KeyEncryptionUtil;
import org.apache.commons.collections4.MapUtils;

import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link TrustedDataSpaceConnector} 的一个默认/内部实现。
 * 精简版本，只保留核心DID操作、签名和验证功能。
 */
public class DefaultTrustedDataSpaceConnector implements TrustedDataSpaceConnector {

    // Jackson对象映射器，用于JSON序列化/反序列化
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        System.out.println("TrustedDataSpaceConnector initialized for DID operations");
    }

    @Override
    public Map<String, String> createDidProfile(Map<String, Object> requestMap) {
        try {
            // 姓名
            String name = MapUtils.getString(requestMap, "name");
            // 身份证号
            String idNumber = MapUtils.getString(requestMap, "idNumber");

            // 从请求参数中获取盐值
            String salt = MapUtils.getString(requestMap, "salt");

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
            
            // 处理密钥信息为加密JSON字符串格式
            String didKeyData = createKeyDataJson(customDid, salt, didResult);
            
            System.out.println("Generated DID for user: " + name + ", DID: " + customDid);
            System.out.println("Public Key (Base58): " + didResult.getPublicKeyBase58());
            System.out.println("DID Document JSON:");
            System.out.println(didDocument.toJson(true));
            System.out.println("Encrypted key data stored for DID: " + customDid);

            // 返回包含完整 DID 信息的 Map
            Map<String, String> result = new HashMap<>();
            result.put("did", customDid);
            result.put("didDocument", didDocument.toJson());
            result.put("serviceEndpoint", serviceEndpoint);
            result.put("status", "success");
            result.put("name", name);
            result.put("idNumber", idNumber);
            result.put("keyId", customDid + "#key-1");

            // 添加加密的密钥数据包装JSON字符串
            result.put("didKeyData", didKeyData);

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
     * 用DID签名数据
     * @param did DID标识符
     * @param salt 盐值，用于解密
     * @param encryptedDidKeyData 加密的密钥数据
     * @param data 要签名的数据
     * @return 签名结果的Base64编码字符串，失败返回null
     */
    public static String signData(String did, String salt, String encryptedDidKeyData, String data) {
        try {
            if (encryptedDidKeyData == null) {
                System.err.println("No key data found for DID: " + did + ". Please load from database first.");
                return null;
            }

            // 从加密的JSON恢复KeyPair
            KeyPair keyPair = rebuildKeyPairFromJson(encryptedDidKeyData, did, salt);

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
     * @param salt 盐值，用于解密
     * @param encryptedDidKeyData 加密的密钥数据
     * @param data 原始数据
     * @param signature Base64编码的签名数据
     * @return 验证结果，true表示验证成功，false表示验证失败
     */
    public static boolean verifySign(String did, String salt, String encryptedDidKeyData, String data, String signature) {
        try {
            if (encryptedDidKeyData == null) {
                System.err.println("No key data found for DID: " + did + ". Please load from database first.");
                return false;
            }

            // 从加密的JSON恢复KeyPair
            KeyPair keyPair = rebuildKeyPairFromJson(encryptedDidKeyData, did, salt);

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

    // ===== 私有辅助方法 =====

    /**
     * 创建加密的密钥数据JSON字符串
     */
    private String createKeyDataJson(String did, String salt, DIDGeneratorUtil.DIDDocumentResult didResult) {
        try {
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
            String keyDataJson = objectMapper.writeValueAsString(keyData);
            System.out.println("Key data JSON: " + keyDataJson);
            
            // 使用AES加密keyDataJson字符串
            String encryptedKeyData = KeyEncryptionUtil.encryptString(keyDataJson, did, salt);
            System.out.println("Encrypted key data: " + encryptedKeyData);

            return encryptedKeyData;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create encrypted key data JSON", e);
        }
    }

    /**
     * 解密加密的JSON字符串
     */
    private static String decryptKeyDataJson(String encryptedKeyDataJson, String did, String salt) {
        try {
            return KeyEncryptionUtil.decryptString(encryptedKeyDataJson, did, salt);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt key data JSON", e);
        }
    }

    /**
     * 从加密的JSON字符串恢复KeyPair对象
     */
    private static KeyPair rebuildKeyPairFromJson(String encryptedKeyDataJson, String did, String salt) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            // 解密JSON字符串
            String keyDataJson = decryptKeyDataJson(encryptedKeyDataJson, did, salt);
            
            // 解析JSON
            KeyData keyData = mapper.readValue(keyDataJson, KeyData.class);
            
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
            throw new RuntimeException("Failed to rebuild KeyPair from encrypted JSON", e);
        }
    }

    /**
     * 从加密的JSON字符串中解析公钥
     * @param encryptedKeyDataJson 加密的密钥数据JSON字符串
     * @param did DID标识符
     * @param salt 盐值
     * @return Base58编码的公钥字符串
     */
    public static String parsePublicKeyBase58FromJson(String encryptedKeyDataJson, String did, String salt) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            // 解密JSON字符串
            String keyDataJson = decryptKeyDataJson(encryptedKeyDataJson, did, salt);
            
            // 解析JSON
            KeyData keyData = mapper.readValue(keyDataJson, KeyData.class);
            
            return keyData.getPublicKeyBase58();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse public key from encrypted JSON", e);
        }
    }

    /**
     * 从加密的JSON字符串中解析密钥数据
     * @param encryptedKeyDataJson 加密的密钥数据JSON字符串
     * @param did DID标识符
     * @param salt 盐值
     * @return 解析后的KeyData对象
     */
    public static KeyData parseKeyDataFromJson(String encryptedKeyDataJson, String did, String salt) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            // 解密JSON字符串
            String keyDataJson = decryptKeyDataJson(encryptedKeyDataJson, did, salt);
            
            // 解析JSON
            return mapper.readValue(keyDataJson, KeyData.class);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse key data from encrypted JSON", e);
        }
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

    // ===== 接口实现（保持兼容性） =====

    @Override
    public String uploadData(String dataId, Object payload) {
        throw new UnsupportedOperationException("uploadData not supported in this simplified version");
    }

    @Override
    public Object downloadData(String dataId) {
        throw new UnsupportedOperationException("downloadData not supported in this simplified version");
    }

    @Override
    public String getEndpointUrl() {
        return "simplified-version";
    }

    @Override
    public String getApiKey() {
        return null;
    }

    @Override
    public Map<String, String> getCustomHeaders() {
        return new HashMap<>();
    }

    @Override
    public long getConnectTimeoutMillis() {
        return 0;
    }
}
