package me.flyray.bsin.infrastructure.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@Data
@ConfigurationProperties(prefix = "bsin.default-menu")
@PropertySource(value="classpath:application.yml", encoding="UTF-8")
public class DefaultMenuConfig {

    private String appId;

    private String menuName;

    private String menuIcon;

    private Integer sort;

    private Integer type;

    private String permission;

    private String path;

    private String parentId;

    private String roleName;

}
