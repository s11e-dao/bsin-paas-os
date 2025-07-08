package me.flyray.bsin.blockchain.tds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * DefaultTrustedDataSpaceConnector 单元测试
 * 测试DID创建、签名和验证功能
 */
public class DefaultTrustedDataSpaceConnectorTest {

    private TrustedDataSpaceConnector connector;
    private DefaultTrustedDataSpaceConnector defaultConnector;

    @BeforeEach
    public void setUp() {
        // 创建连接器实例
        connector = TrustedDataSpaceConnector.builder()
                .endpointUrl("https://test.s11e.network")
                .apiKey("test-api-key")
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .maxRetries(3)
                .addCustomHeader("X-Test-Header", "test-value")
                .build();
        
        // 获取默认实现用于访问私有方法
        defaultConnector = (DefaultTrustedDataSpaceConnector) connector;
    }

    @Test
    public void testCreateDidProfile_Success() {
        // 准备测试数据
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("name", "张三");
        requestMap.put("idNumber", "110101199001010001");

        // 执行测试
        Map<String, String> result = connector.createDidProfile(requestMap);

        // 验证结果
        assertNotNull(result, "结果不应为空");
        assertEquals("success", result.get("status"), "状态应为成功");
        assertNotNull(result.get("did"), "DID不应为空");
        assertNotNull(result.get("didDocument"), "DID文档不应为空");
        assertNotNull(result.get("publicKeyBase58"), "公钥不应为空");
        assertNotNull(result.get("privateKeyBase58"), "私钥不应为空");
        assertNotNull(result.get("serviceEndpoint"), "服务端点不应为空");
        assertEquals("张三", result.get("userName"), "用户名应正确");
        assertEquals("110101199001010001", result.get("userIdNumber"), "身份证号应正确");

        // 验证DID格式
        String did = result.get("did");
        assertTrue(did.startsWith("did:s11e:"), "DID应以did:s11e:开头");
        
        // 验证DID是否被正确存储
        assertTrue(defaultConnector.didExists(did), "DID应被存储");
        assertNotNull(defaultConnector.getPublicKeyBase58(did), "应能获取公钥");

        System.out.println("=== 创建DID测试通过 ===");
        System.out.println("DID: " + did);
        System.out.println("Public Key: " + result.get("publicKeyBase58"));
        System.out.println("Service Endpoint: " + result.get("serviceEndpoint"));
    }

    @Test
    public void testCreateDidProfile_ConsistentGeneration() {
        // 测试相同用户信息生成相同DID
        Map<String, Object> requestMap1 = new HashMap<>();
        requestMap1.put("name", "李四");
        requestMap1.put("idNumber", "110101199002020002");

        Map<String, Object> requestMap2 = new HashMap<>();
        requestMap2.put("name", "李四");
        requestMap2.put("idNumber", "110101199002020002");

        // 执行测试
        Map<String, String> result1 = connector.createDidProfile(requestMap1);
        Map<String, String> result2 = connector.createDidProfile(requestMap2);

        // 验证DID一致性（基于相同的哈希应该生成相同的标识符）
        String did1 = result1.get("did");
        String did2 = result2.get("did");
        assertEquals(did1, did2, "相同用户信息应生成相同DID");

        System.out.println("=== DID一致性测试通过 ===");
        System.out.println("DID1: " + did1);
        System.out.println("DID2: " + did2);
    }

    @Test
    public void testSignDataAndVerifySignature_Success() throws Exception {
        // 先创建DID
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("name", "王五");
        requestMap.put("idNumber", "110101199003030003");

        Map<String, String> didResult = connector.createDidProfile(requestMap);
        String did = didResult.get("did");

        // 准备测试数据
        String testData = "这是一条需要签名的测试数据";

        // 使用反射访问私有方法进行签名
        java.lang.reflect.Method signMethod = DefaultTrustedDataSpaceConnector.class
                .getDeclaredMethod("signData", String.class, String.class);
        signMethod.setAccessible(true);
        String signature = (String) signMethod.invoke(defaultConnector, did, testData);

        // 验证签名不为空
        assertNotNull(signature, "签名不应为空");
        assertFalse(signature.isEmpty(), "签名不应为空字符串");

        // 使用反射访问私有方法进行验证
        java.lang.reflect.Method verifyMethod = DefaultTrustedDataSpaceConnector.class
                .getDeclaredMethod("verifySign", String.class, String.class, String.class);
        verifyMethod.setAccessible(true);
        boolean verifyResult = (Boolean) verifyMethod.invoke(defaultConnector, did, testData, signature);

        // 验证结果
        assertTrue(verifyResult, "签名验证应成功");

        System.out.println("=== 签名和验证测试通过 ===");
        System.out.println("DID: " + did);
        System.out.println("原始数据: " + testData);
        System.out.println("签名: " + signature);
        System.out.println("验证结果: " + verifyResult);
    }

    @Test
    public void testVerifySignature_InvalidSignature() throws Exception {
        // 先创建DID
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("name", "赵六");
        requestMap.put("idNumber", "110101199004040004");

        Map<String, String> didResult = connector.createDidProfile(requestMap);
        String did = didResult.get("did");

        // 准备测试数据
        String testData = "原始数据";
        String wrongData = "错误数据";

        // 使用反射访问私有方法进行签名
        java.lang.reflect.Method signMethod = DefaultTrustedDataSpaceConnector.class
                .getDeclaredMethod("signData", String.class, String.class);
        signMethod.setAccessible(true);
        String signature = (String) signMethod.invoke(defaultConnector, did, testData);

        // 使用错误数据进行验证
        java.lang.reflect.Method verifyMethod = DefaultTrustedDataSpaceConnector.class
                .getDeclaredMethod("verifySignature", String.class, String.class, String.class);
        verifyMethod.setAccessible(true);
        boolean verifyResult = (Boolean) verifyMethod.invoke(defaultConnector, did, wrongData, signature);

        // 验证应失败
        assertFalse(verifyResult, "使用错误数据验证签名应失败");

        System.out.println("=== 错误签名验证测试通过 ===");
        System.out.println("原始数据: " + testData);
        System.out.println("错误数据: " + wrongData);
        System.out.println("验证结果: " + verifyResult);
    }

    @Test
    public void testSignData_NonExistentDID() throws Exception {
        // 使用不存在的DID进行签名
        String nonExistentDID = "did:s11e:nonexistent123456";
        String testData = "测试数据";

        // 使用反射访问私有方法
        java.lang.reflect.Method signMethod = DefaultTrustedDataSpaceConnector.class
                .getDeclaredMethod("signData", String.class, String.class);
        signMethod.setAccessible(true);
        String signature = (String) signMethod.invoke(defaultConnector, nonExistentDID, testData);

        // 应返回null
        assertNull(signature, "不存在的DID应返回null签名");

        System.out.println("=== 不存在DID签名测试通过 ===");
    }

    @Test
    public void testVerifySignature_NonExistentDID() throws Exception {
        // 使用不存在的DID进行验证
        String nonExistentDID = "did:s11e:nonexistent123456";
        String testData = "测试数据";
        String fakeSignature = "fake-signature";

        // 使用反射访问私有方法
        java.lang.reflect.Method verifyMethod = DefaultTrustedDataSpaceConnector.class
                .getDeclaredMethod("verifySignature", String.class, String.class, String.class);
        verifyMethod.setAccessible(true);
        boolean verifyResult = (Boolean) verifyMethod.invoke(defaultConnector, nonExistentDID, testData, fakeSignature);

        // 应返回false
        assertFalse(verifyResult, "不存在的DID验证应失败");

        System.out.println("=== 不存在DID验证测试通过 ===");
    }

    @Test
    public void testDidExists() {
        // 先创建DID
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("name", "测试用户");
        requestMap.put("idNumber", "110101199005050005");

        Map<String, String> didResult = connector.createDidProfile(requestMap);
        String did = didResult.get("did");

        // 测试DID存在
        assertTrue(defaultConnector.didExists(did), "DID应存在");

        // 测试不存在的DID
        assertFalse(defaultConnector.didExists("did:s11e:nonexistent"), "不存在的DID应返回false");

        System.out.println("=== DID存在性测试通过 ===");
    }

    @Test
    public void testGetPublicKeyBase58() {
        // 先创建DID
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("name", "公钥测试");
        requestMap.put("idNumber", "110101199006060006");

        Map<String, String> didResult = connector.createDidProfile(requestMap);
        String did = didResult.get("did");
        String originalPublicKey = didResult.get("publicKeyBase58");

        // 测试获取公钥
        String retrievedPublicKey = defaultConnector.getPublicKeyBase58(did);
        assertNotNull(retrievedPublicKey, "应能获取公钥");
        assertEquals(originalPublicKey, retrievedPublicKey, "获取的公钥应与原始公钥一致");

        // 测试不存在的DID
        assertNull(defaultConnector.getPublicKeyBase58("did:s11e:nonexistent"), "不存在的DID应返回null");

        System.out.println("=== 公钥获取测试通过 ===");
        System.out.println("原始公钥: " + originalPublicKey);
        System.out.println("获取公钥: " + retrievedPublicKey);
    }

    @Test
    public void testCompleteWorkflow() throws Exception {
        System.out.println("\n=== 完整工作流测试开始 ===");
        
        // 1. 创建DID
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("name", "工作流测试用户");
        requestMap.put("idNumber", "110101199007070007");

        Map<String, String> didResult = connector.createDidProfile(requestMap);
        String did = didResult.get("did");
        
        System.out.println("1. 创建DID: " + did);

        // 2. 验证DID存在
        assertTrue(defaultConnector.didExists(did));
        System.out.println("2. 验证DID存在: ✓");

        // 3. 签名数据
        String originalData = "重要的业务数据需要签名保护";
        java.lang.reflect.Method signMethod = DefaultTrustedDataSpaceConnector.class
                .getDeclaredMethod("signData", String.class, String.class);
        signMethod.setAccessible(true);
        String signature = (String) signMethod.invoke(defaultConnector, did, originalData);
        
        assertNotNull(signature);
        System.out.println("3. 数据签名: " + signature.substring(0, 20) + "...");

        // 4. 验证签名
        java.lang.reflect.Method verifyMethod = DefaultTrustedDataSpaceConnector.class
                .getDeclaredMethod("verifySignature", String.class, String.class, String.class);
        verifyMethod.setAccessible(true);
        boolean isValid = (Boolean) verifyMethod.invoke(defaultConnector, did, originalData, signature);
        
        assertTrue(isValid);
        System.out.println("4. 签名验证: ✓");

        // 5. 测试篡改检测
        boolean isTampered = (Boolean) verifyMethod.invoke(defaultConnector, did, "篡改后的数据", signature);
        assertFalse(isTampered);
        System.out.println("5. 篡改检测: ✓");

        System.out.println("=== 完整工作流测试通过 ===\n");
    }
} 