package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.BizRoleApp;
import me.flyray.bsin.enums.AppChannel;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.BizRoleAppService;
import me.flyray.bsin.http.HttpUtils;
import me.flyray.bsin.infrastructure.mapper.BizRoleAppMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import me.flyray.bsin.utils.ReqBodyHandler;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.List;

import static me.flyray.bsin.constants.ResponseCode.*;


@Slf4j
@ShenyuDubboService(path = "/bizRoleApp", timeout = 6000)
@ApiModule(value = "bizRoleApp")
@Service
public class BizRoleAppServiceImpl implements BizRoleAppService {

    @Value("${bsin.ai.aesKey}")
    private String aesKey;

    @Value("${bsin.go.base}")
    private String bsinGoBase;

    @Autowired
    private BizRoleAppMapper bizRoleAppMapper;

    /**
     * 添加
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public void add(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        BizRoleApp bizRoleApp = BsinServiceContext.getReqBodyDto(BizRoleApp.class, requestMap);
        String appChannel = (String) requestMap.get("appChannel");
        bizRoleApp.setSerialNo(BsinSnowflake.getId());
        if(!AppChannel.WX_MINIAPP.getType().equals(appChannel) && !AppChannel.WX_MP.getType().equals(appChannel) ){
            String appId = BsinSnowflake.getId();
            bizRoleApp.setAppId(appId);
            bizRoleApp.setAppSecret(BsinSnowflake.getId());
        }
        bizRoleApp.setTenantId(tenantId);
        bizRoleApp.setBizRoleType(LoginInfoContextHelper.getLoginUser().getBizRoleType());
        bizRoleApp.setBizRoleTypeNo(LoginInfoContextHelper.getLoginUser().getBizRoleTypeNo());
        bizRoleAppMapper.insert(bizRoleApp);
//        MerchantApiFeeConfig tenantApiFeeConfig = new MerchantApiFeeConfig();
//        tenantApiFeeConfig.setTenantId(tenantId);
//        tenantApiFeeConfig.setProductId(productId);
//        tenantApiFeeConfig.setSerialNo(BsinSnowflake.getId());
//        tenantApiFeeConfig.setCreateTime(new Date());
//        tenantApiFeeConfig.setStatus("0");
//        // 添加一条app的调用费用配置信息
//        merchantApiFeeConfigMapper.insert(tenantApiFeeConfig);

    }

    /**
     * 删除
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        BizRoleApp bizRoleApp = BsinServiceContext.bisId(BizRoleApp.class, requestMap);
        if (bizRoleAppMapper.deleteById(bizRoleApp.getSerialNo())==0) {
            throw new BusinessException(APP_NOT_EXISTS);
        }
    }

    /**
     * 修改
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public void edit(Map<String, Object> requestMap) {
        BizRoleApp bizRoleApp = BsinServiceContext.bisId(BizRoleApp.class, requestMap);
        String serialNo = (String) requestMap.get("serialNo");
        bizRoleApp.setAppId(serialNo);
        if (bizRoleAppMapper.updateById(bizRoleApp)==0) {
            throw new BusinessException(APP_NOT_EXISTS);
        }
    }

    /**
     * 修改
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public BizRoleApp getDetail(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        String merchantNo = LoginInfoContextHelper.getMerchantNo();
        // 从当前token中获取appId
        String serialNo = (String) requestMap.get("serialNo");
        BizRoleApp tenantAppResult = bizRoleAppMapper.getAppInfo(tenantId, merchantNo, serialNo);
        return tenantAppResult;
    }

    /**
     * 分页查询
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        String bizRoleTypeNo = LoginInfoContextHelper.getLoginUser().getBizRoleTypeNo();
        String appName = (String) requestMap.get("appName");
        String appChannel = (String) requestMap.get("appChannel");
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<BizRoleApp> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<BizRoleApp> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(BizRoleApp::getCreateTime);
        warapper.eq(BizRoleApp::getTenantId, tenantId);
        warapper.eq(BizRoleApp::getBizRoleTypeNo, bizRoleTypeNo);
        warapper.eq(StringUtils.isNotEmpty(appName), BizRoleApp::getAppName, appName);
        warapper.eq(StringUtils.isNotEmpty(appChannel), BizRoleApp::getAppChannel, appChannel);
        IPage<BizRoleApp> pageList = bizRoleAppMapper.selectPage(page,warapper);
        return pageList;
    }


    @ApiDoc(desc = "wechatAgentLogin")
    @ShenyuDubboClient("/wechatAgentLogin")
    @Override
    public BizRoleApp wechatAgentLogin(Map<String, Object> requestMap) throws JsonProcessingException {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");
        if (merchantNo == null) {
            merchantNo = loginUser.getMerchantNo();
//            if (merchantNo == null) {
//                throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
//            }
        }
        String customerNo = MapUtils.getString(requestMap, "customerNo");
        if (customerNo == null) {
            customerNo = loginUser.getCustomerNo();
//            if (customerNo == null) {
//                throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
//            }
        }
        String tenantId = MapUtils.getString(requestMap, "tenantId");
        if (tenantId == null) {
            tenantId = loginUser.getTenantId();
            if (tenantId == null) {
                throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
            }
        }
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        String operation = MapUtils.getString(requestMap, "operation");
        BizRoleApp bizRoleApp = bizRoleAppMapper.selectById(serialNo);
        if (bizRoleApp == null) {
            throw new BusinessException("100000", "未找到微信机器人ID:" + serialNo);
        }

        if (!AppChannel.WX_WECHAT.getType().equals(bizRoleApp.getAppChannel())) {
            throw new BusinessException("100000", "not support operation!!!");
        }

        try {
//            String exceptionRespStr = bizRoleApp.getExceptionResp();
            String exceptionRespStr = "True";
            String exceptionResp = "false";
            if (StringUtils.isNotBlank(exceptionRespStr)) {
                exceptionResp = "true";
            }

            JSONObject bizParamsObject = new JSONObject();
            bizParamsObject.put("tenantId", tenantId);
            if (merchantNo != null){
                bizParamsObject.put("merchantNo", merchantNo);
            }
            if (customerNo != null){
                bizParamsObject.put("customerNo", customerNo);
            }
            bizParamsObject.put("name", bizRoleApp.getAppName());
            bizParamsObject.put("wxNo", bizRoleApp.getAppId());
            bizParamsObject.put("bizRoleAppNo", serialNo);
//            bizParamsObject.put("preResp", bizRoleApp.getPreResp());
//            bizParamsObject.put("groupChat", bizRoleApp.getGroupChat().toString());
//            bizParamsObject.put("historyChatSummary", bizRoleApp.getHistoryChatSummary().toString());
            bizParamsObject.put("exceptionResp", exceptionResp);
            bizParamsObject.put("copilotNo", bizRoleApp.getAgentId());
//            bizParamsObject.put("requestIntervalLimit", bizRoleApp.getRequestIntervalLimit().toString());
            bizParamsObject.put("operation", operation); // 登录： loginWechat  退出： !loginWechat
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//            bizParamsObject.put("expirationTime", sdf.format(validFunction.getExpirationTime()));
            ReqBodyHandler reqBody =
                    ReqBodyHandler.builder()
                            .serviceName("Wechat")
                            .methodName("login")
                            .version("1.0")
                            .bizParams(bizParamsObject)
                            .build();
            // 创建ObjectMapper对象
            ObjectMapper objectMapper = new ObjectMapper();
            String req = objectMapper.writeValueAsString(reqBody);
            String ret = HttpUtils.postJson(bsinGoBase + "/wechat/login", req);
            JSONObject objectRet = JSONObject.parseObject(ret);
            if (objectRet.isEmpty()) {
                throw new BusinessException("100000", "ai-go-wechat interface exception!!!");
            }
            if ((int) objectRet.get("code") != 0) {
                throw new BusinessException("100000", (String) objectRet.get("message"));
            }
            bizRoleApp.setNotifyUrl(objectRet.getString("loginQrUrl"));
            return bizRoleApp;
        } catch (Exception e) {
            throw new BusinessException("100000", e.toString());
        }
    }

    @ApiDoc(desc = "updateWechatLoginStatus")
    @ShenyuDubboClient("/updateWechatLoginStatus")
    @Override
    public BizRoleApp updateWechatLoginStatus(Map<String, Object> requestMap) throws JsonProcessingException {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");
        if (merchantNo == null) {
            merchantNo = loginUser.getMerchantNo();
            if (merchantNo == null) {
                throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
            }
        }
        String appId = MapUtils.getString(requestMap, "appId");
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        String appStatus = MapUtils.getString(requestMap, "appStatus");
        if (serialNo == null) {
            throw new BusinessException("100000", "Invalid serialNo");
        }
        BizRoleApp bizRoleApp = BsinServiceContext.getReqBodyDto(BizRoleApp.class, requestMap);
        bizRoleApp.setSerialNo(serialNo);
        bizRoleAppMapper.updateById(bizRoleApp);
        return bizRoleApp;
    }

    @ApiDoc(desc = "getWechatLoginList")
    @ShenyuDubboClient("/getWechatLoginList")
    @Override
    public List<?> getWechatLoginList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");
        if (merchantNo == null) {
            merchantNo = loginUser.getMerchantNo();
            if (merchantNo == null) {
                throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
            }
        }
        String customerNo = MapUtils.getString(requestMap, "customerNo");
        String tenantId = loginUser.getTenantId();
        try {
            JSONObject bizParamsObject = new JSONObject();
            bizParamsObject.put("tenantId", tenantId);
            bizParamsObject.put("merchantNo", merchantNo);
            bizParamsObject.put("customerNo", customerNo);
            ReqBodyHandler reqBody =
                    ReqBodyHandler.builder()
                            .serviceName("Wechat")
                            .methodName("monitor")
                            .version("1.0")
                            .bizParams(bizParamsObject)
                            .build();
            log.info("reqBody: {}", reqBody);
            // 创建ObjectMapper对象
            ObjectMapper objectMapper = new ObjectMapper();
            String req = objectMapper.writeValueAsString(reqBody);
            String ret = HttpUtils.postJson(bsinGoBase + "/wechat/monitor", req);
            JSONObject objectRet = JSONObject.parseObject(ret);
            if (objectRet.isEmpty()) {
                throw new BusinessException("100000", "ai-go-wechat interface exception!!!");
            }
            if ((int) objectRet.get("code") != 0) {
                throw new BusinessException("100000", (String) objectRet.get("message"));
            }
            return (List<?>) objectRet.get("wechatBotMonitorInfo");
        } catch (Exception e) {
            throw new BusinessException("100000", e.toString());
        }
    }
}
