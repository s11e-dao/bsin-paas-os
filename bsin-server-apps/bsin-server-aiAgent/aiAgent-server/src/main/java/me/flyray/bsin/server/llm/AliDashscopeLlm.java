package me.flyray.bsin.server.llm;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

import java.util.HashMap;
import java.util.Map;

public class AliDashscopeLlm implements LlmChat{

    @Override
    public Map<String, Object> chat(Map<String, Object> requestMap) throws NoApiKeyException, InputRequiredException {
        // 向量结果
        String text = (String) requestMap.get("text");
        String opCodeParams = (String) requestMap.get("opCodeParams");
        String question = (String) requestMap.get("question");

        ApplicationParam param = ApplicationParam.builder()
                .apiKey("sk-cea3a5fba1784065b05c2cf1d6826ace")
                .appId("85348eefaa5c470a9d4251044242e2fe")
                .prompt("帮我根据提供的信息，只生成请求接口的json字符串，不要带```json，不要返回多余的信息，信息为："+ question +" ，接口的请求数据字段为：" + opCodeParams )
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(param);

        System.out.printf("requestId: %s, 大模型返回结果: %s \n",
                result.getRequestId(), result.getOutput().getText());
        Map resultMap = new HashMap<>();
        resultMap.put("jsonAnswer", result.getOutput().getText());
        return resultMap;
    }
}
