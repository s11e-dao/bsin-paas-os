package me.flyray.bsin.payment.channel.wxpay;

import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.config.WxPayConfig;
import me.flyray.bsin.payment.channel.wxpay.WxServiceUtil;
import me.flyray.bsin.payment.enums.PayMerchantModeEnum;
import org.apache.commons.lang3.StringUtils;

/*
 * 【微信支付】支付通道工具包
 *
 */
public class WxPayUtil {

  /** 放置 isv特殊信息 */
  public static void putApiIsvInfo(
      String merchantType, int infoType, String infoId, BaseWxPayRequest req) {

    // 不是特约商户， 无需放置此值
    if (!PayMerchantModeEnum.ISV_SUB_MERCHANT_MODE.getCode().equals(merchantType)) {
      return;
    }

//    ConfigContextQueryBiz configContextQueryService =
//        SpringUtils.getBean(ConfigContextQueryBiz.class);
//
//    WxpayIsvsubMchParams isvsubMchParams =
//        (WxpayIsvsubMchParams)
//            configContextQueryService.queryIsvsubMchParams(
//                infoType, infoId, CS.IF_CODE.WXPAY, merchantType);
//
//    req.setSubMchId(isvsubMchParams.getSubMchId());
//    req.setSubAppId(isvsubMchParams.getSubMchAppId());
  }

  /** 构造服务商 + 商户配置 wxPayConfig */
  public static WxPayConfig getWxPayConfig(WxServiceUtil wxServiceWrapper) {
    return wxServiceWrapper.getWxPayService().getConfig();
  }

  public static String appendErrCode(String code, String subCode) {
    return StringUtils.defaultIfEmpty(subCode, code); // 优先： subCode
  }

  public static String appendErrMsg(String msg, String subMsg) {

    if (StringUtils.isNotEmpty(msg) && StringUtils.isNotEmpty(subMsg)) {
      return msg + "【" + subMsg + "】";
    }
    return StringUtils.defaultIfEmpty(subMsg, msg);
  }

//  public static void commonSetErrInfo(ChannelRetMsg channelRetMsg, WxPayException wxPayException) {
//
//    channelRetMsg.setChannelErrCode(
//        appendErrCode(wxPayException.getReturnCode(), wxPayException.getErrCode()));
//    channelRetMsg.setChannelErrMsg(
//        appendErrMsg(
//            "OK".equalsIgnoreCase(wxPayException.getReturnMsg())
//                ? null
//                : wxPayException.getReturnMsg(),
//            wxPayException.getErrCodeDes()));
//
//    // 如果仍然为空
//    if (StringUtils.isEmpty(channelRetMsg.getChannelErrMsg())) {
//      channelRetMsg.setChannelErrMsg(
//          StringUtils.defaultIfEmpty(
//              wxPayException.getCustomErrorMsg(), wxPayException.getMessage()));
//    }
//  }
}
