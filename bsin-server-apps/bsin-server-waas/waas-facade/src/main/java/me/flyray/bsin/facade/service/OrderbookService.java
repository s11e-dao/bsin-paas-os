package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/26 15:00
 * @desc 数字资产订单簿
 */

public interface OrderbookService {

    /**
     * 挂单服务：卖单、买单
     * buy sell
     */
    Map<String, Object> maker(Map<String, Object> requestMap);

    /**
     * 吃单
     */
    Map<String, Object> taker(Map<String, Object> requestMap);

    /**
     * 取消订单
     */
    Map<String, Object> cancel(Map<String, Object> requestMap);

    /**
     * 租户和商户的数字资产交易数据
     */
    Map<String, Object> getPageList(Map<String, Object> requestMap);

    /**
     * 市集挂单详情
     */
    Map<String, Object> getDetail(Map<String, Object> requestMap);

}
