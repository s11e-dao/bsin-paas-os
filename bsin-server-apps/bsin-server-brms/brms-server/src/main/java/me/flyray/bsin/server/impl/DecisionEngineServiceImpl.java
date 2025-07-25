package me.flyray.bsin.server.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import me.flyray.bsin.domain.entity.DecisionRule;
import me.flyray.bsin.domain.entity.EventModel;
import me.flyray.bsin.domain.enums.EventModelType;
import me.flyray.bsin.facade.engine.DecisionEngine;
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

@Log4j2
@Data
@EqualsAndHashCode(callSuper = false)
@ShenyuDubboService(value = "/decisionEngine",timeout = 20000)
public class DecisionEngineServiceImpl implements DecisionEngine {

    @Autowired
    private DecisionEngineContextBuilder decisionEngineContextBuilder;

    private final DubboHelper dubboHelper;
    @Autowired
    private DecisionRuleMapper decisionRuleMapper;

    @Autowired
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
    public Map<?, ?> execute(Map executeParams) throws JsonProcessingException {
        String eventCode = (String) executeParams.get("eventCode");
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("eventCode", eventCode);
        EventModel eventModel = eventModelService.getDetail(requestMap);
        // 判断事件模型类型
        if(EventModelType.FLOW_MODEL.equals(eventModel.getModelType()) || EventModelType.FORM_MODEL.equals(eventModel.getModelType())
            || EventModelType.INFERENCE_MODEL.equals(eventModel.getModelType())){
            return requestMap;
        }
        DecisionRule decisionRule = decisionRuleMapper.selectById(eventModel.getModelNo());
        Map<String, Object> globalMap = new LinkedHashMap<>();
        if(decisionRule == null){
            return globalMap;
        }
        // 1、构建决策引擎环境
        KieSession kieSession = decisionEngineContextBuilder.buildDecisionEngine(decisionRule,
                decisionRule.getKieBaseName() + "-session");
        kieSession.setGlobal("globalMap", globalMap);

        // 2、处理fact（事实对象，接收数据的对象实体类）
        Map<String, Object> params = decisionEngineContextBuilder.buildDecisionFact(decisionRule, executeParams);
        log.info("请求指标参数: {}", params);
        // 3、插入数据并执行规则
        kieSession.insert(params);
        kieSession.fireAllRules();
        kieSession.destroy();

        // 4、根据decisionRule中json afer配置和then的globalMap结果调用逻辑处理
        decisionEngineContextBuilder.handleThenResult(decisionRule, globalMap);

        return globalMap;
    }

}
