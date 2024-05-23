package me.flyray.bsin.facade.service;

import me.flyray.bsin.domain.entity.customer.CustomerBase;
import me.flyray.bsin.utils.BsinResultEntity;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.Map;

/**
 * 商户服务
 * @author bolei
 * @date 2023/8/6 10:09
 * @desc
 */

public interface MemberService {

    /** 商户登录 */
    public BsinResultEntity<Map<String, Object>> login(Map<String, Object> requestMap);

    /** 商户注册 */
    public BsinResultEntity register(CustomerBase customerBase)throws UnsupportedEncodingException;

    /** 获取登录验证码 */
    public Map<String, Object> getLoginVerifycode(Map<String, Object> requestMap);

    /** 客户注册登录接口:用户注册成一个客户，还不是会员，会员需要开通 1、微信注册登录 2、手机验证码注册登录 */
    public Map<String, Object> registerOrLogin(Map<String, Object> requestMap)throws UnsupportedEncodingException ;

    /** web3登录 前端钱包签名，后端验证之后换取token */
    public Map<String, Object> web3Login(Map<String, Object> requestMap) throws SignatureException, UnsupportedEncodingException;

    /**
     * 开通会员
     */
    public Map<String, Object> openMember(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public Map<String, Object> getPageList(Map<String, Object> requestMap);

    /**
     * 详情
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);

    /**
     * 详情
     */
    public Map<String, Object> getMemberGradeDetail(Map<String, Object> requestMap);


}
