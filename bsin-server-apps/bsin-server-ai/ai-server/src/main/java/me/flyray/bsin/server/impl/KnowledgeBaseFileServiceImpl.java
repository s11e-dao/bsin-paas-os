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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.AiCustomerFunction;
import me.flyray.bsin.domain.entity.EmbeddingModel;
import me.flyray.bsin.domain.entity.KnowledgeBase;
import me.flyray.bsin.domain.entity.KnowledgeBaseFile;
import me.flyray.bsin.domain.entity.KnowledgeBaseFileChunk;
import me.flyray.bsin.domain.enums.FileLoadType;
import me.flyray.bsin.domain.enums.VectorStoreType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.EmbeddingVO;
import me.flyray.bsin.facade.response.KnowledgeBaseFileVO;
import me.flyray.bsin.facade.service.KnowledgeBaseFileService;
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
import me.flyray.bsin.server.document.parser.DocumentParserFactory;
import me.flyray.bsin.server.milvus.BsinTextSegment;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author leonard
 * @description 针对表【ai_knowledge_base_file】的数据库操作Service实现
 * @createDate 2023-12-09 01:50:35
 */

@ShenyuDubboService(path = "/knowledgeBaseFile", timeout = 6000)
@ApiModule(value = "knowledgeBaseFile")
@Service
@Slf4j
public class KnowledgeBaseFileServiceImpl implements KnowledgeBaseFileService {

  @Value("${milvus.datasource.merchantCollectionName}")
  private String merchantCollectionName;

  @Value("${milvus.datasource.partitionName}")
  private String partitionNameName;

  @Autowired private KnowledgeBaseFileMapper knowledgeBaseFileMapper;
  @Autowired private KnowledgeBaseFileChunkMapper knowledgeBaseFileChunkMapper;
  @Autowired private KnowledgeBaseMapper knowledgeBaseMapper;
  @Autowired private EmbeddingModelMapper embeddingModelMapper;
  @Autowired private VectorRetrievalBiz vectorRetrievalBiz;
  @Autowired private ModelBiz modelBiz;
  @Autowired private DocumentBiz documentBiz;
  @Autowired private AccountAvailableResourcesBiz accountAvailableResourcesBiz;
  @Autowired private DocumentParserFactory documentParserFactory;

  /** 添加知识库文件: 1.加载文件: 文件为路径或者url 2.chunk 3.embedding 4.存储在向量数据库 5.在关系型数据库mysql插入一条记录 */
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
    String knowledgeBaseNo = MapUtils.getString(requestMap, "knowledgeBaseNo");

    if (knowledgeBaseNo == null) {
      throw new BusinessException("100000", "关联知识库ID为空！！！");
    }
    KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(knowledgeBaseNo);
    if (knowledgeBase == null) {
      throw new BusinessException("100000", "未查询到关联的知识库: " + knowledgeBaseNo);
    }

    AiCustomerFunction validFunction =
        accountAvailableResourcesBiz.functionServiceCheck(tenantId, merchantNo, customerNo);

    // 查询出当前客户已经创建的资源数量
    LambdaUpdateWrapper<KnowledgeBaseFile> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(KnowledgeBaseFile::getKnowledgeBaseNo, knowledgeBaseNo);
    List<KnowledgeBaseFile> knowledgeBaseFileList = knowledgeBaseFileMapper.selectList(wrapper);
    if (knowledgeBaseFileList.size() >= validFunction.getKnowledgeBaseFileNum()) {
      throw new BusinessException(
          "100000",
          "每个知识库可添加的知识库文件超过限制："
              + knowledgeBaseFileList.size()
              + ">="
              + validFunction.getKnowledgeBaseFileNum());
    }
    KnowledgeBaseFile knowledgeBaseFile =
        BsinServiceContext.getReqBodyDto(KnowledgeBaseFile.class, requestMap);
    try {
      // 1.非结构化文档加载
      Document document = documentBiz.loadDocument(knowledgeBaseFile);

      // 2.1 找到知识库的embeddingMode配置
      if (StringUtils.isEmpty(knowledgeBase.getEmbeddingModelNo())) {
        throw new BusinessException("100000", "知识库索引模型ID为空！！！");
      }
      EmbeddingModel embeddingModel =
          embeddingModelMapper.selectById(knowledgeBase.getEmbeddingModelNo());
      if (embeddingModel == null) {
        throw new BusinessException(
            "100000", "未查询到关联知识库的索引模型: " + knowledgeBase.getEmbeddingModelNo());
      }

      // 2.3 chunk
      DocumentSplitter splitter = modelBiz.getDocumentSplitter(embeddingModel);
      List<TextSegment> segments = splitter.split(document);

      // 2.4 选择embedding model 默认 AllMiniLmL6V2
      dev.langchain4j.model.embedding.EmbeddingModel langchin4jEmbeddingModel =
          modelBiz.getEmbeddingModel(embeddingModel);

      // 2.5 向量化
      List<Embedding> embeddings = langchin4jEmbeddingModel.embedAll(segments).content();

      String knowledgeBaseFileNo = BsinSnowflake.getId();
      knowledgeBaseFile.setSerialNo(knowledgeBaseFileNo);
      // 3 存储在向量库
      vectorRetrievalBiz.addDataToVector(
          embeddings,
          segments,
          merchantNo,
          VectorStoreType.KNOWLEDGE_BASE.getDesc(),
          knowledgeBaseNo,
          knowledgeBaseFileNo,
          merchantCollectionName,
          partitionNameName,
          embeddingModel.getDimension());
      knowledgeBaseFile.setChunkNum(embeddings.size());
      // 4. 存储mySQL
      knowledgeBaseFileMapper.insert(knowledgeBaseFile);
      KnowledgeBaseFileChunk knowledgeBaseFileChunk = new KnowledgeBaseFileChunk();
      knowledgeBaseFileChunk.setKnowledgeBaseFileNo(knowledgeBaseFile.getSerialNo());
      Integer chunkNoInt = 0;
      for (TextSegment segment : segments) {
        knowledgeBaseFileChunk.setChunkText(segment.text());
        // TODO: QA补充字段
        //        knowledgeBaseFileChunk.setChunkContent(segment.text());
        knowledgeBaseFileChunk.setChunkNo(knowledgeBaseFile.getSerialNo() + chunkNoInt.toString());
        knowledgeBaseFileChunkMapper.insert(knowledgeBaseFileChunk);
        chunkNoInt++;
      }

    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
    return RespBodyHandler.setRespBodyDto(knowledgeBaseFile);
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public Map<String, Object> delete(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String tenantId = loginUser.getTenantId();

    String knowledgeBaseFileNo = MapUtils.getString(requestMap, "serialNo");
    String chunkNo = MapUtils.getString(requestMap, "chunkNo");
    if (knowledgeBaseFileNo == null) {
      knowledgeBaseFileNo = MapUtils.getString(requestMap, "knowledgeBaseFileNo");
    }
    if (knowledgeBaseFileNo == null) {
      throw new BusinessException("100000", "知识库文件ID为空!!!");
    }
    try {
      vectorRetrievalBiz.delDataFromVector(
          knowledgeBaseFileNo, chunkNo, merchantCollectionName, partitionNameName);
      knowledgeBaseFileMapper.deleteById(knowledgeBaseFileNo);
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
    String tenantId = loginUser.getTenantId();

    String knowledgeBaseFileNo = MapUtils.getString(requestMap, "serialNo");
    if (knowledgeBaseFileNo == null) {
      knowledgeBaseFileNo = MapUtils.getString(requestMap, "knowledgeBaseFileNo");
    }

    String chunkNo = MapUtils.getString(requestMap, "chunkNo");
    KnowledgeBaseFile knowledgeBaseFile = knowledgeBaseFileMapper.selectById(knowledgeBaseFileNo);

    if (knowledgeBaseFile == null) {
      throw new BusinessException("100000", "未查询到知识库文件:" + knowledgeBaseFileNo);
    }
    KnowledgeBase knowledgeBase =
        knowledgeBaseMapper.selectById(knowledgeBaseFile.getKnowledgeBaseNo());
    if (knowledgeBase == null) {
      throw new BusinessException(
          "100000", "未查询到关联的知识库: " + knowledgeBaseFile.getKnowledgeBaseNo());
    }
    try {
      // 1.非结构化文档加载
      Document document = null;
      // url
      if (knowledgeBaseFile.getType().equals(FileLoadType.URL.getCode())) {
        if (StringUtils.isEmpty(knowledgeBaseFile.getFileUri())) {
          throw new BusinessException("100000", "知识库文件URI为空！！！");
        }
        document = UrlDocumentLoader.load(knowledgeBaseFile.getFileUri(), new TextDocumentParser());
      } else if (knowledgeBaseFile.getType().equals(FileLoadType.FILE.getCode())) {
        if (StringUtils.isEmpty(knowledgeBaseFile.getFileUri())) {
          throw new BusinessException("100000", "知识库文件路径为空！！！");
        }
        document =
            FileSystemDocumentLoader.loadDocument(
                knowledgeBaseFile.getFileUri(), new TextDocumentParser());
      } else if (knowledgeBaseFile.getType().equals(FileLoadType.WECHAT_MP.getCode())) {
        throw new BusinessException("100000", "暂不支持的知识库导入方式，敬请期待！！");
      } else {
        throw new BusinessException("100000", "暂不支持的知识库导入方式，敬请期待！！");
      }

      // 2.1 找到知识库的embeddingMode配置
      if (StringUtils.isEmpty(knowledgeBase.getEmbeddingModelNo())) {
        throw new BusinessException("100000", "知识库索引模型ID为空！！！");
      }
      EmbeddingModel embeddingModel =
          embeddingModelMapper.selectById(knowledgeBase.getEmbeddingModelNo());
      if (embeddingModel == null) {
        throw new BusinessException(
            "100000", "未查询到关联知识库的索引模型: " + knowledgeBase.getEmbeddingModelNo());
      }

      // 2.3、chunk
      DocumentSplitter splitter = modelBiz.getDocumentSplitter(embeddingModel);
      List<TextSegment> segments = splitter.split(document);

      // 2.4 选择embedding model 默认 AllMiniLmL6V2
      dev.langchain4j.model.embedding.EmbeddingModel langchin4jEmbeddingModel =
          modelBiz.getEmbeddingModel(embeddingModel);

      // 2.5 向量化
      List<Embedding> embeddings = langchin4jEmbeddingModel.embedAll(segments).content();

      // 3 更新向量库数据
      vectorRetrievalBiz.editDataFromVector(
          embeddings,
          segments,
          merchantNo,
          VectorStoreType.KNOWLEDGE_BASE.getDesc(),
          knowledgeBaseFile.getKnowledgeBaseNo(),
          knowledgeBaseFileNo,
          null,
          merchantCollectionName,
          partitionNameName,
          embeddingModel.getDimension());

      // 更新mySQL
      knowledgeBaseFileMapper.updateById(knowledgeBaseFile);
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }

    knowledgeBaseFileMapper.updateById(knowledgeBaseFile);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) throws Exception {
    String knowledgeBaseFileNo = MapUtils.getString(requestMap, "serialNo");
    if (knowledgeBaseFileNo == null) {
      knowledgeBaseFileNo = MapUtils.getString(requestMap, "knowledgeBaseFileNo");
    }
    String vectorRetStr = MapUtils.getString(requestMap, "vectorRet");
    boolean vectorRet = false;
    if (vectorRetStr != null) {
      vectorRet = Boolean.parseBoolean(vectorRetStr);
    }
    KnowledgeBaseFile knowledgeBaseFile = knowledgeBaseFileMapper.selectById(knowledgeBaseFileNo);
    KnowledgeBaseFileVO knowledgeBaseFileVO = new KnowledgeBaseFileVO();
    BeanUtil.copyProperties(knowledgeBaseFile, knowledgeBaseFileVO);

    List<BsinTextSegment> bsinTextSegments =
        vectorRetrievalBiz.queryDataFromVector(
            knowledgeBaseFileNo, merchantCollectionName, partitionNameName);

    List<EmbeddingVO> embeddingVOList = new ArrayList<EmbeddingVO>();
    for (BsinTextSegment bsinTextSegment : bsinTextSegments) {
      EmbeddingVO embeddingVO = new EmbeddingVO();
      embeddingVO.setText(bsinTextSegment.text());
      embeddingVO.setCustomerNo(bsinTextSegment.customerNo());
      embeddingVO.setAiNo(bsinTextSegment.aiNo());
      embeddingVO.setKnowledgeBaseFileNo(bsinTextSegment.knowledgeBaseFileNo());
      embeddingVO.setChunkNo(bsinTextSegment.chunkNo());
      if (vectorRet) {
        embeddingVO.setEmbeddingVector(bsinTextSegment.vector());
      }
      embeddingVOList.add(embeddingVO);
    }
    knowledgeBaseFileVO.setEmbeddings(embeddingVOList);

    return RespBodyHandler.setRespBodyDto(knowledgeBaseFileVO);
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
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String tenantId = loginUser.getTenantId();

    KnowledgeBaseFile knowledgeBaseFile =
        BsinServiceContext.getReqBodyDto(KnowledgeBaseFile.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<KnowledgeBaseFile> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<KnowledgeBaseFile> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(KnowledgeBaseFile::getCreateTime);
    wrapper.eq(
        StringUtils.isNotBlank(knowledgeBaseFile.getSerialNo()),
        KnowledgeBaseFile::getSerialNo,
        knowledgeBaseFile.getSerialNo());
    wrapper.eq(
            StringUtils.isNotBlank(knowledgeBaseFile.getKnowledgeBaseNo()),
        KnowledgeBaseFile::getKnowledgeBaseNo,
        knowledgeBaseFile.getKnowledgeBaseNo());
    wrapper.eq(
            StringUtils.isNotBlank(knowledgeBaseFile.getStatus()),
        KnowledgeBaseFile::getStatus,
        knowledgeBaseFile.getStatus());
    IPage<KnowledgeBaseFile> pageList = knowledgeBaseFileMapper.selectPage(page, wrapper);
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
    String tenantId = loginUser.getTenantId();

    KnowledgeBaseFile knowledgeBaseFile =
        BsinServiceContext.getReqBodyDto(KnowledgeBaseFile.class, requestMap);

    LambdaUpdateWrapper<KnowledgeBaseFile> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(KnowledgeBaseFile::getCreateTime);
    wrapper.eq(
            StringUtils.isNotBlank(knowledgeBaseFile.getSerialNo()),
        KnowledgeBaseFile::getSerialNo,
        knowledgeBaseFile.getSerialNo());
    wrapper.eq(
            StringUtils.isNotBlank(knowledgeBaseFile.getKnowledgeBaseNo()),
        KnowledgeBaseFile::getKnowledgeBaseNo,
        knowledgeBaseFile.getKnowledgeBaseNo());
    wrapper.eq(
            StringUtils.isNotBlank(knowledgeBaseFile.getType()),
        KnowledgeBaseFile::getType,
        knowledgeBaseFile.getType());
    wrapper.eq(
            StringUtils.isNotBlank(knowledgeBaseFile.getStatus()),
        KnowledgeBaseFile::getStatus,
        knowledgeBaseFile.getStatus());
    List<KnowledgeBaseFile> knowledgeBaseList = knowledgeBaseFileMapper.selectList(wrapper);
    return RespBodyHandler.setRespBodyListDto(knowledgeBaseList);
  }

}
