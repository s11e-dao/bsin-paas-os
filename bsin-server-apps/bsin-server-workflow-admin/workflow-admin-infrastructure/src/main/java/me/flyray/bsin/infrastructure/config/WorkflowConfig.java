package me.flyray.bsin.infrastructure.config;

import org.flowable.ui.common.service.idm.RemoteIdmService;
import org.flowable.ui.modeler.properties.FlowableModelerAppProperties;
import org.flowable.ui.modeler.servlet.ApiDispatcherServletConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author ：bolei
 * @date ：Created in 2020/10/15 19:00
 * @description：流程引擎配置
 * @modified By：
 */

@Configuration
@EnableConfigurationProperties(FlowableModelerAppProperties.class)
@ComponentScan(basePackages = {
//        "org.flowable.ui.modeler.conf",
        "org.flowable.ui.modeler.repository",
//        "org.flowable.ui.task.model.runtime",
        "org.flowable.ui.modeler.service",
//        "org.flowable.ui.modeler.security", //授权方面的都不需要
//        "org.flowable.ui.common.conf", // flowable 开发环境内置的数据库连接
//        "org.flowable.ui.common.filter", // IDM 方面的过滤器
        "org.flowable.ui.common.service",
        "org.flowable.ui.common.repository",
        //
//        "org.flowable.ui.common.security",//授权方面的都不需要
        "org.flowable.ui.common.tenant" },excludeFilters = {
// 移除 RemoteIdmService
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value =
                RemoteIdmService.class),
}
)
public class WorkflowConfig {

    @Bean
    public ServletRegistrationBean modelerApiServlet(ApplicationContext applicationContext) {
        AnnotationConfigWebApplicationContext dispatcherServletConfiguration = new AnnotationConfigWebApplicationContext();
        dispatcherServletConfiguration.setParent(applicationContext);
        dispatcherServletConfiguration.register(ApiDispatcherServletConfiguration.class);
        DispatcherServlet servlet = new DispatcherServlet(dispatcherServletConfiguration);
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(servlet, "/api/*");
        registrationBean.setName("Flowable Modeler App API Servlet");
        registrationBean.setLoadOnStartup(1);
        registrationBean.setAsyncSupported(true);
        return registrationBean;
    }

}
