package me.flyray.bsin.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ModelType implements Serializable {

    /**
     * 模型类型id
     */
    private String id;

    /**
     * 模型类型id
     */
    private String tenantId;

    /**
     * 模型类型code
     */
    private String typeCode;

    /**
     * 模型类型name
     */
    private String typeName;

    /**
     * 父类模型id
     */
    private String parentId;

    /**
     * 描述
     */
    private String description;

    /**
     * 删除标志
     */
    private String delFlag;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;

}
