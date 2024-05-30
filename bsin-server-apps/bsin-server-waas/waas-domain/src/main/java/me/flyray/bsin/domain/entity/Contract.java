package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName da_contract
 */
@TableName(value ="da_contract")
@Data
public class Contract implements Serializable {
    /**
     * 合约配置编号
     */
    @TableId
    private String serialNo;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 商户NO
     */
    private String merchantNo;

    /**
     * 交易hash
     */
    private String txHash;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否被执行 0、未执行 1、执行中 2、已完成
     */
    private Integer status;

    /**
     * 逻辑删除 0、未删除 1、已删除
     */
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private Integer delFlag;

    /**
     * 合约地址
     */
    private String contract;


    /**
     * 合约名称
     */
    private String name;


    /**
     * 版本号：010203
     */
    private String version;

    /**
     * 
     */
    private String contractProtocolNo;

    /**
     * 链环境
     */
    private String chainEnv;

    /**
     * 链类型
     */
    private String chainType;


}