package me.flyray.bsin.server.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.flyray.bsin.domain.request.ExecuteParams;
import me.flyray.bsin.facade.service.DecisionEngineService;
import me.flyray.bsin.server.context.DubboHelper;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import me.flyray.bsin.server.context.DecisionEngineContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@EqualsAndHashCode(callSuper = false)
@ShenyuDubboService(value = "/decisionEngine",timeout = 20000)
public class DecisionEngineServiceImpl implements DecisionEngineService {

    @Autowired
    private DecisionEngineContextBuilder decisionEngineContextBuilder;

    private final DubboHelper dubboHelper;

    @Override
    @ShenyuDubboClient("/execute")
    public Map<?, ?> execute(ExecuteParams executeParams) {
        String eventKey = executeParams.getEventKey();
        KieContainer kieContainer = decisionEngineContextBuilder.getKieContainer();
        KieSession kieSession = kieContainer.newKieSession();
        LinkedHashMap<?, ?> resultMap = new LinkedHashMap<>();
        kieSession.setGlobal("resultMap", resultMap);
        kieSession.setGlobal("globalMap", dubboHelper);
        Map<?, ?> params = executeParams.getParams();
        if (params == null) kieSession.insert(new HashMap<>());
        else kieSession.insert(executeParams.getParams());
        kieSession.fireAllRules();
        kieSession.destroy();
        return resultMap;
    }
}
