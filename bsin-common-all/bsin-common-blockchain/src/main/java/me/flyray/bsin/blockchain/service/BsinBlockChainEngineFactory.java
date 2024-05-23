package me.flyray.bsin.blockchain.service;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.bsc.BscBiz;
import me.flyray.bsin.blockchain.conflux.ConfluxBiz;
import me.flyray.bsin.blockchain.enums.ChainType;
import me.flyray.bsin.blockchain.polygon.PolygonBiz;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.exception.BusinessException;

/**
 * @author ：leonard
 * @date ：Created in 2022/12/24 21：00
 * @description： 区块链引擎 链实例工厂
 * @modified By：
 */

@Slf4j
@Component
public class BsinBlockChainEngineFactory {

    public BsinBlockChainEngine getBsinBlockChainEngineInstance(String chainType) {
        BsinBlockChainEngine bsinBlockChainEngine = null;
        if (chainType.equals(ChainType.CONFLUX.getCode())) {
            bsinBlockChainEngine = new ConfluxBiz();
        } else if (chainType.equals(ChainType.BSC.getCode())) {
            bsinBlockChainEngine = new BscBiz();
        } else if (chainType.equals(ChainType.TRON.getCode())) {
            throw new BusinessException(ResponseCode.NOT_SUPPORTED_ChAIN_TYPE.getCode(), "暂未开放的链，敬请期待！！");
//            bsinBlockChainEngine = new TronBiz();
        } else if (chainType.equals(ChainType.POLYGON.getCode())) {
            bsinBlockChainEngine = new PolygonBiz();
        } else if (chainType.equals(ChainType.ETHEREUM.getCode())) {
            throw new BusinessException(ResponseCode.NOT_SUPPORTED_ChAIN_TYPE.getCode(), "暂未开放的链，敬请期待！！");
//            bsinBlockChainEngine = new EthereumBiz();
        } else {
            throw new BusinessException(ResponseCode.NOT_SUPPORTED_ChAIN_TYPE);
        }
        return bsinBlockChainEngine;
    }

}
