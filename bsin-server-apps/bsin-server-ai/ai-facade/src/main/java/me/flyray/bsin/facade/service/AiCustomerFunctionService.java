package me.flyray.bsin.facade.service;

import java.util.Map;


/**
 * @author bolei
 * @date 2023/6/27
 * @desc AI客户订阅功能服务
 */
public interface AiCustomerFunctionService {

  /** 添加可订阅服务套餐 */
  Map<String, Object> add(Map<String, Object> requestMap);

  /** 删除 */
  Map<String, Object> delete(Map<String, Object> requestMap);

  /** 编辑 */
  public Map<String, Object> edit(Map<String, Object> requestMap);

  /** 查询订阅服务功能信息 */
  public Map<String, Object> getDetail(Map<String, Object> requestMap);

  /** 创建功能服务订单 */
  public Map<String, Object> createOrder(Map<String, Object> requestMap);

  /** 审核订单 */
  public Map<String, Object> auditOrder(Map<String, Object> requestMap);

  /** 可订阅的功能服务 */
  public Map<String, Object> getSubscribableList(Map<String, Object> requestMap);

  /** 所有订阅订单 */
  public Map<String, Object> getAllFunctionSubscribeList(Map<String, Object> requestMap);

  /** 分页查询客户信息 */
  public Map<String, Object> getList(Map<String, Object> requestMap);

  /** 分页查询客户信息 */
  public Map<String, Object> getPageList(Map<String, Object> requestMap);

  /** 获取登录验证码 */
  public Map<String, Object> getLoginVerifyCode(Map<String, Object> requestMap);

  /**
   * 从公众号号获取验证码 1、注册bsin-copilot商户的时候，需要关注火源社区公众号，发送：注册时的用户名+##(后续开率使用菜单提示实现)到公众号 2、后台提取出
   * 注册用户名，并生成6位数验证码，返回到该公众号用户的同时，存入redis: key=mpVerifyCode:注册用户名 value=6位数验证码 validTime=120s
   * 3、微信用户和bsin-copilot商户绑定： 同时将映射火源社区公众号获取的微信用户的 openID 和 bsin-copilot 用户名存入redis,
   * key=s11eMpOpenId2User:openID value=注册用户名 4、实现微信公众号获取默认微信 以上逻辑在公众号实现
   */
  public Map<String, Object> getMpVerifyCode(Map<String, Object> requestMap);

  /** 验证公众号的验证码 */
  public Map<String, Object> verifyMpCode(Map<String, Object> requestMap);
}
