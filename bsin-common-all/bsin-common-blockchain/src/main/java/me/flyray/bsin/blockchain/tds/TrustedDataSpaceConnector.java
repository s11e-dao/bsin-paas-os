package me.flyray.bsin.blockchain.tds;


import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 可信数据空间连接器接口。
 * 定义了与可信数据空间交互所需的基本方法。
 */
public interface TrustedDataSpaceConnector {

    /**
     * (示例方法) 上传数据到可信数据空间。
     * @param dataId 数据唯一标识符
     * @param payload 要上传的数据
     * @return 操作结果或凭证
     * @throws DataOperationException 如果操作失败
     */
    String uploadData(String dataId, Object payload) throws DataOperationException;

    /**
     * (示例方法) 从可信数据空间下载数据。
     * @param dataId 数据唯一标识符
     * @return 下载的数据
     * @throws DataOperationException 如果操作失败
     */
    Object downloadData(String dataId) throws DataOperationException;


    // --- 配置获取方法 (用于演示Builder的参数传递) ---
    String getEndpointUrl();
    String getApiKey();
    Map<String, String> getCustomHeaders();
    long getConnectTimeoutMillis();

    /**
     * 提供一个静态工厂方法来创建 Builder 实例。
     * @return 一个新的 Builder 实例。
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * 用于构建 {@link TrustedDataSpaceConnector} 实例的 Builder 类。
     * 允许链式调用来配置连接器参数。
     */
    class Builder {
        public String endpointUrl;
        public String apiKey;
        public String clientId;
        public char[] clientSecret; // 使用 char[] 存储密码更安全
        public byte[] clientCertificate;
        public char[] clientCertificatePassword;
        public byte[] trustStore;
        public char[] trustStorePassword;
        public long connectTimeoutMillis = TimeUnit.SECONDS.toMillis(10); // 默认连接超时
        public long readTimeoutMillis = TimeUnit.SECONDS.toMillis(30);   // 默认读取超时
        public int maxRetries = 3; // 默认最大重试次数
        public String proxyHost;
        public int proxyPort = -1; // -1 表示未设置
        public Map<String, String> customHeaders = new HashMap<>();

        private Builder() {
            // 私有构造函数，强制通过 TrustedDataSpaceConnector.builder() 创建
        }

        public Builder endpointUrl(String endpointUrl) {
            this.endpointUrl = endpointUrl;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(char[] clientSecret) {
            this.clientSecret = clientSecret; // 考虑克隆以保护原始数组
            return this;
        }

        public Builder clientCertificate(byte[] clientCertificate, char[] password) {
            this.clientCertificate = clientCertificate; // 考虑克隆
            this.clientCertificatePassword = password; // 考虑克隆
            return this;
        }

        public Builder trustStore(byte[] trustStore, char[] password) {
            this.trustStore = trustStore; // 考虑克隆
            this.trustStorePassword = password; // 考虑克隆
            return this;
        }

        public Builder connectTimeout(long duration, TimeUnit unit) {
            this.connectTimeoutMillis = unit.toMillis(duration);
            return this;
        }

        public Builder readTimeout(long duration, TimeUnit unit) {
            this.readTimeoutMillis = unit.toMillis(duration);
            return this;
        }

        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public Builder proxy(String host, int port) {
            this.proxyHost = host;
            this.proxyPort = port;
            return this;
        }

        public Builder addCustomHeader(String name, String value) {
            Objects.requireNonNull(name, "Header name cannot be null");
            Objects.requireNonNull(value, "Header value cannot be null");
            this.customHeaders.put(name, value);
            return this;
        }

        public Builder customHeaders(Map<String, String> headers) {
            if (headers != null) {
                this.customHeaders = new HashMap<>(headers); // 防御性拷贝
            }
            return this;
        }

        /**
         * 构建并返回一个配置好的 {@link TrustedDataSpaceConnector} 实例。
         * @return 配置好的连接器实例。
         * @throws IllegalStateException 如果必要的参数未设置 (例如 endpointUrl)。
         */
        public TrustedDataSpaceConnector build() {
            Objects.requireNonNull(endpointUrl, "Endpoint URL is required.");
            if (endpointUrl.trim().isEmpty()) {
                throw new IllegalStateException("Endpoint URL cannot be empty.");
            }
            if (connectTimeoutMillis <= 0) {
                throw new IllegalStateException("Connect timeout must be positive.");
            }
            if (readTimeoutMillis <= 0) {
                throw new IllegalStateException("Read timeout must be positive.");
            }
            if (maxRetries < 0) {
                throw new IllegalStateException("Max retries cannot be negative.");
            }
            if (proxyHost != null && (proxyPort <= 0 || proxyPort > 65535)) {
                throw new IllegalStateException("Proxy port is invalid.");
            }
            if ((clientSecret != null && clientId == null) || (clientId != null && clientSecret == null && apiKey == null && clientCertificate == null) ){
                // 根据实际认证逻辑调整, 例如：如果设置了clientSecret，则clientId也必须设置
                // 或者至少需要一种认证方式
                // System.out.println("Warning: Partial OAuth2 credentials or missing authentication method.");
            }


            // 创建并返回实际的连接器实现
            return new DefaultTrustedDataSpaceConnector(this);
        }
    }


    // --- 自定义异常 (示例) ---
    class ConnectionException extends Exception {
        public ConnectionException(String message) {
            super(message);
        }
        public ConnectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    class DataOperationException extends Exception {
        public DataOperationException(String message) {
            super(message);
        }
        public DataOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static void main(String[] args) {
        try {
            TrustedDataSpaceConnector connector = TrustedDataSpaceConnector.builder()
                    .endpointUrl("https://api.mytrusteddataspace.com/v1")
                    .apiKey("your-api-key-here")
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(45, TimeUnit.SECONDS)
                    .maxRetries(5)
                    .addCustomHeader("X-Tenant-ID", "tenant-123")
                    .addCustomHeader("X-Client-Version", "1.0.0")
                    .build();

            // 示例：使用 OAuth2 客户端凭据 (概念性，实际实现需要HTTP客户端支持)
            TrustedDataSpaceConnector oauthConnector = TrustedDataSpaceConnector.builder()
                    .endpointUrl("https://auth.mytrusteddataspace.com/token") // 通常是 token 端点
                    // .clientId("your-client-id")
                    // .clientSecret("your-client-secret".toCharArray())
                    // ... 更多 OAuth2 或 HTTP 客户端相关配置
                    .build();
            System.out.println("OAuth Connector Endpoint: " + oauthConnector.getEndpointUrl());


            // 示例：配置代理
            TrustedDataSpaceConnector proxiedConnector = TrustedDataSpaceConnector.builder()
                    .endpointUrl("https://api.internal.mytrusteddataspace.com/v1")
                    .apiKey("internal-api-key")
                    .proxy("proxy.example.com", 8080)
                    .build();
            System.out.println("Proxied Connector Endpoint: " + proxiedConnector.getEndpointUrl());


        }catch (IllegalStateException e) {
            System.err.println("Configuration error: " + e.getMessage());
        }
    }

}
