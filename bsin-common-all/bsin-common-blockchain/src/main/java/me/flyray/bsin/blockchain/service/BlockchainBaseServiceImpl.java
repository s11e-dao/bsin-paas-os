package me.flyray.bsin.blockchain.service;

import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigInteger;
import java.util.function.Consumer;

/**
 * 区块链基础服务实现
 * 整合事件监听和区块监听功能
 */
@Slf4j
@Service
public class BlockchainBaseServiceImpl implements BlockchainBaseService {
    
    @Autowired
    private BsinBlockChainEventListonService eventListenService;
    
    @Autowired
    private BsinBlockChainBlockListonService blockListenService;
    
    @Override
    public Disposable listenContractEvent(String chainEnv, String contractAddress, String eventName, Consumer<Log> eventHandler) {
        return eventListenService.listenContractEvent(chainEnv, contractAddress, eventName, eventHandler);
    }
    
    @Override
    public Disposable listenBlockEvent(String chainEnv, Consumer<BigInteger> blockHandler) {
        return eventListenService.listenBlockEvent(chainEnv, blockHandler);
    }
    
    @Override
    public Disposable listenNewBlocks(String chainEnv, Consumer<BigInteger> blockHandler) {
        return blockListenService.listenNewBlocks(chainEnv, blockHandler);
    }
    
    @Override
    public Disposable listenPendingTransactions(String chainEnv, Consumer<String> transactionHandler) {
        return blockListenService.listenPendingTransactions(chainEnv, transactionHandler);
    }
}
