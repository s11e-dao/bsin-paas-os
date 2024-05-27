package me.flyray.bsin.facade.service;

import java.util.Map;

public interface MerchantApiConsumingRecordService {

    /**
     * api消费
     */
    public Map<String,Object> apiConsuming(Map<String, Object> requestMap);

    /**
     * 分页查询
     */
    public Map<String,Object> getPageList(Map<String, Object> requestMap);
}
