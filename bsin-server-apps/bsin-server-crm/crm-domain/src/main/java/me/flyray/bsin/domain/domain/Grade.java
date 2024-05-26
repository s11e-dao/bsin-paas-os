package me.flyray.bsin.domain.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 客户等级划分配置
 * @TableName crm_grade
 */

@Data
@TableName(value ="crm_grade")
public class Grade implements Serializable {
    /**
     * 序列号
     */
    @TableId
    private String serialNo;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 逻辑删除 0、未删除 1、已删除
     */
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private Integer delFlag;

    /**
     * 租户
     */
    private String tenantId;

    /**
     * 商户编号
     */
    private String merchantNo;

    /**
     * 等级名称
     */
    private String name;

    /**
     * 等级描述
     */
    private String description;

    /**
     * 等级级数
     */
    private String gradeNum;

    /**
     * 等级图标
     */
    private String gradeImage;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}