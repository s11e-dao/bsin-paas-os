package me.flyray.bsin.facade.engine;

import java.util.Map;

/**
 * 分佣引擎: 通过事件触发
 */

public interface BrokerageEngine {

    /**
     * 1、分销分佣引擎
     */
    void execute(Map<String, Object> requestMap);

}
