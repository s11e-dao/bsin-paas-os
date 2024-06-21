package me.flyray.bsin.server.biz;

import static java.net.Proxy.Type.HTTP;
import static java.util.stream.Collectors.joining;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.server.memory.store.InVectorDataBaseEmbeddingStore;

import me.flyray.bsin.server.milvus.BsinMilvusEmbeddingStore;
import me.flyray.bsin.server.milvus.BsinTextSegment;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

/**
 * @author ：leonard
 * @date ：Created in 2023/10/20 16:25
 * @description：
 * @modified By：
 */
@Slf4j
@Component
public class LangCahinBiz {

  interface Assistant {
    // String chat(@MemoryId int memoryId, @UserMessage String userMessage);
    @SystemMessage("你是一个专业的web3.0品牌运营官")
    TokenStream chat(@MemoryId String memoryId, @UserMessage String userMessage);
  }

  @Value("${langchain4j.chat-model.openai.api-key}")
  private String OPENAI_API_KEY;

  @Value("${milvus.datasource.host}")
  private String vectorHost;

  @Value("${milvus.datasource.port}")
  private Integer vectorPort;

  @Autowired private EmbeddingModel embeddingModel;

  @Autowired private InVectorDataBaseEmbeddingStore store;

  public String chat(String question) {
    OpenAiChatModel model =
        OpenAiChatModel.builder()
            .apiKey(OPENAI_API_KEY)
            // .baseUrl("")
            .proxy(new Proxy(HTTP, new InetSocketAddress("127.0.0.1", 8889)))
            .build();

    String answer = model.generate(question);
    return answer;
  }

  public void chatWithDocument(Session session, String question) {
    // Specify the question you want to ask the model
    Embedding questionEmbedding = embeddingModel.embed(question).content();
    // 根据知识库ID查询知识库
    // 构建对应的知识库
    EmbeddingStore<BsinTextSegment> bsinMilvusEmbeddingStore =
        BsinMilvusEmbeddingStore.builder()
            .host(vectorHost)
            .port(vectorPort)
            .collectionName("tes")
            .dimension(512) // 384
            .build();
    // 1. Create an in-memory embedding store
    int maxResults = 3;
    double minScore = 0.9;
    // 向量匹配
    List<EmbeddingMatch<BsinTextSegment>> relevantEmbeddings =
        bsinMilvusEmbeddingStore.findRelevant(questionEmbedding, maxResults, minScore);

    // TODO PromptTemplate 需要不断优化
    // Create a prompt for the model that includes question and relevant embeddings
    PromptTemplate promptTemplate =
        PromptTemplate.from(
            "Answer the following question to the best of your ability in simplified Chinese':\n"
                + "\n"
                + "Question:\n"
                + "{{question}}\n"
                + "\n"
                + "Base your answer on the following information:\n"
                + "{{information}}");

    String information =
        relevantEmbeddings.stream().map(match -> match.embedded().text()).collect(joining("\n\n"));

    Map<String, Object> variables = new HashMap<>();
    variables.put("question", question);
    // 如果向量数据库不存在，则从历史会话中获取数据
    variables.put("information", information);
    if (StringUtils.isBlank(information)) {
      // 获取历史对话，放入信息中
      variables.put("information", information);
    }

    Prompt prompt = promptTemplate.apply(variables);

    StreamingChatLanguageModel streamingModel =
        OpenAiStreamingChatModel.builder()
            .apiKey(OPENAI_API_KEY)
            // .baseUrl("")
            .proxy(new Proxy(HTTP, new InetSocketAddress("127.0.0.1", 8889)))
            .build();

    ChatMemoryProvider chatMemoryProvider =
        memoryId ->
            MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(100)
                .chatMemoryStore(store)
                .build();

    Assistant assistant =
        AiServices.builder(Assistant.class)
            .streamingChatLanguageModel(streamingModel)
            .chatMemoryProvider(chatMemoryProvider)
            .build();
    System.out.println(prompt.toUserMessage().text());
    TokenStream tokenStream = assistant.chat(session.getId(), prompt.toUserMessage().text());

    // 流式返回
    tokenStream
        .onNext(
            token -> {
              try {
                session.getBasicRemote().sendText(token);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .onComplete(
            response -> {
              try {
                session.getBasicRemote().sendText("done");
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              System.out.println(response);
            })
        .onError(Throwable::printStackTrace)
        .start();
  }

  public static void main(String[] args) {

    StreamingChatLanguageModel streamingModel =
        OpenAiStreamingChatModel.builder()
            .apiKey("demo")
            // .baseUrl("")
            .proxy(new Proxy(HTTP, new InetSocketAddress("127.0.0.1", 8889)))
            .build();

    InVectorDataBaseEmbeddingStore store = new InVectorDataBaseEmbeddingStore();

    ChatMemoryProvider chatMemoryProvider =
        memoryId ->
            MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10000)
                .chatMemoryStore(store)
                .build();

    Assistant assistant =
        AiServices.builder(Assistant.class)
            .streamingChatLanguageModel(streamingModel)
            .chatMemoryProvider(chatMemoryProvider)
            .build();

    PromptTemplate promptTemplate =
        PromptTemplate.from(
            "Answer the following question to the best of your ability in simplified Chinese':\n"
                + "\n"
                + "Question:\n"
                + "{{question}}\n"
                + "\n"
                + "Base your answer on the following information:\n"
                + "{{information}}");

    Map<String, Object> variables = new HashMap<>();
    variables.put("question", "What is my name?");
    // 如果向量数据库不存在，则从历史会话中获取数据
    variables.put("information", "我是红商助手");
    Prompt prompt = promptTemplate.apply(variables);

    System.out.println(assistant.chat("3", "Hello, my name is Klaus"));
    // System.out.println(assistant.chat("4", "Hi, my name is Francine"));

    // Now, comment out the two lines above, uncomment the two lines below, and run again.

    //        TokenStream tokenStream = assistant.chat("3",prompt.toUserMessage().text());
    //        tokenStream.onNext(token -> {
    //                            System.out.println(token);
    //                        }
    //                )
    //                .onComplete(System.out::println)
    //                .onError(Throwable::printStackTrace)
    //                .start();
    System.out.println(prompt.toUserMessage().text());
    // System.out.println(assistant.chat("1", "What is my name?"));
    TokenStream tokenStream = assistant.chat("3", prompt.toUserMessage().text());
    tokenStream
        .onNext(
            token -> {
              System.out.println(token);
            })
        .onComplete(System.out::println)
        .onError(Throwable::printStackTrace)
        .start();
  }
}
