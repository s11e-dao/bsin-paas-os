package me.flyray.bsin.server.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;

/**
 * @author leonard
 * @description
 * @createDate 2023/12/2023/12/18 /16/50
 */
public class BsinThreadPool {
  private static final Logger LOGGER = LoggerFactory.getLogger(BsinThreadPool.class);
  private static ThreadPoolExecutor threadPool = null;
  private static final String POOL_NAME = "bsinThreadPool";
  // 等待队列长度:任务队列，被提交到那时尚未被执行的任务
  private static final int BLOCKING_QUEUE_LENGTH = 20000;
  // 闲置线程存活时间:当线程池中线程数量超过corePoolSize时，多余线程存活的时间（非核心线程存活时间），即非核心线程在多长时间后被销毁
  private static final int KEEP_ALIVE_TIME = 60 * 1000;

  private BsinThreadPool() {
    throw new IllegalStateException("utility class");
  }

  /**
   * 无返回值直接执行
   *
   * @param runnable 需要运行的任务
   */
  public static void execute(Runnable runnable) {
    getThreadPool().execute(runnable);
  }

  /**
   * 有返回值执行 主线程中使用Future.get()获取返回值时，会阻塞主线程，直到任务执行完毕
   *
   * @param callable 需要运行的任务
   */
  public static <T> Future<T> submit(Callable<T> callable) {
    return getThreadPool().submit(callable);
  }

  public static synchronized ThreadPoolExecutor getThreadPool() {
    if (threadPool == null) {
      // 获取处理器数量
      int cpuNum = Runtime.getRuntime().availableProcessors();
      // maximumPoolSize(线程池中核心线程数): 根据cpu数量,计算出合理的线程并发数
      int maximumPoolSize = cpuNum * 2 + 1;
      // 核心线程数、最大线程数、闲置线程存活时间、时间单位、线程队列、线程工厂、当前线程数已经超过最大线程数时的异常处理策略
      threadPool =
          new ThreadPoolExecutor(
              maximumPoolSize - 1,
              maximumPoolSize,
              KEEP_ALIVE_TIME,
              TimeUnit.MILLISECONDS, // keepAliveTime的时间单位
              new ArrayBlockingQueue<>(BLOCKING_QUEUE_LENGTH),
              new ThreadFactoryBuilder().setNameFormat(POOL_NAME + "-%d").build(),
              new ThreadPoolExecutor.AbortPolicy() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                  LOGGER.warn(
                      "线程爆炸了，当前运行线程总数：{}，活动线程数：{}。等待队列已满，等待运行任务数：{}",
                      e.getPoolSize(),
                      e.getActiveCount(),
                      e.getQueue().size());
                }
              });
    }
    return threadPool;
  }
}
