package me.flyray.bsin.server.biz;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.BgeSmallZhEmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.EmbeddingModel;
import me.flyray.bsin.domain.enums.EmbeddingModeType;
import me.flyray.bsin.domain.enums.VectorStoreType;
import me.flyray.bsin.server.memory.chat.SummaryEmbeddingVectorStoreMemory;

import java.util.List;

/**
 * @author leonard
 * @description
 * @createDate 2023/12/2023/12/18 /17/34
 */
@Slf4j
public class ChatHistorySummaryAndStoreBiz implements Runnable {

  //  PromptEngineering promptEngineering;
  //  String chatHistorySummary;
  ChatLanguageModel chatModel;
  List<ChatMessage> messages;
  private EmbeddingModel embeddingModel;
  private SummaryEmbeddingVectorStoreMemory chatMemory;
  private VectorRetrievalBiz vectorRetrievalBiz;

  private String customerNo;
  private String aiNo;
  private String collectionName;
  private String partitionName;

  public ChatHistorySummaryAndStoreBiz(
      //      PromptEngineering promptEngineering,
      //      String chatHistorySummary,
      ChatLanguageModel chatModel,
      List<ChatMessage> messages,
      EmbeddingModel embeddingModel,
      SummaryEmbeddingVectorStoreMemory chatMemory,
      VectorRetrievalBiz vectorRetrievalBiz,
      String customerNo,
      String aiNo,
      String collectionName,
      String partitionName) {
    //    this.promptEngineering = promptEngineering;
    //    this.chatHistorySummary = chatHistorySummary;
    this.chatModel = chatModel;
    this.messages = messages;
    this.embeddingModel = embeddingModel;
    this.chatMemory = chatMemory;
    this.vectorRetrievalBiz = vectorRetrievalBiz;
    this.customerNo = customerNo;
    this.aiNo = aiNo;
    this.collectionName = collectionName;
    this.partitionName = partitionName;
  }

  @Override
  public void run() {
    log.info("\n\n\n我在异步执行对话总结任务： 请求的prompt：" + messages.get(0).toString());
    // 1.请求总结接口
    AiMessage aiMessage = chatModel.generate(messages).content();
    String chatSummary = aiMessage.text();
    log.info("\n\n\n总结结果：" + chatSummary);

    // 2.向量化
    dev.langchain4j.model.embedding.EmbeddingModel langchin4jEmbeddingModel =
        new AllMiniLmL6V2EmbeddingModel();
    if (EmbeddingModeType.All_MINI_LM_L6_V2.getDesc().equals(embeddingModel.getName())) {
      //
    } else if (EmbeddingModeType.BGE_SMALL_ZH.getDesc().equals(embeddingModel.getName())) {
      langchin4jEmbeddingModel = new BgeSmallZhEmbeddingModel();
    } else {
    }
    TextSegment chatSummaryTextSegment = new TextSegment(chatSummary, new Metadata());
    List<TextSegment> chatSummaryTextSegments = List.of(chatSummaryTextSegment);
    List<Embedding> chatSummaryEmbeddings =
        langchin4jEmbeddingModel.embedAll(chatSummaryTextSegments).content();
    log.info("\n\n\n向量化完毕");
    // 3.存储
    vectorRetrievalBiz.addDataToVector(
        chatSummaryEmbeddings,
        chatSummaryTextSegments,
        customerNo,
        VectorStoreType.CHAT_HISTORY_SUMMARY.getDesc(),
        aiNo,
        "null",
        collectionName,
        partitionName,
        embeddingModel.getDimension());

    log.info("\n\n\n向量化存储完毕");
    // clear
    chatMemory.cacheClear();
    log.info("\n\n\n待总结对话清楚完毕");
  }
}
