package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.DisCommissionRule;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DisCommissionRuleService;
import me.flyray.bsin.infrastructure.mapper.DisCommissionRuleMapper;
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
* @description 针对表【crm_dis_brokerage_rule】的数据库操作Service实现
* @createDate 2024-10-25 17:14:01
*/
@Slf4j
@ShenyuDubboService(path = "/disBrokerageRule", timeout = 6000)
@ApiModule(value = "disBrokerageRule")
@Service
public class DisCommissionRuleServiceImpl implements DisCommissionRuleService {

    @Autowired
    private DisCommissionRuleMapper disBrokerageRuleMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public DisCommissionRule add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisCommissionRule disBrokerageRule = BsinServiceContext.getReqBodyDto(DisCommissionRule.class, requestMap);
        // 分佣比例加起来等于100
        if(disBrokerageRule.getFirstSalePer() + disBrokerageRule.getSecondSalePer() != 100){
            throw new BusinessException("999","比例设置不等于100");
        }
        disBrokerageRule.setTenantId(loginUser.getTenantId());
        disBrokerageRule.setSerialNo(BsinSnowflake.getId());
        disBrokerageRuleMapper.insert(disBrokerageRule);
        return disBrokerageRule;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        if (disBrokerageRuleMapper.deleteById(serialNo) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public DisCommissionRule edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisCommissionRule disBrokerageRule = BsinServiceContext.getReqBodyDto(DisCommissionRule.class, requestMap);
        disBrokerageRule.setTenantId(loginUser.getTenantId());
        if (disBrokerageRuleMapper.updateById(disBrokerageRule) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
        return disBrokerageRule;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String brokeragePolicyNo = MapUtils.getString(requestMap, "brokeragePolicyNo");
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<DisCommissionRule> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        DisCommissionRule disBrokerageRule = BsinServiceContext.getReqBodyDto(DisCommissionRule.class, requestMap);
        LambdaQueryWrapper<DisCommissionRule> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(DisCommissionRule::getCreateTime);
        warapper.eq(DisCommissionRule::getTenantId, loginUser.getTenantId());
        warapper.eq(DisCommissionRule::getBrokeragePolicyNo, brokeragePolicyNo);
        IPage<DisCommissionRule> pageList = disBrokerageRuleMapper.selectPage(page, warapper);
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
    public DisCommissionRule getDetail(Map<String, Object> requestMap){
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        DisCommissionRule disBrokerageRule = disBrokerageRuleMapper.selectById(serialNo);
        return disBrokerageRule;
    }

}




