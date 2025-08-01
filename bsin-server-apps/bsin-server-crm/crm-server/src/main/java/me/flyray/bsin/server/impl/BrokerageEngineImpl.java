package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.domain.enums.AccountCategory;
import me.flyray.bsin.dubbo.invoke.BsinServiceInvoke;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.engine.BrokerageServiceEngine;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import me.flyray.bsin.server.biz.AccountBiz;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@ShenyuDubboService(path = "/brokerageEngine", timeout = 6000)
@ApiModule(value = "brokerageEngine")
@Service
public class BrokerageEngineImpl implements BrokerageServiceEngine {

    @Autowired private BsinServiceInvoke bsinServiceInvoke;
    @Value("${bsin.supersTenantId}")
    private String supersTenantId;
    @Autowired
    private DisCommissionJournalMapper disBrokerageJournalMapper;
    @Autowired
    private CustomerIdentityMapper customerIdentityMapper;
    @Autowired
    private DisCommissionRuleMapper disBrokerageRuleMapper;
    @Autowired
    private DisCommissionPolicyMapper disBrokeragePolicyMapper;
    @Autowired
    private DisTeamRelationMapper disTeamRelationMapper;
    @Autowired
    private AccountBiz accountBiz;

    /**
     * 参数： eventCode tenantId sysAgentNo customerNo isPreview（是否预先分佣处理） orderNo transactionType remark goodsSkuList
     * 接口主方法：处理请求中的分佣逻辑
     * @param requestMap 包含分佣信息的请求参数
     */
    @ApiDoc(desc = "execute")
    @ShenyuDubboClient("/execute")
    @Override
    @Transactional
    public void execute(Map<String, Object> requestMap) {
        // 获取商品 SKU 列表
        List<Map<String, Object>> goodsSkuList = getGoodsSkuList(requestMap);
        if (goodsSkuList == null) {
            log.warn("goodsSkuList 为空或类型错误");
            throw new BusinessException("111","goodsSkuList 为空或类型错误");
        }

        // 遍历处理每个商品 SKU 的分佣逻辑
        goodsSkuList.forEach(goodsSkuMap -> {
            goodsSkuMap.putAll(requestMap);
            try {
                brokerage(goodsSkuMap);
            } catch (BusinessException | UnsupportedEncodingException e) {
                log.error("处理分佣失败: {}", goodsSkuMap, e);
                throw new BusinessException("111",e.getMessage());
            }
        });
    }

    /**
     * 提取商品 SKU 列表
     * @param requestMap 请求参数
     * @return 商品 SKU 列表，若格式错误则返回 null
     */
    private List<Map<String, Object>> getGoodsSkuList(Map<String, Object> requestMap) {
        try {
            return (List<Map<String, Object>>) requestMap.get("goodsSkuList");
        } catch (ClassCastException e) {
            log.error("goodsSkuList 类型转换失败: {}", requestMap.get("goodsSkuList"), e);
            throw new BusinessException("111","goodsSkuList 类型转换失败");
        }
    }

    /**
     * 核心方法：处理单个商品 SKU 的分佣逻辑
     * @param requestMap 包含单个商品的分佣信息
     */
    public void brokerage(Map<String, Object> requestMap) throws UnsupportedEncodingException {

        String eventCode = MapUtils.getString(requestMap, "eventCode");
        String tenantId = MapUtils.getString(requestMap, "tenantId");
        String sysAgentNo = MapUtils.getString(requestMap, "sysAgentNo");
        String customerNo = MapUtils.getString(requestMap, "customerNo");

        // 验证和获取分佣配置
        DisCommissionConfig disCommissionConfig = validateAndGetConfig(requestMap);
        // 验证和获取分佣规则
        DisCommissionRule disCommissionRule = validateAndGetBrokerageRule(requestMap);
        // 验证和获取分佣政策
        DisCommissionPolicy disCommissionPolicy = validateAndGetBrokeragePolicy(disCommissionRule);

        // TODO 统一规则引擎处理 判断触发事件编码是否一致, 不一致则直接返回
        if(!disCommissionPolicy.getTriggerEventCode().equals(eventCode)){
            return;
        }
        // TODO 统一规则引擎处理 存在, 一致则根据分佣时间扔给队列进行分佣（目前是根据时间直接进行分佣）
        if(disCommissionPolicy.getTriggerEventAfterDate() > 0){
            // 判断完成事件时间跟当前时间差是否小于triggerEventAfterDate
            // TODO 小于扔给延时队列进行处理，到到时间继续触发规则引擎
            
        }
        // 计算商户分佣金额
        BigDecimal merchantGoodsSkuSharingAmount = calculateSharingAmount(requestMap, disCommissionConfig);
        int comparisonResult = merchantGoodsSkuSharingAmount.compareTo(BigDecimal.ZERO);
        // 处理不同角色的佣金
        if(comparisonResult > 0){
            BigDecimal superTenantBrokerageAmount = merchantGoodsSkuSharingAmount.multiply(disCommissionConfig.getSuperTenantRate()).divide(new BigDecimal(100));
            int superTenantResult = superTenantBrokerageAmount.compareTo(BigDecimal.ZERO);
            if(superTenantResult > 0){
                // 处理平台佣金
                handleBrokerage(BizRoleType.TENANT.getCode(), supersTenantId, disCommissionConfig.getSuperTenantRate(), disCommissionPolicy, disCommissionRule, superTenantBrokerageAmount, requestMap);
            }
            // 处理租户佣金
            BigDecimal tenantBrokerageAmount = merchantGoodsSkuSharingAmount.multiply(disCommissionConfig.getTenantRate()).divide(new BigDecimal(100));
            int tenantResult = tenantBrokerageAmount.compareTo(BigDecimal.ZERO);
            if(tenantResult > 0){
                handleBrokerage(BizRoleType.TENANT.getCode(), tenantId, disCommissionConfig.getSuperTenantRate(), disCommissionPolicy, disCommissionRule, tenantBrokerageAmount, requestMap);
            }
            //  处理合伙人佣金
            BigDecimal sysAgentBrokerageAmount = merchantGoodsSkuSharingAmount.multiply(disCommissionConfig.getSysAgentRate()).divide(new BigDecimal(100));
            int sysAgentResult = sysAgentBrokerageAmount.compareTo(BigDecimal.ZERO);
            if(sysAgentResult > 0){
                handleSysAgentBrokerage(sysAgentNo, disCommissionPolicy, disCommissionRule, disCommissionConfig, sysAgentBrokerageAmount, requestMap);
            }
            // 处理客户佣金
            BigDecimal customerBrokerageAmount = merchantGoodsSkuSharingAmount.multiply(disCommissionConfig.getCustomerRate()).divide(new BigDecimal(100));
            int customerResult = customerBrokerageAmount.compareTo(BigDecimal.ZERO);
            if(customerResult > 0){
                handleBrokerage(BizRoleType.CUSTOMER.getCode(), customerNo, disCommissionConfig.getSuperTenantRate(), disCommissionPolicy, disCommissionRule, customerBrokerageAmount, requestMap);
            }
        }
    }

    /**
     * 验证并获取分佣配置
     * DisCommissionConfig 是根据参数调用waas的服务获取
     */
    private DisCommissionConfig validateAndGetConfig(Map<String, Object> requestMap) {
        String tenantId = MapUtils.getString(requestMap, "tenantId");
        // 泛化调用

        // 4、异步调用（泛化调用解耦）订单完成方法统一处理： 根据订单类型后续处理
        DisCommissionConfig disCommissionConfig = (DisCommissionConfig) bsinServiceInvoke.genericInvoke("ProfitSharingConfigService", "getDetailForCrm", "dev", requestMap);
        if (disCommissionConfig == null) {
            throw new BusinessException("111","未找到分佣配置, tenantId: " + tenantId);
        }
        return disCommissionConfig;
    }

    /**
     * 验证并获取分佣规则
     */
    private DisCommissionRule validateAndGetBrokerageRule(Map<String, Object> requestMap) {
        String goodsCategoryNo = MapUtils.getString(requestMap, "goodsCategoryNo");
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");
        DisCommissionRule rule = disBrokerageRuleMapper.selectOne(
                new LambdaQueryWrapper<DisCommissionRule>()
                        .eq(DisCommissionRule::getGoodsCategoryNo, goodsCategoryNo)
                        .eq(DisCommissionRule::getMerchantNo, merchantNo)
        );
        if (rule == null) {
            throw new BusinessException("111","未找到分佣规则, goodsCategoryNo: " + goodsCategoryNo);
        }
        return rule;
    }

    /**
     * 验证并获取分佣政策
     */
    private DisCommissionPolicy validateAndGetBrokeragePolicy(DisCommissionRule rule) {
        DisCommissionPolicy policy = disBrokeragePolicyMapper.selectOne(
                new LambdaQueryWrapper<DisCommissionPolicy>()
                        .eq(DisCommissionPolicy::getSerialNo, rule.getBrokeragePolicyNo())
        );
        if (policy == null || policy.getStatus() != 1 || isPolicyExpired(policy)) {
            throw new BusinessException("111","分佣政策无效或已过期");
        }
        return policy;
    }

    /**
     * 检查分佣政策是否过期
     */
    private boolean isPolicyExpired(DisCommissionPolicy policy) {
        java.util.Date now = new java.util.Date();
        return policy.getStartTime().after(now) || policy.getEndTime().before(now);
    }

    /**
     * 计算商户分佣金额
     */
    private BigDecimal calculateSharingAmount(Map<String, Object> requestMap, DisCommissionConfig config) {
        String goodsSkuPayAmount = MapUtils.getString(requestMap, "goodsSkuPayAmount");
        return new BigDecimal(goodsSkuPayAmount).multiply(config.getMerchantProfitSharingRate().divide(new BigDecimal(100)));
    }

    /**
     *
     * 处理平台分佣
     */
    private void handleBrokerage(String bizRoleType, String bizRoleTypeNo, BigDecimal rate, DisCommissionPolicy policy, DisCommissionRule rule,
                                 BigDecimal brokerageAmount, Map<String, Object> requestMap) throws UnsupportedEncodingException {
        // 创建分佣流水
        DisCommissionJournal journal = BsinServiceContext.getReqBodyDto(DisCommissionJournal.class, requestMap);
        journal.setSerialNo(BsinSnowflake.getId());
        journal.setTenantId(policy.getTenantId());
        journal.setTriggerEventCode(policy.getTriggerEventCode());
        journal.setRuleNo(rule.getSerialNo());
        journal.setPolicyNo(rule.getBrokeragePolicyNo());
        journal.setExcludeFeeType(policy.getExcludeFeeType());
        journal.setExcludeCustomPer(policy.getExcludeCustomPer());
        journal.setRate(rate);
        journal.setDisAmount(brokerageAmount);
        disBrokerageJournalMapper.insert(journal);
        Integer isPreview = MapUtils.getInteger(requestMap, "isPreview");
        String orderNo = MapUtils.getString(requestMap, "orderNo");
        String transactionType = MapUtils.getString(requestMap, "transactionType");
        String remark = MapUtils.getString(requestMap, "remark");
        // 入账操作
        if (isPreview == 0) {
            // 入账到合伙人待结算账户
            accountBiz.inAccount(journal.getTenantId(), bizRoleType, bizRoleTypeNo,
                    AccountCategory.PENDING_SETTLEMENT.getCode(), AccountCategory.PENDING_SETTLEMENT.getDesc(),
                    "cny", orderNo, transactionType,2,  journal.getDisAmount(), remark);
        } else {
            // 从待分佣账户转到待结算账户
            accountBiz.innerTransfer(journal.getTenantId(), bizRoleType, bizRoleTypeNo, bizRoleType, bizRoleTypeNo,
                    AccountCategory.PENDING_BROKERAGE.getCode(), AccountCategory.PENDING_SETTLEMENT.getCode(),
                    "cny", orderNo, transactionType,2, journal.getDisAmount().negate(), remark);
        }

    }

    /**
     * 处理合伙人分佣逻辑，包括一级和二级分佣
     */
    private void handleSysAgentBrokerage(String sysAgentNo, DisCommissionPolicy policy, DisCommissionRule rule, DisCommissionConfig config,
                                         BigDecimal sysAgentBrokerageAmount, Map<String, Object> requestMap) throws UnsupportedEncodingException {
        Integer isPreview = MapUtils.getInteger(requestMap, "isPreview");
        // 一级分佣
        processBrokerage(1, policy, rule, config, sysAgentBrokerageAmount, isPreview, sysAgentNo, requestMap);
        // 二级分佣
        processBrokerage(2, policy, rule, config, sysAgentBrokerageAmount, isPreview, sysAgentNo, requestMap);
    }

    /**
     * 根据等级处理具体的分佣逻辑
     * 计算一级分销或二级分销
     */
    private void processBrokerage(int level, DisCommissionPolicy policy, DisCommissionRule rule, DisCommissionConfig config,
                                  BigDecimal sysAgentBrokerageAmount, Integer isPreview, String sysAgentNo, Map<String, Object> requestMap)
            throws UnsupportedEncodingException {
        Integer salePer = level == 1 ? rule.getFirstSalePer() : rule.getSecondSalePer();
        String agentNo = level == 1 ? sysAgentNo : getParentAgentNo(sysAgentNo);
        if (salePer == null || agentNo == null) {
            throw new BusinessException("111","分佣等级 {} 无效或合伙人编号为空");
        }
        DisCommissionJournal journal = createBrokerageJournal(policy, rule, config, sysAgentBrokerageAmount, level, salePer, isPreview, agentNo, requestMap);
        disBrokerageJournalMapper.insert(journal);
        // 处理账户交易
        processAccountTransaction(agentNo, journal, isPreview, requestMap);
    }

    /**
     * 获取上级合伙人编号
     */
    private String getParentAgentNo(String sysAgentNo) {
        DisTeamRelation teamRelation = disTeamRelationMapper.selectOne(
                new LambdaQueryWrapper<DisTeamRelation>().eq(DisTeamRelation::getSysAgentNo, sysAgentNo)
        );
        return teamRelation != null ? teamRelation.getPrarentSysAgentNo() : null;
    }

    /**
     * 创建分佣流水记录
     */
    private DisCommissionJournal createBrokerageJournal(DisCommissionPolicy policy, DisCommissionRule rule, DisCommissionConfig config,
                                                        BigDecimal sysAgentBrokerageAmount, int disLevel, Integer salePer,
                                                        Integer isPreview, String agentNo, Map<String, Object> requestMap) {
        DisCommissionJournal journal = BsinServiceContext.getReqBodyDto(DisCommissionJournal.class, requestMap);
        journal.setSerialNo(BsinSnowflake.getId());
        journal.setTriggerEventCode(policy.getTriggerEventCode());
        journal.setRuleNo(rule.getSerialNo());
        journal.setPolicyNo(rule.getBrokeragePolicyNo());
        journal.setExcludeFeeType(policy.getExcludeFeeType());
        journal.setExcludeCustomPer(policy.getExcludeCustomPer());
        journal.setDisLevel(disLevel);
        journal.setRate(config.getSysAgentRate());
        journal.setDisAmount(sysAgentBrokerageAmount.multiply(new BigDecimal(salePer)).divide(new BigDecimal(100)));
        journal.setSysAgentNo(agentNo);
        journal.setIsPreview(isPreview);
        return journal;
    }

    /**
     * 处理账户交易记录
     */
    private void processAccountTransaction(String sysAgentNo, DisCommissionJournal journal, Integer isPreview, Map<String, Object> requestMap) throws UnsupportedEncodingException {
        String orderNo = MapUtils.getString(requestMap, "orderNo");
        String transactionType = MapUtils.getString(requestMap, "transactionType");
        String remark = MapUtils.getString(requestMap, "remark");
        if (isPreview == 0) {
            // 入账到合伙人待结算账户
            accountBiz.inAccount(journal.getTenantId(), BizRoleType.SYS_AGENT.getCode(), sysAgentNo,
                    AccountCategory.PENDING_SETTLEMENT.getCode(), AccountCategory.PENDING_SETTLEMENT.getDesc(),
                    "cny", orderNo, transactionType,2, journal.getDisAmount(), remark);
        } else {
            // 从待分佣账户转到待结算账户
            accountBiz.innerTransfer(journal.getTenantId(), BizRoleType.SYS_AGENT.getCode(), sysAgentNo, BizRoleType.SYS_AGENT.getCode(), sysAgentNo,
                    AccountCategory.PENDING_BROKERAGE.getCode(), AccountCategory.PENDING_SETTLEMENT.getCode(),
                    "cny", orderNo, transactionType,2, journal.getDisAmount().negate(), remark);
        }
    }
}




