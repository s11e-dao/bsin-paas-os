package me.flyray.bsin.facade.service;


import me.flyray.bsin.domain.entity.FlowElementsVo;
import org.flowable.bpmn.model.UserTask;
import org.flowable.task.api.Task;

import java.util.List;
import java.util.Map;


public interface BsinTaskService {

    /**
     * 根据用户名获取待办任务
     */
    public List<Task> getTaskByUser(Map<String, Object> requestMap) throws ClassNotFoundException;

    /**
     * 根据用户名获取候选任务
     */
    public List<Task> getCandidateTaskByUser(Map<String, Object> requestMap);

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


    /**
     * 附带表单数据完成任务
     */
    public void completeTaskWithForm(Map<String, Object> requestMap) throws ClassNotFoundException;

    /**
     * 设置用户任务受理人
     */
    public void  setAssignee(Map<String, Object> requestMap) throws ClassNotFoundException;

    /**
     * 获取所有流程节点列表
     */
    List<FlowElementsVo> getAllNode(Map<String, Object> requestMap);

    /**
     * 获取当前节点信息
     */
    FlowElementsVo getCurrentNodeInfo(Map<String, Object> requestMap);

    /**
     * 获取下一节点信息
     */
    FlowElementsVo getNextNodeInfo(Map<String, Object> requestMap);

    /**
     * 获取上一节点信息
     */
    FlowElementsVo getPreviousNodeInfo(Map<String, Object> requestMap);

    /**
     * 流程收回/驳回
     */
    void flowTackback(Map<String, Object> requestMap);

    /**
     * 流程回退
     */
    void flowReturn(Map<String,Object> requestMap);

    /**
     * 获取所有可回退的节点
     */
    List<UserTask> findReturnUserTask(Map<String,Object> requestMap );
}
