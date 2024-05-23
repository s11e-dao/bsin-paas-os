package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.entity.BsinActivityInstance;
import me.flyray.bsin.domain.response.DefinitionResp;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.BsinAdminProcessInstanceService;
import me.flyray.bsin.infrastructure.biz.ProcessInstanceBiz;
import me.flyray.bsin.mybatis.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.ui.common.model.ResultListDataRepresentation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程实例
 */
@Slf4j
@DubboService
@ApiModule(value = "processInstance")
@ShenyuDubboService("/processInstance")
public class ProcessInstanceServiceImpl implements BsinAdminProcessInstanceService {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ProcessInstanceBiz processInstanceBiz;
    @Autowired
    private HistoryService historyService;

    /**
     * 挂起流程实例
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/suspendProcessInstance")
    @ApiDoc(desc = "suspendProcessInstance")
    public void suspendProcessInstance(Map<String, Object> requestMap) {
        String processInstanceId = (String) requestMap.get("processInstanceId");
        //根据流程实例id查询流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        //判断流程实例是否已挂起
        if(processInstance.isSuspended()){
            throw new BusinessException(ResponseCode.SUSPEND_PROCESS_INSTANCE_FAIL);
        }
        //按 ID 挂起流程实例
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

    /**
     * 激活流程实例
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/activateProcessInstance")
    @ApiDoc(desc = "activateProcessInstance")
    public Map<String, Object> activateProcessInstance(Map<String, Object> requestMap) {
        String processInstanceId =(String) requestMap.get("processInstanceId");
        //根据流程实例id查询流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        //判断流程实例是否已激活
        if(!processInstance.isSuspended()){
            throw new BusinessException(ResponseCode.ACTIVATE_PROCESS_INSTANCE_FAIL);
        }
        //按 ID 激活流程实例
        runtimeService.activateProcessInstanceById(processInstanceId);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("processInstanceId", processInstanceId);
        return resultMap;
    }

    /**
     * 分页查询运行中的流程实例
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getPageListProcessInstance")
    @ApiDoc(desc = "getPageListProcessInstance")
    public DefinitionResp getPageListProcessInstance(Map<String, Object> requestMap) {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        String tenantId = (String) requestMap.get("tenantId");
        //进程实例 ID
        String processInstanceId = (String) requestMap.get("processInstanceId");
        //业务唯一标识
        String businessKey = (String) requestMap.get("businessKey");
        //流程实例名称
        String processInstanceName = (String) requestMap.get("processInstanceName");
        //流程定义名称
        String processDefinitionName = (String) requestMap.get("processDefinitionName");
        //当前页
        Integer pageNum = (Integer) pagination.get("pageNum");
        //页大小
        Integer pageSize = (Integer) pagination.get("pageSize");
        List<ProcessInstance> instances = null;
        long total = 0L;
        if (StringUtils.isNotEmpty(businessKey)) {
            //获取流程实例
            instances = runtimeService.createProcessInstanceQuery().includeProcessVariables().processInstanceTenantId(tenantId)
                    .processInstanceBusinessKey(businessKey).orderByStartTime().desc().
                    listPage((pageNum - 1) * pageSize, pageSize);
            //总条数
            total = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).processInstanceTenantId(tenantId).
                    count();

        } else if (StringUtils.isNotEmpty(processInstanceId)) {
            //获取流程实例
            instances = runtimeService.createProcessInstanceQuery().includeProcessVariables().processInstanceTenantId(tenantId).
                    processInstanceId(processInstanceId).orderByStartTime().desc().listPage((pageNum - 1) * pageSize,
                    pageSize);
            //总条数
            total = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).processInstanceTenantId(tenantId).count();

        } else if (StringUtils.isNotEmpty(processDefinitionName)){
            //获取流程实例
            instances = runtimeService.createProcessInstanceQuery().includeProcessVariables().processInstanceTenantId(tenantId).
                    processDefinitionName(processDefinitionName).orderByStartTime().desc().
                    listPage((pageNum - 1) * pageSize, pageSize);
            //总条数
            total = runtimeService.createProcessInstanceQuery().processDefinitionName(processDefinitionName).processInstanceTenantId(tenantId).count();
        } else if(StringUtils.isNotEmpty(processInstanceName)){
            //获取流程实例
            instances = runtimeService.createProcessInstanceQuery().includeProcessVariables().processInstanceTenantId(tenantId).
                    processInstanceName(processInstanceName).orderByStartTime().desc().
                    listPage((pageNum - 1) * pageSize, pageSize);
            //总条数
            total = runtimeService.createProcessInstanceQuery().processDefinitionName(processDefinitionName).processInstanceTenantId(tenantId).count();
        } else {
            //获取流程实例
            instances = runtimeService.createProcessInstanceQuery().includeProcessVariables().processInstanceTenantId(tenantId).orderByStartTime().
                    desc().listPage((pageNum - 1) * pageSize, pageSize);
            //总条数
            total = runtimeService.createProcessInstanceQuery().processInstanceTenantId(tenantId).count();
        }

        //对查询的结果进行dto转换
        ResultListDataRepresentation result = new ResultListDataRepresentation(processInstanceBiz.
                convertInstanceList(instances));

        //构建返回参数值
        DefinitionResp queryRunInstancesResp = new DefinitionResp();
        queryRunInstancesResp.setPageNum(pageNum);
        queryRunInstancesResp.setTotal(total);
        queryRunInstancesResp.setPageSize(pageSize);
        queryRunInstancesResp.setDeployModels(result.getData());
        return queryRunInstancesResp;
    }

    @Override
    @ShenyuDubboClient("/getMyProcessInstancePageList")
    @ApiDoc(desc = "getMyProcessInstancePageList")
    public DefinitionResp getMyProcessInstancePageList(Map<String, Object> requestMap) {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        String tenantId = (String) requestMap.get("tenantId");
        //进程实例 ID
        String processInstanceId = (String) requestMap.get("processInstanceId");
        //业务唯一标识
        String businessKey = (String) requestMap.get("businessKey");
        String userId = (String) requestMap.get("userId");
        //流程实例名称
        String processInstanceName = (String) requestMap.get("processInstanceName");
        //流程定义名称
        String processDefinitionName = (String) requestMap.get("processDefinitionName");
        //当前页
        Integer pageNum = (Integer) pagination.get("pageNum");
        //页大小
        Integer pageSize = (Integer) pagination.get("pageSize");
        List<ProcessInstance> instances = null;
        long total = 0L;
        if (StringUtils.isNotEmpty(businessKey)) {
            //获取流程实例
            instances = runtimeService.createProcessInstanceQuery().includeProcessVariables().processInstanceTenantId(tenantId).startedBy(userId)
                    .processInstanceBusinessKey(businessKey).orderByStartTime().desc().
                    listPage((pageNum - 1) * pageSize, pageSize);
            //总条数
            total = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).processInstanceTenantId(tenantId).
                    count();

        } else if (StringUtils.isNotEmpty(processInstanceId)) {
            //获取流程实例
            instances = runtimeService.createProcessInstanceQuery().includeProcessVariables().processInstanceTenantId(tenantId).
                    processInstanceId(processInstanceId).orderByStartTime().desc().listPage((pageNum - 1) * pageSize,
                            pageSize);
            //总条数
            total = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).processInstanceTenantId(tenantId).count();

        } else if (StringUtils.isNotEmpty(processDefinitionName)){
            //获取流程实例
            instances = runtimeService.createProcessInstanceQuery().includeProcessVariables().processInstanceTenantId(tenantId).
                    processDefinitionName(processDefinitionName).orderByStartTime().desc().
                    listPage((pageNum - 1) * pageSize, pageSize);
            //总条数
            total = runtimeService.createProcessInstanceQuery().processDefinitionName(processDefinitionName).processInstanceTenantId(tenantId).count();
        } else if(StringUtils.isNotEmpty(processInstanceName)){
            //获取流程实例
            instances = runtimeService.createProcessInstanceQuery().includeProcessVariables().processInstanceTenantId(tenantId).
                    processInstanceName(processInstanceName).orderByStartTime().desc().
                    listPage((pageNum - 1) * pageSize, pageSize);
            //总条数
            total = runtimeService.createProcessInstanceQuery().processDefinitionName(processDefinitionName).processInstanceTenantId(tenantId).count();
        } else {
            //获取流程实例
            instances = runtimeService.createProcessInstanceQuery().includeProcessVariables().processInstanceTenantId(tenantId).orderByStartTime().
                    desc().listPage((pageNum - 1) * pageSize, pageSize);
            //总条数
            total = runtimeService.createProcessInstanceQuery().processInstanceTenantId(tenantId).count();
        }

        //对查询的结果进行dto转换
        ResultListDataRepresentation result = new ResultListDataRepresentation(processInstanceBiz.
                convertInstanceList(instances));

        //构建返回参数值
        DefinitionResp queryRunInstancesResp = new DefinitionResp();
        queryRunInstancesResp.setPageNum(pageNum);
        queryRunInstancesResp.setTotal(total);
        queryRunInstancesResp.setPageSize(pageSize);
        queryRunInstancesResp.setDeployModels(result.getData());
        return queryRunInstancesResp;
    }


    /**
     * 流程实例预览
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/processInstancePreview")
    @ApiDoc(desc = "processInstancePreview")
    public Map<String, Object> processInstancePreview(Map<String, Object> requestMap) {
        Map<String,Object> bizParams = (Map<String,Object>)requestMap.get("bizParams");
        String processInstanceId =(String) bizParams.get("processInstanceId");
        //查询流程图与节点状态
       /* String modelAndStatusByProcessInstanceId = processInstanceBiz.getModelAndStatusByProcessInstanceId(processInstanceId);
        //jsonString反序列化
        Map map = JSON.parseObject(modelAndStatusByProcessInstanceId, Map.class);*/
        //构建返回参数值
        return null;
    }

    //___________________________________________________________________________________________________________



    /**
     * 查询当前活动信息
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getActInstList")
    @ApiDoc(desc = "getActInstList")
    public List<BsinActivityInstance> getActInstList(Map<String, Object> requestMap) {
        String processInstanceId = (String) requestMap.get("processInstanceId");
        List<ActivityInstance> list = runtimeService.createActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByActivityInstanceStartTime().asc()
                .list();
        List<BsinActivityInstance> bsinActivityInstancesList = new ArrayList<>();
        for (ActivityInstance activityInstance : list) {
            if(activityInstance.getActivityType().equals("sequenceFlow")){
                continue;
            }
            BsinActivityInstance bsinActivityInstance = new BsinActivityInstance();
            BeanUtils.copyProperties(activityInstance,bsinActivityInstance);
            if(activityInstance.getActivityType().equals("startEvent")){
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                bsinActivityInstance.setAssignee(processInstance.getStartUserId());
            }
            if(activityInstance.getActivityType().equals("userTask")){
                Map<String, Object> variables = runtimeService.getVariables(activityInstance.getExecutionId());
                bsinActivityInstance.setApprovalOpinion((String) variables.get("approvalOpinion"));
            }
            bsinActivityInstancesList.add(bsinActivityInstance);
        }
        return bsinActivityInstancesList;
    }

}
