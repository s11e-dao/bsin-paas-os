package me.flyray.bsin.server;

import com.alibaba.fastjson.JSONObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.server.engine.AipmnModelParseService;
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
public class s11eCopilotTest {

  @Autowired
  private AipmnModelParseService aipmnModelParseService;

  @Test
  public void parseAiModelJson() throws IOException {
//    String filePath = this.getClass().getResource("/") + "s11eCopilot.json";
    String filePath =
        "/home/rednet/x-workspace/bsin-paas-os/bsin-server-apps/bsin-server-aiAgent/aiAgent-server/target/test-classes/s11eCopilot.json";
    // 读取json文件
    JSONObject jsonObejct = readJsonFile(filePath);
    log.info("------------------------------");
    log.info(jsonObejct.toJSONString());
    log.info("------------------------------");
    aipmnModelParseService.parse(jsonObejct);
  }

  public static JSONObject readJsonFile(String filename) throws IOException {
    String jsonString = "";
    File jsonFile = new File(filename);
    jsonFile.canExecute();
    log.info(String.valueOf(jsonFile.canExecute()));
    FileReader fileReader = new FileReader(jsonFile);
    Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
    int ch = 0;
    StringBuffer stringBuffer = new StringBuffer();
    while ((ch = reader.read()) != -1) {
      stringBuffer.append((char) ch);
    }
    fileReader.close();
    reader.close();
    jsonString = stringBuffer.toString();
    log.info("---------------------------------------");
    log.info(jsonString);
    System.out.println(jsonString);

    return JSONObject.parseObject(jsonString);
  }
}
