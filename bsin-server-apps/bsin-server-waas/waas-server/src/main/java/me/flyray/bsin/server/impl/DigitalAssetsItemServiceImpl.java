package me.flyray.bsin.server.impl;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.facade.service.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.enums.ContractProtocolStandards;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.enums.AssetsCollectionType;
import me.flyray.bsin.domain.enums.ObtainMethod;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.DigitalAssetsDetailRes;
import me.flyray.bsin.facade.response.DigitalAssetsItemRes;
import me.flyray.bsin.infrastructure.mapper.ContractProtocolMapper;
import me.flyray.bsin.infrastructure.mapper.DigitalAssetsCollectionMapper;
import me.flyray.bsin.infrastructure.mapper.DigitalAssetsItemMapper;
import me.flyray.bsin.infrastructure.mapper.DigitalAssetsItemObtainCodeMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.infrastructure.biz.CustomerInfoBiz;
import me.flyray.bsin.infrastructure.biz.DigitalAssetsItemBiz;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.validate.QueryGroup;

/**
 * @author bolei
 * @date 2023/6/26 15:17
 * @desc
 */
@Slf4j
@ShenyuDubboService(path = "/digitalAssetsItem", timeout = 6000)
@ApiModule(value = "digitalAssetsItem")
@Service
public class DigitalAssetsItemServiceImpl implements DigitalAssetsItemService {

  @Autowired private DigitalAssetsItemMapper digitalAssetsItemMapper;
  @Autowired private DigitalAssetsItemObtainCodeMapper digitalAssetsItemObtainCodeMapper;
  @Autowired private DigitalAssetsCollectionMapper digitalAssetsCollectionMapper;
  @Autowired private ContractProtocolMapper contractProtocolMapper;
  @Autowired private DigitalAssetsItemBiz digitalAssetsItemBiz;
  @Autowired private CustomerInfoBiz customerInfoBiz;

  @DubboReference(version = "${dubbo.provider.version}")
  private CustomerService customerService;

  @DubboReference(version = "${dubbo.provider.version}")
  private MerchantService merchantService;

  @DubboReference(version = "${dubbo.provider.version}")
  private EquityService equityService;

  @DubboReference(version = "${dubbo.provider.version}")
  private MemberService memberService;

  /**
   * 领取逻辑：
   * */
  @ShenyuDubboClient("/claim")
  @ApiDoc(desc = "claim")
  @Override
  @Transactional
  public Map<String, Object> claim(Map<String, Object> requestMap) throws Exception {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    log.info("DigitalAssetsItemService obtainNft请求参数:{}", JSON.toJSONString(requestMap));

    String digitalAssetsItemNo = (String) requestMap.get("digitalAssetsItemNo");
    if (digitalAssetsItemNo == null) {
      digitalAssetsItemNo = (String) requestMap.get("serialNo");
    }

    String tenantId = LoginInfoContextHelper.getTenantId();

    String password = MapUtils.getString(requestMap, "password"); // 领取口令
    String toAddress = MapUtils.getString(requestMap, "toAddress");
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String code = MapUtils.getString(requestMap, "code"); // anyweb login 的code
    String amount = MapUtils.getString(requestMap, "amount"); // 数量
    String tokenIdStr = MapUtils.getString(requestMap, "tokenId"); // 数量
    String limitClaimStr = MapUtils.getString(requestMap, "limitClaim"); // 限制领取
    boolean limitClaim = true;
    if (limitClaimStr != null) {
      limitClaim = Boolean.parseBoolean(limitClaimStr);
    }

    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
      if (customerNo == null) {
        throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
      }
    }

    if (amount == null) {
      amount = "1";
    } else {
      // 避免小数点
      amount = (new BigDecimal(amount).divide(new BigDecimal("1"), 0, ROUND_HALF_DOWN)).toString();
    }

    // 1.获取资产信息
    DigitalAssetsItem digitalAssetsItem = digitalAssetsItemMapper.selectById(digitalAssetsItemNo);
    if (digitalAssetsItem == null) {
      throw new BusinessException(ResponseCode.DIGITAL_ASSETS_ITEM_NOT_EXISTS);
    }
    // 2.根据资产编号（digitalAssetsNo）查询资产合约集合信息
    DigitalAssetsCollection digitalAssetsCollection =
        digitalAssetsCollectionMapper.selectById(digitalAssetsItem.getDigitalAssetsCollectionNo());
    if (digitalAssetsCollection == null) {
      throw new BusinessException(ResponseCode.DIGITAL_ASSETS_COLLECTION_NOT_EXISTS);
    }

    // 3.获取资产的合约集合的合约信息
    ContractProtocol contractProtocol =
        contractProtocolMapper.selectById(digitalAssetsCollection.getContractProtocolNo());
    if (digitalAssetsCollection == null) {
      throw new BusinessException(ResponseCode.ILLEGAL_ASSETS_PROTOCOL);
    }
    // ERC721 协议只支持 1 个领取   TODO: 多个领取和指定tokenId领取
    if (contractProtocol
        .getProtocolStandards()
        .equals(ContractProtocolStandards.ERC721.getCode())) {
      amount = "1";
    }
    // 4.校验库存
    digitalAssetsItemBiz.verifyInventory(digitalAssetsItem, amount);

    // 5.获取DigitalAssetsItem所属商户的客户信息
    Map merchantCustomerBase =
        customerInfoBiz.getMerchantCustomerBase(
            digitalAssetsCollection.getMerchantNo(), digitalAssetsCollection.getChainType());

    // 6.获取客户信息
    Map customerBase =
        customerInfoBiz.getCustomerBase(customerNo, digitalAssetsCollection.getChainType());
    String phone = (String) customerBase.get("phone");
    if (toAddress == null) {
      toAddress = (String) customerBase.get("walletAddress");
    }
    if (toAddress == null) {
      throw new BusinessException(ResponseCode.CUSTOMER_WALLET_ISNULL);
    }

    // 8. anyweb 信息 (针对anyweb授权领取的)
    if (code != null) {
      // 根据授权码获取用户信息
      Map<String, String> anywebUserInfo = digitalAssetsItemBiz.getAnywebInfo(code);
      phone = anywebUserInfo.get("phone");
    }
    // 9.领取方式： 根据领取方式 mint/transfer
    digitalAssetsItemBiz.claimMethodProcess(
        digitalAssetsItem,
        digitalAssetsCollection,
        contractProtocol,
        password,
        merchantCustomerBase,
        customerBase,
        toAddress,
        phone,
        tokenIdStr,
        amount,
        limitClaim);

    // 9.更新库存
    digitalAssetsItemBiz.updateInventory(
        digitalAssetsItem, contractProtocol.getProtocolStandards(), amount);

    // 10.更新领取码
    digitalAssetsItemBiz.updateObtainCode(digitalAssetsItem, password);

    // 11. 更新客户资产表
    digitalAssetsItemBiz.updateCustomerDigitalAssets(
        digitalAssetsItem, customerNo, new BigDecimal(amount), 1);
    //    1、数字徽章(ERC1155) 2、PFP(ERC71） 3、积分(ERC20) 4、门票(ERC1155)  5、pass卡(ERC1155)
    if (digitalAssetsItem.getAssetsType().equals(AssetsCollectionType.PASS_CARD.getCode())) {
      // 12. 如果资产是 passCard 类型，
      // 12.1 插入到 customerPassCardMapper
      digitalAssetsItemBiz.insertCustomerPassCard(
          digitalAssetsItem, customerNo, digitalAssetsCollection.getContractAddress());

      // 12.2 开通会员
      requestMap.put("customerNo", customerNo);
      requestMap.put("tenantId", tenantId);
      requestMap.put("merchantNo", digitalAssetsItem.getMerchantNo());
      requestMap.put("nickname", customerBase.get("nickname"));
      memberService.openMember(requestMap);
    }
    return RespBodyHandler.RespBodyDto();
  }

  @ShenyuDubboClient("/buy")
  @ApiDoc(desc = "buy")
  @Override
  public Map<String, Object> buy(Map<String, Object> requestMap) throws IOException {
    return null;
  }

  @ShenyuDubboClient("/openBlindBox")
  @ApiDoc(desc = "openBlindBox")
  @Override
  public Map<String, Object> openBlindBox(Map<String, Object> requestMap) throws IOException {
    return null;
  }

  @ShenyuDubboClient("/obtainNftPasswordCheck")
  @ApiDoc(desc = "obtainNftPasswordCheck")
  @Override
  public Map<String, Object> obtainNftPasswordCheck(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    String password = MapUtils.getString(requestMap, "password");
    DigitalAssetsItem digitalAssetsItem = digitalAssetsItemMapper.selectById(serialNo);
    if (digitalAssetsItem == null) {
      throw new BusinessException(ResponseCode.PASSWORD_EXISTS);
    }
    // 判断主题是否需要密码
    if (ObtainMethod.FIXED_PASSWORD.getCode().equals(digitalAssetsItem.getObtainMethod())
        || ObtainMethod.RANDOM_PASSWORD.getCode().equals(digitalAssetsItem.getObtainMethod())) {
      // 获取商品口令对比
      LambdaUpdateWrapper<DigitalAssetsItemObtainCode> warapper = new LambdaUpdateWrapper<>();
      warapper.orderByDesc(DigitalAssetsItemObtainCode::getCreateTime);
      // warapper.eq(ContractProtocol::getTenantId, contractProtocol.getTenantId());
      warapper.eq(
          ObjectUtil.isNotNull(password), DigitalAssetsItemObtainCode::getPassword, password);
      DigitalAssetsItemObtainCode obtainCode =
          digitalAssetsItemObtainCodeMapper.selectOne(warapper);
      if (obtainCode == null) {
        throw new BusinessException(ResponseCode.PASSWORD_ERROR);
      }
    }
    return RespBodyHandler.RespBodyDto();
  }

  /**
   * 根据类型查询数字资产
   *
   * @param requestMap
   * @return
   */
  @ShenyuDubboClient("/getList")
  @ApiDoc(desc = "getList")
  @Override
  public Map<String, Object> getList(Map<String, Object> requestMap) {
    String tenantId = LoginInfoContextHelper.getTenantId();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    // 查询商户下的数字资产
    if (merchantNo == null) {
      merchantNo = LoginInfoContextHelper.getMerchantNo();
    }

    List<String> assetsTypes = (List<String>) requestMap.get("assetsTypes");
    List<DigitalAssetsItemRes> digitalAssetsItemList =
        digitalAssetsItemMapper.selectDigitalAssetsList(tenantId, merchantNo, assetsTypes);

    List<DigitalAssetsItemRes> digitalAssetsItemListRes = new ArrayList<>();

    List<String> customerNos = new ArrayList<>();
    for (DigitalAssetsItemRes digitalAssetsItem : digitalAssetsItemList) {
      // 获取商户信息
      customerNos.add(digitalAssetsItem.getMerchantNo());
    }
    if (customerNos.size() > 0) {
      Map crmReqMap = new HashMap();
      crmReqMap.put("customerNos", customerNos);
      Map<String, Object> customerResult = customerService.getListByCustomerNos(crmReqMap);
      List<Map> customerList = (List<Map>) customerResult.get("data");
      for (DigitalAssetsItemRes digitalAssetsItem : digitalAssetsItemList) {
        // 找出客户信息
        for (Map customer : customerList) {
          if (digitalAssetsItem.getMerchantNo().equals(customer.get("customerNo"))) {
            digitalAssetsItem.setMerchantName((String) customer.get("username"));
          }
        }
        digitalAssetsItemListRes.add(digitalAssetsItem);
      }
    }
    return RespBodyHandler.setRespBodyListDto(digitalAssetsItemListRes);
  }

  @ShenyuDubboClient("/getPageList")
  @ApiDoc(desc = "getPageList")
  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {
    DigitalAssetsItem digitalAssetsItemReq =
        BsinServiceContext.getReqBodyDto(DigitalAssetsItem.class, requestMap, QueryGroup.class);
    String tenantId = LoginInfoContextHelper.getTenantId();
    String merchantNo = LoginInfoContextHelper.getMerchantNo();
    if (digitalAssetsItemReq.getMerchantNo() != null) {
      merchantNo = digitalAssetsItemReq.getMerchantNo();
    }
    List<String> assetsTypes = (List<String>) requestMap.get("assetsTypes");
    String assetsType = (String) requestMap.get("assetsType");
    String obtainMethod = (String) requestMap.get("obtainMethod");
    String digitalAssetsItemNo = (String) requestMap.get("digitalAssetsItemNo");
    if (digitalAssetsItemNo == null) {
      digitalAssetsItemNo = (String) requestMap.get("serialNo");
    }
    Object paginationObj =  requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj,pagination);
    Page<DigitalAssetsItem> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());

    IPage<DigitalAssetsItem> digitalAssetsItemPageList =
        digitalAssetsItemMapper.selectDigitalAssetsPageList(
            page, tenantId, merchantNo, assetsTypes, assetsType, obtainMethod, digitalAssetsItemNo);

    List<String> merchantNos = new ArrayList<>();
    for (DigitalAssetsItem digitalAssetsItemRes : digitalAssetsItemPageList.getRecords()) {
      // 获取商户信息
      merchantNos.add(digitalAssetsItemRes.getMerchantNo());
    }
    if (merchantNos.size() > 0) {
      Map crmReqMap = new HashMap();
      crmReqMap.put("merchantNos", merchantNos);
      Map<String, Object> merchantResult = merchantService.getListByMerchantNos(crmReqMap);
      List<Merchant> merchantList = (List<Merchant>) merchantResult.get("data");
      List<DigitalAssetsItem> digitalAssetsItemList = new ArrayList<>();
      for (DigitalAssetsItem digitalAssetsItem : digitalAssetsItemPageList.getRecords()) {
        // 找出商户信息
        for (Merchant merchant : merchantList) {
          if (digitalAssetsItem.getMerchantNo().equals(merchant.getSerialNo())) {
            digitalAssetsItem.setMerchantName(merchant.getMerchantName());
            digitalAssetsItem.setMerchantLogo(merchant.getLogoUrl());
          }
        }
        digitalAssetsItemList.add(digitalAssetsItem);
      }
      digitalAssetsItemPageList.setRecords(digitalAssetsItemList);
    }
    return RespBodyHandler.setRespPageInfoBodyDto(digitalAssetsItemPageList);
  }

  /**
   * @param requestMap
   * @return
   */
  @ShenyuDubboClient("/getDetail")
  @ApiDoc(desc = "getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    // 客户资产编号
    String digitalAssetsItemNo = MapUtils.getString(requestMap, "digitalAssetsItemNo");
    if (digitalAssetsItemNo == null) {
      digitalAssetsItemNo = MapUtils.getString(requestMap, "serialNo");
    }

    DigitalAssetsItem digitalAssetsItem = digitalAssetsItemMapper.selectById(digitalAssetsItemNo);
    DigitalAssetsDetailRes digitalAssetsDetailRes =
        digitalAssetsItemBiz.getDetail(
            digitalAssetsItem.getDigitalAssetsCollectionNo(),
            digitalAssetsItemNo,
            digitalAssetsItem.getTokenId());
    // TODO 成交记录数据
    return RespBodyHandler.setRespBodyDto(digitalAssetsDetailRes);
  }

  /**
   * 查询商户发行的数字会员卡
   *
   * @param requestMap
   * @return
   */
  @ShenyuDubboClient("/getPassCard")
  @ApiDoc(desc = "getPassCard")
  @Override
  public Map<String, Object> getPassCard(Map<String, Object> requestMap) {
    String tenantId = LoginInfoContextHelper.getTenantId();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    // 查询商户下的数字资产
    if (merchantNo == null) {
      merchantNo = LoginInfoContextHelper.getMerchantNo();
    }

    List<String> assetsTypes = new ArrayList<String>(Arrays.asList("5"));
    List<DigitalAssetsItemRes> digitalAssetsItemList =
        digitalAssetsItemMapper.selectDigitalAssetsList(tenantId, merchantNo, assetsTypes);

    List<DigitalAssetsItemRes> digitalAssetsItemListRes = new ArrayList<>();

    List<String> customerNos = new ArrayList<>();
    for (DigitalAssetsItemRes digitalAssetsItem : digitalAssetsItemList) {
      // 获取商户信息
      customerNos.add(digitalAssetsItem.getMerchantNo());
    }
    if (customerNos.size() > 0) {
      Map crmReqMap = new HashMap();
      crmReqMap.put("customerNos", customerNos);
      Map<String, Object> customerResult = customerService.getListByCustomerNos(crmReqMap);
      List<Map> customerList = (List<Map>) customerResult.get("data");
      for (DigitalAssetsItemRes digitalAssetsItem : digitalAssetsItemList) {
        // 找出客户信息
        for (Map customer : customerList) {
          if (digitalAssetsItem.getMerchantNo().equals(customer.get("customerNo"))) {
            digitalAssetsItem.setMerchantName((String) customer.get("username"));
          }
        }
        digitalAssetsItemListRes.add(digitalAssetsItem);
      }
    }
    return RespBodyHandler.setRespBodyListDto(digitalAssetsItemListRes);
  }

  @ShenyuDubboClient("/getObtainCodePageList")
  @ApiDoc(desc = "getObtainCodePageList")
  @Override
  public Map<String, Object> getObtainCodePageList(Map<String, Object> requestMap) {
    String assetsNo = MapUtils.getString(requestMap, "assetsNo");
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<DigitalAssetsItemObtainCode> page =
        new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<DigitalAssetsItemObtainCode> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(DigitalAssetsItemObtainCode::getCreateTime);
    warapper.eq(ObjectUtil.isNotNull(assetsNo), DigitalAssetsItemObtainCode::getAssetsNo, assetsNo);
    IPage<DigitalAssetsItemObtainCode> pageList =
        digitalAssetsItemObtainCodeMapper.selectPage(page, warapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  @ShenyuDubboClient("/equityConfig")
  @ApiDoc(desc = "equityConfig")
  @Override
  public Map<String, Object> equityConfig(Map<String, Object> requestMap) {
    // TODO 参数校验待处理
    equityService.add(requestMap);
    return RespBodyHandler.RespBodyDto();
  }
}
