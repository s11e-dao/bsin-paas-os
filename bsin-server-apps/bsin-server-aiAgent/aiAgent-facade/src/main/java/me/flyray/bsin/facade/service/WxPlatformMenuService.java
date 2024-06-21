package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.WxPlatformMenu;
import me.flyray.bsin.facade.response.WxPlatformMenuTemplateVo;
import me.flyray.bsin.facade.response.WxPlatformMenuTreeVo;

import java.util.List;
import java.util.Map;

/**
 * @author leonard
 * @description 针对表【ai_wx_platform_menu】的数据库操作Service
 * @createDate 2024-01-26 11:16:19
 */
public interface WxPlatformMenuService {

  /** 添加 */
  WxPlatformMenu add(Map<String, Object> requestMap);

  /** 删除 */
  void delete(Map<String, Object> requestMap);

  /** 编辑 */
  void edit(Map<String, Object> requestMap);

  /** 详情 */
  WxPlatformMenu getDetail(Map<String, Object> requestMap);

  /** 分页查询 */
  IPage<WxPlatformMenu> getPageList(Map<String, Object> requestMap);

  /** 查询 */
  List<WxPlatformMenu> getList(Map<String, Object> requestMap);

  /** 获取默认 */
  public WxPlatformMenu getDefault(Map<String, Object> requestMap);

  /** 设置为默认 */
  public void setDefault(Map<String, Object> requestMap);

  /** 启动 */
  Map<String, Object> syncMenu(Map<String, Object> requestMap);

  /** 获取菜单模版列表的菜单树 */
  List<WxPlatformMenuTreeVo> getMenuTemplateMenuTreeList(Map<String, Object> requestMap);

  /** 获取菜单模版菜单树 */
  WxPlatformMenuTemplateVo getMenuTemplateMenuTree(Map<String, Object> requestMap);

  /** 同步公众号菜单 */
  Map<String, Object> syncMpMenu(Map<String, Object> requestMap);

  /** 移除公众号菜单 */
  Map<String, Object> removeMpMenu(Map<String, Object> requestMap);

}
