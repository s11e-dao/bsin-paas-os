package me.flyray.bsin.server.engine;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.AppAgent;
import me.flyray.bsin.domain.entity.KnowledgeBase;
import me.flyray.bsin.domain.request.ExecuteParams;
import me.flyray.bsin.dubbo.invoke.BsinServiceInvoke;
import me.flyray.bsin.facade.model.*;
import me.flyray.bsin.facade.node.*;
import me.flyray.bsin.facade.service.DecisionEngineService;
import me.flyray.bsin.infrastructure.mapper.KnowledgeBaseMapper;
import me.flyray.bsin.server.llm.AliDashscopeLlm;
import me.flyray.bsin.server.llm.LlmChat;
import me.flyray.bsin.server.milvus.BsinMilvusEmbeddingStore;
import me.flyray.bsin.server.milvus.BsinOsOperateCodeEmbeddingStore;
import me.flyray.bsin.server.milvus.BsinOsOperateCodeSegment;
import me.flyray.bsin.server.milvus.BsinTextSegment;
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
  @Autowired
  private KnowledgeBaseMapper knowledgeBaseMapper;
  @Autowired
  EmbeddingModel embeddingModel;
  @DubboReference(version = "${dubbo.provider.version}")
  private DecisionEngineService decisionEngineService;

  public Map<String, Object> startExecutors(AppAgent appAgent, String question) {
    // 编排数据
    JSONObject appAgentModel = JSONObject.parseObject(appAgent.getAppAgentModel());
    Map<String, Object> resultMap = new HashMap<>();
    try {
      // 将AI编排Json数据解析成Ai的Bpmn对象
      AiBpmnModel aipmnModel = aipmnModelParseService.parse(appAgentModel);
      // 根据节点连线组成的有序集合，默认当前设计只有一个流程，直接取get(0)
      AiProcess aiProcess = aipmnModel.getAiProcesses().get(0);
      // 获取流程节点顺序
      List<SequenceFlow> sequenceFlows = aipmnModel.getSequenceFlows();
      // 编排流程的开始节点
      FlowElement initialFlowElement = aiProcess.getInitialFlowElement();
      // TODO 意图指令识别处理，从关联的知识库进行指令检索，处理结果给到requetMap
      KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(appAgent.getKnowledgeBaseNo());
      // TODO 确定milvus的数据库

      Map<String, Object> requestMap = new HashMap<>();

      // 调用milvus向量数据库，检索指令
      BsinOsOperateCodeEmbeddingStore embeddingStore =
              BsinOsOperateCodeEmbeddingStore.builder().host("localhost").port(19530).collectionName("osOperateCode").dimension(512).build();
      Embedding queryEmbedding = embeddingModel.embed(question).content();
      List<EmbeddingMatch<BsinOsOperateCodeSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1);
      if(relevant != null && relevant.size() > 0){
        // 存在操作指令，则通过大语言模型和指令参数字段进行指令调用的参数拼装
        EmbeddingMatch<BsinOsOperateCodeSegment> embeddingMatch = relevant.get(0);
        log.info("AI OS 操作指令匹配得分: {}", embeddingMatch.score());
        log.info("AI OS 操作指令描述: {}", embeddingMatch.embedded().text());
        log.info("AI OS 操作指令: {}", embeddingMatch.embedded().getOpCode());
        log.info("AI OS 请求参数字段: {}", embeddingMatch.embedded().getParams());
        requestMap.put("hasOsOp",true);
        requestMap.put("opCode",embeddingMatch.embedded().getOpCode());
        requestMap.put("opCodeParams",embeddingMatch.embedded().getParams());
        requestMap.put("text",embeddingMatch.embedded().text());
        requestMap.put("question",question);
      }

      // 根据开始节点不断递归查找下一个节点并进行处理
      resultMap = handleFlowElement(initialFlowElement, sequenceFlows, requestMap);

    } catch (Exception e) {
      log.error("An unexpected error occurred: {}", e.getMessage(), e);
      // 处理其他未捕获的异常
      resultMap.put("error", "An unexpected error occurred.");
    }

    return resultMap;
  }

  /**
   * 递归查找下一个节点并执行节点操作
   * @param currentFlowElement
   * @param sequenceFlows
   * @return
   */
  public Map<String, Object> handleFlowElement(FlowElement currentFlowElement, List<SequenceFlow> sequenceFlows, Map<String, Object> requestMap) {
    Map<String, Object> resultMap = new HashMap<>();
    try {
      // 处理当前节点的业务逻辑
      resultMap = processCurrentFlowElement(currentFlowElement, requestMap);
      // 递归查找下一个节点
      FlowElement nextFlowElement = getNextFlowElement(currentFlowElement, sequenceFlows);
      // 如果存在下一个节点，递归处理下一个节点, 并将上一个节点处理的结果作为下一个节点处理的入参
      if (nextFlowElement != null) {
        Map<String, Object> nextResultMap = handleFlowElement(nextFlowElement, sequenceFlows, resultMap);
        // 合并当前节点和上一个节点的结果，作为下一个节点的入参
        resultMap.putAll(nextResultMap);
      }
    } catch (Exception e) {
      log.error("An error occurred while handling flow element: {}", e.getMessage(), e);
      resultMap.put("error", "An error occurred in the flow element.");
    }

    return resultMap;
  }

  /**
   * 处理当前节点的业务逻辑
   * @param flowNode
   * @return
   */
  private Map<String, Object> processCurrentFlowElement(FlowElement flowNode, Map<String, Object> requestMap) {
    Map<String, Object> resultMap = new HashMap<>();
    try {
      // 在此处执行与当前节点相关的操作, 例如，根据节点类型进行不同的处理
      if (flowNode instanceof StartEvent) {
        resultMap = handleStartEvent((StartEvent) flowNode);
      } else if (flowNode instanceof LlmAgent) {
        resultMap = handleLlmAgent((LlmAgent) flowNode,requestMap);
      } else if (flowNode instanceof RuleAgent) {
        resultMap = handleRuleAgent((RuleAgent) flowNode,requestMap);
      } else if (flowNode instanceof DubboAgent) {
        resultMap = handleDubboAgent((DubboAgent) flowNode,requestMap);
      } else if (flowNode instanceof EndEvent) {
        resultMap = handleEndEvent(requestMap);
      }
    } catch (NoApiKeyException | InputRequiredException e) {
      log.error("Error processing current flow element: {}", e.getMessage(), e);
      resultMap.put("error", e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected error processing current flow element: {}", e.getMessage(), e);
      resultMap.put("error", "Unexpected error occurred in processing flow element.");
    }
    resultMap.putAll(requestMap);
    return resultMap;
  }

  /**
   * 处理开始节点的逻辑
   * 返回开场白
   */
  private Map<String, Object> handleStartEvent(StartEvent flowNode) {
    log.info("开始节点");
    Map resultMap = new HashMap<>();
    // 开场白信息，从flowNode节点配置中获取
    resultMap.put("prologue",flowNode.getPrologue());
    return resultMap;
  }

  /**
   * 处理大模型节点的逻辑
   * @return 大模型处理结果
   */
  private Map<String, Object> handleLlmAgent(LlmAgent flowNode, Map<String, Object> requestMap) throws NoApiKeyException, InputRequiredException {
    log.info("大模型节点");
    // 判断是否需要os指令处理
    Boolean hasOsOp = (Boolean) requestMap.get("hasOsOp");
    if(hasOsOp){

    }

    LlmChat llmChat = new AliDashscopeLlm();
    return llmChat.chat(requestMap);
  }

  /**
   * 处理规则引擎节点的逻辑
   * @return 规则引擎处理结果
   */
  private Map<String, Object> handleRuleAgent(RuleAgent flowNode, Map<String, Object> requestMap) {
    log.info("规则引擎节点");
    ExecuteParams executeParams = new ExecuteParams();
    executeParams.setEventCode("appAgentChat");
    executeParams.setJsonParams(JSONObject.from(new JSONObject(requestMap)));
    // decisionEngineService.execute(executeParams);

    return new HashMap<>(); // 替换为实际的返回值
  }

  /**
   * 处理Dubbo调用节点的逻辑
   * @return Dubbo调用结果
   */
  private Map<String, Object> handleDubboAgent(DubboAgent flowNode, Map<String, Object> requestMap) throws JsonProcessingException {
    log.info("dubbo调用节点，请求参数: {}", requestMap.get("jsonAnswer"));
    String jsonAnswer = (String) requestMap.get("jsonAnswer");
    Map dubboRequest = new ObjectMapper().readValue(jsonAnswer, Map.class);
    Object object = bsinServiceInvoke.genericInvoke("","",null,dubboRequest);
    // 处理请求参数
    return new HashMap<>(); // 替换为实际的返回值
  }

  /**
   * 处理结束节点的逻辑
   * @param requestMap 请求数据
   * @return 结束节点处理结果
   */
  private Map<String, Object> handleEndEvent(Map<String, Object> requestMap) {
    log.info("结束节点");
    Map<String, Object> resultMap = new HashMap<>();

    resultMap.putAll(requestMap);
    return resultMap;
  }

  /**
   * 获取下一个节点
   * @param currentFlowElement
   * @param sequenceFlows
   * @return 下一个流程节点
   */
  private FlowElement getNextFlowElement(FlowElement currentFlowElement, List<SequenceFlow> sequenceFlows) {
    return sequenceFlows.stream()
            .filter(flow -> flow.getSourceRef().equals(currentFlowElement.getId()))
            .map(SequenceFlow::getTargetFlowElement)
            .findFirst()
            .orElse(null);
  }

}
