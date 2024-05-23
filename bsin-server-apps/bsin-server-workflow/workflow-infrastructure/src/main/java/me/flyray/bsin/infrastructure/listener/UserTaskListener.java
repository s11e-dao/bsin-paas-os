package me.flyray.bsin.infrastructure.listener;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

public class UserTaskListener implements TaskListener {

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    /**
     * 用户任务监听器
     *
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("用户监听器执行成功");
//        // 获取当前任务id
//        String taskId = delegateTask.getId();
//        Map<String, Object> variables = taskService.getVariables(taskId);
//        // 获取办理人
//        Map<String, Object> map = ( Map<String, Object>)BsinInvoke.genericInvoke("NurseTaskListener", "setAssignee", variables);
//        if(map.size()==1){
//            for (String key: map.keySet()) {
//                // 从流程变量中获取 nurseId
//                String value = (String) variables.get(key);
//                // 设置办理人
//                taskService.setAssignee(taskId,value);
//            }
//        }else {
//            for (String key: map.keySet()) {
//                // 从流程变量中获取 nurseId
//                String value = (String)variables.get(key);
//                // 设置候选人（可以是多人）
//                taskService.addCandidateUser(taskId,value);
//            }
//        }
    }
}

