package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Condition;
import me.flyray.bsin.domain.entity.Equity;
import me.flyray.bsin.domain.entity.Event;
import me.flyray.bsin.domain.entity.Grade;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.GradeVO;
import me.flyray.bsin.facade.service.EventService;
import me.flyray.bsin.infrastructure.mapper.EquityMapper;
import me.flyray.bsin.infrastructure.mapper.EventMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.GRADE_NOT_EXISTS;

@Slf4j
@ShenyuDubboService(path = "/event", timeout = 6000)
@ApiModule(value = "event")
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventMapper eventMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public Event add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Event event = BsinServiceContext.getReqBodyDto(Event.class, requestMap);
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
    public Event edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Event event = BsinServiceContext.getReqBodyDto(Event.class, requestMap);
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
        Page<Event> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        Event event = BsinServiceContext.getReqBodyDto(Event.class, requestMap);
        LambdaQueryWrapper<Event> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(Event::getCreateTime);
        warapper.eq(Event::getTenantId, loginUser.getTenantId());
        IPage<Event> pageList = eventMapper.selectPage(page, warapper);
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
    public Event getDetail(Map<String, Object> requestMap){
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        Event event = eventMapper.selectById(serialNo);
        return event;
    }

}
