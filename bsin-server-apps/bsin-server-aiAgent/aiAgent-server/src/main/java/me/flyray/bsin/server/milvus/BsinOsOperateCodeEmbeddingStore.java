package me.flyray.bsin.server.milvus;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.QueryResults;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.langchain4j.internal.Utils.getOrDefault;
import static dev.langchain4j.internal.Utils.isNullOrBlank;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import static io.milvus.common.clientenum.ConsistencyLevelEnum.EVENTUALLY;
import static io.milvus.param.IndexType.FLAT;
import static io.milvus.param.MetricType.COSINE;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static me.flyray.bsin.server.milvus.CollectionOperationsExecutor.*;
import static me.flyray.bsin.server.milvus.CollectionRequestBuilder.*;
import static me.flyray.bsin.server.milvus.Generator.generateRandomId;
import static me.flyray.bsin.server.milvus.Generator.generateRandomIds;
import static me.flyray.bsin.server.milvus.Mapper.*;

/**
 * Represents an <a href="https://milvus.io/">Milvus</a> index as an embedding store. Does not
 * support storing {@link dev.langchain4j.data.document.Metadata} yet.
 */
public class BsinOsOperateCodeEmbeddingStore implements EmbeddingStore<BsinOsOperateCodeSegment> {

  public static final String ID_FIELD_NAME = "id";
  public static final String TEXT_FIELD_NAME = "text";
  public static final String VECTOR_FIELD_NAME = "vector";
  public static final String OP_CODE_FIELD_NAME = "opCode";
  public static final String PARAMS_FIELD_NAME = "params";
  public static final String SCOPE_FIELD_NAME = "scope";

  private final MilvusServiceClient milvusClient;
  private final String collectionName;
  //  private final String partitionName;
  private final MetricType metricType;
  private final ConsistencyLevelEnum consistencyLevel;
  private final boolean retrieveEmbeddingsOnSearch;

  public BsinOsOperateCodeEmbeddingStore(
      String host,
      Integer port,
      String collectionName,
      //      String partitionName,
      Integer dimension,
      IndexType indexType,
      MetricType metricType,
      String uri,
      String token,
      String username,
      String password,
      ConsistencyLevelEnum consistencyLevel,
      Boolean retrieveEmbeddingsOnSearch,
      String databaseName) {
    ConnectParam.Builder connectBuilder =
        ConnectParam.newBuilder()
            .withHost(getOrDefault(host, "localhost"))
            .withPort(getOrDefault(port, 19530))
            .withUri(uri)
            .withToken(token)
            .withAuthorization(username, password);

    if (databaseName != null) {
      connectBuilder.withDatabaseName(databaseName);
    }

    this.milvusClient = new MilvusServiceClient(connectBuilder.build());
    this.collectionName = getOrDefault(collectionName, "default");
    //    this.partitionName = getOrDefault(partitionName, "default");
    this.metricType = getOrDefault(metricType, COSINE);
    this.consistencyLevel = getOrDefault(consistencyLevel, EVENTUALLY);
    this.retrieveEmbeddingsOnSearch = getOrDefault(retrieveEmbeddingsOnSearch, false);

    if (!hasCollection(milvusClient, this.collectionName)) {
      // 初始化的时候创建集合字段
      createOsOperateCodeCollection(milvusClient, this.collectionName, ensureNotNull(dimension, "dimension"));
      // TODO: create partition
      createIndex(milvusClient, this.collectionName, getOrDefault(indexType, FLAT), this.metricType);
    }
  }

  public String add(Embedding embedding) {
    String id = generateRandomId();
    add(id, embedding);
    return id;
  }

  public void add(String id, Embedding embedding) {
    addInternal(id, embedding, null);
  }

  public String add(Embedding embedding, BsinOsOperateCodeSegment textSegment) {
    String id = generateRandomId();
    addInternal(id, embedding, textSegment);
    return id;
  }

  public List<String> addAll(List<Embedding> embeddings) {
    List<String> ids = generateRandomIds(embeddings.size());
    addAllInternal(ids, embeddings, null);
    return ids;
  }

  public List<String> addAll(List<Embedding> embeddings, List<BsinOsOperateCodeSegment> embedded) {
    List<String> ids = generateRandomIds(embeddings.size());
    addAllInternal(ids, embeddings, embedded);
    return ids;
  }

  /**
   * @param expr: eg: "ai_no == '1693149658324525060'"
   * @return R<MutationResult>
   */
  public List<BsinOsOperateCodeSegment> queryDataWithExp(String expr) throws Exception {
    try {
      // 1.加载到内存
      loadCollectionInMemory(milvusClient, collectionName);
      // 2.query
      List<String> query_output_fields =
          Arrays.asList(
              ID_FIELD_NAME,
              TEXT_FIELD_NAME,
              OP_CODE_FIELD_NAME,
              PARAMS_FIELD_NAME,
              SCOPE_FIELD_NAME,
              VECTOR_FIELD_NAME);
      QueryParam queryParam =
          QueryParam.newBuilder()
              .withCollectionName(collectionName)
              .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
              .withExpr(expr)
              .withOutFields(query_output_fields)
              .build();
      R<QueryResults> respQuery = milvusClient.query(queryParam);
      QueryResultsWrapper queryResultWrapper = new QueryResultsWrapper(respQuery.getData());

      List<BsinOsOperateCodeSegment> bsegments = new ArrayList<BsinOsOperateCodeSegment>();
      for (int i = 0; i < queryResultWrapper.getRowRecords().size(); i++) {
        String text =
            String.valueOf(
                queryResultWrapper.getFieldWrapper(TEXT_FIELD_NAME).getFieldData().get(i));
        List<Float> vector =
            (List<Float>)
                queryResultWrapper.getFieldWrapper(VECTOR_FIELD_NAME).getFieldData().get(i);
        String opCode =
            String.valueOf(
                queryResultWrapper.getFieldWrapper(OP_CODE_FIELD_NAME).getFieldData().get(i));
        String params =
            String.valueOf(
                queryResultWrapper.getFieldWrapper(PARAMS_FIELD_NAME).getFieldData().get(i));
        String scope =
            String.valueOf(queryResultWrapper.getFieldWrapper(SCOPE_FIELD_NAME).getFieldData().get(i));
        BsinOsOperateCodeSegment textSegment =
            isNullOrBlank(text)
                ? null
                : BsinOsOperateCodeSegment.fromBsin(
                    text, opCode, params, scope, vector);
        bsegments.add(textSegment);
      }
      return bsegments;

    } catch (Exception e) {
      throw new Exception(e.toString());
    }
  }

  /**
   * @param expr: eg: "ai_no == '1693149658324525060'"
   * @return R<MutationResult>
   */
  public List<String> updateDataWithExp(
      String expr, List<Embedding> embeddings, List<BsinOsOperateCodeSegment> segments) throws Exception {
    try {
      // 1.删除原来数据
      delDataWithExp(expr);
      // 2.插入新数据
      return addAll(embeddings, segments);
    } catch (Exception e) {
      throw new Exception(e.toString());
    }
  }

  /**
   * @param expr: eg: "ai_no == '1693149658324525060'"
   * @return R<MutationResult>
   */
  public String updateDataWithExp(String expr, Embedding embedding, BsinOsOperateCodeSegment segment)
      throws Exception {
    try {
      // 1.删除原来数据
      delDataWithExp(expr);
      // 2.插入新数据
      return add(embedding, segment);
    } catch (Exception e) {
      throw new Exception(e.toString());
    }
  }

  /**
   * @param expr: eg: "ai_no == '1693149658324525060'"
   * @return R<MutationResult>
   */
  public R<MutationResult> delDataWithExp(String expr) throws Exception {
    try {
      // 1.加载到内存
      loadCollectionInMemory(milvusClient, collectionName);
      // 2.query, 设置查询输出字段
      List<String> query_output_fields = Arrays.asList(ID_FIELD_NAME);
      QueryParam queryParam =
          QueryParam.newBuilder()
              .withCollectionName(collectionName)
              .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
              .withExpr(expr)
              .withOutFields(query_output_fields)
              .build();
      R<QueryResults> respQuery = milvusClient.query(queryParam);
      QueryResultsWrapper wrapperQuery = new QueryResultsWrapper(respQuery.getData());
      // 3.获取主键ID
      List<String> exprRowIdList =
          (List<String>) wrapperQuery.getFieldWrapper(ID_FIELD_NAME).getFieldData();
      String delExpr = buildDeleteExpression(exprRowIdList);
      //      String delExpr = ID_FIELD_NAME + " in " + exprRowIdList.toString();   //主鍵只能是整型
      // 4.根据主键id删除
      R<MutationResult> mutationResultR =
          milvusClient.delete(
              DeleteParam.newBuilder()
                  .withCollectionName(collectionName)
                  .withExpr(delExpr)
                  .build());
      return mutationResultR;
    } catch (Exception e) {
      throw new Exception(e.toString());
    }
  }

  /**
   * @param expr: eg: "book_id in [1,2,3]" "book_id < 100" "book_id not in [1,2,3]"
   * @return R<MutationResult>
   */
  public R<MutationResult> delDataWithPrimaryKeyExp(String expr) throws Exception {
    loadCollectionInMemory(milvusClient, collectionName);

    // TODO: 异常捕获
    try {
      R<MutationResult> mutationResultR =
          milvusClient.delete(
              DeleteParam.newBuilder().withCollectionName(collectionName).withExpr(expr).build());
      return mutationResultR;
    } catch (Exception e) {
      throw new Exception(e.toString());
    }
  }

  /**
   * @param expr: eg: "book_id in [1,2,3]" "book_id < 100" "book_id not in [1,2,3]"
   * @return R<MutationResult>
   */
  public R<MutationResult> delDataByPartition(String expr) {
    loadCollectionInMemory(milvusClient, collectionName);
    R<MutationResult> mutationResultR =
        milvusClient.delete(
            DeleteParam.newBuilder()
                .withCollectionName(collectionName)
                //                .withPartitionName(partitionName)
                .withExpr(expr)
                .build());
    return mutationResultR;
  }

  public List<EmbeddingMatch<BsinOsOperateCodeSegment>> findRelevant(
      Embedding referenceEmbedding, int maxResults, double minScore) {
    loadCollectionInMemory(milvusClient, collectionName);

    SearchParam searchRequest = buildBsinOsOpCodeSearchRequest(
            collectionName,
            referenceEmbedding.vectorAsList(),
            maxResults,
            metricType,
            consistencyLevel);
    SearchResultsWrapper resultsWrapper = search(milvusClient, searchRequest);

    List<EmbeddingMatch<BsinOsOperateCodeSegment>> matches = toBsinOsOpCodeEmbeddingMatches(
            milvusClient,
            resultsWrapper,
            collectionName,
            consistencyLevel,
            retrieveEmbeddingsOnSearch);

    return matches.stream().filter(match -> match.score() >= minScore).collect(toList());
  }

  private void addInternal(String id, Embedding embedding, BsinOsOperateCodeSegment textSegment) {
    addAllInternal(
        singletonList(id),
        singletonList(embedding),
        textSegment == null ? null : singletonList(textSegment));
  }
  private void addAllInternal(
          List<String> ids, List<Embedding> embeddings, List<BsinOsOperateCodeSegment> textSegments) {
    List<InsertParam.Field> fields = new ArrayList<>();
    fields.add(new InsertParam.Field(ID_FIELD_NAME, ids));
    fields.add(new InsertParam.Field(TEXT_FIELD_NAME, toBsinOsOperateCodeScalars(textSegments, ids.size())));
    fields.add(new InsertParam.Field(OP_CODE_FIELD_NAME, toOpCode(textSegments, ids.size())));
    fields.add(new InsertParam.Field(PARAMS_FIELD_NAME, toParams(textSegments, ids.size())));
    fields.add(new InsertParam.Field(SCOPE_FIELD_NAME, toScope(textSegments, ids.size())));
    fields.add(new InsertParam.Field(VECTOR_FIELD_NAME, toVectors(embeddings)));

    insert(milvusClient, collectionName, fields);
    flush(milvusClient, collectionName);
  }


  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String host;
    private Integer port;
    private String collectionName;
    //    private String partitionName;
    private Integer dimension;
    private IndexType indexType;
    private MetricType metricType;
    private String uri;
    private String token;
    private String username;
    private String password;
    private ConsistencyLevelEnum consistencyLevel;
    private Boolean retrieveEmbeddingsOnSearch;
    private String databaseName;

    /**
     * @param host The host of the self-managed Milvus instance. Default value: "localhost".
     * @return builder
     */
    public Builder host(String host) {
      this.host = host;
      return this;
    }

    /**
     * @param port The port of the self-managed Milvus instance. Default value: 19530.
     * @return builder
     */
    public Builder port(Integer port) {
      this.port = port;
      return this;
    }

    /**
     * @param collectionName The name of the Milvus collection. If there is no such collection yet,
     *     it will be created automatically. Default value: "default".
     * @return builder
     */
    public Builder collectionName(String collectionName) {
      this.collectionName = collectionName;
      return this;
    }

    //    /**
    //     * @param partitionName The name of the Milvus partition. If there is no such collection
    // yet, it
    //     *     will be created automatically. Default value: "default".
    //     * @return builder
    //     */
    //    public Builder partitionName(String partitionName) {
    //      this.partitionName = partitionName;
    //      return this;
    //    }

    /**
     * @param dimension The dimension of the embedding vector. (e.g. 384) Mandatory if a new
     *     collection should be created.
     * @return builder
     */
    public Builder dimension(Integer dimension) {
      this.dimension = dimension;
      return this;
    }

    /**
     * @param indexType The type of the index. Default value: FLAT.
     * @return builder
     */
    public Builder indexType(IndexType indexType) {
      this.indexType = indexType;
      return this;
    }

    /**
     * @param metricType The type of the metric used for similarity search. Default value: COSINE.
     * @return builder
     */
    public Builder metricType(MetricType metricType) {
      this.metricType = metricType;
      return this;
    }

    /**
     * @param uri The URI of the managed Milvus instance. (e.g.
     *     "https://xxx.api.gcp-us-west1.zillizcloud.com")
     * @return builder
     */
    public Builder uri(String uri) {
      this.uri = uri;
      return this;
    }

    /**
     * @param token The token (API key) of the managed Milvus instance.
     * @return builder
     */
    public Builder token(String token) {
      this.token = token;
      return this;
    }

    /**
     * @param username The username. See details <a
     *     href="https://milvus.io/docs/authenticate.md">here</a>.
     * @return builder
     */
    public Builder username(String username) {
      this.username = username;
      return this;
    }

    /**
     * @param password The password. See details <a
     *     href="https://milvus.io/docs/authenticate.md">here</a>.
     * @return builder
     */
    public Builder password(String password) {
      this.password = password;
      return this;
    }

    /**
     * @param consistencyLevel The consistency level used by Milvus. Default value: EVENTUALLY.
     * @return builder
     */
    public Builder consistencyLevel(ConsistencyLevelEnum consistencyLevel) {
      this.consistencyLevel = consistencyLevel;
      return this;
    }

    /**
     * @param retrieveEmbeddingsOnSearch During a similarity search in Milvus (when calling
     *     findRelevant()), the embedding itself is not retrieved. To retrieve the embedding, an
     *     additional query is required. Setting this parameter to "true" will ensure that embedding
     *     is retrieved. Be aware that this will impact the performance of the search. Default
     *     value: false.
     * @return builder
     */
    public Builder retrieveEmbeddingsOnSearch(Boolean retrieveEmbeddingsOnSearch) {
      this.retrieveEmbeddingsOnSearch = retrieveEmbeddingsOnSearch;
      return this;
    }

    /**
     * @param databaseName Milvus name of database. Default value: null. In this case default Milvus
     *     database name will be used.
     * @return builder
     */
    public Builder databaseName(String databaseName) {
      this.databaseName = databaseName;
      return this;
    }

    public BsinOsOperateCodeEmbeddingStore build() {
      return new BsinOsOperateCodeEmbeddingStore(
          host,
          port,
          collectionName,
          //          partitionName,
          dimension,
          indexType,
          metricType,
          uri,
          token,
          username,
          password,
          consistencyLevel,
          retrieveEmbeddingsOnSearch,
          databaseName);
    }
  }
}
