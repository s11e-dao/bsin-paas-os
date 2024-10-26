package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.DisBrokerageRule;
import me.flyray.bsin.domain.entity.PayChannelConfig;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DisBrokerageRuleService;
import me.flyray.bsin.infrastructure.mapper.DisBrokerageRuleMapper;
import me.flyray.bsin.infrastructure.mapper.DisModelMapper;
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
public class DisBrokerageRuleServiceImpl implements DisBrokerageRuleService {

    @Autowired
    private DisBrokerageRuleMapper disBrokerageRuleMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public DisBrokerageRule add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisBrokerageRule disBrokerageRule = BsinServiceContext.getReqBodyDto(DisBrokerageRule.class, requestMap);
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
    public DisBrokerageRule edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisBrokerageRule disBrokerageRule = BsinServiceContext.getReqBodyDto(DisBrokerageRule.class, requestMap);
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
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<DisBrokerageRule> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        DisBrokerageRule disBrokerageRule = BsinServiceContext.getReqBodyDto(DisBrokerageRule.class, requestMap);
        LambdaQueryWrapper<DisBrokerageRule> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(DisBrokerageRule::getCreateTime);
        warapper.eq(DisBrokerageRule::getTenantId, loginUser.getTenantId());
        IPage<DisBrokerageRule> pageList = disBrokerageRuleMapper.selectPage(page, warapper);
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
    public DisBrokerageRule getDetail(Map<String, Object> requestMap){
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        DisBrokerageRule disBrokerageRule = disBrokerageRuleMapper.selectById(serialNo);
        return disBrokerageRule;
    }

}




