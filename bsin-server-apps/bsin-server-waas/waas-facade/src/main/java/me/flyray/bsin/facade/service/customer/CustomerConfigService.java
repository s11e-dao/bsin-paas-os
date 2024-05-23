package me.flyray.bsin.facade.service.customer;

import me.flyray.bsin.domain.entity.customer.CustomerConfig;
import me.flyray.bsin.utils.BsinResultEntity;

public interface CustomerConfigService {

    void save(CustomerConfig customerConfig);

    /**
     * 查询商户配置
     * @param customerConfig
     * @return
     * @
     */
    CustomerConfig getCustomerConfig(CustomerConfig customerConfig) ;

    /**
     * 设置商户配置
     * @param customerConfig
     * @return
     * @
     */
    void update(CustomerConfig customerConfig) ;

}
