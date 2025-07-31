package me.flyray.bsin.payment.channel.wxpay.model;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/** 抽象类 isv参数定义 */
@Slf4j
public abstract class IsvParams {


  public static IsvParams factory(String ifCode, String paramsStr) {

//    try {
//      // 首字母大写，倒数第三个字母大写
//      String className = capitalizeFirstAndThirdFromEnd(ifCode) + "IsvParams";
//
//      return (IsvParams)
//          JSONObject.parseObject(
//              paramsStr,
//              Class.forName(
//                  IsvParams.class.getPackage().getName()
//                      + "."
//                      + className));
//    } catch (ClassNotFoundException e) {
//      e.printStackTrace();
//    }

    try {
      // 首字母大写，倒数第三个字母大写
      String className = capitalizeFirstAndThirdFromEnd(ifCode) + "IsvParams";

      Class<?> clazz = Class.forName(
              NormalMchParams.class.getPackage().getName()
                      + "."
                      + className);

      // 创建实例
      IsvParams instance = (IsvParams) JSONObject.parseObject(paramsStr, clazz);

      // 如果是微信支付参数，自动处理证书文件
      if (instance instanceof WxPayIsvParams) {
        WxPayIsvParams wxPayParams = (WxPayIsvParams) instance;
        // 自动处理Base64证书文件
        wxPayParams.processBase64CertFiles();
        log.info("微信支付参数证书文件处理完成");
      }

      return instance;
    } catch (ClassNotFoundException e) {
      String className = capitalizeFirstAndThirdFromEnd(ifCode) + "NormalMchParams";
      log.error("未找到对应的参数类: {}", className, e);
    } catch (Exception e) {
      log.error("创建支付参数实例时发生错误: ifCode={}, paramsStr={}", ifCode, paramsStr, e);
    }
    return null;
  }

  /**
   * 首字母大写，倒数第三个字母大写
   * 例如：wxpay -> WxPay, alipay -> AliPay,  brandspointpay -> BrandspointPay
   */
  private static String capitalizeFirstAndThirdFromEnd(String str) {
    if (str == null || str.length() < 3) {
      return StrUtil.upperFirst(str);
    }
    
    // 首字母大写
    String result = StrUtil.upperFirst(str);
    
    // 倒数第三个字母大写
    int thirdFromEndIndex = result.length() - 3;
    if (thirdFromEndIndex >= 0) {
      char[] chars = result.toCharArray();
      chars[thirdFromEndIndex] = Character.toUpperCase(chars[thirdFromEndIndex]);
      result = new String(chars);
    }
    
    return result;
  }

  /** 敏感数据脱敏 */
  public abstract String deSenData();
}
