package me.flyray.bsin.payment.common;

import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import lombok.Data;

/*
 * 支付接口抽象Response 參數
 *
 */
@Data
public abstract class AbstractRes implements Serializable {

  /**
   * 是否成功 - 默认为true，子类可以重写此方法
   */
  public boolean isSuccess() {
    return true;
  }

  public String toJSONString() {
    return JSON.toJSONString(this);
  }
}
