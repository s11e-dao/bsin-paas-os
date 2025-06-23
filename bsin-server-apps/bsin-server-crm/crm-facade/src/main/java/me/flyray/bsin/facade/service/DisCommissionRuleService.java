package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisCommissionRule;

import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_dis_brokerage_rule】的数据库操作Service
* @createDate 2024-10-25 17:14:01
*/
public interface DisCommissionRuleService {

    /**
     * 添加
     */
    public DisCommissionRule add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public DisCommissionRule edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public DisCommissionRule getDetail(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

}
