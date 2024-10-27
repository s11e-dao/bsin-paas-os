package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DisBrokerageJournalService;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.math3.util.Decimal64;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.flyray.bsin.infrastructure.mapper.CustomerIdentityMapper;

import java.math.BigDecimal;
import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.GRADE_NOT_EXISTS;

/**
* @author bolei
* @description 针对表【crm_dis_brokerage_journal】的数据库操作Service实现
* @createDate 2024-10-25 17:13:52
*/
@Slf4j
@ShenyuDubboService(path = "/disBrokerageJournal", timeout = 6000)
@ApiModule(value = "disBrokerageJournal")
@Service
public class DisBrokerageJournalServiceImpl implements DisBrokerageJournalService {

    @Autowired
    private DisBrokerageJournalMapper disBrokerageJournalMapper;
    @Autowired
    private CustomerIdentityMapper CustomerIdentityMapper;

    @Autowired
    private DisBrokerageRuleMapper disBrokerageRuleMapper;
    @Autowired
    private DisBrokeragePolicyMapper disBrokeragePolicyMapper;

    @Autowired
    private DisTeamRelationMapper disTeamRelationMapper;

    @Autowired
    private DisBrokerageConfigMapper disBrokerageConfigMapper;


    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public DisBrokerageJournal add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisBrokerageJournal disBrokerageJournal = BsinServiceContext.getReqBodyDto(DisBrokerageJournal.class, requestMap);
        disBrokerageJournal.setSerialNo(BsinSnowflake.getId());
        disBrokerageJournalMapper.insert(disBrokerageJournal);
        return disBrokerageJournal;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        if (disBrokerageJournalMapper.deleteById(serialNo) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<DisBrokerageJournal> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        DisBrokerageJournal disBrokerageJournal = BsinServiceContext.getReqBodyDto(DisBrokerageJournal.class, requestMap);
        LambdaQueryWrapper<DisBrokerageJournal> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(DisBrokerageJournal::getCreateTime);
        IPage<DisBrokerageJournal> pageList = disBrokerageJournalMapper.selectPage(page, warapper);
        return pageList;
    }


    /**
     * 事件详情
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public DisBrokerageJournal getDetail(Map<String, Object> requestMap){
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        DisBrokerageJournal disBrokerageJournal = disBrokerageJournalMapper.selectById(serialNo);
        return disBrokerageJournal;
    }

    @Override
    @ApiDoc(desc = "分佣")
    @ShenyuDubboClient("/brokerage")
    public void brokerage(Map<String, Object> requestMap) {
        String orderNo = MapUtils.getString(requestMap, "orderNo");
        String goodSkuNo = MapUtils.getString(requestMap, "goodSkuNo");
        String goodsSkuAmount = MapUtils.getString(requestMap, "goodsSkuAmount");
        String sysAgentNo = MapUtils.getString(requestMap, "sysAgentNo");
        String goodNo = MapUtils.getString(requestMap, "goodNo");
        String goodsCategoryNo = MapUtils.getString(requestMap, "goodsCategoryNo");
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");
        DisBrokerageConfig config = disBrokerageConfigMapper.selectOne(
                new LambdaQueryWrapper<DisBrokerageConfig>()
                        .eq(DisBrokerageConfig::getTenantId,  MapUtils.getString(requestMap, "tenantId"))
        );
        if (config == null){
            return;
        }
        DisBrokerageRule disBrokerageRule = disBrokerageRuleMapper.selectOne(
                new LambdaQueryWrapper<DisBrokerageRule>()
                        .eq(DisBrokerageRule::getGoodsCategoryNo, goodsCategoryNo)
                        .eq(DisBrokerageRule::getMerchantNo, merchantNo)
        );
        if (disBrokerageRule == null){
            return;
        }else{
            DisBrokeragePolicy disBrokeragePolicy = disBrokeragePolicyMapper.selectOne(
                    new LambdaQueryWrapper<DisBrokeragePolicy>()
                            .eq(DisBrokeragePolicy::getSerialNo, disBrokerageRule.getBrokeragePolicyNo())

            );
            Integer firstSalePer = disBrokerageRule.getFirstSalePer();
            Integer secondSalePer = disBrokerageRule.getSecondSalePer();
            String customerNo1 = getCustomerNo(sysAgentNo);
            String parentAgentNo = disTeamRelationMapper.selectOne(
                    new LambdaQueryWrapper<DisTeamRelation>()
                            .eq(DisTeamRelation::getSysAgentNo, sysAgentNo)
            ).getPrarentSysAgentNo();
            DisBrokerageJournal disBrokerageJournal = BsinServiceContext.getReqBodyDto(DisBrokerageJournal.class, requestMap);
            disBrokerageJournal.setSerialNo(BsinSnowflake.getId());
            disBrokerageJournal.setBrokeragePoint(disBrokeragePolicy.getBrokeragePoint());
            disBrokerageJournal.setRuleNo(disBrokerageRule.getSerialNo());
            disBrokerageJournal.setPolicyNo(disBrokerageRule.getBrokeragePolicyNo());
            disBrokerageJournal.setExcludeFeeType(disBrokeragePolicy.getExcludeFeeType());
            disBrokerageJournal.setExcludeCustomPer(disBrokeragePolicy.getExcludeCustomPer());
            disBrokerageJournal.setDisLevel(1);
            disBrokerageJournal.setSysAgentRate(config.getSysAgentRate());
            disBrokerageJournal.setDisAmount(new BigDecimal(goodsSkuAmount).multiply(config.getSysAgentRate()).multiply(new BigDecimal(firstSalePer)).divide(new BigDecimal(100)).divide(new BigDecimal(100)) );
//            disBrokerageJournal.setSysAgentNo(sysAgentNo);
            disBrokerageJournalMapper.insert(disBrokerageJournal);
            if (secondSalePer != null && parentAgentNo != null){
                DisBrokerageJournal disBrokerageJournal2 = BsinServiceContext.getReqBodyDto(DisBrokerageJournal.class, requestMap);
                disBrokerageJournal2.setSerialNo(BsinSnowflake.getId());
                disBrokerageJournal2.setBrokeragePoint(disBrokeragePolicy.getBrokeragePoint());
                disBrokerageJournal2.setRuleNo(disBrokerageRule.getSerialNo());
                disBrokerageJournal2.setPolicyNo(disBrokerageRule.getBrokeragePolicyNo());
                disBrokerageJournal2.setExcludeFeeType(disBrokeragePolicy.getExcludeFeeType());
                disBrokerageJournal2.setExcludeCustomPer(disBrokeragePolicy.getExcludeCustomPer());
                disBrokerageJournal2.setDisLevel(2);
                disBrokerageJournal2.setSysAgentRate(config.getSysAgentRate());
                disBrokerageJournal2.setDisAmount(new BigDecimal(goodsSkuAmount).multiply(config.getSysAgentRate()).multiply(new BigDecimal(secondSalePer)).divide(new BigDecimal(100)).divide(new BigDecimal(100)) );
                disBrokerageJournalMapper.insert(disBrokerageJournal2);
            }





        }
        System.out.println("分佣接口调用成功");


    };
    public String getCustomerNo(String sysAgentNo){
        return CustomerIdentityMapper.selectOne(
                new LambdaQueryWrapper<CustomerIdentity>()
                        .eq(CustomerIdentity::getBizRoleTypeNo, sysAgentNo)
        ).getCustomerNo();
    }



}




