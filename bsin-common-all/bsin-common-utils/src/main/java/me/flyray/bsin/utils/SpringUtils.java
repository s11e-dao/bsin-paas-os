package me.flyray.bsin.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具类 - 用于手动获取Bean 使用说明： 1. 确保此类被Spring扫描（通过@Component注解） 2. 在需要的地方调用
 *
 * @author leonard
 * @description
 * @createDate 2025/07/2025/7/25 /19/44
 */
@Component
public class SpringUtils extends SpringUtil {

  /** 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true */
  public static boolean containsBean(String name) {
    return getBeanFactory().containsBean(name);
  }

  /**
   * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
   * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
   */
  public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
    return getBeanFactory().isSingleton(name);
  }

  /**
   * @return Class 注册对象的类型
   */
  public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
    return getBeanFactory().getType(name);
  }

  /** 如果给定的bean名字在bean定义中有别名，则返回这些别名 */
  public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
    return getBeanFactory().getAliases(name);
  }

  /** 获取aop代理对象 */
  @SuppressWarnings("unchecked")
  public static <T> T getAopProxy(T invoker) {
    return (T) getBean(invoker.getClass());
  }

  /** 获取spring上下文 */
  public static ApplicationContext context() {
    return getApplicationContext();
  }

  public static boolean isVirtual() {
    return Threading.VIRTUAL.isActive(getBean(Environment.class));
  }
}
