package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FileLoadType {
  //  类型：1、url 2、文件(FileSystemDocumentLoader) 3、文件路径 4、文件夹 4、公众号
  /** load by url */
  URL("1", "url"),

  /** load by file */
  FILE("2", "文件"),

  /** load by file path */
  FILE_PATH("3", "文件路径"),

  /** load by folder */
  FOLDER("4", "文件夹"),

  /** 微信公众号 */
  WECHAT_MP("4", "微信公众号");

  private String code;

  private String desc;

  FileLoadType(String code, String desc) {
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
  public static FileLoadType getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (FileLoadType status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
