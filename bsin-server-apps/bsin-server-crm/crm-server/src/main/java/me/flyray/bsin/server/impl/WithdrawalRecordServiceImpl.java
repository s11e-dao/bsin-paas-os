package me.flyray.bsin.server.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.WithdrawalRecord;
import me.flyray.bsin.domain.enums.WithdrawalAuditStatus;
import me.flyray.bsin.domain.enums.WithdrawalStatus;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.WithdrawalRecordService;
import me.flyray.bsin.infrastructure.mapper.WithdrawalRecordMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.DATA_BASE_UPDATE_FAILED;
import static me.flyray.bsin.constants.ResponseCode.SYS_AGENT_NOT_EXISTS;

@Slf4j
@ShenyuDubboService(path = "/withdrawal", timeout = 15000)
@ApiModule(value = "withdrawal")
@Service
public class WithdrawalRecordServiceImpl implements WithdrawalRecordService {

    @Autowired
    private WithdrawalRecordMapper withdrawalRecordMapper;

    /**
     * 生成提现记录
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "apply")
    @ShenyuDubboClient("/apply")
    @Override
    public WithdrawalRecord apply(Map<String, Object> requestMap) {
        String tenantId = MapUtils.getString(requestMap, "tenantId");
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        if (StringUtils.isEmpty(tenantId)){
            tenantId = LoginInfoContextHelper.getTenantId();
        }
        WithdrawalRecord withdrawalRecord = BsinServiceContext.getReqBodyDto(WithdrawalRecord.class, requestMap);
        withdrawalRecord.setTenantId(tenantId);
        withdrawalRecordMapper.insert(withdrawalRecord);
        return withdrawalRecord;
    }

    /**
     * 审核通过之后代付打款
     * @param requestMap
     */
    @ApiDoc(desc = "audit")
    @ShenyuDubboClient("/audit")
    @Override
    public void audit(Map<String, Object> requestMap) {
        // 获取当前登录用户信息
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        // 转换请求参数为实体对象
        WithdrawalRecord withdrawalRecord = BsinServiceContext.getReqBodyDto(WithdrawalRecord.class, requestMap);
        String newAuditStatus = withdrawalRecord.getAuditStatus();
        // 设置审核相关信息
        withdrawalRecord.setAuditTime(new Date());
        
        // 根据审核结果设置处理状态
        if (WithdrawalAuditStatus.APPROVED.getCode().equals(newAuditStatus)) {
            // 审核通过，设置为处理中
            withdrawalRecord.setStatus(WithdrawalStatus.PROCESSING.getCode());
            // TODO 调用waas模块打款处理

        } else if (WithdrawalAuditStatus.REJECTED.getCode().equals(newAuditStatus)) {
            // 审核拒绝，设置为已取消
            withdrawalRecord.setStatus(WithdrawalStatus.CANCELLED.getCode());
        }
        
        // 更新记录
        if (withdrawalRecordMapper.updateById(withdrawalRecord) == 0) {
            throw new BusinessException(DATA_BASE_UPDATE_FAILED);
        }
        
        log.info("提现记录审核完成，记录ID: {}, 审核状态: {}, 审核人: {}", 
                withdrawalRecord.getSerialNo(), 
                loginUser.getUsername());
    }

    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public WithdrawalRecord getDetail(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        WithdrawalRecord withdrawalRecord = withdrawalRecordMapper.selectById(serialNo);
        if (withdrawalRecord == null) {
            throw new BusinessException(SYS_AGENT_NOT_EXISTS);
        }
        return withdrawalRecord;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        WithdrawalRecord withdrawalRecord = BsinServiceContext.getReqBodyDto(WithdrawalRecord.class, requestMap);
        String tenantId = withdrawalRecord.getTenantId();
        String merchantNo = LoginInfoContextHelper.getMerchantNo();
        if (tenantId == null) {
            tenantId = LoginInfoContextHelper.getTenantId();
        }
        Object paginationObj = requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj, pagination);
        Page<WithdrawalRecord> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        LambdaQueryWrapper<WithdrawalRecord> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(WithdrawalRecord::getCreateTime);
        warapper.eq(StringUtils.isNotEmpty(tenantId), WithdrawalRecord::getTenantId, tenantId);
        warapper.eq(
                StringUtils.isNotEmpty(withdrawalRecord.getStatus()), WithdrawalRecord::getStatus, withdrawalRecord.getStatus());
        IPage<WithdrawalRecord> pageList = withdrawalRecordMapper.selectPage(page, warapper);
        return pageList;
    }

}
