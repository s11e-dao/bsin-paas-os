package me.flyray.bsin.domain.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @TableName ai_knowledge_base_file
 */
@Data
@TableName(value = "ai_knowledge_base_file")
public class KnowledgeBaseFile implements Serializable {

  @TableId private String serialNo;

  /** 文件名称 */
  private String name;

  /** 类型：1、url 2、filePath(FileSystemDocumentLoader) */
  private String type;

  /** 关联的知识库ID */
  private String knowledgeBaseNo;

  /** 文件类型：1、pdf 2、markdown 3、doc */
  private String fileType;

  /** 数据总量: 分段后的数量 */
  private Integer chunkNum;

  /** 知识库文件地址: url or path */
  private String fileUri;

  /** 知识库文件地址: 本地存储路径 */
  private String localPath;

  /** 知识库封面图片 */
  private String coverImage;

  /** 状态： 0：禁用 1:启用 */
  private String status;

  /** 访问权限： 1-private 2-public */
  private String accessAuthority;

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
}
