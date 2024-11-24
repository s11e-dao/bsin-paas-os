package me.flyray.bsin.server.impl;

import me.flyray.bsin.domain.entity.CrmTransaction;
import me.flyray.bsin.facade.service.CrmTransactionService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Service;

import java.util.Map;

@ShenyuDubboService(path = "/transaction", timeout = 6000)
@ApiModule(value = "transaction")
@Service
public class CrmTransactionServiceImpl implements CrmTransactionService {


    @Override
    public CrmTransaction pay(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public CrmTransaction recharge(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public CrmTransaction transfer(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public CrmTransaction withdraw(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public CrmTransaction refund(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public CrmTransaction settlement(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public CrmTransaction income(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public CrmTransaction redeem(Map<String, Object> requestMap) {
        return null;
    }
}
