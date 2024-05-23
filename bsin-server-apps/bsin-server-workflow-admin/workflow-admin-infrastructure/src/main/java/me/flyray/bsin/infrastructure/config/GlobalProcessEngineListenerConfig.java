package me.flyray.bsin.infrastructure.config;

import me.flyray.bsin.infrastructure.listener.GlobalTaskCreateListener;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author cjf
 * ${DATE} ${TIME}
 */

@Configuration
public class GlobalProcessEngineListenerConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private SpringProcessEngineConfiguration configuration;

    @Autowired
    private GlobalTaskCreateListener globalTaskCreateListener;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        FlowableEventDispatcher dispatcher = configuration.getEventDispatcher();
        dispatcher.addEventListener(globalTaskCreateListener,FlowableEngineEventType.TASK_CREATED);

    }
}
