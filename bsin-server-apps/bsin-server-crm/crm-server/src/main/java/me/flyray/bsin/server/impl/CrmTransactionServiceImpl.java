package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Condition;
import me.flyray.bsin.domain.entity.CrmTransaction;
import me.flyray.bsin.enums.TransactionType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.CrmTransactionService;
import me.flyray.bsin.infrastructure.mapper.ConditionMapper;
import me.flyray.bsin.infrastructure.mapper.CrmTransactionMapper;
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

import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.CONDITION_NOT_EXISTS;

@ShenyuDubboService(path = "/transaction", timeout = 6000)
@ApiModule(value = "transaction")
@Service
public class CrmTransactionServiceImpl implements CrmTransactionService {

    @Autowired
    private CrmTransactionMapper crmTransactionMapper;

    @ApiDoc(desc = "pay")
    @ShenyuDubboClient("/pay")
    @Override
    public CrmTransaction pay(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "recharge")
    @ShenyuDubboClient("/recharge")
    @Override
    public CrmTransaction recharge(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "transfer")
    @ShenyuDubboClient("/transfer")
    @Override
    public CrmTransaction transfer(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "withdraw")
    @ShenyuDubboClient("/withdraw")
    @Override
    public CrmTransaction withdraw(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "withdrawApply")
    @ShenyuDubboClient("/withdrawApply")
    @Override
    public CrmTransaction withdrawApply(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "refund")
    @ShenyuDubboClient("/refund")
    @Override
    public CrmTransaction refund(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "settlement")
    @ShenyuDubboClient("/settlement")
    @Override
    public CrmTransaction settlement(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "income")
    @ShenyuDubboClient("/income")
    @Override
    public CrmTransaction income(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "redeem")
    @ShenyuDubboClient("/redeem")
    @Override
    public CrmTransaction redeem(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        CrmTransaction transaction = BsinServiceContext.getReqBodyDto(CrmTransaction.class, requestMap);
        if(TransactionType.getInstanceById(transaction.getTransactionType()) == null){
            throw new BusinessException("999","交易类型不存在！");
        }
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<CrmTransaction> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        LambdaQueryWrapper<CrmTransaction> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(CrmTransaction::getCreateTime);
        warapper.eq(CrmTransaction::getTenantId, loginUser.getTenantId());
        warapper.eq(CrmTransaction::getTransactionType, transaction.getTransactionType());
        IPage<CrmTransaction> pageList = crmTransactionMapper.selectPage(page,warapper);
        return pageList;
    }

    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public CrmTransaction getDetail(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        CrmTransaction crmTransaction = crmTransactionMapper.selectById(serialNo);
        if (crmTransaction == null) {
            throw new BusinessException(CONDITION_NOT_EXISTS);
        }
        return crmTransaction;
    }

}
