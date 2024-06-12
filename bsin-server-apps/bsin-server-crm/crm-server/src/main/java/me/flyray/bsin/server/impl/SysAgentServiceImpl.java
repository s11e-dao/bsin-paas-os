package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Merchant;
import me.flyray.bsin.domain.entity.SysAgent;
import me.flyray.bsin.domain.entity.SysUser;
import me.flyray.bsin.domain.response.UserResp;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.SysAgentService;
import me.flyray.bsin.infrastructure.mapper.SysAgentMapper;
import me.flyray.bsin.security.authentication.AuthenticationProvider;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统代理商服务
 */

@Slf4j
@ShenyuDubboService(path = "/sysAgent",timeout = 15000)
@ApiModule(value = "sysAgent")
@Service
public class SysAgentServiceImpl implements SysAgentService {

    @Value("${bsin.security.authentication-secretKey}")
    private String authSecretKey;
    @Value("${bsin.security.authentication-expiration}")
    private int authExpiration;

    @Autowired
    SysAgentMapper sysAgentMapper;


    @Override
    public Map<String, Object> login(Map<String, Object> requestMap) {
        String username = MapUtils.getString(requestMap, "username");
        // 查询商户信息
        LambdaQueryWrapper<SysAgent> warapper = new LambdaQueryWrapper<>();
        warapper.eq(SysAgent::getUsername, username);
        SysAgent sysAgent = sysAgentMapper.selectOne(warapper);

        // TODO 判断是否是商户员工
        if(sysAgent == null){
            throw new BusinessException(ResponseCode.MERCHANT_NOT_EXISTS);
        }
        LoginUser loginUser = new LoginUser();
        BeanUtil.copyProperties(sysAgent, loginUser);

        // 查询upms用户
        Map res = new HashMap<>();
        loginUser.setUsername(sysAgent.getUsername());
        loginUser.setPhone(sysAgent.getPhone());
        loginUser.setBizRoleTypeNo(sysAgent.getSerialNo());
        loginUser.setBizRoleType(BizRoleType.SYS_AGENT.getCode());
        String token = AuthenticationProvider.createToken(loginUser, authSecretKey, authExpiration);
        res.put("sysAgentInfo",sysAgent);
        res.put("token",token);
        // 查询商户信息
        return res;
    }

    @Override
    public SysAgent add(Map<String, Object> requestMap) {
        SysAgent sysAgent = BsinServiceContext.getReqBodyDto(SysAgent.class, requestMap);
        sysAgent.setSerialNo(BsinSnowflake.getId());
        sysAgentMapper.insert(sysAgent);
        return sysAgent;
    }

    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        sysAgentMapper.deleteById(serialNo);
    }

    @Override
    public SysAgent edit(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        SysAgent sysAgent = BsinServiceContext.getReqBodyDto(SysAgent.class, requestMap);
        String tenantId = sysAgent.getTenantId();
        String merchantNo = LoginInfoContextHelper.getMerchantNo();
        if(tenantId == null){
            tenantId = LoginInfoContextHelper.getTenantId();
        }
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<SysAgent> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaQueryWrapper<SysAgent> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(SysAgent::getCreateTime);
        warapper.eq(SysAgent::getTenantId, tenantId);
        warapper.eq(StringUtils.isNotEmpty(sysAgent.getBusinessType()),SysAgent::getBusinessType, sysAgent.getBusinessType());
        warapper.eq(StringUtils.isNotEmpty(sysAgent.getStatus()),SysAgent::getStatus, sysAgent.getStatus());
        warapper.eq(StringUtils.isNotEmpty(merchantNo),SysAgent::getSerialNo, merchantNo);
        IPage<SysAgent> pageList = sysAgentMapper.selectPage(page,warapper);
        return pageList;
    }

    @Override
    public SysAgent getDetail(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        SysAgent sysAgent = sysAgentMapper.selectById(serialNo);
        return sysAgent;
    }
}
