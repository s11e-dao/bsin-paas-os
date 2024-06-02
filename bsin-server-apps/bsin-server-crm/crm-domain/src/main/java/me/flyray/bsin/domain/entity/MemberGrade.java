package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 客户等级
 * @TableName crm_member_grade
 */
@TableName(value ="crm_member_grade")
@Data
public class MemberGrade implements Serializable {
    /**
     * 序列号
     */
    @TableId
    private String serialNo;

    /**
     * 用户id
     */
    private String customerNo;

    /**
     * 等级编号
     */
    private String gradeNo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}