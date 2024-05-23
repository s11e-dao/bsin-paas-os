package me.flyray.bsin.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskVo {
    //任务id
    public String taskId;

    public String assignee;

    public String processDefinitionId;

    public String tenantId;

    // 父任务id
    public String parentTaskId;

    // 任务名称
    public String name;

    // 流程实例id
    public String processInstanceId;

    // 医嘱
    public String medicalAdvice;




}
