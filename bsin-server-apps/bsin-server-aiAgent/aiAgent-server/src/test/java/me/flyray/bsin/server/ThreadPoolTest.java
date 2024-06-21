package me.flyray.bsin.server;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.flyray.bsin.server.utils.BsinThreadPool;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author leonard
 * @description
 * @createDate 2023/12/2023/12/18 /16/48
 */
@Slf4j
public class ThreadPoolTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolTest.class);
  ExecutorService executorService = Executors.newFixedThreadPool(10);

  @Test
  public void testRetrun() throws Exception {
    Future<String> future =
        BsinThreadPool.submit(
            () -> {
              return "我有返回值哦";
            });
    try {
      System.out.println(future.get());
    } catch (InterruptedException | ExecutionException e) {
      System.out.println("任务超过指定时间未返回值，线程超时退出");
    }
  }

  @Test
  public void testSync() throws Exception {
    int loop = 40;
    for (int i = 0; i < loop; i++) {
      System.out.println("任务:" + i);
      BsinThreadPool.execute(
          () -> {
            System.out.println("干活好累");
            log.info("干活好累");
            try {
              Thread.sleep(10);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            System.out.println("终于干完了");
            log.info("终于干完了");
          });
    }
    System.out.println("我在这儿等着你回来等你回来");
  }

  // 测试10个线程使用工具类
  @Test
  public void testAsync() throws Exception {
    for (int i = 0; i < 10; i++) {
      executorService.submit(
          new Runnable() {
            @Override
            public void run() {
              final String name = Thread.currentThread().getName();
              BsinThreadPool.execute(
                  () -> {
                    //                    System.out.println(name + "： 干活好累");
                    LOGGER.info(name + "干活好累");
                    try {
                      Thread.sleep(10);
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }
                    //                    System.out.println(name + ": 终于干完了");
                    LOGGER.warn(name + "终于干完了");
                  });
            }
          });
      System.out.println("create new thread:" + i);
    }
    System.out.println("不用等他，我们先干");
    while (Thread.currentThread().isAlive()) {
      System.out.println("wait for thread");
      Thread.sleep(1000);
    }
  }
}
