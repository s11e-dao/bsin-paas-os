package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.EmbeddingModel;

import java.util.List;
import java.util.Map;

/**
 * @author leonard
 * @description 针对表【ai_embedding_model】的数据库操作Service
 * @createDate 2023-12-9 01:40:35
 */
public interface EmbeddingModelService {

  /** 添加 */
  EmbeddingModel add(Map<String, Object> requestMap);

  /** 删除 */
  void delete(Map<String, Object> requestMap);

  /** 编辑 */
  void edit(Map<String, Object> requestMap);

  /** 详情 */
  EmbeddingModel getDetail(Map<String, Object> requestMap);

  /** 分页查询 */
  IPage<EmbeddingModel> getPageList(Map<String, Object> requestMap);

  /** 查询 */
  List<EmbeddingModel> getList(Map<String, Object> requestMap);

  /** 获取默认  */
  public EmbeddingModel getDefault(Map<String, Object> requestMap);


  /** 设置为默认  */
  public void setDefault(Map<String, Object> requestMap);

}
