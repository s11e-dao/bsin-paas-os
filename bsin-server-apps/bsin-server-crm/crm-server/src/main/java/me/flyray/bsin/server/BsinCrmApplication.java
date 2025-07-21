package me.flyray.bsin.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
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

}
