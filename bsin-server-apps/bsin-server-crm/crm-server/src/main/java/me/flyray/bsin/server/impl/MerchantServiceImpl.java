package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.facade.service.MerchantService;

/**
 * @author bolei
 * @date 2023/6/28 16:41
 * @desc
 */

@ShenyuDubboService(path = "/merchant", timeout = 6000)
@ApiModule(value = "merchant")
@Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @ApiDoc(desc = "register")
    @ShenyuDubboClient("/register")
    @Override
    public Map<String, Object> register(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> login(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> authentication(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> audit(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> subscribeFunction(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getMerchantCustomerInfoByUsername(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> delete(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> edit(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getListByMerchantNos(Map<String, Object> requestMap) {
        return null;
    }

}
