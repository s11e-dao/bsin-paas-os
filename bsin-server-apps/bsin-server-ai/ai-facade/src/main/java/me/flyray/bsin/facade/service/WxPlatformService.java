package me.flyray.bsin.facade.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

/**
 * @author bolei
 * @description 针对表【ai_wx_platform】的数据库操作Service
 * @createDate 2023-04-25 18:41:19
 */
public interface WxPlatformService {

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
  Map<String, Object> loginIn(Map<String, Object> requestMap) throws JsonProcessingException;

  /** 更新登录状态 */
  Map<String, Object> updateLoginResult(Map<String, Object> requestMap)
      throws JsonProcessingException;

  /** 获取登录状态列表 */
  Map<String, Object> getLoginList(Map<String, Object> requestMap);
}
