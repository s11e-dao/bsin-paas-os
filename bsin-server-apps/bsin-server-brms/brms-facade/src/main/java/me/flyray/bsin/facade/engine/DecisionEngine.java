package me.flyray.bsin.facade.engine;


import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

/**
 * 规则引擎执行服务
 */

public interface DecisionEngine {

    Map<?, ?> execute(Map executeParams) throws JsonProcessingException;

}
