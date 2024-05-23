package me.flyray.bsin.server.impl;

import static java.util.stream.Collectors.joining;
import static me.flyray.bsin.constants.ResponseCode.MERCHANT_NO_IS_NULL;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.hutool.core.util.ObjectUtil;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.AiCustomerFunction;
import me.flyray.bsin.domain.domain.CopilotInfo;
import me.flyray.bsin.domain.domain.LLMParam;
import me.flyray.bsin.domain.domain.PromptTemplateParam;
import me.flyray.bsin.domain.domain.QuickReplyMessage;
import me.flyray.bsin.domain.domain.RedisChatMessage;
import me.flyray.bsin.domain.enums.CopilotType;
import me.flyray.bsin.domain.enums.VectorStoreType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.EmbeddingVO;
import me.flyray.bsin.facade.service.CopilotService;
import me.flyray.bsin.infrastructure.mapper.CopilotMapper;
import me.flyray.bsin.infrastructure.mapper.EmbeddingModelMapper;
import me.flyray.bsin.infrastructure.mapper.KnowledgeBaseMapper;
import me.flyray.bsin.infrastructure.mapper.LLMMapper;
import me.flyray.bsin.infrastructure.mapper.PromptTemplateMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.biz.AccountAvailableResourcesBiz;
import me.flyray.bsin.server.biz.ChatBiz;
import me.flyray.bsin.server.biz.ChatHistorySummaryAndStoreBiz;
import me.flyray.bsin.server.biz.ModelBiz;
import me.flyray.bsin.server.biz.PromptEngineeringBiz;
import me.flyray.bsin.server.biz.VectorRetrievalBiz;
import me.flyray.bsin.server.memory.chat.SummaryEmbeddingVectorStoreMemory;
import me.flyray.bsin.server.memory.store.InRamStore;
import me.flyray.bsin.server.memory.store.InRedisStore;
import me.flyray.bsin.server.memory.store.InVectorDataBaseEmbeddingStore;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author leonard
 * @description 针对表【ai_copilot】的数据库操作Service实现
 * @createDate 2023-12-09 01:50:35
 */
@ShenyuDubboService(path = "/copilot", timeout = 6000)
@ApiModule(value = "copilot")
@Service
@Slf4j
public class CopilotServiceImpl implements CopilotService {

  @Value("${bsin.jiujiu.tenantId}")
  private String jiujiuTenantId;

  @Value("${bsin.jiujiu.merchantNo}")
  private String jiujiumerchantNo;

  @Value("${milvus.datasource.merchantCollectionName}")
  private String merchantCollectionName;

  @Value("${milvus.datasource.customerCollectionName}")
  private String customerCollection;

  @Value("${milvus.datasource.partitionName}")
  private String partitionName;

  @Autowired private VectorRetrievalBiz vectorRetrievalBiz;
  @Autowired private LLMMapper llmMapper;
  @Autowired private EmbeddingModelMapper embeddingModelMapper;
  @Autowired private PromptTemplateMapper promptTemplateMapper;
  @Autowired private KnowledgeBaseMapper knowledgeBaseMapper;
  @Autowired private CopilotMapper copilotMapper;

  @Autowired private InVectorDataBaseEmbeddingStore inVectorDataBaseEmbeddingStore;
  @Autowired private InRedisStore inRedisStore; // 用于缓存历史聊天记录
  private InRamStore inRamStore = new InRamStore(); // 用于缓存最近K条上下文窗口聊天记录
  private InRamStore inRamCacheStore = new InRamStore(); // 用于缓存总结的历史聊天记录
  @Autowired private PromptEngineeringBiz promptEngineeringBiz;

  // 历史聊天记录总结和存储异步线程执行服务
  ExecutorService chatSummaryAndStoreExecutorService = Executors.newFixedThreadPool(10);
  @Autowired private ChatBiz chatBiz;
  @Autowired private ModelBiz modelBiz;
  @Autowired private AccountAvailableResourcesBiz accountAvailableResourcesBiz;

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public Map<String, Object> add(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
      if (customerNo == null) {
        throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
      }
    }
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
      if (tenantId == null) {
        throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
      }
    }
    CopilotInfo copilotInfo = BsinServiceContext.getReqBodyDto(CopilotInfo.class, requestMap);
    copilotInfo.setSerialNo(BsinSnowflake.getId());
    copilotInfo.setTenantId(tenantId);
    copilotInfo.setMerchantNo(merchantNo);
    copilotInfo.setCustomerNo(customerNo);
    if (copilotInfo.getLlmNo() != null) {
      LLMParam llmparam = llmMapper.selectById(copilotInfo.getLlmNo());
      if (llmparam != null) {
        copilotInfo.setStreaming(llmparam.isStreaming());
      }
    }
    AiCustomerFunction validFunction =
        accountAvailableResourcesBiz.functionServiceCheck(tenantId, merchantNo, customerNo);

    // 查询出当前客户已经创建的资源数量
    LambdaUpdateWrapper<CopilotInfo> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(CopilotInfo::getTenantId, tenantId);
    wrapper.eq(CopilotInfo::getMerchantNo, merchantNo);
    wrapper.eq(CopilotInfo::getCustomerNo, customerNo);
    List<CopilotInfo> copilotList = copilotMapper.selectList(wrapper);
    if (copilotList.size() >= validFunction.getCopilotNum()) {
      throw new BusinessException(
          "100000", "可创建的智能体资源超过限制：" + copilotList.size() + ">=" + validFunction.getCopilotNum());
    }
    copilotMapper.insert(copilotInfo);
    return RespBodyHandler.setRespBodyDto(copilotInfo);
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public Map<String, Object> delete(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    copilotMapper.deleteById(serialNo);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public Map<String, Object> edit(Map<String, Object> requestMap) {
    CopilotInfo copilotInfo = BsinServiceContext.getReqBodyDto(CopilotInfo.class, requestMap);
    if (Boolean.TRUE.equals(copilotInfo.getDefaultFlag())) {
      setDefault(requestMap);
    }
    if (copilotInfo.getLlmNo() != null) {
      LLMParam llmparam = llmMapper.selectById(copilotInfo.getLlmNo());
      if (llmparam != null) {
        copilotInfo.setStreaming(llmparam.isStreaming());
      }
    }
    copilotMapper.updateById(copilotInfo);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    if (serialNo == null) {
      serialNo = MapUtils.getString(requestMap, "copilotNo");
    }
    CopilotInfo copilotInfo = copilotMapper.selectById(serialNo);
    return RespBodyHandler.setRespBodyDto(copilotInfo);
  }

  @ApiDoc(desc = "createDigitalAvatarOrBrandOfficer")
  @ShenyuDubboClient("/createDigitalAvatarOrBrandOfficer")
  @Override
  public Map<String, Object> createDigitalAvatarOrBrandOfficer(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
      if (customerNo == null) {
        throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
      }
    }
    String tenantId = loginUser.getTenantId();
    if (tenantId == null) {
      tenantId = MapUtils.getString(requestMap, "tenantId");
    }
    String avatar = MapUtils.getString(requestMap, "avatar");
    String name = MapUtils.getString(requestMap, "name");
    String sex = MapUtils.getString(requestMap, "sex");
    String description = MapUtils.getString(requestMap, "description");
    String type = MapUtils.getString(requestMap, "type");

    // 判断商户|用户是否存在品牌馆|数字分身，存在则不再创建
    LambdaQueryWrapper<CopilotInfo> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(CopilotInfo::getTenantId, tenantId);
    wrapper.eq(StringUtils.isNotBlank(merchantNo), CopilotInfo::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), CopilotInfo::getCustomerNo, customerNo);
    wrapper.eq(CopilotInfo::getDefaultFlag, true);
    wrapper.eq(CopilotInfo::getType, type);
    CopilotInfo copilotInfo = copilotMapper.selectOne(wrapper);
    if (copilotInfo != null) {
      return RespBodyHandler.setRespBodyDto(copilotInfo);
    }

    // 1.从jiujiu租户复制一个数字分身|品牌官类型的Copilot
    LambdaQueryWrapper<CopilotInfo> wrapperCopilot = new LambdaQueryWrapper<>();
    wrapperCopilot.eq(CopilotInfo::getTenantId, tenantId);
    wrapperCopilot.eq(
            StringUtils.isNotBlank(jiujiumerchantNo), CopilotInfo::getMerchantNo, jiujiumerchantNo);
    wrapperCopilot.eq(CopilotInfo::getDefaultFlag, true);
    wrapperCopilot.eq(CopilotInfo::getType, type);
    CopilotInfo defaultCopilotInfo = copilotMapper.selectOne(wrapperCopilot);
    if (defaultCopilotInfo == null) {
      throw new BusinessException("100000", "未查询到平台默认数字人|品牌官配置的Copilot！！");
    }
    // 2.修改为空户|商户的参数配置
    defaultCopilotInfo.setSerialNo(BsinSnowflake.getId());
    defaultCopilotInfo.setAvatar(avatar);
    defaultCopilotInfo.setDescription(description);
    defaultCopilotInfo.setName(name);
    defaultCopilotInfo.setSex(sex);
    defaultCopilotInfo.setCustomerNo(customerNo);
    if (type.equals(CopilotType.BRAND_OFFICER.getCode())) {
      // 挂在商户自己下
      defaultCopilotInfo.setMerchantNo(merchantNo);
    } else {
      // 挂在jiujiu商户下
    }

    // 3.拷贝一份jiujiu租户默认数字分身|品牌官Copilot的提示词模版
    PromptTemplateParam defaultPromptTemplateParam =
        promptTemplateMapper.selectById(defaultCopilotInfo.getPromptTemplateNo());
    if (defaultPromptTemplateParam == null) {
      throw new BusinessException(
          "100000", "未查询到平台默认数字人Copilot的提示词模板！！" + defaultCopilotInfo.getPromptTemplateNo());
    }
    // 4.修改为空户的参数配置
    defaultPromptTemplateParam.setSerialNo(BsinSnowflake.getId());
    defaultPromptTemplateParam.setSystemRole(name);
    defaultPromptTemplateParam.setDescription(description);
    defaultCopilotInfo.setPromptTemplateNo(defaultPromptTemplateParam.getSerialNo());
    copilotMapper.insert(defaultCopilotInfo);

    return RespBodyHandler.setRespBodyDto(defaultCopilotInfo);
  }

  /**
   * 查选租户下的个人客户数字分身
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getPageListByTenant")
  @ShenyuDubboClient("/getPageListByTenant")
  @Override
  public Map<String, Object> getPageListByTenant(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String tenantId = loginUser.getTenantId();
    if (tenantId == null) {
      tenantId = MapUtils.getString(requestMap, "tenantId");
    }
    CopilotInfo copilotInfo = BsinServiceContext.getReqBodyDto(CopilotInfo.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<CopilotInfo> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<CopilotInfo> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(CopilotInfo::getCreateTime);
    wrapper.eq(CopilotInfo::getTenantId, tenantId);
    wrapper.eq(
        StringUtils.isNotBlank(copilotInfo.getSerialNo()),
        CopilotInfo::getSerialNo,
        copilotInfo.getSerialNo());
    wrapper.eq(
            StringUtils.isNotBlank(copilotInfo.getType()), CopilotInfo::getType, copilotInfo.getType());
    wrapper.eq(
            StringUtils.isNotBlank(copilotInfo.getStatus()),
        CopilotInfo::getStatus,
        copilotInfo.getStatus());
    // 匹配系统资源
    wrapper.or().eq(CopilotInfo::getEditable, false);
    IPage<CopilotInfo> pageList = copilotMapper.selectPage(page, wrapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    String tenantId = loginUser.getTenantId();

    CopilotInfo copilotInfo = BsinServiceContext.getReqBodyDto(CopilotInfo.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<CopilotInfo> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<CopilotInfo> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(CopilotInfo::getCreateTime);
    wrapper.eq(CopilotInfo::getTenantId, tenantId);
    wrapper.eq(StringUtils.isNotBlank(merchantNo), CopilotInfo::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), CopilotInfo::getCustomerNo, customerNo);
    wrapper.eq(
            StringUtils.isNotBlank(copilotInfo.getSerialNo()),
        CopilotInfo::getSerialNo,
        copilotInfo.getSerialNo());
    wrapper.eq(
            StringUtils.isNotBlank(copilotInfo.getType()), CopilotInfo::getType, copilotInfo.getType());
    wrapper.eq(
            StringUtils.isNotBlank(copilotInfo.getStatus()),
        CopilotInfo::getStatus,
        copilotInfo.getStatus());
    // 匹配系统资源
    wrapper.or().eq(CopilotInfo::getEditable, false);
    IPage<CopilotInfo> pageList = copilotMapper.selectPage(page, wrapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  @ApiDoc(desc = "getList")
  @ShenyuDubboClient("/getList")
  @Override
  public Map<String, Object> getList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    String tenantId = loginUser.getTenantId();

    CopilotInfo copilotInfo = BsinServiceContext.getReqBodyDto(CopilotInfo.class, requestMap);
    String isDefault = (String) requestMap.get("isDefault");

    LambdaUpdateWrapper<CopilotInfo> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(CopilotInfo::getCreateTime);
    wrapper.eq(CopilotInfo::getTenantId, tenantId);
    wrapper.eq(CopilotInfo::getMerchantNo, merchantNo);
    wrapper.eq(ObjectUtil.isNotNull(isDefault), CopilotInfo::getDefaultFlag, isDefault);
    wrapper.eq(StringUtils.isNotBlank(customerNo), CopilotInfo::getCustomerNo, customerNo);
    wrapper.eq(
            StringUtils.isNotBlank(copilotInfo.getSerialNo()),
        CopilotInfo::getSerialNo,
        copilotInfo.getSerialNo());
    wrapper.eq(
            StringUtils.isNotBlank(copilotInfo.getType()), CopilotInfo::getType, copilotInfo.getType());
    wrapper.eq(
            StringUtils.isNotBlank(copilotInfo.getStatus()),
        CopilotInfo::getStatus,
        copilotInfo.getStatus());
    // 匹配系统资源
    wrapper.or().eq(CopilotInfo::getEditable, false);
    List<CopilotInfo> embeddingModelList = copilotMapper.selectList(wrapper);
    return RespBodyHandler.setRespBodyListDto(embeddingModelList);
  }

  @ApiDoc(desc = "getMerchantDefault")
  @ShenyuDubboClient("/getMerchantDefault")
  @Override
  public Map<String, Object> getMerchantDefault(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
    }
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }
    // type为1是商户品牌官，为2是个人数字分身助手
    String type = MapUtils.getString(requestMap, "type");
    LambdaQueryWrapper<CopilotInfo> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(CopilotInfo::getTenantId, tenantId);
    wrapper.eq(CopilotInfo::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(type), CopilotInfo::getType, type);
    wrapper.eq(CopilotInfo::getDefaultFlag, true);
    CopilotInfo copilotInfo = copilotMapper.selectOne(wrapper);
    return RespBodyHandler.setRespBodyDto(copilotInfo);
  }

  /**
   * 查询appAgent
   * 不同业务角色相同的appAgent产品操作
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getAppAgent")
  @ShenyuDubboClient("/getAppAgent")
  @Override
  public Map<String, Object> getAppAgent(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    LambdaQueryWrapper<CopilotInfo> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(CopilotInfo::getTenantId, loginUser.getTenantId());
    wrapper.eq(CopilotInfo::getType, "0");
    CopilotInfo copilotInfo = copilotMapper.selectOne(wrapper);
    if (copilotInfo == null) {
      wrapper.orderByDesc(CopilotInfo::getCreateTime);
      wrapper.eq(CopilotInfo::getDefaultFlag, false);
      copilotInfo = copilotMapper.selectOne(wrapper);
      if (copilotInfo != null) {
        // 当无默认时设置默认
        copilotInfo.setDefaultFlag(true);
        copilotMapper.updateById(copilotInfo);
      }
    }
    return RespBodyHandler.setRespBodyDto(copilotInfo);
  }

  @ApiDoc(desc = "getCustomerDefault")
  @ShenyuDubboClient("/getCustomerDefault")
  @Override
  public Map<String, Object> getCustomerDefault(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String type = MapUtils.getString(requestMap, "type");
    LambdaQueryWrapper<CopilotInfo> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(CopilotInfo::getTenantId, loginUser.getTenantId());
    wrapper.eq(CopilotInfo::getCustomerNo, loginUser.getCustomerNo());
    wrapper.eq(StringUtils.isNotBlank(type), CopilotInfo::getType, type);
    wrapper.eq(CopilotInfo::getDefaultFlag, true);
    CopilotInfo copilotInfo = copilotMapper.selectOne(wrapper);
    if (copilotInfo == null) {
      wrapper.orderByDesc(CopilotInfo::getCreateTime);
      wrapper.eq(CopilotInfo::getDefaultFlag, false);
      copilotInfo = copilotMapper.selectOne(wrapper);
      if (copilotInfo != null) {
        // 当无默认时设置默认
        copilotInfo.setDefaultFlag(true);
        copilotMapper.updateById(copilotInfo);
      }
    }
    return RespBodyHandler.setRespBodyDto(copilotInfo);
  }

  @ApiDoc(desc = "setDefault")
  @ShenyuDubboClient("/setDefault")
  @Override
  public Map<String, Object> setDefault(Map<String, Object> requestMap) {
    CopilotInfo copilotInfo = BsinServiceContext.getReqBodyDto(CopilotInfo.class, requestMap);
    LambdaUpdateWrapper<CopilotInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    lambdaUpdateWrapper.set(CopilotInfo::getDefaultFlag, copilotInfo.getDefaultFlag());
    lambdaUpdateWrapper.eq(CopilotInfo::getSerialNo, copilotInfo.getSerialNo());
    if (Boolean.TRUE.equals(copilotInfo.getDefaultFlag())) {
      copilotMapper.update(
          null,
          new LambdaUpdateWrapper<>(CopilotInfo.class)
              .set(CopilotInfo::getDefaultFlag, Boolean.FALSE)
              .eq(
                      StringUtils.isNotBlank(copilotInfo.getMerchantNo()),
                  CopilotInfo::getMerchantNo,
                  copilotInfo.getMerchantNo())
              .eq(
                      StringUtils.isNotBlank(copilotInfo.getCustomerNo()),
                  CopilotInfo::getCustomerNo,
                  copilotInfo.getCustomerNo()));
    }
    copilotMapper.update(null, lambdaUpdateWrapper);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "chat")
  @ShenyuDubboClient("/chat")
  @Override
  public Map<String, Object> chat(Map<String, Object> requestMap) {

    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    // 来自微信分身的客户号： wxNo:509219960
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    // 来自微信分身聊天请求字段
    String historyChatSummary = MapUtils.getString(requestMap, "historyChatSummary");
    // 根据提示词模版开启，同时需要满足： historyChatSummary == "true"
    boolean historyChatSummaryEnable = true;
    if (historyChatSummary != null) {
      historyChatSummaryEnable = Boolean.parseBoolean(historyChatSummary);
    }
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
      if (customerNo == null) {
        throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
      }
    }

    String tenantId = loginUser.getTenantId();
    String copilotNo = MapUtils.getString(requestMap, "copilotNo");
    String knowledgeBaseNo = MapUtils.getString(requestMap, "knowledgeBaseNo");
    String question = MapUtils.getString(requestMap, "question");
    String promptTemplateNo = MapUtils.getString(requestMap, "promptTemplateNo");
    String llmNo = MapUtils.getString(requestMap, "llmNo");
    String quickRepliesStr = MapUtils.getString(requestMap, "quickReplies");
    boolean quickReplies = false;
    if (quickRepliesStr != null) {
      quickReplies = Boolean.parseBoolean(quickRepliesStr);
    }

    if (copilotNo == null) {
      throw new BusinessException("100000", "copilot ID为空!!!");
    }

    CopilotInfo copilotInfo = copilotMapper.selectById(copilotNo);
    if (copilotInfo == null) {
      throw new BusinessException("100000", "未查询到copilot：" + copilotNo);
    }

    if (knowledgeBaseNo == null) {
      knowledgeBaseNo = copilotInfo.getKnowledgeBaseNo();
    }
    try {
      // 用于历史聊天记录query
      if (historyChatSummaryEnable) {
        inRedisStore.rightPushMessage(
            "chat:",
            RedisChatMessage.newBuilder()
                .withSender(customerNo)
                .withReceiver(copilotNo)
                .withMessage(question)
                .build());
      }

      // 1.知识库search
      List<EmbeddingVO> knowledgeBaseEmbeddingVOs =
          vectorRetrievalBiz.retrievalBaseOnKnowledgeBase(
              question, merchantNo, knowledgeBaseNo, merchantCollectionName, false);

      //  2.查找知识库对应的LLM
      if (llmNo == null) {
        llmNo = copilotInfo.getLlmNo();
      }
      LLMParam llmParam = llmMapper.selectById(llmNo);
      if (llmParam == null) {
        throw new BusinessException("100000", "未找到LLM！！！");
      }
      // 用于缓存最近K条上下文窗口聊天记录
      inVectorDataBaseEmbeddingStore.setInRamStore(inRamStore);
      // 用于缓存总结的历史聊天记录
      inVectorDataBaseEmbeddingStore.setInRamCacheStore(inRamCacheStore);

      // 向量存储的历史聊天记录
      SummaryEmbeddingVectorStoreMemory chatMemory =
          SummaryEmbeddingVectorStoreMemory.builder()
              .id(customerNo)
              .triggerSummaryMessages(llmParam.getMaxMessages())
              .maxMessages(llmParam.getMaxMessages())
              .triggerSummaryMessages(llmParam.getMaxSummaryMessages())
              .triggerSummaryTokens(llmParam.getMaxMessages())
              .chatMemoryStore(inVectorDataBaseEmbeddingStore)
              .build();

      // 3.提示词上下文补充
      // copilot绑定的知识库
      String knowledgeContent =
          knowledgeBaseEmbeddingVOs.stream().map(match -> match.getText()).collect(joining("\n\n"));

      // 最近k条历史聊天记录
      List<ChatMessage> chatBufferWindowList = chatMemory.messages();

      // 历史聊天记录向量搜索匹配
      List<EmbeddingVO> chatHistorySummaryEmbeddingVOs =
          vectorRetrievalBiz.retrievalBaseOnChatMemory(
              question,
              customerNo,
              copilotNo,
              customerCollection,
                 null,
              false);
      StringBuilder result = new StringBuilder();
      for (EmbeddingVO embeddingVO : chatHistorySummaryEmbeddingVOs) {
        if (StringUtils.isNotEmpty(embeddingVO.getText())) {
          result.append(embeddingVO.getText()).append("\n");
        }
      }
      String chatHistorySummary = result.toString();

      // 查找copilot对应的提示词模板
      if (promptTemplateNo == null) {
        promptTemplateNo = copilotInfo.getPromptTemplateNo();
      }
      List<ChatMessage> promptChatMessage =
          promptEngineeringBiz.generatePromptMessages(
              promptTemplateNo,
              knowledgeContent,
              chatBufferWindowList,
              chatHistorySummary,
              question,
              chatMemory,
              VectorStoreType.CHAT_HISTORY_SUMMARY);

      // 3. chat with LLM
      ChatLanguageModel chatModel = modelBiz.getChatModel(llmParam);
      // 开启了聊天总结的才需要总结
      if (historyChatSummaryEnable) {
        // 异步执行需要总结对话并存储在向量数据库
        if (((SummaryEmbeddingVectorStoreMemory) chatMemory).triggerSummaryMessages() != null) {
          // generate summary prompt
          String chatHistory = ((SummaryEmbeddingVectorStoreMemory) chatMemory).toString();
          UserMessage userMessage =
              promptEngineeringBiz.generateUserMessage(
                  "chatBufferWindowList", copilotInfo.getSummaryPromptTemplate(), chatHistory);
          chatSummaryAndStoreExecutorService.submit(
              new ChatHistorySummaryAndStoreBiz(
                  chatModel,
                  List.of(userMessage),
                  knowledgeBaseEmbeddingVOs.get(0).getEmbeddingModel(),
                  chatMemory,
                  vectorRetrievalBiz,
                  customerNo,
                  copilotNo,
                  customerCollection,
                  partitionName));
        }
      } else {
        // clear,防止内存爆掉
        chatMemory.cacheClear();
      }

      // 3.1.Send the prompt to the OpenAI chat model
      Response<AiMessage> response = chatModel.generate(promptChatMessage);
      log.info("promptChatMessage:{}", promptChatMessage.toString());
      TokenUsage tokenUsage = response.tokenUsage();
      AiMessage aiMessage = response.content();

      // 3.1.See an answer from the model
      String answer = aiMessage.text();
      chatMemory.add(aiMessage);

      // 用于历史聊天记录query
      if (historyChatSummaryEnable) {
        inRedisStore.rightPushMessage(
            "chat:",
            RedisChatMessage.newBuilder()
                .withSender(copilotNo)
                .withReceiver(customerNo)
                .withMessage(answer)
                .build());
      }

      if (quickReplies) {
        List<QuickReplyMessage> quickReplyMessages =
            chatBiz.generateQuickReplies(new ArrayList<String>(Arrays.asList(question, answer)), 3);
        requestMap.put("quickReplyMessages", quickReplyMessages);
      }
      // TODO: return chatVO
      requestMap.put("answer", answer);
      requestMap.put("tokenUsage", tokenUsage);
      requestMap.put("quotes", knowledgeBaseEmbeddingVOs);
      requestMap.put("prompt", promptEngineeringBiz.toPromptMessages(promptChatMessage)); // debug
      requestMap.put("knowledgeContent", knowledgeContent); // debug
      requestMap.put("chatHistorySummary", chatHistorySummary); // debug
      requestMap.put(
          "chatBufferWindow", promptEngineeringBiz.toPromptMessages(chatBufferWindowList)); // debug

    } catch (Exception e) {
      // 用于历史聊天记录query
      if (historyChatSummaryEnable) {
        inRedisStore.rightPushMessage(
            "chat:",
            RedisChatMessage.newBuilder()
                .withSender(copilotNo)
                .withReceiver(customerNo)
                .withMessage(e.toString())
                .build());
      }
      throw new BusinessException("100000", e.toString());
    }
    return RespBodyHandler.setRespBodyDto(requestMap);
  }

}
