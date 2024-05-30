package me.flyray.bsin.infrastructure.biz;

import static me.flyray.bsin.constants.ResponseCode.INSUFFUCIENT_ASSETS_BALANCE;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import me.flyray.bsin.facade.service.AccountService;
import me.flyray.bsin.redis.manager.BsinCacheProvider;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.dto.ContractTransactionResp;
import me.flyray.bsin.blockchain.enums.ContractProtocolStandards;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.domain.ContractProtocol;
import me.flyray.bsin.domain.domain.CustomerDigitalAssets;
import me.flyray.bsin.domain.domain.CustomerPassCard;
import me.flyray.bsin.domain.domain.DigitalAssetsCollection;
import me.flyray.bsin.domain.domain.DigitalAssetsItem;
import me.flyray.bsin.domain.domain.DigitalAssetsItemObtainCode;
import me.flyray.bsin.domain.domain.MintJournal;
import me.flyray.bsin.domain.domain.TransferJournal;
import me.flyray.bsin.domain.enums.ObtainMethod;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.request.DigitalAssetsIssueReqDTO;
import me.flyray.bsin.facade.response.DigitalAssetsDetailRes;
import me.flyray.bsin.facade.service.EquityConfigService;
import me.flyray.bsin.infrastructure.mapper.ContractProtocolMapper;
import me.flyray.bsin.infrastructure.mapper.CustomerDigitalAssetsMapper;
import me.flyray.bsin.infrastructure.mapper.CustomerPassCardMapper;
import me.flyray.bsin.infrastructure.mapper.DigitalAssetsCollectionMapper;
import me.flyray.bsin.infrastructure.mapper.DigitalAssetsItemMapper;
import me.flyray.bsin.infrastructure.mapper.DigitalAssetsItemObtainCodeMapper;
import me.flyray.bsin.infrastructure.mapper.MintJournalMapper;
import me.flyray.bsin.infrastructure.mapper.TransferJournalMapper;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author bolei
 * @date 2023/8/9 13:51
 * @desc
 */
@Component
@Slf4j
public class DigitalAssetsItemBiz {

  @Value("${bsin.jiujiu.aesKey}")
  private String aesKey;

  @Value("${bsin.s11edao.ipfs.gateway}")
  private String ipfsGateway;

  @Autowired private DigitalAssetsItemMapper digitalAssetsItemMapper;

  @Autowired private DigitalAssetsCollectionMapper digitalAssetsCollectionMapper;

  @Autowired private ContractProtocolMapper contractProtocolMapper;

  @Autowired private DigitalAssetsItemObtainCodeMapper digitalAssetsItemObtainCodeMapper;

  @Autowired private MintJournalMapper mintJournalMapper;

  @Autowired private TransferJournalMapper transferJournalMapper;

  @Autowired private DigitalAssetsBiz digitalAssetsBiz;

  @Autowired private CustomerDigitalAssetsMapper customerDigitalAssetsMapper;

  @Autowired public CustomerPassCardMapper customerPassCardMapper;

  @Autowired private AnywebBiz anywebBiz;

  @Autowired private CustomerInfoBiz customerInfoBiz;

  @DubboReference(version = "dev")
  private EquityConfigService equityConfigService;

  @DubboReference(version = "dev")
  private AccountService accountService;

  @Autowired private BsinCacheProvider bsinCacheProvider;

  public DigitalAssetsDetailRes getDetail(
      String digitalAssetsCollectionNo, String itemSerialNo, BigInteger tokenId) {

    DigitalAssetsDetailRes digitalAssetsDetailRes = new DigitalAssetsDetailRes();
    // 数字资产Item信息
    DigitalAssetsItem digitalAssetsItem;
    if (itemSerialNo != null) {
      digitalAssetsItem = digitalAssetsItemMapper.selectById(itemSerialNo);
    } else {
      LambdaQueryWrapper<DigitalAssetsItem> digitalAssetsItemWarapper = new LambdaQueryWrapper<>();
      digitalAssetsItemWarapper.eq(
          DigitalAssetsItem::getDigitalAssetsCollectionNo, digitalAssetsCollectionNo);
      digitalAssetsItemWarapper.eq(
          ObjectUtil.isNotNull(tokenId), DigitalAssetsItem::getTokenId, tokenId);
      digitalAssetsItem = digitalAssetsItemMapper.selectOne(digitalAssetsItemWarapper);
    }

    // 查询发行商户
    Map<String, Object> merchant =
        customerInfoBiz.getMerchantBase(
            digitalAssetsItem.getMerchantNo(), digitalAssetsItem.getChainType());

    digitalAssetsItem.setMerchantName((String) merchant.get("merchantName"));
    digitalAssetsItem.setMerchantLogo((String) merchant.get("logoUrl"));
    digitalAssetsDetailRes.setDigitalAssetsItem(digitalAssetsItem);

    // 数字资产Collection信息
    DigitalAssetsCollection digitalAssetsCollection =
        digitalAssetsCollectionMapper.selectById(digitalAssetsCollectionNo);
    digitalAssetsDetailRes.setDigitalAssetsCollection(digitalAssetsCollection);

    // 数字资产contract信息
    ContractProtocol contractProtocol =
        contractProtocolMapper.selectById(digitalAssetsCollection.getContractProtocolNo());
    digitalAssetsDetailRes.setContractProtocol(contractProtocol);

    Map requestMap = new HashMap();
    // 查询数字资产的权益和详情
    Map<String, Object> equityResult = equityConfigService.getListByCategoryNo(requestMap);
    List<Map> equityList = (List<Map>) equityResult.get("data");
    digitalAssetsDetailRes.setEquityList(equityList);
    return digitalAssetsDetailRes;
  }

  /** 校验库存 */
  public boolean verifyInventory(DigitalAssetsItem digitalAssetsItem, String amount) {
    String inventoryStr = bsinCacheProvider.get("inventory:" + digitalAssetsItem.getSerialNo());
    // 1.如果为空则说明没有设置缓存，跳过
    BigDecimal inventory = digitalAssetsItem.getInventory();
    BigDecimal afterInventory = inventory.subtract(new BigDecimal(amount));

    if ((inventoryStr != null && (Integer.valueOf(inventoryStr) <= 0)
        || afterInventory.compareTo(new BigDecimal("0")) < 0)) {
      throw new BusinessException(ResponseCode.NFT_INVERTORY_NOT_ENOUGH);
    }
    // 2.先锁住库存：600S
    bsinCacheProvider.setEx(
        "inventory:" + digitalAssetsItem.getSerialNo(), String.valueOf(afterInventory), 600L);
    return true;
  }

  /** 更新库存 */
  public boolean updateInventory(
      DigitalAssetsItem digitalAssetsItem, String protocolStandards, String amount) {
    // 1.更新库存
    BigDecimal inventory = digitalAssetsItem.getInventory().subtract(new BigDecimal(amount));
    digitalAssetsItem.setInventory(inventory);
    digitalAssetsItemMapper.updateById(digitalAssetsItem);
    // 2.设置库存缓存
    bsinCacheProvider.set(
        "inventory:" + digitalAssetsItem.getSerialNo(), String.valueOf(inventory));

    // 3.更新tokenId 缓存和数据库
    if (protocolStandards.equals(ContractProtocolStandards.ERC721.getCode())) {
      digitalAssetsItem.setCurrentMintTokenId(
          digitalAssetsItem.getCurrentMintTokenId().add(new BigInteger(amount)));
      bsinCacheProvider.set(
          "tokenId:" + digitalAssetsItem.getSerialNo(),
          String.valueOf(digitalAssetsItem.getCurrentMintTokenId()));
    } else {
      bsinCacheProvider.set(
          "tokenId:" + digitalAssetsItem.getSerialNo(),
          String.valueOf(digitalAssetsItem.getTokenId()));
    }
    digitalAssetsItemMapper.updateById(digitalAssetsItem);

    return true;
  }

  /** anyweb 信息 */
  public Map getAnywebInfo(String code) throws IOException {
    // 根据授权码获取用户信息
    Map<String, String> anywebUserInfo = anywebBiz.getAnywebUserInfo(code);
    return anywebUserInfo;
  }

  /** 更新领取码 */
  public boolean updateObtainCode(DigitalAssetsItem digitalAssetsItem, String password) {
    if (ObtainMethod.FIXED_PASSWORD.getCode().equals(digitalAssetsItem.getObtainMethod())
        || ObtainMethod.RANDOM_PASSWORD.getCode().equals(digitalAssetsItem.getObtainMethod())) {
      // WHERE 条件的字段
      LambdaUpdateWrapper<DigitalAssetsItemObtainCode> updateWrapper =
          new UpdateWrapper<DigitalAssetsItemObtainCode>().lambda();
      updateWrapper.eq(DigitalAssetsItemObtainCode::getPassword, password);
      updateWrapper.eq(DigitalAssetsItemObtainCode::getAssetsNo, digitalAssetsItem.getSerialNo());
      // 要更新的字段
      DigitalAssetsItemObtainCode obtainCode = new DigitalAssetsItemObtainCode(); // 已经领取
      obtainCode.setStatus("2");
      // TODO 获取mintNo
      // obtainCode.setNftMintNo();
      // update()方法，第一个是要更新的 entity， 第二个是查询条件。
      digitalAssetsItemObtainCodeMapper.update(obtainCode, updateWrapper);
    }
    return true;
  }

  /** 更新客户资产表 inOutFlag: 1-入账 2-出账 */
  public boolean updateCustomerDigitalAssets(
      DigitalAssetsItem digitalAssetsItem, String customerNo, BigDecimal amount, int inOutFlag) {
    // 1.存在同类资产则相加
    CustomerDigitalAssets customerDigitalAssetsSelect =
        customerDigitalAssetsMapper.selectOne(
            new LambdaQueryWrapper<CustomerDigitalAssets>()
                .eq(CustomerDigitalAssets::getDigitalAssetsItemNo, digitalAssetsItem.getSerialNo())
                .eq(CustomerDigitalAssets::getCustomerNo, customerNo)
                .eq(
                    ObjectUtil.isNotNull(digitalAssetsItem.getTokenId()),
                    CustomerDigitalAssets::getTokenId,
                    digitalAssetsItem.getTokenId()));
    if (customerDigitalAssetsSelect != null) {
      if (inOutFlag == 1) {
        customerDigitalAssetsSelect.setAmount(customerDigitalAssetsSelect.getAmount().add(amount));
      } else {
        customerDigitalAssetsSelect.setAmount(
            customerDigitalAssetsSelect.getAmount().divide(amount));
        if (customerDigitalAssetsSelect.getAmount().compareTo(amount) < 0) {
          throw new BusinessException(INSUFFUCIENT_ASSETS_BALANCE);
        }
      }
      customerDigitalAssetsMapper.updateById(customerDigitalAssetsSelect);
    } else if (inOutFlag == 2) {
      throw new BusinessException(INSUFFUCIENT_ASSETS_BALANCE);
    } else {
      CustomerDigitalAssets customerDigitalAssets = new CustomerDigitalAssets();
      BeanUtil.copyProperties(digitalAssetsItem, customerDigitalAssets);
      customerDigitalAssets.setSerialNo(BsinSnowflake.getId());
      customerDigitalAssets.setDigitalAssetsItemNo(digitalAssetsItem.getSerialNo());
      customerDigitalAssets.setCustomerNo(customerNo);
      customerDigitalAssets.setAmount(amount);
      customerDigitalAssetsMapper.insert(customerDigitalAssets);
    }
    return true;
  }

  /** 更新客户passCard表 */
  public boolean insertCustomerPassCard(
      DigitalAssetsItem digitalAssetsItem, String customerNo, String contractAddress)
      throws Exception {
    CustomerPassCard customerPassCard = new CustomerPassCard();
    BeanUtil.copyProperties(digitalAssetsItem, customerPassCard);
    customerPassCard.setSerialNo(BsinSnowflake.getId());
    customerPassCard.setCustomerNo(customerNo);
    customerPassCard.setTokenId(digitalAssetsItem.getTokenId());
    customerPassCard.setDigitalAssetsItemNo(digitalAssetsItem.getSerialNo());
    customerPassCard.setAmount(1);

    // 查询合约，获取TBA address
    String tbaAddress =
        digitalAssetsBiz.getTbaAddress(
            contractAddress,
            digitalAssetsItem.getTokenId(),
            digitalAssetsItem.getChainType(),
            digitalAssetsItem.getChainEnv());
    System.out.println("tbaAddress:\n\n\n" + tbaAddress);
    customerPassCard.setTbaAddress(tbaAddress);
    customerPassCardMapper.insert(customerPassCard);
    return true;
  }

  /** 获取合约byteCode */
  public ContractProtocol getContractProtocol(
      DigitalAssetsIssueReqDTO digitalAssetsIssueReqDTO,
      String protocolCode,
      String contractProtocolNo) {
    ContractProtocol contractProtocol = null;
    if (contractProtocolNo != null) {
      contractProtocol = contractProtocolMapper.selectById(contractProtocolNo);
    } else if (protocolCode != null) {
      LambdaUpdateWrapper<ContractProtocol> warapper = new LambdaUpdateWrapper<>();
      warapper.eq(ContractProtocol::getProtocolCode, protocolCode);
      warapper.eq(ContractProtocol::getChainType, digitalAssetsIssueReqDTO.getChainType());
      // 根据合约模板查询合约contractBytecode
      contractProtocol = contractProtocolMapper.selectOne(warapper);
    } else {
      throw new BusinessException(ResponseCode.NOT_FOUND_CONTRACT_BYTECODE);
    }
    // 未查询到合约模板
    if (contractProtocol == null) {
      throw new BusinessException(ResponseCode.NOT_FOUND_CONTRACT_BYTECODE);
    }
    BeanUtil.copyProperties(contractProtocol, digitalAssetsIssueReqDTO);
    digitalAssetsIssueReqDTO.setProtocolType(contractProtocol.getType());
    digitalAssetsIssueReqDTO.setContractProtocolNo(contractProtocol.getSerialNo());
    digitalAssetsIssueReqDTO.setProtocolStandards(contractProtocol.getProtocolStandards());
    return contractProtocol;
  }

  /** 获取合约byteCode */
  public ContractProtocol getContractProtocol(String chainType, String protocolCode) {
    LambdaUpdateWrapper<ContractProtocol> warapper = new LambdaUpdateWrapper<>();
    warapper.eq(ContractProtocol::getProtocolCode, protocolCode);
    warapper.eq(ContractProtocol::getChainType, chainType);
    // 根据合约模板查询合约contractBytecode
    ContractProtocol contractProtocol = contractProtocolMapper.selectOne(warapper);
    // 未查询到合约模板
    if (contractProtocol == null) {
      throw new BusinessException(ResponseCode.NOT_FOUND_CONTRACT_BYTECODE);
    }
    return contractProtocol;
  }

  /**
   * 领取方式逻辑处理 1.免费领取/空投 2.购买 3.固定口令领取 4.随机口令 5.盲盒购买 领取处理 ERC1155: transfer insert transferJournal
   * ERC721： mint insert mintJournal
   */
  public boolean claimMethodProcess(
      DigitalAssetsItem digitalAssetsItem,
      DigitalAssetsCollection digitalAssetsCollection,
      ContractProtocol contractProtocol,
      String password,
      Map merchantCustomerBase,
      Map customerBase,
      String toAddress,
      String phone,
      String tokenIdStr,
      String amount,
      boolean limitClaim) {
    DigitalAssetsItemObtainCode obtainCode = null;

    ContractTransactionResp contractTransactionResp = new ContractTransactionResp();
    boolean addPrivilege = false;
    if (phone == null) {
      phone = (String) customerBase.get("phone");
    }
    String customerNo = (String) customerBase.get("customerNo");
    String customerName = (String) customerBase.get("username");
    String merchantNo = digitalAssetsItem.getMerchantNo();
    String fromCustomerNo = (String) merchantCustomerBase.get("customerNo");

    // 1.领取方式判断
    if (ObtainMethod.FIXED_PASSWORD.getCode().equals(digitalAssetsItem.getObtainMethod())
        || ObtainMethod.RANDOM_PASSWORD.getCode().equals(digitalAssetsItem.getObtainMethod())) {
      // 1.1.获取商品口令对比
      LambdaQueryWrapper<DigitalAssetsItemObtainCode> obtainCodeWarapper =
          new LambdaQueryWrapper<>();
      obtainCodeWarapper.eq(
          ObjectUtil.isNotNull(digitalAssetsItem.getSerialNo()),
          DigitalAssetsItemObtainCode::getAssetsNo,
          digitalAssetsItem.getSerialNo());
      obtainCodeWarapper.eq(
          ObjectUtil.isNotNull(password), DigitalAssetsItemObtainCode::getPassword, password);
      obtainCode = digitalAssetsItemObtainCodeMapper.selectOne(obtainCodeWarapper);
      if (obtainCode == null) {
        throw new BusinessException(ResponseCode.PASSWORD_ERROR);
      }
    }
    // 1.2.随机口令
    if (ObtainMethod.RANDOM_PASSWORD.getCode().equals(digitalAssetsItem.getObtainMethod())) {
      if (ObtainMethod.BUY.getCode().equals(obtainCode.getStatus())) {
        throw new BusinessException(ResponseCode.NFT_OBTAINED);
      }
    }

    // 1.3.购买
    if (ObtainMethod.BUY.getCode().equals(digitalAssetsItem.getObtainMethod())) {
      // TODO:
    }

    // 2.领取记录查询
    // 2.1ERC1155 判断 transfer记录
    if (limitClaim) {
      if (contractProtocol.getProtocolStandards().startsWith("ERC1155")) {
        LambdaQueryWrapper<TransferJournal> transferWarapper = new LambdaQueryWrapper<>();
        transferWarapper.eq(
            ObjectUtil.isNotNull(digitalAssetsItem.getDigitalAssetsCollectionNo()),
            TransferJournal::getDigitalAssetsCollectionNo,
            digitalAssetsItem.getDigitalAssetsCollectionNo());
        transferWarapper.eq(
            ObjectUtil.isNotNull(digitalAssetsItem.getTokenId()),
            TransferJournal::getTokenId,
            digitalAssetsItem.getTokenId());
        transferWarapper.eq(ObjectUtil.isNotNull(phone), TransferJournal::getToPhone, phone);
        transferWarapper.eq(
            ObjectUtil.isNotNull(customerNo), TransferJournal::getToCustomerNo, customerNo);
        transferWarapper.eq(
            ObjectUtil.isNotNull(toAddress), TransferJournal::getToAddress, toAddress);
        transferWarapper.eq(TransferJournal::getToAddress, toAddress);
        List<TransferJournal> transferJournals = transferJournalMapper.selectList(transferWarapper);
        if (transferJournals.size() > 0) {
          throw new BusinessException(ResponseCode.KYC_NFT_OBTAINED);
        }
      }
      // 2.2.ERC721 判断 mint记录
      else if (contractProtocol.getProtocolStandards().startsWith("ERC721")) {
        LambdaQueryWrapper<MintJournal> mintWarapper = new LambdaQueryWrapper<>();
        mintWarapper.eq(
            ObjectUtil.isNotNull(digitalAssetsItem.getDigitalAssetsCollectionNo()),
            MintJournal::getDigitalAssetsCollectionNo,
            digitalAssetsItem.getDigitalAssetsCollectionNo());
        mintWarapper.eq(
            ObjectUtil.isNotNull(digitalAssetsItem.getTokenId()),
            MintJournal::getTokenId,
            digitalAssetsItem.getTokenId());
        mintWarapper.eq(ObjectUtil.isNotNull(phone), MintJournal::getToPhone, phone);
        mintWarapper.eq(ObjectUtil.isNotNull(customerNo), MintJournal::getToCustomerNo, customerNo);
        mintWarapper.eq(ObjectUtil.isNotNull(toAddress), MintJournal::getToAddress, toAddress);
        List<MintJournal> mintJournals = mintJournalMapper.selectList(mintWarapper);
        if (mintJournals.size() > 0) {
          throw new BusinessException(ResponseCode.KYC_NFT_OBTAINED);
        }
      }
    }
    if (contractProtocol.getProtocolStandards().startsWith("ERC1155")) {
      // 3.ERC1155领取
      TransferJournal transferJournal = new TransferJournal();
      BeanUtil.copyProperties(digitalAssetsItem, transferJournal);
      transferJournal.setToAddress(toAddress);
      transferJournal.setFromCustomerNo(fromCustomerNo);
      transferJournal.setToCustomerNo(customerNo);
      transferJournal.setMerchantNo(merchantNo);
      transferJournal.setFromAddress((String) merchantCustomerBase.get("walletAddress"));
      transferJournal.setTokenId(digitalAssetsItem.getTokenId());
      transferJournal.setAmount(new BigInteger(amount));
      transferJournal.setMetadataImage(digitalAssetsItem.getCoverImage());
      contractTransactionResp =
          digitalAssetsBiz.transfer(
              digitalAssetsCollection.getContractAddress(),
              (String) merchantCustomerBase.get("privateKey"),
              addPrivilege,
              transferJournal,
              contractProtocol);
      // 生成一条 transfer 记录
      transferJournal.setTxHash(contractTransactionResp.getTxHash());
      transferJournal.setSerialNo(BsinSnowflake.getId());
      transferJournalMapper.insert(transferJournal);
    } else if (contractProtocol.getProtocolStandards().startsWith("ERC721")) {
      // 2.2 ERC721 领取
      if (tokenIdStr == null) {
        // 从缓存中获取
        tokenIdStr = bsinCacheProvider.get("tokenId:" + digitalAssetsItem.getSerialNo());
        if (tokenIdStr == null || new Integer(tokenIdStr).longValue() < 1) {
          // 设置当前铸造tokenId:
          tokenIdStr = String.valueOf(digitalAssetsItem.getCurrentMintTokenId());
          if (tokenIdStr == null || digitalAssetsItem.getCurrentMintTokenId().longValue() < 1) {
            throw new BusinessException(ResponseCode.ILLEGAL_TOKEN_ID);
          }
        }
      }
      BigInteger tokenId = new BigInteger(tokenIdStr);
      digitalAssetsItem.setTokenId(tokenId);
      MintJournal mintJournal =
          digitalAssetsBiz.generateMintjournal(
              digitalAssetsCollection,
              tokenId,
              digitalAssetsItem.getAssetsName(),
              digitalAssetsItem.getDescription(),
              null);
      mintJournal.setChainEnv(digitalAssetsCollection.getChainEnv());
      mintJournal.setTenantId(digitalAssetsItem.getTenantId());
      mintJournal.setDigitalAssetsCollectionNo(digitalAssetsItem.getDigitalAssetsCollectionNo());
      mintJournal.setToPhone(phone);
      //        mintJournal.setFromCustomerNo(fromCustomerNo);
      mintJournal.setToCustomerNo(customerNo);
      mintJournal.setMerchantNo(merchantNo);
      mintJournal.setToMinterName(customerName);
      mintJournal.setAssetsType(digitalAssetsItem.getAssetsType());
      contractTransactionResp =
          digitalAssetsBiz.mint(
              digitalAssetsCollection.getContractAddress(),
              (String) merchantCustomerBase.get("privateKey"),
              addPrivilege,
              mintJournal,
              contractProtocol);
      // 数据库中增加baseURI
      mintJournal.setMetadataUrl(ipfsGateway + mintJournal.getMetadataUrl());
      mintJournal.setDigitalAssetsItemNo(digitalAssetsItem.getSerialNo());
      mintJournal.setTxHash(contractTransactionResp.getTxHash());
      mintJournal.setChainType(contractProtocol.getChainType());
      mintJournal.setMultimediaType(digitalAssetsItem.getMultimediaType());

      // 生成一条 mint 记录
      mintJournalMapper.insert(mintJournal);
    }

    return true;
  }
}
