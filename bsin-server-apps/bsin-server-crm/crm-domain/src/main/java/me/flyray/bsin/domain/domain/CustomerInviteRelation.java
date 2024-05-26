package me.flyray.bsin.domain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import lombok.Data;

@Data
@TableName("crm_customer_invite_relation")
public class CustomerInviteRelation {
    /**
     * 序列号
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String serialNo;

    /**
     * 被邀请的客户号
     */
    private String customerNo;

    /**
     * 邀请人
     */
    private String parentNo;

    /**
     * 邀请等级
     */
    private int inviteLevel;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 用户名称
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 用户头像
     */
    @TableField(exist = false)
    private String headImage;

}
