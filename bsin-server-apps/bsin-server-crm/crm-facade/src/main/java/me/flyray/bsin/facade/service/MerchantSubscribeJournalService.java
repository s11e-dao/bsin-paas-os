package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

/**
* @author bolei
* @description 针对表【market_merchant_subscribe_journal】的数据库操作Service
* @createDate 2023-11-08 14:38:51
*/

public interface MerchantSubscribeJournalService {

    /**
     * 分页查询
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

}
