package me.flyray.bsin.server.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.dto.ContractTransactionResp;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.ContractProtocol;
import me.flyray.bsin.domain.domain.DigitalAssetsCollection;
import me.flyray.bsin.domain.domain.TokenParam;
import me.flyray.bsin.domain.enums.AssetsCollectionType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.request.DigitalAssetsIssueReqDTO;
import me.flyray.bsin.facade.service.DigitalAssetsCollectionService;
import me.flyray.bsin.facade.service.DigitalPointsService;
import me.flyray.bsin.infrastructure.mapper.ContractProtocolMapper;
import me.flyray.bsin.infrastructure.mapper.DigitalAssetsCollectionMapper;
import me.flyray.bsin.infrastructure.mapper.TokenParamMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.biz.CustomerInfoBiz;
import me.flyray.bsin.server.biz.DigitalAssetsBiz;
import me.flyray.bsin.server.biz.DigitalAssetsItemBiz;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author bolei
 * @date 2023/7/3 15:49
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/digitalPoints", timeout = 6000)
@ApiModule(value = "digitalPoints")
@Service
public class DigitalPointsServiceImpl implements DigitalPointsService {

  @Autowired private ContractProtocolMapper contractProtocolMapper;
  @Autowired private DigitalAssetsBiz digitalAssetsBiz;
  @Autowired private DigitalAssetsCollectionMapper digitalAssetsCollectionMapper;
  @Autowired private TokenParamMapper tokenParamMapper;
  @Autowired private DigitalAssetsItemBiz digitalAssetsItemBiz;
  @Autowired private CustomerInfoBiz customerInfoBiz;
  @Autowired private DigitalAssetsCollectionService digitalAssetsCollectionService;

  @Value("${bsin.jiujiu.aesKey}")
  private String aesKey;

  /**
   * 开通商户的积分账户，商户可以选择激励的数字资产 1、开通对应数字积分账户，通过币种关联数字资产中心 2、调用数字资产中心发行数字积分资产
   *
   * @param requestMap
   * @return
   * @throws Exception
   */
  @Override
  @Transactional
  public Map<String, Object> issue(Map<String, Object> requestMap) throws Exception {
    log.info("DigitalPointsService issue 请求参数:{}", JSON.toJSONString(requestMap));
    // 发行的商户
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    String tenantId = loginUser.getTenantId();
    DigitalAssetsIssueReqDTO digitalAssetsIssueReqDTO =
        BsinServiceContext.getReqBodyDto(DigitalAssetsIssueReqDTO.class, requestMap);

    String chainEnv = digitalAssetsIssueReqDTO.getChainEnv();
    String chainType = digitalAssetsIssueReqDTO.getChainType();
    // 合约是否被赞数
    String sponsorFlag = digitalAssetsIssueReqDTO.getSponsorFlag();

    String contractProtocolNo = MapUtils.getString(requestMap, "contractProtocolNo");
    String protocolCode = MapUtils.getString(requestMap, "protocolCode");
//    if (protocolCode == null) {
//      throw new BusinessException("100000", "protocolCode为空！！！");
//    }

    // 1.获取商户的客户信息
    Map merchantCustomerBase = customerInfoBiz.getMerchantCustomerBase(merchantNo, chainType);
    digitalAssetsIssueReqDTO.setPrivateKey((String) merchantCustomerBase.get("privateKey"));
    digitalAssetsIssueReqDTO.setOwnerAddress((String) merchantCustomerBase.get("walletAddress"));

    // 2.账户余额判断
    customerInfoBiz.checkAccountBalance(merchantCustomerBase, chainType, chainEnv);

    // 3.根据protocolCode和chainType 获取 contractProtocol
    ContractProtocol contractProtocol =
        digitalAssetsItemBiz.getContractProtocol(
            digitalAssetsIssueReqDTO, protocolCode, contractProtocolNo);
    // 4.部署合约
    ContractTransactionResp contractTransactionResp =
        digitalAssetsBiz.deployContract(digitalAssetsIssueReqDTO);

    // 5.账户扣费
    customerInfoBiz.accountOut(merchantCustomerBase, chainEnv);

    // 6. digitalAssetsMapper 数据插入
    DigitalAssetsCollection digitalAssetsColletion = new DigitalAssetsCollection();
    BeanUtil.copyProperties(digitalAssetsIssueReqDTO, digitalAssetsColletion);
    digitalAssetsColletion.setMerchantNo(merchantNo);
    digitalAssetsColletion.setSerialNo(BsinSnowflake.getId());
    digitalAssetsColletion.setContractAddress(contractTransactionResp.getContractAddress());
    digitalAssetsColletion.setSponsorFlag(sponsorFlag);
    digitalAssetsColletion.setCreateBy(customerNo);
    digitalAssetsColletion.setContractProtocolNo(contractProtocol.getSerialNo());
    digitalAssetsColletion.setName((String) requestMap.get("name"));

    if (digitalAssetsIssueReqDTO.getDecimals() != null) {
      digitalAssetsColletion.setDecimals(digitalAssetsIssueReqDTO.getDecimals());
    } else {
      digitalAssetsColletion.setDecimals(0);
    }

    BigDecimal melo =
        new BigDecimal(Math.pow(10, digitalAssetsColletion.getDecimals().doubleValue()));
    // 初始供应量
    if (digitalAssetsIssueReqDTO.getInitialSupply() != null) {
      digitalAssetsColletion.setInitialSupply(
          digitalAssetsIssueReqDTO.getInitialSupply().multiply(melo));
    } else {
      digitalAssetsColletion.setInitialSupply(new BigDecimal("0"));
    }
    // 总供应量
    digitalAssetsColletion.setTotalSupply(
        new BigDecimal((String) requestMap.get("totalSupply")).multiply(melo));

    // 库存
    BigDecimal inventory =
        digitalAssetsColletion.getTotalSupply().subtract(digitalAssetsColletion.getInitialSupply());
    digitalAssetsColletion.setInventory(inventory);

    digitalAssetsColletion.setTenantId(tenantId);
    digitalAssetsColletion.setChainEnv(chainEnv);
    digitalAssetsColletion.setChainType(contractProtocol.getChainType());
    digitalAssetsColletion.setCollectionType(digitalAssetsIssueReqDTO.getAssetsCollectionType());
    digitalAssetsCollectionMapper.insert(digitalAssetsColletion);
    log.info("DigitalAssetsService issue 相应结果:{}", JSON.toJSONString(contractTransactionResp));

    // 7.插入token基础信息
    TokenParam tokenParam = new TokenParam();
    tokenParam.setTenantId(loginUser.getTenantId());
    tokenParam.setMerchantNo(merchantNo);
    tokenParam.setDigitalAssetsCollectionNo(digitalAssetsColletion.getSerialNo());
    tokenParam.setTotalSupply(digitalAssetsColletion.getTotalSupply());
    tokenParam.setDecimals(digitalAssetsColletion.getDecimals());
    tokenParam.setCirculation(digitalAssetsColletion.getInitialSupply());
    tokenParam.setName(digitalAssetsColletion.getName());
    tokenParam.setSymbol(digitalAssetsColletion.getSymbol());
    tokenParamMapper.insert(tokenParam);
    return RespBodyHandler.setRespBodyDto(contractTransactionResp);
  }

  @Override
  public Map<String, Object> getDetailByMerchantNo(Map<String, Object> requestMap) {
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    DigitalAssetsCollection digitalAssetsCollection =
        digitalAssetsCollectionMapper.selectOne(
            new LambdaQueryWrapper<DigitalAssetsCollection>()
                .eq(DigitalAssetsCollection::getMerchantNo, merchantNo)
                .eq(
                    DigitalAssetsCollection::getCollectionType,
                    AssetsCollectionType.DIGITAL_POINT.getCode()));
    return RespBodyHandler.setRespBodyDto(digitalAssetsCollection);
  }

  @Override
  public Map<String, Object> mint(Map<String, Object> requestMap) throws Exception {
    return RespBodyHandler.setRespBodyDto(digitalAssetsCollectionService.mint(requestMap));
  }
}
