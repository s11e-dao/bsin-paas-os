package me.flyray.bsin.server.biz;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

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
import me.flyray.bsin.domain.enums.AccountJournalEnum;
import me.flyray.bsin.infrastructure.mapper.CustomerAccountJournalMapper;
import me.flyray.bsin.infrastructure.mapper.CustomerAccountMapper;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author bolei
 * @date 2023/7/7 14:32
 * @desc
 */
@Component
public class CustomerAccountBiz {

  @Autowired private CustomerAccountMapper customerAccountMapper;
  @Autowired private CustomerAccountJournalMapper customerAccountJournalMapper;

  public Account openAccount(Account customerAccount) {
    customerAccountMapper.insert(customerAccount);
    return customerAccount;
  }

  public Account inAccount(
      String tenantId,
      String customerNo,
      String accountCategory,
      String accountName,
      String ccy,
      Integer decimals,
      BigDecimal amount)
      throws UnsupportedEncodingException {
    return handleAccount(
        tenantId,
        customerNo,
        accountCategory,
        accountName,
        ccy,
        decimals,
        amount,
        AccountJournalEnum.INT_ACCOUNT.getCode());
  }

  public Account outAccount(
      String tenantId,
      String customerNo,
      String accountCategory,
      String accountName,
      String ccy,
      Integer decimals,
      BigDecimal amount)
      throws UnsupportedEncodingException {
    return handleAccount(
        tenantId,
        customerNo,
        accountCategory,
        accountName,
        ccy,
        decimals,
        amount,
        AccountJournalEnum.OUT_ACCOUNT.getCode());
  }

  private Account handleAccount(
      String tenantId,
      String customerNo,
      String category,
      String accountName,
      String ccy,
      Integer decimals,
      BigDecimal amount,
      Integer journalDirection) {
    LambdaQueryWrapper<Account> warapper = new LambdaQueryWrapper<>();
    warapper.eq(Account::getTenantId, tenantId);
    warapper.eq(Account::getCustomerNo, customerNo);
    warapper.eq(Account::getCcy, ccy);
    warapper.eq(ObjectUtil.isNotNull(category), Account::getCategory, category);
    Account customerAccount = customerAccountMapper.selectOne(warapper);
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    MD5 md5 = null;
    AccountJournal accountJournal = new AccountJournal();
    if (amount.compareTo(BigDecimal.ZERO) < 1) {
      throw new BusinessException(ResponseCode.AMOUNT_MUST_GREATER_THAN_ZERO);
    }
    if (customerAccount == null) {
      customerAccount = new Account();
      customerAccount.setCustomerNo(customerNo);
      customerAccount.setTenantId(tenantId);
      customerAccount.setCcy(ccy);
      customerAccount.setType("0");
      customerAccount.setName(accountName);
      customerAccount.setCategory(category);
      customerAccount.setDecimals(decimals);
      String amountStr = decimalFormat.format(amount);
      if (AccountJournalEnum.INT_ACCOUNT.getCode().equals(journalDirection)) {
        customerAccount.setBalance(amount);
        accountJournal.setInOutFlag(1);
      } else {
        throw new BusinessException(ResponseCode.AMOUNT_MUST_GREATER_THAN_ZERO);
      }
      customerAccount.setBalance(amount);
      md5 = new MD5(customerAccount.getCustomerNo().getBytes());
      String checkCode = HexUtil.encodeHexStr(md5.digest(amountStr));
      System.out.println("1.Check Code: \n\n\n\n" + checkCode);
      customerAccount.setCheckCode(checkCode);
      customerAccount.setStatus(AccountEnum.NORMAL.getCode());
      customerAccountMapper.insert(customerAccount);
    } else {
      md5 = new MD5(customerAccount.getCustomerNo().getBytes());
      // 余额校验
      String checkCode = HexUtil.encodeHexStr(md5.digest(customerAccount.getBalance().toString()));
      System.out.println("2.Check Code: \n\n\n\n" + checkCode);
      System.out.println("3.getCheckCode: \n\n\n\n" + customerAccount.getCheckCode());
      if (!checkCode.equals(customerAccount.getCheckCode())) {
        throw new BusinessException(ResponseCode.ACCOUNT_BALANCE_ANNORMAL);
      }
      if (customerAccount.getStatus().equals(AccountEnum.FREEZE.getCode())) {
        throw new BusinessException(ResponseCode.ACCOUNT_NOT_EXISTS);
      }
      // 账户余额判断
      if (customerAccount.getBalance().compareTo(amount) == -1) {
        throw new BusinessException(ResponseCode.ACCOUNT_BALANCE_INSUFFICIENT);
      }

      if (AccountJournalEnum.INT_ACCOUNT.getCode().equals(journalDirection)) {
        customerAccount.setBalance(customerAccount.getBalance().add(amount));
        accountJournal.setInOutFlag(1);
      } else {
        customerAccount.setBalance(customerAccount.getBalance().subtract(amount));
        accountJournal.setInOutFlag(0);
      }
      String newBalance = decimalFormat.format(customerAccount.getBalance());
      String newCheckCode = HexUtil.encodeHexStr(md5.digest(newBalance));
      customerAccount.setCheckCode(newCheckCode);
    }

    accountJournal.setSerialNo(BsinSnowflake.getId());
    accountJournal.setAccountNo(customerAccount.getSerialNo());
    accountJournal.setAccountType(customerAccount.getType());
    accountJournal.setCustomerNo(customerAccount.getCustomerNo());
    accountJournal.setAmount(amount);
    //        accountJournal.setOrderNo(orderNo);
    accountJournal.setInOutFlag(journalDirection);
    accountJournal.setCcy(customerAccount.getCcy());
    accountJournal.setTenantId(customerAccount.getTenantId());
    customerAccountJournalMapper.insert(accountJournal);
    customerAccountMapper.updateById(customerAccount);

    return customerAccount;
  }

  public Account getAccountDetail(
      String merchantNo, String customerNo, String ccy, String category) {
    LambdaQueryWrapper<Account> warapper = new LambdaQueryWrapper<>();
    warapper.eq(Account::getCustomerNo, customerNo);
    warapper.eq(Account::getCcy, ccy);
    warapper.eq(Account::getCategory, category);
    Account customerAccount = customerAccountMapper.selectOne(warapper);
    return customerAccount;
  }

  public static void main(String[] args) {
    MD5 md5 = new MD5("1738934400126685184".getBytes());
    String checkCode = HexUtil.encodeHexStr(md5.digest("11166.00"));
    System.out.println(checkCode);
  }

}
