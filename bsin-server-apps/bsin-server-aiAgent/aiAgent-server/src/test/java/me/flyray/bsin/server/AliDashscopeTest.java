package me.flyray.bsin.server;

import com.alibaba.dashscope.app.*;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import java.util.List;

import org.junit.Test;

public class AliDashscopeTest {

    @Test
    public void callAgentApp() throws NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                .apiKey("sk-cea3a5fba1784065b05c2cf1d6826ace")
                .appId("85348eefaa5c470a9d4251044242e2fe")
                .prompt("如何做炒西红柿鸡蛋？")
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(param);

        System.out.printf("requestId: %s, text: %s, finishReason: %s\n",
                result.getRequestId(), result.getOutput().getText(), result.getOutput().getFinishReason());
    }

}
