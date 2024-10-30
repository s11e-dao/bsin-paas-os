package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import me.flyray.bsin.domain.enums.AuthenticationStatus;
import me.flyray.bsin.domain.enums.CustomerType;
import me.flyray.bsin.domain.enums.MerchantStatus;

/**
 * 
 * @TableName crm_merchant
 */

@Data
@TableName(value ="crm_merchant")
public class Merchant implements Serializable {

    /**
     * 
     */
    @TableId
    private String serialNo;

    /**
     * 
     */
    private String tenantId;

    /**
     * 企业名称
     */
    private String merchantName;

    /**
     * 登录密码
     */
    private String password;

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
    private String merchantAddress;

    /**
     * 状态：0 正常 1 冻结 2 待审核
     * @see MerchantStatus
     */
    private String status;


    /**
     * 客户|租户|商户|代理商类型 0、个人客户 1、企业客户
     * @see CustomerType
     */
    private String type;

    /**
     *  商户类别：1、品牌商户 2、社区商户（供销社）
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
     * @see AuthenticationStatus
     */
    private String authenticationStatus;

    /**
     * 业态
     * @see sys_dict_item 表
     */
    private String businessType;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 删除标识
     */
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private String delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private String username;

}