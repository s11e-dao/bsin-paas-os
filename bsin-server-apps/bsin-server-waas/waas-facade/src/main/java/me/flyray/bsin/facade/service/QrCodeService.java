package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/7/18 16:12
 * @desc 商户资产二维码码服务
 */

public interface QrCodeService {

    /**
     * 出码
     */
    Map<String, Object> generate(Map<String, Object> requestMap);

    /**
     * 验码
     */
    Map<String, Object> verify(Map<String, Object> requestMap);

}
