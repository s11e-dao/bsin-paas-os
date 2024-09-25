package me.flyray.bsin.server;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchConfigurationScript;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import org.elasticsearch.client.RequestOptions;

import java.io.IOException;

public class ElasticsearchEmbeddingStoreExample {


    /**
     * 初始化客户端
     *
     * @return
     */
    private static RestClient initClient(){
        String hostname = "192.168.198.197";
        int port = 9200;
        String username = "your username";
        String password = "your password";
        // 基本的用户名密码认证
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
//        basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(hostname, port, "http"));
        restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder ->
                httpAsyncClientBuilder.setDefaultCredentialsProvider(basicCredentialsProvider));

//        // 应用响应消费者工厂到RestClientBuilder
//        restClientBuilder.setHttpAsyncResponseConsumerFactory(responseConsumerFactory);

        RestClient restClient = restClientBuilder.build();
        return restClient;
    }


    public static void main(String[] args) throws IOException {

        RestClient client = initClient();

        // 创建一个 EmbeddingStore（嵌入存储）对象，用于存储和检索文本嵌入
        EmbeddingStore<TextSegment> embeddingStore = ElasticsearchEmbeddingStore.builder()
                // 使用上面创建的 RestClient 进行初始化
                .restClient(client)
                // 配置 Elasticsearch 的参数（可以扩展配置项）
                .configuration(ElasticsearchConfigurationScript.builder().build())
                .build();

        // 创建一个文本嵌入模型，用于生成文本的嵌入表示
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        // 创建第一个文本段，表示一个短语
        TextSegment segment1 = TextSegment.from("I like football.");
        // 对该文本段生成嵌入表示
        Embedding embedding1 = embeddingModel.embed(segment1).content();
        // 将生成的嵌入和文本段添加到嵌入存储中
        embeddingStore.add(embedding1, segment1);

        // 创建第二个文本段
        TextSegment segment2 = TextSegment.from("The weather is good today.");
        // 对该文本段生成嵌入表示
        Embedding embedding2 = embeddingModel.embed(segment2).content();
        // 将第二个嵌入及其文本段添加到嵌入存储中
        embeddingStore.add(embedding2, segment2);

        // 强制刷新 Elasticsearch 索引，使新添加的数据可见
        client.performRequest(new Request("POST", "/default/_refresh"));

        // 创建一个查询嵌入，用于进行相似度搜索
        Embedding queryEmbedding = embeddingModel.embed("What is your favourite sport?").content();
        // 在嵌入存储中搜索最相关的文本段
        EmbeddingSearchResult<TextSegment> relevant = embeddingStore.search(
                EmbeddingSearchRequest.builder()
                        // 传入查询嵌入来寻找相似的嵌入
                        .queryEmbedding(queryEmbedding)
                        .build());

        // 获取搜索结果中的第一个匹配项
        EmbeddingMatch<TextSegment> embeddingMatch = relevant.matches().get(0);

        // 打印匹配结果的得分，表示相似度
        System.out.println(embeddingMatch.score()); // 0.8138435

        // 打印匹配的文本段内容
        System.out.println(embeddingMatch.embedded().text()); // I like football.

        // 关闭 Elasticsearch 客户端
        client.close();
    }
}