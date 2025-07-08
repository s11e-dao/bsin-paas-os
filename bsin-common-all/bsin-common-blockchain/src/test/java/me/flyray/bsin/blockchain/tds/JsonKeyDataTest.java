package me.flyray.bsin.blockchain.tds;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.flyray.bsin.blockchain.utils.DIDGeneratorUtil;
import me.flyray.bsin.blockchain.utils.KeyEncryptionUtil;
import org.junit.Test;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 测试JSON格式的密钥数据存储和恢复功能
 */
public class JsonKeyDataTest {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateDidProfileWithJsonKeyData() {
        // 创建连接器
        TrustedDataSpaceConnector connector = TrustedDataSpaceConnector.builder()
                .endpointUrl("https://test.example.com")
                .apiKey("test-api-key")
                .build();

        // 准备请求参数
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("name", "张三");
        requestMap.put("idNumber", "123456789012345678");

        // 创建DID档案
        Map<String, String> result = connector.createDidProfile(requestMap);

        // 验证返回结果
        assertNotNull("Result should not be null", result);
        assertEquals("success", result.get("status"));
        assertNotNull("DID should not be null", result.get("did"));
        assertNotNull("Key data JSON should not be null", result.get("keyDataJson"));
        
        // 打印结果
        System.out.println("DID: " + result.get("did"));
        System.out.println("Key Data JSON: " + result.get("keyDataJson"));
        
        // 验证JSON格式是否正确
        String keyDataJson = result.get("keyDataJson");
        try {
            DefaultTrustedDataSpaceConnector.KeyData keyData = 
                objectMapper.readValue(keyDataJson, DefaultTrustedDataSpaceConnector.KeyData.class);
            
            assertNotNull("Parsed key data should not be null", keyData);
            assertNotNull("DID should not be null", keyData.getDid());
            assertNotNull("Public key should not be null", keyData.getPublicKeyBase58());
            assertNotNull("Encrypted private key should not be null", keyData.getPrivateKeyBase58Encrypted());
            assertNotNull("Salt should not be null", keyData.getKeySalt());
            assertNotNull("Key ID should not be null", keyData.getKeyId());
            assertTrue("Create time should be positive", keyData.getCreateTime() > 0);
            
            System.out.println("Parsed KeyData:");
            System.out.println("  DID: " + keyData.getDid());
            System.out.println("  Public Key: " + keyData.getPublicKeyBase58());
            System.out.println("  Key ID: " + keyData.getKeyId());
            System.out.println("  Create Time: " + keyData.getCreateTime());
            
        } catch (Exception e) {
            fail("Failed to parse key data JSON: " + e.getMessage());
        }
    }

    @Test
    public void testKeyDataJsonSerialization() {
        try {
            // 生成一个DID文档结果
            String testDid = "did:s11e:test123456";
            DIDGeneratorUtil.DIDDocumentResult didResult = DIDGeneratorUtil.generateDIDDocument(
                testDid, null, "https://test.example.com/endpoint"
            );

            // 创建KeyData对象
            String salt = KeyEncryptionUtil.generateSalt();
            String encryptedPrivateKey = KeyEncryptionUtil.encryptPrivateKey(
                didResult.getPrivateKeyBase58(), testDid, salt);

            DefaultTrustedDataSpaceConnector.KeyData keyData = 
                new DefaultTrustedDataSpaceConnector.KeyData(
                    testDid,
                    didResult.getPublicKeyBase58(),
                    encryptedPrivateKey,
                    salt,
                    testDid + "#key-1"
                );

            // 序列化为JSON
            String json = objectMapper.writeValueAsString(keyData);
            assertNotNull("JSON should not be null", json);
            assertTrue("JSON should contain DID", json.contains(testDid));
            
            System.out.println("Serialized JSON: " + json);

            // 反序列化
            DefaultTrustedDataSpaceConnector.KeyData parsed = 
                objectMapper.readValue(json, DefaultTrustedDataSpaceConnector.KeyData.class);
            
            assertEquals("DID should match", testDid, parsed.getDid());
            assertEquals("Public key should match", didResult.getPublicKeyBase58(), parsed.getPublicKeyBase58());
            assertEquals("Salt should match", salt, parsed.getKeySalt());
            assertEquals("Key ID should match", testDid + "#key-1", parsed.getKeyId());

            // 测试私钥解密和KeyPair重建
            String decryptedPrivateKey = KeyEncryptionUtil.decryptPrivateKey(
                parsed.getPrivateKeyBase58Encrypted(),
                parsed.getDid(),
                parsed.getKeySalt()
            );
            
            assertEquals("Decrypted private key should match original", 
                didResult.getPrivateKeyBase58(), decryptedPrivateKey);

            System.out.println("✓ JSON序列化和反序列化测试通过");
            System.out.println("✓ 私钥加密解密测试通过");
            
        } catch (Exception e) {
            fail("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testLoadDidFromJsonString() {
        try {
            // 创建连接器
            DefaultTrustedDataSpaceConnector connector = 
                (DefaultTrustedDataSpaceConnector) TrustedDataSpaceConnector.builder()
                    .endpointUrl("https://test.example.com")
                    .build();

            // 生成测试数据
            String testDid = "did:s11e:loadtest123";
            DIDGeneratorUtil.DIDDocumentResult didResult = DIDGeneratorUtil.generateDIDDocument(
                testDid, null, "https://test.example.com/endpoint"
            );

            // 创建JSON格式的密钥数据
            String salt = KeyEncryptionUtil.generateSalt();
            String encryptedPrivateKey = KeyEncryptionUtil.encryptPrivateKey(
                didResult.getPrivateKeyBase58(), testDid, salt);

            DefaultTrustedDataSpaceConnector.KeyData keyData = 
                new DefaultTrustedDataSpaceConnector.KeyData(
                    testDid,
                    didResult.getPublicKeyBase58(),
                    encryptedPrivateKey,
                    salt,
                    testDid + "#key-1"
                );

            String keyDataJson = objectMapper.writeValueAsString(keyData);

            // 加载到内存
            connector.loadDidFromJsonString(testDid, keyDataJson);

            // 验证是否加载成功
            assertTrue("DID should exist after loading", connector.didExists(testDid));
            String retrievedJson = connector.getDidKeyDataJson(testDid);
            assertNotNull("Retrieved JSON should not be null", retrievedJson);
            
            // 验证获取公钥
            String publicKey = connector.getPublicKeyBase58(testDid);
            assertEquals("Public key should match", didResult.getPublicKeyBase58(), publicKey);

            System.out.println("✓ 从JSON字符串加载DID测试通过");
            System.out.println("  DID: " + testDid);
            System.out.println("  公钥验证通过");
            
        } catch (Exception e) {
            fail("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testParseKeyDataFromJson() {
        try {
            // 创建连接器
            TrustedDataSpaceConnector connector = TrustedDataSpaceConnector.builder()
                    .endpointUrl("https://test.example.com")
                    .apiKey("test-api-key")
                    .build();

            // 准备请求参数
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("name", "李四");
            requestMap.put("idNumber", "987654321098765432");

            // 创建DID档案
            Map<String, String> result = connector.createDidProfile(requestMap);
            String keyDataJson = result.get("keyDataJson");
            String originalPublicKey = result.get("publicKeyBase58");
            String originalPrivateKey = result.get("privateKeyBase58");

            System.out.println("=== 测试从keyDataJson解析密钥数据 ===");
            System.out.println("原始keyDataJson: " + keyDataJson);

            // 测试解析公钥（明文）
            String parsedPublicKey = DefaultTrustedDataSpaceConnector.parsePublicKeyBase58FromJson(keyDataJson);
            assertNotNull("Parsed public key should not be null", parsedPublicKey);
            assertEquals("Parsed public key should match original", originalPublicKey, parsedPublicKey);
            System.out.println("✓ 公钥解析成功: " + parsedPublicKey);

            // 测试解析私钥（需要解密）
            String parsedPrivateKey = DefaultTrustedDataSpaceConnector.parsePrivateKeyBase58FromJson(keyDataJson);
            assertNotNull("Parsed private key should not be null", parsedPrivateKey);
            assertEquals("Parsed private key should match original", originalPrivateKey, parsedPrivateKey);
            System.out.println("✓ 私钥解密解析成功: " + parsedPrivateKey);

            // 测试解析完整密钥信息
            Map<String, String> parsedKeyData = DefaultTrustedDataSpaceConnector.parseKeyDataFromJson(keyDataJson);
            assertNotNull("Parsed key data should not be null", parsedKeyData);
            assertEquals("success", parsedKeyData.get("status"));
            
            // 验证各个字段
            assertEquals("Public key should match", originalPublicKey, parsedKeyData.get("publicKeyBase58"));
            assertEquals("Private key should match", originalPrivateKey, parsedKeyData.get("privateKeyBase58"));
            assertNotNull("DID should not be null", parsedKeyData.get("did"));
            assertNotNull("Key ID should not be null", parsedKeyData.get("keyId"));
            assertNotNull("Create time should not be null", parsedKeyData.get("createTime"));
            assertNotNull("Encrypted private key should not be null", parsedKeyData.get("privateKeyBase58Encrypted"));
            assertNotNull("Salt should not be null", parsedKeyData.get("keySalt"));

            System.out.println("✓ 完整密钥信息解析成功:");
            System.out.println("  DID: " + parsedKeyData.get("did"));
            System.out.println("  公钥: " + parsedKeyData.get("publicKeyBase58"));
            System.out.println("  私钥: " + parsedKeyData.get("privateKeyBase58").substring(0, 20) + "...");
            System.out.println("  密钥ID: " + parsedKeyData.get("keyId"));
            System.out.println("  创建时间: " + parsedKeyData.get("createTime"));

        } catch (Exception e) {
            fail("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testParseInvalidKeyDataJson() {
        // 测试解析无效JSON
        String invalidJson = "invalid json string";
        
        String publicKey = DefaultTrustedDataSpaceConnector.parsePublicKeyBase58FromJson(invalidJson);
        assertNull("Public key should be null for invalid JSON", publicKey);

        String privateKey = DefaultTrustedDataSpaceConnector.parsePrivateKeyBase58FromJson(invalidJson);
        assertNull("Private key should be null for invalid JSON", privateKey);

        Map<String, String> keyData = DefaultTrustedDataSpaceConnector.parseKeyDataFromJson(invalidJson);
        assertEquals("error", keyData.get("status"));
        assertNotNull("Error message should be present", keyData.get("message"));

        System.out.println("✓ 无效JSON处理测试通过");
    }
} 