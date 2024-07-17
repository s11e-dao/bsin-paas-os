package me.flyray.bsin.server.config;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：bolei
 * @date ：Created in 2023/04/07 16:23
 * @description：DroolsConfig
 * @modified By：
 */

@Configuration
public class DroolsConfig {

    @Bean
    public KieContainer kieContainer() {
        KieServices kieServices = KieServices.Factory.get();
        //kieServices.getKieClasspathContainer()时，会自动寻找 META-INF/kmodule.xml
        // 用于创建 KieModule（KieModule 仅是对 KieBase 以及 KieSession 的定义）
        return kieServices.getKieClasspathContainer();
    }

}
