package me.flyray.bsin.blockchain.connection;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.core.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.chain.bsc.BscBiz;
import me.flyray.bsin.blockchain.chain.conflux.ConfluxBiz;
import me.flyray.bsin.blockchain.enums.ChainType;
import me.flyray.bsin.blockchain.chain.polygon.PolygonBiz;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.exception.BusinessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author ：leonard
 * @date ：Created in 2022/12/24 21：00
 * @description： 区块链引擎 链实例工厂
 * @modified By：
 */

@Slf4j
@Component
public class BsinBlockChainEngineFactory {
    
    // 使用缓存避免重复创建实例
    private final Map<String, BsinBlockChainEngine> engineCache = new ConcurrentHashMap<>();
    
    // 支持的链类型映射
    private final Map<String, Supplier<BsinBlockChainEngine>> engineSuppliers = new HashMap<>();
    
    @PostConstruct
    public void initializeEngineSuppliers() {
        engineSuppliers.put(ChainType.CONFLUX.getCode(), ConfluxBiz::new);
        engineSuppliers.put(ChainType.BSC.getCode(), BscBiz::new);
        engineSuppliers.put(ChainType.POLYGON.getCode(), PolygonBiz::new);
        // 暂时不支持的链
        engineSuppliers.put(ChainType.TRON.getCode(), () -> {
            throw new BusinessException(ResponseCode.NOT_SUPPORTED_ChAIN_TYPE.getCode(), "暂未开放的链，敬请期待！！");
        });
        engineSuppliers.put(ChainType.ETHEREUM.getCode(), () -> {
            throw new BusinessException(ResponseCode.NOT_SUPPORTED_ChAIN_TYPE.getCode(), "暂未开放的链，敬请期待！！");
        });
    }

    public BsinBlockChainEngine getBsinBlockChainEngineInstance(String chainType) {
        // 参数验证
        if (chainType == null || chainType.trim().isEmpty()) {
            throw new IllegalArgumentException("链类型不能为空");
        }
        
        // 从缓存获取
        return engineCache.computeIfAbsent(chainType, this::createEngine);
    }
    
    private BsinBlockChainEngine createEngine(String chainType) {
        Supplier<BsinBlockChainEngine> supplier = engineSuppliers.get(chainType);
        if (supplier == null) {
            throw new BusinessException(ResponseCode.NOT_SUPPORTED_ChAIN_TYPE.getCode(), 
                "不支持的链类型: " + chainType);
        }
        
        try {
            BsinBlockChainEngine engine = supplier.get();
            log.info("成功创建区块链引擎实例: {}", chainType);
            return engine;
        } catch (Exception e) {
            log.error("创建区块链引擎实例失败: {}", chainType, e);
            throw new BusinessException(ResponseCode.NOT_SUPPORTED_ChAIN_TYPE.getCode(), 
                "创建区块链引擎失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取支持的链类型列表
     */
    public List<String> getSupportedChainTypes() {
        return new ArrayList<>(engineSuppliers.keySet());
    }
    
    /**
     * 检查是否支持指定链类型
     */
    public boolean isChainTypeSupported(String chainType) {
        return engineSuppliers.containsKey(chainType);
    }
    
    /**
     * 清除缓存
     */
    public void clearCache() {
        engineCache.clear();
        log.info("区块链引擎缓存已清除");
    }

}
