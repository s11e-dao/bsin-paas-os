package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisCommissionPolicy;

import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_dis_brokerage_policy】的数据库操作Service
* @createDate 2024-10-25 17:13:57
*/
public interface DisCommissionPolicyService {

    /**
     * 添加
     */
    public DisCommissionPolicy add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public DisCommissionPolicy edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public DisCommissionPolicy getDetail(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

}
