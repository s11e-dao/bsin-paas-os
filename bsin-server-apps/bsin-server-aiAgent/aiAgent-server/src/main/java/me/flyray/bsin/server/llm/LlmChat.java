package me.flyray.bsin.server.llm;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

import java.util.Map;

public interface LlmChat {

    Map<String, Object> chat(Map<String, Object> requestMap) throws NoApiKeyException, InputRequiredException;

}
