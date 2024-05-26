package me.flyray.bsin.facade.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FreezeStatus {

  /** 冻结状态 */
  FREEZE("1", "冻结"),
  UN_FREEZE("2", "已解冻"),
  SOME_FREEZE("3", "部分解冻");

  private String code;

  private String desc;
}
