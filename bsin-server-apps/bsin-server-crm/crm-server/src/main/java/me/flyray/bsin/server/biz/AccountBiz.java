package me.flyray.bsin.server.biz;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import me.flyray.bsin.domain.enums.CustomerType;
import me.flyray.bsin.domain.enums.InOutAccountFlag;
import me.flyray.bsin.enums.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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
    MD5 md5 = new MD5(account.getBizRoleTypeNo().getBytes(StandardCharsets.UTF_8));
    account.setBalance(BigDecimal.ZERO);
    String balanceStr = formatBalanceForCheckCode(account.getBalance(), account.getDecimals());
    account.setCheckCode(HexUtil.encodeHexStr(md5.digest(balanceStr.getBytes(StandardCharsets.UTF_8))));
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

  public Account inAccount(String accountNo, BigDecimal amount, String remark){
    Account account = accountMapper.selectById(accountNo);
    AccountJournal accountJournal = new AccountJournal();
    MD5 md5 = new MD5(account.getBizRoleTypeNo().getBytes(StandardCharsets.UTF_8));
    // 余额校验 - 生成CheckCode进行比对
    String balancePlainString = formatBalanceForCheckCode(account.getBalance(), account.getDecimals());
    byte[] balanceBytes = balancePlainString.getBytes(StandardCharsets.UTF_8);
    String checkCode = HexUtil.encodeHexStr(md5.digest(balanceBytes));
    if (!checkCode.equals(account.getCheckCode())) {
      throw new BusinessException(ResponseCode.ACCOUNT_BALANCE_ANNORMAL);
    }
    if (account.getStatus().equals(AccountEnum.FREEZE.getCode())) {
      throw new BusinessException(ResponseCode.ACCOUNT_NOT_EXISTS);
    }
    account.setBalance(account.getBalance().add(amount));
    accountJournal.setInOutFlag(InOutAccountFlag.INT_ACCOUNT.getCode());

    String newBalanceStr = formatBalanceForCheckCode(account.getBalance(), account.getDecimals());
    byte[] newBalance = newBalanceStr.getBytes(StandardCharsets.UTF_8);
    String newCheckCode = HexUtil.encodeHexStr(md5.digest(newBalance));
    account.setCheckCode(newCheckCode);

    accountJournal.setRemark(remark);
    accountJournal.setOrderType(TransactionType.RECHARGE.getCode());
    accountJournal.setSerialNo(BsinSnowflake.getId());
    accountJournal.setAccountNo(account.getSerialNo());
    accountJournal.setAccountType(account.getType());
    accountJournal.setBizRoleTypeNo(account.getBizRoleTypeNo());
    accountJournal.setAmount(amount);
  //        accountJournal.setOrderNo(orderNo);
    accountJournal.setCcy(account.getCcy());
    accountJournal.setTenantId(account.getTenantId());
    customerAccountJournalMapper.insert(accountJournal);
    accountMapper.updateById(account);
    return account;
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
      String remark){
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
      String remark){
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
    MD5 md5 = null;
    AccountJournal accountJournal = new AccountJournal();
    if (amount.compareTo(BigDecimal.ZERO) <= 0 ) {
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
      if (InOutAccountFlag.INT_ACCOUNT.getCode().equals(journalDirection)) {
        accountJournal.setInOutFlag(1);
      } else {
        // 如果是出账，则账户不存在
        throw new BusinessException(ResponseCode.ACCOUNT_NOT_EXISTS);
      }
      account.setBalance(amount);
      md5 = new MD5(account.getBizRoleTypeNo().getBytes(StandardCharsets.UTF_8));
      
      // 使用统一的格式：根据decimals统一格式化余额
      String balancePlainString = formatBalanceForCheckCode(account.getBalance(), decimals);
      byte[] balanceBytes = balancePlainString.getBytes(StandardCharsets.UTF_8);
      
      String checkCode = HexUtil.encodeHexStr(md5.digest(balanceBytes));
      account.setCheckCode(checkCode);
      account.setStatus(AccountEnum.NORMAL.getCode());
      accountMapper.insert(account);
   } else {
      md5 = new MD5(account.getBizRoleTypeNo().getBytes(StandardCharsets.UTF_8));
      
      // 使用统一的格式：根据账户的decimals统一格式化余额进行校验
      String balancePlainString = formatBalanceForCheckCode(account.getBalance(), account.getDecimals());
      byte[] balanceBytes = balancePlainString.getBytes(StandardCharsets.UTF_8);
      String checkCode = HexUtil.encodeHexStr(md5.digest(balanceBytes));
      if (!checkCode.equals(account.getCheckCode())) {
          throw new BusinessException(ResponseCode.ACCOUNT_BALANCE_ANNORMAL);
      }
      if (account.getStatus().equals(AccountEnum.FREEZE.getCode())) {
          throw new BusinessException(ResponseCode.ACCOUNT_NOT_EXISTS);
      }

      // 更新余额
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

      // 使用更新后的余额生成新的校验码
      String newBalanceStr = formatBalanceForCheckCode(account.getBalance(), account.getDecimals());
      byte[] newBalance = newBalanceStr.getBytes(StandardCharsets.UTF_8);
      String newCheckCode = HexUtil.encodeHexStr(md5.digest(newBalance));
      account.setCheckCode(newCheckCode);
      accountMapper.updateById(account);
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
    accountJournal.setCcy(account.getCcy());
    accountJournal.setTenantId(account.getTenantId());
    customerAccountJournalMapper.insert(accountJournal);
    return account;
  }

  public Account getAccountDetail(String bizRoleTypeNo, String ccy, String category) {
    LambdaQueryWrapper<Account> warapper = new LambdaQueryWrapper<>();
    warapper.eq(Account::getBizRoleTypeNo, bizRoleTypeNo);
    warapper.eq(Account::getCcy, ccy);
    warapper.eq(Account::getCategory, category);
    Account customerAccount = accountMapper.selectOne(warapper);
    return customerAccount;
  }

  /**
   * 统一格式化余额用于CheckCode生成，确保格式一致性
   */
  private String formatBalanceForCheckCode(BigDecimal balance, Integer decimals) {
    if (balance == null) {
      return "0.00";
    }
    
    if (decimals == null || decimals == 2) {
      // 默认使用2位小数格式
      DecimalFormat decimalFormat = new DecimalFormat("#.00");
      return decimalFormat.format(balance);
    } else {
      // 根据指定的精度格式化
      StringBuilder pattern = new StringBuilder("#.");
      for (int i = 0; i < decimals; i++) {
        pattern.append("0");
      }
      DecimalFormat decimalFormat = new DecimalFormat(pattern.toString());
      return decimalFormat.format(balance);
    }
  }

  public static void main(String[] args) {
    MD5 md5 = new MD5("1977924180729008128".getBytes());
    String checkCode = HexUtil.encodeHexStr(md5.digest("215.97"));
    System.out.println(checkCode);
  }

}
