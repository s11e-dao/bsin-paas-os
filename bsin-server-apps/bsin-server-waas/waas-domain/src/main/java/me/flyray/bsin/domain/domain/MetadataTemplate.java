package me.flyray.bsin.domain.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 元数据模板
 * @TableName da_metadata_template
 */

@Data
@TableName(value ="da_metadata_template")
public class MetadataTemplate implements Serializable {
    /**
     * 模板编号
     */
    @TableId
    private String serialNo;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 模板数据
     */
    private String templateContent;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板编号
     */
    private String templateCode;


    /**
     * 模板描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 逻辑删除 0、未删除 1、已删除
     */
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private Integer delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}