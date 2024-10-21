package me.flyray.bsin.server.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.flyray.bsin.domain.entity.DecisionRule;
import me.flyray.bsin.domain.entity.EventModel;
import me.flyray.bsin.domain.request.ExecuteParams;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DecisionEngineService;
import me.flyray.bsin.facade.service.EventModelService;
import me.flyray.bsin.infrastructure.mapper.DecisionRuleMapper;
import me.flyray.bsin.server.context.DubboHelper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
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

    @DubboReference(version = "${dubbo.provider.version}")
    private EventModelService eventModelService;

    /**
     * 智能决策引擎执行入口
     * 1、构建决策引擎环境
     * 2、处理fact（决策事实对象，接收数据的对象实体类）
     * 3、插入数据并执行决策规则
     * 4、根据decisionRule中json afer配置和then的globalMap结果调用逻辑处理
     * 5、结果处理，同步作出决策触发业务处理，异步通过工作流人工干预，人工处理完成之后回调业务处理
     * @param executeParams
     * @return
     */
    @Override
    @ShenyuDubboClient("/execute")
    public Map<?, ?> execute(ExecuteParams executeParams) {
        String eventCode = executeParams.getEventCode();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("eventCode", eventCode);
        EventModel eventModel = eventModelService.getDetail(requestMap);
        DecisionRule decisionRule = decisionRuleMapper.selectById(eventModel.getModelNo());
        if(decisionRule == null){
            throw new BusinessException("事件模型不存在");
        }
        // 1、构建决策引擎环境
        KieSession kieSession = decisionEngineContextBuilder.buildDecisionEngine(decisionRule,
                decisionRule.getKieBaseName() + "-session");
        Map<String, Object> globalMap = new LinkedHashMap<>();
        kieSession.setGlobal("globalMap", globalMap);

        // 2、处理fact（事实对象，接收数据的对象实体类）
        Map<String, Object> params = decisionEngineContextBuilder.buildDecisionFact(decisionRule, executeParams);

        // 3、插入数据并执行规则
        kieSession.insert(params);
        kieSession.fireAllRules();
        kieSession.destroy();

        // 4、根据decisionRule中json afer配置和then的globalMap结果调用逻辑处理
        decisionEngineContextBuilder.handleThenResult(decisionRule,globalMap);

        return globalMap;
    }

}
