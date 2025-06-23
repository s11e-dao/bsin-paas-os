package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisCommissionGoods;

import java.util.Map;

/**
* @author * @author bolei
* @description 针对表【crm_dis_brokerage_goods】的数据库操作Service
* @createDate 2024-10-25 17:13:47
*/
public interface DisCommissionGoodsService {

    /**
     * 添加
     */
    public DisCommissionGoods add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public DisCommissionGoods edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public DisCommissionGoods getDetail(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

}
