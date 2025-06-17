package me.flyray.bsin.infrastructure.config;

import org.flowable.ui.common.security.SecurityConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * 绕过flowable的登录验证
 */
//@Configuration
//public class SecurityConfiguration {
//    @Configuration(proxyBeanMethods = false)
//    //Order配置说明
//    // 这个地方相同会报错
//    //这个地方如果大于则该配置在FlowableUiSecurityAutoConfiguratio中对应项后加载，不能起到绕过授权作用
//    //所以这个地方-1让该配置项在FlowableUiSecurityAutoConfiguratio中对应配置项前加载，以跳过授权
//    @Order(SecurityConstants.FORM_LOGIN_SECURITY_ORDER - 1)
//    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http.headers().frameOptions().disable();
//
//            http
//                //必须要将csrf设置为disable，不然后面发送POST请求时会报403错误
//                .csrf().disable()
//                //为了简单起见，简单粗暴方式直接放行modeler下面所有请求
//                .authorizeRequests().antMatchers("/modeler/**").permitAll();
//        }
//    }
//}

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/", "/home").permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())  // New lambda-based approach
                .formLogin(withDefaults());
        return http.build();
    }
}