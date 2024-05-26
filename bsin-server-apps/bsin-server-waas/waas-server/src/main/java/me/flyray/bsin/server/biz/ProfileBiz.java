package me.flyray.bsin.server.biz;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.service.BsinBlockChainEngineFactory;
import me.flyray.bsin.domain.domain.*;
import me.flyray.bsin.infrastructure.mapper.ContractMapper;
import me.flyray.bsin.infrastructure.mapper.MetadataFileMapper;
import me.flyray.bsin.infrastructure.mapper.MetadataTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author leonard
 * @date 2023/11/14 24:40
 * @desc
 */
@Slf4j
@Component
public class ProfileBiz {
  @Autowired private DigitalAssetsBiz digitalAssetsBiz;
  @Autowired private BsinBlockChainEngineFactory bsinBlockChainEngineFactory;
  @Autowired private ContractMapper contractMapper;
  @Autowired private MetadataFileMapper metadataFileMapper;
  @Autowired private MetadataTemplateMapper metadataTemplateMapper;
  @Autowired private CustomerInfoBiz customerInfoBiz;

  @Value("${bsin.jiujiu.aesKey}")
  private String aesKey;

  @Value("${bsin.jiujiu.tenantAppType}")
  private String tenantAppType;

  public Map createProfile(CustomerProfile customerProfile) {
    return null;
  }
}
