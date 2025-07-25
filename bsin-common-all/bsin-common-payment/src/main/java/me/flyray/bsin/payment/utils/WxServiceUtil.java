package me.flyray.bsin.payment.utils;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.flyray.bsin.payment.enums.PayInterfaceVersion;
import me.flyray.bsin.payment.model.WxPayIsvParams;
import me.flyray.bsin.payment.model.WxPayNormalMchParams;
import me.flyray.bsin.utils.SpringUtils;
import org.apache.commons.lang3.StringUtils;

/*
 * wxService 包装类
 *
 */
@Data
@AllArgsConstructor
public class WxServiceUtil {

  /** 缓存微信API版本 * */
  private String apiVersion;

  /** 缓存 wxPayService 对象 * */
  private WxPayService wxPayService;

  /** 缓存 wxJavaService 对象 * */
  private WxMpService wxMpService;

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
      String apiClientKey) {

    WxPayConfig wxPayConfig = new WxPayConfig();
    wxPayConfig.setMchId(mchId);
    wxPayConfig.setAppId(appId);
    wxPayConfig.setMchKey(mchKey);

    // 微信条码付、扫码付 只支付V2版本
    if (PayInterfaceVersion.WX_V2.equals(apiVersion)) {
      wxPayConfig.setSignType(WxPayConstants.SignType.MD5);
    }
    ChannelCertConfigUtil channelCertConfigKitBean =
        SpringUtils.getBean(ChannelCertConfigUtil.class);

    if (StringUtils.isNotBlank(apiV3Key)) {
      wxPayConfig.setApiV3Key(apiV3Key);
    }
    if (StringUtils.isNotBlank(serialNo)) {
      wxPayConfig.setCertSerialNo(serialNo);
    }
    if (StringUtils.isNotBlank(cert)) {
      wxPayConfig.setKeyPath(channelCertConfigKitBean.getCertFilePath(cert));
    }
    if (StringUtils.isNotBlank(apiClientCert)) {
      wxPayConfig.setPrivateCertPath(channelCertConfigKitBean.getCertFilePath(apiClientCert));
    }
    if (StringUtils.isNotBlank(apiClientKey)) {
      wxPayConfig.setPrivateKeyPath(channelCertConfigKitBean.getCertFilePath(apiClientKey));
    }

    WxPayService wxPayService = new WxPayServiceImpl();
    wxPayService.setConfig(wxPayConfig); // 微信配置信息

    WxMpDefaultConfigImpl wxMpConfigStorage = new WxMpDefaultConfigImpl();
    wxMpConfigStorage.setAppId(appId);
    wxMpConfigStorage.setSecret(appSecret);

    WxMpService wxMpService = new WxMpServiceImpl();
    wxMpService.setWxMpConfigStorage(wxMpConfigStorage); // 微信配置信息

    return new WxServiceUtil(apiVersion, wxPayService, wxMpService);
  }

  public static WxServiceUtil buildWxServiceWrapper(WxPayIsvParams wxpayParams) {
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
        wxpayParams.getApiClientKey());
  }

  public static WxServiceUtil buildWxServiceWrapper(WxPayNormalMchParams wxpayParams) {
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
        wxpayParams.getApiClientKey());
  }
}
