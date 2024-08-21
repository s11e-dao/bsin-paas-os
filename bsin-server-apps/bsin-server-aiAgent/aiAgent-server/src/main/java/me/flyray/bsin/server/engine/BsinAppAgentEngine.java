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

    AiBpmnModel aipmnModel = aipmnModelParseService.parse(appAgentModel);

    // 根据节点连线组成的有序集合，默认当前设计只有一个流程，直接取get(0)
    AiProcess aiProcess = aipmnModel.getAiProcesses().get(0);
    // 流程节点
    List<FlowElement> taskNodeList = aiProcess.getFlowElements();
    // 流程顺序
    List<SequenceFlow> sequenceFlows = aipmnModel.getSequenceFlows();

    // 编排流程的开始节点
    FlowElement initialFlowElement = aiProcess.getInitialFlowElement();
    // 节点类型： startEvent type: 'start', (开始节点) 开场白和角色设置
    log.info("开始节点开始");

    log.info("开始节点结束");
    // 根据开始节点不断递归查找下一个节点并进行处理
    Map<String, Object> resultMap = handleFlowElement(initialFlowElement, sequenceFlows);

    return resultMap;
  }

  /**
   * 递归查找下一个节点并执行节点操作
   * @param currentFlowElement
   * @param sequenceFlows
   * @return
   */
  public Map<String, Object> handleFlowElement(FlowElement currentFlowElement, List<SequenceFlow> sequenceFlows) {
    // 处理当前节点的业务逻辑
    Map<String, Object> resultMap = processCurrentFlowElement(currentFlowElement);

    // 递归查找下一个节点
    FlowElement nextFlowElement = getNextFlowElement(currentFlowElement, sequenceFlows);

    // 如果存在下一个节点，递归处理下一个节点
    if (nextFlowElement != null) {
      Map<String, Object> nextResultMap = handleFlowElement(nextFlowElement, sequenceFlows);
      // 合并当前节点和下一个节点的结果
      resultMap.putAll(nextResultMap);
    }
    return resultMap;
  }

  /**
   * 处理当前节点的业务逻辑
   * @param flowNode
   * @return
   */
  private Map<String, Object> processCurrentFlowElement(FlowElement flowNode) {
    Map<String, Object> resultMap = new HashMap<>();

    if (flowNode instanceof LlmAgent) {
      /** 节点类型： ai-agent 调用大模型，(有输入输出) type: 'aiAgent', data{ 调用llm input output } */
      log.info("大模型节点开始");
      LlmChat llmChat = new AliDashscopeLlm();
      llmChat.chat();
      log.info("大模型节点结束");
    } else if (flowNode instanceof RuleAgent) {
      /** 节点类型： rule 调用规则引擎 type: 'rule', data{ eventCode } */
      log.info("规则引擎节点开始");
      ExecuteParams executeParams = new ExecuteParams();
      executeParams.setEventCode("chat");
      // decisionEngineService.execute(executeParams);
      log.info("规则引擎节点结束");
    } else if (flowNode instanceof DubboAgent) {
      /** 节点类型： fetchApi 调用api (有输入输出) type: 'fetchApi', data{ url content-type input output } */
      log.info("dubbo调用节点开始");
      // bsinServiceInvoke.genericInvoke(null,null,null,null);
      log.info("dubbo调用节点结束");
    } else if (flowNode instanceof EndEvent) {
      log.info("结束节点开始");

      log.info("结束节点结束");
    }

    // 在此处执行与当前节点相关的操作
    // 例如，根据节点类型进行不同的处理
    System.out.println("Processing FlowElement: " + flowNode.getId());
    resultMap.put(flowNode.getId(), "Processed");

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
