package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.domain.enums.RetrievalScope;
import me.flyray.bsin.domain.enums.VectorStoreType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.EmbeddingDTO;
import me.flyray.bsin.facade.response.EmbeddingVO;
import me.flyray.bsin.facade.response.KnowledgeBaseFileVO;
import me.flyray.bsin.facade.response.KnowledgeBaseVO;
import me.flyray.bsin.facade.service.KnowledgeBaseService;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.server.biz.AccountAvailableResourcesBiz;
import me.flyray.bsin.server.biz.ChatBiz;
import me.flyray.bsin.server.biz.VectorRetrievalBiz;
import me.flyray.bsin.server.milvus.BsinTextSegment;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
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

/**
 * @author leonard
 * @description 针对表【ai_knowledge_base】的数据库操作Service实现
 * @createDate 2023-12-09 01:50:35
 */

@ShenyuDubboService(path = "/knowledgeBase", timeout = 6000)
@ApiModule(value = "knowledgeBase")
@Service
@Slf4j
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

  @Value("${milvus.datasource.merchantCollectionName}")
  private String merchantCollectionName;

  @Value("${milvus.datasource.partitionName}")
  private String partitionNameName;

  @Autowired private KnowledgeBaseMapper knowledgeBaseMapper;
  @Autowired private KnowledgeBaseFileMapper knowledgeBaseFileMapper;
  @Autowired private EmbeddingModelMapper embeddingModelMapper;
  @Autowired private VectorRetrievalBiz vectorRetrievalBiz;
  @Autowired private PromptTemplateMapper promptTemplateMapper;
  @Autowired private LLMMapper llmMapper;
  @Autowired private AccountAvailableResourcesBiz accountAvailableResourcesBiz;
  @Autowired private ChatBiz chatBiz;

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public KnowledgeBase add(Map<String, Object> requestMap) {
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
    KnowledgeBase knowledgeBase = BsinServiceContext.getReqBodyDto(KnowledgeBase.class, requestMap);
    knowledgeBase.setSerialNo(BsinSnowflake.getId());
    knowledgeBase.setTenantId(tenantId);
    knowledgeBase.setMerchantNo(merchantNo);
    knowledgeBase.setCustomerNo(customerNo);

    AiCustomerFunction validFunction =
        accountAvailableResourcesBiz.functionServiceCheck(tenantId, merchantNo, customerNo);

    // 查询出当前客户已经创建的资源数量
    LambdaUpdateWrapper<KnowledgeBase> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(KnowledgeBase::getTenantId, tenantId);
    wrapper.eq(KnowledgeBase::getMerchantNo, merchantNo);
    wrapper.eq(KnowledgeBase::getCustomerNo, customerNo);
    List<KnowledgeBase> knowledgeBaseList = knowledgeBaseMapper.selectList(wrapper);
    if (knowledgeBaseList.size() >= validFunction.getKnowledgeBaseNum()) {
      throw new BusinessException(
          "100000",
          "可创建的知识库资源超过限制：" + knowledgeBaseList.size() + ">=" + validFunction.getKnowledgeBaseNum());
    }

    // 1.绑定大语言模型
    String llmName = MapUtils.getString(requestMap, "llmName");
    String llmType = MapUtils.getString(requestMap, "llmType");
    String llmApiKey = MapUtils.getString(requestMap, "llmApiKey");
    String llmApiBaseUri = MapUtils.getString(requestMap, "llmApiBaseUri");
    String llmDescription = MapUtils.getString(requestMap, "llmDescription");
    if (knowledgeBase.getLlmNo() == null) {
      String llmNo = BsinSnowflake.getId();
      LLMParam llmParam =
          LLMParam.newBuilder()
              .withSerialNo(llmNo)
              .withTenantId(tenantId)
              .withMerchantNo(merchantNo)
              .withCustomerNo(customerNo)
              .withName(llmName)
              .withType(llmType)
              .withApiKey(llmApiKey)
              .withApiBaseUrl(llmApiBaseUri)
              .withDescription(llmDescription)
              .build();
      llmMapper.insert(llmParam);
      knowledgeBase.setLlmNo(llmNo);
    }

    // 2.绑定提示词模板
    String promptTemplateSystemRole = MapUtils.getString(requestMap, "promptTemplateSystemRole");
    String promptTemplateDeterminer = MapUtils.getString(requestMap, "promptTemplateDeterminer");
    String promptTemplateKnowledgeBase =
        MapUtils.getString(requestMap, "promptTemplateKnowledgeBase");
    String promptTemplateChatHistorySummary =
        MapUtils.getString(requestMap, "promptTemplateChatHistorySummary");
    String promptTemplateChatBufferWindow =
        MapUtils.getString(requestMap, "promptTemplateChatBufferWindow");
    String promptTemplateQuestion = MapUtils.getString(requestMap, "promptTemplateQuestion");
    String promptTemplateDescription = MapUtils.getString(requestMap, "promptTemplateDescription");
    if (knowledgeBase.getPromptTemplateNo() == null) {
      String promptTemplateNo = BsinSnowflake.getId();
      PromptTemplateParam template =
          PromptTemplateParam.newBuilder()
              .withSerialNo(promptTemplateNo)
              .withTenantId(tenantId)
              .withMerchantNo(merchantNo)
              .withCustomerNo(customerNo)
              .withSystemRole(promptTemplateSystemRole)
              .withDeterminer(promptTemplateDeterminer)
              .withKnowledgeBase(promptTemplateKnowledgeBase)
              .withChatHistorySummary(promptTemplateChatHistorySummary)
              .withChatBufferWindow(promptTemplateChatBufferWindow)
              .withQuestion(promptTemplateQuestion)
              .withDescription(promptTemplateDescription)
              .build();
      promptTemplateMapper.insert(template);
      knowledgeBase.setEmbeddingModelNo(promptTemplateNo);
    }

    // 2.绑定索引模型
    String embeddingModeName = MapUtils.getString(requestMap, "embeddingModeName");
    String embeddingModeType = MapUtils.getString(requestMap, "embeddingModeType");
    String embeddingModeMinScore = MapUtils.getString(requestMap, "embeddingModeMinScore");
    String embeddingMaxResults = MapUtils.getString(requestMap, "embeddingMaxResults");
    String embeddingSegmentSizeInTokens =
        MapUtils.getString(requestMap, "embeddingSegmentSizeInTokens");
    String embeddingOverlapSizeInTokens =
        MapUtils.getString(requestMap, "embeddingOverlapSizeInTokens");
    String embeddingTokenizerModel = MapUtils.getString(requestMap, "embeddingTokenizerModel");
    if (knowledgeBase.getEmbeddingModelNo() == null) {
      String embeddingModeNo = BsinSnowflake.getId();
      EmbeddingModel embeddingModel =
          EmbeddingModel.newBuilder()
              .withSerialNo(embeddingModeNo)
              .withTenantId(tenantId)
              .withMerchantNo(merchantNo)
              .withCustomerNo(customerNo)
              .withName(embeddingModeName)
              .withType(embeddingModeType)
              .withCollectionName(merchantCollectionName)
              .withMaxResults(
                  embeddingMaxResults != null ? Integer.valueOf(embeddingMaxResults) : null)
              .withMinScore(
                  embeddingModeMinScore != null ? Double.valueOf(embeddingModeMinScore) : null)
              .withSegmentSizeInTokens(
                  embeddingSegmentSizeInTokens != null
                      ? Integer.valueOf(embeddingSegmentSizeInTokens)
                      : null)
              .withOverlapSizeInTokens(
                  embeddingOverlapSizeInTokens != null
                      ? Integer.valueOf(embeddingOverlapSizeInTokens)
                      : null)
              .withTokenizerModel(embeddingTokenizerModel)
              .build();
      embeddingModelMapper.insert(embeddingModel);
      knowledgeBase.setEmbeddingModelNo(embeddingModeNo);
    }
    knowledgeBaseMapper.insert(knowledgeBase);
    return knowledgeBase;
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public void delete(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    knowledgeBaseMapper.deleteById(serialNo);
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public void edit(Map<String, Object> requestMap) {
    KnowledgeBase knowledgeBase = BsinServiceContext.getReqBodyDto(KnowledgeBase.class, requestMap);
    if (Boolean.TRUE.equals(knowledgeBase.getDefaultFlag())) {
      setDefault(requestMap);
    }
    knowledgeBaseMapper.updateById(knowledgeBase);
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public KnowledgeBaseVO getDetail(Map<String, Object> requestMap) throws Exception {

    String knowledgeBaseNo = MapUtils.getString(requestMap, "serialNo");
    if (knowledgeBaseNo == null) {
      knowledgeBaseNo = MapUtils.getString(requestMap, "knowledgeBaseNo");
    }
    String vectorRetStr = MapUtils.getString(requestMap, "vectorRet");
    boolean vectorRet = false;
    if (vectorRetStr != null) {
      vectorRet = Boolean.parseBoolean(vectorRetStr);
    }
    String vectorFileStr = MapUtils.getString(requestMap, "vectorFile");
    boolean vectorFile = false;
    if (vectorFileStr != null) {
      vectorFile = Boolean.parseBoolean(vectorFileStr);
    }

    // 1.查找知识库基本信息
    KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(knowledgeBaseNo);
    KnowledgeBaseVO knowledgeBaseVO = new KnowledgeBaseVO();
    BeanUtil.copyProperties(knowledgeBase, knowledgeBaseVO);

    // 2.查找知识库的索引模型
    EmbeddingModel embeddingModel =
        embeddingModelMapper.selectById(knowledgeBase.getEmbeddingModelNo());
    knowledgeBaseVO.setEmbeddingModel(embeddingModel);

    // 3.查找知识下面的文件
    LambdaUpdateWrapper<KnowledgeBaseFile> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(KnowledgeBaseFile::getCreateTime);
    wrapper.eq(KnowledgeBaseFile::getKnowledgeBaseNo, knowledgeBaseNo);
    List<KnowledgeBaseFile> knowledgeBaseFileList = knowledgeBaseFileMapper.selectList(wrapper);
    List<KnowledgeBaseFileVO> knowledgeBaseFileVOList = new ArrayList<KnowledgeBaseFileVO>();
    for (KnowledgeBaseFile knowledgeBaseFile : knowledgeBaseFileList) {
      KnowledgeBaseFileVO vo = new KnowledgeBaseFileVO();
      BeanUtil.copyProperties(knowledgeBaseFile, vo);
      if (vectorFile) {
        List<BsinTextSegment> bsinTextSegments =
            vectorRetrievalBiz.queryDataFromVector(
                knowledgeBaseFile.getSerialNo(), merchantCollectionName, partitionNameName);
        List<EmbeddingVO> embeddingVOList = new ArrayList<EmbeddingVO>();
        for (BsinTextSegment bsinTextSegment : bsinTextSegments) {
          EmbeddingVO embeddingVO = new EmbeddingVO();
          embeddingVO.setText(bsinTextSegment.text());
          embeddingVO.setTenantId(bsinTextSegment.tenantId());
          embeddingVO.setAiNo(bsinTextSegment.aiNo());
          embeddingVO.setKnowledgeBaseFileNo(bsinTextSegment.knowledgeBaseFileNo());
          embeddingVO.setChunkNo(bsinTextSegment.chunkNo());
          if (vectorRet) {
            embeddingVO.setEmbeddingVector(bsinTextSegment.vector());
          }
          embeddingVOList.add(embeddingVO);
        }
        vo.setEmbeddings(embeddingVOList);
      }
      knowledgeBaseFileVOList.add(vo);
    }
    knowledgeBaseVO.setKnowledgeBaseFiles(knowledgeBaseFileVOList);
    return knowledgeBaseVO;
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public IPage<KnowledgeBase> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    String tenantId = loginUser.getTenantId();

    KnowledgeBase knowledgeBase = BsinServiceContext.getReqBodyDto(KnowledgeBase.class, requestMap);
    // 分页处理
    Object paginationObj =  requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj,pagination);

    Page<KnowledgeBase> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<KnowledgeBase> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(KnowledgeBase::getCreateTime);
    wrapper.eq(KnowledgeBase::getTenantId, tenantId);
    // 区分租户平台和商户登录情况判断
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        // 查询平台租户的数据
        wrapper.isNull(KnowledgeBase::getMerchantNo);
      }else {
        wrapper.eq(KnowledgeBase::getMerchantNo, merchantNo);
      }
    }
    wrapper.eq(StringUtils.isNotBlank(customerNo), KnowledgeBase::getCustomerNo, customerNo);
    wrapper.eq(
            StringUtils.isNotBlank(knowledgeBase.getSerialNo()),
        KnowledgeBase::getSerialNo,
        knowledgeBase.getSerialNo());
    wrapper.eq(
            StringUtils.isNotBlank(knowledgeBase.getType()),
        KnowledgeBase::getType,
        knowledgeBase.getType());
    wrapper.eq(
            StringUtils.isNotBlank(knowledgeBase.getStatus()),
        KnowledgeBase::getStatus,
        knowledgeBase.getStatus());
    // 匹配系统资源
    wrapper.or().eq(KnowledgeBase::getEditable, false);
    IPage<KnowledgeBase> pageList = knowledgeBaseMapper.selectPage(page, wrapper);
    return pageList;
  }

  @ApiDoc(desc = "getList")
  @ShenyuDubboClient("/getList")
  @Override
  public List<KnowledgeBase> getList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String bizRoleType = LoginInfoContextHelper.getBizRoleType();
    String bizRoleTypeNo = loginUser.getUserId();
    if(BizRoleType.TENANT.getCode().equals(bizRoleType)){
      bizRoleTypeNo = loginUser.getTenantId();
    }else if(BizRoleType.MERCHANT.getCode().equals(bizRoleType)){
      bizRoleTypeNo = loginUser.getMerchantNo();
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    String tenantId = loginUser.getTenantId();

    KnowledgeBase knowledgeBase = BsinServiceContext.getReqBodyDto(KnowledgeBase.class, requestMap);

    LambdaUpdateWrapper<KnowledgeBase> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(KnowledgeBase::getCreateTime);
    wrapper.eq(KnowledgeBase::getTenantId, tenantId);
    // TODO 商户号改成业务角色编号
    wrapper.eq(KnowledgeBase::getMerchantNo, bizRoleTypeNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), KnowledgeBase::getCustomerNo, customerNo);
    wrapper.eq(
            StringUtils.isNotBlank(knowledgeBase.getSerialNo()),
        KnowledgeBase::getSerialNo,
        knowledgeBase.getSerialNo());
    wrapper.eq(
            StringUtils.isNotBlank(knowledgeBase.getType()),
        KnowledgeBase::getType,
        knowledgeBase.getType());
    wrapper.eq(
            StringUtils.isNotBlank(knowledgeBase.getStatus()),
        KnowledgeBase::getStatus,
        knowledgeBase.getStatus());
    // 匹配系统资源
    wrapper.or().eq(KnowledgeBase::getEditable, false);
    List<KnowledgeBase> knowledgeBaseList = knowledgeBaseMapper.selectList(wrapper);
    return knowledgeBaseList;
  }

  @ApiDoc(desc = "getDefault")
  @ShenyuDubboClient("/getDefault")
  @Override
  public KnowledgeBase getDefault(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String type = MapUtils.getString(requestMap, "type");
    LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(KnowledgeBase::getTenantId, tenantId);
    wrapper.eq(StringUtils.isNotBlank(merchantNo), KnowledgeBase::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), KnowledgeBase::getCustomerNo, customerNo);
    wrapper.eq(StringUtils.isNotBlank(type), KnowledgeBase::getType, type);
    wrapper.eq(KnowledgeBase::getDefaultFlag, true);
    KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectOne(wrapper);
    return knowledgeBase;
  }

  @ApiDoc(desc = "setDefault")
  @ShenyuDubboClient("/setDefault")
  @Override
  public void setDefault(Map<String, Object> requestMap) {
    KnowledgeBase knowledgeBase = BsinServiceContext.getReqBodyDto(KnowledgeBase.class, requestMap);
    LambdaUpdateWrapper<KnowledgeBase> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    lambdaUpdateWrapper.set(KnowledgeBase::getDefaultFlag, knowledgeBase.getDefaultFlag());
    lambdaUpdateWrapper.eq(KnowledgeBase::getSerialNo, knowledgeBase.getSerialNo());
    if (Boolean.TRUE.equals(knowledgeBase.getDefaultFlag())) {
      knowledgeBaseMapper.update(
          null,
          new LambdaUpdateWrapper<>(KnowledgeBase.class)
              .set(KnowledgeBase::getDefaultFlag, Boolean.FALSE)
              .eq(
                      StringUtils.isNotBlank(knowledgeBase.getMerchantNo()),
                  KnowledgeBase::getMerchantNo,
                  knowledgeBase.getMerchantNo())
              .eq(
                      StringUtils.isNotBlank(knowledgeBase.getCustomerNo()),
                  KnowledgeBase::getCustomerNo,
                  knowledgeBase.getCustomerNo()));
    }
    knowledgeBaseMapper.update(null, lambdaUpdateWrapper);
  }
  /*
   * Retrieval Augmented Generation (RAG) based on 知识库|知识库文件.
   */
  @ApiDoc(desc = "retrieval")
  @ShenyuDubboClient("/retrieval")
  @Override
  public List<EmbeddingVO> retrieval(Map<String, Object> requestMap) {
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
      //      if (customerNo == null) {
      //        throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
      //      }
    }
    String tenantId = loginUser.getTenantId();
    String knowledgeBaseNo = MapUtils.getString(requestMap, "knowledgeBaseNo");
    String knowledgeBaseFileNo = MapUtils.getString(requestMap, "knowledgeBaseFileNo");
    String question = MapUtils.getString(requestMap, "text");
    String vectorRetStr = MapUtils.getString(requestMap, "vectorRet");
    String quickRepliesStr = MapUtils.getString(requestMap, "quickReplies");
    boolean vectorRet = false;
    if (vectorRetStr != null) {
      vectorRet = Boolean.parseBoolean(vectorRetStr);
    }
    EmbeddingDTO embeddingDTO = new EmbeddingDTO();
    BeanUtil.copyProperties(requestMap, embeddingDTO);
    embeddingDTO.setVectorRet(vectorRet);
    if (embeddingDTO.getCollectionName() == null) {
      embeddingDTO.setCollectionName(merchantCollectionName);
    }

    boolean quickReplies = false;
    if (quickRepliesStr != null) {
      quickReplies = Boolean.parseBoolean(quickRepliesStr);
    }

    embeddingDTO.setType(VectorStoreType.KNOWLEDGE_BASE.getDesc());

    if (embeddingDTO.getKnowledgeBaseFileNo().length() != 19) {
      // 检索整个知识库
      embeddingDTO.setRetrievalScope(RetrievalScope.KNOWLEDGE_BASE.getCode());
      embeddingDTO.setAiNo(embeddingDTO.getKnowledgeBaseNo());
    } else if (StringUtils.isNotEmpty(embeddingDTO.getKnowledgeBaseFileNo())) {
      // 检索具体某个知识库
      embeddingDTO.setRetrievalScope(RetrievalScope.KNOWLEDGE_BASE_FILE.getCode());
      embeddingDTO.setAiNo(embeddingDTO.getKnowledgeBaseFileNo());
    } else {
      throw new BusinessException("100000", "知识库库文件ID为空！！！");
    }
    if (embeddingDTO.getCustomerNo() == null) {
      embeddingDTO.setCustomerNo(customerNo);
    }
    try {
      List<EmbeddingVO> embeddingVOList = vectorRetrievalBiz.retrieval(embeddingDTO);

      if (quickReplies) {
        List<QuickReplyMessage> quickReplyMessages =
            chatBiz.generateQuickReplies(
                new ArrayList<String>(Arrays.asList(embeddingDTO.getText())), 3);
        requestMap.put("quickReplyMessages", quickReplyMessages);
      }
      return embeddingVOList;
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
  }

}
