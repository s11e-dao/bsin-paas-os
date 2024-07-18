package me.flyray.bsin.domain.request;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import me.flyray.bsin.exception.BusinessException;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * 执行规则接口参数
 */
@Data
public class ExecuteParams implements Serializable {

    /**
     * 事件Key
     */
    @NotBlank(message = "eventKey不能为空")
    String eventKey;

    /**
     * json类型的参数
     */
    String jsonParams;

    /**
     * 执行参数(即部分事实)
     */
    Map<?, ?> params;

    public void setJsonParams(String jsonParams) {
        if (!JSON.isValid(jsonParams)) throw new BusinessException("JSONPARAMS_NOT_JSON");
        this.jsonParams = jsonParams;
        params = JSON.parseObject(jsonParams);
    }

}
