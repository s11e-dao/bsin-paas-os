package me.flyray.bsin.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import me.flyray.bsin.server.handler.JsonToDroolsConverter;
public class JsonToDroolsConverterTest {


    public static void main(String[] args) {

        String filePath =
                "/home/rednet/x-workspace/bsin-paas-os/bsin-server-apps/bsin-server-brms/brms-server/target/test-classes/rules/rules2.json";
        try {

            // 转换成Drools规则文件内容
            String droolsContent = JsonToDroolsConverter.convertToJsonToDrl(new String(Files.readAllBytes(Paths.get(filePath))));

            // 打印生成的Drools规则
            System.out.println(droolsContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
