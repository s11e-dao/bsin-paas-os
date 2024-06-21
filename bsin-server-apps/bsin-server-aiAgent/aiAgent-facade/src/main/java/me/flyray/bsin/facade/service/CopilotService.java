package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.CopilotInfo;

import java.util.List;
import java.util.Map;

/**
 * @author leonard
 * @description 针对表【ai_copilot】的数据库操作Service
 * @createDate 2023-12-9 01:40:35
 */
public interface CopilotService {

  /** 添加 */
  CopilotInfo add(Map<String, Object> requestMap);

  /** 删除 */
  void delete(Map<String, Object> requestMap);

  /** 编辑 */
  void edit(Map<String, Object> requestMap);

  /** 详情 */
  CopilotInfo getDetail(Map<String, Object> requestMap);

  /** 创建数字分身(个人用户)|品牌官(商户） 与add()不同的是，该接口会默认绑定系统租户的llm,embeddingModel,promoteModel, */
  CopilotInfo createDigitalAvatarOrBrandOfficer(Map<String, Object> requestMap);

  /** 分页查询 */
  IPage<CopilotInfo> getPageList(Map<String, Object> requestMap);

  IPage<CopilotInfo> getPageListByTenant(Map<String, Object> requestMap);

  /** 查询 */
  List<CopilotInfo> getList(Map<String, Object> requestMap);

  public CopilotInfo getAppAgent(Map<String, Object> requestMap);

  /** 获取默认copilot */
  public CopilotInfo getMerchantDefault(Map<String, Object> requestMap);

  /** 获取默认copilot */
  public CopilotInfo getCustomerDefault(Map<String, Object> requestMap);

  /** 设置为默认copilot */
  public void setDefault(Map<String, Object> requestMap);

  /** chat */
  public Map<String, Object> chat(Map<String, Object> requestMap);
}
