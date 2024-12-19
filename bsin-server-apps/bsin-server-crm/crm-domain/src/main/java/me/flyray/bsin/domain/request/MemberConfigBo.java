package me.flyray.bsin.domain.request;


import lombok.Data;
import lombok.EqualsAndHashCode;
import me.flyray.bsin.entity.BaseEntity;
import me.flyray.bsin.validate.AddGroup;
import me.flyray.bsin.validate.EditGroup;

import javax.validation.constraints.NotBlank;
import java.util.Date;


/**
 * 会员配置：会员模型(CrmMemberConfig)-Bo
 *
 * @author zth
 * @since 2024-12-18 13:31:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberConfigBo extends BaseEntity {


    /**
     * 会员模型id
     */
    @NotBlank(message = "会员模型id不能为空", groups = {EditGroup.class})
    private String serialNo;

    /**
     * 1平台会员 2商户会员 3店铺会员
     */
    private Integer model;

    private String createBy;

    private Date createTime;

    private Date updateTime;

    private String updateBy;

    private String tenantId;

    private String merchantId;

    private String delFlag;

}



