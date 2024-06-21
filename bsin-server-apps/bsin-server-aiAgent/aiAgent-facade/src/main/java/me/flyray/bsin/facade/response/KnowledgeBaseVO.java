package me.flyray.bsin.facade.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import me.flyray.bsin.domain.entity.EmbeddingModel;
import lombok.Data;

@Data
public class KnowledgeBaseVO implements Serializable {

  String serialNo;

  /** 租户id */
  private String tenantId;

  /** 商户号 */
  private String merchantNo;

  /** 客户编号 */
  private String customerNo;

  /** 知识库名称 */
  private String name;

  /** 知识库类型：1、通用知识库(文件导入) 2、web站点 3、公众号 */
  private String type;

  /** 知识库封面图片 */
  private String coverImage;

  /** 状态： 0：禁用 1:启用 */
  private String status;

  /** 访问权限： 1-private 2-public */
  private String accessAuthority;

  /** 索引模型ID： */
  private String embeddingModelNo;

  /** 逻辑删除 0、未删除 1、已删除 */
  private Integer delFlag;

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

  private EmbeddingModel embeddingModel;

  /** 知识库文件 */
  private List<KnowledgeBaseFileVO> knowledgeBaseFiles;
}
