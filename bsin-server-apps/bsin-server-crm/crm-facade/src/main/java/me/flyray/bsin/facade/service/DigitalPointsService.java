package me.flyray.bsin.facade.service;

import me.flyray.bsin.domain.entity.DigitalAssetsCollection;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/7/3 15:46
 * @desc
 */

public interface DigitalPointsService {

    /**
     * 发行
     * 1、开通数字积分相关账户
     * 2、部署智能合约
     */
    void issue(Map<String, Object> requestMap) throws Exception;


    /**
     * 查询商户数字积分信息
     */
    DigitalAssetsCollection getDetailByMerchantNo(Map<String, Object> requestMap);



    /**
     * 铸造数字积分
     */
    void mint(Map<String, Object> requestMap) throws Exception;

}
