package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 分销团队关系表
 * @TableName crm_dis_team_relation
 */
@Data
@TableName(value ="crm_dis_team_relation")
public class DisTeamRelation implements Serializable {
    /**
     * 
     */
    @TableId
    private String serialNo;

    /**
     * 上级分销员ID
     */
    private String tenantId;

    /**
     * 上级分销员ID
     */
    private String prarentSysAgentNo;

    /**
     * 分销员ID(代理商ID)
     */
    private String sysAgentNo;


    private Integer agentType;




    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}