package me.flyray.bsin.server.biz;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.MutationResult;
import io.milvus.param.R;
import me.flyray.bsin.domain.entity.CopilotInfo;
import me.flyray.bsin.domain.entity.EmbeddingModel;
import me.flyray.bsin.domain.entity.KnowledgeBase;
import me.flyray.bsin.domain.entity.RedisChatMessage;
import me.flyray.bsin.domain.enums.RetrievalScope;
import me.flyray.bsin.domain.enums.VectorStoreType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.EmbeddingDTO;
import me.flyray.bsin.facade.response.EmbeddingVO;
import me.flyray.bsin.infrastructure.mapper.CopilotMapper;
import me.flyray.bsin.infrastructure.mapper.EmbeddingModelMapper;
import me.flyray.bsin.infrastructure.mapper.KnowledgeBaseFileMapper;
import me.flyray.bsin.infrastructure.mapper.KnowledgeBaseMapper;
import me.flyray.bsin.server.memory.store.InRedisStore;
import me.flyray.bsin.server.milvus.BsinMilvusEmbeddingStore;
import me.flyray.bsin.server.milvus.BsinTextSegment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static me.flyray.bsin.server.milvus.BsinMilvusEmbeddingStore.*;

@Component
public class VectorRetrievalBiz {

  @Value("${milvus.datasource.host}")
  private String milvusHost;

  @Value("${milvus.datasource.port}")
  private Integer milvusPort;

  @Value("${milvus.datasource.port}")
  private Integer dimension;

  @Autowired private KnowledgeBaseMapper knowledgeBaseMapper;
  @Autowired private CopilotMapper copilotMapper;

  @Autowired private KnowledgeBaseFileMapper knowledgeBaseFileMapper;

  @Autowired private EmbeddingModelMapper embeddingModelMapper;

  @Autowired private ModelBiz modelBiz;
  @Autowired private InRedisStore inRedisStore; // 用于缓存历史聊天记录

  public List<String> addDataToVector(
      List<Embedding> embeddings,
      List<TextSegment> segments,
      String merchantNo,
      String type,
      String aiNo,
      String knowledgeBaseFileNo,
      String collectionName,
      String partitionNameName,
      Integer dimension) {
    List<BsinTextSegment> bsinSegments = new ArrayList<>();
    Integer chunkNoInt = 0;
    for (TextSegment segment : segments) {
      BsinTextSegment bsinTextSegment =
          new BsinTextSegment(
              segment,
              merchantNo,
              type,
              aiNo,
              knowledgeBaseFileNo,
              knowledgeBaseFileNo + chunkNoInt.toString(),
              null);
      bsinSegments.add(bsinTextSegment);
      chunkNoInt++;
    }
    EmbeddingStore<BsinTextSegment> bsinMilvusEmbeddingStore =
        BsinMilvusEmbeddingStore.builder()
            .host(milvusHost)
            .port(milvusPort)
            .collectionName(collectionName)
            //            .partitionName(partitionNameName)
            .consistencyLevel(ConsistencyLevelEnum.BOUNDED)
            .dimension(dimension)
            .build();
    return bsinMilvusEmbeddingStore.addAll(embeddings, bsinSegments);
  }

  public String addDataToVector(
      Embedding embedding,
      TextSegment segment,
      String merchantNo,
      String type,
      String aiNo,
      String knowledgeBaseFileNo,
      String chunkNo,
      String collectionName,
      String partitionNameName,
      Integer dimension) {
    BsinTextSegment bsinTextSegment =
        new BsinTextSegment(segment, merchantNo, type, aiNo, knowledgeBaseFileNo, chunkNo, null);

    EmbeddingStore<BsinTextSegment> bsinMilvusEmbeddingStore =
        BsinMilvusEmbeddingStore.builder()
            .host(milvusHost)
            .port(milvusPort)
            .collectionName(collectionName)
            //            .partitionName(partitionNameName)
            .consistencyLevel(ConsistencyLevelEnum.BOUNDED)
            .dimension(dimension)
            .build();
    return bsinMilvusEmbeddingStore.add(embedding, bsinTextSegment);
  }

  /**
   * @param memoryId
   * @return ChatMessage String {@link SystemMessage}
   * @throws Exception
   */
  public String getChatMessagesFromVector(
      String memoryId, String collectionName, String partitionNameName) throws Exception {
    EmbeddingStore<BsinTextSegment> bsinMilvusEmbeddingStore =
        BsinMilvusEmbeddingStore.builder()
            .host(milvusHost)
            .port(milvusPort)
            .collectionName(collectionName)
            //            .partitionName(partitionNameName)
            .consistencyLevel(ConsistencyLevelEnum.BOUNDED)
            .dimension(dimension)
            .build();
    String expr =
        TYPE_FIELD_NAME
            + " == '"
            + VectorStoreType.CHAT_HISTORY.getDesc()
            + "' && "
            + ID_FIELD_NAME
            + " == '"
            + memoryId
            + "'";
    List<BsinTextSegment> segments =
        ((BsinMilvusEmbeddingStore) bsinMilvusEmbeddingStore).queryDataWithExp(expr);
    if (segments.size() > 0) {
      return segments.get(0).text();
    } else {
      return null;
      //      throw new BusinessException("100000", "未查询到消息ID:" + memoryId);
    }
  }

  /**
   * @param memoryId
   * @return ChatMessage String {@link SystemMessage}
   * @throws Exception
   */
  public String updateChatMessagesFromVector(
      String memoryId, String text, String collectionName, String partitionNameName)
      throws Exception {
    EmbeddingStore<BsinTextSegment> bsinMilvusEmbeddingStore =
        BsinMilvusEmbeddingStore.builder()
            .host(milvusHost)
            .port(milvusPort)
            .collectionName(collectionName)
            //            .partitionName(partitionNameName)
            .consistencyLevel(ConsistencyLevelEnum.BOUNDED)
            .build();
    String expr =
        TYPE_FIELD_NAME
            + " == '"
            + VectorStoreType.CHAT_HISTORY.getDesc()
            + "' && "
            + ID_FIELD_NAME
            + " == '"
            + memoryId
            + "'";
    List<BsinTextSegment> segments =
        ((BsinMilvusEmbeddingStore) bsinMilvusEmbeddingStore).queryDataWithExp(expr);
    if (segments.size() > 0) {
      return segments.get(0).text();
    } else {
      return null;
    }
  }

  /**
   * @param memoryId
   * @return ChatMessage String {@link SystemMessage}
   * @throws Exception
   */
  public R<MutationResult> delChatMessagesFromVector(
      String memoryId, String collectionName, String partitionNameName) throws Exception {
    EmbeddingStore<BsinTextSegment> bsinMilvusEmbeddingStore =
        BsinMilvusEmbeddingStore.builder()
            .host(milvusHost)
            .port(milvusPort)
            .collectionName(collectionName)
            //            .partitionName(partitionNameName)
            .consistencyLevel(ConsistencyLevelEnum.BOUNDED)
            .build();
    String expr = ID_FIELD_NAME + " in [" + memoryId + "]";
    return ((BsinMilvusEmbeddingStore) bsinMilvusEmbeddingStore).delDataWithPrimaryKeyExp(expr);
  }

  public List<BsinTextSegment> queryDataFromVector(
      String knowledgeBaseFileNo, String collectionName, String partitionNameName)
      throws Exception {
    EmbeddingStore<BsinTextSegment> bsinMilvusEmbeddingStore =
        BsinMilvusEmbeddingStore.builder()
            .host(milvusHost)
            .port(milvusPort)
            .collectionName(collectionName)
            //            .partitionName(partitionNameName)
            .consistencyLevel(ConsistencyLevelEnum.BOUNDED)
            .build();
    String expr = KNOWLEDGE_BASE_FILE_NO_FIELD_NAME + " == '" + knowledgeBaseFileNo + "'";
    return ((BsinMilvusEmbeddingStore) bsinMilvusEmbeddingStore).queryDataWithExp(expr);
  }

  public R<MutationResult> delDataFromVector(
      String knowledgeBaseFileNo, String chunkNo, String collectionName, String partitionNameName)
      throws Exception {
    EmbeddingStore<BsinTextSegment> bsinMilvusEmbeddingStore =
        BsinMilvusEmbeddingStore.builder()
            .host(milvusHost)
            .port(milvusPort)
            .collectionName(collectionName)
            //            .partitionName(partitionNameName)
            .consistencyLevel(
                ConsistencyLevelEnum
                    .BOUNDED) // Deleting entities by complex boolean expressions is supported
            // only when the consistency is set to Bounded. For details, see
            // Consistency.
            .build();
    String expr = "";
    if (knowledgeBaseFileNo != null) {
      expr = KNOWLEDGE_BASE_FILE_NO_FIELD_NAME + " == '" + knowledgeBaseFileNo + "'";
      if (chunkNo != null) {
        expr += " && " + CHUNK_NO_FIELD_NAME + " == '" + chunkNo + "'";
      }
    } else {
      if (chunkNo != null) {
        expr = CHUNK_NO_FIELD_NAME + " == '" + chunkNo + "'";
      }
    }
    //      String expr = ID_FIELD_NAME + " in ['1733895343499776000'";   // 测试法相只能通过主键
    // in的方式删除-->先检索，再删除
    return ((BsinMilvusEmbeddingStore) bsinMilvusEmbeddingStore).delDataWithExp(expr);
  }

  public List<String> editDataFromVector(
      List<Embedding> embeddings,
      List<TextSegment> segments,
      String merchantNo,
      String type,
      String aiNo,
      String knowledgeBaseFileNo,
      String chunkNo,
      String collectionName,
      String partitionNameName,
      Integer dimension)
      throws Exception {

    List<BsinTextSegment> bsinSegments = new ArrayList<>();
    for (TextSegment segment : segments) {
      BsinTextSegment bsinTextSegment =
          new BsinTextSegment(segment, merchantNo, type, aiNo, knowledgeBaseFileNo, chunkNo, null);
      bsinSegments.add(bsinTextSegment);
    }

    EmbeddingStore<BsinTextSegment> bsinMilvusEmbeddingStore =
        BsinMilvusEmbeddingStore.builder()
            .host(milvusHost)
            .port(milvusPort)
            .collectionName(collectionName)
            //            .partitionName(partitionNameName)
            .consistencyLevel(
                ConsistencyLevelEnum
                    .BOUNDED) // Deleting entities by complex boolean expressions is supported
            // only when the consistency is set to Bounded. For details, see
            // Consistency.
            .dimension(dimension)
            .build();

    String expr = "";
    if (knowledgeBaseFileNo != null) {
      expr = KNOWLEDGE_BASE_FILE_NO_FIELD_NAME + " == '" + knowledgeBaseFileNo + "'";
      if (chunkNo != null) {
        expr += " && " + CHUNK_NO_FIELD_NAME + " == '" + chunkNo + "'";
      }
    } else {
      if (chunkNo != null) {
        expr = CHUNK_NO_FIELD_NAME + " == '" + chunkNo + "'";
      }
    }
    return ((BsinMilvusEmbeddingStore) bsinMilvusEmbeddingStore)
        .updateDataWithExp(expr, embeddings, bsinSegments);
  }

  public String editDataFromVector(
      Embedding embedding,
      TextSegment segment,
      String merchantNo,
      String type,
      String aiNo,
      String knowledgeBaseFileNo,
      String chunkNo,
      String collectionName,
      String partitionNameName,
      Integer dimension)
      throws Exception {

    BsinTextSegment bsinTextSegment =
        new BsinTextSegment(segment, merchantNo, type, aiNo, knowledgeBaseFileNo, chunkNo, null);

    EmbeddingStore<BsinTextSegment> bsinMilvusEmbeddingStore =
        BsinMilvusEmbeddingStore.builder()
            .host(milvusHost)
            .port(milvusPort)
            .collectionName(collectionName)
            //            .partitionName(partitionNameName)
            .consistencyLevel(
                ConsistencyLevelEnum
                    .BOUNDED) // Deleting entities by complex boolean expressions is supported
            // only when the consistency is set to Bounded. For details, see
            // Consistency.
            .dimension(dimension)
            .build();

    String expr = "";
    if (knowledgeBaseFileNo != null) {
      expr = KNOWLEDGE_BASE_FILE_NO_FIELD_NAME + " == '" + knowledgeBaseFileNo + "'";
      if (chunkNo != null) {
        expr += " && " + CHUNK_NO_FIELD_NAME + " == '" + chunkNo + "'";
      }
    } else {
      if (chunkNo != null) {
        expr = CHUNK_NO_FIELD_NAME + " == '" + chunkNo + "'";
      }
    }
    return ((BsinMilvusEmbeddingStore) bsinMilvusEmbeddingStore)
        .updateDataWithExp(expr, embedding, bsinTextSegment);
  }

  public List<EmbeddingVO> retrievalBaseOnKnowledgeBase(
      String text,
      String merchantNo,
      String knowledgeBaseNo,
      String collectionName,
      boolean vectorRet) {
    EmbeddingDTO embeddingDTO = new EmbeddingDTO();
    embeddingDTO.setVectorRet(vectorRet);
    embeddingDTO.setKnowledgeBaseNo(knowledgeBaseNo);
    embeddingDTO.setCollectionName(collectionName);
    embeddingDTO.setText(text);
    embeddingDTO.setRetrievalScope(RetrievalScope.KNOWLEDGE_BASE.getCode());
    //    embeddingDTO.setCustomerNo(customerNo);
    embeddingDTO.setMerchantNo(merchantNo);
    return retrieval(embeddingDTO);
  }

  public List<EmbeddingVO> retrievalBaseOnChatMemory(
      String text,
      String customerNo,
      String copilotNo,
      String collectionName,
      String embeddingModelNo,
      boolean vectorRet) {
    EmbeddingDTO embeddingDTO = new EmbeddingDTO();
    embeddingDTO.setVectorRet(vectorRet);
    embeddingDTO.setAiNo(copilotNo);

    if (copilotNo != null) {
      CopilotInfo copilotInfo = copilotMapper.selectById(copilotNo);
      embeddingDTO.setKnowledgeBaseNo(copilotInfo.getKnowledgeBaseNo());
    }
    embeddingDTO.setCollectionName(collectionName);
    embeddingDTO.setText(text);
    embeddingDTO.setRetrievalScope(RetrievalScope.CHAT_CONTEX.getCode());
    embeddingDTO.setCustomerNo(customerNo);
    embeddingDTO.setEmbeddingModelNo(embeddingModelNo);
    return retrieval(embeddingDTO);
  }

  public List<EmbeddingVO> retrieval(EmbeddingDTO embeddingDTO) {

    // 1.retrieval:记录存储在redis
    inRedisStore.rightPushMessage(
        "retrieval:",
        RedisChatMessage.newBuilder()
            .withSender(embeddingDTO.getCustomerNo())
            .withReceiver(embeddingDTO.getAiNo())
            .withMessage(embeddingDTO.getText())
            .build());
    String customerNo = embeddingDTO.getCustomerNo();
    KnowledgeBase knowledgeBase = null;
    if (embeddingDTO.getKnowledgeBaseNo() != null) {
      knowledgeBase = knowledgeBaseMapper.selectById(embeddingDTO.getKnowledgeBaseNo());
    }
    EmbeddingModel embeddingModel = null;

    String embeddingModelNo = embeddingDTO.getEmbeddingModelNo();

    if (embeddingModelNo == null && knowledgeBase != null) {
      embeddingModelNo = knowledgeBase.getEmbeddingModelNo();
    }
    embeddingModel = embeddingModelMapper.selectById(embeddingModelNo);
    if (embeddingModel == null) {
      throw new BusinessException("100000", "未查询到关联的索引模型:" + embeddingModelNo);
    }

    dev.langchain4j.model.embedding.EmbeddingModel langchin4jEmbeddingModel =
        modelBiz.getEmbeddingModel(embeddingModel);

    Embedding questionEmbedding = langchin4jEmbeddingModel.embed(embeddingDTO.getText()).content();

    int maxResults = 3;
    if (embeddingDTO.getMaxResults() == null) {
      if (embeddingModel == null) {
        throw new BusinessException("100000", "未查询到关联的索引模型！！");
      }
      maxResults = embeddingModel.getMaxResults().intValue();
    } else {
      maxResults = embeddingDTO.getMaxResults().intValue();
    }

    double minScore = 0.7;
    if (embeddingDTO.getMinScore() == null) {
      if (embeddingModel == null) {
        throw new BusinessException("100000", "未查询到关联的索引模型！！");
      }
      minScore = embeddingModel.getMinScore().doubleValue();
    } else {
      minScore = embeddingDTO.getMinScore().doubleValue();
    }

    int dimension = 384;
    if (embeddingDTO.getDimension() == null) {
      if (embeddingModel == null) {
        throw new BusinessException("100000", "未查询到关联的索引模型！！");
      }
      dimension = embeddingModel.getDimension().intValue();
    } else {
      dimension = embeddingDTO.getDimension().intValue();
    }

    EmbeddingStore<BsinTextSegment> bsinMilvusEmbeddingStore =
        BsinMilvusEmbeddingStore.builder()
            .host(milvusHost)
            .port(milvusPort)
            .collectionName(embeddingDTO.getCollectionName())
            //            .partitionName(embeddingDTO.getPartitionName())
            .dimension(dimension)
            .build();

    if (RetrievalScope.KNOWLEDGE_BASE.getCode().equals(embeddingDTO.getRetrievalScope())) {
      if (embeddingDTO.getKnowledgeBaseNo() == null) {
        throw new BusinessException("100000", "知识库ID为空！！！");
      }
      embeddingDTO.setCustomerNo(null);
      embeddingDTO.setKnowledgeBaseFileNo(null);
    } else if (RetrievalScope.KNOWLEDGE_BASE_FILE
        .getCode()
        .equals(embeddingDTO.getRetrievalScope())) {
      if (embeddingDTO.getKnowledgeBaseFileNo() == null) {
        throw new BusinessException("100000", "知识库文件ID为空！！！");
      }
      embeddingDTO.setKnowledgeBaseNo(null);
      embeddingDTO.setCustomerNo(null);
      embeddingDTO.setType(VectorStoreType.KNOWLEDGE_BASE.getDesc());
    } else if (RetrievalScope.CHAT_CONTEX.getCode().equals(embeddingDTO.getRetrievalScope())) {
      embeddingDTO.setKnowledgeBaseNo(embeddingDTO.getAiNo()); // 复用knowledgeNo
      embeddingDTO.setKnowledgeBaseFileNo(null);
      embeddingDTO.setType(VectorStoreType.CHAT_HISTORY_SUMMARY.getDesc());
    } else if (RetrievalScope.CHAT_CONTEX_WITH_KNOWLEDGE_BASE
        .getCode()
        .equals(embeddingDTO.getRetrievalScope())) {
      embeddingDTO.setKnowledgeBaseNo(null);
      embeddingDTO.setKnowledgeBaseFileNo(null);
      embeddingDTO.setType(null);
    }

    List<EmbeddingMatch<BsinTextSegment>> relevantEmbeddings =
        ((BsinMilvusEmbeddingStore) bsinMilvusEmbeddingStore)
            .bsinFindRelevant(
                questionEmbedding,
                maxResults,
                minScore,
                embeddingDTO.getCustomerNo(),
                embeddingDTO.getType(),
                embeddingDTO.getKnowledgeBaseNo(),
                embeddingDTO.getKnowledgeBaseFileNo(),
                embeddingDTO.getChunkNo());

    List<EmbeddingVO> embeddingVOList = new ArrayList<EmbeddingVO>();
    String retrievalResult = "";
    for (EmbeddingMatch<BsinTextSegment> relevantEmbedding : relevantEmbeddings) {
      EmbeddingVO embeddingVO = new EmbeddingVO();
      embeddingVO.setPromptTemplateNo(knowledgeBase.getPromptTemplateNo());
      embeddingVO.setLlmNo(knowledgeBase.getLlmNo());
      embeddingVO.setScore(relevantEmbedding.score());
      //      embeddingVO.setEmbeddingModel(embeddingModel);
      embeddingVO.setMetadata(relevantEmbedding.embedded().metadata());
      embeddingVO.setText(relevantEmbedding.embedded().text());
      embeddingVO.setEmbeddingId(relevantEmbedding.embeddingId());
      embeddingVO.setCustomerNo(relevantEmbedding.embedded().customerNo());
      embeddingVO.setAiNo(relevantEmbedding.embedded().aiNo());
      embeddingVO.setKnowledgeBaseFileNo(relevantEmbedding.embedded().knowledgeBaseFileNo());
      embeddingVO.setChunkNo(relevantEmbedding.embedded().chunkNo());
      if (embeddingDTO.isVectorRet()) {
        embeddingVO.setEmbeddingVector(relevantEmbedding.embedding().vector());
      }
      retrievalResult +=
          "score: "
              + String.format("%.3f", embeddingVO.getScore())
              + "    召回文本:  "
              + embeddingVO.getText()
              + "\n";
      embeddingVOList.add(embeddingVO);
    }
    if (embeddingVOList.size() == 0) {
      EmbeddingVO embeddingVO = new EmbeddingVO();
      embeddingVO.setText(embeddingModel.getEmptyResp());
      embeddingVO.setPromptTemplateNo(knowledgeBase.getPromptTemplateNo());
      embeddingVO.setLlmNo(knowledgeBase.getLlmNo());
      //      embeddingVO.setEmbeddingModel(embeddingModel);
      embeddingVOList.add(embeddingVO);
    }
    embeddingVOList.get(0).setEmbeddingModel(embeddingModel);

    inRedisStore.rightPushMessage(
        "retrieval:",
        RedisChatMessage.newBuilder()
            .withReceiver(customerNo)
            .withSender(embeddingDTO.getAiNo())
            .withMessage(retrievalResult)
            .build());
    return embeddingVOList;
  }
}
