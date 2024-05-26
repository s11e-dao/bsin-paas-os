package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.service.BsinFormRepositoryService;
import me.flyray.bsin.infrastructure.utils.RespBodyHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.form.api.FormDefinition;
import org.flowable.form.api.FormDeployment;
import org.flowable.form.api.FormInfo;
import org.flowable.form.api.FormRepositoryService;
import org.flowable.form.model.FormField;
import org.flowable.form.model.SimpleFormModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Slf4j
@DubboService
@ApiModule(value = "formRepository")
@ShenyuDubboService("/formRepository")
public class BsinFormRepositoryServiceImpl implements BsinFormRepositoryService {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FormRepositoryService formRepositoryService;
    @Autowired
    private FormService formService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    protected ProcessEngineConfiguration processEngineConfiguration;
    @Autowired
    private HistoryService historyService;

    /**
     * 部署form表单（用作测试）
     */
    @Override
    @ShenyuDubboClient("/deployForm")
    @ApiDoc(desc = "deployForm")
    public Map<String, Object> deployForm(Map<String, Object> requestMap){
        FormDeployment formDeployment = formRepositoryService.createDeployment()
                .addClasspathResource("reply_approval.form")
                .name("回复表")
                .parentDeploymentId("d8cc5265-858f-11ed-af1c-005056c00001")  // 流程部署id
                .deploy();
        System.out.println("formDeployment.getId() = " + formDeployment.getId());
        // 获取表单定义
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().deploymentId(formDeployment.getId()).singleResult();
        return RespBodyHandler.setRespBodyDto(formDefinition);
    }

    /**
     * 获取开始表单模型信息（渲染）（用于启动流程实例之前）
     */
    @Override
    @ShenyuDubboClient("/getProcessDefinitionStartForm")
    @ApiDoc(desc = "getProcessDefinitionStartForm")
    public Map<String, Object> getProcessDefinitionStartForm(Map<String, Object> requestMap){
        String processDefinitionId = (String) requestMap.get("processDefinitionId");
        FormInfo formInfo = null;
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Process process = bpmnModel.getProcessById(processDefinition.getKey());
        FlowElement startElement = process.getInitialFlowElement();
        if (startElement instanceof StartEvent) {
            StartEvent startEvent = (StartEvent) startElement;
            if (StringUtils.isNotEmpty(startEvent.getFormKey())) {
                formInfo = formRepositoryService.getFormModelByKeyAndParentDeploymentId(startEvent.getFormKey(), processDefinition.getDeploymentId(),
                        processDefinition.getTenantId(), true);
            }
        }
        if (formInfo == null) {
            // Definition found, but no form attached
            return RespBodyHandler.RespBodyDto();
        }
        SimpleFormModel formModel = (SimpleFormModel) formInfo.getFormModel();
        return RespBodyHandler.setRespBodyDto(formModel);
    }

    /**
     * 查看指定流程实例的开始表单数据（渲染）（用于启动流程实例之后）
     */
    @Override
    @ShenyuDubboClient("/getStartFormData")
    @ApiDoc(desc = "getStartFormData")
    public Map<String, Object> getStartFormData(Map<String, Object> requestMap){
        String processInstanceId = (String)requestMap.get("processInstanceId");
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // FormInfo 表单的元数据信息
        FormInfo formInfo = runtimeService.getStartFormModel(processInstance.getProcessDefinitionId(), processInstanceId);
        // FormModel 表单中的具体信息 具体实现是 SimpleFormModel
        SimpleFormModel formModel = (SimpleFormModel) formInfo.getFormModel();
        List<FormField> fields = formModel.getFields();
        return RespBodyHandler.setRespBodyDto(formModel);
    }

    /**
     * 获取任务表单数据（渲染）
     */
    @Override
    @ShenyuDubboClient("/getTaskFormData")
    @ApiDoc(desc = "getTaskFormData")
    public Map<String, Object> getTaskFormData(Map<String, Object> requestMap){
        String taskId = (String) requestMap.get("taskId");
        FormInfo formInfo;
        try{
            formInfo = taskService.getTaskFormModel(taskId);
        }catch (Exception e){
            return RespBodyHandler.RespBodyDto();
        }
        SimpleFormModel formModel = (SimpleFormModel) formInfo.getFormModel();
        return RespBodyHandler.setRespBodyDto(formModel);
    }


    /**
     * 提交开始表单数据
     * （提交开始表单数据并启动流程的接口在BsinRuntimeService中）
     */
    @Override
    @ShenyuDubboClient("/submitStartFormData")
    @ApiDoc(desc = "submitStartFormData")
    public Map<String, Object> submitStartFormData(Map<String, Object> requestMap){
        String processDefinitionId = (String) requestMap.get("processDefinitionId");
        Map<String, String> properties = (Map<String, String>) requestMap.get("properties");
        formService.submitStartFormData(processDefinitionId,properties);
        return RespBodyHandler.RespBodyDto();
    }

    /**
     * 保存任务表单数据(任务未完成)
     */
    @Override
    @ShenyuDubboClient("/saveFormData")
    @ApiDoc(desc = "saveFormData")
    public Map<String, Object> saveFormData(Map<String, Object> requestMap){
        String taskId = (String) requestMap.get("taskId");
        Map<String, String> formData = (Map<String, String>) requestMap.get("variables");
        formService.saveFormData(taskId, formData);
        return RespBodyHandler.RespBodyDto();
    }


    /**
     * 完成任务并提交表单数据
     */
    @Override
    @ShenyuDubboClient("/submitTaskFormData")
    @ApiDoc(desc = "submitTaskFormData")
    public Map<String, Object> submitTaskFormData(Map<String, Object> requestMap){
        String taskId = (String) requestMap.get("taskId");
        Map<String, String> formData = (Map<String, String>) requestMap.get("variables");
        formService.submitTaskFormData(taskId, formData);
        return RespBodyHandler.RespBodyDto();
    }


}
