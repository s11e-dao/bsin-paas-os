package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.DisCommissionPolicy;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DisCommissionPolicyService;
import me.flyray.bsin.infrastructure.mapper.DisCommissionPolicyMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.GRADE_NOT_EXISTS;

/**
* @author bolei
* @description 针对表【crm_dis_brokerage_policy】的数据库操作Service实现
* @createDate 2024-10-25 17:13:57
*/
@Slf4j
@ShenyuDubboService(path = "/disBrokeragePolicy", timeout = 6000)
@ApiModule(value = "disBrokeragePolicy")
@Service
public class DisCommissionPolicyServiceImpl implements DisCommissionPolicyService {

    @Autowired
    private DisCommissionPolicyMapper disBrokeragePolicyMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public DisCommissionPolicy add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisCommissionPolicy disBrokeragePolicy = BsinServiceContext.getReqBodyDto(DisCommissionPolicy.class, requestMap);
        disBrokeragePolicy.setTenantId(loginUser.getTenantId());
        disBrokeragePolicy.setSerialNo(BsinSnowflake.getId());
        disBrokeragePolicy.setCreateBy(loginUser.getUserId());
        disBrokeragePolicyMapper.insert(disBrokeragePolicy);
        return disBrokeragePolicy;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        if (disBrokeragePolicyMapper.deleteById(serialNo) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public DisCommissionPolicy edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisCommissionPolicy disBrokeragePolicy = BsinServiceContext.getReqBodyDto(DisCommissionPolicy.class, requestMap);
        disBrokeragePolicy.setTenantId(loginUser.getTenantId());
        if (disBrokeragePolicyMapper.updateById(disBrokeragePolicy) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
        return disBrokeragePolicy;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<DisCommissionPolicy> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        DisCommissionPolicy disBrokeragePolicy = BsinServiceContext.getReqBodyDto(DisCommissionPolicy.class, requestMap);
        LambdaQueryWrapper<DisCommissionPolicy> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(DisCommissionPolicy::getCreateTime);
        warapper.eq(DisCommissionPolicy::getTenantId, loginUser.getTenantId());
        IPage<DisCommissionPolicy> pageList = disBrokeragePolicyMapper.selectPage(page, warapper);
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
    public DisCommissionPolicy getDetail(Map<String, Object> requestMap){
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        DisCommissionPolicy disBrokeragePolicy = disBrokeragePolicyMapper.selectById(serialNo);
        return disBrokeragePolicy;
    }

}




