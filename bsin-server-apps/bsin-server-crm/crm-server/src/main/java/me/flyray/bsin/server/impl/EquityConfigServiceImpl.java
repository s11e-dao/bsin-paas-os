package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Equity;
import me.flyray.bsin.domain.entity.EquityRelation;
import me.flyray.bsin.facade.service.EquityConfigService;
import me.flyray.bsin.infrastructure.mapper.EquityMapper;
import me.flyray.bsin.infrastructure.mapper.EquityRelationMapper;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    public EquityRelation config(Map<String, Object> requestMap) {
        EquityRelation equityRelation = BsinServiceContext.getReqBodyDto(EquityRelation.class, requestMap);
        equityRelationshipMapper.insert(equityRelation);
        return equityRelation;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        equityRelationshipMapper.deleteById(serialNo);
    }

    @ApiDoc(desc = "getListByTypeNo")
    @ShenyuDubboClient("/getListByTypeNo")
    @Override
    public List<?> getListByTypeNo(Map<String, Object> requestMap) {
        // 权益分类编号：关联等级 任务 活动的编号
        String typeNo = MapUtils.getString(requestMap, "typeNo");
        List<Equity> equityList =  equityMapper.getEquityList(typeNo);
        return equityList;
    }

}
