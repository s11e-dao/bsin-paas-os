package me.flyray.bsin.facade.engine;



import org.apache.shenyu.plugin.rulesEngine.ExecuteParams;

import java.util.Map;

/**
 * 规则引擎执行服务
 */

public interface DecisionEngine {

    Map<?, ?> execute(Map executeParams);

}
