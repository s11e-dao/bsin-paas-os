package me.flyray.bsin.server.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;

/**
 *  前端规则配置json转换成drools规则文件处理
 */

public class JsonToDroolsConverter {

    public static String convertToJsonToDrl(String jsonStr) throws IOException {

        // 解析JSON
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonStr);

        StringBuilder droolsContent = new StringBuilder();
        droolsContent.append("package rules\n\n");
        droolsContent.append("global java.util.Map globalMap\n\n");

        ArrayNode rulesArray = (ArrayNode) rootNode.get("rules");
        for (JsonNode ruleNode : rulesArray) {
            ObjectNode ruleObject = (ObjectNode) ruleNode;

            // Rule name and attributes
            droolsContent.append("rule \"" + ruleObject.get("name").asText() + "\"\n");
            droolsContent.append("    lock-on-active true\n");
            droolsContent.append("when\n");

            // Conditions
            droolsContent.append("    $map : Map()\n");
            ArrayNode conditionsArray = (ArrayNode) ruleObject.get("conditions");
            StringBuilder conditions = new StringBuilder("eval(");
            boolean firstCondition = true;
            for (JsonNode conditionNode : conditionsArray) {
                if (!firstCondition) {
                    conditions.append(" ").append(conditionNode.get("logic").asText()).append(" ");
                }
                firstCondition = false;
                ObjectNode conditionObject = (ObjectNode) conditionNode;
                conditions.append(convertExpression(conditionObject.get("expression")));
            }
            conditions.append(")");
            droolsContent.append("    ").append(conditions.toString()).append("\n");

            // Actions
            droolsContent.append("then\n");
            ArrayNode actionsArray = (ArrayNode) ruleObject.get("actions");
            for (JsonNode actionNode : actionsArray) {
                String actionType = actionNode.get("type").asText();
                if (actionType.equals("globalMap.put")) {
                    String key = actionNode.get("key").asText();
                    String value = actionNode.get("value").asText();
                    droolsContent.append("    globalMap.put(\"").append(key).append("\", \"").append(value).append("\");\n");
                } else if (actionType.equals("update")) {
                    String map = actionNode.get("map").asText();
                    droolsContent.append("    update(").append(map).append(");\n");
                } else if (actionType.equals("print")) {
                    String message = actionNode.get("message").asText();
                    droolsContent.append("    System.out.println(\"").append(message).append("\");\n");
                } else if (actionType.equals("dubboInvoke")) {
                    // TODO dubbo泛化调用，处理命中后逻辑

                }
            }

            droolsContent.append("end\n\n");
        }

        return droolsContent.toString();
    }

    /**
     * 表达式解析处理
     * @param expressionNode
     * @return
     */
    private static String convertExpression(JsonNode expressionNode) {
        StringBuilder expressionBuilder = new StringBuilder();
        ObjectNode expressionObject = (ObjectNode) expressionNode;

        JsonNode leftNode = expressionObject.get("left");
        JsonNode operatorNode = expressionObject.get("operator");
        JsonNode rightNode = expressionObject.get("right");
        if (leftNode != null && operatorNode != null && rightNode != null) {
            expressionBuilder.append(convertOperand(leftNode));
            expressionBuilder.append(" ");
            expressionBuilder.append(operatorNode.asText());
            expressionBuilder.append(" ");
            expressionBuilder.append(convertOperand(rightNode));
        }

        return expressionBuilder.toString();
    }

    /**
     * 算术运算处理
     * @param operandNode
     * @return
     */
    private static String convertOperand(JsonNode operandNode) {
        StringBuilder operandBuilder = new StringBuilder();
        ObjectNode operandObject = (ObjectNode) operandNode;
        String type = operandObject.get("type").asText();

        if ("value".equals(type)) {
            String value = operandObject.get("value").asText();
            operandBuilder.append(value);
        } else if ("bracket".equals(type)) {
            // 如果是括号里面还有表达式
            JsonNode expressionNode = operandObject.get("expression");
            operandBuilder.append("(");
            operandBuilder.append(convertExpression(expressionNode));
            operandBuilder.append(")");
        }

        return operandBuilder.toString();
    }

    public static void main(String[] args) {

        String filePath =
                "/home/rednet/x-workspace/bsin-paas-os/bsin-server-apps/bsin-server-brms/brms-server/target/test-classes/rules/rules2.json";
        try {
            // 解析JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(filePath));

            // 转换成Drools规则文件内容
            String droolsContent = convertToJsonToDrl(String.valueOf(rootNode));

            // 打印生成的Drools规则
            System.out.println(droolsContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
