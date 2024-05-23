package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.utils.BsinServiceInvokeUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.flowable.common.engine.impl.el.FixedValue;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BsinServiceTask implements JavaDelegate {

    FixedValue serviceName;

    FixedValue methodName;

    FixedValue param;

    /**
     * 反向调用业务工程中的服务任务
     */
    public void execute(DelegateExecution execution) {
        log.info("服务任务调用开始");
        log.info("当前活动ID:{}",execution.getCurrentActivityId());
        log.info("当前流程实例ID:{}",execution.getProcessInstanceId());
        log.info("调用参数{}:",param.getExpressionText());
        Map<String,Object> variables = new HashMap<>();
        variables.put("param",param.getExpressionText());
        BsinServiceInvokeUtil bsinServiceInvoke = new BsinServiceInvokeUtil();
        try {
            // rpc反向调用
            bsinServiceInvoke.genericInvoke( serviceName.getExpressionText(), methodName.getExpressionText(), null, variables);
        }catch (Exception e){
            log.error("服务任务调用失败{}",e.getMessage());
        }
    }
}
