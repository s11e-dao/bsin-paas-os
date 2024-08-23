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

        ApplicationParam param = ApplicationParam.builder()
                .apiKey("sk-cea3a5fba1784065b05c2cf1d6826ace")
                .appId("85348eefaa5c470a9d4251044242e2fe")
                .prompt("帮我根据提供的用户信息，只生成请求用户新增接口的json字符串，不要带```json，不要返回多余的信息，信息为：我的名字叫博羸，今年19岁，是软件工程师，用户新增接口的请求数据字段有：name,age,job")
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(param);

        System.out.printf("requestId: %s, text: %s, finishReason: %s\n",
                result.getRequestId(), result.getOutput().getText(), result.getOutput().getFinishReason());

        return new HashMap<>();
    }
}
