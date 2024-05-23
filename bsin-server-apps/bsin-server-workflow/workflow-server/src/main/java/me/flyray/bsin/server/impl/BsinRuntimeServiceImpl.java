package me.flyray.bsin.server.impl;


import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.BsinRuntimeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Slf4j
@DubboService
@ApiModule(value = "runtime")
@ShenyuDubboService("/runtime")
public class BsinRuntimeServiceImpl implements BsinRuntimeService {

    @Autowired
    public RuntimeService runtimeService;

    /**
     * 设置执行任务中的变量
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/setVariable")
    @ApiDoc(desc = "setVariable")
    public void setVariable(Map<String, Object> requestMap) {
        String executionId = (String)requestMap.get("executionId");
        String key = (String)requestMap.get("key");
        String value = (String)requestMap.get("value");
        runtimeService.setVariable(executionId,key,value);
    }

    /**
     * 获取执行任务中的变量
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getVariables")
    @ApiDoc(desc = "getVariables")
    public Map<String, Object> getVariables(Map<String,Object> requestMap){
        String executionId = (String)requestMap.get("executionId");
        Map<String, Object> variables = runtimeService.getVariables(executionId);
        return variables;
    }

    /**
     * 根据流程定义Id启动流程实例（可以设置变量和流程发起人）
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/startProcessInstanceById")
    @ApiDoc(desc = "startProcessInstanceById")
    public void startProcessInstanceById(Map<String, Object> requestMap) {
        String processDefinitionId =(String) requestMap.get("processDefinitionId");
        Map<String, Object> variables =(Map<String, Object>) requestMap.get("variables");
        String userId =(String) requestMap.get("userId");
        String businessKey =(String) requestMap.get("businessKey");
        //设置流程启动的发起用户信息
        Authentication.setAuthenticatedUserId(userId);
        try{
            runtimeService.startProcessInstanceById(processDefinitionId,businessKey, variables);
        }catch (Exception e){
            throw new BusinessException(ResponseCode.PROCESS_INSTANCE_START_FAIL);
        }
        Authentication.setAuthenticatedUserId(null);
    }

    @Override
    @ShenyuDubboClient("/startProcessInstanceByKey")
    @ApiDoc(desc = "startProcessInstanceByKey")
    public ProcessInstance startProcessInstanceByKey(Map<String, Object> requestMap) {
        String modelKey =(String) requestMap.get("modelKey");
        Map<String, Object> variables =(Map<String, Object>) requestMap.get("variables");
        String userId =(String) requestMap.get("userId");
        //设置流程启动的发起用户信息
        Authentication.setAuthenticatedUserId(userId);
        ProcessInstance processInstance;
        try{
            processInstance = runtimeService.startProcessInstanceByKey(modelKey, variables);
        }catch (Exception e){
            throw new BusinessException(ResponseCode.PROCESS_INSTANCE_START_FAIL);
        }
        Authentication.setAuthenticatedUserId(null);
        return processInstance;
    }

    /**
     * 附带表单数据启动流程实例（外置表单启动）
     */
    @Override
    @ShenyuDubboClient("/startProcessInstanceWithForm")
    @ApiDoc(desc = "startProcessInstanceWithForm")
    public void startProcessInstanceWithForm(Map<String, Object> requestMap) {
        String processDefinitionId =(String) requestMap.get("processDefinitionId");
        // 表单参数 (表单字段中的<id,value>)
        Map<String, Object> variables = (Map<String, Object>) requestMap.get("variables");
        String outcome =(String) requestMap.get("outcome");
        // 流程实例名称
        String processInstanceName =(String) requestMap.get("processInstanceName");
        String tenantId = (String) requestMap.get("tenantId");
        String businessKey =(String) requestMap.get("businessKey");
//        设置流程启动的发起用户信息
        String userId =(String) requestMap.get("userId");
        Authentication.setAuthenticatedUserId(userId);
        try{
            runtimeService.createProcessInstanceBuilder().processDefinitionId(processDefinitionId).outcome(outcome).startFormVariables(variables).name(processInstanceName).businessKey(businessKey).tenantId(tenantId).start();
        }catch (Exception e){
            throw new BusinessException(ResponseCode.PROCESS_INSTANCE_START_FAIL);
        }
        Authentication.setAuthenticatedUserId(null);
    }



    /**
     * 获取流程实例信息
     */
    @Override
    @ShenyuDubboClient("/getProcessInstanceById")
    @ApiDoc(desc = "getProcessInstanceById")
    public ProcessInstance getProcessInstanceById(Map<String, Object> requestMap) {
        String processInstanceId =(String) requestMap.get("processInstanceId");
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        return processInstance;
    }

    /**
     * 挂起流程实例
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/suspendProcessInstanceById")
    @ApiDoc(desc = "suspendProcessInstanceById")
    public void suspendProcessInstanceById(Map<String, Object> requestMap) {
        String processInstanceId =(String) requestMap.get("processInstanceId");
        //流程实例挂起状态检查
        ProcessInstance processInstance1 = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if(processInstance1.isSuspended()){
            throw new BusinessException(ResponseCode.SUSPEND_PROCESS_INSTANCE_FAIL);
        }
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

    /**
     * 获取挂起的流程实例
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getSuspendedInstances")
    @ApiDoc(desc = "getSuspendedInstances")
    public List<ProcessInstance> getSuspendedInstances(Map<String, Object> requestMap) {
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().suspended().list();
        return processInstanceList;
    }

    /**
     * 激活流程实例
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/activateProcessInstanceById")
    @ApiDoc(desc = "activateProcessInstanceById")
    public void activateProcessInstanceById(Map<String, Object> requestMap) {
        String processInstanceId =(String) requestMap.get("processInstanceId");
        //流程实例激活状态检查
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if(!processInstance.isSuspended()){
            throw new BusinessException(ResponseCode.ACTIVATE_PROCESS_INSTANCE_FAIL);
        }

        runtimeService.activateProcessInstanceById(processInstanceId);
    }

    /**
     * 获取激活的流程实例
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getActiveInstances")
    @ApiDoc(desc = "getActiveInstances")
    public List<ProcessInstance> getActiveInstances(Map<String, Object> requestMap) {
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().active().list();
        return processInstanceList;
    }

    /**
     * 删除流程实例
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/deleteProcessInstance")
    @ApiDoc(desc = "deleteProcessInstance")
    public void deleteProcessInstance(Map<String, Object> requestMap) {
        String processInstanceId =(String) requestMap.get("processInstanceId");
        String deleteReason =(String) requestMap.get("deleteReason");
        try {
            runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.PROCESS_INSTANCE_NOT_EXISTS);
        }
    }
}
