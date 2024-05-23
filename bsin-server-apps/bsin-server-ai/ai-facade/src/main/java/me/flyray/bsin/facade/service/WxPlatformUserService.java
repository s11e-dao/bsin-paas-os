package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author bolei
 * @description 针对表【ai_wx_platform_user】的数据库操作Service
 * @createDate 2023-04-28 14:02:38
 */
public interface WxPlatformUserService {

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
}
