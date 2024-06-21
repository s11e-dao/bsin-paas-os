package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.KnowledgeBaseFileChunk;

import java.util.List;
import java.util.Map;

/**
 * @author leonard
 * @description 针对表【ai_knowledge_base_file_chunk】的数据库操作Service
 * @createDate 2024-02-03 14:40:35
 */
public interface KnowledgeBaseFileChunkService {

  /** 添加知识库文件片段 */
  KnowledgeBaseFileChunk add(Map<String, Object> requestMap);

  /** 删除 */
  void delete(Map<String, Object> requestMap);

  /** 编辑 */
  void edit(Map<String, Object> requestMap) throws Exception;

  /** 详情 */
  KnowledgeBaseFileChunk getDetail(Map<String, Object> requestMap) throws Exception;

  /** 分页查询 */
  IPage<KnowledgeBaseFileChunk> getPageList(Map<String, Object> requestMap);

  /** 查询 */
  List<KnowledgeBaseFileChunk> getList(Map<String, Object> requestMap);

}
