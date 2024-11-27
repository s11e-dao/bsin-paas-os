package me.flyray.bsin.server.biz;

import me.flyray.bsin.domain.entity.Account;
import me.flyray.bsin.domain.entity.TokenReleaseJournal;
import me.flyray.bsin.domain.enums.AccountCategory;
import me.flyray.bsin.facade.service.TokenParamService;
import me.flyray.bsin.infrastructure.mapper.AccountMapper;
import me.flyray.bsin.redis.provider.BsinCacheProvider;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bolei
 * @date 2023/8/22
 * @desc
 */
@Component
public class TokenReleaseBiz {

  @Autowired private AccountMapper customerAccountMapper;

  @DubboReference(version = "dev")
  private TokenParamService tokenReleaseParamService;

  @Autowired private AccountBiz customerAccountBiz;

  /**
   * 根据tokenParam参数进行数字积分链上铸造
   *
   * @param CustomerAccount
   */
  public void bcAccountRelease(Account account, BigDecimal amount, String orderNo,
                               String transactionType, String remark)
      throws Exception {

    // 1 用户BC积分余额账户入账
    Account customerAccountRet =
        customerAccountBiz.inAccount(
                account.getTenantId(),
                account.getBizRoleType(),
                account.getBizRoleTypeNo(),
                AccountCategory.BALANCE.getCode(),
                AccountCategory.BALANCE.getDesc(),
                account.getCcy(),
                orderNo,
                transactionType,
                account.getDecimals(),
                amount, remark);

    // 失效时间???
    BsinCacheProvider.put("crm",
        "account:"
            + account.getTenantId()
            + account.getBizRoleTypeNo()
            + account.getCcy(),
        customerAccountRet);

    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put("tenantId", account.getTenantId());
    requestMap.put("customerNo", account.getBizRoleTypeNo());
    requestMap.put("customerAccount", customerAccountRet);

    // 2.请求token释放分配
    TokenReleaseJournal tokenReleaseJournal = tokenReleaseParamService.releaseBcPointToVirtualAccount(requestMap);

    BigDecimal releaseAmount = tokenReleaseJournal.getAmout();

    // 3.用户BC积分释放账户出账
    // TODO: 释放成功才出账
    customerAccountRet =
            customerAccountBiz.outAccount(
                    account.getTenantId(),
                    account.getBizRoleType(),
                    account.getBizRoleTypeNo(),
                    AccountCategory.BALANCE.getCode(),
                    AccountCategory.BALANCE.getDesc(),
                    account.getCcy(),
                    orderNo,
                    transactionType,
                    account.getDecimals(),
                    releaseAmount, remark);
  }

}
