package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.KnowledgeBaseFile;
import me.flyray.bsin.facade.response.KnowledgeBaseFileVO;

import java.util.List;
import java.util.Map;

/**
 * @author leonard
 * @description 针对表【ai_knowledge_base_file】的数据库操作Service
 * @createDate 2023-12-9 01:40:35
 */
public interface KnowledgeBaseFileService {

  /** 添加知识库文件 */
  KnowledgeBaseFile add(Map<String, Object> requestMap);

  /** 删除 */
  void delete(Map<String, Object> requestMap);

  /** 编辑 */
  void edit(Map<String, Object> requestMap);

  /** 详情 */
  KnowledgeBaseFileVO getDetail(Map<String, Object> requestMap) throws Exception;

  /** 分页查询 */
  IPage<?> getPageList(Map<String, Object> requestMap);

  /** 查询 */
  List<KnowledgeBaseFile> getList(Map<String, Object> requestMap);

}
