package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisPolicyMerchant;

import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_dis_policy_merchant】的数据库操作Service
* @createDate 2024-10-25 17:14:15
*/
public interface DisPolicyMerchantService {

    /**
     * 添加
     */
    public DisPolicyMerchant add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public DisPolicyMerchant edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public DisPolicyMerchant getDetail(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

}
