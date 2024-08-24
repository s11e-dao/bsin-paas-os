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

import java.util.*;
import java.util.function.Function;

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

    return createEmbeddingMatches(
            milvusClient,
            resultsWrapper,
            collectionName,
            consistencyLevel,
            queryForVectorOnSearch,
            Mapper::mapBsinTextSegment);
  }

  private static <T> List<EmbeddingMatch<T>> createEmbeddingMatches(
          MilvusServiceClient milvusClient,
          SearchResultsWrapper resultsWrapper,
          String collectionName,
          ConsistencyLevelEnum consistencyLevel,
          boolean queryForVectorOnSearch,
          Function<SearchResultsWrapper, T> textSegmentMapper) {

    List<EmbeddingMatch<T>> matches = new ArrayList<>();
    Map<String, Embedding> idToEmbedding = queryForVectorOnSearch
            ? queryEmbeddings(milvusClient, collectionName, getRowIds(resultsWrapper), consistencyLevel)
            : Collections.emptyMap();

    resultsWrapper.getIDScore(0).forEach(record -> {
      double score = record.getScore();
      String rowId = record.getStrID();
      Embedding embedding = idToEmbedding.get(rowId);
      T textSegment = textSegmentMapper.apply(resultsWrapper);

      matches.add(new EmbeddingMatch<>(
              RelevanceScore.fromCosineSimilarity(score),
              rowId,
              embedding,
              textSegment));
    });

    return matches;
  }

  private static List<String> getRowIds(SearchResultsWrapper resultsWrapper) {
    return (List<String>) resultsWrapper.getFieldWrapper(ID_FIELD_NAME).getFieldData();
  }

  static <T> List<EmbeddingMatch<T>> toBsinOsOpCodeEmbeddingMatches(
          MilvusServiceClient milvusClient,
          SearchResultsWrapper resultsWrapper,
          String collectionName,
          ConsistencyLevelEnum consistencyLevel,
          boolean queryForVectorOnSearch) {

    return createEmbeddingMatches(
            milvusClient,
            resultsWrapper,
            collectionName,
            consistencyLevel,
            queryForVectorOnSearch,
            Mapper::mapBsinOsOperateCodeSegment);
  }

  private static <T> T mapBsinOsOperateCodeSegment(SearchResultsWrapper resultsWrapper) {
    String text = String.valueOf(resultsWrapper.getFieldData(TEXT_FIELD_NAME, 0).get(0));
    String opCode = String.valueOf(resultsWrapper.getFieldData(OP_CODE_FIELD_NAME, 0).get(0));
    String params = String.valueOf(resultsWrapper.getFieldData(PARAMS_FIELD_NAME, 0).get(0));
    String scope = String.valueOf(resultsWrapper.getFieldData(SCOPE_FIELD_NAME, 0).get(0));

    return (T) BsinOsOperateCodeSegment.fromBsin(text, opCode, params, scope, null);
  }

  private static <T> T mapBsinTextSegment(SearchResultsWrapper resultsWrapper) {
    String text = String.valueOf(resultsWrapper.getFieldData(TEXT_FIELD_NAME, 0).get(0));
    String customerNo = String.valueOf(resultsWrapper.getFieldData(TENANT_ID_FIELD_NAME, 0).get(0));
    String aiNo = String.valueOf(resultsWrapper.getFieldData(AI_NO, 0).get(0));
    String type = String.valueOf(resultsWrapper.getFieldData(TYPE_FIELD_NAME, 0).get(0));
    String knowledgeBaseFileNo = String.valueOf(resultsWrapper.getFieldData(KNOWLEDGE_BASE_FILE_NO_FIELD_NAME, 0).get(0));
    String chunkNo = String.valueOf(resultsWrapper.getFieldData(CHUNK_NO_FIELD_NAME, 0).get(0));

    return (T) BsinTextSegment.fromBsin(text, customerNo, type, aiNo, knowledgeBaseFileNo, chunkNo, null);
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
