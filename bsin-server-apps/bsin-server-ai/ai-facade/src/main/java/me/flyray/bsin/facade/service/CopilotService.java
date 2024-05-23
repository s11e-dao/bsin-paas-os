package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author leonard
 * @description 针对表【ai_copilot】的数据库操作Service
 * @createDate 2023-12-9 01:40:35
 */
public interface CopilotService {

  /** 添加 */
  Map<String, Object> add(Map<String, Object> requestMap);

  /** 删除 */
  Map<String, Object> delete(Map<String, Object> requestMap);

  /** 编辑 */
  Map<String, Object> edit(Map<String, Object> requestMap);

  /** 详情 */
  Map<String, Object> getDetail(Map<String, Object> requestMap);

  /** 创建数字分身(个人用户)|品牌官(商户） 与add()不同的是，该接口会默认绑定系统租户的llm,embeddingModel,promoteModel, */
  Map<String, Object> createDigitalAvatarOrBrandOfficer(Map<String, Object> requestMap);

  /** 分页查询 */
  Map<String, Object> getPageList(Map<String, Object> requestMap);

  Map<String, Object> getPageListByTenant(Map<String, Object> requestMap);

  /** 查询 */
  Map<String, Object> getList(Map<String, Object> requestMap);

  public Map<String, Object> getAppAgent(Map<String, Object> requestMap);

  /** 获取默认copilot */
  public Map<String, Object> getMerchantDefault(Map<String, Object> requestMap);

  /** 获取默认copilot */
  public Map<String, Object> getCustomerDefault(Map<String, Object> requestMap);

  /** 设置为默认copilot */
  public Map<String, Object> setDefault(Map<String, Object> requestMap);

  /** chat */
  public Map<String, Object> chat(Map<String, Object> requestMap);
}
