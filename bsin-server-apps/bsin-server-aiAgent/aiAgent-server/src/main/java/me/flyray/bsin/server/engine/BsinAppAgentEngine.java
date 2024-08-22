package me.flyray.bsin.server.engine;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.request.ExecuteParams;
import me.flyray.bsin.dubbo.invoke.BsinServiceInvoke;
import me.flyray.bsin.facade.model.*;
import me.flyray.bsin.facade.node.*;
import me.flyray.bsin.facade.service.DecisionEngineService;
import me.flyray.bsin.server.llm.AliDashscopeLlm;
import me.flyray.bsin.server.llm.LlmChat;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bolei
 * @date 2023/7/21 9:09
 * @desc
 */

@Slf4j
@Service
public class BsinAppAgentEngine {

  @Autowired
  private AiBpmnModelParseService aipmnModelParseService;
  @Autowired
  private BsinServiceInvoke bsinServiceInvoke;

  @DubboReference(version = "${dubbo.provider.version}")
  private DecisionEngineService decisionEngineService;

  public Map<String, Object> startExecutors(JSONObject appAgentModel) {

    // 将AI编排Json数据解析成Ai的Bpmn对象
    AiBpmnModel aipmnModel = aipmnModelParseService.parse(appAgentModel);
    // 根据节点连线组成的有序集合，默认当前设计只有一个流程，直接取get(0)
    AiProcess aiProcess = aipmnModel.getAiProcesses().get(0);
    // 获取所有流程节点
    List<FlowElement> taskNodeList = aiProcess.getFlowElements();
    // 获取流程节点顺序
    List<SequenceFlow> sequenceFlows = aipmnModel.getSequenceFlows();
    // 编排流程的开始节点
    FlowElement initialFlowElement = aiProcess.getInitialFlowElement();
    // 根据开始节点不断递归查找下一个节点并进行处理
    Map<String, Object> resultMap = handleFlowElement(initialFlowElement, sequenceFlows, null);

    return resultMap;
  }

  /**
   * 递归查找下一个节点并执行节点操作
   * @param currentFlowElement
   * @param sequenceFlows
   * @return
   */
  public Map<String, Object> handleFlowElement(FlowElement currentFlowElement, List<SequenceFlow> sequenceFlows, Map<String, Object> requetMap) {
    // 处理当前节点的业务逻辑
    Map<String, Object> resultMap = processCurrentFlowElement(currentFlowElement,requetMap);
    // 递归查找下一个节点
    FlowElement nextFlowElement = getNextFlowElement(currentFlowElement, sequenceFlows);
    // 如果存在下一个节点，递归处理下一个节点, 并将上一个节点处理的结果作为下一个节点处理的入参
    if (nextFlowElement != null) {
      Map<String, Object> nextResultMap = handleFlowElement(nextFlowElement, sequenceFlows, resultMap);
      // 合并当前节点和上一个节点的结果，作为下一个节点的入参
      resultMap.putAll(nextResultMap);
    }
    return resultMap;
  }

  /**
   * 处理当前节点的业务逻辑
   * @param flowNode
   * @return
   */
  private Map<String, Object> processCurrentFlowElement(FlowElement flowNode, Map<String, Object> requetMap) {
    Map<String, Object> resultMap = new HashMap<>();

    // 在此处执行与当前节点相关的操作, 例如，根据节点类型进行不同的处理
    if (flowNode instanceof StartEvent) {
      // 节点类型： startEvent type: 'start', (开始节点) 开场白和角色设置
      log.info("开始节点开始");

      log.info("开始节点结束");
    } else if (flowNode instanceof LlmAgent) {
      // 对用户意图的理解，理解之后决定是否会调用dubbo
      log.info("大模型节点开始");
      LlmChat llmChat = new AliDashscopeLlm();
      resultMap = llmChat.chat();
      log.info("大模型节点结束");
    } else if (flowNode instanceof RuleAgent) {
      /** 节点类型： rule 调用规则引擎 type: 'rule', data{ eventCode } */
      log.info("规则引擎节点开始");
      ExecuteParams executeParams = new ExecuteParams();
      // 固定eventCode为appAgentChat
      executeParams.setEventCode("appAgentChat");
      // resultMap = decisionEngineService.execute(executeParams);
      log.info("规则引擎节点结束");
    } else if (flowNode instanceof DubboAgent) {
      /** 节点类型： fetchApi 调用api (有输入输出) type: 'fetchApi', data{ url content-type input output } */
      log.info("dubbo调用节点开始");
      // 指令查询，如何指令存在，则根据指令调用dubbo服务器
      // resultMap = bsinServiceInvoke.genericInvoke(null,null,null,null);
      log.info("dubbo调用节点结束");
    } else if (flowNode instanceof KnowledgeBaseAgent) {
      log.info("知识库节点开始");

      log.info("知识库节点结束");
    } else if (flowNode instanceof EndEvent) {
      log.info("结束节点开始");
      if(resultMap == null){
        resultMap = new HashMap<>();
      }
      // 加工处理requetMap和resultMap
      resultMap.putAll(requetMap);
      resultMap.put(flowNode.getId(), "对话返回结果");
      log.info("结束节点结束");
    }
    return resultMap;
  }

  /**
   * 获取下一个节点
   * @param currentFlowElement
   * @param sequenceFlows
   * @return
   */
  private FlowElement getNextFlowElement(FlowElement currentFlowElement, List<SequenceFlow> sequenceFlows) {
    for (SequenceFlow flow : sequenceFlows) {
      if (flow.getSourceRef().equals(currentFlowElement.getId())) {
        return flow.getTargetFlowElement();
      }
    }
    return null; // 如果没有找到下一个节点，返回null
  }

}
