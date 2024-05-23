package me.flyray.bsin.facade.response;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxPlatformMenuTreeVo implements Serializable {

  private String serialNo;

  private String menuKey;

  private String name;

  // 菜单URl
  private String url;

  /** 菜单等级 */
  private String level;

  // 菜单图标
  private String icon;

  // 父级菜单ID ，一级菜单为0
  private String parentId;

  // 菜单类型 click|view|event
  private String type;

  private String sort;

  /** 菜单状态 */
  private String status;

  private String description;

  private List<WxPlatformMenuTreeVo> children;
}
