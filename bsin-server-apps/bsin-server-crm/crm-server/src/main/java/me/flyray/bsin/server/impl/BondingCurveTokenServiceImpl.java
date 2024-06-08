package me.flyray.bsin.server.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Account;
import me.flyray.bsin.domain.entity.BondingCurveTokenJournal;
import me.flyray.bsin.domain.entity.BondingCurveTokenParam;
import me.flyray.bsin.domain.enums.AccountCategory;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.BondingCurveTokenService;
import me.flyray.bsin.infrastructure.mapper.BondingCurveTokenJournalMapper;
import me.flyray.bsin.infrastructure.mapper.BondingCurveTokenParamMapper;
import me.flyray.bsin.redis.provider.BsinCacheProvider;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.biz.CustomerAccountBiz;
import me.flyray.bsin.server.biz.TokenReleaseBiz;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static me.flyray.bsin.constants.ResponseCode.*;

/**
 * @author bolei
 * @date 2023/7/19 21:14
 * @desc
 */

@ShenyuDubboService(path = "/bondingCurveToken", timeout = 6000)
@ApiModule(value = "bondingCurveToken")
@Service
@Slf4j
public class BondingCurveTokenServiceImpl implements BondingCurveTokenService {

  @Autowired private BondingCurveTokenJournalMapper bondingCurveTokenJournalMapper;
  @Autowired private BondingCurveTokenParamMapper bondingCurveTokenParamMapper;
  @Autowired private CustomerAccountBiz customerAccountBiz;
  @Autowired private TokenReleaseBiz tokenReleaseBiz;

  /**
   * TODO: 1个商户只能插入一条// TODO: 1个商户只能插入一条
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public BondingCurveTokenParam addCurve(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    BondingCurveTokenParam bondingCurveTokenParam =
        BsinServiceContext.getReqBodyDto(BondingCurveTokenParam.class, requestMap);
    String tenantId = bondingCurveTokenParam.getTenantId();
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }
    String merchantNo = bondingCurveTokenParam.getMerchantNo();
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
    }
    String customerNo = bondingCurveTokenParam.getMerchantNo();
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }

    LambdaQueryWrapper<BondingCurveTokenParam> warapper = new LambdaQueryWrapper<>();
    warapper.eq(BondingCurveTokenParam::getTenantId, tenantId);
    warapper.eq(BondingCurveTokenParam::getMerchantNo, merchantNo);
    warapper.eq(
        ObjectUtil.isNotNull(bondingCurveTokenParam.getType()),
        BondingCurveTokenParam::getType,
        bondingCurveTokenParam.getType());
    warapper.eq(
        ObjectUtil.isNotNull(bondingCurveTokenParam.getVersion()),
        BondingCurveTokenParam::getVersion,
        bondingCurveTokenParam.getVersion());
    warapper.eq(
        ObjectUtil.isNotNull(bondingCurveTokenParam.getSerialNo()),
        BondingCurveTokenParam::getSerialNo,
        bondingCurveTokenParam.getSerialNo());
    BondingCurveTokenParam bondingCurveToken = bondingCurveTokenParamMapper.selectOne(warapper);
    if (bondingCurveToken != null) {
      throw new BusinessException(BC_POINTS_EXISTS);
    }

    bondingCurveTokenParam.setTenantId(tenantId);
    bondingCurveTokenParam.setMerchantNo(merchantNo);
    bondingCurveTokenParam.setVersion("0.1");
    bondingCurveTokenParam.setStatus("1");
    bondingCurveTokenParamMapper.insert(bondingCurveTokenParam);

    // 添加曲线的同时初始化一条数据
    try {
      BondingCurveTokenJournal bondingCurveTokenJournal = new BondingCurveTokenJournal();
      bondingCurveTokenJournal.setSupply(new BigDecimal(0));
      bondingCurveTokenJournal.setPrice(
          new BigDecimal(MapUtils.getString(requestMap, "initialPrice")));
      bondingCurveTokenJournal.setTenantId(tenantId);
      bondingCurveTokenJournal.setMerchantNo(merchantNo);
      bondingCurveTokenJournal.setCustomerNo(customerNo);
      bondingCurveTokenJournal.setReserve(new BigDecimal(0));
      bondingCurveTokenJournal.setSerialNo(BsinSnowflake.getId());
      bondingCurveTokenJournal.setTxHash(BsinSnowflake.getId());
      bondingCurveTokenJournal.setMethod("init");
      bondingCurveTokenJournalMapper.insert(bondingCurveTokenJournal);
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
    return bondingCurveTokenParam;
  }

  @ApiDoc(desc = "deleteCurve")
  @ShenyuDubboClient("/deleteCurve")
  @Override
  public void deleteCurve(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    bondingCurveTokenParamMapper.deleteById(serialNo);
  }

  @ApiDoc(desc = "editCurve")
  @ShenyuDubboClient("/editCurve")
  @Override
  public Map<String, Object> editCurve(Map<String, Object> requestMap) {
    throw new BusinessException("100000", "联合曲线参数一经发布，不支持修改！！！");
    //    BondingCurveTokenParam bondingCurveTokenParam =
    //        BsinServiceContext.getReqBodyDto(BondingCurveTokenParam.class, requestMap);
    //    bondingCurveTokenParamMapper.updateById(bondingCurveTokenParam);
    //    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "getCurvePageList")
  @ShenyuDubboClient("/getCurvePageList")
  @Override
  public IPage<?> getCurvePageList(Map<String, Object> requestMap) {

    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String merchantNo = loginUser.getMerchantNo();
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<BondingCurveTokenParam> page =
        new Page<>(pagination.getPageNum(), pagination.getPageSize());

    BondingCurveTokenParam bondingCurveTokenParam =
        BsinServiceContext.getReqBodyDto(BondingCurveTokenParam.class, requestMap);
    LambdaQueryWrapper<BondingCurveTokenParam> warapper = new LambdaQueryWrapper<>();
    warapper.eq(BondingCurveTokenParam::getTenantId, tenantId);
    warapper.eq(BondingCurveTokenParam::getMerchantNo, merchantNo);
    warapper.eq(
        ObjectUtil.isNotNull(bondingCurveTokenParam.getType()),
        BondingCurveTokenParam::getType,
        bondingCurveTokenParam.getType());
    warapper.eq(
        ObjectUtil.isNotNull(bondingCurveTokenParam.getVersion()),
        BondingCurveTokenParam::getVersion,
        bondingCurveTokenParam.getVersion());
    warapper.eq(
        ObjectUtil.isNotNull(bondingCurveTokenParam.getSerialNo()),
        BondingCurveTokenParam::getSerialNo,
        bondingCurveTokenParam.getSerialNo());
    IPage<BondingCurveTokenParam> pageList =
        bondingCurveTokenParamMapper.selectPage(page, warapper);
    return pageList;
  }

  @ApiDoc(desc = "getCurveDetail")
  @ShenyuDubboClient("/getCurveDetail")
  @Override
  public BondingCurveTokenParam getCurveDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    BondingCurveTokenParam bondingCurveTokenParam =
        bondingCurveTokenParamMapper.selectById(serialNo);
    return bondingCurveTokenParam;
  }

  @ApiDoc(desc = "getCurveList")
  @ShenyuDubboClient("/getCurveList")
  @Override
  public List<?> getCurveList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    BondingCurveTokenParam bondingCurveTokenParam =
        BsinServiceContext.getReqBodyDto(BondingCurveTokenParam.class, requestMap);
    String tenantId = bondingCurveTokenParam.getTenantId();
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }
    String merchantNo = bondingCurveTokenParam.getMerchantNo();
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
    }
    LambdaQueryWrapper<BondingCurveTokenParam> warapper = new LambdaQueryWrapper<>();
    warapper.eq(BondingCurveTokenParam::getTenantId, tenantId);
    warapper.eq(BondingCurveTokenParam::getMerchantNo, merchantNo);
    warapper.eq(
        ObjectUtil.isNotNull(bondingCurveTokenParam.getType()),
        BondingCurveTokenParam::getType,
        bondingCurveTokenParam.getType());
    warapper.eq(
        ObjectUtil.isNotNull(bondingCurveTokenParam.getVersion()),
        BondingCurveTokenParam::getVersion,
        bondingCurveTokenParam.getVersion());
    warapper.eq(
        ObjectUtil.isNotNull(bondingCurveTokenParam.getSerialNo()),
        BondingCurveTokenParam::getSerialNo,
        bondingCurveTokenParam.getSerialNo());

    List<BondingCurveTokenParam> bondingCurveTokenList =
        bondingCurveTokenParamMapper.selectList(warapper);
    return bondingCurveTokenList;
  }

  @ApiDoc(desc = "getMerchantCurve")
  @ShenyuDubboClient("/getMerchantCurve")
  @Override
  public BondingCurveTokenParam getMerchantCurve(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();

    BondingCurveTokenParam bondingCurveTokenParam =
        BsinServiceContext.getReqBodyDto(BondingCurveTokenParam.class, requestMap);
    String tenantId = bondingCurveTokenParam.getTenantId();
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }
    String merchantNo = bondingCurveTokenParam.getMerchantNo();
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(MERCHANT_NO_IS_NULL);
      }
    }
    LambdaQueryWrapper<BondingCurveTokenParam> warapper = new LambdaQueryWrapper<>();
    warapper.eq(BondingCurveTokenParam::getTenantId, tenantId);
    warapper.eq(BondingCurveTokenParam::getMerchantNo, merchantNo);
    warapper.eq(
        ObjectUtil.isNotNull(bondingCurveTokenParam.getType()),
        BondingCurveTokenParam::getType,
        bondingCurveTokenParam.getType());
    warapper.eq(
        ObjectUtil.isNotNull(bondingCurveTokenParam.getVersion()),
        BondingCurveTokenParam::getVersion,
        bondingCurveTokenParam.getVersion());
    warapper.eq(
        ObjectUtil.isNotNull(bondingCurveTokenParam.getSerialNo()),
        BondingCurveTokenParam::getSerialNo,
        bondingCurveTokenParam.getSerialNo());
    BondingCurveTokenParam bondingCurveToken = bondingCurveTokenParamMapper.selectOne(warapper);
    return bondingCurveToken;
  }

  @ApiDoc(desc = "mint")
  @ShenyuDubboClient("/mint")
  @Transactional
  @Override
  public Map<String, Object> mint(Map<String, Object> requestMap)
      throws UnsupportedEncodingException {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String bcCurveNo = (String) requestMap.get("bcCurveNo");
    String ccy = (String) requestMap.get("ccy");

    String tenantId = (String) requestMap.get("tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }
    String merchantNo = (String) requestMap.get("merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
    }
    String customerNo = (String) requestMap.get("customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    BondingCurveTokenJournal bondingCurveTokenJournal =
        BsinServiceContext.getReqBodyDto(BondingCurveTokenJournal.class, requestMap);

    // 1.最少期望铸造的积分数量
    String minMintAmount = MapUtils.getString(requestMap, "minMintAmount");
    // 2.要铸造的劳动价值
    String amount = MapUtils.getString(requestMap, "amount");

    // 3.获取联合曲线配置参数：名称|符号|小数点|flexible|...
    BondingCurveTokenParam bondingCurveTokenParam =
            BsinCacheProvider.get(
            "BondingCurveParam：" + tenantId + "-" + merchantNo, BondingCurveTokenParam.class);
    if (bcCurveNo == null) {
      LambdaQueryWrapper<BondingCurveTokenParam> warapper = new LambdaQueryWrapper<>();
      warapper.eq(BondingCurveTokenParam::getTenantId, tenantId);
      warapper.eq(BondingCurveTokenParam::getMerchantNo, merchantNo);
      bondingCurveTokenParam = bondingCurveTokenParamMapper.selectOne(warapper);

    } else {
      bondingCurveTokenParam = bondingCurveTokenParamMapper.selectById(bcCurveNo);
    }
    BsinCacheProvider.put("crm",
        "BondingCurveParam：" + tenantId + "-" + merchantNo, bondingCurveTokenParam);

    if (!bondingCurveTokenParam.getStatus().equals("1")) {
      throw new BusinessException(TRANSACTION_ON_PAUSE);
    }

    // 4.查询最后一条铸造流水
    BondingCurveTokenJournal lastBondingCurveTokenJournal =
        bondingCurveTokenJournalMapper.selectLastOne(tenantId, merchantNo);
    log.debug("bondingCurve parameter:", bondingCurveTokenParam.toString());

    // 5.根据联合曲线计算应该得到的积分数量
    BigDecimal rewardAmount =
        bondingCurveMintByDeposit(
                bondingCurveTokenParam,
                new BigDecimal(amount),
                lastBondingCurveTokenJournal.getSupply())
            .setScale(0, ROUND_HALF_UP);

    Map<String, Object> result = new HashMap<String, Object>();
    if (minMintAmount != null) {
      if (rewardAmount.compareTo(new BigDecimal(minMintAmount)) < 0) {
        throw new BusinessException(DESIRED_MINT_FAILED);
      }
    }
    // 6.更新当前价格和供应量
    BigDecimal supply = lastBondingCurveTokenJournal.getSupply().add(rewardAmount);
    BigDecimal price = bondingCurvePrice(bondingCurveTokenParam, supply);
    BigDecimal reserveBalance =
        lastBondingCurveTokenJournal.getReserve().add(new BigDecimal(amount));
    String amountToken = rewardAmount.toString();

    result.put("amount", amountToken);
    result.put("price", price.toString());
    result.put("supply", supply.toString());
    result.put("reserve", reserveBalance.toString());
    //        result.put("txHash", txHash);

    // 7.insert mint流水
    bondingCurveTokenJournal.setTenantId(tenantId);
    bondingCurveTokenJournal.setMerchantNo(merchantNo);
    bondingCurveTokenJournal.setCustomerNo(customerNo);
    bondingCurveTokenJournal.setSupply(supply);
    bondingCurveTokenJournal.setPrice(price);
    bondingCurveTokenJournal.setReserve(reserveBalance);
    bondingCurveTokenJournal.setSerialNo(BsinSnowflake.getId());
    bondingCurveTokenJournal.setTxHash(BsinSnowflake.getId());
    if (ccy == null) {
      ccy = bondingCurveTokenParam.getSymbol();
      if (ccy == null) {
        throw new BusinessException(CCY_NOT_ISNULL);
      }
    }
    try {
      bondingCurveTokenJournalMapper.insert(bondingCurveTokenJournal);
      // 增加流通量
      bondingCurveTokenParam.setCirculation(
          bondingCurveTokenParam.getCirculation().add(rewardAmount));
      bondingCurveTokenParamMapper.updateById(bondingCurveTokenParam);

      // 8.用户BC积分累计账户入账
      Account customerAccount =
          customerAccountBiz.inAccount(
              tenantId,
              customerNo,
              AccountCategory.ACCUMULATED_INCOME.getCode(),
              AccountCategory.ACCUMULATED_INCOME.getDesc(),
              ccy,
              bondingCurveTokenParam.getDecimals(),
              new BigDecimal(amountToken));

      // 9.查询数字积分 tokenParam 参数，根据配置规则： 释放比例|释放周期|释放条件 进行链上数字积分 mint， 同时对 释放账户进行出账操作
      tokenReleaseBiz.bcAccountRelease(customerAccount, new BigDecimal(amountToken));
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
    return result;
  }

  @ApiDoc(desc = "redeem")
  @ShenyuDubboClient("/redeem")
  @Override
  public Map<String, Object> redeem(Map<String, Object> requestMap)
      throws UnsupportedEncodingException {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String merchantNo = loginUser.getMerchantNo();
    String customerNo = loginUser.getCustomerNo();

    String ccy = "CNY";

    String accountCategory = MapUtils.getString(requestMap, "accountCategory");
    String accountName = MapUtils.getString(requestMap, "accountName");

    BondingCurveTokenJournal bondingCurveTokenJournal =
        BsinServiceContext.getReqBodyDto(BondingCurveTokenJournal.class, requestMap);
    // 最少期望赎回的劳动价值
    String minRedeemAmount = MapUtils.getString(requestMap, "minRedeemAmount");
    // 要销毁的积分
    String amount = MapUtils.getString(requestMap, "amount");
    // 劳动价值描述
    String description = MapUtils.getString(requestMap, "description");

    // 获取曲线参数
    BondingCurveTokenParam bondingCurveTokenParam = BsinCacheProvider.get("crm",
            "BondingCurveParam：" + tenantId + "-" + merchantNo);
    {
      LambdaQueryWrapper<BondingCurveTokenParam> warapper = new LambdaQueryWrapper<>();
      warapper.eq(BondingCurveTokenParam::getTenantId, tenantId);
      warapper.eq(BondingCurveTokenParam::getMerchantNo, merchantNo);
      bondingCurveTokenParam = bondingCurveTokenParamMapper.selectOne(warapper);

      BsinCacheProvider.put("crm",
          "BondingCurveParam：" + tenantId + "-" + merchantNo, bondingCurveTokenParam);
    }
    if (!bondingCurveTokenParam.getStatus().equals("1")) {
      throw new BusinessException(TRANSACTION_ON_PAUSE);
    }
    BondingCurveTokenJournal lastBondingCurveTokenJournal =
        bondingCurveTokenJournalMapper.selectLastOne(tenantId, merchantNo);
    BsinCacheProvider.put("crm",
        "BondingCurveParam：" + tenantId + "-" + merchantNo, lastBondingCurveTokenJournal);
    log.debug("bondingCurve parameter:", lastBondingCurveTokenJournal.toString());

    // 查询商户的联合曲线，根据联合曲线销毁
    BigDecimal redeemAmount =
        (bondingCurveBurn(
                bondingCurveTokenParam,
                new BigDecimal(amount),
                lastBondingCurveTokenJournal.getSupply()))
            .setScale(2, ROUND_HALF_UP);

    Map<String, Object> result = new HashMap<String, Object>();
    if (minRedeemAmount != null) {
      if (redeemAmount.compareTo(new BigDecimal(minRedeemAmount)) < 0) {
        throw new BusinessException(DESIRED_MINT_FAILED);
      }
    }
    // 更新当前价格和供应量
    BigDecimal supply = lastBondingCurveTokenJournal.getSupply().subtract(new BigDecimal(amount));
    BigDecimal price = bondingCurvePrice(bondingCurveTokenParam, supply);
    BigDecimal reserveBalance = lastBondingCurveTokenJournal.getReserve().subtract(redeemAmount);
    result.put("amount", redeemAmount.toString());
    result.put("price", price.toString());
    result.put("supply", supply.toString());
    result.put("reserve", reserveBalance.toString());
    //        result.put("txHash", txHash);

    bondingCurveTokenJournal.setTenantId(tenantId);
    bondingCurveTokenJournal.setMerchantNo(merchantNo);
    bondingCurveTokenJournal.setMerchantNo(customerNo);
    bondingCurveTokenJournal.setSerialNo(BsinSnowflake.getId());
    bondingCurveTokenJournal.setSupply(supply);
    bondingCurveTokenJournal.setPrice(price);
    bondingCurveTokenJournal.setReserve(reserveBalance);
    bondingCurveTokenJournal.setTxHash(BsinSnowflake.getId());
    bondingCurveTokenJournalMapper.insert(bondingCurveTokenJournal);

    // 用户积分出账
    customerAccountBiz.outAccount(
        tenantId,
        customerNo,
        accountCategory,
        accountName,
        ccy,
        bondingCurveTokenParam.getDecimals(),
        new BigDecimal(amount));

    return result;
  }

  @ApiDoc(desc = "getBondingCurveTokenJournalList")
  @ShenyuDubboClient("/getBondingCurveTokenJournalList")
  @Override
  public List<?> getBondingCurveTokenJournalList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String merchantNo = loginUser.getMerchantNo();
    BigInteger limit = new BigInteger(MapUtils.getString(requestMap, "limit"));
    List<BondingCurveTokenJournal> list =
        bondingCurveTokenJournalMapper.selectCurveList(tenantId, merchantNo, limit.intValue());

    LambdaQueryWrapper<BondingCurveTokenParam> warapper = new LambdaQueryWrapper<>();
    warapper.eq(BondingCurveTokenParam::getTenantId, tenantId);
    warapper.eq(BondingCurveTokenParam::getMerchantNo, merchantNo);
    BondingCurveTokenParam bondingCurveTokenParam =
        bondingCurveTokenParamMapper.selectOne(warapper);

    Iterator<BondingCurveTokenJournal> it = list.iterator();
    int size = list.size();
    // 曲线固定显示请求点位数
    // 分子
    int numerator = limit.intValue();
    // 分母
    int denominator = list.size();
    if (numerator <= 0) {
      throw new BusinessException(ResponseCode.INVALID_FIELDS);
    }

    if (denominator > numerator) {
      // 简化到10以内
      int tmp = numerator;
      int j = 0;
      while (tmp / 10 > 0) {
        j++;
        tmp = tmp / 10;
      }
      if (j > 0) {
        numerator = numerator / (j * 10);
        denominator = denominator / (j * 10);
      }
      while (numerator % denominator != 0) {
        int temp = numerator % denominator;
        numerator = denominator;
        denominator = temp;
      }
      int maxCommonDivisor = denominator;
      numerator = limit.intValue() / (j * 10) / maxCommonDivisor;
      denominator = list.size() / (j * 10) / maxCommonDivisor;

      for (int i = 0; i < (size - 1); i++) {
        BondingCurveTokenJournal tokenJournal = it.next();
        tokenJournal.setSupply(
            tokenJournal
                .getSupply()
                .divide(
                    BigDecimal.valueOf(
                        Math.pow(10, bondingCurveTokenParam.getDecimals().longValue())),
                    5,
                    ROUND_HALF_UP));
        if (i == 0) {
          it.remove();
        } else if (i == 1) {
          // pass
        } else if ((i % denominator) >= numerator) {
          it.remove();
        }
      }
    } else {
      for (int i = 0; i < (size - 1); i++) {
        BondingCurveTokenJournal tokenJournal = it.next();
        tokenJournal.setSupply(
            tokenJournal
                .getSupply()
                .divide(
                    BigDecimal.valueOf(
                        Math.pow(10, bondingCurveTokenParam.getDecimals().longValue())),
                    5,
                    ROUND_HALF_UP));
      }
    }
    return list;
  }

  @ApiDoc(desc = "getBondingCurveTokenTrendList")
  @ShenyuDubboClient("/getBondingCurveTokenTrendList")
  @Override
  public List<?> getBondingCurveTokenTrendList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String merchantNo = loginUser.getMerchantNo();
    BigInteger limit = new BigInteger(MapUtils.getString(requestMap, "limit"));
    List<BondingCurveTokenJournal> list = new ArrayList<BondingCurveTokenJournal>();
    LambdaQueryWrapper<BondingCurveTokenParam> warapper = new LambdaQueryWrapper<>();
    warapper.eq(BondingCurveTokenParam::getTenantId, tenantId);
    warapper.eq(BondingCurveTokenParam::getMerchantNo, merchantNo);
    BondingCurveTokenParam bondingCurveTokenParam =
        bondingCurveTokenParamMapper.selectOne(warapper);
    if (bondingCurveTokenParam != null) {
      // generate limit 条数据
      BigDecimal price;
      BigDecimal step =
          (bondingCurveTokenParam.getCap()).divide(new BigDecimal(limit), 0, ROUND_HALF_UP);
      BigDecimal supply = new BigDecimal("0");
      for (int i = 0; i < limit.intValue(); i++) {
        BondingCurveTokenJournal bondingCurveTokenJournal = new BondingCurveTokenJournal();
        price = bondingCurvePrice(bondingCurveTokenParam, supply);
        bondingCurveTokenJournal.setSupply(supply);
        bondingCurveTokenJournal.setPrice(price);
        supply = supply.add(step);
        list.add(bondingCurveTokenJournal);
      }
    }
    return list;
  }

  @ApiDoc(desc = "getBondingCurveTokenJournalPageList")
  @ShenyuDubboClient("/getBondingCurveTokenJournalPageList")
  @Override
  public IPage<?> getBondingCurveTokenJournalPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<BondingCurveTokenJournal> page =
        new Page<>(pagination.getPageNum(), pagination.getPageSize());
    BondingCurveTokenJournal bondingCurveTokenJournal =
        BsinServiceContext.getReqBodyDto(BondingCurveTokenJournal.class, requestMap);
    LambdaUpdateWrapper<BondingCurveTokenJournal> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(BondingCurveTokenJournal::getCreateTime);
    warapper.eq(BondingCurveTokenJournal::getTenantId, loginUser.getTenantId());
    warapper.eq(BondingCurveTokenJournal::getMerchantNo, loginUser.getMerchantNo());
    warapper.eq(
        ObjectUtil.isNotNull(bondingCurveTokenJournal.getMethod()),
        BondingCurveTokenJournal::getMethod,
        bondingCurveTokenJournal.getMethod());
    IPage<BondingCurveTokenJournal> pageList =
        bondingCurveTokenJournalMapper.selectPage(page, warapper);
    return pageList;
  }

  @ApiDoc(desc = "getTransactionDetail")
  @ShenyuDubboClient("/getTransactionDetail")
  @Override
  public BondingCurveTokenJournal getTransactionDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    BondingCurveTokenJournal bondingCurveTokenJournal =
        bondingCurveTokenJournalMapper.selectById(serialNo);
    return bondingCurveTokenJournal;
  }

  /**
   * 基于联合曲线积分铸造：捕获劳动价值，获得的积分记录,带18位小数点
   *
   * @throws Exception
   */
  private BigDecimal bondingCurveMintByDeposit(
      BondingCurveTokenParam bondingCurveTokenParam,
      BigDecimal amountLaborValue,
      BigDecimal supply) {
    BigDecimal rewardAmount = new BigDecimal("0");
    // 100元铸造一次
    BigDecimal loop = amountLaborValue.divide(BigDecimal.valueOf(100.0), 5, ROUND_HALF_UP);
    BigDecimal remainder = amountLaborValue.remainder(BigDecimal.valueOf(100));
    BigDecimal currentSupply = supply;
    // 每100元重新计算一次
    for (int i = 0; i < loop.intValue(); i++) {
      BigDecimal unitPrice = bondingCurvePrice(bondingCurveTokenParam, currentSupply);
      BigDecimal amountPer100 = (BigDecimal.valueOf(100.0)).divide(unitPrice, 5, ROUND_HALF_UP);
      rewardAmount =
          rewardAmount.add(
              amountPer100
                  .multiply(
                      BigDecimal.valueOf(
                          Math.pow(10, bondingCurveTokenParam.getDecimals().longValue())))
                  .setScale(0, ROUND_HALF_UP));
      currentSupply = currentSupply.add(rewardAmount);
    }
    if (remainder.compareTo(BigDecimal.valueOf(0)) == 1) {
      BigDecimal price = bondingCurvePrice(bondingCurveTokenParam, currentSupply);
      BigDecimal remainderReward = remainder.divide(price, 5, ROUND_HALF_UP);
      rewardAmount = rewardAmount.add(remainderReward);
    }
    return rewardAmount;
  }

  /**
   * 基于联合曲线积分销毁：销毁积分，赎回劳动价值
   *
   * @throws Exception
   */
  private BigDecimal bondingCurveBurn(
      BondingCurveTokenParam bondingCurveTokenParam,
      BigDecimal amountTokenBurn,
      BigDecimal supply) {
    BigDecimal redeemAmount = new BigDecimal("0");
    BigDecimal currentSupply = supply.subtract(amountTokenBurn);
    // 获取价格：最低价格赎回
    BigDecimal unitPrice = bondingCurvePrice(bondingCurveTokenParam, currentSupply);
    redeemAmount =
        (unitPrice.multiply(amountTokenBurn))
            .divide(
                BigDecimal.valueOf(Math.pow(10, bondingCurveTokenParam.getDecimals().longValue())),
                2,
                ROUND_HALF_UP);

    return redeemAmount;
  }

  /**
   * @description: 基于sigmoid曲线公式的联合曲线积分铸造
   * @param cap: token理论上限值，基于联合曲线铸造的积分为增发不限量模型，cap为理论上限值，当流通量为此数值时稳定为 finalPrice 价格
   * @param initialPrice：初始定价：0.01
   * @param finalPrice：稳定定价-可以对标法币进行劳动价值捕获，当前任务价值￥100，则按照当前价格铸造出相应的积分
   * @param flexible：曲线的拉伸变换，越大代表压缩的最厉害，中间（x坐标cap/2点周围）加速度越大；越小越接近匀加速。理想的S曲线 flexible的取值为4-6。
   * @param currentSupply：当前代币供应量，区间[0,cap]
   * @return currentPrice：当前mint价格
   */
  private BigDecimal bondingCurvePrice(
      BondingCurveTokenParam bondingCurveTokenParam, BigDecimal currentSupply) {
    BigDecimal num =
        bondingCurveTokenParam.getCap().divide(BigDecimal.valueOf(2.0), 5, ROUND_HALF_UP);
    double melo =
        (((currentSupply.subtract(num)).divide(num, 5, ROUND_HALF_UP))
                .multiply(bondingCurveTokenParam.getFlexible()))
            .doubleValue();
    double deno = 1.0 / (1 + Math.exp(-melo));
    BigDecimal currentPrice =
        bondingCurveTokenParam
            .getInitialPrice()
            .subtract(
                (bondingCurveTokenParam
                        .getInitialPrice()
                        .subtract(bondingCurveTokenParam.getFinalPrice()))
                    .multiply(BigDecimal.valueOf(deno)));
    return currentPrice;
  }
  //    double bondingCurvePrice(BigDecimal currentSupply) {
  //        BigDecimal num = cap.divide(BigDecimal.valueOf(2.0),5,BigDecimal.ROUND_HALF_UP);
  //        double melo =
  // currentSupply.subtract(num).divide(num,5,BigDecimal.ROUND_HALF_UP).multiply(flexible).doubleValue();
  //        double deno = 1.0 / (1 + Math.exp(-melo));
  //        double currentPrice = initialPrice - (initialPrice - finalPrice) * deno;
  //        return (double)currentPrice;
  //    }

}
