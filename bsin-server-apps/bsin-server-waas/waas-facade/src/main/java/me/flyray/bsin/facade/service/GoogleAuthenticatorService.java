package me.flyray.bsin.facade.service;

import me.flyray.bsin.utils.BsinResultEntity;

import java.util.Map;

public interface GoogleAuthenticatorService {

    String getGoogleAuthToken();

    /**
     * 获取谷歌验证器的二维码
     */
     Map<String,Object> getQrcode(String  customerId);

    /**
     * 验证Google code 是否正确
     * @param username
     * @param code
     * @return
     */
     Map<String,Object> checkCode(String username ,String code);
}
