package me.flyray.bsin.facade.service;

import me.flyray.bsin.domain.entity.BsinActivityInstance;
import me.flyray.bsin.domain.response.DefinitionResp;

import java.util.List;
import java.util.Map;


public interface BsinAdminProcessInstanceService {

    /**
     * 流程实例挂起
     * @param requestMap
     * @return
     */
    void suspendProcessInstance(Map<String, Object> requestMap);

    /**
     * 激活流程实例
     * @param requestMap
     * @return
     */
    Map<String, Object> activateProcessInstance(Map<String, Object> requestMap);

    /**
     * 分页查询流程实例
     * @param requestMap
     * @return
     */
    DefinitionResp getPageListProcessInstance(Map<String, Object> requestMap);

    /**
     * 分页查询流程实例
     * @param requestMap
     * @return
     */
    DefinitionResp getMyProcessInstancePageList(Map<String, Object> requestMap);

    /**
     * 流程实例预览
     * @param requestMap
     * @return
     */
    Map<String, Object> processInstancePreview(Map<String, Object> requestMap);

    /**
     * 流程实例预览
     * @param requestMap
     * @return
     */
    List<BsinActivityInstance> getActInstList(Map<String, Object> requestMap);
}
