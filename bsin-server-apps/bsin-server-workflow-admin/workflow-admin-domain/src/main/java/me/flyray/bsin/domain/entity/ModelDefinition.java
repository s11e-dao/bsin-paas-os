package me.flyray.bsin.domain.entity;

import lombok.Data;
import org.flowable.ui.common.model.AbstractRepresentation;
import org.flowable.engine.repository.ProcessDefinition;

/**
 * @author bolei
 * @ClassName ModelDefinitionInfo
 * @DATE 2021/1/18 10:13
 */


@Data
public class ModelDefinition extends AbstractRepresentation {

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

    public ModelDefinition(ProcessDefinition processDefinition) {
        this.id = processDefinition.getId();
        this.name = processDefinition.getName();
        this.description = processDefinition.getDescription();
        this.key = processDefinition.getKey();
        this.category = processDefinition.getCategory();
        this.version = processDefinition.getVersion();
        this.deploymentId = processDefinition.getDeploymentId();
        this.tenantId = processDefinition.getTenantId();
//        this.hasStartForm = processDefinition.hasStartFormKey();
        this.isSuspended = processDefinition.isSuspended();

    }


}
