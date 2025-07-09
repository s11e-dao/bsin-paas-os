package me.flyray.bsin.blockchain.tds;

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 测试 AES 加密功能
 */
public class AesEncryptionTest {
    
    @Test
    public void testAesEncryptedKeyDataCreation() {
        // 创建连接器
        TrustedDataSpaceConnector connector = TrustedDataSpaceConnector.builder()
                .endpointUrl("https://test.example.com")
                .apiKey("test-api-key")
                .build();

        // 准备请求参数
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("name", "测试用户");
        requestMap.put("idNumber", "123456789012345678");
        requestMap.put("salt", "test-salt-123");

        // 创建DID档案
        Map<String, String> result = connector.createDidProfile(requestMap);

        // 验证返回结果
        assertNotNull("Result should not be null", result);
        assertEquals("success", result.get("status"));
        assertNotNull("DID should not be null", result.get("did"));
        assertNotNull("Key data JSON should not be null", result.get("didKeyData"));
        
        String did = result.get("did");
        String keyDataJson = result.get("didKeyData");
        String salt = "test-salt-123";
        
        System.out.println("=== AES 加密测试结果 ===");
        System.out.println("DID: " + did);
        System.out.println("加密的密钥数据包装JSON: " + keyDataJson);
        
        // 测试从包装对象解析公钥
        String publicKey = DefaultTrustedDataSpaceConnector.parsePublicKeyBase58FromJson(keyDataJson, did, salt);
        assertNotNull("Public key should not be null", publicKey);
        System.out.println("✓ 公钥解析成功: " + publicKey);
        
        // 测试从包装对象解析完整密钥信息
        DefaultTrustedDataSpaceConnector.KeyData keyData = DefaultTrustedDataSpaceConnector.parseKeyDataFromJson(keyDataJson, did, salt);
        assertNotNull("Parsed key data should not be null", keyData);
        assertEquals("DID should match", did, keyData.getDid());
        assertEquals("Public key should match", publicKey, keyData.getPublicKeyBase58());
        
        System.out.println("✓ 完整密钥信息解析成功:");
        System.out.println("  DID: " + keyData.getDid());
        System.out.println("  公钥: " + keyData.getPublicKeyBase58());
        System.out.println("  密钥ID: " + keyData.getKeyId());
    }
    
    @Test
    public void testSignAndVerify() {
        // 创建连接器
        DefaultTrustedDataSpaceConnector connector = 
            (DefaultTrustedDataSpaceConnector) TrustedDataSpaceConnector.builder()
                .endpointUrl("https://test.example.com")
                .build();

        // 准备请求参数
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("name", "签名测试用户");
        requestMap.put("idNumber", "987654321098765432");
        requestMap.put("salt", "sign-test-salt");

        // 创建DID档案
        Map<String, String> result = connector.createDidProfile(requestMap);
        String did = result.get("did");
        String salt = "sign-test-salt";
        String encryptedDidKeyData = result.get("didKeyData");
        String testData = "Hello, World!";

        System.out.println("=== 测试签名和验证 ===");
        System.out.println("DID: " + did);
        System.out.println("测试数据: " + testData);

        // 测试签名
        String signature = DefaultTrustedDataSpaceConnector.signData(did, salt, encryptedDidKeyData, testData);
        assertNotNull("Signature should not be null", signature);
        System.out.println("✓ 签名成功: " + signature.substring(0, 20) + "...");

        // 测试验证
        boolean verified = DefaultTrustedDataSpaceConnector.verifySign(did, salt, encryptedDidKeyData, testData, signature);
        assertTrue("Signature should be verified", verified);
        System.out.println("✓ 签名验证成功");

        // 测试错误数据的验证
        boolean verifiedFalse = DefaultTrustedDataSpaceConnector.verifySign(did, salt, encryptedDidKeyData, "Wrong data", signature);
        assertFalse("Wrong data should not be verified", verifiedFalse);
        System.out.println("✓ 错误数据验证失败（正确行为）");
    }
    
} 