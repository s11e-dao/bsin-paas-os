package me.flyray.bsin.blockchain.tds;

import java.util.HashMap;
import java.util.Map;

/**
 * 演示如何从keyDataJson中解析publicKeyBase58和privateKeyBase58的示例
 */
public class KeyDataParseExample {
    
    public static void main(String[] args) {
        try {
            // 创建连接器
            TrustedDataSpaceConnector connector = TrustedDataSpaceConnector.builder()
                    .endpointUrl("https://example.com")
                    .build();

            // 准备用户数据
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("name", "王五");
            requestMap.put("idNumber", "123456789012345678");

            // 创建DID档案
            Map<String, String> result = connector.createDidProfile(requestMap);
            String keyDataJson = result.get("keyDataJson");

            System.out.println("=== KeyDataJson 解析示例 ===\n");
            System.out.println("KeyDataJson: " + keyDataJson + "\n");

            // 方法1: 只解析公钥（最快，不需要解密）
            String publicKey = DefaultTrustedDataSpaceConnector.parsePublicKeyBase58FromJson(keyDataJson);
            System.out.println("方法1 - 解析公钥:");
            System.out.println("  publicKeyBase58: " + publicKey + "\n");

            // 方法2: 只解析私钥（需要解密）
            String privateKey = DefaultTrustedDataSpaceConnector.parsePrivateKeyBase58FromJson(keyDataJson);
            System.out.println("方法2 - 解析私钥:");
            System.out.println("  privateKeyBase58: " + privateKey + "\n");

            // 方法3: 解析完整信息（推荐使用）
            Map<String, String> parsedData = DefaultTrustedDataSpaceConnector.parseKeyDataFromJson(keyDataJson);
            System.out.println("方法3 - 解析完整信息:");
            System.out.println("  状态: " + parsedData.get("status"));
            System.out.println("  DID: " + parsedData.get("did"));
            System.out.println("  公钥: " + parsedData.get("publicKeyBase58"));
            System.out.println("  私钥: " + parsedData.get("privateKeyBase58"));
            System.out.println("  密钥ID: " + parsedData.get("keyId"));
            System.out.println("  创建时间: " + parsedData.get("createTime"));
            System.out.println("  加密私钥: " + parsedData.get("privateKeyBase58Encrypted"));
            System.out.println("  加密盐值: " + parsedData.get("keySalt"));

            System.out.println("\n=== 验证解析结果 ===");
            System.out.println("原始公钥与解析公钥一致: " + result.get("publicKeyBase58").equals(publicKey));
            System.out.println("原始私钥与解析私钥一致: " + result.get("privateKeyBase58").equals(privateKey));

        } catch (Exception e) {
            System.err.println("示例执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 