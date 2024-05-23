package me.flyray.bsin.facade.service;

import org.flowable.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.Map;


public interface BsinRuntimeService {

    /**
     * 根据执行id获取流程变量
     */
    Map<String,Object> getVariables(Map<String, Object> requestMap);

    /**
     * 设置执行任务中的变量
     */
    void setVariable(Map<String, Object> requestMap);


    /**
     * 根据流程定义Id启动流程实例
     */
    void startProcessInstanceById(Map<String, Object> requestMap);

    /**
     * 根据模型key启动流程实例
     */
    ProcessInstance startProcessInstanceByKey(Map<String, Object> requestMap);

    /**
     * 附带表单数据启动流程实例
     */
    void startProcessInstanceWithForm(Map<String, Object> requestMap);

    /**
     * 根据ID查询运行中的流程实例
     *
     * @param
     */
    ProcessInstance getProcessInstanceById(Map<String, Object> requestMap);

    /**
     * 挂起流程实例
     *
     * @param
     */
    void suspendProcessInstanceById(Map<String, Object> requestMap);

    /**
     * 查询挂起流程实例
     *
     * @param
     */
    List<ProcessInstance> getSuspendedInstances(Map<String, Object> requestMap);

    /**
     * 激活流程实例
     *
     * @param
     */
    void activateProcessInstanceById(Map<String, Object> requestMap);

    /**
     * 查询激活流程实例
     *
     * @param
     */
    List<ProcessInstance> getActiveInstances(Map<String, Object> requestMap);

    /**
     *删除流程实例
     *
     * @param
     */
    void deleteProcessInstance(Map<String, Object> requestMap);
}
