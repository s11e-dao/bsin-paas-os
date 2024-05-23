package me.flyray.bsin.infrastructure.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bsin.tenant")
public class TenantConfig {

    private String password;

    private String postName;

    private String appId;

    private String daobookAppId;

    private String qixietongAppId;

    private String workflowAppId;

    private String workflowRoleId;

    private String roleName;

    private String bizRoleName;

    private String roleId;

    private Integer roleType;

    private List<String> platformMenus;

}
