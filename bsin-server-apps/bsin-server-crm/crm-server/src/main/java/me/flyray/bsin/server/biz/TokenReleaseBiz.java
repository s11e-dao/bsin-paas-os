package me.flyray.bsin.server.biz;

import me.flyray.bsin.redis.manager.BsinCacheProvider;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import me.flyray.bsin.domain.domain.CustomerAccount;
import me.flyray.bsin.domain.enums.AccountCategory;
import me.flyray.bsin.facade.service.TokenParamService;
import me.flyray.bsin.infrastructure.mapper.CustomerAccountMapper;

/**
 * @author bolei
 * @date 2023/8/22
 * @desc
 */
@Component
public class TokenReleaseBiz {

  @Autowired private CustomerAccountMapper customerAccountMapper;

  @DubboReference(version = "dev")
  private TokenParamService tokenReleaseParamService;

  @Autowired private CustomerAccountBiz customerAccountBiz;

  @Autowired private BsinCacheProvider bsinCacheProvider;

  /**
   * 根据tokenParam参数进行数字积分链上铸造
   *
   * @param CustomerAccount
   */
  public void bcAccountRelease(CustomerAccount customerAccount, BigDecimal amount)
      throws Exception {

    // 1 用户BC积分余额账户入账
    CustomerAccount customerAccountRet =
        customerAccountBiz.inAccount(
            customerAccount.getTenantId(),
            customerAccount.getCustomerNo(),
            AccountCategory.BALANCE.getCode(),
            AccountCategory.BALANCE.getDesc(),
            customerAccount.getCcy(),
            customerAccount.getDecimals(),
            amount);

    // 失效时间???
    bsinCacheProvider.set(
        "customerAccount:"
            + customerAccount.getTenantId()
            + customerAccount.getCustomerNo()
            + customerAccount.getCcy(),
        customerAccountRet);

    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put("tenantId", customerAccount.getTenantId());
    requestMap.put("customerNo", customerAccount.getCustomerNo());
    requestMap.put("customerAccount", customerAccountRet);

    // 2.请求token释放分配
    Map respMap = tokenReleaseParamService.releaseBcPointToVirtualAccount(requestMap);
    Map respDataMap = (Map) respMap.get("data");
    if (!((String) respDataMap.get("code")).equals("000000")) {
      System.out.println(respMap.get("data").toString());
    } else {
      BigDecimal releaseAmount = (BigDecimal) respDataMap.get("releaseAmount");
      // 3.用户BC积分释放账户出账
      // TODO: 释放成功才出账
      customerAccountRet =
          customerAccountBiz.outAccount(
              customerAccount.getTenantId(),
              customerAccount.getCustomerNo(),
              AccountCategory.BALANCE.getCode(),
              AccountCategory.BALANCE.getDesc(),
              customerAccount.getCcy(),
              customerAccount.getDecimals(),
              releaseAmount);
    }
  }
}
