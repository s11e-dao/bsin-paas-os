package me.flyray.bsin.facade.service;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.text.ParseException;
import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/27
 * @desc 客户服务
 */
public interface CustomerService {

  /** 分页查询客户信息 */
  public Map<String, Object> getPageList(Map<String, Object> requestMap);

}
