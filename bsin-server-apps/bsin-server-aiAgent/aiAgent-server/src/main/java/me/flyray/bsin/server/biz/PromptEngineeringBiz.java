package me.flyray.bsin.server.biz;

import dev.langchain4j.data.message.*;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.InternalOpenAiHelper;
import me.flyray.bsin.domain.entity.PromptMessage;
import me.flyray.bsin.domain.entity.PromptTemplateParam;
import me.flyray.bsin.domain.enums.VectorStoreType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.infrastructure.mapper.PromptTemplateMapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leonard
 * @description
 * @createDate 2023/12/2023/12/16 /20/57
 */
@Component
public class PromptEngineeringBiz {

  @Autowired private PromptTemplateMapper promptTemplateMapper;
  @Autowired private VectorRetrievalBiz vectorRetrievalBiz;

  public UserMessage generateUserMessage(String name, String template, String content) {
    PromptTemplate promptTemplate = new PromptTemplate(template);
    Map<String, Object> variables = new HashMap<>();
    variables.put(name, content);
    Prompt prompt = promptTemplate.apply(variables);
    String newText = prompt.text().replace("&#10;", "\n").replace("&quot;", "'");
    return new Prompt(newText).toUserMessage();
  }

  public AiMessage generateAiMessage(String name, String template, String content) {
    PromptTemplate promptTemplate = new PromptTemplate(template);
    Map<String, Object> variables = new HashMap<>();
    variables.put(name, content);
    Prompt prompt = promptTemplate.apply(variables);
    String newText = prompt.text().replace("&#10;", "\n").replace("&quot;", "'");
    return new Prompt(newText).toAiMessage();
  }

  public SystemMessage generateSystemMessage(String name, String template, String content) {
    PromptTemplate promptTemplate = new PromptTemplate(template);
    Map<String, Object> variables = new HashMap<>();
    variables.put(name, content);
    Prompt prompt = promptTemplate.apply(variables);
    String newText = prompt.text().replace("&#10;", "\n").replace("&quot;", "'");
    return new Prompt(newText).toSystemMessage();
  }

  public List<PromptMessage> toPromptMessages(List<ChatMessage> chatMessages) {
    List<PromptMessage> promptMessages = new ArrayList<>();
    for (ChatMessage chatMessage : chatMessages) {
      PromptMessage promptMessage =
          PromptMessage.newBuilder()
              .withRole(InternalOpenAiHelper.toOpenAiMessage(chatMessage).role().toString())
              .withContent(chatMessage.text())
              .build();
      promptMessages.add(promptMessage);
    }
    return promptMessages;
  }

  public List<ChatMessage> generatePromptMessages(
      String promptTemplateNo,
      String knowledgeBase,
      List<ChatMessage> chatBufferWindowList,
      String chatHistorySummary,
      String question,
      ChatMemory chatMemory,
      VectorStoreType vectorStoreType) {
    List<ChatMessage> chatMessages = new ArrayList<>();

    if (promptTemplateNo == null) {
      throw new BusinessException("100000", "提示词模板ID为空！！！");
    }
    PromptTemplateParam promptTemplateParam = promptTemplateMapper.selectById(promptTemplateNo);
    if (promptTemplateParam == null) {
      throw new BusinessException("100000", "未找到提示词模板！！！");
    }

    // 1.system message: 角色绑定+限定词
    SystemMessage systemMessage = null;
    if (StringUtils.isNotEmpty(promptTemplateParam.getSystemRole())
        && StringUtils.isNotEmpty(promptTemplateParam.getSystemPromptTemplate())) {
      systemMessage =
          generateSystemMessage(
              "systemRole",
              promptTemplateParam.getSystemPromptTemplate(),
              promptTemplateParam.getSystemRole());
      if (StringUtils.isNotEmpty(promptTemplateParam.getDeterminer())) {
        systemMessage =
            new SystemMessage(systemMessage.text() + promptTemplateParam.getDeterminer());
      }
    } else if (StringUtils.isNotEmpty(promptTemplateParam.getDeterminer())) {
      systemMessage = new SystemMessage(promptTemplateParam.getDeterminer());
    }
    if (StringUtils.isNotEmpty(promptTemplateParam.getChatBufferWindow())
        && chatBufferWindowList != null) {
      // 增加历史聊天记录的提示词
      if (systemMessage != null) {
        systemMessage =
            new SystemMessage(systemMessage.text() + promptTemplateParam.getChatBufferWindow());
      } else {
        systemMessage = new SystemMessage(promptTemplateParam.getChatBufferWindow());
      }
    }
    if (systemMessage != null) {
      chatMessages.add(systemMessage);
    }

    // 2.system message: knowledgeBase
    if (StringUtils.isNotEmpty(promptTemplateParam.getKnowledgeBase())
        && StringUtils.isNotEmpty(knowledgeBase)) {
      systemMessage =
          generateSystemMessage(
              "knowledgeBase", promptTemplateParam.getKnowledgeBase(), knowledgeBase);
      chatMessages.add(systemMessage);
    }

    // 3.system message: chatHistorySummary 根据实际情况，是否只有一条system??????
    if (StringUtils.isNotEmpty(promptTemplateParam.getChatHistorySummary())
        && StringUtils.isNotEmpty(chatHistorySummary)) {
      systemMessage =
          generateSystemMessage(
              "chatHistorySummary",
              promptTemplateParam.getChatHistorySummary(),
              chatHistorySummary);
      chatMessages.add(systemMessage);
    }

    // 4.system|assistant|user message: chatBufferWindow
    if (chatBufferWindowList != null) {
      for (ChatMessage buffer : chatBufferWindowList) {
        chatMessages.add(buffer);
      }
    }

    // 5.user message: question
    UserMessage userMessage = null;
    if (StringUtils.isNotEmpty(promptTemplateParam.getQuestion())
        && StringUtils.isNotEmpty(question)) {
      userMessage = generateUserMessage("question", promptTemplateParam.getQuestion(), question);
      chatMessages.add(userMessage);

    } else if (StringUtils.isNotEmpty(question)) {
      userMessage = new UserMessage(question);
      chatMessages.add(userMessage);
    }
    if (userMessage != null && chatMemory != null) {
      chatMemory.add(userMessage);
    }
    return chatMessages;
  }
}
