package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.DisBrokerageConfig;
import me.flyray.bsin.domain.entity.DisModel;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DisBrokerageConfigService;
import me.flyray.bsin.infrastructure.mapper.DisBrokerageConfigMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.GRADE_NOT_EXISTS;

/**
* @author bolei
* @description 针对表【crm_dis_brokerage_config(参与分佣设置表)】的数据库操作Service实现
* @createDate 2024-10-25 17:13:34
*/
@Slf4j
@ShenyuDubboService(path = "/disBrokerageConfig", timeout = 6000)
@ApiModule(value = "disBrokerageConfig")
@Service
public class DisBrokerageConfigServiceImpl implements DisBrokerageConfigService {

    @Autowired
    private DisBrokerageConfigMapper disBrokerageConfigMapper;

    @ApiDoc(desc = "config")
    @ShenyuDubboClient("/config")
    @Override
    public DisBrokerageConfig config(DisBrokerageConfig disBrokerageConfig) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        disBrokerageConfig.setTenantId(loginUser.getTenantId());
        disBrokerageConfig.setSerialNo(BsinSnowflake.getId());
        // 分佣比例加起来等于100
        BigDecimal totalRate = BigDecimal.ZERO.add(disBrokerageConfig.getSuperTenantRate()).add(disBrokerageConfig.getTenantRate()).add(disBrokerageConfig.getSysAgentRate()).add(disBrokerageConfig.getCustomerRate());
        int totalRateResult = totalRate.compareTo(new BigDecimal(100));
        if(totalRateResult != 0){
            throw new BusinessException("999","比例设置不等于100");
        }
        // 构建查询条件
        LambdaQueryWrapper<DisBrokerageConfig> wrapper = new LambdaQueryWrapper<DisBrokerageConfig>()
                .eq(DisBrokerageConfig::getTenantId, loginUser.getTenantId());
        // 查询记录
        DisBrokerageConfig existingDisBrokerageConfig = disBrokerageConfigMapper.selectOne(wrapper);
        if (existingDisBrokerageConfig == null) {
            // 记录不存在，插入新记录
            disBrokerageConfigMapper.insert(disBrokerageConfig);
        } else {
            // 记录存在，更新记录
            disBrokerageConfigMapper.updateById(disBrokerageConfig);
        }
        return disBrokerageConfig;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        if (disBrokerageConfigMapper.deleteById(serialNo) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public DisBrokerageConfig edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        // 将请求参数转换为 DisModel 对象
        DisBrokerageConfig disbrokerageconfig = BsinServiceContext.getReqBodyDto(DisBrokerageConfig.class, requestMap);
        disbrokerageconfig.setTenantId(loginUser.getTenantId());
        // 构建查询条件
        LambdaQueryWrapper<DisBrokerageConfig> wrapper = new LambdaQueryWrapper<DisBrokerageConfig>()
                .eq(DisBrokerageConfig::getTenantId, loginUser.getTenantId());
        // 查询记录
        DisBrokerageConfig existingModel = disBrokerageConfigMapper.selectOne(wrapper);

        if (existingModel == null) {
            // 记录不存在，插入新记录
            disBrokerageConfigMapper.insert(disbrokerageconfig);
        } else {
            // 记录存在，更新记录
            disbrokerageconfig.setTenantId(existingModel.getTenantId());
            disBrokerageConfigMapper.updateById(disbrokerageconfig);
        }
        return disbrokerageconfig;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<DisBrokerageConfig> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        DisBrokerageConfig disBrokerageConfig = BsinServiceContext.getReqBodyDto(DisBrokerageConfig.class, requestMap);
        LambdaQueryWrapper<DisBrokerageConfig> warapper = new LambdaQueryWrapper<>();
        warapper.eq(DisBrokerageConfig::getTenantId, loginUser.getTenantId());
        IPage<DisBrokerageConfig> pageList = disBrokerageConfigMapper.selectPage(page, warapper);
        return pageList;
    }

    /**
     * 事件详情
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public DisBrokerageConfig getDetail(Map<String, Object> requestMap){
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String tenantId = loginUser.getTenantId();
        DisBrokerageConfig disBrokerageConfig = disBrokerageConfigMapper.selectById(tenantId);
        return disBrokerageConfig;
    }

    /**
     * 商户设置份润比例
     * @param requestMap
     * @return
     */
    @Override
    @ApiDoc(desc = "settingSharingRate")
    @ShenyuDubboClient("/settingSharingRate")
    public DisBrokerageConfig settingSharingRate(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        if(!BizRoleType.MERCHANT.getCode().equals(loginUser.getBizRoleType())){
            throw new BusinessException("999","非商户用户无法设置让利比例");
        }
        DisBrokerageConfig disbrokerageconfig = BsinServiceContext.getReqBodyDto(DisBrokerageConfig.class, requestMap);
        disbrokerageconfig.setTenantId(loginUser.getTenantId());
        disbrokerageconfig.setMerchantNo(loginUser.getMerchantNo());
        disBrokerageConfigMapper.updateById(disbrokerageconfig);
        return disbrokerageconfig;
    }

}




