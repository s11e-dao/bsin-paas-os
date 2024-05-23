package me.flyray.bsin.domain.response;

import lombok.Data;

import java.util.Date;

/**
 * @author huangzh
 * @ClassName ModelInfoRepresentation
 * @DATE 2020/11/2 10:55
 */

@Data
public class ModelInfoRepresentation {

    /**
     * 模型id
     */
    protected String id;

    /**
     * 模型name
     */
    protected String name;

    /**
     * 模型key
     */
    protected String key;

    /**
     * 模型描述
     */
    protected String description;

    /**
     * 创建人
     */
    protected String createdBy;

    /**
     * 更新人
     */
    protected String lastUpdatedBy;

    /**
     * 更新日期
     */
    protected Date lastUpdated;

    /**
     * 最新标志位
     */
    protected boolean latestVersion;

    /**
     * 版本号
     */
    protected int version;

    /**
     * 模型类型
     */
    protected String type;

    /**
     * 租户id
     */
    protected String tenantId;



    public ModelInfoRepresentation(AbstractModel model) {
        this.initialize(model);
    }

    public void initialize(AbstractModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.key = model.getKey();
        this.description = model.getDescription();
        this.createdBy = model.getCreatedBy();
        this.lastUpdated = model.getLastUpdated();
        this.version = model.getVersion();
        this.lastUpdatedBy = model.getLastUpdatedBy();
        //type值对应模型的comment字段
        this.type = model.getComment();
        this.tenantId = model.getTenantId();
        if (model instanceof Model) {
            this.setLatestVersion(true);
        } else if (model instanceof ModelHistory) {
            this.setLatestVersion(false);
        }

    }
}
