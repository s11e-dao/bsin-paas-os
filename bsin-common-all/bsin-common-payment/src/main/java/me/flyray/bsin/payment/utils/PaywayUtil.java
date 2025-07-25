package me.flyray.bsin.payment.utils;

import cn.hutool.core.util.StrUtil;
import me.flyray.bsin.payment.IPaymentService;
import me.flyray.bsin.utils.SpringUtils;

/*
 * 支付方式动态调用Utils
 */
public class PaywayUtil {

  private static final String PAYWAY_PACKAGE_NAME = "payway";

  /** 获取真实的支付方式Service */
  public static IPaymentService getRealPaywayService(Object obj, String wayCode) {

    try {

      // 下划线转换驼峰 & 首字母大写
      String clsName = StrUtil.upperFirst(StrUtil.toCamelCase(wayCode.toLowerCase()));
      return (IPaymentService)
          SpringUtils.getBean(
              Class.forName(
                  obj.getClass().getPackage().getName()
                      + "."
                      + PAYWAY_PACKAGE_NAME
                      + "."
                      + clsName));

    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  /** 获取微信V3真实的支付方式Service */
  public static IPaymentService getRealPaywayV3Service(Object obj, String wayCode) {
    //    getBean
    try {

      // 下划线转换驼峰 & 首字母大写
      String clsName = StrUtil.upperFirst(StrUtil.toCamelCase(wayCode.toLowerCase()));
      return (IPaymentService)
          SpringUtils.getBean(
              Class.forName(
                  obj.getClass().getPackage().getName()
                      + "."
                      + PAYWAY_PACKAGE_NAME
                      + "."
                      + clsName));

    } catch (ClassNotFoundException e) {
      return null;
    }
  }
}
