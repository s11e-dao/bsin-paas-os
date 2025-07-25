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

  public String toJSONString() {
    return JSON.toJSONString(this);
  }
}
