package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.flyray.bsin.entity.BaseEntity;

import java.io.Serializable;

/**
*
* @TableName waas_contract_method
*/
@Data
@TableName(value ="waas_contract_method")
public class ContractMethod implements Serializable {

    /**
    * 方法ID
    */
    private String methodId;
    /**
    * 方法名
    */
    private String methodName;
    /**
    * 方法类型 1、合约方法 2、非合约方法
    */
    private Integer type;

}
