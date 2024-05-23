package me.flyray.bsin.facade.service;


import java.util.Map;


public interface BsinFormRepositoryService {

    /**
     * 获取开始表单数据
     */
    public Map<String, Object>getStartFormData(Map<String, Object> requestMap);

    /**
     * 获取任务表单数据
     */
    public Map<String, Object>getTaskFormData(Map<String, Object> requestMap);

    /**
     * 提交开始表单参数
     */
    public Map<String, Object>submitStartFormData(Map<String, Object> requestMap);

    /**
     * 保存任务表单数据(内置)
     */
    public Map<String, Object>saveFormData(Map<String, Object> requestMap);

    /**
     * 完成任务并提交表单数据
     */
    public Map<String, Object>submitTaskFormData(Map<String, Object> requestMap) throws ClassNotFoundException;

    /**
     * 部署表单
     */
    public Map<String, Object>deployForm(Map<String, Object> requestMap) throws ClassNotFoundException;

    /**
     * 获取开始表单数据
     */
    public Map<String, Object>getProcessDefinitionStartForm(Map<String, Object> requestMap) throws ClassNotFoundException;
}
