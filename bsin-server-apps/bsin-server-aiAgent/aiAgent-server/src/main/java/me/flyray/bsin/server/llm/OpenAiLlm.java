package me.flyray.bsin.server.llm;

import java.util.HashMap;
import java.util.Map;

public class OpenAiLlm implements LlmChat{

    @Override
    public Map<String, Object> chat(Map<String, Object> requestMap) {
        return new HashMap<>();
    }
}
