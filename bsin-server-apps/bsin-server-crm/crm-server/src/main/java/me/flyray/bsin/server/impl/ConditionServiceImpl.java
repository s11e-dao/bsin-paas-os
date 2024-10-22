package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Condition;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.ConditionService;
import me.flyray.bsin.infrastructure.mapper.ConditionMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.CONDITION_NOT_EXISTS;
import static me.flyray.bsin.constants.ResponseCode.EQUITY_NOT_EXISTS;

/**
 * @author bolei
 * @date 2023/9/10
 * @desc
 */


@ShenyuDubboService(path = "/condition", timeout = 6000)
@ApiModule(value = "condition")
@Service
public class ConditionServiceImpl implements ConditionService {

    @Autowired
    private ConditionMapper conditionMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public Condition add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Condition condition = BsinServiceContext.getReqBodyDto(Condition.class, requestMap);
        if (condition.getTenantId() == null) {
            condition.setTenantId(loginUser.getTenantId());
            if (condition.getTenantId() == null) {
                throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
            }
        }
        if (condition.getMerchantNo() == null) {
            condition.setMerchantNo(loginUser.getMerchantNo());
            if (condition.getMerchantNo() == null) {
                throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
            }
        }
        // TODO 条件的币种账户根据需要商户发行的数字积分的符号来添加，不能直接写死币种
        // typeNo 是数字资产编号 ccyType 不同类型找商户发行的不同币种
        conditionMapper.insert(condition);
        return condition;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        conditionMapper.deleteById(serialNo);
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public void edit(Map<String, Object> requestMap) {
        Condition condition = BsinServiceContext.getReqBodyDto(Condition.class, requestMap);
        conditionMapper.updateById(condition);
    }

    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public Condition getDetail(Map<String, Object> requestMap){
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        Condition condition = conditionMapper.selectById(serialNo);
        if (condition == null) {
            throw new BusinessException(CONDITION_NOT_EXISTS);
        }
        return condition;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Condition condition = BsinServiceContext.getReqBodyDto(Condition.class, requestMap);
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<Condition> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<Condition> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(Condition::getCreateTime);
        warapper.eq(Condition::getTenantId, loginUser.getTenantId());
        warapper.eq(Condition::getMerchantNo, loginUser.getMerchantNo());
        warapper.eq(StringUtils.isNotBlank(condition.getType()), Condition::getType, condition.getType());
        IPage<Condition> pageList = conditionMapper.selectPage(page,warapper);
        return pageList;
    }

}
