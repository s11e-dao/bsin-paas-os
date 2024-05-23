package me.flyray.bsin.blockchain.dto;

import lombok.Data;

/**
 * @author leonard
 * @date 2023/11/27 23:00
 * @desc
 */

@Data
public class ContractTransactionResp {

    /**
     * 交易hash
     */
    private String txHash;

    /**
     * 交易状态
     */
    private String txStatus;

    /**
     * 交易确认状态
     */
    private boolean isStatusOK;
    
    /**
     * 部署的合约地址
     */
    private String contractAddress;

}
