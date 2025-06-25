package me.flyray.bsin.blockchain.tds;


import foundation.identity.did.DIDDocument;
import foundation.identity.did.Service;
import foundation.identity.did.VerificationMethod;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link TrustedDataSpaceConnector} 的一个默认/内部实现。
 * 这个类是私有的，外部用户只能通过 Builder 来创建。
 */

class DefaultTrustedDataSpaceConnector implements TrustedDataSpaceConnector {

    private final String endpointUrl;
    private final String apiKey;
    private final String clientId;
    private final char[] clientSecret;
    private final byte[] clientCertificate;
    private final char[] clientCertificatePassword;
    private final byte[] trustStore;
    private final char[] trustStorePassword;
    private final long connectTimeoutMillis;
    private final long readTimeoutMillis;
    private final int maxRetries;
    private final String proxyHost;
    private final int proxyPort;
    private final Map<String, String> customHeaders;

    private boolean connected = false; // 示例连接状态

    // 包私有构造函数，接收 Builder 实例
    DefaultTrustedDataSpaceConnector(Builder builder) {
        this.endpointUrl = builder.endpointUrl;
        this.apiKey = builder.apiKey;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret != null ? builder.clientSecret.clone() : null;
        this.clientCertificate = builder.clientCertificate != null ? builder.clientCertificate.clone() : null;
        this.clientCertificatePassword = builder.clientCertificatePassword != null ? builder.clientCertificatePassword.clone() : null;
        this.trustStore = builder.trustStore != null ? builder.trustStore.clone() : null;
        this.trustStorePassword = builder.trustStorePassword != null ? builder.trustStorePassword.clone() : null;
        this.connectTimeoutMillis = builder.connectTimeoutMillis;
        this.readTimeoutMillis = builder.readTimeoutMillis;
        this.maxRetries = builder.maxRetries;
        this.proxyHost = builder.proxyHost;
        this.proxyPort = builder.proxyPort;
        // 确保 customHeaders 是不可变的或至少是一个安全的副本
        this.customHeaders = Collections.unmodifiableMap(new HashMap<>(builder.customHeaders));

        // 在实际应用中，这里会初始化 HTTP 客户端或其他网络组件
        System.out.println("TrustedDataSpaceConnector configured for endpoint: " + this.endpointUrl);
        if (this.apiKey != null) System.out.println("Using API Key authentication.");
        // ... 其他配置信息的日志输出
    }


    @Override
    public Map<String, String> createDidProfile(Map<String, Object> requestMap) {

        URI did = URI.create("did:ex:1234");

        Service service = Service.builder()
                .type("ServiceEndpointProxyService")
                .serviceEndpoint("https://myservice.com/myendpoint")
                .build();

        VerificationMethod verificationMethod = VerificationMethod.builder()
                .id(URI.create(did + "#key-1"))
                .type("Ed25519VerificationKey2018")
                .publicKeyBase58("FyfKP2HvTKqDZQzvyL38yXH7bExmwofxHf2NR5BrcGf1")
                .build();

        DIDDocument diddoc = DIDDocument.builder()
                .id(did)
                .service(service)
                .verificationMethod(verificationMethod)
                .build();

        System.out.println(diddoc.toJson(true));

        // 返回包含 DID 信息的 Map
        Map<String, String> result = new HashMap<>();
        result.put("did", did.toString());
        result.put("didDocument", diddoc.toJson());
        result.put("status", "success");
        
        return result;
    }

    @Override
    public String uploadData(String dataId, Object payload) {
        System.out.println("Uploading data with ID '" + dataId + "' to " + endpointUrl);
        // 实际的上传逻辑
        return "upload-receipt-" + dataId + "-" + System.currentTimeMillis();
    }

    @Override
    public Object downloadData(String dataId) {
        System.out.println("Downloading data with ID '" + dataId + "' from " + endpointUrl);
        // 实际的下载逻辑
        return "Downloaded data for " + dataId;
    }

    // --- Getter 实现 (用于演示) ---
    @Override
    public String getEndpointUrl() {
        return this.endpointUrl;
    }

    @Override
    public String getApiKey() {
        return this.apiKey;
    }

    @Override
    public Map<String, String> getCustomHeaders() {
        return this.customHeaders; // 已是不可变 Map
    }

    @Override
    public long getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }

    // 在这里可以添加其他 getter 方法来暴露配置信息，如果需要的话。
    // 例如: getClientId(), getConnectTimeoutMillis() 等。
    // 密码和证书内容通常不应通过 getter 直接暴露。

}
