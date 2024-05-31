package me.flyray.bsin.server.impl;

import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.Equity;
import me.flyray.bsin.domain.domain.EquityRelation;
import me.flyray.bsin.facade.service.EquityConfigService;
import me.flyray.bsin.infrastructure.mapper.EquityMapper;
import me.flyray.bsin.infrastructure.mapper.EquityRelationMapper;
import me.flyray.bsin.server.utils.RespBodyHandler;

/**
 * @author bolei
 * @date 2023/9/11
 * @desc
 */


@Slf4j
@ShenyuDubboService(path = "/equityConfig", timeout = 6000)
@ApiModule(value = "equityConfig")
@Service
public class EquityConfigServiceImpl implements EquityConfigService {

    @Autowired
    private EquityRelationMapper equityRelationshipMapper;
    @Autowired
    private EquityMapper equityMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public Map<String, Object> add(Map<String, Object> requestMap) {
        EquityRelation equityRelationship = BsinServiceContext.getReqBodyDto(EquityRelation.class, requestMap);
        equityRelationshipMapper.insert(equityRelationship);
        return RespBodyHandler.setRespBodyDto(equityRelationship);
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public Map<String, Object> delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        equityRelationshipMapper.deleteById(serialNo);
        return RespBodyHandler.RespBodyDto();
    }

    @ApiDoc(desc = "getListByCategoryNo")
    @ShenyuDubboClient("/getListByCategoryNo")
    @Override
    public Map<String, Object> getListByCategoryNo(Map<String, Object> requestMap) {
        // 权益分类编号：关联等级 任务 活动的编号
        String categoryNo = MapUtils.getString(requestMap, "categoryNo");
        List<Equity> equityList =  equityMapper.getEquityList(categoryNo);
        return RespBodyHandler.setRespBodyListDto(equityList);
    }

}
