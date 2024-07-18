package me.flyray.bsin.facade.service;


import me.flyray.bsin.domain.request.ExecuteParams;

import java.util.Map;

public interface DecisionEngineService {

    Map<?, ?> execute(ExecuteParams executeParams);

}
