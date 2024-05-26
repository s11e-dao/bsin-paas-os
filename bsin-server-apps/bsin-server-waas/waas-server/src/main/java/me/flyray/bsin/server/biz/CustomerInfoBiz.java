package me.flyray.bsin.server.biz;

import me.flyray.bsin.facade.service.AccountService;
import me.flyray.bsin.facade.service.MerchantService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.enums.ChainEnv;
import me.flyray.bsin.blockchain.enums.ChainType;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.CustomerService;

/**
 * @author bolei
 * @date 2023/8/9 13:51
 * @desc
 */
@Component
@Slf4j
public class CustomerInfoBiz {

  @Value("${bsin.jiujiu.aesKey}")
  private String aesKey;

  @DubboReference(version = "dev")
  private MerchantService merchantService;

  @DubboReference(version = "dev")
  private CustomerService customerService;

  @DubboReference(version = "dev")
  private AccountService accountService;

  public Map<String, Object> getMerchantBase(String merchantNo, String chainType) {
    // 1.查找资产商户
    Map<String, Object> reqMerchant = new HashMap();
    reqMerchant.put("serialNo", merchantNo);
    Map<String, Object> merchantData = merchantService.getDetail(reqMerchant);
    Map merchant = (Map) merchantData.get("data");
    return merchant;
  }

  /**
   * 获取DigitalAssetsItem所属商户的客户信息
   * TODO: 返回 object 非 Map
   * */
  public Map<String, Object> getMerchantCustomerBase(String merchantNo, String chainType) {
    Map<String, Object> merchantCustomerBase = null;
    // 1.查找资产商户
    Map<String, Object> reqMerchant = new HashMap();
    reqMerchant.put("serialNo", merchantNo);
    Map<String, Object> merchantData = merchantService.getDetail(reqMerchant);
    Map merchant = (Map) merchantData.get("data");

    // 2.查找资产商户的管理员客户
    Map<String, Object> reqCustomerBase = new HashMap();
    String merchantCustomerNo = (String) merchant.get("customerNo");
    reqCustomerBase.put("customerNo", merchantCustomerNo);
    Map<String, Object> merchantCustomerData = customerService.getDetail(reqCustomerBase);
    merchantCustomerBase = (Map) merchantCustomerData.get("data");

    if (merchantCustomerBase == null) {
      throw new BusinessException(ResponseCode.CUSTOMER_ERROR);
    }

    // 3.判断私钥是否设置和正确(此处应该为商户私钥，数字资产由商户发行)
    SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
    String privateKey = null;
    String walletAddress = null;
    if (chainType == null || ChainType.CONFLUX.getCode().equals(chainType)) {
      if (StringUtils.isBlank((String) merchantCustomerBase.get("walletPrivateKey"))) {
        throw new BusinessException(ResponseCode.MERCHANT_WALLET_PRIVATEKEY_ERROR);
      }
      privateKey =
          aes.decryptStr(
              (String) merchantCustomerBase.get("walletPrivateKey"), CharsetUtil.CHARSET_UTF_8);
      if (privateKey.length() != 64) {
        throw new BusinessException(ResponseCode.MERCHANT_WALLET_PRIVATEKEY_ERROR);
      }
      walletAddress = (String) merchantCustomerBase.get("walletAddress");
    } else {
      if (StringUtils.isBlank((String) merchantCustomerBase.get("evmWalletPrivateKey"))) {
        throw new BusinessException(ResponseCode.MERCHANT_WALLET_PRIVATEKEY_ERROR);
      }
      privateKey =
          aes.decryptStr(
              (String) merchantCustomerBase.get("evmWalletPrivateKey"), CharsetUtil.CHARSET_UTF_8);
      if (privateKey.length() != 64) {
        throw new BusinessException(ResponseCode.MERCHANT_WALLET_PRIVATEKEY_ERROR);
      }
      walletAddress = (String) merchantCustomerBase.get("evmWalletAddress");
    }
    merchantCustomerBase.put("privateKey", privateKey);
    merchantCustomerBase.put("walletAddress", walletAddress);
    return merchantCustomerBase;
  }

  /**
   * 获取客户信息
   * TODO: 返回 object 非 Map
   * */
  public Map<String, Object> getCustomerBase(String customerNo, String chainType) {
    Map<String, Object> customerBase = null;
    // 1.查找客户信息
    Map<String, Object> reqCustomerBase = new HashMap();
    reqCustomerBase.put("serialNo", customerNo);
    Map<String, Object> customerData = customerService.getDetail(reqCustomerBase);
    customerBase = (Map) customerData.get("data");

    if (customerBase == null) {
      throw new BusinessException(ResponseCode.CUSTOMER_ERROR);
    }

    // 3.判断私钥是否设置和正确(此处应该为商户私钥，数字资产由商户发行)
    SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
    String privateKey = null;
    String walletAddress = null;
    if (chainType == null || ChainType.CONFLUX.getCode().equals(chainType)) {
      if (StringUtils.isBlank((String) customerBase.get("walletPrivateKey"))) {
        throw new BusinessException(ResponseCode.CUSTOMER_WALLET_PRIVATEKEY_ERROR);
      }
      privateKey =
          aes.decryptStr((String) customerBase.get("walletPrivateKey"), CharsetUtil.CHARSET_UTF_8);
      if (privateKey.length() != 64) {
        throw new BusinessException(ResponseCode.CUSTOMER_WALLET_PRIVATEKEY_ERROR);
      }
      walletAddress = (String) customerBase.get("walletAddress");
    } else {
      if (StringUtils.isBlank((String) customerBase.get("evmWalletPrivateKey"))) {
        throw new BusinessException(ResponseCode.CUSTOMER_WALLET_PRIVATEKEY_ERROR);
      }
      privateKey =
          aes.decryptStr(
              (String) customerBase.get("evmWalletPrivateKey"), CharsetUtil.CHARSET_UTF_8);
      if (privateKey.length() != 64) {
        throw new BusinessException(ResponseCode.CUSTOMER_WALLET_PRIVATEKEY_ERROR);
      }
      walletAddress = (String) customerBase.get("evmWalletAddress");
    }
    customerBase.put("privateKey", privateKey);
    customerBase.put("walletAddress", walletAddress);

    return customerBase;
  }

  /**
   * 检验客户余额
   *
   *
   *
   * */
  public boolean checkAccountBalance(Map customerBase, String chainType, String chainEnv) {
    if (!ChainEnv.TEST.getCode().equals(chainEnv)) {
      Map reqMap = new HashMap();
      reqMap.put("customerNo", (String) customerBase.get("customerNo"));
      // TODO 调用crm中心冻结账户余额
      accountService.freeze(reqMap);
    }
    // TODO 判断用户账户余额是否充足
    else {

    }
    return true;
  }

  /** 扣费 */
  public boolean accountOut(Map customerBase, String chainEnv) {
    // TODO 判断网络：如果是测试网不需要扣费，解冻并扣费
    if (!ChainEnv.TEST.getCode().equals(chainEnv)) {
      // 扣除用户余额
      Map reqMap = new HashMap();
      reqMap.put("customerNo", (String) customerBase.get("customerNo"));
      reqMap.putAll(reqMap);
      // TODO 生成扣费订单，调用crm中心扣费
    }
    return true;
  }
}
