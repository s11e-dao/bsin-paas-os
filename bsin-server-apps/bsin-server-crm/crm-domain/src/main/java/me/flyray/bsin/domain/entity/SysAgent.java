package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.flyray.bsin.domain.enums.CustomerType;
import me.flyray.bsin.domain.enums.BizRoleStatus;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统合伙人
 * @TableName crm_sys_agent
 */

@Data
@TableName(value ="crm_sys_agent")
public class SysAgent implements Serializable {

    /**
     * 
     */
    @TableId
    private String serialNo;

    private String parentAgentNo;

    /**
     * 
     */
    private String tenantId;

    /**
     * 合伙人名称
     */
    private String agentName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 企业logo
     */
    private String logoUrl;

    /**
     * 企业工商号
     */
    private String businessNo;

    /**
     * 法人姓名
     */
    private String legalPersonName;

    /**
     * 法人证件类型
     */
    private String legalPersonCredType;

    /**
     * 法人证件号
     */
    private String legalPersonCredNo;

    /**
     * 营业执照图片
     */
    private String businessLicenceImg;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 公司网址
     */
    private String netAddress;

    /**
     * 企业地址
     */
    private String agentAddress;

    /**
     * 状态：0 正常 1 冻结 2 待审核
     * @see BizRoleStatus
     */
    private String status;


    /**
     * 客户|租户|商户|合伙人类型 0、个人客户 1、企业客户
     * @see CustomerType
     */
    private String type;

    /**
     * 合伙人类别：1、平台代理 2、分销代理
     */
    private String category;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 认证状态   1: 待认证  2：认证成功  3：认证失败
     */
    private String authenticationStatus;

    /**
     * 业态
     */
    private String businessType;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 介绍
     */
    private String description;

    /**
     * 删除标识
     */
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private String delFlag;

    private String password;

    private String username;

    /**
     * 等级序列号（关联crm_grade）
     */
    private String gradeNo;

    /**
     * 区域编码
     */
    private String regionCode;

    /**
     * 合伙人级别：PROVINCE-省代理 CITY-市代理 COUNTY-县代理 DISTRICT-区代理 TOWN-镇代理 NONE-无
     */
    private String agentLevel;

    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}