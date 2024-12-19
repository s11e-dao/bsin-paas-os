package me.flyray.bsin.domain.entity;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.flyray.bsin.entity.BaseEntity;

/**
 * 会员配置：会员模型(CrmMemberConfig)实体类
 *
 * @author zth
 * @since 2024-12-18 13:31:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@TableName("crm_member_config")
public class MemberConfig extends BaseEntity {

    /**
     * 会员模型id
     */
    @TableId(type = IdType.ASSIGN_ID)
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

    private Integer delFlag;
}

