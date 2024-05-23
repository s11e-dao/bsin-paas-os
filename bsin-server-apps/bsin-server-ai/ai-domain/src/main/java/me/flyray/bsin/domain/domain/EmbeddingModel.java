package me.flyray.bsin.domain.domain;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import static dev.langchain4j.internal.Utils.getOrDefault;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NonNull;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @TableName ai_embedding_model
 */
@Data
@TableName(value = "ai_embedding_model")
public class EmbeddingModel implements Serializable {

  @TableId(value = "serial_no", type = IdType.ASSIGN_ID)
  private String serialNo;

  /** 租户id */
  private String tenantId;

  /** 商户号 */
  private String merchantNo;

  /** 客户编号 */
  private String customerNo;

  /** 索引模型名称 */
  private String name;

  /** 索引模型类型：1、embedding-2 2、M3E 3、AllMiniLmL6V2 4、BgeSmallZh 5、Qwen */
  private String type;

  /** 检索方式：1.语义检索 2.增强语义检索 3.混合检索 */
  private String retrievalMethod;

  /** 知识库封面图片 */
  private String coverImage;

  /** 状态： 0：禁用 1:启用 */
  private String status;

  /** 访问权限： 1-private 2-public */
  private String accessAuthority;

  /** 最多检索条目 */
  private Integer maxResults;

  /** 最低相似度 */
  private Double minScore;

  /** 向量维度 */
  private Integer dimension;

  /** Qwen/OpenAI tokenizer 模型需要key */
  private String apiKey;

  /** Qwen tokenizer */
  private String apiBaseUrl;

  /** 代理地址 */
  private String proxyUrl;

  /** 代理端口 */
  private String proxyPort;

  /** 引用上限：300-3000，中文1字1.7token, 英文1字=1token */
  private Integer quoteLimit;

  /** 空搜索回复 */
  private String emptyResp;

  /** 分段最多token数 */
  private Integer segmentSizeInTokens;

  /** 最大重叠token数 */
  private Integer overlapSizeInTokens;

  /** 分词模型：1、OpenAiTokenizer(GPT_3_5_TURBO) 2、Bert 3、Bert */
  private String tokenizerModel;

  /** 文档分词器：1、ByCharacter 2、ByLine 3、ByParagraph 4、ByRegex 5、BySentence 6、ByWord */
  private String documentSplitter;

  /** 向量库集合名称 */
  private String collectionName;

  /** 是否为默认商户或者用户copilot */
  private Boolean defaultFlag;

  /** 逻辑删除 0、未删除 1、已删除 */
  @TableLogic(value = "0", delval = "1")
  private Integer delFlag;

  /** 是否可编辑|删除 */
  private Boolean editable;

  /** 创建人 */
  private String createBy;

  /** 描述 */
  private String description;

  /** 创建时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createTime;

  /** 更新时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date updateTime;

  public EmbeddingModel() {}

  public EmbeddingModel(
      String serialNo,
      String tenantId,
      String merchantNo,
      String customerNo,
      String name,
      String type,
      String collectionName,
      String retrievalMethod,
      Integer maxResults,
      Double minScore,
      Integer dimension,
      Integer quoteLimit,
      String emptyResp,
      Integer segmentSizeInTokens,
      Integer overlapSizeInTokens,
      String tokenizerModel) {
    Builder builder =
        newBuilder()
            .withSerialNo(getOrDefault(serialNo, BsinSnowflake.getId()))
            .withTenantId(getOrDefault(tenantId, "tenantId"))
            .withMerchantNo(getOrDefault(merchantNo, "merchantNo"))
            .withCustomerNo(getOrDefault(customerNo, "customerNo"))
            .withName(getOrDefault(name, "name"))
            .withCollectionName(getOrDefault(collectionName, "collectionName"))
            .withType(getOrDefault(type, "AllMiniLmL6V2"))
            .withRetrievalMethod(getOrDefault(retrievalMethod, "retrievalMethod"))
            .withMaxResults(getOrDefault(maxResults, 3))
            .withMinScore(getOrDefault(minScore, 0.7))
            .withDimension(getOrDefault(dimension, 512))
            .withQuoteLimit(getOrDefault(quoteLimit, 3000))
            .withEmptyResp(getOrDefault(emptyResp, ""))
            .withSegmentSizeInTokens(getOrDefault(segmentSizeInTokens, 100))
            .withOverlapSizeInTokens(getOrDefault(overlapSizeInTokens, 0))
            .withTokenizerModel(getOrDefault(tokenizerModel, "OpenAiTokenizer(GPT_3_5_TURBO)"));
    this.serialNo = builder.getSerialNo();
    this.tenantId = builder.getTenantId();
    this.merchantNo = builder.getMerchantNo();
    this.customerNo = builder.getCustomerNo();
    this.name = builder.getName();
    this.collectionName = builder.getCollectionName();
    this.type = builder.getType();
    this.retrievalMethod = builder.getRetrievalMethod();
    this.maxResults = builder.getMaxResults();
    this.minScore = builder.getMinScore();
    this.dimension = builder.getDimension();
    this.quoteLimit = builder.getQuoteLimit();
    this.emptyResp = builder.getEmptyResp();
    this.segmentSizeInTokens = builder.getSegmentSizeInTokens();
    this.overlapSizeInTokens = builder.getSegmentSizeInTokens();
    this.tokenizerModel = builder.getTokenizerModel();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Data
  public static class Builder {
    private String serialNo;
    private String tenantId;
    private String merchantNo;
    private String customerNo;
    private String name;
    private String type;
    private String collectionName;
    private String retrievalMethod;
    private Integer maxResults;
    private Double minScore;
    private Integer dimension;
    private Integer quoteLimit;
    private String emptyResp;
    private Integer segmentSizeInTokens;
    private Integer overlapSizeInTokens;
    private String tokenizerModel;

    protected Builder() {}

    public Builder withSerialNo(String serialNo) {
      this.serialNo = serialNo;
      return this;
    }

    public Builder withMerchantNo(String merchantNo) {
      this.merchantNo = merchantNo;
      return this;
    }

    public Builder withTenantId(String tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public Builder withCustomerNo(String customerNo) {
      this.customerNo = customerNo;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withType(String type) {
      this.type = type;
      return this;
    }

    public Builder withCollectionName(String collectionName) {
      this.collectionName = collectionName;
      return this;
    }

    public Builder withRetrievalMethod(String retrievalMethod) {
      this.retrievalMethod = retrievalMethod;
      return this;
    }

    public Builder withMaxResults(Integer maxResults) {
      this.maxResults = maxResults;
      return this;
    }

    public Builder withMinScore(Double minScore) {
      this.minScore = minScore;
      return this;
    }

    public Builder withDimension(Integer dimension) {
      this.dimension = dimension;
      return this;
    }

    public Builder withQuoteLimit(Integer quoteLimit) {
      this.quoteLimit = quoteLimit;
      return this;
    }

    public Builder withEmptyResp(String emptyResp) {
      this.emptyResp = emptyResp;
      return this;
    }

    public Builder withSegmentSizeInTokens(Integer segmentSizeInTokens) {
      this.segmentSizeInTokens = segmentSizeInTokens;
      return this;
    }

    public Builder withOverlapSizeInTokens(Integer overlapSizeInTokens) {
      this.overlapSizeInTokens = overlapSizeInTokens;
      return this;
    }

    public Builder withTokenizerModel(String tokenizerModel) {
      this.tokenizerModel = tokenizerModel;
      return this;
    }

    public EmbeddingModel build() {
      return new EmbeddingModel(
          serialNo,
          tenantId,
          merchantNo,
          customerNo,
          name,
          type,
          collectionName,
          retrievalMethod,
          maxResults,
          minScore,
          dimension,
          quoteLimit,
          emptyResp,
          segmentSizeInTokens,
          overlapSizeInTokens,
          tokenizerModel);
    }
  }
}
