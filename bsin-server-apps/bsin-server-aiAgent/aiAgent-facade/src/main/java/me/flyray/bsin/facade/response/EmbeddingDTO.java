package me.flyray.bsin.facade.response;

import dev.langchain4j.data.document.Metadata;
import java.io.Serializable;
import lombok.Data;

@Data
public class EmbeddingDTO implements Serializable {

  /** 集合名称 */
  private String collectionName;

  /** 集合名称 */
  private String partitionName;

  /** 检索范围： 1.知识库文件 2.知识库 3.客户的聊天上下文 4.知识库+聊天上下文 */
  private String retrievalScope;

  /** 商户ID */
  private String merchantNo;

  /** 客户ID */
  private String customerNo;

  /** 1-知识库 2-chatHistory 3-chatHistorySummary */
  private String type;

  /** 向量模型ID */
  private String embeddingModelNo;

  /** 知识库ID */
  private String knowledgeBaseNo;

  /** Copilot ID */
  private String aiNo;

  /** 知识库文件ID */
  private String knowledgeBaseFileNo;

  /** 分段的文本索引 */
  private String chunkNo;

  /** 分段的文本 */
  private String text;

  /** 是否需要返回向量 */
  private boolean vectorRet;

  /** 最多返回 */
  private Integer maxResults;

  private Double minScore;

  /** 向量维度 */
  private Integer dimension;
}
