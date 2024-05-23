package me.flyray.bsin.domain.response;

import lombok.Data;

import java.util.Date;

/**
 * @author huangzh
 * @ClassName ModelInfo
 * @DATE 2020/10/23 15:14
 */


@Data
public class ModelInfo {

    /**
     *  模型id
     */
    protected String id;

    /**
     *  模型姓名
     */
    protected String name;

    /**
     *  模型key
     */
    protected String key;

    /**
     *  模型描述
     */
    protected String description;

    /**
     * 模型评论
     */
    protected String comment;

    /**
     *  最后更新日期
     */
    protected Date lastUpdated;

    /**
     *  新建日期
     */
    protected Date created;

    /**
     *  创建人
     */
    private String createdBy;

    /**
     *  最后更新人
     */
    private String lastUpdatedBy;

    /**
     *  版本
     */
    protected int version;

    /**
     *  模型类型
     */
    protected String type;

    /**
     *  租户id
     */
    protected String tenantId;

}
