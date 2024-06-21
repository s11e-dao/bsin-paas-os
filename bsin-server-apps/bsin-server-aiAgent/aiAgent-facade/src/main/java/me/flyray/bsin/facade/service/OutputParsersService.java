package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.OutputParsers;

import java.util.List;
import java.util.Map;

/**
 * @author leonard
 * @description 针对表【ai_prompt_template】的数据库操作Service
 * @createDate 2023-12-9 01:40:35
 */
public interface OutputParsersService {

  /** 添加 */
  OutputParsers add(Map<String, Object> requestMap);

  /** 删除 */
  void delete(Map<String, Object> requestMap);

  /** 编辑 */
  void edit(Map<String, Object> requestMap);

  /** 详情 */
  OutputParsers getDetail(Map<String, Object> requestMap);

  /** 分页查询 */
  IPage<?> getPageList(Map<String, Object> requestMap);

  /** 查询 */
  List<OutputParsers> getList(Map<String, Object> requestMap);
}
