package me.flyray.bsin.server.milvus;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import lombok.Data;

import java.util.List;

public class BsinOsOperateCodeSegment extends TextSegment {

    /**
   * @Link VectorStoreType
   */
  private final String opCode;
  private final String descripiton;
  private final String params;
  private final String scope;

  private final List<Float> vector;

  public String getOpCode() {
    return opCode;
  }

  public String getDescripiton() {
    return descripiton;
  }

  public String getParams() {
    return params;
  }

  public String getScope() {
    return scope;
  }

  public List<Float> getVector() {
    return vector;
  }

  public BsinOsOperateCodeSegment(
      String descripiton,
      Metadata metadata,
      String opCode,
      String params,
      String scope,
      List<Float> vector) {
    super(descripiton, metadata);
    this.opCode = opCode;
    this.descripiton = descripiton;
    this.params = params;
    this.scope = scope;
    this.vector = vector;
  }

  public BsinOsOperateCodeSegment(
          TextSegment textSegment,
          String opCode, String descripiton,
          String params,
          String scope,
          List<Float> vector) {
    super(textSegment.text(), textSegment.metadata());
    this.opCode = opCode;
    this.descripiton = descripiton;
    this.params = params;
    this.scope = scope;
    this.vector = vector;
  }

  public List<Float> vector() {
    return vector;
  }

  public static BsinOsOperateCodeSegment from(
      String descripiton,
      Metadata metadata,
      String opCode,
      String params,
      String scope,
      List<Float> vector) {
    return new BsinOsOperateCodeSegment(
            descripiton, new Metadata(), opCode, params, scope, vector);
  }

  public static BsinOsOperateCodeSegment fromBsin(
      String descripiton,
      String opCode,
      String params,
      String scope,
      List<Float> vector) {
    return new BsinOsOperateCodeSegment(
            descripiton, new Metadata(), opCode, params, scope, vector);
  }
}
