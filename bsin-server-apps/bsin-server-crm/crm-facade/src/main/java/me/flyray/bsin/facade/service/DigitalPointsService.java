package me.flyray.bsin.facade.service;

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
    Map<String, Object> issue(Map<String, Object> requestMap) throws Exception;


    /**
     * 查询商户数字积分信息
     */
    Map<String, Object> getDetailByMerchantNo(Map<String, Object> requestMap);



    /**
     * 铸造数字积分
     */
    Map<String, Object> mint(Map<String, Object> requestMap) throws Exception;

}
