package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.BsinEvent;
import me.flyray.bsin.domain.entity.BsinEventModel;
import me.flyray.bsin.domain.entity.Equity;
import me.flyray.bsin.domain.enums.AccountCategory;
import me.flyray.bsin.domain.enums.CcyType;
import me.flyray.bsin.enums.TransactionType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.engine.EventServiceEngine;
import me.flyray.bsin.facade.service.AccountService;
import me.flyray.bsin.facade.service.EventService;
import me.flyray.bsin.infrastructure.mapper.EquityMapper;
import me.flyray.bsin.infrastructure.mapper.EventMapper;
import me.flyray.bsin.infrastructure.mapper.EventModelMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.GRADE_NOT_EXISTS;

@Slf4j
@ShenyuDubboService(path = "/event", timeout = 6000)
@ApiModule(value = "event")
@Service
public class EventServiceImpl implements EventService, EventServiceEngine {

    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private EventModelMapper eventModelMapper;
    @Autowired
    private EquityMapper equityMapper;
    @Autowired
    private AccountService accountService;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public BsinEvent add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        BsinEvent event = BsinServiceContext.getReqBodyDto(BsinEvent.class, requestMap);
        event.setTenantId(loginUser.getTenantId());
        event.setSerialNo(BsinSnowflake.getId());
        eventMapper.insert(event);
        return event;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        if (eventMapper.deleteById(serialNo) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public BsinEvent edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        BsinEvent event = BsinServiceContext.getReqBodyDto(BsinEvent.class, requestMap);
        event.setTenantId(loginUser.getTenantId());
        if (eventMapper.updateById(event) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
        return event;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<BsinEvent> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        BsinEvent event = BsinServiceContext.getReqBodyDto(BsinEvent.class, requestMap);
        LambdaQueryWrapper<BsinEvent> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(BsinEvent::getCreateTime);
        warapper.eq(BsinEvent::getTenantId, loginUser.getTenantId());
        IPage<BsinEvent> pageList = eventMapper.selectPage(page, warapper);
        return pageList;
    }

    @ApiDoc(desc = "modelConfig")
    @ShenyuDubboClient("/modelConfig")
    @Override
    public BsinEventModel modelConfig(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        BsinEventModel eventModel = BsinServiceContext.getReqBodyDto(BsinEventModel.class, requestMap);
        eventModel.setTenantId(loginUser.getTenantId());
        eventModel.setSerialNo(BsinSnowflake.getId());
        eventModelMapper.insert(eventModel);
        return eventModel;
    }

    @ApiDoc(desc = "getModelConfig")
    @ShenyuDubboClient("/getModelConfig")
    @Override
    public BsinEventModel getModelConfig(Map<String, Object> requestMap) {
        String eventCode = MapUtils.getString(requestMap, "eventCode");
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        LambdaQueryWrapper<BsinEventModel> warapper = new LambdaQueryWrapper<>();
        warapper.eq(BsinEventModel::getTenantId, loginUser.getTenantId());
        warapper.eq(BsinEventModel::getEventCode, eventCode);
        BsinEventModel eventModel = eventModelMapper.selectOne(warapper);
        return eventModel;
    }

    /**
     * 事件详情
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public BsinEvent getDetail(Map<String, Object> requestMap){
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        BsinEvent event = eventMapper.selectById(serialNo);
        return event;
    }

    @ApiDoc(desc = "execute")
    @ShenyuDubboClient("/execute")
    @Override
    public void execute(Map<String, Object> requestMap){
        // 根据事件code查询事件权益
        String eventCode = MapUtils.getString(requestMap, "eventCode");
        String tenantId = MapUtils.getString(requestMap, "tenantId");
        String bizRoleType = MapUtils.getString(requestMap, "bizRoleType");
        String bizRoleTypeNo = MapUtils.getString(requestMap, "bizRoleTypeNo");
        // 查询出事件
        BsinEvent event = eventMapper.selectOne(new LambdaQueryWrapper<BsinEvent>().eq(BsinEvent::getEventCode, eventCode));

        // 根据事件查询权益
        List<Equity> equityList =  equityMapper.getEquityList(event.getSerialNo());

        // 根据权益类型进行权益发放
        for (Equity equity : equityList) {

            switch (equity.getType()) {
                case "POINTS":
                    // 1.权益类型为积分，执行权益发放，调用crm进行积分入账
                    java.util.Map<String, Object> reqMap = new java.util.HashMap<>();
                    reqMap.put("tenantId",tenantId);
                    reqMap.put("bizRoleType", bizRoleType);
                    reqMap.put("bizRoleTypeNo", bizRoleTypeNo);
                    reqMap.put("ccy", CcyType.CNY.getCode());
                    reqMap.put("category", AccountCategory.CONTRIBUTION_VALUE.getCode());
                    reqMap.put("amount", equity.getValue());
                    reqMap.put("decimals","1");
                    reqMap.put("transactionType", TransactionType.REWARD.getCode());
                    reqMap.put("remark", "注册送积分");
                    // 调用CRM服务进行充值
                    try {
                        accountService.inAccount(reqMap);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "2":
                    // 2.权益类型为2，执行权益发放
                    break;
                case "3":
                    // 3.权益类型为3，执行权益发放
                    break;
                default:
                    break;
            }

        }

    }

}
