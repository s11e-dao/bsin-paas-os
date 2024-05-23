package me.flyray.bsin.server.milvus;

import static dev.langchain4j.internal.Utils.isNullOrBlank;
import static java.util.stream.Collectors.toList;
import static me.flyray.bsin.server.milvus.BsinMilvusEmbeddingStore.*;
import static me.flyray.bsin.server.milvus.CollectionOperationsExecutor.queryForVectors;
import static me.flyray.bsin.server.milvus.Generator.generateEmptyScalars;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.RelevanceScore;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.exception.ParamException;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Mapper {

  static List<List<Float>> toVectors(List<Embedding> embeddings) {
    return embeddings.stream().map(Embedding::vectorAsList).collect(toList());
  }

  static List<String> toScalars(List<BsinTextSegment> textSegments, int size) {
    boolean noScalars = textSegments == null || textSegments.isEmpty();

    return noScalars ? generateEmptyScalars(size) : textSegmentsToScalars(textSegments);
  }

  static List<String> toCustomerNo(List<BsinTextSegment> textSegments, int size) {
    return textSegments.stream().map(BsinTextSegment::customerNo).collect(toList());
  }

  static List<String> toType(List<BsinTextSegment> textSegments, int size) {
    return textSegments.stream().map(BsinTextSegment::type).collect(toList());
  }

  static List<String> toKnowledgeBaseFileNo(List<BsinTextSegment> textSegments, int size) {
    return textSegments.stream().map(BsinTextSegment::knowledgeBaseFileNo).collect(toList());
  }

  static List<String> toChunkNo(List<BsinTextSegment> textSegments, int size) {
    return textSegments.stream().map(BsinTextSegment::chunkNo).collect(toList());
  }

  static List<String> toAiNo(List<BsinTextSegment> textSegments, int size) {
    return textSegments.stream().map(BsinTextSegment::aiNo).collect(toList());
  }

  static List<String> textSegmentsToScalars(List<BsinTextSegment> textSegments) {
    return textSegments.stream().map(BsinTextSegment::text).collect(toList());
  }

  static List<EmbeddingMatch<BsinTextSegment>> toEmbeddingMatches(
      MilvusServiceClient milvusClient,
      SearchResultsWrapper resultsWrapper,
      String collectionName,
      ConsistencyLevelEnum consistencyLevel,
      boolean queryForVectorOnSearch) {
    List<EmbeddingMatch<BsinTextSegment>> matches = new ArrayList<>();

    Map<String, Embedding> idToEmbedding = new HashMap<>();
    if (queryForVectorOnSearch) {
      try {
        List<String> rowIds =
            (List<String>) resultsWrapper.getFieldWrapper(ID_FIELD_NAME).getFieldData();
        idToEmbedding.putAll(
            queryEmbeddings(milvusClient, collectionName, rowIds, consistencyLevel));
      } catch (ParamException e) {
        // There is no way to check if the result is empty or not.
        // If the result is empty, the exception will be thrown.
      }
    }

    for (int i = 0; i < resultsWrapper.getRowRecords().size(); i++) {
      double score = resultsWrapper.getIDScore(0).get(i).getScore();
      String rowId = resultsWrapper.getIDScore(0).get(i).getStrID();
      Embedding embedding = idToEmbedding.get(rowId);
      String text = String.valueOf(resultsWrapper.getFieldData(TEXT_FIELD_NAME, 0).get(i));
      String customerNo =
          String.valueOf(resultsWrapper.getFieldData(CUSTOMER_NO_FIELD_NAME, 0).get(i));
      String aiNo = String.valueOf(resultsWrapper.getFieldData(AI_NO, 0).get(i));
      String type = String.valueOf(resultsWrapper.getFieldData(TYPE_FIELD_NAME, 0).get(i));
      String knowledgeBaseFileNo =
          String.valueOf(resultsWrapper.getFieldData(KNOWLEDGE_BASE_FILE_NO_FIELD_NAME, 0).get(i));
      String chunkNo =
          String.valueOf(resultsWrapper.getFieldData(CHUNK_NO_FIELD_NAME, 0).get(i));
      BsinTextSegment textSegment =
          isNullOrBlank(text)
              ? null
              : BsinTextSegment.fromBsin(
                  text, customerNo, type, aiNo, knowledgeBaseFileNo, chunkNo, null);
      EmbeddingMatch<BsinTextSegment> embeddingMatch =
          new EmbeddingMatch<>(
              RelevanceScore.fromCosineSimilarity(score), rowId, embedding, textSegment);
      matches.add(embeddingMatch);
    }

    return matches;
  }

  private static Map<String, Embedding> queryEmbeddings(
      MilvusServiceClient milvusClient,
      String collectionName,
      List<String> rowIds,
      ConsistencyLevelEnum consistencyLevel) {
    QueryResultsWrapper queryResultsWrapper =
        queryForVectors(milvusClient, collectionName, rowIds, consistencyLevel);

    Map<String, Embedding> idToEmbedding = new HashMap<>();
    for (QueryResultsWrapper.RowRecord row : queryResultsWrapper.getRowRecords()) {
      String id = row.get(ID_FIELD_NAME).toString();
      List<Float> vector = (List<Float>) row.get(VECTOR_FIELD_NAME);
      idToEmbedding.put(id, Embedding.from(vector));
    }

    return idToEmbedding;
  }
}
