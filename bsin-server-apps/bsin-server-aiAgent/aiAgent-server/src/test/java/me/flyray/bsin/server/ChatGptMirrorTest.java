package me.flyray.bsin.server;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ChatGptMirrorTest {
    /**
     * 聊天端点 楼主提供国内免费调用ChatGPT站点，不需要开梯子，请求稍微慢一点，请耐心等待哦
     */
    String chatEndpoint = "http://nginx.web-framework-1qoh.1045995386668294.us-west-1.fc.devsapp.net/v1/chat/completions";
    /**
     * api密匙
     */
    String apiKey = "Bearer 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";

    /**
     * 发送消息
     *
     * @return {@link String}
     */
    @org.junit.Test
    public void chat() {
        String prompt = "你好";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("model", "gpt-3.5-turbo");
        List<Map<String, String>> dataList = new ArrayList<>();
        dataList.add(new HashMap<String, String>() {{
            put("role", "user");
            put("content", prompt);
        }});
        paramMap.put("messages", dataList);
        JSONObject message = null;
        try {
            String body = HttpRequest.post(chatEndpoint)
                    .header("Authorization", apiKey)
                    .header("Content-Type", "application/json")
                    .body(JsonUtils.toJson(paramMap))
                    .execute()
                    .body();
            JSONObject jsonObject = JSONUtil.parseObj(body);
            JSONArray choices = jsonObject.getJSONArray("choices");
            JSONObject result = choices.get(0, JSONObject.class, Boolean.TRUE);
            message = result.getJSONObject("message");
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(message.getStr("content"));
    }
}
