package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import me.flyray.bsin.validate.AddGroup;

/**
 * 
 * @TableName crm_member
 */

@Data
@TableName(value ="crm_member")
public class Member implements Serializable {

    /**
     * 会员编号
     */
    @TableId
    private String serialNo;

    /**
     *
     */
    @NotBlank(message = "客户号不能为空！", groups = {AddGroup.class})
    private String customerNo;

    /**
     * 租户号
     */
    @NotBlank(message = "租户号不能为空！", groups = {AddGroup.class})
    private String tenantId;
    /**
     * 商户号
     */
    @NotBlank(message = "商户号不能为空！", groups = {AddGroup.class})
    private String merchantNo;

    /**
     * 创建时间
     */
    private Date createTime;

    private String nickname;

    /** 状态： 0：禁用 1:正常 */
    private String status;

    /** 会员头像 */
    private String avatar;

    /**
     * 1 普通会员 2 锁客会员
     */
    private String type;

    /**
     *  店铺id
     */
    private String storeNo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}