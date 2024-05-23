package me.flyray.bsin.facade.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.langchain4j.data.document.Metadata;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class KnowledgeBaseFileVO implements Serializable {

  /** 知识库文件ID */
  private String serialNo;

  /** 租户ID */
  private String tenantId;

  /** 商户ID */
  private String merchantNo;

  /** 客户ID */
  private String customerNo;

  /** 关联的知识库ID */
  private String knowledgeBaseNo;

  /** 文件类型：1、pdf 2、markdown 3、doc */
  private String fileType;

  /** 知识库文件地址: url or path */
  private String fileUri;

  /** 知识库封面图片 */
  private String coverImage;

  /** 状态： 0：禁用 1:启用 */
  private String status;

  /** 访问权限： 1-private 2-public */
  private String accessAuthority;

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

  /** 向量文件 */
  private List<EmbeddingVO> embeddings;
}
