package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 客户等级权益利益表
 * @TableName da_customer_profile
 */

@Data
@TableName(value ="da_customer_profile")
public class CustomerProfile implements Serializable {
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
     * 商户编码
     */
    private String merchantNo;

    /**
     * 客户编码
     */
    private String customerNo;

    /**
     * profile名称
     */
//    @NotBlank(message = "profile名称不能为空！")
    private String name;

    /**
     * profile符号
     */
//    @NotBlank(message = "profile符号不能为空！")
    private String symbol;


    /**
     * profile会员数量
     */
    private BigInteger memberNo;

    /**
     * profile 合约地址
     */
    private String contractAddress;

    /**
     * profile分类： Brand Individual
     */
//    @NotBlank(message = "profile分类不能为空！")
    private String type;

    /**
     * profile的编号：根据创建时间从0递增
     */
    private BigInteger profileNum;

    /**
     * assets的数量：发行和注册搭配profile中的资产数量
     */
    private BigInteger assetsCount;

    /**
     * profile external URI for profile metadata
     */
    private String  externalUri;

    /** 链类型
     * @see ChainType: conflux|polygon|ethereum|tron|bsc
     * */
    private String chainType;

    /** 链网络
     * @see ChainEnv
     * */
    private String chainEnv;

    /**
     * 描述
     */
    private String description;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;




}