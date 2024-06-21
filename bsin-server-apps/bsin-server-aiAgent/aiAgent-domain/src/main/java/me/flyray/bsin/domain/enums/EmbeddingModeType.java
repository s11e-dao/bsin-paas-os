package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EmbeddingModeType {

  /** embedding-2 */
  EMBEDDING_2("1", "embedding-2"),

  /** embedding-2 */
  M3E("2", "M3E"),

  /** AllMiniLmL6V2 */
  All_MINI_LM_L6_V2("3", "AllMiniLmL6V2"),

  /** BgeSmallZh */
  BGE_SMALL_ZH("4", "BgeSmallZh"),

  /** QwenEmbeddingModel
   * Support: en, zh, es, fr, pt, id, ja, ko, de, ru
   *
   * */
  QWEN_TEXT_EMBEDDING_V2("5", "QwenTextEmbeddingV2");

  private String code;

  private String desc;

  EmbeddingModeType(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  /** Json 枚举序列化 */
  @JsonCreator
  public static EmbeddingModeType getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (EmbeddingModeType status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
