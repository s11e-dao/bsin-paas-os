package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisBrokerageGoods;

import java.util.Map;

/**
* @author * @author bolei
* @description 针对表【crm_dis_brokerage_goods】的数据库操作Service
* @createDate 2024-10-25 17:13:47
*/
public interface DisBrokerageGoodsService {

    /**
     * 添加
     */
    public DisBrokerageGoods add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public DisBrokerageGoods edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public DisBrokerageGoods getDetail(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

}
