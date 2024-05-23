package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.ValidateService;
import me.flyray.bsin.infrastructure.biz.SmsBiz;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;


@Slf4j
@DubboService
@ApiModule(value = "validate")
@ShenyuDubboService("/validate")
public class ValidateServiceImpl implements ValidateService {
    @Autowired
    private SmsBiz smsBiz;

    @Override
    @ApiDoc(desc = "sendValidateCode")
    @ShenyuDubboClient("/sendValidateCode")
    public Map<String, Object> sendValidateCode(int sendType) {
        log.debug("请求TransactionService.createTransaction,参数:{}", sendType);
        try{
            LoginUser user = LoginInfoContextHelper.getLoginUser();
            Map<String, Object> map = smsBiz.sendValidateCode(user.getPhone(), sendType);
            return map;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("短信发送失败");
        }
    }
}
