package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisBrokerageRule;

import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_dis_brokerage_rule】的数据库操作Service
* @createDate 2024-10-25 17:14:01
*/
public interface DisBrokerageRuleService {

    /**
     * 添加
     */
    public DisBrokerageRule add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public DisBrokerageRule edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public DisBrokerageRule getDetail(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

}
