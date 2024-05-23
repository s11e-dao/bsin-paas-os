package me.flyray.bsin.server.memory.chat;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static dev.langchain4j.internal.ValidationUtils.ensureGreaterThanZero;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

/**
 * @author leonard
 * @description:
 *     基于一个固定长度的滑动窗口的“记忆”功能,只保留最近的交互。它有意地舍弃了最旧的部分，为新的交互腾出空间。这有助于管理内存负担并减少使用的标记数量。重要的是，它仍然保留对话最新的部分，不做任何修改。因此，它为聊天机器人保留了最近的信息，确保更高效和实时的对话体验
 *     优势：有限的历史上下文 + 减少标记数量 + 保持未修改的上下文 + 实时对话 缺点： 影响长期上下文和准确性 + 理解深度减少 + 潜在的上下文相关性丧失
 * @createDate 2023/12/2023/12/14 /19/59
 */
public class BufferWindowInRamStoreMemory implements ChatMemory {

  private static final Logger log = LoggerFactory.getLogger(BufferWindowInRamStoreMemory.class);

  private final Object id;
  private final Integer maxMessages;
  private final ChatMemoryStore store;

  private BufferWindowInRamStoreMemory(BufferWindowInRamStoreMemory.Builder builder) {
    this.id = ensureNotNull(builder.id, "id");
    this.maxMessages = ensureGreaterThanZero(builder.maxMessages, "maxMessages");
    this.store = ensureNotNull(builder.store, "store");
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
    ensureCapacity(messages, maxMessages);
    store.updateMessages(id, messages);
  }

  public String toString() {
    List<ChatMessage> messages = messages();
    StringBuilder result = new StringBuilder();
    for (ChatMessage message : messages) {
      result.append(message.type().toString()).append(":").append(message.text());
    }
    if (messages.size() == 0) {
      return "null";
    }
    return result.toString();
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

  @Override
  public void clear() {
    store.deleteMessages(id);
  }

  public static BufferWindowInRamStoreMemory.Builder builder() {
    return new BufferWindowInRamStoreMemory.Builder();
  }

  public static class Builder {

    private Object id = "default";
    private Integer maxMessages;
    private ChatMemoryStore store = new InMemoryChatMemoryStore();

    /**
     * @param id The ID of the {@link ChatMemory}. If not provided, a "default" will be used.
     * @return builder
     */
    public BufferWindowInRamStoreMemory.Builder id(Object id) {
      this.id = id;
      return this;
    }

    /**
     * @param maxMessages The maximum number of messages to retain.
     * @return builder
     */
    public BufferWindowInRamStoreMemory.Builder maxMessages(Integer maxMessages) {
      this.maxMessages = maxMessages;
      return this;
    }

    /**
     * @param store The chat memory store responsible for storing the chat memory state. If not
     *     provided, an {@link InMemoryChatMemoryStore} will be used.
     * @return builder
     */
    public BufferWindowInRamStoreMemory.Builder chatMemoryStore(ChatMemoryStore store) {
      this.store = store;
      return this;
    }

    public BufferWindowInRamStoreMemory build() {
      return new BufferWindowInRamStoreMemory(this);
    }
  }

  public static BufferWindowInRamStoreMemory withMaxMessages(int maxMessages) {
    return builder().maxMessages(maxMessages).build();
  }
}
