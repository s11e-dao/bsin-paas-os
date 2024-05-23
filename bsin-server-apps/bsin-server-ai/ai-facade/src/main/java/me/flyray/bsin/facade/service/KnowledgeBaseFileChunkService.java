package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author leonard
 * @description 针对表【ai_knowledge_base_file_chunk】的数据库操作Service
 * @createDate 2024-02-03 14:40:35
 */
public interface KnowledgeBaseFileChunkService {

  /** 添加知识库文件片段 */
  Map<String, Object> add(Map<String, Object> requestMap);

  /** 删除 */
  Map<String, Object> delete(Map<String, Object> requestMap);

  /** 编辑 */
  Map<String, Object> edit(Map<String, Object> requestMap);

  /** 详情 */
  Map<String, Object> getDetail(Map<String, Object> requestMap) throws Exception;

  /** 分页查询 */
  Map<String, Object> getPageList(Map<String, Object> requestMap);

  /** 查询 */
  Map<String, Object> getList(Map<String, Object> requestMap);
}
