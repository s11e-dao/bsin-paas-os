package me.flyray.bsin.server.impl;

import static me.flyray.bsin.constants.ResponseCode.USER_NOT_EXIST;

import me.flyray.bsin.domain.domain.*;
import me.flyray.bsin.facade.service.MerchantService;
import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.service.BsinBlockChainEngineFactory;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.DigitalAssetsDetailRes;
import me.flyray.bsin.facade.response.DigitalAssetsItemRes;
import me.flyray.bsin.facade.service.CustomerDigitalAssetsService;
import me.flyray.bsin.facade.service.CustomerService;
import me.flyray.bsin.facade.service.DigitalAssetsItemService;
import me.flyray.bsin.infrastructure.mapper.CustomerDigitalAssetsMapper;
import me.flyray.bsin.infrastructure.mapper.DigitalAssetsItemMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.biz.DigitalAssetsItemBiz;
import me.flyray.bsin.server.utils.RespBodyHandler;

/**
 * @author bolei
 * @date 2023/7/19 14:59
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/customerDigitalAssets", timeout = 6000)
@ApiModule(value = "customerDigitalAssets")
@Service
public class CustomerDigitalAssetsServiceImpl implements CustomerDigitalAssetsService {

  @Autowired private DigitalAssetsItemMapper digitalAssetsItemMapper;
  @Autowired private CustomerDigitalAssetsMapper customerDigitalAssetsMapper;
  @Autowired private DigitalAssetsItemBiz digitalAssetsItemBiz;

  @Autowired private BsinBlockChainEngineFactory bsinBlockChainEngineFactory;

  @DubboReference(version = "dev")
  private CustomerService customerService;

  @DubboReference(version = "dev")
  private MerchantService merchantService;

  @DubboReference(version = "dev")
  private DigitalAssetsItemService digitalAssetsItemService;

  /**
   * 根据租户ID和商户号 查询客户的数字资产
   *
   * @param requestMap
   * @return
   */
  @Override
  public Map<String, Object> getList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String customerNo;
    // 传值用户
    customerNo = (String) requestMap.get("customerNo");
    if (customerNo == null) {
      // 登录用户
      customerNo = loginUser.getCustomerNo();
    }
    // 获取前端传的商户
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
    }
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }
    String assetsType = MapUtils.getString(requestMap, "assetsType");
    String tokenId = MapUtils.getString(requestMap, "tokenId");
    List<DigitalAssetsItemRes> digitalAssetsItemList =
        customerDigitalAssetsMapper.selectCustomerDigitalAssetsList(
            tenantId, merchantNo, customerNo, assetsType, tokenId);
    // 调用crm补充商户信息和客户信息
    List<String> customerNos = new ArrayList<>();
    List<String> merchantNos = new ArrayList<>();
    for (DigitalAssetsItemRes digitalAssetsItemRes : digitalAssetsItemList) {
      // 获取商户信息和用户信息
      merchantNos.add(digitalAssetsItemRes.getMerchantNo());
      customerNos.add(digitalAssetsItemRes.getCustomerNo());
    }
    List<DigitalAssetsItemRes> digitalAssetsItemResList = new ArrayList<>();
    if (customerNos.size() > 0) {
      Map crmReqMap = new HashMap();
      crmReqMap.put("customerNos", customerNos);
      Map<String, Object> customerResult = customerService.getListByCustomerNos(crmReqMap);
      List<Map> customerList = (List<Map>) customerResult.get("data");
      for (DigitalAssetsItemRes digitalAssetsItemRes : digitalAssetsItemList) {
        // 找出客户信息
        for (Map customer : customerList) {
          if (digitalAssetsItemRes.getCustomerNo().equals(customer.get("customerNo"))) {
            digitalAssetsItemRes.setUsername((String) customer.get("username"));
            digitalAssetsItemRes.setAvatar((String) customer.get("avatar"));
          }
        }
        digitalAssetsItemResList.add(digitalAssetsItemRes);
      }
    }
    return RespBodyHandler.setRespBodyListDto(digitalAssetsItemResList);
  }

  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();

    String customerNo = (String) requestMap.get("customerNo");
    if (customerNo == null) {
      // 登录用户
      customerNo = loginUser.getCustomerNo();
    }
    // 获取前端传的商户
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
    }

    // 获取前端传的租户
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }
    Pagination pagination = (Pagination) requestMap.get("pagination");
    String assetsType = MapUtils.getString(requestMap, "assetsType");
    String tokenId = MapUtils.getString(requestMap, "tokenId");
    List<DigitalAssetsItemRes> customerDigitalAssetsList =
        customerDigitalAssetsMapper.selectCustomerDigitalAssetsList(
            tenantId, merchantNo, customerNo, assetsType, tokenId);
    // TODO 补充商户信息
    List<String> merchantNos = new ArrayList<>();
    for (DigitalAssetsItemRes digitalAssetsItem : customerDigitalAssetsList) {
      // 获取商户信息
      merchantNos.add(digitalAssetsItem.getMerchantNo());
    }
    List<DigitalAssetsItemRes> digitalAssetsItemList = new ArrayList<>();
    if (merchantNos.size() > 0) {
      Map crmReqMap = new HashMap();
      crmReqMap.put("merchantNos", merchantNos);
      Map<String, Object> merchantResult = merchantService.getListByMerchantNos(crmReqMap);
      List<Merchant> merchantList = (List<Merchant>) merchantResult.get("data");
      for (DigitalAssetsItemRes digitalAssetsItem : customerDigitalAssetsList) {
        // 找出商户信息
        for (Merchant merchant : merchantList) {
          if (digitalAssetsItem.getMerchantNo().equals(merchant.getSerialNo())) {
            digitalAssetsItem.setMerchantName(merchant.getMerchantName());
            digitalAssetsItem.setMerchantLogo(merchant.getLogoUrl());
          }
        }
        digitalAssetsItemList.add(digitalAssetsItem);
      }
    }

    return RespBodyHandler.setRespBodyListDto(digitalAssetsItemList);
  }

  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    DigitalAssetsItemRes digitalAssetsItem =
        customerDigitalAssetsMapper.selectCustomerDigitalAssetsDetail(serialNo);

    DigitalAssetsDetailRes digitalAssetsDetailRes =
        digitalAssetsItemBiz.getDetail(
            digitalAssetsItem.getDigitalAssetsCollectionNo(), null, digitalAssetsItem.getTokenId());

    return RespBodyHandler.setRespBodyDto(digitalAssetsDetailRes);
  }

  @Override
  public Map<String, Object> verifyAssetsOnChain(Map<String, Object> requestMap) {
    BigDecimal conditionAmount = (BigDecimal) requestMap.get("conditionAmount");
    Map reqMap = new HashMap<>();
    Map resCustomerMap = customerService.getDetail(reqMap);
    Map customerBaseMap = (Map) resCustomerMap.get("data");
    if (customerBaseMap == null) {
      throw new BusinessException(USER_NOT_EXIST);
    }
    CustomerBase customerBase =
        BsinServiceContext.getReqBodyDto(CustomerBase.class, customerBaseMap);

    Map digitalAssetsItemResMap = digitalAssetsItemService.getDetail(requestMap);
    Map digitalAssetsItemMapData = (Map) digitalAssetsItemResMap.get("data");

    if (digitalAssetsItemMapData == null) {
      throw new BusinessException(ResponseCode.DIGITAL_ASSETS_ITEM_NOT_EXISTS);
    }
    DigitalAssetsItem digitalAssetsItem =
        (DigitalAssetsItem) digitalAssetsItemMapData.get("digitalAssetsItem");
    if (digitalAssetsItem == null) {
      throw new BusinessException(ResponseCode.DIGITAL_ASSETS_ITEM_NOT_EXISTS);
    }

    DigitalAssetsCollection digitalAssetsCollection =
        (DigitalAssetsCollection) digitalAssetsItemMapData.get("digitalAssetsCollection");
    if (digitalAssetsCollection == null) {
      throw new BusinessException(ResponseCode.DIGITAL_ASSETS_COLLECTION_NOT_EXISTS);
    }

    ContractProtocol contractProtocol =
        (ContractProtocol) digitalAssetsItemMapData.get("contractProtocol");

    if (contractProtocol == null) {
      throw new BusinessException(ResponseCode.ILLEGAL_ASSETS_PROTOCOL);
    }

    // TODO: validate on chain
    BsinBlockChainEngine bsinBlockChainEngine =
        bsinBlockChainEngineFactory.getBsinBlockChainEngineInstance(
            (String) digitalAssetsCollection.getChainType());
    String balance = null;
    try {
      balance =
          bsinBlockChainEngine.getAssetBalance(
              (String) digitalAssetsCollection.getChainEnv(),
              (String) digitalAssetsCollection.getContractAddress(),
              customerBase.getWalletAddress(),
              (String) contractProtocol.getProtocolStandards(),
              digitalAssetsItem.getTokenId().toString());
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }

    if (new BigDecimal(balance).compareTo(conditionAmount) == -1) {
      throw new BusinessException(ResponseCode.NON_CLAIM_CONDITION);
    }

    Map<String, Object> ret = new HashMap<String, Object>();
    ret.put("balance", balance);
    return RespBodyHandler.setRespBodyDto(ret);
  }
}
