package me.flyray.bsin.server.handler;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Log4j2
@Service
public class JsonToDroolsHandler {

    public String jsonToDrools(String json) throws IOException {

        // 读取JSON文件
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rulesNode = objectMapper.readTree(json.getBytes());

        // 规则文件的公共头部处理
        StringBuilder droolsContent = new StringBuilder();
        droolsContent.append("package rules\n\n");
        droolsContent.append("global java.util.Map globalMap\n\n");

        // 遍历每个规则
        for (JsonNode ruleNode : rulesNode) {
            String ruleName = ruleNode.get("name").asText();
            boolean lockOnActive = ruleNode.get("attributes").get("lock-on-active").asBoolean();

            // 解析属性
            droolsContent.append("rule \"").append(ruleName).append("\"\n");
            if (lockOnActive) {
                droolsContent.append("    lock-on-active true\n");
            }
            droolsContent.append("when\n");

            // 解析when条件
            for (JsonNode conditionNode : ruleNode.get("when").get("conditions")) {
                String type = conditionNode.get("type").asText();
                if ("Map".equals(type)) {
                    String variable = conditionNode.get("variable").asText();
                    droolsContent.append("    ").append(variable).append(" : Map()\n");
                } else if ("Object".equals(type)) {
                    for (JsonNode exprNode : conditionNode.get("conditions")) {
                        droolsContent.append("    eval(").append(exprNode.get("expression").asText()).append(")\n");
                    }
                }
            }

            droolsContent.append("then\n");

            // 解析then动作
            for (JsonNode actionNode : ruleNode.get("then")) {
                String action = actionNode.get("action").asText();
                if ("System.out.println".equals(action)) {
                    droolsContent.append("    System.out.println(\"").append(actionNode.get("message").asText()).append("\");\n");
                } else if ("globalMap.put".equals(action)) {
                    droolsContent.append("    globalMap.put(\"").append(actionNode.get("key").asText()).append("\", \"").append(actionNode.get("value").asText()).append("\");\n");
                } else if ("modify".equals(action)) {
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

        // 将生成的文件保存到文件夹
        String currentDir = System.getProperty("user.dir");
        System.out.println(currentDir);
        // 创建FileWriter对象并写入JSON字符串到文件
        File file = new File(currentDir + "/rules/rules.drl");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(droolsContent.toString());
        fileWriter.close();

        System.out.println("Drools文件生成成功：" + file.getAbsolutePath());

        return droolsContent.toString();
    }
}
