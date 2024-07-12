package me.flyray.bsin.server;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import me.flyray.bsin.server.milvus.BsinMilvusEmbeddingStore;
import me.flyray.bsin.server.milvus.BsinTextSegment;
import org.junit.Before;
import org.junit.Test;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisProperties;

import java.util.List;

public class MilvusTest {

  @Before
  public void before() {}

  @Test
  public void testBsinEmbedding() {
    EmbeddingStore<BsinTextSegment> embeddingStore =
        BsinMilvusEmbeddingStore.builder().host("localhost").port(19530).dimension(384).build();

    EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();


//    BsinTextSegment segment1 = (BsinTextSegment) BsinTextSegment.from("I like football.");

    /**
     * String text,
     *       String type,
     *       String customerNo,
     *       String aiNo,
     *       String knowledgeBaseFileNo,
     *       String chunkNo,
     *       List<Float> vector) {
     */
    BsinTextSegment segment1 = BsinTextSegment.fromBsin("I like football.", "1", "customerNo", "aiNo", "knowledgeBaseFileNo", "chunkNo", null);

    System.out.println("TextSegment: \n\n" + segment1);
    System.out.println("TextSegment-toString: \n\n" + segment1.toString());
    System.out.println("TextSegment-metadata: \n\n" + segment1.metadata());
    System.out.println("TextSegment-text: \n\n" + segment1.text());
    Embedding embedding1 = embeddingModel.embed(segment1).content();

    // embeddingStore.add(embedding1, segment1);

    List<EmbeddingMatch<BsinTextSegment>> matches = embeddingStore.findRelevant(embedding1,1);
    EmbeddingMatch<BsinTextSegment> embeddingMatch = matches.get(0);
    System.out.println(embeddingMatch.score()); // 0.8144287765026093
    System.out.println(embeddingMatch.embedded().text()); // I like football.

    //    TextSegment segment2 = TextSegment.from("The weather is good today.");
    //    Embedding embedding2 = embeddingModel.embed(segment2).content();
    //    embeddingStore.add(embedding2, segment2);
    //
    //    Embedding queryEmbedding = embeddingModel.embed("What is your favourite
    // sport?").content();
    //    List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding,
    // 1);
    //    EmbeddingMatch<TextSegment> embeddingMatch = relevant.get(0);
    //
    //    System.out.println(embeddingMatch.score()); // 0.8144287765026093
    //    System.out.println(embeddingMatch.embedded().text()); // I like football.
  }

  @Test
  public void testEmbedding() {

    EmbeddingStore<TextSegment> embeddingStore =
        MilvusEmbeddingStore.builder().host("localhost").port(19530).dimension(384).build();

    EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

    TextSegment segment1 = TextSegment.from("I like football.");
    Embedding embedding1 = embeddingModel.embed(segment1).content();
    embeddingStore.add(embedding1, segment1);

    TextSegment segment2 = TextSegment.from("The weather is good today.");
    Embedding embedding2 = embeddingModel.embed(segment2).content();
    embeddingStore.add(embedding2, segment2);

    Embedding queryEmbedding = embeddingModel.embed("What is your favourite sport?").content();
    List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1);
    EmbeddingMatch<TextSegment> embeddingMatch = relevant.get(0);

    System.out.println(embeddingMatch.score()); // 0.8144287765026093
    System.out.println(embeddingMatch.embedded().text()); // I like football.
  }

}
