package me.flyray.bsin.server;

import org.dromara.x.file.storage.spring.EnableFileStorage;
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
@MapperScan("me.flyray.bsin.infrastructure.mapper")
@ComponentScan("me.flyray.bsin.*")
@EnableFileStorage // 文件上传工具
public class BsinWaasApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(BsinWaasApplication.class);
        springApplication.run(args);
    }

    /**
     * 初始化监听数据
     */

    /**
     * 开启链上scan
     */

}
