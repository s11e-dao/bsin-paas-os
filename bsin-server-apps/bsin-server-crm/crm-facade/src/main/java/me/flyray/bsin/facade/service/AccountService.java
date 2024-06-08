package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.Account;
import me.flyray.bsin.domain.entity.AccountFreezeJournal;
import me.flyray.bsin.domain.entity.AccountJournal;
import me.flyray.bsin.facade.response.CommunityLedgerVO;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/27 19:45
 * @desc 客户账户
 */
public interface AccountService {

  /** 开账户 */
  public void openAccount(Map<String, Object> requestMap);

  /** 入账 */
  public void inAccount(Map<String, Object> requestMap)throws UnsupportedEncodingException;

  /** 出账 */
  public void outAccount(Map<String, Object> requestMap) throws UnsupportedEncodingException;

  /** 冻结金额 */
  public AccountFreezeJournal freeze(Map<String, Object> requestMap);

  /** 解冻金额 */
  public void unfreeze(Map<String, Object> requestMap);

  /** 解冻并出账 */
  public Map<String, Object> unfreezeAndOutAccount(Map<String, Object> requestMap);

  /** 转账 */
  public Map<String, Object> transfer(Map<String, Object> requestMap);

  /** 查询账户详细 */
  public Account getDetail(Map<String, Object> requestMap);


  /** 账户资产验证 */
  public Map<String, Object> verifyAccountBalance(Map<String, Object> requestMap);

  /** 分页查询 */
  public IPage<Account> getPageList(Map<String, Object> requestMap);

  /** 查询账户 1、品牌账户 2、品牌社区账户 3、品牌商户账户 4、客户账户 */
  public List<?> getList(Map<String, Object> requestMap);

  /** 分页查询账户流水 */
  public IPage<AccountJournal> getAccountJournalPageList(Map<String, Object> requestMap);

  public AccountJournal getAccountJournalDetail(Map<String, Object> requestMap);


  /** 分页查询账户冻结流水 */
  public IPage<?> getAccountFreezeJournalPageList(Map<String, Object> requestMap);

  public AccountFreezeJournal getAccountFreezeJournalDetail(Map<String, Object> requestMap);

  /**
   * 查询社区账本
   * 1、社区总收入账户
   * 2、社区已支出账户
   * 3、社区待支出账户
   * @param requestMap
   * @return
   */
  public CommunityLedgerVO getCommunityLedgerInfo(Map<String, Object> requestMap);

  /**
   * 根据商户号查询客户支付账户
   * 1、火钻账户（fireDiamond）
   * 2、品牌积分账户(brandsPoint)
   * @return
   */
  public Map<String, Object> getPayAccounts(Map<String, Object> requestMap);

}
