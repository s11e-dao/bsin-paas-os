package me.flyray.bsin.server;

import com.alibaba.fastjson.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

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
public class JsonToDroolsTest {

  @Test
  public void parseAiModelJson() throws IOException {
//    String filePath = this.getClass().getResource("/") + "s11eCopilot.json";
    String filePath =
        "/home/rednet/x-workspace/bsin-paas-os/bsin-server-apps/bsin-server-brms/brms-server/target/test-classes/s11eCopilot.json";
    // 读取json文件
    JSONObject jsonObejct = readJsonFile(filePath);
    log.info("------------------------------");
    log.info(jsonObejct.toJSONString());
    log.info("------------------------------");
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


  public static void main(String[] args) throws IOException {
    String filePath =
            "/home/rednet/x-workspace/bsin-paas-os/bsin-server-apps/bsin-server-brms/brms-server/target/test-classes/rules/rules.json";

    // 读取JSON文件
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rulesNode = objectMapper.readTree(new File(filePath)).get("rules");

    // 规则文件的公共头部处理
    StringBuilder droolsContent = new StringBuilder();
    droolsContent.append("package com.example.rules\n\n");
    droolsContent.append("global java.util.Map globalMap\n\n");

    // 遍历每个规则
    for (JsonNode ruleNode : rulesNode) {
      String ruleName = ruleNode.get("name").asText();
      boolean lockOnActive = ruleNode.get("attributes").get("lock-on-active").asBoolean();

      droolsContent.append("rule \"").append(ruleName).append("\"\n");
      if (lockOnActive) {
        droolsContent.append("    lock-on-active true\n");
      }
      droolsContent.append("when\n");

      // 解析when条件
      for (JsonNode conditionNode : ruleNode.get("conditions")) {
        String type = conditionNode.get("type").asText();
        if ("Map".equals(type)) {
          //
        } else if ("Object".equals(type)) {
          for (JsonNode exprNode : conditionNode.get("conditions")) {
            droolsContent.append("    eval(").append(exprNode.get("expression").asText()).append(")\n");
          }
        }
      }

      droolsContent.append("then\n");

      // 解析then动作
      for (JsonNode actionNode : ruleNode.get("actions")) {
        String type = actionNode.get("type").asText();
        if ("System.out.println".equals(type)) {
          droolsContent.append("    System.out.println(\"").append(actionNode.get("message").asText()).append("\");\n");
        } else if ("globalMap.put".equals(type)) {
          droolsContent.append("    globalMap.put(\"").append(actionNode.get("key").asText()).append("\", \"").append(actionNode.get("value").asText()).append("\");\n");
        } else if ("modify".equals(type)) {
          droolsContent.append("    modify (").append(actionNode.get("object").asText()).append(") {\n");
          JsonNode changesNode = actionNode.get("changes");
          changesNode.fieldNames().forEachRemaining(field -> {
            droolsContent.append("        ").append(field).append(" : \"").append(changesNode.get(field).asText()).append("\",\n");
          });
          droolsContent.deleteCharAt(droolsContent.length() - 2); // 删除最后一个多余的逗号
          droolsContent.append("    }\n");
        }
      }

      droolsContent.append("end\n\n");
    }

    // 获取当前类目录
    String currentDir = System.getProperty("user.dir");
    System.out.println(currentDir);

    // 创建FileWriter对象并写入JSON字符串到文件
    File file = new File(currentDir + "/rules.drl");
    FileWriter fileWriter = new FileWriter(file);
    fileWriter.write(droolsContent.toString());
    fileWriter.close();

    System.out.println("Drools文件生成成功：" + file.getAbsolutePath());

  }

}
