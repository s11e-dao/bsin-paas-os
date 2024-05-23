package me.flyray.bsin.infrastructure.listener;

import me.flyray.bsin.utils.BsinServiceInvokeUtil;
import org.flowable.common.engine.impl.el.FixedValue;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * 执行监听器（委托表达式）
 */
@Component("BsinExecutionListener")
public class BsinExecutionListener implements ExecutionListener {

//    @Autowired
//    public BsinServiceInvokeUtil bsinServiceInvoke;

    FixedValue serviceName;

    FixedValue methodName;

    FixedValue param;
    /**
     * 执行监听器
     * @param delegateExecution
     */
    @Override
    public void notify(DelegateExecution delegateExecution) {
        Map<String,Object> variables = new HashMap<>();
        variables.put("param",param.getExpressionText());
        try {
            // rpc反向调用
//            bsinServiceInvoke.genericInvoke( serviceName.getExpressionText(), methodName.getExpressionText(),null, variables);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
