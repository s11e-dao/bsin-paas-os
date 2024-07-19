package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.flyray.bsin.domain.entity.DecisionRule;
import me.flyray.bsin.domain.entity.EventModel;
import me.flyray.bsin.domain.request.ExecuteParams;
import me.flyray.bsin.facade.service.DecisionEngineService;
import me.flyray.bsin.infrastructure.mapper.DecisionRuleMapper;
import me.flyray.bsin.infrastructure.mapper.EventModelMapper;
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
    @Autowired
    private DecisionRuleMapper decisionRuleMapper;
    @Autowired
    private EventModelMapper eventModelMapper;

    @Override
    @ShenyuDubboClient("/execute")
    public Map<?, ?> execute(ExecuteParams executeParams) {
        String eventCode = executeParams.getEventCode();
        LambdaQueryWrapper<EventModel> warapper = new LambdaQueryWrapper<>();
        warapper.eq(EventModel::getEventCode, eventCode);
        EventModel eventModel = eventModelMapper.selectOne(warapper);
        DecisionRule decisionRule = decisionRuleMapper.selectById(eventModel.getModelNo());
        // 1、构建决策引擎环境
        KieSession kieSession = decisionEngineContextBuilder.buildDecisionEngine(decisionRule,
                decisionRule.getKieBaseName() + "-session");
        LinkedHashMap<?, ?> resultMap = new LinkedHashMap<>();
        kieSession.setGlobal("resultMap", resultMap);
        kieSession.setGlobal("globalMap", dubboHelper);
        // 添加fact（事实对象，接收数据的对象实体类）
        Map<?, ?> params = executeParams.getParams();
        if (params == null) kieSession.insert(new HashMap<>());
        else kieSession.insert(executeParams.getParams());
        kieSession.fireAllRules();
        kieSession.destroy();
        return resultMap;
    }
}
