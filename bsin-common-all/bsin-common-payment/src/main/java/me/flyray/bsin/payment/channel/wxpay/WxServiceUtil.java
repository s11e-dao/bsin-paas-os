package me.flyray.bsin.payment.channel.wxpay;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.flyray.bsin.payment.enums.PayInterfaceVersion;
import me.flyray.bsin.payment.channel.wxpay.model.WxPayIsvParams;
import me.flyray.bsin.payment.channel.wxpay.model.WxPayNormalMchParams;
import me.flyray.bsin.payment.utils.ChannelCertResourceUtil;
import me.flyray.bsin.utils.SpringUtils;
import org.apache.commons.lang3.StringUtils;

/*
 * wxService 包装类
 *
 */
@Data
@AllArgsConstructor
@Slf4j
public class WxServiceUtil {

  /** 缓存微信API版本 * */
  private String apiVersion;

  /** 缓存 wxPayService 对象 * */
  private WxPayService wxPayService;

  /** 缓存 wxJavaService 对象 * */
  private WxMpService wxMpService;

  /** 微信回调地址 * */
  private String notifyUrl;

  /** 微信支付渠道配置编号 * */
  private String payChannelConfigNo;

  public static WxServiceUtil buildWxServiceWrapper(
      String mchId,
      String appId,
      String appSecret,
      String mchKey,
      String apiVersion,
      String apiV3Key,
      String serialNo,
      String cert,
      String apiClientCert,
      String apiClientKey,
      String notifyUrl,
      String payChannelConfigNo) {

    WxPayConfig wxPayConfig = new WxPayConfig();
    wxPayConfig.setMchId(mchId);
    wxPayConfig.setAppId(appId);
    wxPayConfig.setMchKey(mchKey);

    // 微信条码付、扫码付 只支付V2版本
    if (PayInterfaceVersion.WX_V2.equals(apiVersion)) {
      wxPayConfig.setSignType(WxPayConstants.SignType.MD5);
    }
    ChannelCertResourceUtil channelCertConfigKitBean =
        SpringUtils.getBean(ChannelCertResourceUtil.class);

    if (StringUtils.isNotBlank(apiV3Key)) {
      log.info("设置API V3密钥，长度: {}", apiV3Key.length());
      wxPayConfig.setApiV3Key(apiV3Key);
      log.info("API V3密钥设置成功");
    } else {
      log.warn("API V3密钥为空");
    }
    if (StringUtils.isNotBlank(serialNo)) {
      wxPayConfig.setCertSerialNo(serialNo);
    }
    // 优先使用 pem 格式的证书文件（推荐方式）
    if (StringUtils.isNotBlank(apiClientCert)) {
      wxPayConfig.setPrivateCertPath(channelCertConfigKitBean.getCertFilePath(apiClientCert));
      log.info("设置证书文件路径: {}", channelCertConfigKitBean.getCertFilePath(apiClientCert));
    }
    if (StringUtils.isNotBlank(apiClientKey)) {
      wxPayConfig.setPrivateKeyPath(channelCertConfigKitBean.getCertFilePath(apiClientKey));
      log.info("设置私钥文件路径: {}", channelCertConfigKitBean.getCertFilePath(apiClientKey));
    }
    // 如果只有 p12 格式证书，则使用 p12 格式（需要密码）
    if (StringUtils.isNotBlank(cert) && StringUtils.isBlank(apiClientCert)) {
      wxPayConfig.setKeyPath(channelCertConfigKitBean.getCertFilePath(cert));
      log.info("设置p12证书路径: {}", channelCertConfigKitBean.getCertFilePath(cert));
      log.warn("使用p12格式证书，请确保证书密码正确（默认为商户号）");
    }
    wxPayConfig.setNotifyUrl(notifyUrl);

    // 检查是否为服务商模式（通过商户号判断）
    // 如果商户号以特定前缀开头，可能是服务商模式
    if (mchId != null
        && (mchId.startsWith("1900") || mchId.startsWith("1901") || mchId.startsWith("1902"))) {
      log.warn("检测到可能的服务商商户号: {}，但未配置子商户信息", mchId);
    }

    WxPayService wxPayService = new WxPayServiceImpl();
    wxPayService.setConfig(wxPayConfig); // 微信配置信息

    WxMpDefaultConfigImpl wxMpConfigStorage = new WxMpDefaultConfigImpl();
    wxMpConfigStorage.setAppId(appId);
    wxMpConfigStorage.setSecret(appSecret);

    WxMpService wxMpService = new WxMpServiceImpl();
    wxMpService.setWxMpConfigStorage(wxMpConfigStorage); // 微信配置信息

    return new WxServiceUtil(apiVersion, wxPayService, wxMpService, notifyUrl, payChannelConfigNo);
  }

  public static WxServiceUtil buildWxServiceWrapper(WxPayIsvParams wxpayParams) {
    // 验证参数
    validateWxPayIsvParams(wxpayParams);

    // 放置 wxJavaService
    return buildWxServiceWrapper(
        wxpayParams.getMchId(),
        wxpayParams.getAppId(),
        wxpayParams.getAppSecret(),
        wxpayParams.getKey(),
        wxpayParams.getApiVersion(),
        wxpayParams.getApiV3Key(),
        wxpayParams.getSerialNo(),
        wxpayParams.getCert(),
        wxpayParams.getApiClientCert(),
        wxpayParams.getApiClientKey(),
        wxpayParams.getNotifyUrl(),
        wxpayParams.getSerialNo());
  }

  /** 验证微信支付ISV参数 */
  private static void validateWxPayIsvParams(WxPayIsvParams params) {
    log.info("验证微信支付ISV参数:");
    log.info("  mchId: {}", params.getMchId());
    log.info("  appId: {}", params.getAppId());
    log.info("  apiVersion: {}", params.getApiVersion());
    log.info(
        "  apiV3Key: {}",
        params.getApiV3Key() != null
            ? "有值：" + params.getApiV3Key() + "长度: " + params.getApiV3Key().length()
            : "无值");
  }

  public static WxServiceUtil buildWxServiceWrapper(WxPayNormalMchParams wxpayParams) {
    // 验证参数
    validateWxPayParams(wxpayParams);

    // 放置 wxJavaService
    return buildWxServiceWrapper(
        wxpayParams.getMchId(),
        wxpayParams.getAppId(),
        wxpayParams.getAppSecret(),
        wxpayParams.getKey(),
        wxpayParams.getApiVersion(),
        wxpayParams.getApiV3Key(),
        wxpayParams.getSerialNo(),
        wxpayParams.getCert(),
        wxpayParams.getApiClientCert(),
        wxpayParams.getApiClientKey(),
        wxpayParams.getNotifyUrl(),
        wxpayParams.getSerialNo());
  }

  /** 验证微信支付参数 */
  private static void validateWxPayParams(WxPayNormalMchParams params) {
    log.info("验证微信支付参数:");
    log.info("  mchId: {}", params.getMchId());
    log.info("  appId: {}", params.getAppId());
    log.info("  apiVersion: {}", params.getApiVersion());
    log.info(
        "  apiV3Key: {}",
        params.getApiV3Key() != null
            ? "有值：" + params.getApiV3Key() + "长度: " + params.getApiV3Key().length()
            : "无值");
    log.info(
        "  serialNo: {}",
        params.getSerialNo() != null
            ? "有值：" + params.getSerialNo() + "长度: " + params.getSerialNo().length()
            : "无值");
    log.info("  notifyUrl: {}", params.getNotifyUrl());
  }
}
