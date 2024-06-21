package me.flyray.bsin.server.memory.chat;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import me.flyray.bsin.server.memory.store.InRamStore;
import me.flyray.bsin.server.memory.store.InVectorDataBaseEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static dev.langchain4j.internal.ValidationUtils.ensureGreaterThanZero;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

/**
 * @author bolei
 * @date 2023/11/2
 * @desc: 根据触发条件（聊天记录token数，记录数）来实现聊天信息总结概述，并储存在向量数据库
 *     将我们过去对话片段的摘要作为我们的历史记录。但是它是如何进行摘要的呢？LLM挺身而出！LLM（语言模型）帮助压缩或摘要对话，捕捉关键信息。
 *     因此，我们存储的不再是整个对话，而是其摘要版本。这有助于管理标记数量，并允许LLM有效地处理对话。总之，ConversationSummaryMemory使用LLM的摘要功能保留了以前对话的压缩版本。
 *     优势： 高效的内存管理 + 改进的处理(通过压缩对话片段，使语言模型更容易处理和生成回应) + 避免超过限制 + 保留重要信息 缺点： 潜在的细节丢失 + 有限的历史上下文 + 降低精度
 *     <p>Once added, a {@link SystemMessage} is always retained. Only one {@link SystemMessage} can
 *     be held at a time. If a new {@link SystemMessage} with the same content is added, it is
 *     ignored. If a new {@link SystemMessage} with different content is added, it replaces the
 *     previous one.
 *     <p>The state of chat memory is stored in {@link ChatMemoryStore}.
 */
public class SummaryRamStoreMemory implements ChatMemory {
  String SUMMARIZER_TEMPLATE =
      "请将以下内容逐步概括所提供的对话内容，并将新的概括添加到之前的概括中，形成新的概括。\n\n EXAMPLE\nCurrent summary:\nHuman询问AI对人工智能的看法。AI认为人工智能是一种积极的力量。\nNew lines of conversation:\nHuman：为什么你认为人工智能是一种积极的力量？\nAI：因为人工智能将帮助人类发挥他们的潜能。\n\nNew summary:\nHuman询问AI对人工智能的看法。AI认为人工智能是一种积极的力量，因为它将帮助人类发挥他们的潜能。\nEND OF EXAMPLE\nCurrent summary:\n{summary}\nNew lines of conversation:\n{new_lines}";

  private static final Logger log = LoggerFactory.getLogger(SummaryRamStoreMemory.class);

  private final Object id;
  private final Integer maxMessages;
  private final Integer triggerSummaryMessages;
  private final Integer triggerSummaryTokens;
  private final ChatMemoryStore store;

  private SummaryRamStoreMemory(Builder builder) {
    this.id = ensureNotNull(builder.id, "id");
    this.maxMessages = ensureGreaterThanZero(builder.maxMessages, "maxMessages");
    this.store = ensureNotNull(builder.store, "store");
    this.triggerSummaryMessages =
        ensureGreaterThanZero(builder.triggerSummaryMessages, "triggerSummaryMessages");
    this.triggerSummaryTokens =
        ensureGreaterThanZero(builder.triggerSummaryTokens, "triggerSummaryTokens");
  }

  @Override
  public Object id() {
    return id;
  }

  @Override
  public void add(ChatMessage message) {
    List<ChatMessage> messages = messages();
    if (message instanceof SystemMessage) {
      Optional<SystemMessage> systemMessage = findSystemMessage(messages);
      if (systemMessage.isPresent()) {
        if (systemMessage.get().equals(message)) {
          return; // do not add the same system message
        } else {
          messages.remove(systemMessage.get()); // need to replace existing system message
        }
      }
    }
    messages.add(message);
    summaryConversation(messages, triggerSummaryMessages, triggerSummaryTokens);
    store.updateMessages(id, messages);
  }

  private static Optional<SystemMessage> findSystemMessage(List<ChatMessage> messages) {
    return messages.stream()
        .filter(message -> message instanceof SystemMessage)
        .map(message -> (SystemMessage) message)
        .findAny();
  }

  @Override
  public List<ChatMessage> messages() {
    List<ChatMessage> messages = new LinkedList<>(store.getMessages(id));
    ensureCapacity(messages, maxMessages);
    return messages;
  }

  private static void ensureCapacity(List<ChatMessage> messages, int maxMessages) {
    while (messages.size() > maxMessages) {
      int messageToRemove = 0;
      if (messages.get(0) instanceof SystemMessage) {
        messageToRemove = 1;
      }
      ChatMessage removedMessage = messages.remove(messageToRemove);
      log.trace(
          "Removing the following message to comply with the capacity requirements: {}",
          removedMessage);
    }
  }

  private static void summaryConversation(
      List<ChatMessage> messages, int triggerSummaryMessages, int triggerSummaryTokens) {
    while (messages.size() > triggerSummaryMessages) {
      // TODO Summary Prompt
      log.trace("Summary the following messages and store into vector database: {}", messages);
    }
  }

  @Override
  public void clear() {
    store.deleteMessages(id);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Object id = "default";
    private Integer maxMessages;
    private Integer triggerSummaryMessages;
    private Integer triggerSummaryTokens;

    private ChatMemoryStore store = new InRamStore();

    /**
     * @param id The ID of the {@link ChatMemory}. If not provided, a "default" will be used.
     * @return builder
     */
    public Builder id(Object id) {
      this.id = id;
      return this;
    }

    /**
     * @param maxMessages The maximum number of messages to retain.
     * @return builder
     */
    public Builder maxMessages(Integer maxMessages) {
      this.maxMessages = maxMessages;
      return this;
    }

    /**
     * @param triggerSummaryMessages The maximum number of messages to trigger Summary action.
     * @return builder
     */
    public Builder triggerSummaryMessages(Integer triggerSummaryMessages) {
      this.triggerSummaryMessages = triggerSummaryMessages;
      return this;
    }

    /**
     * @param triggerSummaryTokens The maximum tokens of messages to trigger Summary action.
     * @return builder
     */
    public Builder triggerSummaryTokens(Integer triggerSummaryTokens) {
      this.triggerSummaryTokens = triggerSummaryTokens;
      return this;
    }

    /**
     * @param store The chat memory store responsible for storing the chat memory state. If not
     *     provided, an {@link InMemoryChatMemoryStore} will be used.
     * @return builder
     */
    public Builder chatMemoryStore(ChatMemoryStore store) {
      this.store = store;
      return this;
    }

    public SummaryRamStoreMemory build() {
      return new SummaryRamStoreMemory(this);
    }
  }

  public static SummaryRamStoreMemory withMaxMessages(int maxMessages) {
    return builder().maxMessages(maxMessages).build();
  }

  public static SummaryRamStoreMemory withTriggerSummaryMessages(int triggerSummaryMessages) {
    return builder().triggerSummaryMessages(triggerSummaryMessages).build();
  }

  public static SummaryRamStoreMemory withTriggerSummaryTokens(int triggerSummaryTokens) {
    return builder().triggerSummaryTokens(triggerSummaryTokens).build();
  }
}
