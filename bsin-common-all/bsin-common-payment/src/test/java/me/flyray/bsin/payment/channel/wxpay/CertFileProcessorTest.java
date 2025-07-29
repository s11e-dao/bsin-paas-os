package me.flyray.bsin.payment.channel.wxpay;

import me.flyray.bsin.payment.channel.wxpay.model.WxPayNormalMchParams;
import me.flyray.bsin.payment.channel.wxpay.util.CertFileProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 证书文件处理测试类
 * 
 * @author leonard
 * @date 2025-01-XX
 */
class CertFileProcessorTest {

    private CertFileProcessor certFileProcessor;
    private WxPayNormalMchParams testParams;

    @BeforeEach
    void setUp() {
        certFileProcessor = new CertFileProcessor();
        testParams = new WxPayNormalMchParams();
        testParams.setAppId("test_app_id");
        testParams.setMchId("test_mch_id");
        testParams.setKey("test_key");
        
        // 设置临时目录
        ReflectionTestUtils.setField(testParams, "tempCertDir", "/tmp/test/cert");
    }

    @Test
    void testProcessWxPayCertFiles_WithBase64Cert() {
        // 准备测试数据
        String base64Cert = Base64.getEncoder().encodeToString("test certificate content".getBytes());
        testParams.setCert(base64Cert);
        testParams.setApiClientCert(base64Cert);
        testParams.setApiClientKey(base64Cert);

        // 执行测试
        WxPayNormalMchParams result = certFileProcessor.processWxPayCertFiles(testParams);

        // 验证结果
        assertNotNull(result);
        assertNotEquals(base64Cert, result.getCert());
        assertNotEquals(base64Cert, result.getApiClientCert());
        assertNotEquals(base64Cert, result.getApiClientKey());
        
        // 验证文件路径格式
        assertTrue(result.getCert().contains("/tmp/test/cert/"));
        assertTrue(result.getCert().endsWith(".p12"));
        assertTrue(result.getApiClientCert().endsWith(".pem"));
        assertTrue(result.getApiClientKey().endsWith(".pem"));
    }

    @Test
    void testProcessWxPayCertFiles_WithFilePaths() {
        // 准备测试数据（文件路径）
        testParams.setCert("/path/to/cert.p12");
        testParams.setApiClientCert("/path/to/cert.pem");
        testParams.setApiClientKey("/path/to/key.pem");

        // 执行测试
        WxPayNormalMchParams result = certFileProcessor.processWxPayCertFiles(testParams);

        // 验证结果（文件路径应该保持不变）
        assertNotNull(result);
        assertEquals("/path/to/cert.p12", result.getCert());
        assertEquals("/path/to/cert.pem", result.getApiClientCert());
        assertEquals("/path/to/key.pem", result.getApiClientKey());
    }

    @Test
    void testProcessWxPayCertFiles_WithNullParams() {
        // 执行测试
        WxPayNormalMchParams result = certFileProcessor.processWxPayCertFiles(null);

        // 验证结果
        assertNull(result);
    }

    @Test
    void testGetCertFilePath() {
        // 准备测试数据
        testParams.setCert("/path/to/cert.p12");
        testParams.setApiClientCert("/path/to/cert.pem");
        testParams.setApiClientKey("/path/to/key.pem");

        // 执行测试
        String certPath = certFileProcessor.getCertFilePath(testParams, "cert");
        String apiClientCertPath = certFileProcessor.getCertFilePath(testParams, "apiClientCert");
        String apiClientKeyPath = certFileProcessor.getCertFilePath(testParams, "apiClientKey");

        // 验证结果
        assertEquals("/path/to/cert.p12", certPath);
        assertEquals("/path/to/cert.pem", apiClientCertPath);
        assertEquals("/path/to/key.pem", apiClientKeyPath);
    }

    @Test
    void testGetCertFilePath_WithInvalidType() {
        // 执行测试
        String result = certFileProcessor.getCertFilePath(testParams, "invalid");

        // 验证结果
        assertNull(result);
    }

    @Test
    void testIsCertFileExists() {
        // 测试存在的文件
        assertTrue(certFileProcessor.isCertFileExists("/tmp"));
        
        // 测试不存在的文件
        assertFalse(certFileProcessor.isCertFileExists("/nonexistent/file"));
        
        // 测试空路径
        assertFalse(certFileProcessor.isCertFileExists(null));
        assertFalse(certFileProcessor.isCertFileExists(""));
    }

    @Test
    void testValidateCertFile() {
        // 测试有效的证书文件（使用临时目录）
        String tempDir = System.getProperty("java.io.tmpdir");
        String testFile = tempDir + "/test_cert.pem";
        
        try {
            // 创建测试文件
            java.io.File file = new java.io.File(testFile);
            file.getParentFile().mkdirs();
            file.createNewFile();
            
            // 写入一些内容
            java.nio.file.Files.write(file.toPath(), "test certificate content".getBytes());
            
            // 执行测试
            boolean result = certFileProcessor.validateCertFile(testFile);
            
            // 验证结果
            assertTrue(result);
            
        } catch (Exception e) {
            fail("测试过程中发生异常: " + e.getMessage());
        } finally {
            // 清理测试文件
            new java.io.File(testFile).delete();
        }
    }

    @Test
    void testValidateCertFile_WithInvalidFile() {
        // 测试不存在的文件
        assertFalse(certFileProcessor.validateCertFile("/nonexistent/cert.pem"));
        
        // 测试空路径
        assertFalse(certFileProcessor.validateCertFile(null));
        assertFalse(certFileProcessor.validateCertFile(""));
    }

    @Test
    void testGetCertFileInfo() {
        // 准备测试数据
        String tempDir = System.getProperty("java.io.tmpdir");
        String testFile = tempDir + "/test_cert_info.pem";
        
        try {
            // 创建测试文件
            java.io.File file = new java.io.File(testFile);
            file.getParentFile().mkdirs();
            file.createNewFile();
            
            // 写入一些内容
            java.nio.file.Files.write(file.toPath(), "test certificate content".getBytes());
            
            // 执行测试
            CertFileProcessor.CertFileInfo info = certFileProcessor.getCertFileInfo(testFile);
            
            // 验证结果
            assertNotNull(info);
            assertEquals("test_cert_info.pem", info.getName());
            assertEquals(testFile, info.getPath());
            assertTrue(info.getSize() > 0);
            assertTrue(info.isCanRead());
            
        } catch (Exception e) {
            fail("测试过程中发生异常: " + e.getMessage());
        } finally {
            // 清理测试文件
            new java.io.File(testFile).delete();
        }
    }

    @Test
    void testGetCertFileInfo_WithInvalidFile() {
        // 测试不存在的文件
        assertNull(certFileProcessor.getCertFileInfo("/nonexistent/cert.pem"));
        
        // 测试空路径
        assertNull(certFileProcessor.getCertFileInfo(null));
        assertNull(certFileProcessor.getCertFileInfo(""));
    }

    @Test
    void testCleanupAllCertFiles() {
        // 执行测试
        certFileProcessor.cleanupAllCertFiles();
        
        // 验证方法能够正常执行，不抛出异常
        assertTrue(true);
    }

    @Test
    void testBase64ContentDetection() {
        // 测试Base64内容检测
        String base64Content = Base64.getEncoder().encodeToString("test".getBytes());
        String filePath = "/path/to/cert.pem";
        String emptyContent = "";
        
        // 使用反射调用私有方法进行测试
        WxPayNormalMchParams params = new WxPayNormalMchParams();
        ReflectionTestUtils.setField(params, "tempCertDir", "/tmp/test/cert");
        
        // 设置Base64内容
        params.setCert(base64Content);
        params.setApiClientCert(filePath);
        params.setApiClientKey(emptyContent);
        
        // 处理证书文件
        params.processBase64CertFiles();
        
        // 验证结果
        assertNotEquals(base64Content, params.getCert()); // Base64内容应该被转换为文件路径
        assertEquals(filePath, params.getApiClientCert()); // 文件路径应该保持不变
        assertEquals(emptyContent, params.getApiClientKey()); // 空内容应该保持不变
    }
}