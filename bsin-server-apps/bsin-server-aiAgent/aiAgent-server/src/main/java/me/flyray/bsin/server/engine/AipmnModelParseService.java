package me.flyray.bsin.server.engine;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.model.AiProcess;
import me.flyray.bsin.facade.model.AipmnModel;
import me.flyray.bsin.facade.model.ChatgptAgent;
import me.flyray.bsin.facade.model.EndEvent;
import me.flyray.bsin.facade.model.FlowElement;
import me.flyray.bsin.facade.model.FlowNode;
import me.flyray.bsin.facade.model.SequenceFlow;
import me.flyray.bsin.facade.model.StartEvent;

/**
 * @author bolei
 * @date 2023/7/28 11:22
 * @desc
 */
@Slf4j
@Component
public class AipmnModelParseService {

  /** 解析json数据 */
  public AipmnModel parse(JSONObject jsonObejct) {
    // 解析成 AipmnModel
    AipmnModel aipmnModel = new AipmnModel();
    // 将json解析成模型对象
    parseToAipmnModel(jsonObejct, aipmnModel);
    // TODO 根据联系排序节点
    // 排序节点及获得节点的数据 节点数据处理(ai_ru_variable：存储节点处理结果数据)
    // ai_copipot_model ai_copipot_ru_actinst(运行实例) ai_copipot_ru_variable
    // 执行节点流程
    execute(aipmnModel);

    return aipmnModel;
  }

  /**
   * 将保存的模型数据解析成模型对象 先找到第一个节点在不断的查询下一个节点
   *
   * @param jsonObejct
   */
  private void parseToAipmnModel(JSONObject jsonObejct, AipmnModel aipmnModel) {
    // 解析流程
    List<AiProcess> aiProcesses = new ArrayList<>();
    AiProcess aiProcess = new AiProcess();

    List<FlowElement> flowElements = new ArrayList<>();
    // 获取到节点信息
    JSONArray nodes = (JSONArray) jsonObejct.get("nodes");
    Iterator nodesIter = nodes.iterator();
    while (nodesIter.hasNext()) {
      JSONObject nodeJson = (JSONObject) nodesIter.next();

      // 处理边线
      // 输入线
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
        ChatgptAgent chatgptAgent = new ChatgptAgent();
        chatgptAgent.setId((String) nodeJson.get("id"));
        JSONObject dataJson = (JSONObject) nodeJson.get("data");
        chatgptAgent.setName((String) dataJson.get("label"));

        chatgptAgent.setIncomingFlows(incomingFlows);
        chatgptAgent.setOutgoingFlows(outgoingFlows);

        flowElements.add(chatgptAgent);
      }
    }
    aiProcess.setFlowElements(flowElements);
    aiProcesses.add(aiProcess);

    aipmnModel.setAiProcesses(aiProcesses);

    // 找到开始节点
    FlowElement startEvent = findFirstFlowElement(aipmnModel);
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
      sequenceFlows.add(sequenceFlow);
    }
    aipmnModel.setSequenceFlows(sequenceFlows);
  }

  /** 找出第一个处理节点 */
  private FlowElement execute(AipmnModel aipmnModel) {
    // 根据节点类型执行

    List<FlowElement> taskNodeList = aipmnModel.getAiProcesses().get(0).getFlowElements();

    for (FlowElement flowNode : taskNodeList) {
      if (flowNode instanceof StartEvent) {
        log.info("-----------------");
        log.info("开始节点开始");
        log.info("-----------------");
        log.info("开始节点结束");
      } else if (flowNode instanceof ChatgptAgent) {
        log.info("-----------------");
        log.info("chatGPT节点开始");

        log.info("-----------------");
        log.info("chatGPT节点结束");

      } else if (flowNode instanceof EndEvent) {
        log.info("-----------------");
        log.info("结束节点开始");
        log.info("-----------------");
        log.info("结束节点结束");
      }
    }

    return null;
  }

  /** 找出第一个处理节点 */
  private FlowElement findFirstFlowElement(AipmnModel aipmnModel) {
    for (FlowElement flowElement : aipmnModel.getAiProcesses().get(0).getFlowElements()) {
      if (flowElement instanceof StartEvent) {
        return flowElement;
      }
    }
    return null;
  }

  private void getFlowElementList(List<FlowNode> taskList, Collection<FlowElement> flowElements) {

    // 获取第一个UserTask
    FlowElement startElement =
        flowElements.stream()
            .filter(flowElement -> flowElement instanceof StartEvent)
            .collect(Collectors.toList())
            .get(0);

    List<SequenceFlow> outgoingFlows = ((StartEvent) startElement).getOutgoingFlows();

    // 选择对外数据
    String targetRef = outgoingFlows.get(0).getTargetRef();

    // 根据ID找到FlowElement
    FlowElement targetElementOfStartElement = getFlowElement(flowElements, targetRef);
    this.getTaskList(taskList, flowElements, targetElementOfStartElement);
  }

  /***
   * 根据bpmnmodel获取流程节点的顺序信息
   * @param taskList
   * @param flowElements
   * @param curFlowElement
   */
  private void getTaskList(
      List<FlowNode> taskList, Collection<FlowElement> flowElements, FlowElement curFlowElement) {
    if (curFlowElement == null && taskList.size() == 0) {
      // 获取第一个UserTask
      FlowElement startElement =
          flowElements.stream()
              .filter(flowElement -> flowElement instanceof StartEvent)
              .collect(Collectors.toList())
              .get(0);
      List<SequenceFlow> outgoingFlows = ((StartEvent) startElement).getOutgoingFlows();
      String targetRef = outgoingFlows.get(0).getTargetRef();
      // 根据ID找到FlowElement
      FlowElement targetElementOfStartElement = getFlowElement(flowElements, targetRef);
      if (targetElementOfStartElement instanceof ChatgptAgent) {
        this.getTaskList(taskList, flowElements, targetElementOfStartElement);
      }

    } else if (curFlowElement instanceof StartEvent) {
      taskList.add((FlowNode) curFlowElement);
      String targetRef = "";
      List<SequenceFlow> outgoingFlows = ((ChatgptAgent) curFlowElement).getOutgoingFlows();
      if (outgoingFlows.size() == 1) {
        targetRef = outgoingFlows.get(0).getTargetRef();
      } else {
        // 找到表达式成立的sequenceFlow的
        SequenceFlow sequenceFlow = getSequenceFlow(outgoingFlows);
        if (sequenceFlow != null) {
          targetRef = sequenceFlow.getTargetRef();
        }
      }
      // 根据ID找到FlowElement
      FlowElement targetElement = getFlowElement(flowElements, targetRef);

      this.getTaskList(taskList, flowElements, targetElement);
    } else if (curFlowElement instanceof EndEvent) {
      taskList.add((FlowNode) curFlowElement);
      String targetRef = "";
      List<SequenceFlow> outgoingFlows = ((ChatgptAgent) curFlowElement).getOutgoingFlows();
      if (outgoingFlows.size() == 1) {
        targetRef = outgoingFlows.get(0).getTargetRef();
      } else {
        // 找到表达式成立的sequenceFlow的
        SequenceFlow sequenceFlow = getSequenceFlow(outgoingFlows);
        if (sequenceFlow != null) {
          targetRef = sequenceFlow.getTargetRef();
        }
      }
      // 根据ID找到FlowElement
      FlowElement targetElement = getFlowElement(flowElements, targetRef);

      this.getTaskList(taskList, flowElements, targetElement);
    }
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
