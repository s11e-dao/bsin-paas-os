package me.flyray.bsin.payment.channel.wxpay;

import me.flyray.bsin.payment.channel.wxpay.model.NormalMchParams;
import me.flyray.bsin.payment.channel.wxpay.model.WxPayNormalMchParams;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NormalMchParams工厂方法测试类
 * 测试factory方法自动处理证书文件的功能
 * 
 * @author leonard
 * @date 2025-01-XX
 */
class NormalMchParamsFactoryTest {

    @Test
    void testFactory_WithWxPayAndBase64Cert() {
        // 准备测试数据
        String base64Cert = Base64.getEncoder().encodeToString("test certificate content".getBytes());
        
        String paramsStr = String.format(
            "{\"appId\":\"test_app_id\",\"mchId\":\"test_mch_id\",\"key\":\"test_key\"," +
            "\"cert\":\"%s\",\"apiClientCert\":\"%s\",\"apiClientKey\":\"%s\"}",
            base64Cert, base64Cert, base64Cert
        );

        // 执行测试
        NormalMchParams result = NormalMchParams.factory("wxpay", paramsStr);

        // 验证结果
        assertNotNull(result);
        assertTrue(result instanceof WxPayNormalMchParams);
        
        WxPayNormalMchParams wxPayParams = (WxPayNormalMchParams) result;
        
        // 验证证书文件已被处理（不再是Base64内容）
        assertNotEquals(base64Cert, wxPayParams.getCert());
        assertNotEquals(base64Cert, wxPayParams.getApiClientCert());
        assertNotEquals(base64Cert, wxPayParams.getApiClientKey());
        
        // 验证文件路径格式
        assertTrue(wxPayParams.getCert().contains("/tmp/bsin/cert/"));
        assertTrue(wxPayParams.getCert().endsWith(".p12"));
        assertTrue(wxPayParams.getApiClientCert().endsWith(".pem"));
        assertTrue(wxPayParams.getApiClientKey().endsWith(".pem"));
    }

    @Test
    void testFactory_WithWxPayAndFilePaths() {
        // 准备测试数据（文件路径）
        String paramsStr = "{\"appId\":\"test_app_id\",\"mchId\":\"test_mch_id\",\"key\":\"test_key\"," +
                          "\"cert\":\"/path/to/cert.p12\",\"apiClientCert\":\"/path/to/cert.pem\",\"apiClientKey\":\"/path/to/key.pem\"}";

        // 执行测试
        NormalMchParams result = NormalMchParams.factory("wxpay", paramsStr);

        // 验证结果
        assertNotNull(result);
        assertTrue(result instanceof WxPayNormalMchParams);
        
        WxPayNormalMchParams wxPayParams = (WxPayNormalMchParams) result;
        
        // 验证文件路径保持不变
        assertEquals("/path/to/cert.p12", wxPayParams.getCert());
        assertEquals("/path/to/cert.pem", wxPayParams.getApiClientCert());
        assertEquals("/path/to/key.pem", wxPayParams.getApiClientKey());
    }

    @Test
    void testFactory_WithWxPayAndMixedContent() {
        // 准备测试数据（混合内容：Base64 + 文件路径）
        String base64Cert = Base64.getEncoder().encodeToString("test certificate".getBytes());
        
        String paramsStr = String.format(
            "{\"appId\":\"test_app_id\",\"mchId\":\"test_mch_id\",\"key\":\"test_key\"," +
            "\"cert\":\"%s\",\"apiClientCert\":\"/path/to/cert.pem\",\"apiClientKey\":\"%s\"}",
            base64Cert, base64Cert
        );

        // 执行测试
        NormalMchParams result = NormalMchParams.factory("wxpay", paramsStr);

        // 验证结果
        assertNotNull(result);
        assertTrue(result instanceof WxPayNormalMchParams);
        
        WxPayNormalMchParams wxPayParams = (WxPayNormalMchParams) result;
        
        // 验证Base64内容被转换
        assertNotEquals(base64Cert, wxPayParams.getCert());
        assertNotEquals(base64Cert, wxPayParams.getApiClientKey());
        
        // 验证文件路径保持不变
        assertEquals("/path/to/cert.pem", wxPayParams.getApiClientCert());
        
        // 验证文件路径格式
        assertTrue(wxPayParams.getCert().contains("/tmp/bsin/cert/"));
        assertTrue(wxPayParams.getApiClientKey().contains("/tmp/bsin/cert/"));
    }

    @Test
    void testFactory_WithInvalidIfCode() {
        // 准备测试数据
        String paramsStr = "{\"appId\":\"test_app_id\",\"mchId\":\"test_mch_id\"}";

        // 执行测试
        NormalMchParams result = NormalMchParams.factory("invalid", paramsStr);

        // 验证结果
        assertNull(result);
    }

    @Test
    void testFactory_WithNullParams() {
        // 执行测试
        NormalMchParams result = NormalMchParams.factory("wxpay", null);

        // 验证结果
        assertNull(result);
    }

    @Test
    void testFactory_WithEmptyParams() {
        // 执行测试
        NormalMchParams result = NormalMchParams.factory("wxpay", "");

        // 验证结果
        assertNull(result);
    }

    @Test
    void testFactory_WithInvalidJson() {
        // 准备测试数据（无效JSON）
        String paramsStr = "invalid json content";

        // 执行测试
        NormalMchParams result = NormalMchParams.factory("wxpay", paramsStr);

        // 验证结果
        assertNull(result);
    }

    @Test
    void testWxPayNormalMchParamsConstructor_WithBase64Cert() {
        // 准备测试数据
        String base64Cert = Base64.getEncoder().encodeToString("test certificate content".getBytes());
        
        String paramsStr = String.format(
            "{\"appId\":\"test_app_id\",\"mchId\":\"test_mch_id\",\"key\":\"test_key\"," +
            "\"cert\":\"%s\",\"apiClientCert\":\"%s\",\"apiClientKey\":\"%s\"}",
            base64Cert, base64Cert, base64Cert
        );

        // 执行测试
        WxPayNormalMchParams result = new WxPayNormalMchParams(paramsStr);

        // 验证结果
        assertNotNull(result);
        assertEquals("test_app_id", result.getAppId());
        assertEquals("test_mch_id", result.getMchId());
        assertEquals("test_key", result.getKey());
        
        // 验证证书文件已被处理
        assertNotEquals(base64Cert, result.getCert());
        assertNotEquals(base64Cert, result.getApiClientCert());
        assertNotEquals(base64Cert, result.getApiClientKey());
        
        // 验证文件路径格式
        assertTrue(result.getCert().contains("/tmp/bsin/cert/"));
        assertTrue(result.getCert().endsWith(".p12"));
        assertTrue(result.getApiClientCert().endsWith(".pem"));
        assertTrue(result.getApiClientKey().endsWith(".pem"));
    }

    @Test
    void testWxPayNormalMchParamsConstructor_WithNullParams() {
        // 执行测试
        WxPayNormalMchParams result = new WxPayNormalMchParams(null);

        // 验证结果
        assertNotNull(result);
        // 所有属性应该为null或默认值
        assertNull(result.getAppId());
        assertNull(result.getMchId());
        assertNull(result.getKey());
    }

    @Test
    void testWxPayNormalMchParamsConstructor_WithEmptyParams() {
        // 执行测试
        WxPayNormalMchParams result = new WxPayNormalMchParams("");

        // 验证结果
        assertNotNull(result);
        // 所有属性应该为null或默认值
        assertNull(result.getAppId());
        assertNull(result.getMchId());
        assertNull(result.getKey());
    }

    @Test
    void testWxPayNormalMchParamsConstructor_WithInvalidJson() {
        // 准备测试数据（无效JSON）
        String paramsStr = "invalid json content";

        // 执行测试
        WxPayNormalMchParams result = new WxPayNormalMchParams(paramsStr);

        // 验证结果
        assertNotNull(result);
        // 解析失败时，所有属性应该为null或默认值
        assertNull(result.getAppId());
        assertNull(result.getMchId());
        assertNull(result.getKey());
    }

    @Test
    void testBase64Detection() {
        // 测试Base64检测功能
        String base64Content = Base64.getEncoder().encodeToString("test".getBytes());
        String filePath = "/path/to/cert.pem";
        String emptyContent = "";
        String nullContent = null;
        
        // 创建WxPayNormalMchParams实例
        WxPayNormalMchParams params = new WxPayNormalMchParams();
        
        // 设置临时目录
        ReflectionTestUtils.setField(params, "tempCertDir", "/tmp/test/cert");
        
        // 设置不同内容
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