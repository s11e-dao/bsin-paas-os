package me.flyray.bsin.server;

import org.junit.runner.RunWith;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author bolei
 * @date 2023/7/28 9:21
 * @desc
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

  @org.junit.Test
  public void parseAiModelJson() throws IOException {
//    String filePath = this.getClass().getResource("/") + "s11eCopilot.json";
    String filePath =
        "/home/rednet/x-workspace/bsin-paas-os/bsin-server-apps/bsin-server-brms/brms-server/target/test-classes/s11eCopilot.json";
    // 读取json文件
    log.info("------------------------------");
    log.info("------------------------------");
  }


}
