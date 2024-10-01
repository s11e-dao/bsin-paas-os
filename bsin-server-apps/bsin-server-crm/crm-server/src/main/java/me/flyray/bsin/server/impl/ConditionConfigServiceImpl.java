package me.flyray.bsin.server.impl;

import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Condition;
import me.flyray.bsin.domain.entity.ConditionRelation;
import me.flyray.bsin.facade.service.ConditionConfigService;
import me.flyray.bsin.infrastructure.mapper.ConditionMapper;
import me.flyray.bsin.infrastructure.mapper.ConditionRelationMapper;
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

@ShenyuDubboService(path = "/conditionConfig", timeout = 6000)
@ApiModule(value = "conditionConfig")
@Service
public class ConditionConfigServiceImpl implements ConditionConfigService {

    @Autowired
    private ConditionRelationMapper conditionRelationshipMapper;
    @Autowired
    private ConditionMapper conditionMapper;

    @ApiDoc(desc = "config")
    @ShenyuDubboClient("/config")
    @Override
    public ConditionRelation config(Map<String, Object> requestMap) {
        ConditionRelation conditionRelation = BsinServiceContext.getReqBodyDto(ConditionRelation.class, requestMap);
        conditionRelationshipMapper.insert(conditionRelation);
        return conditionRelation;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        conditionRelationshipMapper.deleteById(serialNo);
    }

    @ApiDoc(desc = "getListByCategoryNo")
    @ShenyuDubboClient("/getListByCategoryNo")
    @Override
    public List<?> getListByCategoryNo(Map<String, Object> requestMap) {
        // 权益分类编号：关联等级 任务 活动的编号
        String categoryNo = MapUtils.getString(requestMap, "categoryNo");
        List<Condition> conditionList =  conditionMapper.getConditionList(categoryNo);
        return conditionList;
    }

}
