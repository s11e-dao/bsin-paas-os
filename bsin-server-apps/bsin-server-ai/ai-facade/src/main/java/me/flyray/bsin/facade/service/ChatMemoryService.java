package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.ChatMemory;

import java.util.List;
import java.util.Map;

/**
 * @author leonard
 * @description 针对表【ai_chat_memory】的数据库操作Service
 * @createDate 2023-12-9 01:40:35
 */
public interface ChatMemoryService {

  /** 添加 */
  ChatMemory add(Map<String, Object> requestMap);

  /** 删除 */
  void delete(Map<String, Object> requestMap);

  /** 编辑 */
  void edit(Map<String, Object> requestMap);

  /** 详情 */
  ChatMemory getDetail(Map<String, Object> requestMap);

  /** 分页查询 */
  IPage<ChatMemory> getPageList(Map<String, Object> requestMap);

  /** 查询 */
  List<ChatMemory> getList(Map<String, Object> requestMap);

}
