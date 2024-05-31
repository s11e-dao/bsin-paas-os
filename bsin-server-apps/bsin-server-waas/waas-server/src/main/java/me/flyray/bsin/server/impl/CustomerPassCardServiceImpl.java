package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import me.flyray.bsin.domain.domain.Merchant;
import me.flyray.bsin.facade.service.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.CustomerBase;
import me.flyray.bsin.domain.entity.CustomerPassCard;
import me.flyray.bsin.facade.response.DigitalAssetsDetailRes;
import me.flyray.bsin.facade.response.DigitalAssetsItemRes;
import me.flyray.bsin.infrastructure.mapper.CustomerPassCardMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.infrastructure.biz.DigitalAssetsItemBiz;
import me.flyray.bsin.server.utils.RespBodyHandler;

/**
 * @author bolei
 * @date 2023/6/28 16:37
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/customerPassCard", timeout = 6000)
@ApiModule(value = "customerPassCard")
@Service
public class CustomerPassCardServiceImpl implements CustomerPassCardService {

  @Autowired public CustomerPassCardMapper customerPassCardMapper;

  @Autowired private DigitalAssetsItemBiz digitalAssetsItemBiz;

  @DubboReference(version = "${dubbo.provider.version}")
  private CustomerService customerService;

  @DubboReference(version = "${dubbo.provider.version}")
  private MerchantService merchantService;

  @DubboReference(version = "${dubbo.provider.version}")
  private DigitalAssetsCollectionService digitalAssetsCollectionService;

  @DubboReference(version = "${dubbo.provider.version}")
  private DigitalAssetsItemService digitalAssetsItemService;

  @DubboReference(version = "${dubbo.provider.version}")
  private CustomerProfileService customerProfileService;

  /**
   * 统一走 DigitalAssetsCollectionService 的 issue
    */
  @ShenyuDubboClient("/issue")
  @ApiDoc(desc = "issue")
  @Override
  public Map<String, Object> issue(Map<String, Object> requestMap) throws Exception {
    // TODO: issue 抽离biz
    return RespBodyHandler.setRespBodyDto(digitalAssetsCollectionService.issue(requestMap));
  }

  /**
   * 品牌会员开卡:按照顾数字资产上架流程开卡 客户开卡，开卡即 mint 用户购买之后的回调
   *
   * @param requestMap
   * @return
   */
  @ShenyuDubboClient("/claim")
  @ApiDoc(desc = "claim")
  @Override
  public Map<String, Object> claim(Map<String, Object> requestMap) throws Exception {
    requestMap.put("amount", "1");
    requestMap.put("limitClaim", "false");
    digitalAssetsItemService.claim(requestMap);
    Map<String, Object> ret = customerProfileService.follow(requestMap);
    return RespBodyHandler.setRespBodyDto(ret);
  }

  /**
   * 查询我的卡包列表 查询我参与的共建品牌
   *
   * @param requestMap
   * @return
   */
  @ShenyuDubboClient("/getList")
  @ApiDoc(desc = "getList")
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
    CustomerPassCard customerPassCardReq =
        BsinServiceContext.getReqBodyDto(CustomerPassCard.class, requestMap);
    //    LambdaQueryWrapper<CustomerPassCard> warapper = new LambdaQueryWrapper<>();
    //    warapper.eq(CustomerPassCard::getTenantId, tenantId);
    //    warapper.eq(CustomerPassCard::getCustomerNo, customerNo);
    //    List<CustomerPassCard> customerPassCardList = customerPassCardMapper.selectList(warapper);
    List<CustomerPassCard> customerPassCardList =
        customerPassCardMapper.selectCustomerPassCardList(tenantId, customerNo);

    List<String> merchantNos = new ArrayList<>();
    for (CustomerPassCard customerPassCard : customerPassCardList) {
      // 获取商户信息
      merchantNos.add(customerPassCard.getMerchantNo());
    }
    if (merchantNos.size() > 0) {
      Map marketReqMap = new HashMap();
      marketReqMap.put("merchantNos", merchantNos);
      Map<String, Object> merchantResult = merchantService.getListByMerchantNos(marketReqMap);
      List<Merchant> merchantList = (List<Merchant>) merchantResult.get("data");
      for (CustomerPassCard customerPassCard : customerPassCardList) {
        // 找出客户信息
        for (Merchant merchant : merchantList) {
          if (customerPassCard.getMerchantNo().equals(merchant.getSerialNo())) {
            customerPassCard.setMerchantName(merchant.getMerchantName());
            customerPassCard.setMerchantLogo(merchant.getLogoUrl());
          }
        }
      }
    }
    return RespBodyHandler.setRespBodyListDto(customerPassCardList);
  }

  @ShenyuDubboClient("/getMemberList")
  @ApiDoc(desc = "getMemberList")
  @Override
  public Map<String, Object> getMemberList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    CustomerPassCard customerPassCardReq =
        BsinServiceContext.getReqBodyDto(CustomerPassCard.class, requestMap);
    LambdaQueryWrapper<CustomerPassCard> warapper = new LambdaQueryWrapper<>();
    warapper.eq(CustomerPassCard::getTenantId, loginUser.getTenantId());
    warapper.eq(CustomerPassCard::getMerchantNo, customerPassCardReq.getMerchantNo());
    List<CustomerPassCard> customerPassCardList = customerPassCardMapper.selectList(warapper);

    List<String> customerNos = new ArrayList<>();
    for (CustomerPassCard customerPassCard : customerPassCardList) {
      // 获取商户信息
      customerNos.add(customerPassCard.getCustomerNo());
    }
    List<CustomerBase> customerList = new ArrayList<>();
    if (customerNos.size() > 0) {
      Map marketReqMap = new HashMap();
      marketReqMap.put("customerNos", customerNos);
      Map<String, Object> customerResult = customerService.getListByCustomerNos(marketReqMap);
      customerList = (List<CustomerBase>) customerResult.get("data");
    }
    return RespBodyHandler.setRespBodyListDto(customerList);
  }

  @ShenyuDubboClient("/getPageList")
  @ApiDoc(desc = "getPageList")
  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();

    //        String customerNo = (String) requestMap.get("customerNo");
    //        if (customerNo == null) {
    //            // 登录用户
    //            customerNo = loginUser.getCustomerNo();
    //        }
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
    Page<CustomerPassCard> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<CustomerPassCard> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(CustomerPassCard::getCreateTime);
    warapper.eq(CustomerPassCard::getTenantId, tenantId);
    warapper.eq(CustomerPassCard::getMerchantNo, merchantNo);
    IPage<CustomerPassCard> pageList = customerPassCardMapper.selectPage(page, warapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  /**
   * 查询客户在商户下的pass卡
   *
   * @param requestMap
   * @return
   */
  @ShenyuDubboClient("/getDetail")
  @ApiDoc(desc = "getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String tenantId = LoginInfoContextHelper.getTenantId();
    String customerNo = LoginInfoContextHelper.getCustomerNo();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    DigitalAssetsItemRes digitalAssetsItemRes =
        customerPassCardMapper.selectCustomerDigitalAssetsDetail(tenantId, merchantNo, customerNo);

    DigitalAssetsDetailRes digitalAssetsDetailRes = null;
    if (digitalAssetsItemRes != null) {
      digitalAssetsDetailRes =
          digitalAssetsItemBiz.getDetail(
              digitalAssetsItemRes.getDigitalAssetsCollectionNo(),
              digitalAssetsItemRes.getSerialNo(),
              digitalAssetsItemRes.getTokenId());
      digitalAssetsItemRes.setMerchantLogo(digitalAssetsDetailRes.getDigitalAssetsItem().getMerchantLogo());
      digitalAssetsItemRes.setMerchantName(digitalAssetsDetailRes.getDigitalAssetsItem().getMerchantName());
      digitalAssetsDetailRes.setCustomerPassCard(digitalAssetsItemRes);
    }
    // TODO 成交记录数据
    return RespBodyHandler.setRespBodyDto(digitalAssetsDetailRes);
  }

}
