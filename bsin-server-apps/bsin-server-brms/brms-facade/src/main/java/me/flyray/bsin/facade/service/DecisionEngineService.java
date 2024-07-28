package me.flyray.bsin.facade.service;


import me.flyray.bsin.domain.request.ExecuteParams;

import java.util.Map;

/**
 * 规则引擎执行服务
 */

public interface DecisionEngineService {

    Map<?, ?> execute(ExecuteParams executeParams);

}
