package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.BizRoleBill;
import me.flyray.bsin.domain.entity.DisModel;
import me.flyray.bsin.facade.service.CustomerBillService;
import me.flyray.bsin.infrastructure.mapper.BizRoleBillMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@ShenyuDubboService(path = "/customerBill", timeout = 6000)
@ApiModule(value = "customerBill")
@Service
public class CustomerBillServiceImpl implements CustomerBillService {


    @Autowired
    private BizRoleBillMapper bizRoleBillMapper;

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {

        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<BizRoleBill> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        BizRoleBill bizRoleBill = BsinServiceContext.getReqBodyDto(BizRoleBill.class, requestMap);
        LambdaQueryWrapper<BizRoleBill> warapper = new LambdaQueryWrapper<>();
        warapper.eq(BizRoleBill::getTenantId, loginUser.getTenantId());
        // 如果是查询收入，则查询收款方为当前角色
        if ("income".equals(bizRoleBill.getDirection())){
            warapper.eq(BizRoleBill::getPayerNo, loginUser.getBizRoleTypeNo());
        }
        // 如果查询支出，则查询付款方为当前角色
        if ("expense".equals(bizRoleBill.getDirection())){
            warapper.eq(BizRoleBill::getPayeeNo, loginUser.getBizRoleTypeNo());
        }
        IPage<BizRoleBill> pageList = bizRoleBillMapper.selectPage(page, warapper);
        return pageList;

    }

    @ApiDoc(desc = "getHyperledgerSummaryData")
    @ShenyuDubboClient("/getHyperledgerSummaryData")
    @Override
    public Map<String, Object> getHyperledgerSummaryData(Map<String, Object> requestMap) {

        return null;
    }

}
