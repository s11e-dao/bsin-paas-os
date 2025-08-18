package me.flyray.bsin.server.biz;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import me.flyray.bsin.domain.enums.CustomerType;
import me.flyray.bsin.domain.enums.InOutAccountFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import cn.hutool.crypto.digest.MD5;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.domain.entity.Account;
import me.flyray.bsin.domain.entity.AccountJournal;
import me.flyray.bsin.domain.enums.AccountEnum;
import me.flyray.bsin.infrastructure.mapper.AccountJournalMapper;
import me.flyray.bsin.infrastructure.mapper.AccountMapper;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author bolei
 * @date 2023/7/7 14:32
 * @desc
 */
@Component
public class AccountBiz {

  @Autowired private AccountMapper accountMapper;
  @Autowired private AccountJournalMapper customerAccountJournalMapper;

  public Account openAccount(Account account) {
    accountMapper.insert(account);
    return account;
  }

  /**
   * 内部转账方法
   */
  public Account innerTransfer(
          String tenantId,
          String fromBizRoleType,
          String fromBizRoleTypeNo,
          String toBizRoleType,
          String toBizRoleTypeNo,
          String fromAccountCategory,
          String toAccountCategory,
          String ccy,
          String orderNo,
          String transactionType,
          Integer decimals,
          BigDecimal amount,
          String remark)
          throws UnsupportedEncodingException {

    return null;
  }

  public Account inAccount(
      String tenantId,
      String bizRoleType,
      String bizRoleTypeNo,
      String accountCategory,
      String accountName,
      String ccy,
      String orderNo,
      String transactionType,
      Integer decimals,
      BigDecimal amount,
      String remark)
      throws UnsupportedEncodingException {
    return handleAccount(
        tenantId,
        bizRoleType,
        bizRoleTypeNo,
        accountCategory,
        accountName,
        ccy,
        orderNo,
        transactionType,
        decimals,
        amount,
        InOutAccountFlag.INT_ACCOUNT.getCode(),
        remark);
  }

  public Account outAccount(
      String tenantId,
      String bizRoleType,
      String bizRoleTypeNo,
      String accountCategory,
      String accountName,
      String ccy,
      String orderNo,
      String transactionType,
      Integer decimals,
      BigDecimal amount,
      String remark)
      throws UnsupportedEncodingException {
    return handleAccount(
        tenantId,
        bizRoleType,
        bizRoleTypeNo,
        accountCategory,
        accountName,
        ccy,
        orderNo,
        transactionType,
        decimals,
        amount,
        InOutAccountFlag.OUT_ACCOUNT.getCode(),
        remark);
  }

  private Account handleAccount(
      String tenantId,
      String bizRoleType,
      String bizRoleTypeNo,
      String category,
      String accountName,
      String ccy,
      String orderNo,
      String transactionType,
      Integer decimals,
      BigDecimal amount,
      Integer journalDirection,
      String remark) {
    LambdaQueryWrapper<Account> warapper = new LambdaQueryWrapper<>();
    warapper.eq(Account::getTenantId, tenantId);
    warapper.eq(Account::getBizRoleTypeNo, bizRoleTypeNo);
    warapper.eq(Account::getCcy, ccy);
    warapper.eq(ObjectUtil.isNotNull(category), Account::getCategory, category);
    Account account = accountMapper.selectOne(warapper);
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    MD5 md5 = null;
    AccountJournal accountJournal = new AccountJournal();
    if (amount.compareTo(BigDecimal.ZERO) < 1) {
      throw new BusinessException(ResponseCode.AMOUNT_MUST_GREATER_THAN_ZERO);
    }
    if (account == null) {
      account = new Account();
      account.setBizRoleType(bizRoleType);
      account.setBizRoleTypeNo(bizRoleTypeNo);
      account.setTenantId(tenantId);
      account.setCcy(ccy);
      account.setType(CustomerType.PERSONAL.getCode());
      account.setName(accountName);
      account.setCategory(category);
      account.setDecimals(decimals);
      String amountStr = decimalFormat.format(amount);
      if (InOutAccountFlag.INT_ACCOUNT.getCode().equals(journalDirection)) {
        account.setBalance(amount);
        accountJournal.setInOutFlag(1);
      } else {
        // 如果是出账，则账户不存在
        throw new BusinessException(ResponseCode.ACCOUNT_NOT_EXISTS);
      }
      account.setBalance(amount);
      md5 = new MD5(account.getBizRoleTypeNo().getBytes());
      String checkCode = HexUtil.encodeHexStr(md5.digest(amountStr));
      System.out.println("1.Check Code: \n\n\n\n" + checkCode);
      account.setCheckCode(checkCode);
      account.setStatus(AccountEnum.NORMAL.getCode());
      accountMapper.insert(account);
    } else {
      md5 = new MD5(account.getBizRoleTypeNo().getBytes());
      // 余额校验
      System.out.println("账户余额: \n\n\n\n" + account.getBalance().toString());
      String checkCode = HexUtil.encodeHexStr(md5.digest(account.getBalance().toString()));
      System.out.println("2.Check Code: \n\n\n\n" + checkCode);
      System.out.println("3.getCheckCode: \n\n\n\n" + account.getCheckCode());
      if (!checkCode.equals(account.getCheckCode())) {
        throw new BusinessException(ResponseCode.ACCOUNT_BALANCE_ANNORMAL);
      }
      if (account.getStatus().equals(AccountEnum.FREEZE.getCode())) {
        throw new BusinessException(ResponseCode.ACCOUNT_NOT_EXISTS);
      }
      if (InOutAccountFlag.INT_ACCOUNT.getCode().equals(journalDirection)) {
        account.setBalance(account.getBalance().add(amount));
        accountJournal.setInOutFlag(1);
      } else {
        // 出账的时候账户余额判断
        if (account.getBalance().compareTo(amount) < 0) {
          throw new BusinessException(ResponseCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountJournal.setInOutFlag(0);
      }
      String newBalance = account.getBalance().toString();
      System.out.println("新的账户余额: \n\n\n\n" + newBalance);
      String newCheckCode = HexUtil.encodeHexStr(md5.digest(newBalance));
      System.out.println("4.newBalance getCheckCode: \n\n\n\n" + newCheckCode);
      account.setCheckCode(newCheckCode);
    }
    accountJournal.setRemark(remark);
    accountJournal.setOrderType(transactionType);
    accountJournal.setOrderNo(orderNo);
    accountJournal.setSerialNo(BsinSnowflake.getId());
    accountJournal.setAccountNo(account.getSerialNo());
    accountJournal.setAccountType(account.getType());
    accountJournal.setBizRoleTypeNo(account.getBizRoleTypeNo());
    accountJournal.setAmount(amount);
    //        accountJournal.setOrderNo(orderNo);
    accountJournal.setInOutFlag(journalDirection);
    accountJournal.setCcy(account.getCcy());
    accountJournal.setTenantId(account.getTenantId());
    customerAccountJournalMapper.insert(accountJournal);
    accountMapper.updateById(account);

    return account;
  }

  public Account getAccountDetail(
      String merchantNo, String customerNo, String ccy, String category) {
    LambdaQueryWrapper<Account> warapper = new LambdaQueryWrapper<>();
    warapper.eq(Account::getBizRoleTypeNo, customerNo);
    warapper.eq(Account::getCcy, ccy);
    warapper.eq(Account::getCategory, category);
    Account customerAccount = accountMapper.selectOne(warapper);
    return customerAccount;
  }

  public static void main(String[] args) {
    MD5 md5 = new MD5("1738934400126685184".getBytes());
    String checkCode = HexUtil.encodeHexStr(md5.digest("11166.00"));
    System.out.println(checkCode);
  }

}
