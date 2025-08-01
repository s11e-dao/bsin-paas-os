package me.flyray.bsin.payment.channel.wxpay;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.ProfitSharingService;
import com.github.binarywang.wxpay.service.TransferService;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.ProfitSharingServiceImpl;
import com.github.binarywang.wxpay.service.impl.TransferServiceImpl;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BsinWxPayServiceUtil {

  private static ConcurrentHashMap<String, WxPayService> concurrentWxServiceHashMap =
      new ConcurrentHashMap();

  private static ConcurrentHashMap<String, TransferService> concurrentWxTransferServiceHashMap =
      new ConcurrentHashMap();

  private static ConcurrentHashMap<String, ProfitSharingService>
      concurrentWxProfitSharingServiceHashMap = new ConcurrentHashMap();

  public WxPayService getWxPayService(WxPayConfig payConfig) {

    WxPayService wxPayService;
    wxPayService = (WxPayService) concurrentWxServiceHashMap.get(payConfig.getMchId());

    if (null != wxPayService) {
      return wxPayService;
    }

    final List<WxPayConfig> configs = new ArrayList<>();
    configs.add(payConfig);
    if (configs == null) {
      throw new RuntimeException("配置文件未生效！");
    }
    wxPayService = new WxPayServiceImpl();
    wxPayService.setMultiConfig(
        configs.stream()
            .map(
                a -> {
                  WxPayConfig config = new WxPayConfig();
                  config.setAppId(a.getAppId());
                  config.setServiceId(a.getServiceId());
                  config.setMchId(a.getMchId());
                  config.setMchKey(a.getMchKey());
                  config.setSignType(a.getSignType());
                  config.setApiV3Key(a.getApiV3Key());
                  config.setNotifyUrl(a.getNotifyUrl());
                  config.setKeyPath(a.getKeyPath());
                  config.setCertSerialNo(a.getCertSerialNo());
                  config.setPrivateKeyPath(a.getPrivateKeyPath());
                  return config;
                })
            .collect(Collectors.toMap(WxPayConfig::getMchId, a -> a, (o, n) -> o)));
    //    concurrentWxServiceHashMap.put(payConfig.getAppId(), wxPayService);
    concurrentWxServiceHashMap.put(payConfig.getMchId(), wxPayService);
    return wxPayService;
  }

  public TransferService getWxTransferService(WxPayConfig payConfig) {
    TransferService transferService;
    transferService =
        (TransferService) concurrentWxTransferServiceHashMap.get(payConfig.getMchId());
    if (null != transferService) {
      return transferService;
    }
    WxPayService wxPayService = this.getWxPayService(payConfig);
    transferService = new TransferServiceImpl(wxPayService);

    //    concurrentWxServiceHashMap.put(payConfig.getAppId(), wxPayService);
    return transferService;
  }

  public ProfitSharingService getProfitSharingService(WxPayConfig payConfig) {
    ProfitSharingService profitSharingService;
    profitSharingService =
        (ProfitSharingService) concurrentWxProfitSharingServiceHashMap.get(payConfig.getMchId());
    if (null != profitSharingService) {
      return profitSharingService;
    }
    WxPayService wxPayService = this.getWxPayService(payConfig);

    profitSharingService = new ProfitSharingServiceImpl(wxPayService);

    return profitSharingService;
  }
}
