package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisCommissionConfig;

import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_dis_brokerage_config(参与分佣设置表)】的数据库操作Service
* @createDate 2024-10-25 17:13:34
*/

public interface DisCommissionConfigService {

    /**
     * 添加
     */
    public DisCommissionConfig config(DisCommissionConfig disBrokerageConfig);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public DisCommissionConfig edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public DisCommissionConfig getDetail(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);


    DisCommissionConfig settingSharingRate(Map<String, Object> requestMap);
}
