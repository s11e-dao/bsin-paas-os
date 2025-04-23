package me.flyray.bsin.server;

import me.flyray.bsin.facade.mcp.OpenMeteoService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ：bolei
 * @date ：Created in 2021/11/30 16:00
 * @description：bsin 脚手架启动类
 * @modified By：
 */

@SpringBootApplication
@EnableScheduling //开启定时任务
//@EnableOpenApi //启用swagger3
@MapperScan("me.flyray.bsin.infrastructure.mapper")
@ComponentScan("me.flyray.bsin.*")
public class BsinCrmApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(BsinCrmApplication.class);
        springApplication.run(args);
    }

    @Bean
    public ToolCallbackProvider weatherTools(OpenMeteoService openMeteoService) {
        return MethodToolCallbackProvider.builder().toolObjects(openMeteoService).build();
    }

}
