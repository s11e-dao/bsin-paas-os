package me.flyray.bsin.server.engine;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import me.flyray.bsin.facade.node.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.model.AiProcess;
import me.flyray.bsin.facade.model.AiBpmnModel;
import me.flyray.bsin.facade.model.FlowElement;
import me.flyray.bsin.facade.model.FlowNode;
import me.flyray.bsin.facade.model.SequenceFlow;

/**
 * @author bolei
 * @date 2023/7/28 11:22
 * @desc
 */
@Slf4j
@Component
public class AiBpmnModelParseService {

  /** 解析json数据 */
  public AiBpmnModel parse(JSONObject jsonObejct) {
    // 解析成 AipmnModel
    AiBpmnModel aiBpmnModel = new AiBpmnModel();
    // 将json解析成模型对象
    parseToAipmnModel(jsonObejct, aiBpmnModel);
    // TODO 根据联系排序节点
    // 排序节点及获得节点的数据 节点数据处理(ai_ru_variable：存储节点处理结果数据)
    // ai_copipot_model ai_copipot_ru_actinst(运行实例) ai_copipot_ru_variable
    // 执行节点流程
    return aiBpmnModel;
  }

  /**
   * 将保存的模型数据解析成模型对象 先找到第一个节点在不断的查询下一个节点
   *
   * @param jsonObejct
   */
  private void parseToAipmnModel(JSONObject jsonObejct, AiBpmnModel aiBpmnModel) {
    // 解析流程
    List<AiProcess> aiProcesses = new ArrayList<>();
    AiProcess aiProcess = new AiProcess();

    List<FlowElement> flowElements = new ArrayList<>();
    // 获取到节点信息
    JSONArray nodes = (JSONArray) jsonObejct.get("nodes");
    Iterator nodesIter = nodes.iterator();
    while (nodesIter.hasNext()) {
      JSONObject nodeJson = (JSONObject) nodesIter.next();
      // 处理边线,输入线
      List<SequenceFlow> incomingFlows = new ArrayList<>();
      SequenceFlow intSequenceFlow = new SequenceFlow();
      intSequenceFlow.setId(("1"));
      incomingFlows.add(intSequenceFlow);
      // 输出线
      List<SequenceFlow> outgoingFlows = new ArrayList<>();
      SequenceFlow outSequenceFlow = new SequenceFlow();
      outSequenceFlow.setId(("1"));
      outgoingFlows.add(outSequenceFlow);

      // TODO 处理
      FlowElement flowElement;
      String type = (String) nodeJson.get("type");
      if ("startEvent".equals(type)) {
        StartEvent startEvent = new StartEvent();
        startEvent.setId((String) nodeJson.get("id"));
        JSONObject dataJson = (JSONObject) nodeJson.get("data");
        startEvent.setName((String) dataJson.get("label"));

        startEvent.setIncomingFlows(incomingFlows);
        startEvent.setOutgoingFlows(outgoingFlows);

        flowElements.add(startEvent);
      } else if ("endEvent".equals(type)) {
        EndEvent endEvent = new EndEvent();
        endEvent.setId((String) nodeJson.get("id"));
        JSONObject dataJson = (JSONObject) nodeJson.get("data");
        endEvent.setName((String) dataJson.get("label"));

        endEvent.setIncomingFlows(incomingFlows);
        endEvent.setOutgoingFlows(outgoingFlows);

        flowElements.add(endEvent);
      } else if ("llm".equals(type)) {
        LlmAgent llmAgent = new LlmAgent();
        llmAgent.setId((String) nodeJson.get("id"));
        JSONObject dataJson = (JSONObject) nodeJson.get("data");
        llmAgent.setName((String) dataJson.get("label"));

        llmAgent.setIncomingFlows(incomingFlows);
        llmAgent.setOutgoingFlows(outgoingFlows);
        flowElements.add(llmAgent);
      }else if ("rule".equals(type)) {
        // 规则应用调用
        RuleAgent ruleAgent = new RuleAgent();
        ruleAgent.setId((String) nodeJson.get("id"));
        JSONObject dataJson = (JSONObject) nodeJson.get("data");
        ruleAgent.setName((String) dataJson.get("label"));

        ruleAgent.setIncomingFlows(incomingFlows);
        ruleAgent.setOutgoingFlows(outgoingFlows);
        flowElements.add(ruleAgent);
      }else if ("dubboInvoke".equals(type)) {
        // dubbo服务调用
        DubboAgent dubboAgent = new DubboAgent();
        dubboAgent.setId((String) nodeJson.get("id"));
        JSONObject dataJson = (JSONObject) nodeJson.get("data");
        dubboAgent.setName((String) dataJson.get("label"));

        dubboAgent.setIncomingFlows(incomingFlows);
        dubboAgent.setOutgoingFlows(outgoingFlows);
        flowElements.add(dubboAgent);
      }
    }
    aiProcess.setFlowElements(flowElements);
    aiProcesses.add(aiProcess);

    aiBpmnModel.setAiProcesses(aiProcesses);

    // 找到开始节点
    FlowElement startEvent = findFirstFlowElement(aiBpmnModel);
    aiProcess.setInitialFlowElement(startEvent);

    // 根据开始节点顺序找到下一个节点，放到有序集合中
    List<SequenceFlow> sequenceFlows = new ArrayList<>();
    // 获取到连线 处理边线
    JSONArray edges = (JSONArray) jsonObejct.get("edges");
    Iterator edgesIter = edges.iterator();
    while (edgesIter.hasNext()) {
      JSONObject edgesJson = (JSONObject) edgesIter.next();
      SequenceFlow sequenceFlow = new SequenceFlow();
      sequenceFlow.setId((String) edgesJson.get("id"));
      sequenceFlow.setSourceRef((String) edgesJson.get("source"));
      sequenceFlow.setTargetRef((String) edgesJson.get("target"));
      sequenceFlow.setSourceFlowElement(getFlowElement(flowElements, (String) edgesJson.get("source")));
      sequenceFlow.setTargetFlowElement(getFlowElement(flowElements, (String) edgesJson.get("target")));
      sequenceFlows.add(sequenceFlow);
    }
    aiBpmnModel.setSequenceFlows(sequenceFlows);
  }

  /** 找出第一个处理节点 */
  private FlowElement findFirstFlowElement(AiBpmnModel aipmnModel) {
    for (FlowElement flowElement : aipmnModel.getAiProcesses().get(0).getFlowElements()) {
      if (flowElement instanceof StartEvent) {
        return flowElement;
      }
    }
    return null;
  }

  /***
   * 根据ID找到FlowElement
   * @param flowElements
   * @param targetRef
   * @return
   */
  private FlowElement getFlowElement(Collection<FlowElement> flowElements, String targetRef) {
    List<FlowElement> targetFlowElements =
        flowElements.stream()
            .filter(
                flowElement ->
                    !StringUtils.isEmpty(flowElement.getId())
                        && flowElement.getId().equals(targetRef))
            .collect(Collectors.toList());
    if (targetFlowElements.size() > 0) {
      return targetFlowElements.get(0);
    }
    return null;
  }

  /***
   * 找到表达式成立的sequenceFlow的
   * @param outgoingFlows
   * @return
   */
  private SequenceFlow getSequenceFlow(List<SequenceFlow> outgoingFlows) {

    List<SequenceFlow> sequenceFlows =
        outgoingFlows.stream()
            .filter(
                item -> {
                  Object execConditionExpressionResult = null;
                  boolean re = false;
                  try {
                    execConditionExpressionResult =
                        null; // this.getElValue(item.getConditionExpression());
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                  if (execConditionExpressionResult.equals("true")) {
                    re = true;
                  }
                  return (Boolean) execConditionExpressionResult;
                })
            .collect(Collectors.toList());
    if (sequenceFlows.size() > 0) {
      return sequenceFlows.get(0);
    }
    return null;
  }

}
