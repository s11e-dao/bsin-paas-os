package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.entity.MerchantPayEntry;
import me.flyray.bsin.domain.request.MerchantDTO;
import me.flyray.bsin.dubbo.invoke.BsinServiceInvoke;
import me.flyray.bsin.facade.service.MerchantPayEntryService;
import me.flyray.bsin.infrastructure.mapper.MerchantPayEntryMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections4.MapUtils;

@Slf4j
@ShenyuDubboService(path = "/merchantPayEntry", timeout = 6000)
@ApiModule(value = "merchantPayEntry")
@Service
public class MerchantPayEntryServiceImpl implements MerchantPayEntryService {

    @Autowired
    private BsinServiceInvoke bsinServiceInvoke;
    @Autowired
    private MerchantPayEntryMapper merchantPayEntryMapper;

    /**
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "apply")
    @ShenyuDubboClient("/apply")
    @Override
    public Map<String, Object> apply(Map<String, Object> requestMap) {
        log.info("开始处理商户支付进件申请，请求参数：{}", requestMap);
        
        try {
            // 1. 验证请求参数
            validateRequestParams(requestMap);
            
            // 2. 调用 WaaS 模块的进件服务
            Object result = bsinServiceInvoke.genericInvoke("PayMerchantEntryService", "apply", "dev", requestMap);
            
            // 3. 处理返回结果
            Map<String, Object> entryResult = (Map<String, Object>) result;;

            // 4. 保存进件记录到数据库
            MerchantPayEntry payEntry = saveMerchantPayEntry(requestMap, entryResult);
            
            // 5. 返回完整结果
            Map<String, Object> finalResult = new HashMap<>(entryResult);
            finalResult.put("serialNo", payEntry.getSerialNo());
            
            log.info("商户支付进件申请成功，序列号：{}", payEntry.getSerialNo());
            return finalResult;
            
        } catch (Exception e) {
            log.error("商户支付进件申请失败：{}", e.getMessage(), e);
            throw new RuntimeException("商户支付进件申请失败：" + e.getMessage());
        }
    }

    @ApiDoc(desc = "getApplyStatus")
    @ShenyuDubboClient("/getApplyStatus")
    @Override
    public Map<String, Object> getApplyStatus(Map<String, Object> requestMap) {
        log.info("开始查询商户支付进件状态，请求参数：{}", requestMap);
        
        // 1. 验证请求参数
        String serialNo = MapUtils.getString(requestMap, "serialNo");

        // 2. 查询数据库中的进件记录
        MerchantPayEntry payEntry = merchantPayEntryMapper.selectBySerialNo(serialNo);
        if (payEntry == null) {
            log.error("未找到对应的进件记录，序列号：{}", serialNo);
            throw new RuntimeException("未找到对应的进件记录");
        }

        // 3. 构建查询参数
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("payChannel", payEntry.getChannel());
        queryParams.put("applymentId", payEntry.getApplymentId());

        // 4. 调用 WaaS 模块的进件状态查询服务
        Object result = bsinServiceInvoke.genericInvoke("PayMerchantEntryService", "getApplyStatus", "dev", queryParams);

        // 5. 处理返回结果
        Map<String, Object> statusResult = (Map<String, Object>) result;

        // 6. 更新数据库中的状态
        payEntry.setStatus((String) statusResult.get("status"));
        payEntry.setResponseJson(com.alibaba.fastjson.JSONObject.toJSONString(statusResult));
        payEntry.setUpdateBy(LoginInfoContextHelper.getLoginUser().getUserId());
        payEntry.setUpdateTime(new Date());

        merchantPayEntryMapper.updateById(payEntry);
        log.info("商户支付进件状态更新成功，序列号：{}，状态：{}", serialNo, payEntry.getStatus());

        // 7. 返回完整结果
        Map<String, Object> finalResult = new HashMap<>(statusResult);
        finalResult.put("serialNo", serialNo);
        finalResult.put("businessCode", payEntry.getBusinessCode());
        finalResult.put("channel", payEntry.getChannel());

        log.info("商户支付进件状态查询成功，序列号：{}", serialNo);
        return finalResult;

    }


    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public MerchantDTO getDetail(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<MerchantDTO> getPageList(Map<String, Object> requestMap) {
        try {
            String tenantId = LoginInfoContextHelper.getTenantId();
            
            // 处理分页参数
            Object paginationObj = requestMap.get("pagination");
            Pagination pagination = new Pagination();
            if (paginationObj != null) {
                BeanUtil.copyProperties(paginationObj, pagination);
            } else {
                // 默认分页参数
                pagination.setPageNum(1);
                pagination.setPageSize(10);
            }
            
            // 验证分页参数
            if (pagination.getPageNum() < 1) {
                pagination.setPageNum(1);
            }
            if (pagination.getPageSize() < 1 || pagination.getPageSize() > 100) {
                pagination.setPageSize(10);
            }
            
            Page<CustomerBase> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
            IPage<MerchantDTO> pageList = merchantPayEntryMapper.selectPageList(page, tenantId);
            
            log.info("查询商户支付进件列表成功，总数：{}，当前页：{}", pageList.getTotal(), pagination.getPageNum());
            return pageList;
            
        } catch (Exception e) {
            log.error("查询商户支付进件列表失败：{}", e.getMessage(), e);
            throw new RuntimeException("查询商户支付进件列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 保存商户支付进件记录
     */
    private MerchantPayEntry saveMerchantPayEntry(Map<String, Object> requestMap, Map<String, Object> entryResult) {
        try {
            MerchantPayEntry payEntry = new MerchantPayEntry();
            payEntry.setSerialNo(BsinSnowflake.getId());
            payEntry.setTenantId(LoginInfoContextHelper.getTenantId());
            payEntry.setMerchantNo(LoginInfoContextHelper.getLoginUser().getMerchantNo());
            payEntry.setChannel((String) requestMap.get("payChannel"));
            payEntry.setRequestJson(com.alibaba.fastjson.JSONObject.toJSONString(requestMap));
            payEntry.setApplymentId((String) entryResult.get("applymentId"));
            payEntry.setBusinessCode((String) requestMap.get("businessCode"));
            payEntry.setStatus((String) entryResult.get("status"));
            payEntry.setResponseJson(com.alibaba.fastjson.JSONObject.toJSONString(entryResult));
            payEntry.setCreateBy(LoginInfoContextHelper.getLoginUser().getUserId());
            payEntry.setCreateTime(new Date());
            payEntry.setDelFlag(0);
            
            // 验证必填字段
            if (StringUtils.isBlank(payEntry.getChannel())) {
                throw new RuntimeException("支付渠道不能为空");
            }
            if (StringUtils.isBlank(payEntry.getApplymentId())) {
                throw new RuntimeException("申请单号不能为空");
            }
            if (StringUtils.isBlank(payEntry.getBusinessCode())) {
                throw new RuntimeException("业务申请编号不能为空");
            }
            
            merchantPayEntryMapper.insert(payEntry);
            log.info("商户支付进件记录保存成功，序列号：{}", payEntry.getSerialNo());
            
            return payEntry;
            
        } catch (Exception e) {
            log.error("保存商户支付进件记录失败：{}", e.getMessage(), e);
            throw new RuntimeException("保存商户支付进件记录失败：" + e.getMessage());
        }
    }

    /**
     * 验证请求参数
     */
    private void validateRequestParams(Map<String, Object> requestMap) {
        if (requestMap == null) {
            throw new RuntimeException("请求参数不能为空");
        }
        
        String payChannel = (String) requestMap.get("payChannel");
        if (StringUtils.isBlank(payChannel)) {
            throw new RuntimeException("支付渠道不能为空");
        }
        
        String businessCode = (String) requestMap.get("businessCode");
        if (StringUtils.isBlank(businessCode)) {
            throw new RuntimeException("业务申请编号不能为空");
        }
        
        // 验证商户信息
        @SuppressWarnings("unchecked")
        Map<String, Object> merchantInfo = (Map<String, Object>) requestMap.get("merchantInfo");
        if (merchantInfo == null || StringUtils.isBlank((String) merchantInfo.get("merchantName"))) {
            throw new RuntimeException("商户信息不能为空");
        }
        
        // 验证联系人信息
        @SuppressWarnings("unchecked")
        Map<String, Object> contactInfo = (Map<String, Object>) requestMap.get("contactInfo");
        if (contactInfo == null || StringUtils.isBlank((String) contactInfo.get("name")) || 
            StringUtils.isBlank((String) contactInfo.get("mobile"))) {
            throw new RuntimeException("联系人信息不完整");
        }
        
        log.info("请求参数验证通过");
    }
}
