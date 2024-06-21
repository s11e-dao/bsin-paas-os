package me.flyray.bsin.server.utils;

import lombok.Builder;
import lombok.Data;

/**
 * @author ：leonard
 * @date ：Created in 2024/1/20 18:12
 * @description：请求数据
 * @modified By：
 */
@Builder
@Data
public class ReqBodyHandler {
  private String serviceName;
  private String methodName;
  private String version;
  Object bizParams;
}
