package me.flyray.bsin.domain.entity;

import lombok.Data;
import org.flowable.idm.api.User;
import org.flowable.ui.common.model.AbstractRepresentation;
import org.flowable.ui.common.model.UserRepresentation;
import org.flowable.engine.repository.ProcessDefinition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author huangzh
 * @ClassName ProcessInstanceInfo
 * @DATE 2021/1/18 11:27
 */

@Data
public class ProcessInstance extends AbstractRepresentation {
    protected String id;
    protected String name;
    protected String businessKey;
    protected String processDefinitionId;
    protected String tenantId;
    protected Date started;
    protected Date ended;
    protected UserRepresentation startedBy;
    protected String processDefinitionName;
    protected String processDefinitionDescription;
    protected String processDefinitionKey;
    protected String processDefinitionCategory;
    protected int processDefinitionVersion;
    protected String processDefinitionDeploymentId;
    protected boolean graphicalNotationDefined;
    protected boolean startFormDefined;
    protected boolean isSuspended;
    protected List<RestVariable> variables;

    public ProcessInstance(org.flowable.engine.runtime.ProcessInstance processInstance, ProcessDefinition processDefinition, boolean graphicalNotation, User startedBy) {
        this(processInstance, graphicalNotation, startedBy);
        this.mapProcessDefinition(processDefinition);
    }

    public ProcessInstance(org.flowable.engine.runtime.ProcessInstance processInstance, boolean graphicalNotation, User startedBy) {
        this.variables = new ArrayList();
        this.id = processInstance.getId();
        this.name = processInstance.getName();
        this.businessKey = processInstance.getBusinessKey();
        this.processDefinitionId = processInstance.getProcessDefinitionId();
        this.tenantId = processInstance.getTenantId();
        this.isSuspended = processInstance.isSuspended();
        this.graphicalNotationDefined = graphicalNotation;
        this.startedBy = startedBy != null ? new UserRepresentation(startedBy) : null;
    }

    protected void mapProcessDefinition(ProcessDefinition processDefinition) {
        if (processDefinition != null) {
            this.processDefinitionName = processDefinition.getName();
            this.processDefinitionDescription = processDefinition.getDescription();
            this.processDefinitionKey = processDefinition.getKey();
            this.processDefinitionCategory = processDefinition.getCategory();
            this.processDefinitionVersion = processDefinition.getVersion();
            this.processDefinitionDeploymentId = processDefinition.getDeploymentId();
        }

    }


}
