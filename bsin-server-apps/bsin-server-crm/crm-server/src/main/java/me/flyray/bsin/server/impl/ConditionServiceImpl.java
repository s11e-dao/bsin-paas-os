package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.Condition;
import me.flyray.bsin.facade.service.ConditionService;
import me.flyray.bsin.infrastructure.mapper.ConditionMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;

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
    public Map<String, Object> add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Condition condition = BsinServiceContext.getReqBodyDto(Condition.class, requestMap);
        condition.setTenantId(loginUser.getTenantId());
        condition.setMerchantNo(loginUser.getMerchantNo());
        // TODO 条件的币种账户根据需要商户发行的数字积分的符号来添加，不能直接写死币种
        // typeNo 是数字资产编号 ccyType 不同类型找商户发行的不同币种
        conditionMapper.insert(condition);
        return RespBodyHandler.setRespBodyDto(condition);
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public Map<String, Object> delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        conditionMapper.deleteById(serialNo);
        return RespBodyHandler.RespBodyDto();
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public Map<String, Object> edit(Map<String, Object> requestMap) {
        Condition condition = BsinServiceContext.getReqBodyDto(Condition.class, requestMap);
        conditionMapper.updateById(condition);
        return RespBodyHandler.RespBodyDto();
    }

    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap){
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        Condition condition =
                conditionMapper.selectById(serialNo);
        return RespBodyHandler.setRespBodyDto(condition);
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Condition condition = BsinServiceContext.getReqBodyDto(Condition.class, requestMap);
        Pagination pagination = (Pagination) requestMap.get("pagination");
        Page<Condition> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<Condition> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(Condition::getCreateTime);
        warapper.eq(Condition::getTenantId, loginUser.getTenantId());
        warapper.eq(Condition::getMerchantNo, loginUser.getMerchantNo());
        warapper.eq(StringUtils.isNotBlank(condition.getType()), Condition::getType, condition.getType());
        IPage<Condition> pageList = conditionMapper.selectPage(page,warapper);
        return RespBodyHandler.setRespPageInfoBodyDto(pageList);
    }

}
