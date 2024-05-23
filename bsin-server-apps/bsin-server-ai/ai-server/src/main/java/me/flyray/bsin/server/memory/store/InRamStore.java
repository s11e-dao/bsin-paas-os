package me.flyray.bsin.server.memory.store;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * @author leonard
 * @description
 * @createDate 2023/12/2023/12/14 /21/36
 */
@Component
public class InRamStore implements ChatMemoryStore {
  private final Map<Object, List<ChatMessage>> messagesByMemoryId = new ConcurrentHashMap<>();

  @Override
  public List<ChatMessage> getMessages(Object memoryId) {
    return messagesByMemoryId.computeIfAbsent(memoryId, ignored -> new ArrayList<>());
  }

  @Override
  public void updateMessages(Object memoryId, List<ChatMessage> messages) {
    messagesByMemoryId.put(memoryId, messages);
  }

  @Override
  public void deleteMessages(Object memoryId) {
    messagesByMemoryId.remove(memoryId);
  }
}
