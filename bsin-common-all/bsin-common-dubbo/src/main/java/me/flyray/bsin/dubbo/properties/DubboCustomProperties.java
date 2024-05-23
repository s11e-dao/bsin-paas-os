package me.flyray.bsin.dubbo.properties;

import lombok.Data;
import me.flyray.bsin.dubbo.enums.RequestLogEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 自定义配置
 *
 */
@Data
@ConfigurationProperties(prefix = "dubbo.custom")
public class DubboCustomProperties {

    private Boolean requestLog;

    private RequestLogEnum logLevel;

}
