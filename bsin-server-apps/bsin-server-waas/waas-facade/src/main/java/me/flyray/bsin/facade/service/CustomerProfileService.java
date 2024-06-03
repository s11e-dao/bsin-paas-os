package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.CustomerProfile;

import java.util.Map;

/**
* @author leonard
* @description 针对表【da_customer_profile(客户profile画像)】的数据库操作Service
* @createDate 2023-11-14 16:04:25
*/

public interface CustomerProfileService {

    /**
     * 创建Profile
     */
    public Map<String, Object> create(Map<String, Object> requestMap) throws Exception;


    /**
     * 更新Profile basic information
     */
    public Map<String, Object> update(Map<String, Object> requestMap) throws Exception;


    /**
     * collect 资产
     */
    public Map<String, Object> collect(Map<String, Object> requestMap) throws Exception;



    /**
     * follow： validated passCard assets
     */
    public Map<String, Object> follow(Map<String, Object> requestMap) throws Exception;

    /**
     * 删除
     */
    public Map<String, Object> burn(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public Map<String, Object> edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);


    /**
     * 租户下所有
     */
    public IPage<CustomerProfile> getPageList(Map<String, Object> requestMap);

}
