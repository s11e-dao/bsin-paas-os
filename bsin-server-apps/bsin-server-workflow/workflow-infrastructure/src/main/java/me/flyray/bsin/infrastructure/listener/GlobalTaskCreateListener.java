package me.flyray.bsin.infrastructure.listener;

import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.springframework.stereotype.Component;

/**
 * @author bolei
 */

@Component
public class GlobalTaskCreateListener extends AbstractFlowableEngineEventListener {


    /**
     * 任务、创建时 触发
     * 通知相应的任务处理人
     * @param event
     */
    @Override
    protected void taskCreated(FlowableEngineEntityEvent event) {
        //获得任务节点

    }
}
