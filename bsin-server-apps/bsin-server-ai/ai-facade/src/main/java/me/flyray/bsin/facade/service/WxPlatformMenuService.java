package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author leonard
 * @description 针对表【ai_wx_platform_menu】的数据库操作Service
 * @createDate 2024-01-26 11:16:19
 */
public interface WxPlatformMenuService {

  /** 添加 */
  Map<String, Object> add(Map<String, Object> requestMap);

  /** 删除 */
  Map<String, Object> delete(Map<String, Object> requestMap);

  /** 编辑 */
  Map<String, Object> edit(Map<String, Object> requestMap);

  /** 详情 */
  Map<String, Object> getDetail(Map<String, Object> requestMap);

  /** 分页查询 */
  Map<String, Object> getPageList(Map<String, Object> requestMap);

  /** 查询 */
  Map<String, Object> getList(Map<String, Object> requestMap);

  /** 获取默认 */
  public Map<String, Object> getDefault(Map<String, Object> requestMap);

  /** 设置为默认 */
  public Map<String, Object> setDefault(Map<String, Object> requestMap);

  /** 启动 */
  Map<String, Object> syncMenu(Map<String, Object> requestMap);

  /** 获取菜单模版列表的菜单树 */
  Map<String, Object> getMenuTemplateMenuTreeList(Map<String, Object> requestMap);

  /** 获取菜单模版菜单树 */
  Map<String, Object> getMenuTemplateMenuTree(Map<String, Object> requestMap);

  /** 同步公众号菜单 */
  Map<String, Object> syncMpMenu(Map<String, Object> requestMap);

  /** 移除公众号菜单 */
  Map<String, Object> removeMpMenu(Map<String, Object> requestMap);

}
