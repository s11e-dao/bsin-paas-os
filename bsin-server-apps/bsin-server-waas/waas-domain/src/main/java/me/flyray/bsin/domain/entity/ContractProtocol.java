package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.flyray.bsin.domain.enums.AssetsCollectionType;
import me.flyray.bsin.domain.enums.ContracrCategory;
import me.flyray.bsin.domain.enums.ProtocolName;


import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author makejava
 */

@TableName(value ="da_contract_protocol")
@Data
@EqualsAndHashCode(callSuper = false)
public class ContractProtocol implements Serializable {

    /** 合约ID */
    @TableId(type = IdType.ASSIGN_ID)
    private String serialNo;

    private String tenantId;

    private String createBy;

    /** 合约项目编号 */
    private String protocolCode;

    /** 合约协议标准
     * @see ContractProtocolStandards
     * */
    private String protocolStandards;

    /**
     * 合约类型: 1、数字徽章 2、PFP 3、数字积分 4、数字门票 5、pass卡 6、徽章/门票
     * @see AssetsCollectionType
     */
    private String type;

    /**
     * 合约分类： 1、Core 2、Factory 3、Extension 4、Wrapper  5、Proxy  5、Other
     * @see ContracrCategory
     */
    private String category;


    /** 协议名称
     * @see ProtocolName
     * */
    private String protocolName;

    /**
     * 版本号：010203
     */
    private String version;

    /** 合约模板bytecode */
    private String protocolBytecode;

    /** 合约模板abi字符 */
    private String protocolAbi;

    /** 模板描述 */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /** 链类型
     * @see ChainType: conflux|polygon|ethereum|tron|bsc
     * */
    private String chainType;

    /** 合约封面
     * */
    private String coverImage;


    @TableLogic(
            value = "0",
            delval = "1"
    )
    private String delFlag;

/*    *//** 链网络
     * @see ChainEnv
     * *//*
    private String chainEnv;*/

}
