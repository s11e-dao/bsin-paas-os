package me.flyray.bsin.server;

import me.flyray.bsin.server.biz.WxPlatformMsgHandlerBiz;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import dev.langchain4j.model.embedding.BgeSmallZhEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import me.flyray.bsin.server.websocket.WebSocketServer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ：leonard
 * @date ：Created in 2023/04/21 16:00
 * @description：bsin ai 引擎
 * @modified By：
 */

// @ImportResource({"classpath*:sofa/rpc-provider-ai.xml"})
@SpringBootApplication
@EnableScheduling // 开启定时任务
@MapperScan("me.flyray.bsin.infrastructure.mapper")
@ComponentScan("me.flyray.bsin.*")
public class BsinAiApplication {

  @Bean
  EmbeddingModel embeddingModel() {
    // 此处可以选择不同的EmbeddingModel：OpenAiEmbeddingModel AllMiniLmL6V2EmbeddingModel
    return new BgeSmallZhEmbeddingModel();
  }

  public static void main(String[] args) {
    ConfigurableApplicationContext applicationContext =
        SpringApplication.run(BsinAiApplication.class, args);
    WebSocketServer.setApplicationContext(applicationContext);
    WxPlatformMsgHandlerBiz.setApplicationContext(applicationContext);
  }
}
