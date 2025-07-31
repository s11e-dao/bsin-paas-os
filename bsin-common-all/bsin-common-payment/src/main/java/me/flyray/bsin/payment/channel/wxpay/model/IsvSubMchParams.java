package me.flyray.bsin.payment.channel.wxpay.model;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;

/*
 * 抽象类 特约商户参数定义
 *
 */
public abstract class IsvSubMchParams {

  public static IsvSubMchParams factory(String ifCode, String paramsStr) {

    try {
      return (IsvSubMchParams)
          JSONObject.parseObject(
              paramsStr,
              Class.forName(
                  IsvSubMchParams.class.getPackage().getName()
                      + "."
                      + StrUtil.upperFirst(ifCode)
                      + "IsvSubMchParams"));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
