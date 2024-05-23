package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.FlowElementsVo;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.BsinTaskService;
import me.flyray.bsin.infrastructure.utils.FlowableUtils;
import me.flyray.bsin.mybatis.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
@DubboService
@ApiModule(value = "task")
@ShenyuDubboService("/task")
public class BsinTaskServiceImpl implements BsinTaskService  {
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;

    /**
     * 根据用户名获取待办任务列表
     * @param requestMap
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    @ShenyuDubboClient("/getTaskByUser")
    @ApiDoc(desc = "getTaskByUser")
    public List<Task> getTaskByUser(Map<String, Object> requestMap) {
        String assignee =(String) requestMap.get("assignee");
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> taskList = taskQuery.taskAssignee(assignee).list();
//        List<TaskVo> taskVos = TaskConvert.INSTANCE.toConvertTaskVoList(taskList);
//        for (TaskVo task:taskVos) {
//            Map<String, Object> variables = taskService.getVariables(task.taskId);
//            String medicalAdvice =(String)variables.get("medicalAdvice");
//            task.setMedicalAdvice(medicalAdvice);
//        }
//         return RespBodyHandler.setRespBodyListDto(taskVos);
        return taskList;
    }

    /**
     * 根据用户名获取候选任务
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getCandidateTaskByUser")
    @ApiDoc(desc = "getCandidateTaskByUser")
    public List<Task> getCandidateTaskByUser(Map<String, Object> requestMap){
        String username =(String) requestMap.get("username");
        String processInstanceId =(String) requestMap.get("processInstanceId");
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId)
                //设置taskCandidateUser候选人条件来查询任务，这里的参数值就是候选人流程变量绑定的值
                //员工1和员工2都是可以查询到数据的
                .taskCandidateUser(username)
                .list();
        return taskList;
    }

    /**
     * 领取候选任务
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/claimCandidateTask")
    @ApiDoc(desc = "claimCandidateTask")
    public void claimCandidateTask(Map<String, Object> requestMap){
        String username =(String) requestMap.get("username");
        String taskId =(String) requestMap.get("taskId");
        taskService.claim(taskId, username);
    }

    /**
     * 任务归还（如果任务拾取之后不想操作或者误拾取任务也可以进行归还任务）
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/unClaimCandidateTask")
    @ApiDoc(desc = "unClaimCandidateTask")
    public void unClaimCandidateTask(Map<String, Object> requestMap){
        String taskId =(String) requestMap.get("taskId");
        taskService.unclaim(taskId);
    }

    /**
     * 任务转办
     * @param requestMap
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    @ShenyuDubboClient("/transfer")
    @ApiDoc(desc = "transfer")
    public void transfer(Map<String, Object> requestMap) throws ClassNotFoundException {
        String taskId =(String) requestMap.get("taskId");
        String userId =(String) requestMap.get("userId");
        taskService.setAssignee(taskId, userId);
    }

    /**
     * 任务委派
     * @param requestMap
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    @ShenyuDubboClient("/delegateTask")
    @ApiDoc(desc = "delegateTask")
    public void delegateTask(Map<String, Object> requestMap) throws ClassNotFoundException {
        String taskId =(String) requestMap.get("taskId");
        String userId =(String) requestMap.get("userId");
        taskService.delegateTask(taskId,userId);
    }

    /**
     * 任务解决
     * @param requestMap
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    @ShenyuDubboClient("/resolve")
    @ApiDoc(desc = "resolve")
    public void resolve(Map<String, Object> requestMap) throws ClassNotFoundException {
        String taskId =(String) requestMap.get("taskId");
        Map<String, Object> variables =(Map<String, Object>) requestMap.get("variables");
        taskService.resolveTask(taskId, variables);
    }

    /**
     * 任务完成
     * @param requestMap
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    @ShenyuDubboClient("/complete")
    @ApiDoc(desc = "complete")
    public void complete(Map<String, Object> requestMap) throws ClassNotFoundException {
        String taskId =(String) requestMap.get("taskId");
        // 表单参数（id，value）
        Map<String, Object> variables =(Map<String, Object>) requestMap.get("variables");
        taskService.complete(taskId,variables);
    }

    /**
     * 附带表单数据完成任务
     * @param requestMap
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    @ShenyuDubboClient("/completeTaskWithForm")
    @ApiDoc(desc = "completeTaskWithForm")
    public void completeTaskWithForm(Map<String, Object> requestMap) throws ClassNotFoundException {
        String taskId =(String) requestMap.get("taskId");
        String formDefinitionId =(String) requestMap.get("formDefinitionId");
        String outcome =(String) requestMap.get("outcome");
        Map<String, Object> variables =(Map<String, Object>) requestMap.get("variables");
        taskService.completeTaskWithForm(taskId, formDefinitionId,outcome,  variables);
    }

    /**
     * 设置用户任务受理人
     * @param requestMap
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    @ShenyuDubboClient("/setAssignee")
    @ApiDoc(desc = "setAssignee")
    public void setAssignee(Map<String, Object> requestMap) throws ClassNotFoundException {
        String taskId =(String) requestMap.get("taskId");
        String assignee =(String) requestMap.get("Assignee");
        taskService.setAssignee(taskId,assignee);
    }

    /**
     * 获取流程的所有节点列表(通过流程定义id)
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getAllNode")
    @ApiDoc(desc = "getAllNode")
    public List<FlowElementsVo> getAllNode(Map<String, Object> requestMap){
            String processDefinitionId = (String) requestMap.get("processDefinitionId");
//        final BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
//         Process mainProcess = bpmnModel.getMainProcess();
        List<Process> processes = repositoryService.getBpmnModel(processDefinitionId).getProcesses();
        List<FlowElement> flowElementList = new ArrayList<>();
        for (Process process : processes) {
            Collection<FlowElement> flowElements = process.getFlowElements();
            flowElementList.addAll(flowElements);
        }
        // FlowElement 是大对象不能直接返回
        List<FlowElementsVo> flowElementsVoList = new ArrayList<>();
        for (FlowElement flowElement : flowElementList) {
            if (flowElement instanceof SequenceFlow) {
                continue;
            }
            FlowElementsVo flowElementsVo = new FlowElementsVo();
            flowElementsVo.setId(flowElement.getId());
            flowElementsVo.setName(flowElement.getName());
            flowElementsVo.setFlowableListenerList(flowElement.getExecutionListeners());
            flowElementsVo.setDocumentation(flowElement.getDocumentation());
            flowElementsVoList.add(flowElementsVo);

        }
        return flowElementsVoList;
    }

    /**
     * 获取当前节点信息
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getCurrentNodeInfo")
    @ApiDoc(desc = "getCurrentNodeInfo")
    public FlowElementsVo getCurrentNodeInfo(Map<String, Object> requestMap){
        String taskId = (String) requestMap.get("taskId");
        //当前任务信息
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //ProcessInstanceId流程实例
        String processlnstanceld = task.getProcessInstanceId();
        //获取流程定义的信息
        String definitionld = runtimeService.createProcessInstanceQuery().processInstanceId(processlnstanceld).singleResult().getProcessDefinitionId();        //获取bpm（模型）对象
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionld);
        //传节点定义key获取当前节点
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        System.out.println("当前节点：id = "+flowNode.getId()+" name:"+flowNode.getName());
        FlowElementsVo flowElementsVo = new FlowElementsVo();
        flowElementsVo.setId(flowNode.getId());
        flowElementsVo.setName(flowNode.getName());
        flowElementsVo.setFlowableListenerList(flowNode.getExecutionListeners());
        flowElementsVo.setDocumentation(flowNode.getDocumentation());
        return flowElementsVo;
    }

    /**
     * 获取下一节点信息
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getNextNodeInfo")
    @ApiDoc(desc = "getNextNodeInfo")
    public FlowElementsVo getNextNodeInfo(Map<String, Object> requestMap){
        String taskId = (String) requestMap.get("taskId");
        //当前任务信息
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 获取当前活动id
        ExecutionEntity executionEntity = (ExecutionEntity)runtimeService.createExecutionQuery()
                .executionId(task.getExecutionId()).singleResult();
        String crruentActivityId = executionEntity.getActivityId();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        // 当前审批节点
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(crruentActivityId);

        // 下一节点信息列表
        List<SequenceFlow> outFlows = flowNode.getOutgoingFlows();
        FlowElementsVo flowElementsVo = new FlowElementsVo();
        for (SequenceFlow sequenceFlow : outFlows) {
                // 下一个审批节点
                FlowElement targetFlow = sequenceFlow.getTargetFlowElement();
                if (targetFlow instanceof UserTask) {
                    flowElementsVo.setId(targetFlow.getId());
                    flowElementsVo.setName(targetFlow.getName());
                    flowElementsVo.setFlowableListenerList(targetFlow.getExecutionListeners());
                    flowElementsVo.setDocumentation(targetFlow.getDocumentation());
                    System.out.println("下一节点: id=" + targetFlow.getId() + ",name=" + targetFlow.getName());
                }
                // 如果下个审批节点为结束节点
                if (targetFlow instanceof EndEvent) {
                    flowElementsVo.setId(targetFlow.getId());
                    flowElementsVo.setName(targetFlow.getName());
                    flowElementsVo.setFlowableListenerList(targetFlow.getExecutionListeners());
                    flowElementsVo.setDocumentation(targetFlow.getDocumentation());
                    System.out.println("下一节点为结束节点：id=" + targetFlow.getId() + ",name=" + targetFlow.getName());
                }
            }

        return flowElementsVo;
    }


    /**
     * 获取上一节点信息
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getPreviousNodeInfo")
    @ApiDoc(desc = "getPreviousNodeInfo")
    public FlowElementsVo getPreviousNodeInfo(Map<String, Object> requestMap){
        String taskId = (String) requestMap.get("taskId");
        //当前任务信息
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 获取当前活动id
        ExecutionEntity executionEntity = (ExecutionEntity)runtimeService.createExecutionQuery()
                .executionId(task.getExecutionId()).singleResult();
        String crruentActivityId = executionEntity.getActivityId();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        // 当前审批节点
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(crruentActivityId);

        //上一节点信息列表
        List<SequenceFlow> incomingFlows = flowNode.getIncomingFlows();
        FlowElementsVo flowElementsVo = new FlowElementsVo();
        for (SequenceFlow sequenceFlow : incomingFlows) {
            // 上一个审批节点
            FlowElement sourceFlow = sequenceFlow.getSourceFlowElement();
            if (sourceFlow instanceof UserTask) {
                flowElementsVo.setId(sourceFlow.getId());
                flowElementsVo.setName(sourceFlow.getName());
                flowElementsVo.setFlowableListenerList(sourceFlow.getExecutionListeners());
                flowElementsVo.setDocumentation(sourceFlow.getDocumentation());
                System.out.println("上一节点: id=" + sourceFlow.getId() + ",name=" + sourceFlow.getName());
            }
            // 如果上个审批节点为开始节点
            if (sourceFlow instanceof StartEvent) {
                flowElementsVo.setId(sourceFlow.getId());
                flowElementsVo.setName(sourceFlow.getName());
                flowElementsVo.setFlowableListenerList(sourceFlow.getExecutionListeners());
                flowElementsVo.setDocumentation(sourceFlow.getDocumentation());
                System.out.println("上一节点为开始节点：id=" + sourceFlow.getId() + ",name=" + sourceFlow.getName());
            }
        }

        return flowElementsVo;
    }


    /**
     * 流程收回/驳回
     * taskId 当前任务ID
     * comment 审核意见
     */
    @Override
    @ShenyuDubboClient("/flowTackback")
    @ApiDoc(desc = "flowTackback")
    public void flowTackback(Map<String, Object> requestMap) {
        String taskId = (String) requestMap.get("taskId");
        String comment = (String) requestMap.get("comment");


        if (taskService.createTaskQuery().taskId(taskId).singleResult().isSuspended()) {
            throw new BusinessException("任务处于挂起状态");
        }
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        // 获取全部节点列表，包含子节点
        Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        // 获取当前任务节点元素
        FlowElement source = null;
        if (allElements != null) {
            for (FlowElement flowElement : allElements) {
                // 类型为用户节点
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    // 获取节点信息
                    source = flowElement;
                }
            }
        }


        // 目的获取所有跳转到的节点 targetIds
        // 获取当前节点的所有父级用户任务节点
        // 深度优先算法思想：延边迭代深入
        List<UserTask> parentUserTaskList = FlowableUtils.iteratorFindParentUserTasks(source, null, null);
        if (parentUserTaskList == null || parentUserTaskList.size() == 0) {
            throw new BusinessException("当前节点为初始任务节点，不能驳回");
        }
        // 获取活动 ID 即节点 Key
        List<String> parentUserTaskKeyList = new ArrayList<>();
        parentUserTaskList.forEach(item -> parentUserTaskKeyList.add(item.getId()));
        // 2021.03.03修改：之前使用的是历史任务表获取数据改为使用历史活动表获取数据，因为发现并行情况下，只有一个流程完成的情况下，会出现单独停留在网关的情况，因此扭转时网关也是需要进行扭转的点，网关数据历史活动表才能获取到
        // 后续相关部分同步修改，以及工具类中部分同步修改
        // 获取全部历史节点活动实例，即已经走过的节点历史，数据采用开始时间升序
        List<HistoricActivityInstance> historicActivityIdList = historyService.createHistoricActivityInstanceQuery().processInstanceId(task.getProcessInstanceId()).orderByHistoricActivityInstanceStartTime().asc().list();
        // 数据清洗，将回滚导致的脏数据清洗掉
        List<String> lastHistoricTaskInstanceList = FlowableUtils.historicTaskInstanceClean(allElements, historicActivityIdList);
        // 此时历史任务实例为倒序，获取最后走的节点
        List<String> targetIds = new ArrayList<>();
        // 循环结束标识，遇到当前目标节点的次数
        int number = 0;
        StringBuilder parentHistoricTaskKey = new StringBuilder();
        for (String historicTaskInstanceKey : lastHistoricTaskInstanceList) {
            // 当会签时候会出现特殊的，连续都是同一个节点历史数据的情况，这种时候跳过
            if (parentHistoricTaskKey.toString().equals(historicTaskInstanceKey)) {
                continue;
            }
            parentHistoricTaskKey = new StringBuilder(historicTaskInstanceKey);
            if (historicTaskInstanceKey.equals(task.getTaskDefinitionKey())) {
                number ++;
            }
            // 在数据清洗后，历史节点就是唯一一条从起始到当前节点的历史记录，理论上每个点只会出现一次
            // 在流程中如果出现循环，那么每次循环中间的点也只会出现一次，再出现就是下次循环
            // number == 1，第一次遇到当前节点
            // number == 2，第二次遇到，代表最后一次的循环范围
            if (number == 2) {
                break;
            }
            // 如果当前历史节点，属于父级的节点，说明最后一次经过了这个点，需要退回这个点
            if (parentUserTaskKeyList.contains(historicTaskInstanceKey)) {
                targetIds.add(historicTaskInstanceKey);
            }
        }



        // 目的获取所有需要被跳转的节点 currentIds
        // 取其中一个父级任务，因为后续要么存在公共网关，要么就是串行公共线路
        UserTask oneUserTask = parentUserTaskList.get(0);
        // 获取所有正常进行的执行任务的活动节点ID，这些任务不能直接使用，需要找出其中需要撤回的任务
        List<Execution> runExecutionList = runtimeService.createExecutionQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runActivityIdList = new ArrayList<>();
        runExecutionList.forEach(item -> {
            if (StringUtils.isNotBlank(item.getActivityId())) {
                runActivityIdList.add(item.getActivityId());
            }
        });
        // 需驳回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runExecutionList 比对，获取需要撤回的任务
        List<FlowElement> currentFlowElementList = FlowableUtils.iteratorFindChildUserTasks(oneUserTask, runActivityIdList, null, null);
        currentFlowElementList.forEach(item -> currentIds.add(item.getId()));



        // 规定：并行网关之前节点必须需存在唯一用户任务节点，如果出现多个任务节点，则并行网关节点默认为结束节点，原因为不考虑多对多情况
        if (targetIds.size() > 1 && currentIds.size() > 1) {
            throw new BusinessException("任务出现多对多情况，无法撤回");
        }



        // 2021.03.03修改：添加需撤回的节点为网关时，添加网关的删除信息
        AtomicReference<List<HistoricActivityInstance>> tmp = new AtomicReference<>();
        // 用于下面新增网关删除信息时使用
        String targetTmp = String.join(",", targetIds);
        // currentIds 为活动ID列表
        // currentExecutionIds 为执行任务ID列表
        // 需要通过执行任务ID来设置驳回信息，活动ID不行
        List<String> currentExecutionIds = new ArrayList<>();
        currentIds.forEach(currentId -> runExecutionList.forEach(runExecution -> {
            if (StringUtils.isNotBlank(runExecution.getActivityId()) && currentId.equals(runExecution.getActivityId())) {
                currentExecutionIds.add(runExecution.getId());
                // 查询当前节点的执行任务的历史数据
                tmp.set(historyService.createHistoricActivityInstanceQuery().processInstanceId(task.getProcessInstanceId()).executionId(runExecution.getId()).activityId(runExecution.getActivityId()).list());
                // 如果这个列表的数据只有 1 条数据
                // 网关肯定只有一条，且为包容网关或并行网关
                // 这里的操作目的是为了给网关在扭转前提前加上删除信息，结构与普通节点的删除信息一样，目的是为了知道这个网关也是有经过跳转的
                if (tmp.get() != null && tmp.get().size() == 1 && StringUtils.isNotBlank(tmp.get().get(0).getActivityType())
                        && ("parallelGateway".equals(tmp.get().get(0).getActivityType()) || "inclusiveGateway".equals(tmp.get().get(0).getActivityType()))) {
                    // singleResult 能够执行更新操作
                    // 利用 流程实例ID + 执行任务ID + 活动节点ID 来指定唯一数据，保证数据正确
                    historyService.createNativeHistoricActivityInstanceQuery().sql("UPDATE ACT_HI_ACTINST SET DELETE_REASON_ = 'Change activity to "+ targetTmp +"'  WHERE PROC_INST_ID_='"+ task.getProcessInstanceId() +"' AND EXECUTION_ID_='"+ runExecution.getId() +"' AND ACT_ID_='"+ runExecution.getActivityId() +"'").singleResult();
                }
            }
        }));
        // 设置驳回信息
        AtomicReference<Task> atomicCurrentTask = new AtomicReference<>();
        currentExecutionIds.forEach(item -> {
            atomicCurrentTask.set(taskService.createTaskQuery().executionId(currentExecutionIds.get(0)).singleResult());
            // 类型为网关时，获取用户任务为 null
            if (atomicCurrentTask.get() != null) {
                taskService.addComment(atomicCurrentTask.get().getId(), task.getProcessInstanceId(), "taskStatus", "reject");
                taskService.addComment(atomicCurrentTask.get().getId(), task.getProcessInstanceId(), "taskMessage", "已驳回");
                taskService.addComment(atomicCurrentTask.get().getId(), task.getProcessInstanceId(), "taskComment", comment);
            }
        });
        try {
            // 如果父级任务多于 1 个，说明当前节点不是并行节点，原因为不考虑多对多情况
            if (targetIds.size() > 1) {
                // 1 对 多任务跳转，currentIds 当前节点(1)，targetIds 跳转到的节点(多)
                runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId()).moveSingleActivityIdToActivityIds(currentIds.get(0), targetIds).changeState();
            }
            // 如果父级任务只有一个，因此当前任务可能为网关中的任务
            if (targetIds.size() == 1) {
                // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetIds.get(0) 跳转到的节点(1)
                runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId()).moveActivityIdsToSingleActivityId(currentIds, targetIds.get(0)).changeState();
            }
        } catch (FlowableObjectNotFoundException e) {
            throw new BusinessException("未找到流程实例，流程可能已发生变化");
        } catch (FlowableException e) {
            throw new BusinessException("无法取消或开始活动");
        }
    }


    /**
     * 流程回退
     * taskId 当前任务ID
     * targetKey 要回退的任务 Key
     */
    @Override
    @ShenyuDubboClient("/flowReturn")
    @ApiDoc(desc = "flowReturn")
    public void flowReturn(Map<String,Object> requestMap) {
        String taskId = (String) requestMap.get("taskId");
        String targetKey = (String) requestMap.get("targetKey");


        if (taskService.createTaskQuery().taskId(taskId).singleResult().isSuspended()) {
            throw new BusinessException("任务处于挂起状态");
        }
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息，暂不考虑子流程情况
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        // 获取全部节点列表，包含子节点
        Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        // 获取当前任务节点元素
        FlowElement source = null;
        // 获取跳转的节点元素
        FlowElement target = null;
        if (allElements != null) {
            for (FlowElement flowElement : allElements) {
                // 当前任务节点元素
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    source = flowElement;
                }
                // 跳转的节点元素
                if (flowElement.getId().equals(targetKey)) {
                    target = flowElement;
                }
            }
        }



        // 从当前节点向前扫描
        // 如果存在路线上不存在目标节点，说明目标节点是在网关上或非同一路线上，不可跳转
        // 否则目标节点相对于当前节点，属于串行
        Boolean isSequential = FlowableUtils.iteratorCheckSequentialReferTarget(source, targetKey, null, null);
        if (!isSequential) {
            throw new BusinessException("当前节点相对于目标节点，不属于串行关系，无法回退");
        }



        // 获取所有正常进行的执行任务节点的活动ID，这些任务不能直接使用，需要找出其中需要撤回的任务
        List<Execution> runExecutionList = runtimeService.createExecutionQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runActivityIdList = new ArrayList<>();
        runExecutionList.forEach(item -> {
            if (StringUtils.isNotBlank(item.getActivityId())) {
                runActivityIdList.add(item.getActivityId());
            }
        });
        // 需退回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runExecutionList 比对，获取需要撤回的任务
        List<FlowElement> currentFlowElementList = FlowableUtils.iteratorFindChildUserTasks(target, runActivityIdList, null, null);
        currentFlowElementList.forEach(item -> currentIds.add(item.getId()));


        // 修改：添加需撤回的节点为网关时，添加网关的删除信息
        AtomicReference<List<HistoricActivityInstance>> tmp = new AtomicReference<>();
        // currentIds 为活动ID列表
        // currentExecutionIds 为执行任务ID列表
        // 需要通过执行任务ID来设置驳回信息，活动ID不行
        List<String> currentExecutionIds = new ArrayList<>();
        currentIds.forEach(currentId -> runExecutionList.forEach(runExecution -> {
            if (StringUtils.isNotBlank(runExecution.getActivityId()) && currentId.equals(runExecution.getActivityId())) {
                currentExecutionIds.add(runExecution.getId());
                // 查询当前节点的执行任务的历史数据
                tmp.set(historyService.createHistoricActivityInstanceQuery().processInstanceId(task.getProcessInstanceId()).executionId(runExecution.getId()).activityId(runExecution.getActivityId()).list());
                // 如果这个列表的数据只有 1 条数据
                // 网关肯定只有一条，且为包容网关或并行网关
                // 这里的操作目的是为了给网关在扭转前提前加上删除信息，结构与普通节点的删除信息一样，目的是为了知道这个网关也是有经过跳转的
                if (tmp.get() != null && tmp.get().size() == 1 && StringUtils.isNotBlank(tmp.get().get(0).getActivityType())
                        && ("parallelGateway".equals(tmp.get().get(0).getActivityType()) || "inclusiveGateway".equals(tmp.get().get(0).getActivityType()))) {
                    // singleResult 能够执行更新操作
                    // 利用 流程实例ID + 执行任务ID + 活动节点ID 来指定唯一数据，保证数据正确
                    historyService.createNativeHistoricActivityInstanceQuery().sql("UPDATE ACT_HI_ACTINST SET DELETE_REASON_ = 'Change activity to "+ targetKey +"'  WHERE PROC_INST_ID_='"+ task.getProcessInstanceId() +"' AND EXECUTION_ID_='"+ runExecution.getId() +"' AND ACT_ID_='"+ runExecution.getActivityId() +"'").singleResult();
                }
            }
        }));
        // 设置驳回信息
        AtomicReference<Task> atomicCurrentTask = new AtomicReference<>();
        currentExecutionIds.forEach(item -> {
            atomicCurrentTask.set(taskService.createTaskQuery().executionId(currentExecutionIds.get(0)).singleResult());
            // 类型为网关时，获取用户任务为 null
            if (atomicCurrentTask.get() != null) {
                taskService.addComment(atomicCurrentTask.get().getId(), task.getProcessInstanceId(), "taskStatus", "return");
                taskService.addComment(atomicCurrentTask.get().getId(), task.getProcessInstanceId(), "taskMessage", "已退回");
                taskService.addComment(atomicCurrentTask.get().getId(), task.getProcessInstanceId(), "taskComment", "流程回退到" + atomicCurrentTask.get().getName() + "节点");
            }
        });

        try {
            // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetKey 跳转到的节点(1)
            runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId()).moveActivityIdsToSingleActivityId(currentIds, targetKey).changeState();
        } catch (FlowableObjectNotFoundException e) {
            throw new BusinessException("未找到流程实例，流程可能已发生变化");
        } catch (FlowableException e) {
            throw new BusinessException("无法取消或开始活动");
        }
    }

    /**
     * 获取所有可回退的节点
     * taskId
     */
    @Override
    @ShenyuDubboClient("/findReturnUserTask")
    @ApiDoc(desc = "findReturnUserTask")
    public List<UserTask> findReturnUserTask(Map<String,Object> requestMap ) {
        String taskId = (String)requestMap.get("taskId");

        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息，暂不考虑子流程情况
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        Collection<FlowElement> flowElements = process.getFlowElements();
        // 获取当前任务节点元素
        UserTask source = null;
        if (flowElements != null) {
            for (FlowElement flowElement : flowElements) {
                // 类型为用户节点
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    source = (UserTask) flowElement;
                }
            }
        }
        // 获取节点的所有路线
        List<List<UserTask>> roads = FlowableUtils.findRoad(source, null, null, null);
        // 可回退的节点列表
        List<UserTask> userTaskList = new ArrayList<>();
        for (List<UserTask> road : roads) {
            if (userTaskList.size() == 0) {
                // 还没有可回退节点直接添加
                userTaskList = road;
            } else {
                // 如果已有回退节点，则比对取交集部分
                userTaskList.retainAll(road);
            }
        }
        return userTaskList;
    }


}
