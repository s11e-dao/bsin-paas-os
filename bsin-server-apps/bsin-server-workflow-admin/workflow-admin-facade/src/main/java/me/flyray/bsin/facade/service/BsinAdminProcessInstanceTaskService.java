package me.flyray.bsin.facade.service;

import com.github.pagehelper.PageInfo;
import me.flyray.bsin.domain.entity.Task;

import java.util.List;
import java.util.Map;


public interface BsinAdminProcessInstanceTaskService {

    /**
     * 查询流程实例任务
     * @param requestMap
     * @return
     */
    PageInfo<Task> getProcessInstanceTasks(Map<String, Object> requestMap);

    /**
     * 查询实例任务历史
     * @param requestMap
     * @return
     */
    PageInfo<Task> getHistoricInstanceTasks(Map<String, Object> requestMap);


    /**
     * 查询所有任务
     */
    PageInfo<org.flowable.task.api.Task> getAllTask(Map<String, Object> requestMap);

    /**
     * 根据组织架构系统用户ID
     */
    public PageInfo<Task> getTasksByUser(Map<String, Object> requestMap);

    /**
     * 根据业务系统的客户编号查询待办
     */
    public PageInfo<Task> getTasksByCustomerNo(Map<String, Object> requestMap);

    /**
     * 根据用户名获取候选任务
     */
    public List<org.flowable.task.api.Task> getCandidateTasksByUser(Map<String, Object> requestMap);

    /**
     * 领取候选任务
     */
    public void claimCandidateTask(Map<String, Object> requestMap);

    /**
     * 任务归还（如果任务拾取之后不想操作或者误拾取任务也可以进行归还任务）
     */
    public void unClaimCandidateTask(Map<String, Object> requestMap);

    /**
     * 任务转办
     */
    public void transfer(Map<String, Object> requestMap) throws ClassNotFoundException;

    /**
     * 任务委派
     */
    public void delegateTask(Map<String, Object> requestMap) throws ClassNotFoundException;

    /**
     * 任务解决
     */
    public void resolve(Map<String, Object> requestMap) throws ClassNotFoundException;

    /**
     * 任务完成
     */
    public void complete(Map<String, Object> requestMap) throws ClassNotFoundException;

}
