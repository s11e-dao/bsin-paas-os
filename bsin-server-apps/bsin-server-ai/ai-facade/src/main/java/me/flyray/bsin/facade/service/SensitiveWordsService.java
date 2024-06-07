package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.SensitiveWords;

import java.util.List;
import java.util.Map;

/**
 * @author leonard
 * @description 针对表【ai_sensitive_words】的数据库操作Service
 * @createDate 2023-12-9 01:40:35
 */
public interface SensitiveWordsService {

  /** 添加 */
  Map<String, Object> add(Map<String, Object> requestMap);

  /** 删除 */
  Map<String, Object> delete(Map<String, Object> requestMap);

  /** 编辑 */
  Map<String, Object> edit(Map<String, Object> requestMap);

  /** 详情 */
  Map<String, Object> getDetail(Map<String, Object> requestMap);

  /** 分页查询 */
  IPage<SensitiveWords> getPageList(Map<String, Object> requestMap);

  /** 查询 */
  List<SensitiveWords> getList(Map<String, Object> requestMap);
}
