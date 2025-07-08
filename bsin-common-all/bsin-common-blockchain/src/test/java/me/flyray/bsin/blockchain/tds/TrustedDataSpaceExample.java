package me.flyray.bsin.blockchain.tds;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 可信数据空间连接器使用示例
 * 演示DID创建、签名和验证的完整流程
 */
public class TrustedDataSpaceExample {

    public static void main(String[] args) {
        try {
            // 创建连接器
            TrustedDataSpaceConnector connector = TrustedDataSpaceConnector.builder()
                    .endpointUrl("https://s11e.network")
                    .apiKey("demo-api-key")
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(45, TimeUnit.SECONDS)
                    .maxRetries(5)
                    .addCustomHeader("X-Demo-Version", "1.0.0")
                    .build();

            System.out.println("======================================");
            System.out.println("    可信数据空间DID系统演示");
            System.out.println("======================================\n");

            // 示例1：创建个人DID
            demonstratePersonalDID(connector);
            
            System.out.println("\n" + "=".repeat(50) + "\n");
            
            // 示例2：创建企业DID
            demonstrateEnterpriseDID(connector);
            
            System.out.println("\n" + "=".repeat(50) + "\n");
            
            // 示例3：签名和验证示例
            demonstrateSignatureWorkflow(connector);

        } catch (Exception e) {
            System.err.println("演示过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 演示个人DID创建
     */
    private static void demonstratePersonalDID(TrustedDataSpaceConnector connector) {
        System.out.println("=== 个人DID创建演示 ===");
        
        Map<String, Object> personalRequest = new HashMap<>();
        personalRequest.put("name", "张小明");
        personalRequest.put("idNumber", "110101199001010001");
        
        Map<String, String> result = connector.createDidProfile(personalRequest);
        
        if ("success".equals(result.get("status"))) {
            System.out.println("✓ 个人DID创建成功");
            System.out.println("  DID: " + result.get("did"));
            System.out.println("  用户: " + result.get("userName"));
            System.out.println("  身份证: " + result.get("userIdNumber"));
            System.out.println("  公钥: " + result.get("publicKeyBase58").substring(0, 20) + "...");
            System.out.println("  服务端点: " + result.get("serviceEndpoint"));
        } else {
            System.out.println("✗ 个人DID创建失败: " + result.get("message"));
        }
    }

    /**
     * 演示企业DID创建
     */
    private static void demonstrateEnterpriseDID(TrustedDataSpaceConnector connector) {
        System.out.println("=== 企业DID创建演示 ===");
        
        Map<String, Object> enterpriseRequest = new HashMap<>();
        enterpriseRequest.put("name", "北京科技有限公司");
        enterpriseRequest.put("idNumber", "91110000123456789X");
        
        Map<String, String> result = connector.createDidProfile(enterpriseRequest);
        
        if ("success".equals(result.get("status"))) {
            System.out.println("✓ 企业DID创建成功");
            System.out.println("  DID: " + result.get("did"));
            System.out.println("  企业: " + result.get("userName"));
            System.out.println("  统一社会信用代码: " + result.get("userIdNumber"));
            System.out.println("  公钥: " + result.get("publicKeyBase58").substring(0, 20) + "...");
            System.out.println("  服务端点: " + result.get("serviceEndpoint"));
        } else {
            System.out.println("✗ 企业DID创建失败: " + result.get("message"));
        }
    }

    /**
     * 演示签名和验证工作流
     */
    private static void demonstrateSignatureWorkflow(TrustedDataSpaceConnector connector) throws Exception {
        System.out.println("=== 数据签名验证演示 ===");
        
        // 1. 创建用于签名的DID
        Map<String, Object> signerRequest = new HashMap<>();
        signerRequest.put("name", "数据签名者");
        signerRequest.put("idNumber", "110101199002020002");
        
        Map<String, String> didResult = connector.createDidProfile(signerRequest);
        String did = didResult.get("did");
        
        System.out.println("1. 创建签名者DID: " + did);
        
        // 2. 准备要签名的数据
        String[] testDataSet = {
            "重要合同内容需要签名确认",
            "用户身份认证信息",
            "区块链交易数据",
            "敏感的商业机密文件"
        };
        
        DefaultTrustedDataSpaceConnector defaultConnector = (DefaultTrustedDataSpaceConnector) connector;
        
        System.out.println("\n2. 数据签名和验证过程:");
        
        for (int i = 0; i < testDataSet.length; i++) {
            String data = testDataSet[i];
            System.out.println("\n  测试数据 " + (i + 1) + ": " + data);
            
            try {
                // 使用反射调用私有签名方法
                java.lang.reflect.Method signMethod = DefaultTrustedDataSpaceConnector.class
                        .getDeclaredMethod("signData", String.class, String.class);
                signMethod.setAccessible(true);
                String signature = (String) signMethod.invoke(defaultConnector, did, data);
                
                if (signature != null) {
                    System.out.println("    ✓ 签名成功: " + signature.substring(0, 32) + "...");
                    
                    // 验证签名
                    java.lang.reflect.Method verifyMethod = DefaultTrustedDataSpaceConnector.class
                            .getDeclaredMethod("verifySignature", String.class, String.class, String.class);
                    verifyMethod.setAccessible(true);
                    boolean isValid = (Boolean) verifyMethod.invoke(defaultConnector, did, data, signature);
                    
                    if (isValid) {
                        System.out.println("    ✓ 签名验证成功");
                    } else {
                        System.out.println("    ✗ 签名验证失败");
                    }
                    
                    // 测试篡改检测
                    String tamperedData = data + " [已篡改]";
                    boolean isTampered = (Boolean) verifyMethod.invoke(defaultConnector, did, tamperedData, signature);
                    
                    if (!isTampered) {
                        System.out.println("    ✓ 篡改检测成功（验证失败）");
                    } else {
                        System.out.println("    ✗ 篡改检测失败（应该验证失败）");
                    }
                    
                } else {
                    System.out.println("    ✗ 签名失败");
                }
                
            } catch (Exception e) {
                System.out.println("    ✗ 处理过程中发生错误: " + e.getMessage());
            }
        }
        
        // 3. 统计信息
        System.out.println("\n3. 系统状态:");
        System.out.println("  - DID是否存在: " + (defaultConnector.didExists(did) ? "✓" : "✗"));
        System.out.println("  - 公钥可获取: " + (defaultConnector.getPublicKeyBase58(did) != null ? "✓" : "✗"));
        
        System.out.println("\n✓ 签名验证演示完成");
    }
} 