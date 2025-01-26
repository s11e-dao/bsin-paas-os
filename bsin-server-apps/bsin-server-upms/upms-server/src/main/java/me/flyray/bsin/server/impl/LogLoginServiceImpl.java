package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.SysLogLogin;
import me.flyray.bsin.domain.entity.SysPost;
import me.flyray.bsin.domain.entity.SysTenant;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.LogLoginService;
import me.flyray.bsin.infrastructure.mapper.DictItemMapper;
import me.flyray.bsin.infrastructure.mapper.LogLoginMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@ShenyuDubboService(path ="/logLogin",timeout = 15000)
@ApiModule(value = "logLogin")
@Service
public class LogLoginServiceImpl implements LogLoginService {

    @Autowired
    private LogLoginMapper logLoginMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public SysLogLogin add(Map<String, Object> requestMap) {
        SysLogLogin sysLogLogin = BsinServiceContext.getReqBodyDto(SysLogLogin.class, requestMap);
        sysLogLogin.setSerialNo(BsinSnowflake.getId());
        logLoginMapper.insert(sysLogLogin);
        return sysLogLogin;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String tenantId = loginUser.getTenantId();
        String merchantNo = loginUser.getMerchantNo();
        SysLogLogin sysLogLogin = BsinServiceContext.getReqBodyDto(SysLogLogin.class, requestMap);
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<SysLogLogin> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        LambdaUpdateWrapper<SysLogLogin> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(SysLogLogin::getLoginTime);
        warapper.eq(SysLogLogin::getTenantId, tenantId);
        IPage<SysLogLogin> pageList = logLoginMapper.selectPage(page, warapper);
        return pageList;
    }

}
