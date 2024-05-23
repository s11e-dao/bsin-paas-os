package me.flyray.bsin.facade.service;


import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.List;
import java.util.Map;


public interface BsinAdminHistoryProcessInstService {

    /**
     * 查询历史流程实例
     */

    List<HistoricProcessInstance> getHistoryProcessInst(Map<String, Object> requestMap);

    /**
     * 查询历史任务信息
     */
    List<HistoricActivityInstance> getHistoryActInst(Map<String, Object> requestMap);

    /**
     * 查询历史任务信息
     */
    List<HistoricTaskInstance> getHistoryTaskInst(Map<String, Object> requestMap);


    /**
     * 查询历史任务信息
     */
    void getApprovalRecord(Map<String, Object> requestMap);
}
