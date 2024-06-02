package me.flyray.bsin.server.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.entity.MerchantApiConsumingRecord;
import me.flyray.bsin.domain.entity.MerchantApiFeeConfig;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.MerchantApiConsumingRecordService;
import me.flyray.bsin.infrastructure.mapper.MerchantApiConsumingRecordMapper;
import me.flyray.bsin.infrastructure.mapper.MerchantApiFeeConfigMapper;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
@ShenyuDubboService(path = "/merchantApiConsumingRecord", timeout = 6000)
@ApiModule(value = "merchantApiConsumingRecord")
@Service
public class MerchantApiConsumingRecordServiceImpl implements MerchantApiConsumingRecordService {

    @Autowired
    private MerchantApiConsumingRecordMapper tenantApiConsumingRecordMapper;
    @Autowired
    private MerchantApiFeeConfigMapper tenantApiFeeConfigMapper;


    /**
     * 1、扣除账户余额
     * 2、添加消费记录
     * 添加消费记录
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "apiConsuming")
    @ShenyuDubboClient("/apiConsuming")
    @Override
    public Map<String, Object> apiConsuming(Map<String, Object> requestMap) {
        // 添加消费记录
        MerchantApiConsumingRecord tenantApiConsumingRecord =
                BsinServiceContext.getReqBodyDto(MerchantApiConsumingRecord.class, requestMap);
        MerchantApiFeeConfig tenantApiFeeConfig = tenantApiFeeConfigMapper.getTenantApiFeeConfig(tenantApiConsumingRecord
                .getProductId(), tenantApiConsumingRecord.getTenantId());
        if (tenantApiFeeConfig == null){
            throw new BusinessException(ResponseCode.APP_NOT_FEE_CONFIG);
        }
        tenantApiConsumingRecord.setFee(tenantApiFeeConfig.getFee());
        // 根据租户ID查询客户信息
        CustomerBase customerBase = null; //customerBaseMapper.getCustomerInfoByTenantIdAndType(
                // tenantApiConsumingRecord.getTenantId(), 1);
        if (customerBase == null){
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_EXISTS);
        }
        if(StringUtils.isBlank(tenantApiFeeConfig.getFee())){
            throw new BusinessException(ResponseCode.FEE_NOT_CONFIG);
        }
//        accountInfoBiz.outAccount(customerBase.getCustomerNo(), "cny", new BigDecimal(tenantApiFeeConfig.getFee()),
//                null,100);
        tenantApiConsumingRecord.setSerialNo(BsinSnowflake.getId());
        tenantApiConsumingRecordMapper.insert(tenantApiConsumingRecord);
        return RespBodyHandler.RespBodyDto();
    }

    /**
     * 分页查询
     *
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        String appId = (String) requestMap.get("appId");
        String tenantId = (String) requestMap.get("tenantId");
        String customerNo = (String) requestMap.get("customerNo");
        String apiName = (String) requestMap.get("apiName");
        PageHelper.startPage((Integer) pagination.get("pageNum"), (Integer) pagination.get("pageSize"));
        List<MerchantApiConsumingRecord> pageList =
                tenantApiConsumingRecordMapper.getPageList(tenantId, appId, apiName, customerNo);
        PageInfo<MerchantApiConsumingRecord> pageInfo = new
                PageInfo<>(pageList);
        return RespBodyHandler.setRespPageInfoBodyDto(pageInfo);
    }

}
