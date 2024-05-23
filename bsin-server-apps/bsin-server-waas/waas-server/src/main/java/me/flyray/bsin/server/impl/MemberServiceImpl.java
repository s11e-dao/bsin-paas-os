package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.customer.CustomerBase;
import me.flyray.bsin.utils.BsinResultEntity;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.Map;

import me.flyray.bsin.facade.service.MemberService;

/**
 * @author bolei
 * @date 2023/8/6 10:12
 * @desc
 */
@Slf4j
@DubboService
@ApiModule(value = "member")
@ShenyuDubboService("/member")
public class MemberServiceImpl implements MemberService {

    @Override
    @ApiDoc(desc = "login")
    @ShenyuDubboClient("/login")
    public BsinResultEntity<Map<String, Object>> login(Map<String, Object> requestMap) {
        return BsinResultEntity.ok();
    }

    @Override
    @ShenyuDubboClient("/register")
    @ApiDoc(desc = "register")
    public BsinResultEntity register(CustomerBase customerBase) throws UnsupportedEncodingException {
        log.info("MemberService.register,params:{}", customerBase);
        return BsinResultEntity.ok();
    }

    @Override
    public Map<String, Object> getLoginVerifycode(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> registerOrLogin(Map<String, Object> requestMap) throws UnsupportedEncodingException {
        return null;
    }

    @Override
    public Map<String, Object> web3Login(Map<String, Object> requestMap) throws SignatureException, UnsupportedEncodingException {
        return null;
    }

    @Override
    public Map<String, Object> openMember(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getMemberGradeDetail(Map<String, Object> requestMap) {
        return null;
    }
}
