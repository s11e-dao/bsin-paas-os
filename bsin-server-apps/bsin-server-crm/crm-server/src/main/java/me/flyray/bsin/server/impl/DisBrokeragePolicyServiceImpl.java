package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.DisBrokeragePolicy;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DisBrokeragePolicyService;
import me.flyray.bsin.infrastructure.mapper.DisBrokeragePolicyMapper;
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
* @description 针对表【crm_dis_brokerage_policy】的数据库操作Service实现
* @createDate 2024-10-25 17:13:57
*/
@Slf4j
@ShenyuDubboService(path = "/disBrokeragePolicy", timeout = 6000)
@ApiModule(value = "disBrokeragePolicy")
@Service
public class DisBrokeragePolicyServiceImpl implements DisBrokeragePolicyService {

    @Autowired
    private DisBrokeragePolicyMapper disBrokeragePolicyMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public DisBrokeragePolicy add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisBrokeragePolicy disBrokeragePolicy = BsinServiceContext.getReqBodyDto(DisBrokeragePolicy.class, requestMap);
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
    public DisBrokeragePolicy edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisBrokeragePolicy disBrokeragePolicy = BsinServiceContext.getReqBodyDto(DisBrokeragePolicy.class, requestMap);
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
        Page<DisBrokeragePolicy> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        DisBrokeragePolicy disBrokeragePolicy = BsinServiceContext.getReqBodyDto(DisBrokeragePolicy.class, requestMap);
        LambdaQueryWrapper<DisBrokeragePolicy> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(DisBrokeragePolicy::getCreateTime);
        warapper.eq(DisBrokeragePolicy::getTenantId, loginUser.getTenantId());
        IPage<DisBrokeragePolicy> pageList = disBrokeragePolicyMapper.selectPage(page, warapper);
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
    public DisBrokeragePolicy getDetail(Map<String, Object> requestMap){
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        DisBrokeragePolicy disBrokeragePolicy = disBrokeragePolicyMapper.selectById(serialNo);
        return disBrokeragePolicy;
    }

}




