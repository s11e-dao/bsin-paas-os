package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Event;
import me.flyray.bsin.domain.entity.EventModel;
import me.flyray.bsin.domain.entity.Merchant;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.EventModelService;
import me.flyray.bsin.infrastructure.mapper.EventMapper;
import me.flyray.bsin.infrastructure.mapper.EventModelMapper;
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

import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.GRADE_NOT_EXISTS;

@Slf4j
@ShenyuDubboService(path = "/eventModel", timeout = 6000)
@ApiModule(value = "eventModel")
@Service
public class EventModelServiceImpl implements EventModelService {

    @Autowired
    private EventModelMapper eventModelMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public EventModel add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        EventModel eventModel = BsinServiceContext.getReqBodyDto(EventModel.class, requestMap);
        eventModel.setTenantId(loginUser.getTenantId());
        eventModel.setSerialNo(BsinSnowflake.getId());
        eventModelMapper.insert(eventModel);
        return eventModel;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        if (eventModelMapper.deleteById(serialNo) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
    }


    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<EventModel> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        EventModel eventModel = BsinServiceContext.getReqBodyDto(EventModel.class, requestMap);
        LambdaQueryWrapper<EventModel> warapper = new LambdaQueryWrapper<>();
        warapper.eq(EventModel::getTenantId, loginUser.getTenantId());
        IPage<EventModel> pageList = eventModelMapper.selectPage(page, warapper);
        return pageList;
    }

    @Override
    public EventModel getDetail(Map<String, Object> requestMap) {
        String eventCode = MapUtils.getString(requestMap, "eventCode");
        LambdaQueryWrapper<EventModel> warapper = new LambdaQueryWrapper<>();
        warapper.eq(EventModel::getEventCode, eventCode);
        EventModel eventModel = eventModelMapper.selectOne(warapper);
        return eventModel;
    }


}
