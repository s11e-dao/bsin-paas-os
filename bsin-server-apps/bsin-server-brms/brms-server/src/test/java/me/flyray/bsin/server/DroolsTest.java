package me.flyray.bsin.server;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DroolsTest {

    public static void main(String[] args) {

        // 初始化Drools
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        KieSession kieSession = kieContainer.newKieSession("ksession-rules");

        // 创建globalMap
        Map<String, Object> globalMap = new LinkedHashMap<>();
        kieSession.setGlobal("globalMap", globalMap);

        // 创建要处理的Map对象，事实数据的处理
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("sex", "女");
        inputData.put("userAge", "18");
        inputData.put("userName", "李四");

        // 插入数据并执行规则
        kieSession.insert(inputData);
        kieSession.fireAllRules();

        // 打印globalMap的内容
        System.out.println("globalMap内容：" + globalMap);

        // 关闭会话
        kieSession.dispose();

    }

}
