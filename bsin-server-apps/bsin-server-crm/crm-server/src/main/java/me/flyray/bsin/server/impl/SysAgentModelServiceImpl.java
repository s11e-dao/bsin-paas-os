package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.SysAgentModel;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.SysAgentModelService;
import me.flyray.bsin.infrastructure.mapper.SysAgentModelMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@ShenyuDubboService(path = "/sysAgentModel", timeout = 6000)
@ApiModule(value = "sysAgentModel")
@Service
public class SysAgentModelServiceImpl implements SysAgentModelService {

    @Autowired
    private SysAgentModelMapper sysAgentModelMapper;

    /**
     * 每个租户有一条数据
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "config")
    @ShenyuDubboClient("/config")
    @Override
    public SysAgentModel config(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        SysAgentModel sysAgentModel = BsinServiceContext.getReqBodyDto(SysAgentModel.class, requestMap);
        sysAgentModel.setTenantId(loginUser.getTenantId());
        
        // 查询是否已存在该租户的合伙人模型
        LambdaQueryWrapper<SysAgentModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAgentModel::getTenantId, loginUser.getTenantId());
        SysAgentModel existingModel = sysAgentModelMapper.selectOne(wrapper);
        
        if (existingModel != null) {
            // 存在则更新
            sysAgentModelMapper.updateById(sysAgentModel);
            log.info("更新租户{}的合伙人模型", loginUser.getTenantId());
        } else {
            // 不存在则新增
            sysAgentModel.setCreateTime(new Date());
            sysAgentModelMapper.insert(sysAgentModel);
            log.info("新增租户{}的合伙人模型", loginUser.getTenantId());
        }
        
        return sysAgentModel;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        if (sysAgentModelMapper.deleteById(serialNo) == 0) {
            throw new BusinessException("SYS_AGENT_MODEL_NOT_EXISTS", "合伙人模型不存在");
        }
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public SysAgentModel edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        SysAgentModel sysAgentModel = BsinServiceContext.getReqBodyDto(SysAgentModel.class, requestMap);
        sysAgentModel.setTenantId(loginUser.getTenantId());
        if (sysAgentModelMapper.updateById(sysAgentModel) == 0) {
            throw new BusinessException("SYS_AGENT_MODEL_NOT_EXISTS", "合伙人模型不存在");
        }
        return sysAgentModel;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj = requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj, pagination);
        Page<SysAgentModel> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        
        SysAgentModel sysAgentModel = BsinServiceContext.getReqBodyDto(SysAgentModel.class, requestMap);
        LambdaQueryWrapper<SysAgentModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysAgentModel::getCreateTime);
        wrapper.eq(SysAgentModel::getTenantId, loginUser.getTenantId());
        wrapper.eq(StringUtils.isNotEmpty(sysAgentModel.getModel()), SysAgentModel::getModel, sysAgentModel.getModel());
        
        IPage<SysAgentModel> pageList = sysAgentModelMapper.selectPage(page, wrapper);
        return pageList;
    }

    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public SysAgentModel getDetail(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        LambdaQueryWrapper<SysAgentModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAgentModel::getTenantId, tenantId);
        SysAgentModel sysAgentModel = sysAgentModelMapper.selectOne(wrapper);
        if (sysAgentModel == null) {
            throw new BusinessException("SYS_AGENT_MODEL_NOT_EXISTS", "合伙人模型不存在");
        }
        return sysAgentModel;
    }

}
