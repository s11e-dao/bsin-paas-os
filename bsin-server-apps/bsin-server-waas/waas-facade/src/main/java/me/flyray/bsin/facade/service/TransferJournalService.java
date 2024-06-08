package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/8/13
 * @desc
 */
public interface TransferJournalService {

  /** 分页查询 */
  public IPage<?> getPageList(Map<String, Object> requestMap);

  /** 转让详情 */
  public Map<String, Object> getDetail(Map<String, Object> requestMap);

}
