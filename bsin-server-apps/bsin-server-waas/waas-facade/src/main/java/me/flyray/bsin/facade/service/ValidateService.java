package me.flyray.bsin.facade.service;

import java.util.Map;

public interface ValidateService {

    /**
     * 发送短信验证码
     * @param sendType
     * @return
     */
    Map<String,Object> sendValidateCode(int sendType);

}
