package me.flyray.bsin.payment;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.flyray.bsin.thirdauth.wx.utils.WxRedisConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * wechat pay properties
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Data
@ConfigurationProperties(prefix = "wx.pay")
@PropertySource(value="classpath:application.properties", encoding="UTF-8")
public class WxPayProperties {
  /** 是否使用redis存储access token */
  private boolean useRedis;

  /** redis 配置 */
  private WxRedisConfig redisConfig;

  /** 设置微信公众号或者小程序等的appid */
  private String appId;

  /** 微信支付商户号 */
  private String mchId;

  /** 微信支付商户密钥 */
  private String mchKey;

  /** 服务商模式下的子商户公众账号ID，普通模式请不要配置，请在配置文件中将对应项删除 */
  private String subAppId;

  /** 服务商模式下的子商户号，普通模式请不要配置，最好是请在配置文件中将对应项删除 */
  private String subMchId;

  /** apiclient_cert.p12文件的绝对路径，或者如果放在项目中，请以classpath:开头指定 */
  private String keyPath;


}
