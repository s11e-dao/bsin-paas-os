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

}
