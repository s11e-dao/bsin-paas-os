package me.flyray.bsin.blockchain.service;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.config.BlockchainProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;

import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Web3j连接池管理服务
 * 复用Web3j连接，减少创建开销
 */
@Slf4j
@Service
public class Web3jConnectionPoolService {

    @Autowired
    private BlockchainProperties blockchainProperties;

    // HTTP连接池
    private final ConcurrentHashMap<String, PooledConnection<Web3j>> httpConnectionPool = new ConcurrentHashMap<>();
    
    // WebSocket连接池
    private final ConcurrentHashMap<String, PooledConnection<Web3j>> wsConnectionPool = new ConcurrentHashMap<>();
    
    // 连接池配置
    private static final int MAX_CONNECTIONS_PER_CHAIN = 5;
    private static final long CONNECTION_TIMEOUT_MINUTES = 30;
    private static final long HEALTH_CHECK_INTERVAL_MINUTES = 5;
    
    // 定时任务执行器
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    // 连接统计
    private final AtomicInteger totalConnections = new AtomicInteger(0);
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    /**
     * 池化连接包装类
     */
    private static class PooledConnection<T> {
        private final T connection;
        private final LocalDateTime createTime;
        private final LocalDateTime lastUsedTime;
        private final AtomicInteger useCount;
        private volatile boolean inUse;

        public PooledConnection(T connection) {
            this.connection = connection;
            this.createTime = LocalDateTime.now();
            this.lastUsedTime = LocalDateTime.now();
            this.useCount = new AtomicInteger(0);
            this.inUse = false;
        }

        public T getConnection() {
            return connection;
        }

        public boolean isInUse() {
            return inUse;
        }

        public void setInUse(boolean inUse) {
            this.inUse = inUse;
            if (inUse) {
                useCount.incrementAndGet();
            }
        }

        public LocalDateTime getLastUsedTime() {
            return lastUsedTime;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public int getUseCount() {
            return useCount.get();
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(createTime.plusMinutes(CONNECTION_TIMEOUT_MINUTES));
        }

        public void updateLastUsedTime() {
            // 注意：这里没有实际更新lastUsedTime，因为LocalDateTime是不可变的
            // 在实际使用中，可以考虑使用volatile的long类型时间戳
        }
    }

    /**
     * 构造函数 - 启动健康检查
     */
    public Web3jConnectionPoolService() {
        // 启动连接健康检查任务
        scheduler.scheduleAtFixedRate(this::healthCheck, HEALTH_CHECK_INTERVAL_MINUTES, 
                HEALTH_CHECK_INTERVAL_MINUTES, TimeUnit.MINUTES);
        
        // 启动连接清理任务
        scheduler.scheduleAtFixedRate(this::cleanupExpiredConnections, 10, 10, TimeUnit.MINUTES);
        
        log.info("Web3j连接池服务已启动");
    }

    /**
     * 获取HTTP Web3j连接
     */
    public Web3j getHttpConnection(String chainName) {
        return getConnection(chainName, true);
    }

    /**
     * 获取WebSocket Web3j连接
     */
    public Web3j getWebSocketConnection(String chainName) {
        return getConnection(chainName, false);
    }

    /**
     * 获取连接（HTTP或WebSocket）
     */
    private Web3j getConnection(String chainName, boolean isHttp) {
        String poolKey = chainName + "_" + (isHttp ? "http" : "ws");
        ConcurrentHashMap<String, PooledConnection<Web3j>> pool = isHttp ? httpConnectionPool : wsConnectionPool;
        
        try {
            // 1. 尝试从池中获取可用连接
            PooledConnection<Web3j> pooledConnection = pool.get(poolKey);
            if (pooledConnection != null && !pooledConnection.isInUse() && !pooledConnection.isExpired()) {
                pooledConnection.setInUse(true);
                activeConnections.incrementAndGet();
                log.debug("复用连接: chain={}, type={}, useCount={}", chainName, isHttp ? "HTTP" : "WebSocket", pooledConnection.getUseCount());
                return pooledConnection.getConnection();
            }

            // 2. 检查连接数量限制
            if (pool.size() >= MAX_CONNECTIONS_PER_CHAIN) {
                log.warn("连接池已满，强制使用现有连接: chain={}, type={}", chainName, isHttp ? "HTTP" : "WebSocket");
                if (pooledConnection != null) {
                    pooledConnection.setInUse(true);
                    activeConnections.incrementAndGet();
                    return pooledConnection.getConnection();
                }
            }

            // 3. 创建新连接
            Web3j newConnection = createNewConnection(chainName, isHttp);
            PooledConnection<Web3j> newPooledConnection = new PooledConnection<>(newConnection);
            newPooledConnection.setInUse(true);
            
            pool.put(poolKey, newPooledConnection);
            totalConnections.incrementAndGet();
            activeConnections.incrementAndGet();
            
            log.info("创建新连接: chain={}, type={}, totalConnections={}", chainName, isHttp ? "HTTP" : "WebSocket", totalConnections.get());
            return newConnection;
            
        } catch (Exception e) {
            log.error("获取连接失败: chain={}, type={}", chainName, isHttp ? "HTTP" : "WebSocket", e);
            throw new RuntimeException("无法获取Web3j连接", e);
        }
    }

    /**
     * 释放连接回池中
     */
    public void releaseConnection(String chainName, Web3j connection, boolean isHttp) {
        String poolKey = chainName + "_" + (isHttp ? "http" : "ws");
        ConcurrentHashMap<String, PooledConnection<Web3j>> pool = isHttp ? httpConnectionPool : wsConnectionPool;
        
        PooledConnection<Web3j> pooledConnection = pool.get(poolKey);
        if (pooledConnection != null && pooledConnection.getConnection() == connection) {
            pooledConnection.setInUse(false);
            activeConnections.decrementAndGet();
            log.debug("释放连接: chain={}, type={}", chainName, isHttp ? "HTTP" : "WebSocket");
        }
    }

    /**
     * 创建新的Web3j连接
     */
    private Web3j createNewConnection(String chainName, boolean isHttp) throws Exception {
        BlockchainProperties.ChainConfig chainConfig = blockchainProperties.getChainConfig(chainName);
        if (chainConfig == null || chainConfig.getRpcUrl() == null) {
            throw new IllegalArgumentException("链 " + chainName + " 的RPC配置不存在");
        }

        if (isHttp) {
            // 创建HTTP连接
            HttpService httpService = new HttpService(chainConfig.getRpcUrl());
            return Web3j.build(httpService);
        } else {
            // 创建WebSocket连接
            String wsUrl = getWebSocketUrl(chainConfig);
            WebSocketService wsService = new WebSocketService(wsUrl, true);
            wsService.connect();
            return Web3j.build(wsService);
        }
    }

    /**
     * 获取WebSocket URL
     */
    private String getWebSocketUrl(BlockchainProperties.ChainConfig chainConfig) {
        // 优先使用配置的WebSocket URL
        if (chainConfig.getWebSocketUrl() != null && !chainConfig.getWebSocketUrl().isEmpty()) {
            return chainConfig.getWebSocketUrl();
        }
        
        // 如果没有配置WebSocket URL，尝试将HTTP URL转换为WebSocket URL
        String rpcUrl = chainConfig.getRpcUrl();
        if (rpcUrl.startsWith("http://")) {
            return rpcUrl.replace("http://", "ws://");
        } else if (rpcUrl.startsWith("https://")) {
            return rpcUrl.replace("https://", "wss://");
        }
        
        throw new IllegalArgumentException("无法确定WebSocket URL: " + rpcUrl);
    }

    /**
     * 连接健康检查
     */
    private void healthCheck() {
        log.debug("开始连接健康检查...");
        
        int httpHealthy = 0, wsHealthy = 0;
        int httpTotal = 0, wsTotal = 0;
        
        // 检查HTTP连接
        for (var entry : httpConnectionPool.entrySet()) {
            httpTotal++;
            try {
                PooledConnection<Web3j> pooledConnection = entry.getValue();
                if (!pooledConnection.isInUse()) {
                    Web3j web3j = pooledConnection.getConnection();
                    // 简单的健康检查：获取最新区块号
                    web3j.ethBlockNumber().send();
                    httpHealthy++;
                }
            } catch (Exception e) {
                log.warn("HTTP连接健康检查失败: {}", entry.getKey(), e);
            }
        }
        
        // 检查WebSocket连接
        for (var entry : wsConnectionPool.entrySet()) {
            wsTotal++;
            try {
                PooledConnection<Web3j> pooledConnection = entry.getValue();
                if (!pooledConnection.isInUse()) {
                    Web3j web3j = pooledConnection.getConnection();
                    // WebSocket连接的健康检查
                    web3j.ethBlockNumber().send();
                    wsHealthy++;
                }
            } catch (Exception e) {
                log.warn("WebSocket连接健康检查失败: {}", entry.getKey(), e);
            }
        }
        
        log.info("连接健康检查完成: HTTP {}/{} 健康, WebSocket {}/{} 健康, 活跃连接: {}", 
                httpHealthy, httpTotal, wsHealthy, wsTotal, activeConnections.get());
    }

    /**
     * 清理过期连接
     */
    private void cleanupExpiredConnections() {
        log.debug("开始清理过期连接...");
        
        int httpRemoved = 0, wsRemoved = 0;
        
        // 清理HTTP连接
        httpConnectionPool.entrySet().removeIf(entry -> {
            PooledConnection<Web3j> pooledConnection = entry.getValue();
            if (pooledConnection.isExpired() && !pooledConnection.isInUse()) {
                try {
                    // 关闭Web3j连接
                    Web3j web3j = pooledConnection.getConnection();
                    if (web3j != null) {
                        web3j.shutdown();
                    }
                } catch (Exception e) {
                    log.warn("关闭过期HTTP连接失败: {}", entry.getKey(), e);
                }
                return true;
            }
            return false;
        });
        
        // 清理WebSocket连接
        wsConnectionPool.entrySet().removeIf(entry -> {
            PooledConnection<Web3j> pooledConnection = entry.getValue();
            if (pooledConnection.isExpired() && !pooledConnection.isInUse()) {
                try {
                    // 关闭WebSocket连接
                    Web3j web3j = pooledConnection.getConnection();
                    if (web3j != null) {
                        web3j.shutdown();
                    }
                } catch (Exception e) {
                    log.warn("关闭过期WebSocket连接失败: {}", entry.getKey(), e);
                }
                return true;
            }
            return false;
        });
        
        if (httpRemoved > 0 || wsRemoved > 0) {
            log.info("清理过期连接完成: HTTP {} 个, WebSocket {} 个", httpRemoved, wsRemoved);
        }
    }

    /**
     * 获取连接池统计信息
     */
    public String getPoolStats() {
        return String.format("连接池统计 - 总连接: %d, 活跃连接: %d, HTTP池: %d, WebSocket池: %d",
                totalConnections.get(),
                activeConnections.get(),
                httpConnectionPool.size(),
                wsConnectionPool.size());
    }

    /**
     * 获取详细的连接池信息
     */
    public String getDetailedPoolStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Web3j连接池详细信息 ===\n");
        sb.append(String.format("总连接数: %d, 活跃连接数: %d\n", totalConnections.get(), activeConnections.get()));
        sb.append(String.format("HTTP连接池: %d 个连接\n", httpConnectionPool.size()));
        sb.append(String.format("WebSocket连接池: %d 个连接\n", wsConnectionPool.size()));
        
        sb.append("\n--- HTTP连接详情 ---\n");
        for (var entry : httpConnectionPool.entrySet()) {
            PooledConnection<Web3j> conn = entry.getValue();
            sb.append(String.format("%s: 使用次数=%d, 创建时间=%s, 状态=%s\n",
                    entry.getKey(), conn.getUseCount(), conn.getCreateTime(), 
                    conn.isInUse() ? "使用中" : "空闲"));
        }
        
        sb.append("\n--- WebSocket连接详情 ---\n");
        for (var entry : wsConnectionPool.entrySet()) {
            PooledConnection<Web3j> conn = entry.getValue();
            sb.append(String.format("%s: 使用次数=%d, 创建时间=%s, 状态=%s\n",
                    entry.getKey(), conn.getUseCount(), conn.getCreateTime(), 
                    conn.isInUse() ? "使用中" : "空闲"));
        }
        
        return sb.toString();
    }

    /**
     * 销毁时关闭所有连接
     */
    @PreDestroy
    public void destroy() {
        log.info("正在关闭Web3j连接池...");
        
        // 关闭定时任务
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        // 关闭所有HTTP连接
        for (var entry : httpConnectionPool.entrySet()) {
            try {
                Web3j web3j = entry.getValue().getConnection();
                if (web3j != null) {
                    web3j.shutdown();
                }
            } catch (Exception e) {
                log.warn("关闭HTTP连接失败: {}", entry.getKey(), e);
            }
        }
        
        // 关闭所有WebSocket连接
        for (var entry : wsConnectionPool.entrySet()) {
            try {
                Web3j web3j = entry.getValue().getConnection();
                if (web3j != null) {
                    web3j.shutdown();
                }
            } catch (Exception e) {
                log.warn("关闭WebSocket连接失败: {}", entry.getKey(), e);
            }
        }
        
        httpConnectionPool.clear();
        wsConnectionPool.clear();
        
        log.info("Web3j连接池已关闭");
    }
}
