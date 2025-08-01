package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Account;
import me.flyray.bsin.domain.entity.AccountFreezeJournal;
import me.flyray.bsin.domain.entity.AccountJournal;
import me.flyray.bsin.domain.enums.AccountCategory;
import me.flyray.bsin.domain.enums.CcyType;
import me.flyray.bsin.dubbo.invoke.BsinServiceInvoke;
import me.flyray.bsin.enums.TransactionType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.enums.FreezeStatus;
import me.flyray.bsin.facade.response.CommunityLedgerVO;
import me.flyray.bsin.facade.service.AccountService;
import me.flyray.bsin.infrastructure.mapper.AccountFreezeJournalMapper;
import me.flyray.bsin.infrastructure.mapper.AccountJournalMapper;
import me.flyray.bsin.infrastructure.mapper.AccountMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.server.biz.AccountBiz;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.CUSTOMER_ACCOUNT_IS_NULL;
import static me.flyray.bsin.constants.ResponseCode.TASK_NON_CLAIM_CONDITION;

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

  @Autowired private AccountMapper accountMapper;
  @Autowired private AccountJournalMapper accountJournalMapper;
  @Autowired private AccountBiz accountBiz;
  @Autowired private AccountFreezeJournalMapper accountFreezeJournalMapper;

  @Autowired
  private BsinServiceInvoke bsinServiceInvoke;

  @ShenyuDubboClient("/openAccount")
  @ApiDoc(desc = "openAccount")
  @Override
  public void openAccount(Map<String, Object> requestMap) {
    Account account =
        BsinServiceContext.getReqBodyDto(Account.class, requestMap);
    accountBiz.openAccount(account);
  }

  /**
   * 根据账号ID入账或是根据四要数入账（业务角色类型编号，业务角色类型，币种，会计分类）
   * @param requestMap
   * @throws UnsupportedEncodingException
   */
  @ShenyuDubboClient("/inAccount")
  @ApiDoc(desc = "inAccount")
  @Override
  public void inAccount(Map<String, Object> requestMap)
      throws UnsupportedEncodingException {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String bizRoleType = MapUtils.getString(requestMap, "bizRoleType");
    String bizRoleTypeNo = MapUtils.getString(requestMap, "bizRoleTypeNo");
    if (bizRoleTypeNo == null) {
      bizRoleTypeNo = loginUser.getBizRoleTypeNo();
      if(bizRoleTypeNo == null){
        throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
      }
    }
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }

    String ccy = MapUtils.getString(requestMap, "ccy");
    String amount = MapUtils.getString(requestMap, "amount");
    // TODO 建立账户体系枚举
    String category = MapUtils.getString(requestMap, "category");
    if(AccountCategory.getInstanceById(category) == null){
      throw new BusinessException(ResponseCode.ACCOUNT_CATEGORY_NOT_EXISTS);
    }

    String name = MapUtils.getString(requestMap, "name");
    Integer decimals = Integer.valueOf(MapUtils.getString(requestMap, "decimals"));
    String orderNo = MapUtils.getString(requestMap, "orderNo");
    String transactionType = MapUtils.getString(requestMap, "transactionType");
    // 判断交易类型是否存在
    if(TransactionType.getInstanceById(transactionType) == null){
      throw new BusinessException("9999", "交易类型不存在");
    }
    String remark = MapUtils.getString(requestMap, "remark");
    accountBiz.inAccount(
        tenantId, bizRoleType, bizRoleTypeNo, category, name, ccy, orderNo, transactionType, decimals, new BigDecimal(amount), remark);

  }

  @ShenyuDubboClient("/outAccount")
  @ApiDoc(desc = "outAccount")
  @Override
  public void outAccount(Map<String, Object> requestMap)
      throws UnsupportedEncodingException {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String bizRoleType = MapUtils.getString(requestMap, "bizRoleType");
    String bizRoleTypeNo = MapUtils.getString(requestMap, "bizRoleTypeNo");
    if (bizRoleTypeNo == null) {
      throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
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
    String orderNo = MapUtils.getString(requestMap, "orderNo");
    String transactionType = MapUtils.getString(requestMap, "transactionType");
    // 判断交易类型是否存在
    if(TransactionType.getInstanceById(transactionType) == null){
      throw new BusinessException("9999", "交易类型不存在");
    }
    String remark = MapUtils.getString(requestMap, "remark");
    accountBiz.outAccount(
        tenantId, bizRoleType, bizRoleTypeNo, category, name, ccy, orderNo, transactionType, decimals, new BigDecimal(amount), remark);
  }

  @ShenyuDubboClient("/freeze")
  @ApiDoc(desc = "freeze")
  @Override
  public AccountFreezeJournal freeze(Map<String, Object> requestMap) {
    Account customerAccount =
        BsinServiceContext.getReqBodyDto(Account.class, requestMap);
    // 1.冻结
    int i = accountMapper.freezeAmount(customerAccount);

    // 客户的账户编号
    Account freezeAccount =
            accountMapper.selectOne(
            new LambdaQueryWrapper<Account>()
                .eq(Account::getTenantId, customerAccount.getTenantId())
                .eq(Account::getBizRoleTypeNo, customerAccount.getBizRoleTypeNo())
                .eq(Account::getCcy, customerAccount.getCcy())
                .eq(Account::getCategory, customerAccount.getCategory()));
    if (freezeAccount == null) {
      throw new BusinessException(CUSTOMER_ACCOUNT_IS_NULL);
    }
    // 2.插入冻结流水记录
    AccountFreezeJournal accountFreezeJournal = new AccountFreezeJournal();
    accountFreezeJournal.setSerialNo(BsinSnowflake.getId());
    accountFreezeJournal.setTenantId(customerAccount.getTenantId());
    accountFreezeJournal.setAccountNo(freezeAccount.getSerialNo());
    accountFreezeJournal.setFreezeAmount(customerAccount.getFreezeAmount());
    accountFreezeJournal.setType((String) requestMap.get("type")); // 冻结事件类型
    accountFreezeJournal.setTypeNo((String) requestMap.get("typeNo")); // 冻结事件编号
    accountFreezeJournal.setBizRoleTypeNo(freezeAccount.getBizRoleTypeNo());
    accountFreezeJournal.setStatus(FreezeStatus.FREEZE.getCode());

    accountFreezeJournalMapper.insert(accountFreezeJournal);
    return accountFreezeJournal;
  }

  @ShenyuDubboClient("/unfreeze")
  @ApiDoc(desc = "unfreeze")
  @Override
  public void unfreeze(Map<String, Object> requestMap) {
    Account customerAccount =
        BsinServiceContext.getReqBodyDto(Account.class, requestMap);

    List<String> customerNoList = (List) requestMap.get("customerNoList");

    for (String customerNo : customerNoList) {
      // 1.解冻
      customerAccount.setBizRoleTypeNo(customerNo);
      int i = accountMapper.unFreezeAmount(customerAccount);
      // 查询出冻结记录
      AccountFreezeJournal accountFreezeJournal =
          accountFreezeJournalMapper.selectOne(
              new LambdaQueryWrapper<AccountFreezeJournal>()
                  .eq(AccountFreezeJournal::getType, customerAccount.getType())
                  .eq(AccountFreezeJournal::getTypeNo, requestMap.get("typeNo"))
                  .eq(AccountFreezeJournal::getBizRoleTypeNo, customerAccount.getBizRoleTypeNo())
                  .eq(AccountFreezeJournal::getStatus, FreezeStatus.FREEZE.getCode()));
      // 修改冻结状态
      if (accountFreezeJournal != null) {
        accountFreezeJournal.setStatus(FreezeStatus.UN_FREEZE.getCode());
        //    accountFreezeJournal.setUpdateTime(LocalDateTime.now());
        accountFreezeJournalMapper.updateById(accountFreezeJournal);
      }
    }
  }

  @ShenyuDubboClient("/unfreezeAndOutAccount")
  @ApiDoc(desc = "unfreezeAndOutAccount")
  @Override
  public Map<String, Object> unfreezeAndOutAccount(Map<String, Object> requestMap) {
    return null;
  }

  @ShenyuDubboClient("/recharge")
  @ApiDoc(desc = "recharge")
  @Override
  public Map<String, Object> recharge(Map<String, Object> requestMap) {
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
  public Account getDetail(Map<String, Object> requestMap) {
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

    String bizRoleType = MapUtils.getString(requestMap, "bizRoleType");
    String bizRoleTypeNo = "";
    if(BizRoleType.CUSTOMER.getCode().equals(bizRoleType)){
      bizRoleTypeNo = customerNo;
    } else if (BizRoleType.MERCHANT.getCode().equals(bizRoleType)) {
      bizRoleTypeNo = merchantNo;
    } else if (BizRoleType.TENANT.getCode().equals(bizRoleType)) {
      bizRoleTypeNo = tenantId;
    }

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
    if (serialNo != null) {
      accountDetail = accountMapper.selectById(serialNo);
      if(accountDetail == null){
        throw new BusinessException(ResponseCode.CUSTOMER_ACCOUNT_IS_NULL);
      }
    } else {
      LambdaQueryWrapper<Account> warapper = new LambdaQueryWrapper<>();
      warapper.eq(Account::getTenantId, tenantId);
      warapper.eq(Account::getBizRoleTypeNo, bizRoleTypeNo);
      warapper.eq(Account::getCategory, customerAccount.getCategory());
      warapper.eq(Account::getCcy, customerAccount.getCcy());
      accountDetail = accountMapper.selectOne(warapper);
      // 如果账户不存在则开通账户
      if (accountDetail == null && isAutoOpenAccount) {
        customerAccount.setTenantId(tenantId);
        customerAccount.setBizRoleTypeNo(customerNo);
        accountBiz.openAccount(customerAccount);
        customerAccount.setBalance(BigDecimal.ZERO);
        accountDetail = customerAccount;
      }
    }
    return accountDetail;
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
    Account account = getDetail(requestMap);
    // TODO: validate on chain
    if (((BigDecimal) account.getBalance()).compareTo(amount) == -1) {
      throw new BusinessException(TASK_NON_CLAIM_CONDITION);
    }
    return null;
  }

  @ShenyuDubboClient("/getPageList")
  @ApiDoc(desc = "getPageList")
  @Override
  public IPage<Account> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String merchantNo = loginUser.getMerchantNo();

    Account customerAccount =
        BsinServiceContext.getReqBodyDto(Account.class, requestMap);
    Object paginationObj =  requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj,pagination);
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
    IPage<Account> pageList = accountMapper.selectPage(page, warapper);
    return pageList;
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
  public List<?> getList(Map<String, Object> requestMap) {
    Account customerAccount =
        BsinServiceContext.getReqBodyDto(Account.class, requestMap);
    String tenantId = (String) requestMap.get("tenantId");
    LambdaQueryWrapper<Account> warapper = new LambdaQueryWrapper<>();
    warapper.eq(Account::getTenantId, tenantId);
    warapper.eq(Account::getBizRoleTypeNo, customerAccount.getBizRoleTypeNo());
    warapper.eq(
        ObjectUtil.isNotNull(customerAccount.getCcy()),
        Account::getCcy,
        customerAccount.getCcy());
    List<Account> accounts = accountMapper.selectList(warapper);
    return accounts;
  }

  @ShenyuDubboClient("/getAccountJournalPageList")
  @ApiDoc(desc = "getAccountJournalPageList")
  @Override
  public IPage<AccountJournal> getAccountJournalPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String merchantNo = loginUser.getMerchantNo();
    AccountJournal accountJournal = BsinServiceContext.getReqBodyDto(AccountJournal.class, requestMap);
    Object paginationObj =  requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj, pagination);
    String bizRoleTypeNo = "";
    if(BizRoleType.MERCHANT.getCode().equals(LoginInfoContextHelper.getBizRoleType())){
      bizRoleTypeNo = merchantNo;
    }
    Page<AccountJournal> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<AccountJournal> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(AccountJournal::getCreateTime);
    warapper.eq(AccountJournal::getTenantId, tenantId);
    warapper.eq(StringUtils.isNotEmpty(bizRoleTypeNo),
        AccountJournal::getBizRoleTypeNo,
            accountJournal.getBizRoleTypeNo());
    warapper.eq(StringUtils.isNotEmpty(accountJournal.getCcy()),
        AccountJournal::getCcy,
            accountJournal.getCcy());
    warapper.eq(StringUtils.isNotEmpty(accountJournal.getOrderType()),
            AccountJournal::getOrderType,
            accountJournal.getOrderType());
    IPage<AccountJournal> pageList = accountJournalMapper.selectPage(page, warapper);
    return pageList;
  }

  @ShenyuDubboClient("/getAccountJournalDetail")
  @ApiDoc(desc = "getAccountJournalDetail")
  @Override
  public AccountJournal getAccountJournalDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    AccountJournal accountJournal = accountJournalMapper.selectById(serialNo);
    return accountJournal;
  }

  @ShenyuDubboClient("/getAccountFreezeJournalPageList")
  @ApiDoc(desc = "getAccountFreezeJournalPageList")
  @Override
  public IPage<?> getAccountFreezeJournalPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String merchantNo = loginUser.getMerchantNo();
    AccountFreezeJournal accountFreeze =
        BsinServiceContext.getReqBodyDto(AccountFreezeJournal.class, requestMap);
    Object paginationObj =  requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj,pagination);
    Page<AccountFreezeJournal> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<AccountFreezeJournal> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(AccountFreezeJournal::getCreateTime);
    warapper.eq(AccountFreezeJournal::getTenantId, tenantId);
    warapper.eq(AccountFreezeJournal::getMerchantNo, merchantNo);
    warapper.eq(
        StringUtils.isNotEmpty(accountFreeze.getBizRoleTypeNo()),
        AccountFreezeJournal::getBizRoleTypeNo,
            accountFreeze.getBizRoleTypeNo());
    warapper.eq(
        StringUtils.isNotEmpty(accountFreeze.getBizRoleTypeNo()),
        AccountFreezeJournal::getBizRoleTypeNo,
            accountFreeze.getBizRoleTypeNo());
    warapper.eq(
        StringUtils.isNotEmpty(accountFreeze.getTypeNo()),
        AccountFreezeJournal::getTypeNo,
            accountFreeze.getTypeNo());
    warapper.eq(
        StringUtils.isNotEmpty(accountFreeze.getType()),
        AccountFreezeJournal::getType,
            accountFreeze.getType());
    IPage<AccountFreezeJournal> pageList = accountFreezeJournalMapper.selectPage(page, warapper);
    return pageList;
  }

  @ShenyuDubboClient("/getAccountFreezeJournalDetail")
  @ApiDoc(desc = "getAccountFreezeJournalDetail")
  @Override
  public AccountFreezeJournal getAccountFreezeJournalDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    AccountFreezeJournal accountFreezeJournal = accountFreezeJournalMapper.selectById(serialNo);
    return accountFreezeJournal;
  }

  /**
   * 查询每个租户的账本数据
   * 1、总积分
   * 2、已分配积分
   * 3、待分配积分
   * 4、领卡数据： 发行卡数量、领卡数量
   * @param requestMap
   * @return
   */
  @ShenyuDubboClient("/getCommunityLedgerInfo")
  @ApiDoc(desc = "getCommunityLedgerInfo")
  @Override
  public CommunityLedgerVO getCommunityLedgerInfo(Map<String, Object> requestMap) {
    // 查询社区账本账户的余额信息
    CommunityLedgerVO communityLedgerVO = new CommunityLedgerVO();

    return communityLedgerVO;
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

    // 泛化调用解耦
    Object object = bsinServiceInvoke.genericInvoke("TokenParamService", "getDetailByMerchantNo", "dev", requestMap);
    Map<String, Object> tokenParamMap = BeanUtil.beanToMap(object);

    Account accountDetail = null;
    if(tokenParamMap != null){
      String ccy = (String) tokenParamMap.get("symbol");
      BigDecimal anchoringValue = new BigDecimal(String.valueOf(tokenParamMap.get("anchoringValue")));
      // 查询用户在该币种下的余额
      LambdaQueryWrapper<Account> warapper = new LambdaQueryWrapper<>();
      warapper.eq(Account::getTenantId, tenantId);
      warapper.eq(Account::getBizRoleTypeNo, customerNo);
      warapper.eq(Account::getCategory, AccountCategory.BALANCE.getCode());
      warapper.eq(Account::getCcy, ccy);
      accountDetail = accountMapper.selectOne(warapper);
      accountDetail.setAnchoringValue(anchoringValue);
    }

    LambdaQueryWrapper<Account> fdWarapper = new LambdaQueryWrapper<>();
    fdWarapper.eq(Account::getTenantId, tenantId);
    fdWarapper.eq(Account::getBizRoleTypeNo, customerNo);
    fdWarapper.eq(Account::getCategory, AccountCategory.BALANCE.getCode());
    fdWarapper.eq(Account::getCcy, CcyType.CNY.getCode());
    Account fdAccountDetail = accountMapper.selectOne(fdWarapper);
    fdAccountDetail.setAnchoringValue(BigDecimal.valueOf(1));

    payAccounts.put("fireDiamond",fdAccountDetail);
    payAccounts.put("brandsPoint",accountDetail);
    // * 1、火钻账户（fireDiamond）
    //   * 2、品牌积分账户(brandsPoint)
    return payAccounts;
  }

  /**
   * @see AccountCategory
   * 根据币种 按账户分类查询账户
   * @param requestMap
   * @return
   */
  @ShenyuDubboClient("/getCategoryAccounts")
  @ApiDoc(desc = "getCategoryAccounts")
  @Override
  public Map<String, Object> getCategoryAccounts(Map<String, Object> requestMap) {
    String ccy = MapUtils.getString(requestMap, "ccy");
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    // 遍历AccountCategory枚举查询对应账户
    Map<String, Object> accountList = new HashMap<>();
    for (AccountCategory category : AccountCategory.values()) {
      System.out.println("Code: " + category.getCode() + ", Description: " + category.getDesc());
      LambdaQueryWrapper<Account> warapper = new LambdaQueryWrapper<>();
      warapper.eq(Account::getTenantId, loginUser.getTenantId());
      warapper.eq(Account::getBizRoleTypeNo, loginUser.getBizRoleTypeNo());
      warapper.eq(Account::getCategory, category.getCode());
      warapper.eq(Account::getCcy, ccy);
      Account account = accountMapper.selectOne(warapper);
      accountList.put(category.getCode(), account);
    }
    return accountList;
  }

}
