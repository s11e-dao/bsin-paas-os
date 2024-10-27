package me.flyray.bsin.facade.engine;

import java.util.Map;

/**
 * 分佣引擎
 */

public interface BrokerageEngine {

    /**
     * 1、分销分佣引擎
     */
    public void excute(Map<String, Object> requestMap);

}
