package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 邀请关系表
 * @TableName crm_dis_invite_relation
 */
@Data
@TableName(value ="crm_dis_invite_relation")
public class DisInviteRelation implements Serializable {
    /**
     * 序列号
     */
    @TableId
    private String serialNo;

    /**
     * 租户号
     */
    private String tenantId;

    /**
     * 被邀请人序列号
     */
    private String customerNo;

    /**
     * 父级邀请人客户号
     */
    private String parentNo;

    /**
     * 所属代理商编号
     */
    private String sysAgentNo;

    /**
     * 邀请等级 1、2、3级
     */
    private Integer inviteLevel;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}