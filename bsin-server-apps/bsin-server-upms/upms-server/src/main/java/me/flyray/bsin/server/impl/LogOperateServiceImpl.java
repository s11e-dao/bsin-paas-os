package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.SysLogOperate;
import me.flyray.bsin.facade.service.LogOperateService;
import me.flyray.bsin.infrastructure.mapper.LogOperMapper;
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

@ShenyuDubboService(path ="/logOperate",timeout = 15000)
@ApiModule(value = "logOperate")
@Service
public class LogOperateServiceImpl implements LogOperateService {

    @Autowired
    private LogOperMapper logOperMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public SysLogOperate add(Map<String, Object> requestMap) {
        SysLogOperate sysLogOper = BsinServiceContext.getReqBodyDto(SysLogOperate.class, requestMap);
        sysLogOper.setSerialNo(BsinSnowflake.getId());
        logOperMapper.insert(sysLogOper);
        return sysLogOper;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String tenantId = loginUser.getTenantId();
        String merchantNo = loginUser.getMerchantNo();
        SysLogOperate sysLogOper = BsinServiceContext.getReqBodyDto(SysLogOperate.class, requestMap);
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<SysLogOperate> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        LambdaUpdateWrapper<SysLogOperate> warapper = new LambdaUpdateWrapper<>();
        warapper.eq(SysLogOperate::getTenantId, tenantId);
        IPage<SysLogOperate> pageList = logOperMapper.selectPage(page, warapper);
        return pageList;
    }

}
