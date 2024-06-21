package me.flyray.bsin.server.milvus;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static me.flyray.bsin.server.milvus.BsinMilvusEmbeddingStore.*;

import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.MetricType;
import io.milvus.param.collection.FlushParam;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.param.dml.SearchParam;
import java.util.List;

class CollectionRequestBuilder {

  static FlushParam buildFlushRequest(String collectionName) {
    return FlushParam.newBuilder().withCollectionNames(singletonList(collectionName)).build();
  }

  static HasCollectionParam buildHasCollectionRequest(String collectionName) {
    return HasCollectionParam.newBuilder().withCollectionName(collectionName).build();
  }

  static InsertParam buildInsertRequest(String collectionName, List<InsertParam.Field> fields) {
    return InsertParam.newBuilder().withCollectionName(collectionName).withFields(fields).build();
  }

  static LoadCollectionParam buildLoadCollectionInMemoryRequest(String collectionName) {
    return LoadCollectionParam.newBuilder().withCollectionName(collectionName).build();
  }

  static SearchParam buildSearchRequest(
      String collectionName,
      List<Float> vector,
      int maxResults,
      MetricType metricType,
      ConsistencyLevelEnum consistencyLevel) {
    return SearchParam.newBuilder()
        .withCollectionName(collectionName)
        .withVectors(singletonList(vector))
        .withVectorFieldName(VECTOR_FIELD_NAME)
        .withTopK(maxResults)
        .withMetricType(metricType)
        .withConsistencyLevel(consistencyLevel)
        .withOutFields(
            asList(
                ID_FIELD_NAME,
                TEXT_FIELD_NAME,
                CUSTOMER_NO_FIELD_NAME,
                TYPE_FIELD_NAME,
                AI_NO,
                KNOWLEDGE_BASE_FILE_NO_FIELD_NAME,
                CHUNK_NO_FIELD_NAME))
        .build();
  }

  static SearchParam buildSearchRequest(
      String collectionName,
      List<Float> vector,
      int maxResults,
      MetricType metricType,
      ConsistencyLevelEnum consistencyLevel,
      String customerNo,
      String type,
      String aiNo,
      String knowledgeBaseFileNo,
      String chunkNo) {
    String queryExpr = "";
    if (customerNo != null) {
      queryExpr += CUSTOMER_NO_FIELD_NAME + " == '" + customerNo + "'";
      if (aiNo != null) {
        queryExpr += " && " + AI_NO + " == '" + aiNo + "'";
      }
    } else {
      if (aiNo != null) {
        queryExpr += AI_NO + " == '" + aiNo + "'";
        if (knowledgeBaseFileNo != null) {
          queryExpr +=
              " && " + KNOWLEDGE_BASE_FILE_NO_FIELD_NAME + " == '" + knowledgeBaseFileNo + "'";
        }
      } else {
        if (knowledgeBaseFileNo != null) {
          queryExpr += KNOWLEDGE_BASE_FILE_NO_FIELD_NAME + " == '" + knowledgeBaseFileNo + "'";
        }
      }
    }
    if (type != null) {
      queryExpr += " && " + TYPE_FIELD_NAME + " == '" + type + "'";
    }
    if (chunkNo != null) {
      queryExpr += " && " + CHUNK_NO_FIELD_NAME + " == '" + chunkNo + "'";
    }

    return SearchParam.newBuilder()
        // 集合名称
        .withCollectionName(collectionName)
        // 搜索的向量值
        .withVectors(singletonList(vector))
        // 搜索的Field
        .withVectorFieldName(VECTOR_FIELD_NAME)
        // 返回多少条结果
        .withTopK(maxResults)
        // 计算方式
        // 欧氏距离 (L2)
        // 内积 (IP)
        .withMetricType(metricType)
        .withConsistencyLevel(consistencyLevel)
        .withOutFields(
            asList(
                ID_FIELD_NAME,
                TEXT_FIELD_NAME,
                TYPE_FIELD_NAME,
                CUSTOMER_NO_FIELD_NAME,
                AI_NO,
                KNOWLEDGE_BASE_FILE_NO_FIELD_NAME,
                CHUNK_NO_FIELD_NAME))
        // https://milvus.io/docs/boolean.md
        .withExpr(queryExpr)
        .build();
  }

  static QueryParam buildQueryRequest(
      String collectionName, List<String> rowIds, ConsistencyLevelEnum consistencyLevel) {
    return QueryParam.newBuilder()
        .withCollectionName(collectionName)
        .withExpr(buildQueryExpression(rowIds))
        .withConsistencyLevel(consistencyLevel)
        .withOutFields(singletonList(VECTOR_FIELD_NAME))
        .build();
  }

  static String buildQueryExpression(List<String> rowIds) {
    return rowIds.stream()
        .map(id -> format("%s == '%s'", ID_FIELD_NAME, id))
        .collect(joining(" || "));
  }

  static String buildDeleteExpression(List<String> rowIds) {
    return ID_FIELD_NAME
        + " in ["
        + rowIds.stream().map(id -> format("'%s'", id)).collect(joining(","))
        + "]";
  }
}
