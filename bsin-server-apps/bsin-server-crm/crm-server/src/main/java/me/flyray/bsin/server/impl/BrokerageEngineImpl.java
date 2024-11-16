package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.domain.enums.AccountCategory;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.engine.BrokerageEngine;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.flyray.bsin.server.biz.AccountBiz;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@ShenyuDubboService(path = "/brokerageEngine", timeout = 6000)
@ApiModule(value = "brokerageEngine")
@Service
public class BrokerageEngineImpl implements BrokerageEngine {

    @Autowired
    private DisBrokerageJournalMapper disBrokerageJournalMapper;
    @Autowired
    private CustomerIdentityMapper customerIdentityMapper;
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
     * 接口主方法：处理请求中的分佣逻辑
     * @param requestMap 包含分佣信息的请求参数
     */
    @ApiDoc(desc = "execute")
    @ShenyuDubboClient("/execute")
    @Override
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
        // 验证和获取分佣配置
        DisBrokerageConfig config = validateAndGetConfig(requestMap);
        // 验证和获取分佣规则
        DisBrokerageRule rule = validateAndGetBrokerageRule(requestMap, config);
        // 验证和获取分佣政策
        DisBrokeragePolicy policy = validateAndGetBrokeragePolicy(rule);

        // 判断触发事件编码是否一致, 不一致则直接返回
        if(!policy.getTriggerEventCode().equals(eventCode)){
            return;
        }

        // 存在, 一致则根据分佣时间扔给队列进行分佣（目前是根据时间直接进行分佣）
        if(policy.getTriggerEventAfterDate() > 0){
            // 判断完成事件时间跟当前时间差是否小于triggerEventAfterDate
            // TODO 小于扔给延时队列进行处理

        }
        // 计算商户分佣金额
        BigDecimal merchantGoodsSkuSharingAmount = calculateSharingAmount(requestMap, config);
        int comparisonResult = merchantGoodsSkuSharingAmount.compareTo(BigDecimal.ZERO);
        // 不分佣金
        if(comparisonResult > 0){
            // 处理分佣逻辑
            handleBrokerage(policy, rule, config, merchantGoodsSkuSharingAmount, requestMap);
        }
    }

    /**
     * 验证并获取分佣配置
     */
    private DisBrokerageConfig validateAndGetConfig(Map<String, Object> requestMap) {
        String tenantId = MapUtils.getString(requestMap, "tenantId");
        DisBrokerageConfig config = disBrokerageConfigMapper.selectOne(
                new LambdaQueryWrapper<DisBrokerageConfig>().eq(DisBrokerageConfig::getTenantId, tenantId)
        );
        if (config == null) {
            throw new BusinessException("111","未找到分佣配置, tenantId: " + tenantId);
        }
        return config;
    }

    /**
     * 验证并获取分佣规则
     */
    private DisBrokerageRule validateAndGetBrokerageRule(Map<String, Object> requestMap, DisBrokerageConfig config) {
        String goodsCategoryNo = MapUtils.getString(requestMap, "goodsCategoryNo");
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");
        DisBrokerageRule rule = disBrokerageRuleMapper.selectOne(
                new LambdaQueryWrapper<DisBrokerageRule>()
                        .eq(DisBrokerageRule::getGoodsCategoryNo, goodsCategoryNo)
                        .eq(DisBrokerageRule::getMerchantNo, merchantNo)
        );
        if (rule == null) {
            throw new BusinessException("111","未找到分佣规则, goodsCategoryNo: " + goodsCategoryNo);
        }
        return rule;
    }

    /**
     * 验证并获取分佣政策
     */
    private DisBrokeragePolicy validateAndGetBrokeragePolicy(DisBrokerageRule rule) {
        DisBrokeragePolicy policy = disBrokeragePolicyMapper.selectOne(
                new LambdaQueryWrapper<DisBrokeragePolicy>()
                        .eq(DisBrokeragePolicy::getSerialNo, rule.getBrokeragePolicyNo())
        );
        if (policy == null || policy.getStatus() != 1 || isPolicyExpired(policy)) {
            throw new BusinessException("111","分佣政策无效或已过期");
        }
        return policy;
    }

    /**
     * 检查分佣政策是否过期
     */
    private boolean isPolicyExpired(DisBrokeragePolicy policy) {
        java.util.Date now = new java.util.Date();
        return policy.getStartTime().after(now) || policy.getEndTime().before(now);
    }

    /**
     * 计算商户分佣金额
     */
    private BigDecimal calculateSharingAmount(Map<String, Object> requestMap, DisBrokerageConfig config) {
        String goodsSkuPayAmount = MapUtils.getString(requestMap, "goodsSkuPayAmount");
        return new BigDecimal(goodsSkuPayAmount).multiply(config.getMerchantSharingRate());
    }

    /**
     * 处理分佣逻辑，包括一级和二级分佣
     */
    private void handleBrokerage(DisBrokeragePolicy policy, DisBrokerageRule rule, DisBrokerageConfig config,
                                 BigDecimal goodsSkuAmount, Map<String, Object> requestMap) throws UnsupportedEncodingException {
        String sysAgentNo = MapUtils.getString(requestMap, "sysAgentNo");
        Integer isPreview = MapUtils.getInteger(requestMap, "isPreview");
        // 一级分佣
        processBrokerage(1, policy, rule, config, goodsSkuAmount, isPreview, sysAgentNo, requestMap);
        // 二级分佣
        processBrokerage(2, policy, rule, config, goodsSkuAmount, isPreview, sysAgentNo, requestMap);
    }

    /**
     * 根据等级处理具体的分佣逻辑
     */
    private void processBrokerage(int level, DisBrokeragePolicy policy, DisBrokerageRule rule, DisBrokerageConfig config,
                                  BigDecimal goodsSkuAmount, Integer isPreview, String sysAgentNo, Map<String, Object> requestMap)
            throws UnsupportedEncodingException {
        Integer salePer = level == 1 ? rule.getFirstSalePer() : rule.getSecondSalePer();
        String agentNo = level == 1 ? sysAgentNo : getParentAgentNo(sysAgentNo);
        if (salePer == null || agentNo == null) {
            throw new BusinessException("111","分佣等级 {} 无效或代理商编号为空");
        }
        DisBrokerageJournal journal = createBrokerageJournal(policy, rule, config, goodsSkuAmount, level, salePer, isPreview, agentNo, requestMap);
        disBrokerageJournalMapper.insert(journal);
        // 处理账户交易
        processAccountTransaction(agentNo, journal, isPreview);
    }

    /**
     * 获取上级代理商编号
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
    private DisBrokerageJournal createBrokerageJournal(DisBrokeragePolicy policy, DisBrokerageRule rule, DisBrokerageConfig config,
                                                       BigDecimal goodsSkuAmount, int disLevel, Integer salePer,
                                                       Integer isPreview, String agentNo, Map<String, Object> requestMap) {
        DisBrokerageJournal journal = BsinServiceContext.getReqBodyDto(DisBrokerageJournal.class, requestMap);
        journal.setSerialNo(BsinSnowflake.getId());
        journal.setTriggerEventCode(policy.getTriggerEventCode());
        journal.setRuleNo(rule.getSerialNo());
        journal.setPolicyNo(rule.getBrokeragePolicyNo());
        journal.setExcludeFeeType(policy.getExcludeFeeType());
        journal.setExcludeCustomPer(policy.getExcludeCustomPer());
        journal.setDisLevel(disLevel);
        journal.setSysAgentRate(config.getSysAgentRate());
        journal.setDisAmount(goodsSkuAmount.multiply(config.getSysAgentRate())
                .multiply(new BigDecimal(salePer)).divide(new BigDecimal(10000)));
        journal.setSysAgentNo(agentNo);
        journal.setIsPreview(isPreview);
        return journal;
    }

    /**
     * 处理账户交易记录
     */
    private void processAccountTransaction(String sysAgentNo, DisBrokerageJournal journal, Integer isPreview) throws UnsupportedEncodingException {
        if (isPreview == 0) {
            // 入账到代理商待结算账户
            accountBiz.inAccount(journal.getTenantId(), BizRoleType.SYS_AGENT.getCode(), sysAgentNo,
                    AccountCategory.PENDING_SETTLEMENT.getCode(), AccountCategory.PENDING_SETTLEMENT.getDesc(),
                    "cny", 2, journal.getDisAmount());
        } else {
            // 从待分佣账户转到待结算账户
            accountBiz.innerTransfer(journal.getTenantId(), BizRoleType.SYS_AGENT.getCode(), sysAgentNo, BizRoleType.SYS_AGENT.getCode(), sysAgentNo,
                    AccountCategory.PENDING_BROKERAGE.getCode(), AccountCategory.PENDING_SETTLEMENT.getCode(),
                    "cny", 2, journal.getDisAmount().negate());
        }
    }
}




