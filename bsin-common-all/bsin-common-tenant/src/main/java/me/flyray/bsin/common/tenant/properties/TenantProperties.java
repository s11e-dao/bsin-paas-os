package me.flyray.bsin.common.tenant.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * 租户 配置属性
 *
 */
@Data
@ConfigurationProperties(prefix = "tenant")
public class TenantProperties {

    /**
     * 是否启用
     */
    private Boolean enable;


    /**
     * 租户字段名
     */
    private String column;


    /**
     * 排除表
     */
    private List<String> excludeTables = Collections.emptyList();


    /**
     * 排除数据源
     */
    private List<String> excludeDataSource = Collections.emptyList();

}
