package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.facade.response.CustomerAccountVO;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/27
 * @desc 客户服务
 */
public interface CustomerService {

  /** 会员客户登录 */
  public Map<String, Object> login(Map<String, Object> requestMap);

  /** 会员客户注册 */
  public CustomerBase register(Map<String, Object> requestMap)throws UnsupportedEncodingException ;

  /** 获取登录验证码 */
  public Map<String, Object> getLoginVerifycode(Map<String, Object> requestMap);

  /** 客户注册登录接口:用户注册成一个客户，还不是会员，会员需要开通 1、微信注册登录 2、手机验证码注册登录 */
  public Map<String, Object> registerOrLogin(Map<String, Object> requestMap)throws UnsupportedEncodingException ;

  /** web3登录 前端钱包签名，后端验证之后换取token */
  public Map<String, Object> web3Login(Map<String, Object> requestMap) throws SignatureException, UnsupportedEncodingException;

  /** 身份认证 */
  public void identityVerification(Map<String, Object> requestMap);

  /** 分页查询客户信息 */
  public IPage<?> getPageList(Map<String, Object> requestMap);

  /** 查询客户信息 1、基础信息 2、等级信息 */
  public CustomerBase getDetail(Map<String, Object> requestMap);

  /** 客户信息编辑 */
  public CustomerBase edit(Map<String, Object> requestMap);

  /** 激励发放 */
  public Map<String, Object> incentiveDistribution(Map<String, Object> requestMap);

  /** 根据数组查询客户信息 */
  public List<?> getListByCustomerNos(Map<String, Object> requestMap);

  /** 实名认证后 */
  public CustomerBase certification(Map<String, Object> requestMap) throws Exception;

  /** 设置钱包信息 */
  public void settingWallet(Map<String, Object> requestMap);

  /**
   * 设置Profile信息
   * */
  public void settingProfile(Map<String, Object> requestMap);


  /**
   * 查询品牌客户的钱包账户 1、曲线积分（相当于声誉值、成长值、贡献值） 2、品牌积分 3、平台储值账户余额 4、钱包地址信息
   *
   * @param requestMap
   * @return
   */
  public CustomerAccountVO getWalletInfo(Map<String, Object> requestMap);

  /**
   * 本月连续签到次数
   *
   * @param requestMap
   * @return
   */
  public Map<String, Object> getContinuousSignCount(Map<String, Object> requestMap)
      throws ParseException;

  /**
   * 获取累计签到数
   *
   * @param requestMap
   * @return
   */
  public Map<String, Object> getSumSignCount(Map<String, Object> requestMap) throws ParseException;

  /**
   * 签到
   *
   * @param requestMap
   * @return
   */
  public String sign(Map<String, Object> requestMap) throws ParseException;

  /**
   * 签到结果
   *
   * @param requestMap
   * @return
   */
  public boolean getSignResult(Map<String, Object> requestMap) throws ParseException;

  /**
   * 签到信息
   *
   * @param requestMap
   * @return
   */
  public Map<String, String> getSignInfo(Map<String, Object> requestMap) throws ParseException;

  /**
   * 获取客户邀请列表
   */
  public List<?> getInviteeList(Map<String, Object> requestMap);

}
