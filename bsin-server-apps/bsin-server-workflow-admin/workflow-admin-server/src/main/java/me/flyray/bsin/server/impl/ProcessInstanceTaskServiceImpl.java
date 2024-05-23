package me.flyray.bsin.server.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.Task;
import me.flyray.bsin.facade.service.BsinAdminProcessInstanceTaskService;
import me.flyray.bsin.infrastructure.mapper.TaskMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.flowable.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 流程任务
 */
@Slf4j
@DubboService
@ApiModule(value = "processInstanceTask")
@ShenyuDubboService("/processInstanceTask")
public class ProcessInstanceTaskServiceImpl implements BsinAdminProcessInstanceTaskService {

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskService taskService;
    /**
     * 查询流程实例任务
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getProcessInstanceTasks")
    @ApiDoc(desc = "getProcessInstanceTasks")
    public PageInfo<Task> getProcessInstanceTasks(Map<String, Object> requestMap) {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        String processInstanceId = (String)requestMap.get("processInstanceId");
        String processDefinitionName = (String)requestMap.get("processDefinitionName");
        String owner = (String)requestMap.get("owner");
        String assignee = (String)requestMap.get("assignee");
        String name = (String)requestMap.get("name");
        Map<String, Object> map=new HashMap<>();
        map.put("processInstanceId",processInstanceId);
        map.put("processDefinitionName",processDefinitionName);
        map.put("owner",owner);
        map.put("assignee",assignee);
        map.put("name",name);
        List<Task> pageList = taskMapper.selectTasks(map);
        PageInfo<Task> pageInfo = new PageInfo<>(pageList);
        return pageInfo;
    }

    /**
     * 查询历史实例任务
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getHistoricInstanceTasks")
    @ApiDoc(desc = "getHistoricInstanceTasks")
    public PageInfo<Task> getHistoricInstanceTasks(Map<String, Object> requestMap) {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        String processInstanceId = (String)requestMap.get("processInstanceId");
        String processDefinitionName = (String)requestMap.get("processDefinitionName");
        String owner = (String)requestMap.get("owner");
        String assignee = (String)requestMap.get("assignee");
        String name = (String)requestMap.get("name");
        Map<String, Object> map=new HashMap<>();
        map.put("processInstanceId",processInstanceId);
        map.put("processDefinitionName",processDefinitionName);
        map.put("owner",owner);
        map.put("assignee",assignee);
        map.put("name",name);
        Long pageNum = (Long) pagination.get("pageNum");
        Long pageSize = (Long) pagination.get("pageSize");
        PageHelper.startPage(Math.toIntExact(pageNum), Math.toIntExact(pageSize));
        List<Task> pageList = taskMapper.selectHistoricTasks(map);
        PageInfo<Task> pageInfo = new PageInfo<>(pageList);
        return pageInfo;
    }

   //  __________________________________________________________________________________________________

    /**
     * 查询所有未暂停的任务
     */
    @Override
    @ShenyuDubboClient("/getAllTask")
    @ApiDoc(desc = "getAllTask")
    public PageInfo<org.flowable.task.api.Task> getAllTask(Map<String, Object> requestMap)  {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        String tenantId =(String) requestMap.get("tenantId");
        List<org.flowable.task.api.Task> taskList = taskService.createTaskQuery()
                .active()
                .taskTenantId(tenantId)
                .orderByTaskCreateTime()
                .desc()
                .list();
        List<org.flowable.task.api.Task> partList = getPartList(taskList, (Integer) pagination.get("pageNum"), (Integer) pagination.get("pageSize"));
        PageInfo<org.flowable.task.api.Task> pageInfo = new PageInfo<>(partList);
        pageInfo.setTotal(taskList.size());
        return pageInfo;
    }

    public static <T> List<T> getPartList(List<T> inLs,int page,int limit){
        page = (page <= 0) ? 1 : page;
        limit= (limit <= 0) ? 10 : limit;
        int total = inLs.size();
        return inLs.subList(Math.min((page - 1) * limit, total), Math.min(page * limit, total));
    }

    /**
     * 根据用户名获取待办任务
     */
    @Override
    @ShenyuDubboClient("/getTaskByUser")
    @ApiDoc(desc = "getTaskByUser")
    public PageInfo<Task> getTasksByUser(Map<String, Object> requestMap) {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        String tenantId =(String) requestMap.get("tenantId");
        String owner =(String) requestMap.get("owner");
        String assignee =(String) requestMap.get("userId");
        Long pageNum = (Long) pagination.get("pageNum");
        Long pageSize = (Long) pagination.get("pageSize");
        PageHelper.startPage(Math.toIntExact(pageNum), Math.toIntExact(pageSize));
        List<Task> pageList = taskMapper.selectTasksByUser(tenantId,assignee,owner);
        PageInfo<Task> pageInfo = new PageInfo<>(pageList);
        return pageInfo;
    }

    /**
     * 根据用户名获取待办任务
     */
    @Override
    @ShenyuDubboClient("/getTasksByCustomerNo")
    @ApiDoc(desc = "getTasksByCustomerNo")
    public PageInfo<Task> getTasksByCustomerNo(Map<String, Object> requestMap) {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        String tenantId =(String) requestMap.get("tenantId");
        String owner =(String) requestMap.get("owner");
        String assignee =(String) requestMap.get("customerNo");
        Long pageNum = (Long) pagination.get("pageNum");
        Long pageSize = (Long) pagination.get("pageSize");
        PageHelper.startPage(Math.toIntExact(pageNum), Math.toIntExact(pageSize));
        List<Task> pageList = taskMapper.selectTasksByUser(tenantId,assignee,owner);
        PageInfo<Task> pageInfo = new PageInfo<>(pageList);
        return pageInfo;
    }

    /**
     * 根据用户名获取候选任务
     */
    @Override
    @ShenyuDubboClient("/getCandidateTaskByUser")
    @ApiDoc(desc = "getCandidateTaskByUser")
    public List<org.flowable.task.api.Task> getCandidateTasksByUser(Map<String, Object> requestMap) {
        String tenantId =(String) requestMap.get("tenantId");
        String username =(String) requestMap.get("username");
        String processInstanceId =(String) requestMap.get("processInstanceId");
        List<org.flowable.task.api.Task> taskList = taskService.createTaskQuery()
                //设置taskCandidateUser候选人条件来查询任务，这里的参数值就是候选人流程变量绑定的值
                //员工1和员工2都是可以查询到数据的
//                .taskCandidateUser(username)
                .taskTenantId(tenantId)
                .list();
        return taskList;
    }

    /**
     * 领取候选任务
     */
    @Override
    @ShenyuDubboClient("/claimTaskCandidate")
    @ApiDoc(desc = "claimTaskCandidate")
    public void claimCandidateTask(Map<String, Object> requestMap) {
        String userId =(String) requestMap.get("userId");
        String taskId =(String) requestMap.get("taskId");
        taskService.claim(taskId, userId);
    }

    /**
     * 任务归还（如果任务拾取之后不想操作或者误拾取任务也可以进行归还任务）
     */
    @Override
    @ShenyuDubboClient("/unClaimCandidateTask")
    @ApiDoc(desc = "unClaimCandidateTask")
    public void unClaimCandidateTask(Map<String, Object> requestMap) {
        String taskId =(String) requestMap.get("taskId");
        taskService.unclaim(taskId);
    }

    /**
     * 任务转办
     */
    @Override
    @ShenyuDubboClient("/transfer")
    @ApiDoc(desc = "transfer")
    public void transfer(Map<String, Object> requestMap) throws ClassNotFoundException {
        String taskId =(String) requestMap.get("taskId");
        String assignee =(String) requestMap.get("assignee");
        taskService.setAssignee(taskId, assignee);
    }

    /**
     * 任务委派
     */
    @Override
    @ShenyuDubboClient("/delegateTask")
    @ApiDoc(desc = "delegateTask")
    public void delegateTask(Map<String, Object> requestMap) throws ClassNotFoundException {
        String taskId =(String) requestMap.get("taskId");
        String assignee =(String) requestMap.get("assignee");
        taskService.delegateTask(taskId,assignee);
    }

    /**
     * 任务解决
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

}
