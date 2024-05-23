package me.flyray.bsin.server.impl;

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

import java.util.List;
import java.util.Map;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.AiCustomerFunction;
import me.flyray.bsin.domain.domain.EmbeddingModel;
import me.flyray.bsin.domain.domain.KnowledgeBase;
import me.flyray.bsin.domain.domain.KnowledgeBaseFile;
import me.flyray.bsin.domain.domain.KnowledgeBaseFileChunk;
import me.flyray.bsin.domain.enums.VectorStoreType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.KnowledgeBaseFileChunkService;
import me.flyray.bsin.infrastructure.mapper.EmbeddingModelMapper;
import me.flyray.bsin.infrastructure.mapper.KnowledgeBaseFileChunkMapper;
import me.flyray.bsin.infrastructure.mapper.KnowledgeBaseFileMapper;
import me.flyray.bsin.infrastructure.mapper.KnowledgeBaseMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.biz.AccountAvailableResourcesBiz;
import me.flyray.bsin.server.biz.DocumentBiz;
import me.flyray.bsin.server.biz.ModelBiz;
import me.flyray.bsin.server.biz.VectorRetrievalBiz;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;

/**
 * @author leonard
 * @description 针对表【ai_knowledge_base_file_chunk】的数据库操作Service实现
 * @createDate 2024-02-03 14:50:35
 */

@ShenyuDubboService(path = "/knowledgeBaseFileChunk", timeout = 6000)
@ApiModule(value = "knowledgeBaseFileChunk")
@Service
@Slf4j
public class KnowledgeBaseFileChunkServiceImpl implements KnowledgeBaseFileChunkService {

  @Value("${milvus.datasource.merchantCollectionName}")
  private String merchantCollectionName;

  @Value("${milvus.datasource.partitionName}")
  private String partitionNameName;

  @Autowired private KnowledgeBaseFileChunkMapper knowledgeBaseFileChunkMapper;
  @Autowired private KnowledgeBaseFileMapper knowledgeBaseFileMapper;
  @Autowired private EmbeddingModelMapper embeddingModelMapper;
  @Autowired private AccountAvailableResourcesBiz accountAvailableResourcesBiz;
  @Autowired private KnowledgeBaseMapper knowledgeBaseMapper;
  @Autowired private VectorRetrievalBiz vectorRetrievalBiz;
  @Autowired private ModelBiz modelBiz;
  @Autowired private DocumentBiz documentBiz;

  /** 添加知识库文件片段 */
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
    String knowledgeBaseFileNo = MapUtils.getString(requestMap, "knowledgeBaseFileNo");

    if (knowledgeBaseFileNo == null) {
      throw new BusinessException("100000", "关联知识库文件ID为空！！！");
    }
    KnowledgeBaseFile knowledgeBaseFile = knowledgeBaseFileMapper.selectById(knowledgeBaseFileNo);
    if (knowledgeBaseFile == null) {
      throw new BusinessException("100000", "未查询到关联的知识库文件: " + knowledgeBaseFileNo);
    }
    AiCustomerFunction validFunction =
        accountAvailableResourcesBiz.functionServiceCheck(tenantId, merchantNo, customerNo);
    KnowledgeBaseFileChunk knowledgeBaseFileChunk =
        BsinServiceContext.getReqBodyDto(KnowledgeBaseFileChunk.class, requestMap);

    // 1、mysql查询
    LambdaUpdateWrapper<KnowledgeBaseFileChunk> wrapperChunk = new LambdaUpdateWrapper<>();
    wrapperChunk.eq(
        StringUtils.isNotBlank(knowledgeBaseFileChunk.getChunkNo()),
        KnowledgeBaseFileChunk::getChunkNo,
        knowledgeBaseFileChunk.getChunkNo());
    wrapperChunk.eq(
            StringUtils.isNotBlank(knowledgeBaseFileChunk.getKnowledgeBaseFileNo()),
        KnowledgeBaseFileChunk::getKnowledgeBaseFileNo,
        knowledgeBaseFileChunk.getKnowledgeBaseFileNo());
    // 删除的也查询
    wrapperChunk.or().eq(KnowledgeBaseFileChunk::getDelFlag, false);

    Long knowledgeBaseChunkCount = knowledgeBaseFileChunkMapper.selectCount(wrapperChunk);
    String chunkNo =
        knowledgeBaseFileChunk.getKnowledgeBaseFileNo() + String.valueOf(knowledgeBaseChunkCount);

    // 2、向量数据库插入
    String knowledgeBaseNo = MapUtils.getString(requestMap, "knowledgeBaseNo");
    String chunkText = MapUtils.getString(requestMap, "chunkText");
    if (knowledgeBaseNo == null) {
      throw new BusinessException("100000", "关联知识库ID为空！！！");
    }
    KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(knowledgeBaseNo);
    if (knowledgeBase == null) {
      throw new BusinessException("100000", "未查询到关联的知识库: " + knowledgeBaseNo);
    }
    try {
      EmbeddingModel embeddingModel =
          embeddingModelMapper.selectById(knowledgeBase.getEmbeddingModelNo());
      if (embeddingModel == null) {
        throw new BusinessException(
            "100000", "未查询到关联知识库的索引模型: " + knowledgeBase.getEmbeddingModelNo());
      }
      // 2.4 选择embedding model 默认 AllMiniLmL6V2
      dev.langchain4j.model.embedding.EmbeddingModel langchin4jEmbeddingModel =
          modelBiz.getEmbeddingModel(embeddingModel);

      // 2.5 向量化
      Embedding embedding = langchin4jEmbeddingModel.embed(chunkText).content();
      TextSegment segment = TextSegment.from(chunkText);
      // 3 存储在向量库
      vectorRetrievalBiz.addDataToVector(
          embedding,
          segment,
          merchantNo,
          VectorStoreType.KNOWLEDGE_BASE.getDesc(),
          knowledgeBaseNo,
          knowledgeBaseFile.getSerialNo(),
          chunkNo,
          merchantCollectionName,
          partitionNameName,
          embeddingModel.getDimension());
      knowledgeBaseFile.setChunkNum(knowledgeBaseFile.getChunkNum() + 1);
      // 4. 更新mySQL
      knowledgeBaseFileMapper.updateById(knowledgeBaseFile);
      knowledgeBaseFileChunk.setKnowledgeBaseFileNo(knowledgeBaseFile.getSerialNo());
      knowledgeBaseFileChunk.setChunkText(chunkText);
      // TODO: QA补充字段
      //        knowledgeBaseFileChunk.setChunkContent(segment.text());
      knowledgeBaseFileChunk.setChunkNo(chunkNo);
      knowledgeBaseFileChunkMapper.insert(knowledgeBaseFileChunk);
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }

    return RespBodyHandler.setRespBodyDto(knowledgeBaseFileChunk);
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public Map<String, Object> delete(Map<String, Object> requestMap) {
    String chunkNo = MapUtils.getString(requestMap, "chunkNo");
    if (chunkNo == null) {
      throw new BusinessException("100000", "知识库文件chunkNo为空!!!");
    }
    try {
      vectorRetrievalBiz.delDataFromVector(
          null, chunkNo, merchantCollectionName, partitionNameName);
      knowledgeBaseFileChunkMapper.deleteById(chunkNo);
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public Map<String, Object> edit(Map<String, Object> requestMap) {
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
    String knowledgeBaseFileNo = MapUtils.getString(requestMap, "knowledgeBaseFileNo");
    if (knowledgeBaseFileNo == null) {
      throw new BusinessException("100000", "关联知识库文件ID为空！！！");
    }

    String chunkNo = MapUtils.getString(requestMap, "chunkNo");
    if (chunkNo == null) {
      throw new BusinessException("100000", "chunkNo为空！！！");
    }

    KnowledgeBaseFile knowledgeBaseFile = knowledgeBaseFileMapper.selectById(knowledgeBaseFileNo);
    if (knowledgeBaseFile == null) {
      throw new BusinessException("100000", "未查询到关联的知识库文件: " + knowledgeBaseFileNo);
    }
    AiCustomerFunction validFunction =
        accountAvailableResourcesBiz.functionServiceCheck(tenantId, merchantNo, customerNo);
    KnowledgeBaseFileChunk knowledgeBaseFileChunk =
        BsinServiceContext.getReqBodyDto(KnowledgeBaseFileChunk.class, requestMap);

    // 2、向量数据库修改
    String knowledgeBaseNo = MapUtils.getString(requestMap, "knowledgeBaseNo");
    String chunkText = MapUtils.getString(requestMap, "chunkText");
    if (knowledgeBaseNo == null) {
      throw new BusinessException("100000", "关联知识库ID为空！！！");
    }
    KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(knowledgeBaseNo);
    if (knowledgeBase == null) {
      throw new BusinessException("100000", "未查询到关联的知识库: " + knowledgeBaseNo);
    }
    try {
      EmbeddingModel embeddingModel =
          embeddingModelMapper.selectById(knowledgeBase.getEmbeddingModelNo());
      if (embeddingModel == null) {
        throw new BusinessException(
            "100000", "未查询到关联知识库的索引模型: " + knowledgeBase.getEmbeddingModelNo());
      }
      // 选择embedding model 默认 AllMiniLmL6V2
      dev.langchain4j.model.embedding.EmbeddingModel langchin4jEmbeddingModel =
          modelBiz.getEmbeddingModel(embeddingModel);

      // 向量化
      Embedding embedding = langchin4jEmbeddingModel.embed(chunkText).content();
      TextSegment segment = TextSegment.from(chunkText);
      vectorRetrievalBiz.editDataFromVector(
          embedding,
          segment,
          merchantNo,
          VectorStoreType.KNOWLEDGE_BASE.getDesc(),
          knowledgeBaseNo,
          knowledgeBaseFileNo,
          chunkNo,
          merchantCollectionName,
          partitionNameName,
          embeddingModel.getDimension());
      knowledgeBaseFileChunkMapper.updateById(knowledgeBaseFileChunk);
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) throws Exception {
    String chunkNo = MapUtils.getString(requestMap, "chunkNo");
    if (chunkNo == null) {
      throw new BusinessException("100000", "知识库文件chunkNo为空!!!");
    }
    String vectorRetStr = MapUtils.getString(requestMap, "vectorRet");
    boolean vectorRet = false;
    if (vectorRetStr != null) {
      vectorRet = Boolean.parseBoolean(vectorRetStr);
    }
    KnowledgeBaseFileChunk knowledgeBaseFileChunk =
        knowledgeBaseFileChunkMapper.selectById(chunkNo);

    return RespBodyHandler.setRespBodyDto(knowledgeBaseFileChunk);
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {

    KnowledgeBaseFileChunk knowledgeBaseFileChunk =
        BsinServiceContext.getReqBodyDto(KnowledgeBaseFileChunk.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<KnowledgeBaseFileChunk> page =
        new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<KnowledgeBaseFileChunk> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(
        StringUtils.isNotBlank(knowledgeBaseFileChunk.getChunkNo()),
        KnowledgeBaseFileChunk::getChunkNo,
        knowledgeBaseFileChunk.getChunkNo());
    wrapper.eq(
        StringUtils.isNotBlank(knowledgeBaseFileChunk.getKnowledgeBaseFileNo()),
        KnowledgeBaseFileChunk::getKnowledgeBaseFileNo,
        knowledgeBaseFileChunk.getKnowledgeBaseFileNo());
    IPage<KnowledgeBaseFileChunk> pageList = knowledgeBaseFileChunkMapper.selectPage(page, wrapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  @ApiDoc(desc = "getList")
  @ShenyuDubboClient("/getList")
  @Override
  public Map<String, Object> getList(Map<String, Object> requestMap) {
    KnowledgeBaseFileChunk knowledgeBaseFileChunk =
        BsinServiceContext.getReqBodyDto(KnowledgeBaseFileChunk.class, requestMap);
    LambdaUpdateWrapper<KnowledgeBaseFileChunk> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(
            StringUtils.isNotBlank(knowledgeBaseFileChunk.getChunkNo()),
        KnowledgeBaseFileChunk::getChunkNo,
        knowledgeBaseFileChunk.getChunkNo());
    wrapper.eq(
        StringUtils.isNotBlank(knowledgeBaseFileChunk.getKnowledgeBaseFileNo()),
        KnowledgeBaseFileChunk::getKnowledgeBaseFileNo,
        knowledgeBaseFileChunk.getKnowledgeBaseFileNo());
    List<KnowledgeBaseFileChunk> knowledgeBaseChunkList =
        knowledgeBaseFileChunkMapper.selectList(wrapper);
    return RespBodyHandler.setRespBodyListDto(knowledgeBaseChunkList);
  }

}
