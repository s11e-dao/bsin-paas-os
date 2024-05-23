package me.flyray.bsin.domain.entity;


import lombok.Data;

import java.util.Date;

@Data
public class BsinActivityInstance  {
    private static final long serialVersionUID = 1L;
    protected String processInstanceId;
    protected String processDefinitionId;
    protected Date startTime;
    protected Date endTime;
    protected Long durationInMillis;
    protected Integer transactionOrder;
    protected String deleteReason;
    protected String activityId;
    protected String activityName;
    protected String activityType;
    protected String executionId;
    protected String assignee;
    protected String taskId;
    protected String calledProcessInstanceId;
    protected String tenantId ;
    protected String approvalOpinion;
}
