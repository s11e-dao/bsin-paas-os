package me.flyray.bsin.facade.service;

import me.flyray.bsin.facade.response.DigitalAssetsDetailRes;
import me.flyray.bsin.facade.response.DigitalAssetsItemRes;

import java.util.List;
import java.util.Map;

/**
* @author bolei
* @description 针对表【da_customer_digital_assets】的数据库操作Service
* @createDate 2023-07-19 14:57:09
*/

public interface CustomerDigitalAssetsService {

    /**
     * 客户数字资产列表
     */
    public List<DigitalAssetsItemRes> getList(Map<String, Object> requestMap);


    /**
     * 分页查询数字资产
     */
    public List<DigitalAssetsItemRes> getPageList(Map<String, Object> requestMap);


    /**
     * 查询数字资产详情
     */
    public DigitalAssetsDetailRes getDetail(Map<String, Object> requestMap);


    /**
     * 客户数字资产验证
     */
    public Map<String, Object> verifyAssetsOnChain(Map<String, Object> requestMap);

}
