package me.flyray.bsin.payment.channel.wxpay.model;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;

/** 抽象类 isv参数定义 */
public abstract class IsvParams {

  public static IsvParams factory(String ifCode, String paramsStr) {

    try {
      return (IsvParams)
          JSONObject.parseObject(
              paramsStr,
              Class.forName(
                  IsvParams.class.getPackage().getName()
                      + "."
                      + StrUtil.upperFirst(ifCode)
                      + "IsvParams"));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /** 敏感数据脱敏 */
  public abstract String deSenData();
}
