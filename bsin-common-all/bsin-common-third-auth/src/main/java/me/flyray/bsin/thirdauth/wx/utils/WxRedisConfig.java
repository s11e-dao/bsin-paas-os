package me.flyray.bsin.thirdauth.wx.utils;

import lombok.Data;

/**
 * wechat miniapp properties
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Data
public class WxRedisConfig {

  /** redis服务器 主机地址 */
  private String host;

  /** redis服务器 端口号 */
  private Integer port;

  /** redis服务器 密码 */
  private String password;

  /** redis 服务连接超时时间 */
  private Integer timeout;

}
