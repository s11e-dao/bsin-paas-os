package me.flyray.bsin.server.milvus;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import java.util.List;

public class BsinTextSegment extends TextSegment {

  /**
   * @Link VectorStoreType
   */
  private final String type;

  private final String tenantId;
  private final String aiNo;
  private final String knowledgeBaseFileNo;
  private final String chunkNo;

  private final List<Float> vector;

  public BsinTextSegment(
      String text,
      Metadata metadata,
      String tenantId,
      String type,
      String aiNo,
      String knowledgeBaseFileNo,
      String chunkNo,
      List<Float> vector) {
    super(text, metadata);
    //    this.customerNo = ensureNotBlank(customerNo, "customerNo");
    //    this.knowledgeBaseFileNo = ensureNotNull(knowledgeBaseFileNo, "knowledgeBaseFileNo");
    this.tenantId = tenantId;
    this.type = type;
    this.vector = vector;
    this.aiNo = aiNo;
    this.knowledgeBaseFileNo = knowledgeBaseFileNo;
    this.chunkNo = chunkNo;
  }

  public BsinTextSegment(
      TextSegment textSegment,
      String tenantId,
      String type,
      String aiNo,
      String knowledgeBaseFileNo,
      String chunkNo,
      List<Float> vector) {
    super(textSegment.text(), textSegment.metadata());
    //    this.customerNo = ensureNotBlank(customerNo, "customerNo");
    //    this.knowledgeBaseFileNo = ensureNotNull(knowledgeBaseFileNo, "knowledgeBaseFileNo");
    this.tenantId = tenantId;
    this.type = type;
    this.aiNo = aiNo;
    this.knowledgeBaseFileNo = knowledgeBaseFileNo;
    this.chunkNo = chunkNo;
    this.vector = vector;
  }

  public String tenantId() {
    return tenantId;
  }

  public String type() {
    return type;
  }

  public String aiNo() {
    return aiNo;
  }

  public String knowledgeBaseFileNo() {
    return knowledgeBaseFileNo;
  }

  public String chunkNo() {
    return chunkNo;
  }

  public List<Float> vector() {
    return vector;
  }

  public static BsinTextSegment from(
      String text,
      Metadata metadata,
      String customerNo,
      String type,
      String aiNo,
      String knowledgeBaseFileNo,
      String chunkNo,
      List<Float> vector) {
    return new BsinTextSegment(
        text, new Metadata(), customerNo, type, aiNo, knowledgeBaseFileNo, chunkNo, vector);
  }

  public static BsinTextSegment fromBsin(
      String text,
      String type,
      String tenantId,
      String aiNo,
      String knowledgeBaseFileNo,
      String chunkNo,
      List<Float> vector) {
    return new BsinTextSegment(
        text, new Metadata(), tenantId, type, aiNo, knowledgeBaseFileNo, chunkNo, vector);
  }
}
