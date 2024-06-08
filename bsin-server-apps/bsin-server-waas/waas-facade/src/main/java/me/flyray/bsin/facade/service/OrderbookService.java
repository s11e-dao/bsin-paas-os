package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.Orderbook;
import me.flyray.bsin.facade.response.DigitalAssetsDetailRes;

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
    Orderbook maker(Map<String, Object> requestMap);

    /**
     * 吃单
     */
    Orderbook taker(Map<String, Object> requestMap);

    /**
     * 取消订单
     */
    void cancel(Map<String, Object> requestMap);

    /**
     * 租户和商户的数字资产交易数据
     */
    IPage<?> getPageList(Map<String, Object> requestMap);

    /**
     * 市集挂单详情
     */
    DigitalAssetsDetailRes getDetail(Map<String, Object> requestMap);

}
