package me.flyray.bsin.domain.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author bolei
 * @ClassName ProcessDefinition
 * @DATE 2021/1/18 10:13
 */


@Data
public class ProcessDefinition implements Serializable {

    /**
     * 模型id
     */
    protected String id;

    /**
     * 模型名称
     */
    protected String name;

    /**
     * 模型描述
     */
    protected String description;

    /**
     * 模型key
     */
    protected String key;

    /**
     * 模型部署类别
     */
    protected String category;

    /**
     * 版本
     */
    protected int version;

    /**
     *
     */
    protected String deploymentId;

    /**
     * 租户id
     */
    protected String tenantId;

    /**
     *
     */
    protected boolean hasStartForm;

    /**
     * 挂起状态
     */
    protected boolean isSuspended;


}
