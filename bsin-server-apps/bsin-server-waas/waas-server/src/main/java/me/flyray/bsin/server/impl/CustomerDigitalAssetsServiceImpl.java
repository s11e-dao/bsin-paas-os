package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.service.BsinBlockChainEngineFactory;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.DigitalAssetsDetailRes;
import me.flyray.bsin.facade.response.DigitalAssetsItemRes;
import me.flyray.bsin.facade.service.CustomerDigitalAssetsService;
import me.flyray.bsin.facade.service.CustomerService;
import me.flyray.bsin.facade.service.DigitalAssetsItemService;
import me.flyray.bsin.facade.service.MerchantService;
import me.flyray.bsin.infrastructure.biz.DigitalAssetsItemBiz;
import me.flyray.bsin.infrastructure.mapper.CustomerDigitalAssetsMapper;
import me.flyray.bsin.infrastructure.mapper.DigitalAssetsItemMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  @DubboReference(version = "${dubbo.provider.version}")
  private CustomerService customerService;

  @DubboReference(version = "${dubbo.provider.version}")
  private MerchantService merchantService;

  @DubboReference(version = "${dubbo.provider.version}")
  private DigitalAssetsItemService digitalAssetsItemService;

  /**
   * 根据租户ID和商户号 查询客户的数字资产
   *
   * @param requestMap
   * @return
   */
  @ShenyuDubboClient("/getList")
  @ApiDoc(desc = "getList")
  @Override
  public List<DigitalAssetsItemRes> getList(Map<String, Object> requestMap) {
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
      List<Map> customerList = customerService.getListByCustomerNos(crmReqMap);
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
    return digitalAssetsItemResList;
  }

  @ShenyuDubboClient("/getPageList")
  @ApiDoc(desc = "getPageList")
  @Override
  public List<DigitalAssetsItemRes> getPageList(Map<String, Object> requestMap) {
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
      List<Merchant> merchantList = merchantService.getListByMerchantNos(crmReqMap);
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

    return digitalAssetsItemList;
  }

  @ShenyuDubboClient("/getDetail")
  @ApiDoc(desc = "getDetail")
  @Override
  public DigitalAssetsDetailRes getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    DigitalAssetsItemRes digitalAssetsItem =
        customerDigitalAssetsMapper.selectCustomerDigitalAssetsDetail(serialNo);

    DigitalAssetsDetailRes digitalAssetsDetailRes =
        digitalAssetsItemBiz.getDetail(
            digitalAssetsItem.getDigitalAssetsCollectionNo(), null, digitalAssetsItem.getTokenId());

    return digitalAssetsDetailRes;
  }

  @ShenyuDubboClient("/verifyAssetsOnChain")
  @ApiDoc(desc = "verifyAssetsOnChain")
  @Override
  public Map<String, Object> verifyAssetsOnChain(Map<String, Object> requestMap) {
    BigDecimal conditionAmount = (BigDecimal) requestMap.get("conditionAmount");
    Map reqMap = new HashMap<>();
    CustomerBase customerBase = customerService.getDetail(reqMap);
    DigitalAssetsDetailRes digitalAssetsDetailRes = digitalAssetsItemService.getDetail(requestMap);

    if (digitalAssetsDetailRes == null) {
      throw new BusinessException(ResponseCode.DIGITAL_ASSETS_ITEM_NOT_EXISTS);
    }
    DigitalAssetsItem digitalAssetsItem = digitalAssetsDetailRes.getDigitalAssetsItem();
    if (digitalAssetsItem == null) {
      throw new BusinessException(ResponseCode.DIGITAL_ASSETS_ITEM_NOT_EXISTS);
    }

    DigitalAssetsCollection digitalAssetsCollection = (DigitalAssetsCollection)digitalAssetsDetailRes.getDigitalAssetsCollection();
    if (digitalAssetsCollection == null) {
      throw new BusinessException(ResponseCode.DIGITAL_ASSETS_COLLECTION_NOT_EXISTS);
    }

    ContractProtocol contractProtocol = (ContractProtocol)digitalAssetsDetailRes.getContractProtocol();

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
    return ret;
  }

}
