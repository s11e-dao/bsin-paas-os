package me.flyray.bsin.blockchain.did;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * 简化的 DID 文档实现
 * 替代复杂的 foundation.identity.did 库
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleDIDDocument {
    
    @JsonProperty("@context")
    private List<String> context;
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("verificationMethod")
    private List<SimpleVerificationMethod> verificationMethod;
    
    @JsonProperty("authentication")
    private List<String> authentication;
    
    @JsonProperty("assertionMethod")
    private List<String> assertionMethod;
    
    @JsonProperty("controller")
    private String controller;
    
    @JsonProperty("service")
    private List<SimpleService> service;

    // 构造函数
    public SimpleDIDDocument() {
        this.context = List.of(
            "https://www.w3.org/ns/did/v1",
            "https://w3id.org/security/suites/ed25519-2018/v1"
        );
    }

    // Getters and Setters
    public List<String> getContext() { return context; }
    public void setContext(List<String> context) { this.context = context; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<SimpleVerificationMethod> getVerificationMethod() { return verificationMethod; }
    public void setVerificationMethod(List<SimpleVerificationMethod> verificationMethod) { this.verificationMethod = verificationMethod; }

    public List<String> getAuthentication() { return authentication; }
    public void setAuthentication(List<String> authentication) { this.authentication = authentication; }

    public List<String> getAssertionMethod() { return assertionMethod; }
    public void setAssertionMethod(List<String> assertionMethod) { this.assertionMethod = assertionMethod; }

    public String getController() { return controller; }
    public void setController(String controller) { this.controller = controller; }

    public List<SimpleService> getService() { return service; }
    public void setService(List<SimpleService> service) { this.service = service; }

    /**
     * 转换为 JSON 字符串
     */
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert DID document to JSON", e);
        }
    }

    /**
     * 转换为格式化的 JSON 字符串
     */
    public String toJson(boolean prettyPrint) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (prettyPrint) {
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
            } else {
                return mapper.writeValueAsString(this);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert DID document to JSON", e);
        }
    }

    /**
     * 从 JSON 字符串创建 DID 文档
     */
    public static SimpleDIDDocument fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, SimpleDIDDocument.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse DID document from JSON", e);
        }
    }

    /**
     * 验证方法内部类
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SimpleVerificationMethod {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("controller")
        private String controller;
        
        @JsonProperty("publicKeyBase58")
        private String publicKeyBase58;

        public SimpleVerificationMethod() {}

        public SimpleVerificationMethod(String id, String type, String controller, String publicKeyBase58) {
            this.id = id;
            this.type = type;
            this.controller = controller;
            this.publicKeyBase58 = publicKeyBase58;
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getController() { return controller; }
        public void setController(String controller) { this.controller = controller; }

        public String getPublicKeyBase58() { return publicKeyBase58; }
        public void setPublicKeyBase58(String publicKeyBase58) { this.publicKeyBase58 = publicKeyBase58; }
    }

    /**
     * 服务内部类
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SimpleService {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("serviceEndpoint")
        private String serviceEndpoint;

        public SimpleService() {}

        public SimpleService(String id, String type, String serviceEndpoint) {
            this.id = id;
            this.type = type;
            this.serviceEndpoint = serviceEndpoint;
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getServiceEndpoint() { return serviceEndpoint; }
        public void setServiceEndpoint(String serviceEndpoint) { this.serviceEndpoint = serviceEndpoint; }
    }
}
