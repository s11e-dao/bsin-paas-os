package me.flyray.bsin.server.memory.store;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.spi.ServiceHelper;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.server.spi.store.embedding.inmemory.InVectorDataBaseEmbeddingStoreJsonCodecFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

/**
 * @author bolei
 * @date 2023/11/2
 * @desc: 是否考虑实现 EmbeddingStore<Embedded> 接口？？ public class InVectorDataBaseEmbeddingStore<Embedded>
 *     implements EmbeddingStore<Embedded> Main Contex: Ram ChatHistoryList External contex: Vector
 *     ChatSummaryList
 */
@Component
public class InVectorDataBaseEmbeddingStore<Embedded>
    implements ChatMemoryStore, EmbeddingStore<Embedded> {

  private InRamStore inRamStore = null;
  private InRamStore inRamCacheStore = null; // 缓存待总结的聊天记录

  /**
   * 调用前必须先设置
   *
   * @param inRamStore
   */
  public void setInRamStore(InRamStore inRamStore) {
    this.inRamStore = inRamStore;
  }

  public void setInRamCacheStore(InRamStore inRamStore) {
    this.inRamCacheStore = inRamStore;
  }

  @Override
  public List<ChatMessage> getMessages(Object memoryId) {
    return inRamStore.getMessages(memoryId);
    //    // 优先从 Main Contex 中获取
    //    if (chatMessages != null) {
    //      return chatMessages;
    //    }
    //    String json = null;
    //    try {
    //      json =
    //          vectorRetrievalBiz.getChatMessagesFromVector(
    //              (String) memoryId, customerCollection, partitionNameName);
    //    } catch (Exception e) {
    //      throw new RuntimeException(e);
    //    }
    //    return messagesFromJson(json);
  }

  public List<ChatMessage> getCacheMessages(Object memoryId) {
    return inRamCacheStore.getMessages(memoryId);
  }

  @Override
  public void updateMessages(Object memoryId, List<ChatMessage> messages) {
    inRamStore.updateMessages(memoryId, messages);
    //    String json = messagesToJson(messages);
    //    try {
    //      vectorRetrievalBiz.updateChatMessagesFromVector(
    //          (String) memoryId, json, customerCollection, partitionNameName);
    //    } catch (Exception e) {
    //      throw new RuntimeException(e);
    //    }
  }

  @Override
  public void deleteMessages(Object memoryId) {
    inRamStore.deleteMessages(memoryId);
  }

  public void deleteCacheMessages(Object memoryId) {
    inRamCacheStore.deleteMessages(memoryId);
  }

  /** EmbeddingStore interfaces */
  @Override
  @Deprecated
  public String add(Embedding embedding) {
    throw new BusinessException(
        "100000", "this interface is deprecated, please use addMessages interface..");
    //    String id = randomUUID();
    //    add(id, embedding);
    //    return id;
  }

  @Override
  @Deprecated
  public void add(String id, Embedding embedding) {
    throw new BusinessException(
        "100000", "this interface is deprecated, please use addMessages interface..");
    //    add(id, embedding, null);
  }

  @Override
  @Deprecated
  public String add(Embedding embedding, Embedded embedded) {
    throw new BusinessException(
        "100000", "this interface is deprecated, please use addMessages interface..");
    //    String id = randomUUID();
    //    add(id, embedding, embedded);
    //    return id;
  }

  @Override
  @Deprecated
  public List<String> addAll(List<Embedding> embeddings) {
    throw new BusinessException(
        "100000", "this interface is deprecated, please use addMessages interface..");
    //    List<String> ids = new ArrayList<>();
    //    for (Embedding embedding : embeddings) {
    //      ids.add(add(embedding));
    //    }
    //    return ids;
  }

  @Override
  @Deprecated
  public List<String> addAll(List<Embedding> embeddings, List<Embedded> embedded) {
    throw new BusinessException(
        "100000", "this interface is deprecated, please use addMessages interface..");
    //    if (embeddings.size() != embedded.size()) {
    //      throw new IllegalArgumentException(
    //          "The list of embeddings and embedded must have the same size");
    //    }
    //
    //    List<String> ids = new ArrayList<>();
    //    for (int i = 0; i < embeddings.size(); i++) {
    //      ids.add(add(embeddings.get(i), embedded.get(i)));
    //    }
    //    return ids;
  }

  @Override
  public List<EmbeddingMatch<Embedded>> findRelevant(
      Embedding referenceEmbedding, int maxResults, double minScore) {

    return null;
  }

  @Override
  @Deprecated
  public boolean equals(Object o) {
    //    if (this == o) return true;
    //    if (o == null || getClass() != o.getClass()) return false;
    //    InVectorDataBaseEmbeddingStore<?> that = (InVectorDataBaseEmbeddingStore<?>) o;
    //    return Objects.equals(this.entries, that.entries);
    return false;
  }

  @Override
  @Deprecated
  public int hashCode() {
    return Objects.hash(null);
  }

  public String serializeToJson() {
    return CODEC.toJson(this);
  }

  public void serializeToFile(Path filePath) {
    try {
      String json = serializeToJson();
      Files.write(filePath, json.getBytes(), CREATE, TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void serializeToFile(String filePath) {
    serializeToFile(Paths.get(filePath));
  }

  private static final InVectorDataBaseEmbeddingStoreJsonCodec CODEC = loadCodec();

  private static InVectorDataBaseEmbeddingStoreJsonCodec loadCodec() {
    Collection<InVectorDataBaseEmbeddingStoreJsonCodecFactory> factories =
        ServiceHelper.loadFactories(InVectorDataBaseEmbeddingStoreJsonCodecFactory.class);
    for (InVectorDataBaseEmbeddingStoreJsonCodecFactory factory : factories) {
      return factory.create();
    }
    // fallback to default
    return new GsonInVectorDataBaseEmbeddingStoreJsonCodec();
  }

  public static InVectorDataBaseEmbeddingStore<TextSegment> fromJson(String json) {
    return CODEC.fromJson(json);
  }

  public static InVectorDataBaseEmbeddingStore<TextSegment> fromFile(Path filePath) {
    try {
      String json = new String(Files.readAllBytes(filePath));
      return fromJson(json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static InVectorDataBaseEmbeddingStore<TextSegment> fromFile(String filePath) {
    return fromFile(Paths.get(filePath));
  }
}
