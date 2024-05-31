package me.flyray.bsin.server.impl;

import static me.flyray.bsin.constants.ResponseCode.CUSTOMER_ACCOUNT_IS_NULL;
import static me.flyray.bsin.constants.ResponseCode.TASK_NON_CLAIM_CONDITION;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.AccountFreezeJournal;
import me.flyray.bsin.domain.domain.Account;
import me.flyray.bsin.domain.domain.AccountJournal;
import me.flyray.bsin.domain.enums.AccountCategory;
import me.flyray.bsin.domain.enums.CcyType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.enums.FreezeStatus;
import me.flyray.bsin.facade.response.CommunityLedgerVO;
import me.flyray.bsin.facade.service.AccountService;
import me.flyray.bsin.facade.service.TokenParamService;
import me.flyray.bsin.infrastructure.mapper.AccountFreezeJournalMapper;
import me.flyray.bsin.infrastructure.mapper.CustomerAccountJournalMapper;
import me.flyray.bsin.infrastructure.mapper.CustomerAccountMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.biz.CustomerAccountBiz;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author bolei
 * @date 2023/6/28 16:35
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/account", timeout = 6000)
@ApiModule(value = "account")
@Service
public class AccountServiceImpl implements AccountService {

  @Autowired private CustomerAccountMapper customerAccountMapper;
  @Autowired private CustomerAccountJournalMapper customerAccountJournalMapper;
  @Autowired private CustomerAccountBiz customerAccountBiz;
  @Autowired private AccountFreezeJournalMapper accountFreezeJournalMapper;

  @DubboReference(version = "${dubbo.provider.version}")
  private TokenParamService tokenParamService;

  @ShenyuDubboClient("/openAccount")
  @ApiDoc(desc = "openAccount")
  @Override
  public Map<String, Object> openAccount(Map<String, Object> requestMap) {
    Account customerAccount =
        BsinServiceContext.getReqBodyDto(Account.class, requestMap);
    customerAccountBiz.openAccount(customerAccount);
    return RespBodyHandler.RespBodyDto();
  }

  @ShenyuDubboClient("/inAccount")
  @ApiDoc(desc = "inAccount")
  @Override
  public Map<String, Object> inAccount(Map<String, Object> requestMap)
      throws UnsupportedEncodingException {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();

    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }

    String ccy = MapUtils.getString(requestMap, "ccy");
    String amount = MapUtils.getString(requestMap, "amount");
    // TODO 建立账户体系枚举
    String category = MapUtils.getString(requestMap, "category");
    String name = MapUtils.getString(requestMap, "name");
    Integer decimals = Integer.valueOf(MapUtils.getString(requestMap, "decimals"));
    customerAccountBiz.inAccount(
        tenantId, customerNo, category, name, ccy, decimals, new BigDecimal(amount));
    return RespBodyHandler.RespBodyDto();
  }

  @ShenyuDubboClient("/outAccount")
  @ApiDoc(desc = "outAccount")
  @Override
  public Map<String, Object> outAccount(Map<String, Object> requestMap)
      throws UnsupportedEncodingException {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
      if (customerNo == null) {
        throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
      }
    }
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }

    String ccy = MapUtils.getString(requestMap, "ccy");
    String amount = MapUtils.getString(requestMap, "amount");
    String category = MapUtils.getString(requestMap, "category");
    String name = MapUtils.getString(requestMap, "name");
    Integer decimals = Integer.valueOf(MapUtils.getString(requestMap, "decimals"));
    customerAccountBiz.outAccount(
        tenantId, customerNo, category, name, ccy, decimals, new BigDecimal(amount));
    return RespBodyHandler.RespBodyDto();
  }

  @ShenyuDubboClient("/freeze")
  @ApiDoc(desc = "freeze")
  @Override
  public Map<String, Object> freeze(Map<String, Object> requestMap) {
    Account customerAccount =
        BsinServiceContext.getReqBodyDto(Account.class, requestMap);
    // 1.冻结
    int i = customerAccountMapper.freezeAmount(customerAccount);

    // 客户的账户编号
    Account customerFreezeAccount =
        customerAccountMapper.selectOne(
            new LambdaQueryWrapper<Account>()
                .eq(Account::getTenantId, customerAccount.getTenantId())
                .eq(Account::getCustomerNo, customerAccount.getCustomerNo())
                .eq(Account::getCcy, customerAccount.getCcy())
                .eq(Account::getCategory, customerAccount.getCategory()));
    if (customerFreezeAccount == null) {
      throw new BusinessException(CUSTOMER_ACCOUNT_IS_NULL);
    }
    // 2.插入冻结流水记录
    AccountFreezeJournal accountFreezeJournal = new AccountFreezeJournal();
    accountFreezeJournal.setSerialNo(BsinSnowflake.getId());
    accountFreezeJournal.setTenantId(customerAccount.getTenantId());
    accountFreezeJournal.setCustomerAccountNo(customerFreezeAccount.getSerialNo());
    accountFreezeJournal.setFreezeAmount(customerAccount.getFreezeAmount());
    accountFreezeJournal.setType((String) requestMap.get("type")); // 冻结事件类型
    accountFreezeJournal.setTypeNo((String) requestMap.get("typeNo")); // 冻结事件编号
    accountFreezeJournal.setCustomerNo(customerFreezeAccount.getCustomerNo());
    accountFreezeJournal.setStatus(FreezeStatus.FREEZE.getCode());

    accountFreezeJournalMapper.insert(accountFreezeJournal);
    return RespBodyHandler.setRespBodyDto(i);
  }

  @ShenyuDubboClient("/unfreeze")
  @ApiDoc(desc = "unfreeze")
  @Override
  public Map<String, Object> unfreeze(Map<String, Object> requestMap) {
    Account customerAccount =
        BsinServiceContext.getReqBodyDto(Account.class, requestMap);

    List<String> customerNoList = (List) requestMap.get("customerNoList");

    for (String customerNo : customerNoList) {
      // 1.解冻
      customerAccount.setCustomerNo(customerNo);
      int i = customerAccountMapper.unFreezeAmount(customerAccount);
      // 查询出冻结记录
      AccountFreezeJournal accountFreezeJournal =
          accountFreezeJournalMapper.selectOne(
              new LambdaQueryWrapper<AccountFreezeJournal>()
                  .eq(AccountFreezeJournal::getType, customerAccount.getType())
                  .eq(AccountFreezeJournal::getTypeNo, requestMap.get("typeNo"))
                  .eq(AccountFreezeJournal::getCustomerNo, customerAccount.getCustomerNo())
                  .eq(AccountFreezeJournal::getStatus, FreezeStatus.FREEZE.getCode()));
      // 修改冻结状态
      if (accountFreezeJournal != null) {
        accountFreezeJournal.setStatus(FreezeStatus.UN_FREEZE.getCode());
        //    accountFreezeJournal.setUpdateTime(LocalDateTime.now());
        accountFreezeJournalMapper.updateById(accountFreezeJournal);
      }
    }
    return RespBodyHandler.setRespBodyDto(customerNoList);
  }

  @ShenyuDubboClient("/unfreezeAndOutAccount")
  @ApiDoc(desc = "unfreezeAndOutAccount")
  @Override
  public Map<String, Object> unfreezeAndOutAccount(Map<String, Object> requestMap) {
    return null;
  }

  @ShenyuDubboClient("/transfer")
  @ApiDoc(desc = "transfer")
  @Override
  public Map<String, Object> transfer(Map<String, Object> requestMap) {
    return null;
  }

  /**
   * 查询客户的账户账户详情
   *
   * @param requestMap
   * @return
   */
  @ShenyuDubboClient("/getDetail")
  @ApiDoc(desc = "getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");

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
    // 不存在账户是否立即开户
    String openAccount = MapUtils.getString(requestMap, "openAccount");

    boolean isAutoOpenAccount = true;
    if (openAccount != null) {
      isAutoOpenAccount = Boolean.parseBoolean(openAccount);
    }

    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    Account accountDetail = null;
    Account customerAccount =
        BsinServiceContext.getReqBodyDto(Account.class, requestMap);
    if (serialNo == null) {
      LambdaQueryWrapper<Account> warapper = new LambdaQueryWrapper<>();
      warapper.eq(Account::getTenantId, tenantId);
      warapper.eq(Account::getCustomerNo, customerNo);
      warapper.eq(Account::getCategory, customerAccount.getCategory());
      warapper.eq(Account::getCcy, customerAccount.getCcy());
      accountDetail = customerAccountMapper.selectOne(warapper);
    } else {
      accountDetail = customerAccountMapper.selectById(serialNo);
    }
    // 如果账户不存在则开通账户
    if (accountDetail == null && isAutoOpenAccount) {
      customerAccount.setTenantId(tenantId);
      customerAccount.setCustomerNo(customerNo);
      customerAccountBiz.openAccount(customerAccount);
      customerAccount.setBalance(BigDecimal.ZERO);
      accountDetail = customerAccount;
    }
    if (accountDetail != null) {
      return RespBodyHandler.setRespBodyDto(accountDetail);
    } else {
      Map response = new HashMap<>();
      response.put("code", "100000");
      response.put("data", "账户不存在！！");
      return RespBodyHandler.setRespBodyDto(response);
    }
  }

  @ShenyuDubboClient("/verifyAccountBalance")
  @ApiDoc(desc = "verifyAccountBalance")
  public Map<String, Object> verifyAccountBalance(Map<String, Object> requestMap) {
    //    Map reqMap = new HashMap<>();
    //    reqMap.put("tenantId", loginUser.getTenantId());
    //    reqMap.put("customerNo", customerNo);
    //    reqMap.put("category", AccountCategory.BALANCE.getCode());
    //    reqMap.put("ccy", condition.getTypeNo());
    //    // TODO: 账户类型 0、个人账户 1、企业账户 2 租户(dao)账户
    //    reqMap.put("type", '0');
    BigDecimal amount = (BigDecimal) requestMap.get("conditionAmount");
    Map resMap = getDetail(requestMap);
    Map customerAccount = (Map) resMap.get("data");
    // TODO: validate on chain
    if (((BigDecimal) customerAccount.get("balance")).compareTo(amount) == -1) {
      throw new BusinessException(TASK_NON_CLAIM_CONDITION);
    }
    return null;
  }

  @ShenyuDubboClient("/getPageList")
  @ApiDoc(desc = "getPageList")
  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String merchantNo = loginUser.getMerchantNo();

    Account customerAccount =
        BsinServiceContext.getReqBodyDto(Account.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<Account> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<Account> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(Account::getCreateTime);
    warapper.eq(Account::getTenantId, tenantId);
    warapper.eq(
        ObjectUtil.isNotNull(customerAccount.getStatus()),
        Account::getStatus,
        customerAccount.getStatus());
    warapper.eq(
        ObjectUtil.isNotNull(customerAccount.getType()),
        Account::getType,
        customerAccount.getType());
    warapper.eq(
        ObjectUtil.isNotNull(customerAccount.getCategory()),
        Account::getCategory,
        customerAccount.getCategory());
    warapper.eq(
        ObjectUtil.isNotNull(customerAccount.getCcy()),
        Account::getCcy,
        customerAccount.getCcy());
    IPage<Account> pageList = customerAccountMapper.selectPage(page, warapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  /**
   * 查询客户的账户列表
   *
   * @param requestMap
   * @return
   */
  @ShenyuDubboClient("/getList")
  @ApiDoc(desc = "getList")
  @Override
  public Map<String, Object> getList(Map<String, Object> requestMap) {
    Account customerAccount =
        BsinServiceContext.getReqBodyDto(Account.class, requestMap);
    String tenantId = (String) requestMap.get("tenantId");
    LambdaQueryWrapper<Account> warapper = new LambdaQueryWrapper<>();
    warapper.eq(Account::getTenantId, tenantId);
    warapper.eq(Account::getCustomerNo, customerAccount.getCustomerNo());
    warapper.eq(
        ObjectUtil.isNotNull(customerAccount.getCcy()),
        Account::getCcy,
        customerAccount.getCcy());
    List<Account> accounts = customerAccountMapper.selectList(warapper);
    return RespBodyHandler.setRespBodyListDto(accounts);
  }

  @ShenyuDubboClient("/getAccountJournalPageList")
  @ApiDoc(desc = "getAccountJournalPageList")
  @Override
  public Map<String, Object> getAccountJournalPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String merchantNo = loginUser.getMerchantNo();
    Account customerAccount =
        BsinServiceContext.getReqBodyDto(Account.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<AccountJournal> page =
        new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<AccountJournal> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(AccountJournal::getCreateTime);
    warapper.eq(AccountJournal::getTenantId, tenantId);
    warapper.eq(
        StringUtils.isNotEmpty(customerAccount.getCustomerNo()),
        AccountJournal::getCustomerNo,
        customerAccount.getCustomerNo());
    warapper.eq(
        StringUtils.isNotEmpty(customerAccount.getCcy()),
        AccountJournal::getCcy,
        customerAccount.getCcy());
    IPage<AccountJournal> pageList =
        customerAccountJournalMapper.selectPage(page, warapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  @ShenyuDubboClient("/getAccountJournalDetail")
  @ApiDoc(desc = "getAccountJournalDetail")
  @Override
  public Map<String, Object> getAccountJournalDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    AccountJournal accountJournal = customerAccountJournalMapper.selectById(serialNo);
    return RespBodyHandler.setRespBodyDto(accountJournal);
  }

  @ShenyuDubboClient("/getAccountFreezeJournalPageList")
  @ApiDoc(desc = "getAccountFreezeJournalPageList")
  @Override
  public Map<String, Object> getAccountFreezeJournalPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String merchantNo = loginUser.getMerchantNo();
    AccountFreezeJournal customerAccountFreeze =
        BsinServiceContext.getReqBodyDto(AccountFreezeJournal.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<AccountFreezeJournal> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<AccountFreezeJournal> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(AccountFreezeJournal::getCreateTime);
    warapper.eq(AccountFreezeJournal::getTenantId, tenantId);
    warapper.eq(AccountFreezeJournal::getMerchantNo, merchantNo);
    warapper.eq(
        StringUtils.isNotEmpty(customerAccountFreeze.getCustomerNo()),
        AccountFreezeJournal::getCustomerNo,
        customerAccountFreeze.getCustomerNo());
    warapper.eq(
        StringUtils.isNotEmpty(customerAccountFreeze.getCustomerAccountNo()),
        AccountFreezeJournal::getCustomerAccountNo,
        customerAccountFreeze.getCustomerAccountNo());
    warapper.eq(
        StringUtils.isNotEmpty(customerAccountFreeze.getTypeNo()),
        AccountFreezeJournal::getTypeNo,
        customerAccountFreeze.getTypeNo());
    warapper.eq(
        StringUtils.isNotEmpty(customerAccountFreeze.getType()),
        AccountFreezeJournal::getType,
        customerAccountFreeze.getType());
    IPage<AccountFreezeJournal> pageList = accountFreezeJournalMapper.selectPage(page, warapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  @ShenyuDubboClient("/getAccountFreezeJournalDetail")
  @ApiDoc(desc = "getAccountFreezeJournalDetail")
  @Override
  public Map<String, Object> getAccountFreezeJournalDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    AccountFreezeJournal accountFreezeJournal = accountFreezeJournalMapper.selectById(serialNo);
    return RespBodyHandler.setRespBodyDto(accountFreezeJournal);
  }

  @ShenyuDubboClient("/getCommunityLedgerInfo")
  @ApiDoc(desc = "getCommunityLedgerInfo")
  @Override
  public Map<String, Object> getCommunityLedgerInfo(Map<String, Object> requestMap) {
    // 查询社区账本账户的余额信息
    CommunityLedgerVO communityLedgerVO = new CommunityLedgerVO();

    return RespBodyHandler.setRespBodyDto(communityLedgerVO);
  }

  /**
   * 查询客户在商户下可用的支付账户
   * 1、火钻账户(fireDiamond)
   * 2、品牌积分账户(brandsPoint)
   * @param requestMap
   * @return
   */
  @ShenyuDubboClient("/getPayAccounts")
  @ApiDoc(desc = "getPayAccounts")
  @Override
  public Map<String, Object> getPayAccounts(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String customerNo = loginUser.getCustomerNo();
    log.info("客户号: {}", customerNo);
    // 查询社区账本账户的余额信息
    Map payAccounts = new HashMap();

    // 查询商户发行的积分的币种
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    Map<String, Object> tokenReq = new HashMap();
    tokenReq.put("merchantNo", merchantNo);
    Map<String, Object> tokenParamMap = tokenParamService.getDetailByMerchantNo(tokenReq);
    Account accountDetail = null;
    if(StringUtils.isNotEmpty(tokenParamMap.get("data").toString())){
      Map tokenParam = (Map) tokenParamMap.get("data");
      String ccy = (String) tokenParam.get("symbol");
      BigDecimal anchoringValue = (BigDecimal) tokenParam.get("anchoringValue");
      // 查询用户在该币种下的余额
      LambdaQueryWrapper<Account> warapper = new LambdaQueryWrapper<>();
      warapper.eq(Account::getTenantId, tenantId);
      warapper.eq(Account::getCustomerNo, customerNo);
      warapper.eq(Account::getCategory, AccountCategory.BALANCE.getCode());
      warapper.eq(Account::getCcy, ccy);
      accountDetail = customerAccountMapper.selectOne(warapper);
      accountDetail.setAnchoringValue(anchoringValue);
    }

    LambdaQueryWrapper<Account> fdWarapper = new LambdaQueryWrapper<>();
    fdWarapper.eq(Account::getTenantId, tenantId);
    fdWarapper.eq(Account::getCustomerNo, customerNo);
    fdWarapper.eq(Account::getCategory, AccountCategory.BALANCE.getCode());
    fdWarapper.eq(Account::getCcy, CcyType.CNY.getCode());
    Account fdAccountDetail = customerAccountMapper.selectOne(fdWarapper);
    fdAccountDetail.setAnchoringValue(BigDecimal.valueOf(1));

    payAccounts.put("fireDiamond",fdAccountDetail);
    payAccounts.put("brandsPoint",accountDetail);
    // * 1、火钻账户（fireDiamond）
    //   * 2、品牌积分账户(brandsPoint)
    return RespBodyHandler.setRespBodyDto(payAccounts);
  }

}
