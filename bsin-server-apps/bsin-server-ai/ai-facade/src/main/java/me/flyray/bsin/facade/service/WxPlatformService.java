package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import me.flyray.bsin.domain.entity.WxPlatform;

import java.util.List;
import java.util.Map;

/**
 * @author bolei
 * @description 针对表【ai_wx_platform】的数据库操作Service
 * @createDate 2023-04-25 18:41:19
 */
public interface WxPlatformService {

  /** 添加 */
  WxPlatform add(Map<String, Object> requestMap);

  /** 删除 */
  void delete(Map<String, Object> requestMap);

  /** 编辑 */
  void edit(Map<String, Object> requestMap);

  /** 详情 */
  WxPlatform getDetail(Map<String, Object> requestMap);

  /** 分页查询 */
  IPage<WxPlatform> getPageList(Map<String, Object> requestMap);

  /** 查询 */
  List<WxPlatform> getList(Map<String, Object> requestMap);

  /** 获取默认 */
  public WxPlatform getDefault(Map<String, Object> requestMap);

  /** 设置为默认 */
  public void setDefault(Map<String, Object> requestMap);

  /** 启动 */
  WxPlatform loginIn(Map<String, Object> requestMap) throws JsonProcessingException;

  /** 更新登录状态 */
  WxPlatform updateLoginResult(Map<String, Object> requestMap)
      throws JsonProcessingException;

  /** 获取登录状态列表 */
  List<?> getLoginList(Map<String, Object> requestMap);
}
