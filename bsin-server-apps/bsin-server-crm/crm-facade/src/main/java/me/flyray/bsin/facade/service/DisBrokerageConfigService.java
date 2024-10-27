package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisBrokerageConfig;
import me.flyray.bsin.domain.entity.DisModel;

import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_dis_brokerage_config(参与分佣设置表)】的数据库操作Service
* @createDate 2024-10-25 17:13:34
*/
public interface DisBrokerageConfigService {

    /**
     * 添加
     */
    public DisBrokerageConfig add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public DisBrokerageConfig edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public DisBrokerageConfig getDetail(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);


    DisBrokerageConfig update(Map<String, Object> requestMap);
}
