package me.flyray.bsin.server.milvus;

import static dev.langchain4j.internal.Utils.isNullOrBlank;
import static java.util.stream.Collectors.toList;
import static me.flyray.bsin.server.milvus.BsinMilvusEmbeddingStore.*;
import static me.flyray.bsin.server.milvus.BsinMilvusEmbeddingStore.ID_FIELD_NAME;
import static me.flyray.bsin.server.milvus.BsinMilvusEmbeddingStore.TEXT_FIELD_NAME;
import static me.flyray.bsin.server.milvus.BsinMilvusEmbeddingStore.VECTOR_FIELD_NAME;
import static me.flyray.bsin.server.milvus.BsinOsOperateCodeEmbeddingStore.*;
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

  static <T> List<List<Float>> toVectors(List<Embedding> embeddings) {
    return embeddings.stream().map(Embedding::vectorAsList).collect(toList());
  }

  static <T> List<String> toScalars(List<T> textSegments, int size) {
    boolean noScalars = textSegments == null || textSegments.isEmpty();

    return noScalars ? generateEmptyScalars(size) : textSegmentsToScalars(textSegments);
  }

  static <T> List<String> toBsinOsOperateCodeScalars(List<T> textSegments, int size) {
    boolean noScalars = textSegments == null || textSegments.isEmpty();

    return noScalars ? generateEmptyScalars(size) : bsinOsOperateCodeTextSegmentsToScalars(textSegments);
  }

  static <T> List<String> toTenantId(List<T> textSegments, int size) {
    return textSegments.stream()
            .map(segment -> ((BsinTextSegment) segment).tenantId())
            .collect(toList());
  }

  static <T> List<String> toOpCode(List<T> textSegments, int size) {
    return textSegments.stream()
            .map(segment -> ((BsinOsOperateCodeSegment) segment).getOpCode())
            .collect(toList());
  }

  static <T> List<String> toParams(List<T> textSegments, int size) {
    return textSegments.stream()
            .map(segment -> ((BsinOsOperateCodeSegment) segment).getParams())
            .collect(toList());
  }

  static <T> List<String> toScope(List<T> textSegments, int size) {
    return textSegments.stream()
            .map(segment -> ((BsinOsOperateCodeSegment) segment).getScope())
            .collect(toList());
  }

  static <T> List<String> toType(List<T> textSegments, int size) {
    return textSegments.stream()
            .map(segment -> ((BsinTextSegment) segment).type())
            .collect(toList());
  }

  static <T> List<String> toKnowledgeBaseFileNo(List<T> textSegments, int size) {
    return textSegments.stream()
            .map(segment -> ((BsinTextSegment) segment).knowledgeBaseFileNo())
            .collect(toList());
  }

  static <T> List<String> toChunkNo(List<T> textSegments, int size) {
    return textSegments.stream()
            .map(segment -> ((BsinTextSegment) segment).chunkNo())
            .collect(toList());
  }

  static <T> List<String> toAiNo(List<T> textSegments, int size) {
    return textSegments.stream()
            .map(segment -> ((BsinTextSegment) segment).aiNo())
            .collect(toList());
  }

  static <T> List<String> textSegmentsToScalars(List<T> textSegments) {
    return textSegments.stream()
            .map(segment -> ((BsinTextSegment) segment).text())
            .collect(toList());
  }

  static <T> List<String> bsinOsOperateCodeTextSegmentsToScalars(List<T> textSegments) {
    return textSegments.stream()
            .map(segment -> ((BsinOsOperateCodeSegment) segment).text())
            .collect(toList());
  }

  static <T> List<EmbeddingMatch<T>> toEmbeddingMatches(
          MilvusServiceClient milvusClient,
          SearchResultsWrapper resultsWrapper,
          String collectionName,
          ConsistencyLevelEnum consistencyLevel,
          boolean queryForVectorOnSearch) {
    List<EmbeddingMatch<T>> matches = new ArrayList<>();

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
              String.valueOf(resultsWrapper.getFieldData(TENANT_ID_FIELD_NAME, 0).get(i));
      String aiNo = String.valueOf(resultsWrapper.getFieldData(AI_NO, 0).get(i));
      String type = String.valueOf(resultsWrapper.getFieldData(TYPE_FIELD_NAME, 0).get(i));
      String knowledgeBaseFileNo =
              String.valueOf(resultsWrapper.getFieldData(KNOWLEDGE_BASE_FILE_NO_FIELD_NAME, 0).get(i));
      String chunkNo =
              String.valueOf(resultsWrapper.getFieldData(CHUNK_NO_FIELD_NAME, 0).get(i));
      T textSegment =
              isNullOrBlank(text)
                      ? null
                      : (T) BsinTextSegment.fromBsin(
                      text, customerNo, type, aiNo, knowledgeBaseFileNo, chunkNo, null);
      EmbeddingMatch<T> embeddingMatch =
              new EmbeddingMatch<>(
                      RelevanceScore.fromCosineSimilarity(score), rowId, embedding, textSegment);
      matches.add(embeddingMatch);
    }

    return matches;
  }

  static <T> List<EmbeddingMatch<T>> toBsinOsOpCodeEmbeddingMatches(
          MilvusServiceClient milvusClient,
          SearchResultsWrapper resultsWrapper,
          String collectionName,
          ConsistencyLevelEnum consistencyLevel,
          boolean queryForVectorOnSearch) {
    List<EmbeddingMatch<T>> matches = new ArrayList<>();

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
      String opCode =
              String.valueOf(resultsWrapper.getFieldData(OP_CODE_FIELD_NAME, 0).get(i));
      String params = String.valueOf(resultsWrapper.getFieldData(PARAMS_FIELD_NAME, 0).get(i));
      String scope = String.valueOf(resultsWrapper.getFieldData(SCOPE_FIELD_NAME, 0).get(i));
      T textSegment =
              isNullOrBlank(text)
                      ? null
                      : (T) BsinOsOperateCodeSegment.fromBsin(
                      text, opCode, params, scope, null);
      EmbeddingMatch<T> embeddingMatch =
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
