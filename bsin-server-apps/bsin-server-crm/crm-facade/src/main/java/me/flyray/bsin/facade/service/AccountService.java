package me.flyray.bsin.facade.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/27 19:45
 * @desc 客户账户
 */
public interface AccountService {

  /** 开账户 */
  public Map<String, Object> openAccount(Map<String, Object> requestMap);

  /** 入账 */
  public Map<String, Object> inAccount(Map<String, Object> requestMap)throws UnsupportedEncodingException;

  /** 出账 */
  public Map<String, Object> outAccount(Map<String, Object> requestMap) throws UnsupportedEncodingException;

  /** 冻结金额 */
  public Map<String, Object> freeze(Map<String, Object> requestMap);

  /** 解冻金额 */
  public Map<String, Object> unfreeze(Map<String, Object> requestMap);

  /** 解冻并出账 */
  public Map<String, Object> unfreezeAndOutAccount(Map<String, Object> requestMap);

  /** 转账 */
  public Map<String, Object> transfer(Map<String, Object> requestMap);

  /** 查询账户详细 */
  public Map<String, Object> getDetail(Map<String, Object> requestMap);


  /** 账户资产验证 */
  public Map<String, Object> verifyAccountBalance(Map<String, Object> requestMap);

  /** 分页查询 */
  public Map<String, Object> getPageList(Map<String, Object> requestMap);

  /** 查询账户 1、品牌账户 2、品牌社区账户 3、品牌商户账户 4、客户账户 */
  public Map<String, Object> getList(Map<String, Object> requestMap);

  /** 分页查询账户流水 */
  public Map<String, Object> getAccountJournalPageList(Map<String, Object> requestMap);

  public Map<String, Object> getAccountJournalDetail(Map<String, Object> requestMap);


  /** 分页查询账户冻结流水 */
  public Map<String, Object> getAccountFreezeJournalPageList(Map<String, Object> requestMap);

  public Map<String, Object> getAccountFreezeJournalDetail(Map<String, Object> requestMap);

  /**
   * 查询社区账本
   * 1、社区总收入账户
   * 2、社区已支出账户
   * 3、社区待支出账户
   * @param requestMap
   * @return
   */
  public Map<String, Object> getCommunityLedgerInfo(Map<String, Object> requestMap);

  /**
   * 根据商户号查询客户支付账户
   * 1、火钻账户（fireDiamond）
   * 2、品牌积分账户(brandsPoint)
   * @return
   */
  public Map<String, Object> getPayAccounts(Map<String, Object> requestMap);

}
