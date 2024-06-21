package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.KnowledgeBase;
import me.flyray.bsin.facade.response.EmbeddingVO;
import me.flyray.bsin.facade.response.KnowledgeBaseVO;

import java.util.List;
import java.util.Map;

/**
 * @author leonard
 * @description 针对表【ai_knowledge_base】的数据库操作Service
 * @createDate 2023-12-9 01:40:35
 */
public interface KnowledgeBaseService {

  /**
   * 创建知识库
   * */
  KnowledgeBase add(Map<String, Object> requestMap);

  /**
   * 删除
   * */
  void delete(Map<String, Object> requestMap);

  /** 编辑 */
  void edit(Map<String, Object> requestMap);

  /** 详情 */
  KnowledgeBaseVO getDetail(Map<String, Object> requestMap) throws Exception;

  /** 分页查询 */
  IPage<KnowledgeBase> getPageList(Map<String, Object> requestMap);

  /** 查询 */
  List<KnowledgeBase> getList(Map<String, Object> requestMap);

  /** 获取默认copilot */
  public KnowledgeBase getDefault(Map<String, Object> requestMap);

  /** 设置为默认copilot */
  public void setDefault(Map<String, Object> requestMap);

  /** 检索 */
  List<EmbeddingVO> retrieval(Map<String, Object> requestMap);

}
