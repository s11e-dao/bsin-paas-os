package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

public interface MerchantApiConsumingRecordService {

    /**
     * api消费
     */
    public void apiConsuming(Map<String, Object> requestMap);

    /**
     * 分页查询
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);
}
