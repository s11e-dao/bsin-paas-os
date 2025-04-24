package me.flyray.bsin.server.config;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component // 把此类托管给 Spring，不能省略
public class TaskUtils {

    // 添加定时任务
//    @Scheduled(cron = "0/5 * * * * *") // cron表达式：每五秒执行一次
    public void doTask(){
        System.out.println("我是定时任务~");
    }

}
