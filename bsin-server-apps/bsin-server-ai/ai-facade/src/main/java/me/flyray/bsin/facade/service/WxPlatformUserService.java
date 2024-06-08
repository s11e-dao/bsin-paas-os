package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.WxPlatformUser;

import java.util.Map;

/**
 * @author bolei
 * @description 针对表【ai_wx_platform_user】的数据库操作Service
 * @createDate 2023-04-28 14:02:38
 */
public interface WxPlatformUserService {

  /** 添加 */
  WxPlatformUser add(Map<String, Object> requestMap);

  /** 删除 */
  void delete(Map<String, Object> requestMap);

  /** 编辑 */
  void edit(Map<String, Object> requestMap);

  /** 详情 */
  WxPlatformUser getDetail(Map<String, Object> requestMap);

  /** 分页查询 */
  IPage<WxPlatformUser> getPageList(Map<String, Object> requestMap);
}
