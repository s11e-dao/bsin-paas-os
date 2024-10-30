package me.flyray.bsin.facade.engine;


import me.flyray.bsin.domain.request.ExecuteParams;

import java.util.Map;

/**
 * 规则引擎执行服务
 */

public interface DecisionEngine {

    Map<?, ?> execute(ExecuteParams executeParams);

}
