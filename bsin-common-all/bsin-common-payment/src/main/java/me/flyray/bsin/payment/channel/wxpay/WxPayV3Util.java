package me.flyray.bsin.payment.channel.wxpay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import java.util.Map;

import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.payment.channel.wxpay.model.WxPayV3OrderRequestModel;

@Slf4j
public class WxPayV3Util {

  private static final String PAY_BASE_URL = "https://api.mch.weixin.qq.com";
  public static final Map<String, String> NORMALMCH_URL_MAP = new HashMap<>();

  static {
    NORMALMCH_URL_MAP.put(WxPayConstants.TradeType.APP, "/v3/pay/transactions/app");
    NORMALMCH_URL_MAP.put(WxPayConstants.TradeType.JSAPI, "/v3/pay/transactions/jsapi");
    NORMALMCH_URL_MAP.put(WxPayConstants.TradeType.NATIVE, "/v3/pay/transactions/native");
    NORMALMCH_URL_MAP.put(WxPayConstants.TradeType.MWEB, "/v3/pay/transactions/h5");
  }

  public static final Map<String, String> ISV_URL_MAP = new HashMap<>();

  static {
    ISV_URL_MAP.put(WxPayConstants.TradeType.APP, "/v3/pay/partner/transactions/app");
    ISV_URL_MAP.put(WxPayConstants.TradeType.JSAPI, "/v3/pay/partner/transactions/jsapi");
    ISV_URL_MAP.put(WxPayConstants.TradeType.NATIVE, "/v3/pay/partner/transactions/native");
    ISV_URL_MAP.put(WxPayConstants.TradeType.MWEB, "/v3/pay/partner/transactions/h5");
  }

  public static JSONObject queryOrderV3(String url, WxPayService wxPayService)
      throws WxPayException {
    String response = wxPayService.getV3(PAY_BASE_URL + url);
    return JSON.parseObject(response);
  }

  public static JSONObject closeOrderV3(String url, JSONObject reqJSON, WxPayService wxPayService)
      throws WxPayException {
    String response = wxPayService.postV3(PAY_BASE_URL + url, reqJSON.toJSONString());
    return JSON.parseObject(response);
  }

  public static JSONObject refundV3(JSONObject reqJSON, WxPayService wxPayService)
      throws WxPayException {
    String url = String.format("%s/v3/refund/domestic/refunds", PAY_BASE_URL);
    String response = wxPayService.postV3(url, reqJSON.toJSONString());
    return JSON.parseObject(response);
  }

  public static JSONObject refundQueryV3(String refundOrderId, WxPayService wxPayService)
      throws WxPayException {
    String url = String.format("%s/v3/refund/domestic/refunds/%s", PAY_BASE_URL, refundOrderId);
    String response = wxPayService.getV3(url);
    return JSON.parseObject(response);
  }

  public static JSONObject refundQueryV3Isv(String refundOrderId, WxPayService wxPayService)
      throws WxPayException {
    String url =
        String.format(
            "%s/v3/refund/domestic/refunds/%s?sub_mchid=%s",
            PAY_BASE_URL, refundOrderId, wxPayService.getConfig().getSubMchId());
    String response = wxPayService.getV3(url);
    return JSON.parseObject(response);
  }

  /**
   * 功能描述: 统一调起微信支付请求
   *
   * @param model
   * @param wxPayService
   * @param isIsvsubMch
   * @param tradeType
   * @param wxCallBack @Return: java.lang.String @Author: terrfly @Date: 2022/4/25 12:38
   */
  public static String commonReqWx(
      WxPayV3OrderRequestModel model,
      WxPayService wxPayService,
      boolean isIsvsubMch,
      String tradeType,
      WxCallBack wxCallBack)
      throws WxPayException {

    // 请求地址
    String reqUrl = PAY_BASE_URL + NORMALMCH_URL_MAP.get(tradeType);
    if (isIsvsubMch) { // 特约商户
      reqUrl = PAY_BASE_URL + ISV_URL_MAP.get(tradeType);
    }

    // 调起http请求
    String requestJson = JSON.toJSONString(model);
    log.info("微信支付V3请求数据: {}", requestJson);
    String response = wxPayService.postV3(reqUrl, requestJson);

    JSONObject wxRes = JSON.parseObject(response);

    if (wxCallBack != null) {
      return wxCallBack.genPayInfo(wxRes);
    }

    return response;
  }

  public interface WxCallBack {
    /** 生成返回数据 */
    String genPayInfo(JSONObject wxRes);
  }
}
