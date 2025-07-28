package me.flyray.bsin.payment.channel.wxpay.model;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;

/*
 * 抽象类 普通商户参数定义
 *
 */
public abstract class NormalMchParams {

  public static NormalMchParams factory(String ifCode, String paramsStr) {

    try {
      // 首字母大写，倒数第三个字母大写
      String className = capitalizeFirstAndThirdFromEnd(ifCode) + "NormalMchParams";
      
      return (NormalMchParams)
          JSONObject.parseObject(
              paramsStr,
              Class.forName(
                  NormalMchParams.class.getPackage().getName()
                      + "."
                      + className));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 首字母大写，倒数第三个字母大写
   * 例如：wxpay -> WxPay, alipay -> AliPay, brandspointpay -> BrandspointPay
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
