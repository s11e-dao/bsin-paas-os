package me.flyray.bsin.domain.entity;

import org.flowable.ui.modeler.domain.AbstractModel;

import java.util.Date;

public class BsinFormRepresentation {
    protected String id;
    protected String name;
    protected String key;
    protected String description;
    protected Integer version;
    protected String lastUpdatedBy;
    protected Date lastUpdated;
    protected String formDefinition;

    public BsinFormRepresentation(AbstractModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.key = model.getKey();
        this.description = model.getDescription();
        this.version = model.getVersion();
        this.lastUpdated = model.getLastUpdated();
        this.lastUpdatedBy = model.getLastUpdatedBy();
    }

    public BsinFormRepresentation() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getFormDefinition() {
        return this.formDefinition;
    }

    public void setFormDefinition(String formDefinition) {
        this.formDefinition = formDefinition;
    }
}
