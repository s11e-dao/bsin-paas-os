package org.apache.shenyu.plugin.rulesEngine;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 执行规则接口参数
 */
@Data
public class ExecuteParams implements Serializable {

    /**
     * 事件Key
     */
    @NotBlank(message = "eventCode不能为空")
    String eventCode;

    /**
     * json类型的参数
     */
    JSONObject jsonParams;

    /**
     * 执行参数(即部分事实)
     */
    Map<String, Object> params = new HashMap<>();

    public void setJsonParams(JSONObject jsonParams) {
        this.jsonParams = jsonParams;
        // 将JSONObject转换成Map
        Map<String, Object> hashMap = new HashMap<>();
        for (String key : jsonParams.keySet()) {
            hashMap.put(key, jsonParams.get(key));
        }
        this.params = hashMap;
    }

}
