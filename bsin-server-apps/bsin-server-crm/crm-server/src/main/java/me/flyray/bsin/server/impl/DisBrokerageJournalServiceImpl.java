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
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.flyray.bsin.infrastructure.mapper.CustomerIdentityMapper;
import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.flyray.bsin.server.biz.AccountBiz;

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

    @Autowired
    private AccountBiz accountBiz;

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
    @ShenyuDubboClient("/brokerage/all")
    public void execute(Map<String, Object> requestMap) {

            // 检查 goodsSkuList 是否为 null 或者不是 List 类型
        List<Map<String, Object>> goodsSkuListObj = (List<Map<String, Object>>) requestMap.get("goodsSkuList");
            if (goodsSkuListObj != null) {
                for (Map<String, Object> skuMap : goodsSkuListObj) {
                    skuMap.putAll(requestMap);

                    brokerage(skuMap);
                }
            } else {
                System.out.println("goodsSkuList 不是 List 类型");
            }
        }

    @Override
    @ApiDoc(desc = "分佣")
    @ShenyuDubboClient("/brokerage")
    public void brokerage(Map<String, Object> requestMap) {
        try {
            // 获取请求参数
            String orderNo = MapUtils.getString(requestMap, "orderNo");
            String goodsSkuNo = MapUtils.getString(requestMap, "goodsSkuNo");
            String goodsSkuAmount = MapUtils.getString(requestMap, "goodsSkuAmount");
            String sysAgentNo = MapUtils.getString(requestMap, "sysAgentNo");
            String goodsCategoryNo = MapUtils.getString(requestMap, "goodsCategoryNo");
            String merchantNo = MapUtils.getString(requestMap, "merchantNo");
            Integer idPreview = MapUtils.getInteger(requestMap, "isPreview");
            // 获取配置
            DisBrokerageConfig config = getConfig(requestMap);
            if (config == null ) {
                System.out.println("配置未找到，tenantId: {}");
                return;
            }

            // 获取分佣规则
            DisBrokerageRule disBrokerageRule = getBrokerageRule(goodsCategoryNo, merchantNo);
            if (disBrokerageRule == null) {
                System.out.println("分佣规则未找到，goodsCategoryNo: {}, merchantNo: {}");
                return;
            }

            // 获取分佣策略
            DisBrokeragePolicy disBrokeragePolicy = getBrokeragePolicy(disBrokerageRule.getBrokeragePolicyNo());

            if (disBrokeragePolicy == null || disBrokeragePolicy.getStatus() == 0 || disBrokeragePolicy.getStartTime().after(new java.util.Date()) || disBrokeragePolicy.getEndTime().before(new java.util.Date()) ) {
                System.out.println("过期规则不处理");
                return;
            }

            // 处理一级分佣
            handleFirstLevelBrokerage(disBrokeragePolicy, disBrokerageRule, config, goodsSkuAmount, sysAgentNo,idPreview, requestMap);

            // 处理二级分佣
            handleSecondLevelBrokerage(disBrokeragePolicy, disBrokerageRule, config, goodsSkuAmount, sysAgentNo,idPreview, requestMap);

            System.out.println("分佣接口调用成功");
        } catch (Exception e) {
            System.out.println("分佣接口调用失败");
        }
    }

    private DisBrokerageConfig getConfig(Map<String, Object> requestMap) {
        return disBrokerageConfigMapper.selectOne(
                new LambdaQueryWrapper<DisBrokerageConfig>()
                        .eq(DisBrokerageConfig::getTenantId, MapUtils.getString(requestMap, "tenantId"))
        );
    }

    private DisBrokerageRule getBrokerageRule(String goodsCategoryNo, String merchantNo) {
        return disBrokerageRuleMapper.selectOne(
                new LambdaQueryWrapper<DisBrokerageRule>()
                        .eq(DisBrokerageRule::getGoodsCategoryNo, goodsCategoryNo)
                        .eq(DisBrokerageRule::getMerchantNo, merchantNo)
        );
    }

    private DisBrokeragePolicy getBrokeragePolicy(String brokeragePolicyNo) {
        return disBrokeragePolicyMapper.selectOne(
                new LambdaQueryWrapper<DisBrokeragePolicy>()
                        .eq(DisBrokeragePolicy::getSerialNo, brokeragePolicyNo)
        );
    }

    private void handleFirstLevelBrokerage(DisBrokeragePolicy disBrokeragePolicy, DisBrokerageRule disBrokerageRule, DisBrokerageConfig config, String goodsSkuAmount, String sysAgentNo, Integer isPreview, Map<String, Object> requestMap)
        throws UnsupportedEncodingException {
        Integer firstSalePer = disBrokerageRule.getFirstSalePer();
        String parentAgentNo = sysAgentNo;
        if (parentAgentNo == null){
            return;
        }
        DisBrokerageJournal disBrokerageJournal = createBrokerageJournal(disBrokeragePolicy, disBrokerageRule, config, goodsSkuAmount, 1, firstSalePer, sysAgentNo, isPreview, requestMap);
        disBrokerageJournalMapper.insert(disBrokerageJournal);
        CustomerIdentity identity = CustomerIdentityMapper.selectOne(
                new LambdaQueryWrapper<CustomerIdentity>()
                        .eq(CustomerIdentity::getBizRoleTypeNo, parentAgentNo
                        )
        );
        if (identity == null){
            return;
        }
        if (isPreview==0){
            accountBiz.inAccount(
                    identity.getTenantId(),
                    identity.getCustomerNo(),
                    "4",
                    "预分佣账户",
                    "cny",
                    2,
                    disBrokerageJournal.getDisAmount()
            );
        }else{
            accountBiz.outAccount(
                    identity.getTenantId(),
                    identity.getCustomerNo(),
                    "4",
                    "预分佣账户",
                    "cny",
                    2,
                    disBrokerageJournal.getDisAmount().negate()
            );
            accountBiz.inAccount(
                    identity.getTenantId(),
                    identity.getCustomerNo(),
                    "4",
                    "分佣账户",
                    "cny",
                    2,
                    disBrokerageJournal.getDisAmount()
            );
        }
    }


    private void handleSecondLevelBrokerage(DisBrokeragePolicy disBrokeragePolicy, DisBrokerageRule disBrokerageRule, DisBrokerageConfig config, String goodsSkuAmount, String sysAgentNo, Integer isPreview, Map<String, Object> requestMap)
            throws UnsupportedEncodingException {
        Integer secondSalePer = disBrokerageRule.getSecondSalePer();
        String parentAgentNo = getParentAgentNo(sysAgentNo);

        if (secondSalePer != null && parentAgentNo != null) {
            DisBrokerageJournal disBrokerageJournal2 = createBrokerageJournal(disBrokeragePolicy, disBrokerageRule, config, goodsSkuAmount, 2, secondSalePer, parentAgentNo, isPreview, requestMap);
            disBrokerageJournalMapper.insert(disBrokerageJournal2);
            CustomerIdentity identity = CustomerIdentityMapper.selectOne(
                    new LambdaQueryWrapper<CustomerIdentity>()
                            .eq(CustomerIdentity::getBizRoleTypeNo, parentAgentNo
                            )
            );
            if (isPreview==0){
                accountBiz.inAccount(
                        identity.getTenantId(),
                        identity.getCustomerNo(),
                        "4",
                        "预分佣账户",
                        "cny",
                        2,
                        disBrokerageJournal2.getDisAmount()
                );
            }else{
                accountBiz.outAccount(
                        identity.getTenantId(),
                        identity.getCustomerNo(),
                        "4",
                        "预分佣账户",
                        "cny",
                        2,
                        disBrokerageJournal2.getDisAmount().negate()
                );
                accountBiz.inAccount(
                        identity.getTenantId(),
                        identity.getCustomerNo(),
                        "4",
                        "分佣账户",
                        "cny",
                        2,
                        disBrokerageJournal2.getDisAmount()
                );
            }

        }
    }

    private DisBrokerageJournal createBrokerageJournal(DisBrokeragePolicy disBrokeragePolicy, DisBrokerageRule disBrokerageRule, DisBrokerageConfig config, String goodsSkuAmount, int disLevel, Integer salePer, String sysAgentNo,Integer isPreview, Map<String, Object> requestMap) {
        DisBrokerageJournal disBrokerageJournal = BsinServiceContext.getReqBodyDto(DisBrokerageJournal.class, requestMap);
        disBrokerageJournal.setSerialNo(BsinSnowflake.getId());
        disBrokerageJournal.setBrokeragePoint(disBrokeragePolicy.getBrokeragePoint());
        disBrokerageJournal.setRuleNo(disBrokerageRule.getSerialNo());
        disBrokerageJournal.setPolicyNo(disBrokerageRule.getBrokeragePolicyNo());
        disBrokerageJournal.setExcludeFeeType(disBrokeragePolicy.getExcludeFeeType());
        disBrokerageJournal.setExcludeCustomPer(disBrokeragePolicy.getExcludeCustomPer());
        disBrokerageJournal.setDisLevel(disLevel);
        disBrokerageJournal.setSysAgentRate(config.getSysAgentRate());
        disBrokerageJournal.setDisAmount(new BigDecimal(goodsSkuAmount).multiply(config.getSysAgentRate()).multiply(new BigDecimal(salePer)).divide(new BigDecimal(100)).divide(new BigDecimal(100)));
        disBrokerageJournal.setSysAgentNo(sysAgentNo);
        disBrokerageJournal.setIsPreview(isPreview);
        return disBrokerageJournal;
    }

    private String getParentAgentNo(String sysAgentNo) {
        DisTeamRelation teamRelation = disTeamRelationMapper.selectOne(
                new LambdaQueryWrapper<DisTeamRelation>()
                        .eq(DisTeamRelation::getSysAgentNo, sysAgentNo)
        );
        return teamRelation != null ? teamRelation.getPrarentSysAgentNo() : null;
    }




}




