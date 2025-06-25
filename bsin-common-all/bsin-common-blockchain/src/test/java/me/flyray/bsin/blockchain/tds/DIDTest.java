package me.flyray.bsin.blockchain.tds;

import foundation.identity.did.DIDDocument;
import foundation.identity.did.Service;
import foundation.identity.did.VerificationMethod;
import foundation.identity.jsonld.JsonLDDereferencer;
import me.flyray.bsin.blockchain.utils.DIDGeneratorUtil;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DIDTest {

    public static void main(String[] args) {
        try {
            // 示例1：生成基本的DID文档
            basicDIDGeneration();

            System.out.println("\n" + "=".repeat(50) + "\n");

            // 示例2：生成带服务端点的DID文档
            didWithServiceEndpoint();

            System.out.println("\n" + "=".repeat(50) + "\n");

            // 示例3：签名和验证示例
            signatureExample();

            System.out.println("\n" + "=".repeat(50) + "\n");

            // 示例4：自定义DID方法
            customDIDMethod();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 示例1：基本DID生成
     */
    public static void basicDIDGeneration() throws Exception {
        System.out.println("=== 基本DID生成示例 ===");

        // 生成随机DID文档
        DIDGeneratorUtil.DIDDocumentResult result = DIDGeneratorUtil.generateDIDDocument();

        // 打印详细信息
        result.printDetails();
    }

    /**
     * 示例2：带服务端点的DID生成
     */
    public static void didWithServiceEndpoint() throws Exception {
        System.out.println("=== 带服务端点的DID生成示例 ===");

        // 生成带服务端点的DID
        String serviceEndpoint = "https://example.com/didcomm";
        DIDGeneratorUtil.DIDDocumentResult result = DIDGeneratorUtil.generateDIDDocument(
                null, // 自动生成DID
                null, // 无控制器
                serviceEndpoint
        );

        result.printDetails();
    }

    /**
     * 示例3：数据签名和验证
     */
    public static void signatureExample() throws Exception {
        System.out.println("=== 数据签名和验证示例 ===");

        // 生成DID和密钥对
        DIDGeneratorUtil.DIDDocumentResult result = DIDGeneratorUtil.generateDIDDocument();
        KeyPair keyPair = result.getKeyPair();

        // 要签名的数据
        String message = "Hello, DID World!";
        byte[] data = message.getBytes(StandardCharsets.UTF_8);

        System.out.println("原始消息: " + message);
        System.out.println("DID: " + result.getDID());

        // 签名
        byte[] signature = DIDGeneratorUtil.signData(data, keyPair.getPrivate());
        System.out.println("签名长度: " + signature.length + " bytes");

        // 验证签名
        boolean isValid = DIDGeneratorUtil.verifySignature(data, signature, keyPair.getPublic());
        System.out.println("签名验证结果: " + (isValid ? "✓ 有效" : "✗ 无效"));

        // 测试错误数据的验证
        String wrongMessage = "Wrong message";
        byte[] wrongData = wrongMessage.getBytes(StandardCharsets.UTF_8);
        boolean isInvalid = DIDGeneratorUtil.verifySignature(wrongData, signature, keyPair.getPublic());
        System.out.println("错误数据验证结果: " + (isInvalid ? "✓ 有效" : "✗ 无效（预期）"));
    }

    /**
     * 示例4：自定义DID方法
     */
    public static void customDIDMethod() throws Exception {
        System.out.println("=== 自定义DID方法示例 ===");

        // 使用不同的DID方法
        String[] methods = {"key", "web", "btcr", "ethr"};

        for (String method : methods) {
            DIDGeneratorUtil.DIDDocumentResult result = DIDGeneratorUtil.generateCustomDID(
                    method,
                    "https://" + method + ".example.com/endpoint"
            );

            System.out.println("方法: " + method);
            System.out.println("DID: " + result.getDID());
            System.out.println("公钥: " + result.getPublicKeyBase58().substring(0, 20) + "...");
            System.out.println();
        }
    }

    /**
     * 示例5：从Base58恢复密钥并使用
     */
    public static void keyRecoveryExample() throws Exception {
        System.out.println("=== 密钥恢复和使用示例 ===");

        // 生成DID
        DIDGeneratorUtil.DIDDocumentResult result = DIDGeneratorUtil.generateDIDDocument();

        // 获取Base58编码的密钥
        String publicKeyBase58 = result.getPublicKeyBase58();
        String privateKeyBase58 = result.getPrivateKeyBase58();

        System.out.println("原始公钥 Base58: " + publicKeyBase58);

        // 从Base58恢复密钥
        var recoveredPublicKey = DIDGeneratorUtil.decodePublicKeyFromBase58(publicKeyBase58);
        var recoveredPrivateKey = DIDGeneratorUtil.decodePrivateKeyFromBase58(privateKeyBase58);

        // 使用恢复的密钥进行签名验证
        String testData = "Test data for key recovery";
        byte[] data = testData.getBytes(StandardCharsets.UTF_8);

        byte[] signature = DIDGeneratorUtil.signData(data, recoveredPrivateKey);
        boolean verified = DIDGeneratorUtil.verifySignature(data, signature, recoveredPublicKey);

        System.out.println("密钥恢复后签名验证: " + (verified ? "✓ 成功" : "✗ 失败"));
    }

}

