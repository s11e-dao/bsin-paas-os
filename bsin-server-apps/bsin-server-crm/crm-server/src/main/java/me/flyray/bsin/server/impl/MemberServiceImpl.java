package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;

import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.facade.service.MemberService;

/**
 * @author bolei
 * @date 2023/8/6 10:12
 * @desc
 */
public class MemberServiceImpl implements MemberService {

    @Override
    public Map<String, Object> login(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> register(Map<String, Object> requestMap) throws UnsupportedEncodingException {
        return null;
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
