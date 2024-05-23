package me.flyray.bsin.server.impl;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.service.BsinAdminHistoryProcessInstService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 *  历史流程实例
 */

@Slf4j
@DubboService
@ApiModule(value = "historyProcessInst")
@ShenyuDubboService("/historyProcessInst")
public class HistoryProcessInstServiceImpl implements BsinAdminHistoryProcessInstService {

    @Autowired
    private HistoryService historyService;

    /**
     * 查询全部历史流程实例
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getHistoryProcessInst")
    @ApiDoc(desc = "getHistoryProcessInst")
    public List<HistoricProcessInstance> getHistoryProcessInst(Map<String, Object> requestMap) {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        //当前页
        Integer pageNum = (Integer) pagination.get("pageNum");
        //页大小
        Integer pageSize = (Integer) pagination.get("pageSize");
         List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery()
                .orderByProcessInstanceStartTime().desc()
                .listPage((pageNum - 1) * pageSize, pageSize);
        return list;
    }

    /**
     * 查询历史活动信息
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getHistoryActInst")
    @ApiDoc(desc = "getHistoryActInst")
    public List<HistoricActivityInstance> getHistoryActInst(Map<String, Object> requestMap) {
        String processInstanceId = (String) requestMap.get("processInstanceId");
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
        return list;
    }

    /**
     * 查询历史任务信息
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getHistoryTaskInst")
    @ApiDoc(desc = "getHistoryTaskInst")
    public List<HistoricTaskInstance> getHistoryTaskInst(Map<String, Object> requestMap) {
        String processInstanceId = (String) requestMap.get("processInstanceId");
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime().asc()
                .list();

        return list;
    }

    /**
     * 查询审批记录
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getApprovalRecord")
    @ApiDoc(desc = "getApprovalRecord")
    public void getApprovalRecord(Map<String, Object> requestMap) {
        String processInstanceId = (String) requestMap.get("processInstanceId");
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime().asc()
                .list();
        for (HistoricTaskInstance hti : list) {
            System.out.println("流程实例ID：" + hti.getId());
            System.out.println("流程定义ID：" + hti.getProcessDefinitionId());
            System.out.println("流程执行实例ID：" + hti.getExecutionId());
            System.out.println("流程结束时间：" + DateUtil.format(hti.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
            System.out.println("流程的处理人是：" + hti.getAssignee());
            System.out.println("################################");
        }
    }

}
