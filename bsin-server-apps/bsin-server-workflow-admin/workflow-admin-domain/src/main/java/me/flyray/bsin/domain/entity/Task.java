package me.flyray.bsin.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Task {
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 创建时间
     */

    private Date createTime;

    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 任务id
     */
    private String id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 流程定义id
     */
    private String processDefinitionId;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 任务分配者
     */
    private String owner;

    /**
     * 任务参与者
     */
    private String assignee;

    /**
     * 模型定义名称
     */
    private String processDefinitionName;

    /**
     * 模型优先级
     */
    private int priority;

    /**
     * 是否委派
     */
    private String delegation;

}
