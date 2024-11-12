package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.facade.engine.BrokerageEngine;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.flyray.bsin.server.biz.AccountBiz;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ShenyuDubboService(path = "/brokerageEngine", timeout = 6000)
@ApiModule(value = "brokerageEngine")
@Service
@Slf4j
public class BrokerageEngineImpl implements BrokerageEngine {

    @Autowired
    private DisBrokerageJournalMapper disBrokerageJournalMapper;
    @Autowired
    private  CustomerIdentityMapper CustomerIdentityMapper;

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
    /**
     * 事件触发分佣，分佣根据分佣事件点决定是否分佣
     * 1、判断商品对应的商户下是否存在分佣政策(触发事件编码是否一致)
     * 2、存在, 一致则根据分佣时间扔给队列进行分佣（目前是根据时间直接进行分佣）
     * 3、计算商户让利分佣金额
     * 4、根据分佣比例对不同等级的用户按分佣规则配置进行分佣
     * @param requestMap
     */
    @ApiDoc(desc = "execute")
    @ShenyuDubboClient("/execute")
    @Override
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

    public void brokerage(Map<String, Object> requestMap) {

        String eventCode = MapUtils.getString(requestMap, "eventCode");
        String tenantId = MapUtils.getString(requestMap, "tenantId");
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");

        try {
            // 获取请求参数
            String orderNo = MapUtils.getString(requestMap, "orderNo");
            String goodsSkuNo = MapUtils.getString(requestMap, "goodsSkuNo");
            String goodsSkuPayAmount = MapUtils.getString(requestMap, "goodsSkuPayAmount");
            String sysAgentNo = MapUtils.getString(requestMap, "sysAgentNo");
            String goodsCategoryNo = MapUtils.getString(requestMap, "goodsCategoryNo");
            Integer isPreview = MapUtils.getInteger(requestMap, "isPreview");

            // 获取配置
            DisBrokerageConfig disBrokerageConfig = getConfig(requestMap);
            if (disBrokerageConfig == null ) {
                System.out.println("配置未找到，tenantId: {}");
                return;
            }

            // 获取商品的分佣规则
            DisBrokerageRule disBrokerageRule = getBrokerageRule(goodsCategoryNo, merchantNo);
            if (disBrokerageRule == null) {
                System.out.println("分佣规则未找到，goodsCategoryNo: {}, merchantNo: {}");
                return;
            }

            // 获取商户下的分佣政策
            DisBrokeragePolicy disBrokeragePolicy = getBrokeragePolicy(disBrokerageRule.getBrokeragePolicyNo());
            if (disBrokeragePolicy == null || disBrokeragePolicy.getStatus() == 1 || disBrokeragePolicy.getStartTime().after(new java.util.Date()) || disBrokeragePolicy.getEndTime().before(new java.util.Date()) ) {
                System.out.println("过期规则不处理");
                return;
            }

            // 1、判断触发事件编码是否一致, 不一致则直接返回
            if(!disBrokeragePolicy.getTriggerEventCode().equals(eventCode)){
                return;
            }

            // 2、存在, 一致则根据分佣时间扔给队列进行分佣（目前是根据时间直接进行分佣）
            if(disBrokeragePolicy.getTriggerEventAfterDate() > 0){
                // 判断完成事件时间跟当前时间差是否小于triggerEventAfterDate
                // TODO 小于扔给延时队列进行处理

            }

            // 3、计算商户对应商品的让利分佣金额

            BigDecimal merchantGoodsSkuSharingAmount = new BigDecimal(goodsSkuPayAmount).multiply(disBrokerageConfig.getMerchantSharingRate());

            // 4、根据分佣比例对不同等级的用户按分佣规则配置进行分佣
            // 处理一级分佣
            handleFirstLevelBrokerage(
                    disBrokeragePolicy,
                    disBrokerageRule,
                    disBrokerageConfig,
                    merchantGoodsSkuSharingAmount,
                    isPreview,
                    sysAgentNo,
                    requestMap);
            // 处理二级分佣
            handleSecondLevelBrokerage(
                    disBrokeragePolicy,
                    disBrokerageRule,
                    disBrokerageConfig,
                    merchantGoodsSkuSharingAmount,
                    isPreview,
                    sysAgentNo,
                    requestMap
            );
            System.out.println("分佣接口调用成功");
        } catch (Exception e) {
            log.error("分佣接口调用失败");
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

    private void handleFirstLevelBrokerage(DisBrokeragePolicy disBrokeragePolicy, DisBrokerageRule disBrokerageRule, DisBrokerageConfig config, BigDecimal goodsSkuAmount, Integer isPreview, String sysAgentNo, Map<String, Object> requestMap)
        throws UnsupportedEncodingException {


        Integer firstSalePer = disBrokerageRule.getFirstSalePer();
        String parentAgentNo = sysAgentNo;
        if (parentAgentNo == null){
            return;
        }
        DisBrokerageJournal disBrokerageJournal = createBrokerageJournal(
                disBrokeragePolicy,
                disBrokerageRule,
                config,
                goodsSkuAmount,
                1,
                firstSalePer,
                isPreview,
                sysAgentNo,
                requestMap
        );
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

    private void handleSecondLevelBrokerage(DisBrokeragePolicy disBrokeragePolicy, DisBrokerageRule disBrokerageRule, DisBrokerageConfig config, BigDecimal goodsSkuAmount, Integer isPreview, String sysAgentNo,  Map<String, Object> requestMap)
            throws UnsupportedEncodingException {
        Integer secondSalePer = disBrokerageRule.getSecondSalePer();
        String parentAgentNo = getParentAgentNo(sysAgentNo);

        if (secondSalePer != null && parentAgentNo != null) {
            DisBrokerageJournal disBrokerageJournal2 = createBrokerageJournal(
                    disBrokeragePolicy,
                    disBrokerageRule,
                    config,
                    goodsSkuAmount,
                    2,
                    secondSalePer,
                    isPreview,
                    parentAgentNo,
                    requestMap
            );
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

    private DisBrokerageJournal createBrokerageJournal(DisBrokeragePolicy disBrokeragePolicy, DisBrokerageRule disBrokerageRule, DisBrokerageConfig config, BigDecimal goodsSkuAmount, int disLevel, Integer salePer, Integer isPreview, String sysAgentNo, Map<String, Object> requestMap) {
        DisBrokerageJournal disBrokerageJournal = BsinServiceContext.getReqBodyDto(DisBrokerageJournal.class, requestMap);
        disBrokerageJournal.setSerialNo(BsinSnowflake.getId());
        disBrokerageJournal.setTriggerEventCode(disBrokeragePolicy.getTriggerEventCode());
        disBrokerageJournal.setRuleNo(disBrokerageRule.getSerialNo());
        disBrokerageJournal.setPolicyNo(disBrokerageRule.getBrokeragePolicyNo());
        disBrokerageJournal.setExcludeFeeType(disBrokeragePolicy.getExcludeFeeType());
        disBrokerageJournal.setExcludeCustomPer(disBrokeragePolicy.getExcludeCustomPer());
        disBrokerageJournal.setDisLevel(disLevel);
        disBrokerageJournal.setSysAgentRate(config.getSysAgentRate());
        disBrokerageJournal.setDisAmount(goodsSkuAmount.multiply(config.getSysAgentRate()).multiply(new BigDecimal(salePer)).divide(new BigDecimal(100)).divide(new BigDecimal(100)));
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




