package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName ai_knowledge_base_file_chunk
 */
@Data
@TableName(value = "ai_knowledge_base_file_chunk")
public class KnowledgeBaseFileChunk implements Serializable {

  /** 片段索引ID */
  @TableId private String chunkNo;

  /** 类型： */
  private String type;

  /** 关联的知识库文件ID */
  private String knowledgeBaseFileNo;

  /** 片段内容： 原始分段内容，存放在mysql中，向量检索时，先召回 index，在根据 index的 chunkNo可查询出 chunkContent,作为补充 */
  private String chunkContent;

  /** 逻辑删除 0、未删除 1、已删除 */
  @TableLogic(value = "0", delval = "1")
  private Integer delFlag;

  /** 索引内容：需要向量化存储的字段：对应milvus的text字段 */
  private String chunkText;
}
