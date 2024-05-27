package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import me.flyray.bsin.facade.service.AccountService;
import me.flyray.bsin.redis.manager.BsinCacheProvider;
import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.annotations.CaptureCustomerBehavior;
import me.flyray.bsin.domain.domain.Account;
import me.flyray.bsin.domain.domain.DigitalAssetsCollection;
import me.flyray.bsin.domain.domain.TokenParam;
import me.flyray.bsin.domain.domain.TokenReleaseJournal;
import me.flyray.bsin.domain.enums.AccountCategory;
import me.flyray.bsin.domain.enums.BehaviorCode;
import me.flyray.bsin.enums.CustomerType;
import me.flyray.bsin.facade.service.DigitalPointsService;
import me.flyray.bsin.facade.service.TokenParamService;
import me.flyray.bsin.infrastructure.mapper.CustomerPassCardMapper;
import me.flyray.bsin.infrastructure.mapper.DigitalAssetsCollectionMapper;
import me.flyray.bsin.infrastructure.mapper.TokenParamMapper;
import me.flyray.bsin.infrastructure.mapper.TokenReleaseJournalMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author bolei
 * @date 2023/8/22
 * @desc
 */
@Slf4j
@ShenyuDubboService(path = "/token", timeout = 6000)
@ApiModule(value = "token")
@Service
public class TokenParamServiceImpl implements TokenParamService {

  @Autowired private TokenParamMapper tokenParamMapper;
  @Autowired private TokenReleaseJournalMapper tokenReleaseJournalMapper;
  @Autowired private DigitalAssetsCollectionMapper digitalAssetsCollectionMapper;
  @Autowired private CustomerPassCardMapper customerPassCardMapper;
  @Autowired private DigitalPointsService digitalPointsService;
  @Autowired private BsinCacheProvider bsinCacheProvider;

  @DubboReference(version = "dev")
  private AccountService accountService;

  @CaptureCustomerBehavior(behaviorCode = BehaviorCode.ISSUE, customerType = CustomerType.MEMBER)
  @Override
  public Map<String, Object> edit(Map<String, Object> requestMap) {
    TokenParam tokenParam = BsinServiceContext.getReqBodyDto(TokenParam.class, requestMap);
    BigDecimal melo = new BigDecimal(Math.pow(10, tokenParam.getDecimals().doubleValue()));
    if (tokenParam.getUnitReleaseAmout() != null) {
      tokenParam.setUnitReleaseAmout(tokenParam.getUnitReleaseAmout().multiply(melo));
    }
    if (tokenParam.getUnitReleaseTriggerValue() != null) {
      tokenParam.setUnitReleaseTriggerValue(tokenParam.getUnitReleaseTriggerValue().multiply(melo));
    }
    if (tokenParam.getReservedAmount() != null) {
      tokenParam.setReservedAmount(tokenParam.getReservedAmount().multiply(melo));
    }

    tokenParamMapper.updateById(tokenParam);
    return RespBodyHandler.setRespBodyDto(tokenParam);
  }

  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {
    TokenParam tokenReleaseParam = BsinServiceContext.getReqBodyDto(TokenParam.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<TokenParam> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<TokenParam> warapper = new LambdaUpdateWrapper<>();
    // warapper.eq(TokenReleaseParam::getTenantId, contractProtocol.getTenantId());
    warapper.eq(
        ObjectUtil.isNotNull(tokenReleaseParam.getMerchantNo()),
        TokenParam::getMerchantNo,
        tokenReleaseParam.getMerchantNo());
    IPage<TokenParam> pageList = tokenParamMapper.selectPage(page, warapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  @Override
  public Map<String, Object> getDetailByMerchantNo(Map<String, Object> requestMap) {
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    TokenParam tokenParam =
        tokenParamMapper.selectOne(
            new LambdaQueryWrapper<TokenParam>().eq(TokenParam::getMerchantNo, merchantNo));
    return RespBodyHandler.setRespBodyDto(tokenParam);
  }

  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    String digitalAssetsCollectionNo = MapUtils.getString(requestMap, "digitalAssetsCollectionNo");
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    TokenParam tokenParam = null;
    if (digitalAssetsCollectionNo != null) {
      // 商户后台登录
      LambdaUpdateWrapper<TokenParam> warapper = new LambdaUpdateWrapper<>();
      warapper.eq(TokenParam::getTenantId, LoginInfoContextHelper.getTenantId());
      warapper.eq(TokenParam::getDigitalAssetsCollectionNo, digitalAssetsCollectionNo);
      tokenParam = tokenParamMapper.selectOne(warapper);
    } else if (serialNo != null) {
      tokenParam = tokenParamMapper.selectById(serialNo);
    } else {
      LambdaUpdateWrapper<TokenParam> warapper = new LambdaUpdateWrapper<>();
      warapper.eq(TokenParam::getTenantId, LoginInfoContextHelper.getTenantId());
      warapper.eq(TokenParam::getMerchantNo, merchantNo);
      tokenParam = tokenParamMapper.selectOne(warapper);
    }
    if (tokenParam != null) {
      BigDecimal melo = new BigDecimal(Math.pow(10, tokenParam.getDecimals().doubleValue()));
      if (tokenParam.getCirculation() != null) {
        tokenParam.setCirculation(
            tokenParam.getCirculation().divide(melo, 2, BigDecimal.ROUND_HALF_UP));
      }
      if (tokenParam.getUnitReleaseAmout() != null) {
        tokenParam.setUnitReleaseAmout(
            tokenParam.getUnitReleaseAmout().divide(melo, 2, BigDecimal.ROUND_HALF_UP));
      }
      if (tokenParam.getUnitReleaseTriggerValue() != null) {
        tokenParam.setUnitReleaseTriggerValue(
            tokenParam.getUnitReleaseTriggerValue().divide(melo, 2, BigDecimal.ROUND_HALF_UP));
      }
      if (tokenParam.getTotalSupply() != null) {
        tokenParam.setTotalSupply(
            tokenParam.getTotalSupply().divide(melo, 2, BigDecimal.ROUND_HALF_UP));
      }
      if (tokenParam.getReservedAmount() != null) {
        tokenParam.setReservedAmount(
            tokenParam.getReservedAmount().divide(melo, 2, BigDecimal.ROUND_HALF_UP));
      }
    }
    return RespBodyHandler.setRespBodyDto(tokenParam);
  }

  /**
   * 定时任务是否分配 商户号存在则根据指定商户号去释放分配，不存在则根据查询所有商户去释放 1、查询商户的数字积分tokenParm释放配置参数 2、查询触发释放条件，满足则释放分配
   * 3、查询分配客户群体（持有会员卡的用户参与分配） 4、根据用户持有的曲线积分占比进行分配释放总量
   *
   * @param requestMap
   * @return
   */
  @Override
  public Map<String, Object> release(Map<String, Object> requestMap) throws Exception {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    String customerAccountNo = MapUtils.getString(requestMap, "customerAccountNo");
    Map response = new HashMap<>();
    String ccy = MapUtils.getString(requestMap, "ccy");
    if (ccy == null) {
      response.put("code", "100000");
      response.put("data", "缺少释放币种参数：ccy！！！");
      return RespBodyHandler.setRespBodyDto(response);
      //      throw new BusinessException("100000", "商户未配置释放规则，不支持释放！！！");
    }

    // 1.查询释放参数
    TokenParam tokenReleaseParam =
        tokenParamMapper.selectOne(
            new LambdaQueryWrapper<TokenParam>().eq(TokenParam::getMerchantNo, merchantNo));
    if (tokenReleaseParam == null) {
      response.put("code", "100000");
      response.put("data", "商户未配置释放规则，不支持释放！！！");
      return RespBodyHandler.setRespBodyDto(response);
      //      return RespBodyHandler.setRespBodyDto(new BusinessException("100000",
      // "商户未配置释放规则，不支持释放！！！"));
      //      throw new BusinessException("100000", "商户未配置释放规则，不支持释放！！！");
    }

    // 2.查询联合曲线绑定的数字积分资产集合
    String digitalAssetsCollectionNo = tokenReleaseParam.getDigitalAssetsCollectionNo();
    if (digitalAssetsCollectionNo == null) {
      response.put("code", "100000");
      response.put("data", "商户未配置要释放的数字积分资产编号，情先配置！！！");
      return RespBodyHandler.setRespBodyDto(response);
      //      return RespBodyHandler.setRespBodyDto(
      //          new BusinessException("100000", "商户未配置要释放的数字积分资产编号，情先配置！！！"));
      //      throw new BusinessException("100000", "商户未配置要释放的数字积分资产编号，情先配置！！！");
    }
    DigitalAssetsCollection digitalAssetsCollection =
        digitalAssetsCollectionMapper.selectById(digitalAssetsCollectionNo);
    if (digitalAssetsCollection == null) {
      //      throw new BusinessException("100000", "未查询到数字积分合约集合资产:" + digitalAssetsCollectionNo);
    }
    //        BigDecimal accumulateBcTokenBalance = new BigDecimal(MapUtils.getString(requestMap,
    // "accumulateBcTokenBalance"));
    //        // 查询释放条件参数：曲线积分劳动价值账户单元累计账户余额
    //        BigDecimal unitAccumulateBalance = new BigDecimal(MapUtils.getString(requestMap,
    // "unitAccumulateBalance"));

    //    String ccy = digitalAssetsCollection.getSymbol();
    // 2.查询要释放的账户
    BigDecimal releaseAccountBalance = null;
    Map reqMap = new HashMap<>();
    reqMap.put("tenantId", tenantId);
    reqMap.put("merchantNo", merchantNo);
    reqMap.put("customerNo", customerNo);
    // 优先从缓存中获取
    Account customerAccount =
        bsinCacheProvider.get(
            "customerAccount:" + tenantId + merchantNo + customerNo + ccy, Account.class);
    if (customerAccount == null) {
      if (customerAccountNo == null) {
        // 币种：用币种英文代替
        reqMap.put("ccy", ccy);
        // 账户类别：比如总收入账户、总支出账户
        reqMap.put("category", AccountCategory.BALANCE.getCode());
        reqMap.put("name", AccountCategory.BALANCE.getDesc());
        reqMap.put("openAccount", "false");
      } else {
        reqMap.put("serialId", customerAccountNo);
      }
      Map resMap = (Map) accountService.getDetail(reqMap);
      Map respDataMap = (Map) resMap.get("data");

      if (respDataMap.get("code") != null) {
        response.put("code", "100000");
        response.put("data", "释放余额账户不存在，不满足释放条件！！！");
        return RespBodyHandler.setRespBodyDto(response);
      }

      Map customerAccountMap = (Map) resMap.get("data");
      releaseAccountBalance = (BigDecimal) customerAccountMap.get("balance");
    } else {
      releaseAccountBalance = customerAccount.getBalance();
    }

    // TODO: 根据释放方式：1、劳动价值释放， 2、购买释放， 3、周期释放 优化， 目前只支持：劳动价值释放-单元释放的触发价值(特指捕获劳动价值的积分数量)，每累计达到释放一次
    // tokenReleaseParam.getReleaseMethod();

    // 3.1. 根据兑换比例计算释放量
    BigDecimal releaseAmount = releaseAccountBalance.multiply(tokenReleaseParam.getExchangeRate());

    // 3.2. 判断预留量是否充足
    if (tokenReleaseParam
            .getTotalSupply()
            .subtract(tokenReleaseParam.getCirculation())
            .subtract(releaseAmount)
            .compareTo(new BigDecimal(0))
        < 0) {
      response.put("code", "100000");
      response.put("data", "数字积分可流通量不足，无法释放，请联系社区管理员！！！");
      return RespBodyHandler.setRespBodyDto(response);
    }

    // 3.3.检验释放条件
    int flag = tokenReleaseParam.getUnitReleaseTriggerValue().compareTo(releaseAmount);
    if (flag > 0) {
      response.put("code", "100000");
      response.put(
          "data",
          "不满足释放条件: 释放账户余额："
              + releaseAccountBalance
              + " < 触发余额："
              + tokenReleaseParam.getUnitReleaseTriggerValue().toString()
              + "+预留量："
              + tokenReleaseParam.getReservedAmount().toString());
      return RespBodyHandler.setRespBodyDto(response);
      //      return RespBodyHandler.setRespBodyDto("不满足释放条件: 释放账户余额："
      //              + releaseAccountBalance
      //              + " < 触发余额："
      //              + tokenReleaseParam.getUnitReleaseTriggerValue().toString()
      //              + "+预留量："
      //              + tokenReleaseParam.getReservedAmount().toString()
      //          );
      //      throw new BusinessException(
      //          "100000",
      //          "不满足释放条件: 释放账户余额："
      //              + releaseAccountBalance
      //              + " < 触发余额："
      //              + tokenReleaseParam.getUnitReleaseTriggerValue().toString()
      //              + "+预留量："
      //              + tokenReleaseParam.getReservedAmount().toString());
    }

    //        // TODO 会员数太大需优化，查询分配用户群体，执行token释放分配
    //        List<CustomerPassCard> customerPassCardList = customerPassCardMapper.selectList(new
    // LambdaQueryWrapper<CustomerPassCard>()
    //                .eq(CustomerPassCard::getMerchantNo, customerNo));
    //        // 计算占比进行入账操作
    //        for (CustomerPassCard customerPassCard : customerPassCardList) {
    //            // 查询客户的曲线积分
    //            Map<String, Object> accountReq = new HashMap<>();
    //            accountReq.put("tenantId", customerPassCard.getTenantId());
    //            accountReq.put("customerNo", customerPassCard.getCustomerNo());
    //            accountReq.put("category", AccountCategory.BALANCE.getCode());
    //            accountReq.put("ccy", CcyType.BC.getCode());
    //            Map<String, Object> accountData = customerAccountService.getDetail(accountReq);
    //            Map<String, Object> accountDetail = (Map<String, Object>)accountData.get("data");
    //            // 入账金额
    //            BigDecimal bcTokenBalance = new
    // BigDecimal(accountDetail.get("balance").toString());
    //            BigDecimal amout =
    // bcTokenBalance.divide(accumulateBcTokenBalance).multiply(tokenReleaseParam.getUnitReleaseAmout());
    //            Map<String, Object> inAccountReq = new HashMap<>();
    //            accountReq.put("tenantId", customerPassCard.getTenantId());
    //            accountReq.put("customerNo", customerPassCard.getCustomerNo());
    //            accountReq.put("category", AccountCategory.BALANCE.getCode());
    //            accountReq.put("name", AccountCategory.BALANCE.getDesc());
    //            accountReq.put("ccy", CcyType.DP.getCode());
    //            inAccountReq.put("amout", amout);
    //            // TODO 先入账处理，后期直接改成铸造在链上
    //            customerAccountService.inAccount(inAccountReq);
    //        }

    // 4.释放token，进行链上铸造: releaseAmount
    reqMap.put("amount", releaseAmount);
    reqMap.put("digitalAssetsCollectionNo", digitalAssetsCollectionNo);
    digitalPointsService.mint(reqMap);

    // 5.生成释放分配流水
    TokenReleaseJournal tokenReleaseJournal = new TokenReleaseJournal();
    BeanUtil.copyProperties(tokenReleaseParam, tokenReleaseJournal);
    tokenReleaseJournal.setSerialNo(BsinSnowflake.getId());
    tokenReleaseJournal.setAmout(releaseAmount);
    tokenReleaseJournal.setCustomerNo(customerNo);
    tokenReleaseJournalMapper.insert(tokenReleaseJournal);

    // 6.更新当前流通量
    tokenReleaseParam.setCirculation(
        tokenReleaseParam.getCirculation().add(tokenReleaseParam.getUnitReleaseAmout()));
    tokenParamMapper.updateById(tokenReleaseParam);

    // 7.释放账户 出账操作 不方便 调用 crm 可以放在 调用次方法的biz中
    response.put("code", "000000");
    response.put("data", "release successful ");
    response.put("releaseAmount", releaseAmount);
    return RespBodyHandler.setRespBodyDto(response);
    //    return RespBodyHandler.setRespBodyDto(Boolean.TRUE);
  }

  /**
   * 内部RPC调用
   *
   * @param requestMap
   * @return
   */
  @Override
  public Map<String, Object> releaseBcPointToVirtualAccount(Map<String, Object> requestMap)
      throws Exception {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    Map response = new HashMap<>();

    // 1.查询释放参数
    TokenParam tokenReleaseParam =
        tokenParamMapper.selectOne(
            new LambdaQueryWrapper<TokenParam>().eq(TokenParam::getMerchantNo, merchantNo));
    if (tokenReleaseParam == null) {
      response.put("code", "100000");
      response.put("data", "商户未配置释放规则，不支持释放！！！");
      return RespBodyHandler.setRespBodyDto(response);
    }

    // 2.查询联合曲线绑定的数字积分资产集合
    String digitalAssetsCollectionNo = tokenReleaseParam.getDigitalAssetsCollectionNo();
    if (digitalAssetsCollectionNo == null) {
      response.put("code", "100000");
      response.put("data", "商户未配置要释放的数字积分资产编号，情先配置！！！");
      return RespBodyHandler.setRespBodyDto(response);
    }
    DigitalAssetsCollection digitalAssetsCollection =
        digitalAssetsCollectionMapper.selectById(digitalAssetsCollectionNo);
    if (digitalAssetsCollection == null) {
      response.put("code", "100000");
      response.put("data", "未查询到商户数字积分资产，请先发行数字积分资产！！！");
      return RespBodyHandler.setRespBodyDto(response);
    }
    // 2.从请求参数中查询要释放的账户
    BigDecimal releaseAccountBalance = null;
    Account customerAccount = (Account) requestMap.get("customerAccount");
    releaseAccountBalance = customerAccount.getBalance();

    // TODO: 根据释放方式：1、劳动价值释放， 2、购买释放， 3、周期释放 优化， 目前只支持：劳动价值释放-单元释放的触发价值(特指捕获劳动价值的积分数量)，每累计达到释放一次
    // tokenReleaseParam.getReleaseMethod();
    // 3.1. 根据兑换比例计算释放量
    BigDecimal releaseAmount = releaseAccountBalance.multiply(tokenReleaseParam.getExchangeRate());

    // 3.2. 判断预留量是否充足
    if (tokenReleaseParam
            .getTotalSupply()
            .subtract(tokenReleaseParam.getCirculation())
            .subtract(releaseAmount)
            .compareTo(new BigDecimal(0))
        < 0) {
      response.put("code", "100000");
      response.put("data", "数字积分可流通量不足，无法释放，请联系社区管理员！！！");
      return RespBodyHandler.setRespBodyDto(response);
    }

    // 3.3.检验释放条件
    int flag = tokenReleaseParam.getUnitReleaseTriggerValue().compareTo(releaseAmount);
    if (flag > 0) {
      response.put("code", "100000");
      response.put(
          "data",
          "不满足释放条件: 释放账户余额："
              + releaseAccountBalance
              + " < 触发余额："
              + tokenReleaseParam.getUnitReleaseTriggerValue().toString()
              + "+预留量："
              + tokenReleaseParam.getReservedAmount().toString());
      return RespBodyHandler.setRespBodyDto(response);
    }

    // 4.释放token，进行链上铸造: releaseAmount
    String mintOnChainStr = MapUtils.getString(requestMap, "mintOnChain");
    boolean mintOnChain = false;
    if (mintOnChainStr != null) {
      mintOnChain = Boolean.parseBoolean(mintOnChainStr);
    }
    Map reqMap = new HashMap<>();
    reqMap.put("tenantId", tenantId);
    reqMap.put("merchantNo", merchantNo);
    reqMap.put("customerNo", customerNo);
    reqMap.put("amount", releaseAmount.toString());
    if (mintOnChain) {
      reqMap.put("digitalAssetsCollectionNo", digitalAssetsCollectionNo);
      digitalPointsService.mint(reqMap);
    } else {
      // 虚拟余额账户入账
      reqMap.put("ccy", digitalAssetsCollection.getSymbol());
      reqMap.put("category", AccountCategory.BALANCE.getCode());
      reqMap.put("name", AccountCategory.BALANCE.getDesc());
      accountService.inAccount(reqMap);
    }

    // 5.生成释放分配流水
    TokenReleaseJournal tokenReleaseJournal = new TokenReleaseJournal();
    BeanUtil.copyProperties(tokenReleaseParam, tokenReleaseJournal);
    tokenReleaseJournal.setSerialNo(BsinSnowflake.getId());
    tokenReleaseJournal.setAmout(releaseAmount);
    tokenReleaseJournal.setCustomerNo(customerNo);
    tokenReleaseJournalMapper.insert(tokenReleaseJournal);

    // 6.更新当前流通量
    tokenReleaseParam.setCirculation(
        tokenReleaseParam.getCirculation().add(tokenReleaseParam.getUnitReleaseAmout()));
    tokenParamMapper.updateById(tokenReleaseParam);

    // 7.释放账户 出账操作 不方便 调用 crm 可以放在 调用次方法的biz中
    response.put("code", "000000");
    response.put("data", "release successful ");
    response.put("releaseAmount", releaseAmount);
    return RespBodyHandler.setRespBodyDto(response);
  }
}
