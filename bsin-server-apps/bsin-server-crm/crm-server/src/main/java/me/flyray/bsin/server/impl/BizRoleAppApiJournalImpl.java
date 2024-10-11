package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.entity.BizRoleAppApiJournal;
import me.flyray.bsin.domain.entity.BizRoleAppApiFeeConfig;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.BizRoleAppApiJournalService;
import me.flyray.bsin.infrastructure.mapper.BizRoleApiConsumingRecordMapper;
import me.flyray.bsin.infrastructure.mapper.BizRoleAppApiFeeConfigMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@ShenyuDubboService(path = "/merchantApiConsumingRecord", timeout = 6000)
@ApiModule(value = "merchantApiConsumingRecord")
@Service
public class BizRoleAppApiJournalImpl implements BizRoleAppApiJournalService {

    @Autowired
    private BizRoleApiConsumingRecordMapper tenantApiConsumingRecordMapper;
    @Autowired
    private BizRoleAppApiFeeConfigMapper tenantApiFeeConfigMapper;


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
    public void apiConsuming(Map<String, Object> requestMap) {
        // 添加消费记录
        BizRoleAppApiJournal tenantApiConsumingRecord =
                BsinServiceContext.getReqBodyDto(BizRoleAppApiJournal.class, requestMap);
        BizRoleAppApiFeeConfig tenantApiFeeConfig = tenantApiFeeConfigMapper.getTenantApiFeeConfig(tenantApiConsumingRecord
                .getAppId(), tenantApiConsumingRecord.getTenantId());
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
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        String appId = (String) requestMap.get("appId");
        String tenantId = (String) requestMap.get("tenantId");
        String customerNo = (String) requestMap.get("customerNo");
        String apiName = (String) requestMap.get("apiName");
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<BizRoleAppApiJournal> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        LambdaQueryWrapper<BizRoleAppApiJournal> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(BizRoleAppApiJournal::getCreateTime);
        warapper.eq(BizRoleAppApiJournal::getTenantId, loginUser.getTenantId());
        warapper.eq(StringUtils.isNotEmpty(appId),
                BizRoleAppApiJournal::getAppId, appId);
        warapper.eq(StringUtils.isNotEmpty(apiName),
                BizRoleAppApiJournal::getApiName, apiName);
        warapper.eq(StringUtils.isNotEmpty(customerNo),
                BizRoleAppApiJournal::getCustomerNo, customerNo);
        IPage<BizRoleAppApiJournal> pageList = tenantApiConsumingRecordMapper.selectPage(page, warapper);
        return pageList;
    }

}
