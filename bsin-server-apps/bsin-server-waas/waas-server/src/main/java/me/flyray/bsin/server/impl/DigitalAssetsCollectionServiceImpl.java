package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.dto.ContractTransactionResp;
import me.flyray.bsin.blockchain.enums.ContractProtocolStandards;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.domain.enums.AccountCategory;
import me.flyray.bsin.domain.enums.AssetsCollectionType;
import me.flyray.bsin.domain.enums.ObtainMethod;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.request.DigitalAssetsIssueReqDTO;
import me.flyray.bsin.facade.request.DigitalAssetsPutShelvesDTO;
import me.flyray.bsin.facade.service.AccountService;
import me.flyray.bsin.facade.service.DigitalAssetsCollectionService;
import me.flyray.bsin.infrastructure.biz.CustomerInfoBiz;
import me.flyray.bsin.infrastructure.biz.DigitalAssetsBiz;
import me.flyray.bsin.infrastructure.biz.DigitalAssetsItemBiz;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.redis.provider.BsinCacheProvider;
import me.flyray.bsin.redis.provider.BsinRedisProvider;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.InvertCodeGenerator;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.MERCHANT_NO_IS_NULL;
import static me.flyray.bsin.constants.ResponseCode.TENANT_ID_NOT_ISNULL;

/**
 * @author bolei
 * @date 2023/6/26 15:20
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/digitalAssetsCollection", timeout = 6000)
@ApiModule(value = "digitalAssetsCollection")
@Service
public class DigitalAssetsCollectionServiceImpl implements DigitalAssetsCollectionService {

  @Autowired private DigitalAssetsCollectionMapper digitalAssetsCollectionMapper;
  @Autowired private DigitalAssetsItemMapper digitalAssetsItemMapper;
  @Autowired private ContractProtocolMapper contractProtocolMapper;
  @Autowired private DigitalAssetsItemObtainCodeMapper digitalAssetsItemObtainCodeMapper;
  @Autowired private MintJournalMapper mintJournalMapper;
  @Autowired private TransferJournalMapper transferJournalMapper;
  @Autowired private DigitalAssetsBiz digitalAssetsBiz;
  @Autowired private MetadataFileMapper metadataFileMapper;
  @Autowired private DigitalAssetsItemBiz digitalAssetsItemBiz;
  @Autowired private CustomerInfoBiz customerInfoBiz;

  @DubboReference(version = "${dubbo.provider.version}")
  private AccountService accountService;

  /**
   * 部署数字资产智能合约
   *
   * @param requestMap 请求参数 1、品牌商户发行资产类型
   * @see AssetsCollectionType 2、不同资产协议，铸造入参不一样
   * @return
   * @throws Exception
   */
  @ShenyuDubboClient("/issue")
  @ApiDoc(desc = "issue")
  @Override
  @Transactional
  public Map<String, Object> issue(Map<String, Object> requestMap) throws Exception {
    log.info("DigitalAssetsService issue 请求参数:{}", JSON.toJSONString(requestMap));
    // 发行的商户
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    String tenantId = loginUser.getTenantId();
    DigitalAssetsIssueReqDTO digitalAssetsIssueReqDTO =
        BsinServiceContext.getReqBodyDto(DigitalAssetsIssueReqDTO.class, requestMap);
    BsinServiceContext.getReqBodyDto(DigitalAssetsIssueReqDTO.class, requestMap);

    String contractProtocolNo = MapUtils.getString(requestMap, "contractProtocolNo");

    String protocolCode = MapUtils.getString(requestMap, "protocolCode");
    //    if (protocolCode == null) {
    //      throw new BusinessException("100000", "protocolCode为空！！！");
    //    }

    String metadataTemplateNo = digitalAssetsIssueReqDTO.getMetadataTemplateNo();
    String chainEnv = digitalAssetsIssueReqDTO.getChainEnv();
    String chainType = digitalAssetsIssueReqDTO.getChainType();
    String metadataImageSameFlag = digitalAssetsIssueReqDTO.getMetadataImageSameFlag();
    // 元数据存放路径编号
    String metadataFilePathNo = digitalAssetsIssueReqDTO.getMetadataFilePathNo();
    String bondingCurveFlag = digitalAssetsIssueReqDTO.getBondingCurveFlag();
    // 合约是否被赞数
    String sponsorFlag = digitalAssetsIssueReqDTO.getSponsorFlag();

    // 1.获取商户的客户信息
    Map merchantCustomerBase = customerInfoBiz.getMerchantCustomerBase(merchantNo, chainType);
    digitalAssetsIssueReqDTO.setPrivateKey((String) merchantCustomerBase.get("privateKey"));
    digitalAssetsIssueReqDTO.setOwnerAddress((String) merchantCustomerBase.get("walletAddress"));

    // 2.账户余额判断
    customerInfoBiz.checkAccountBalance(merchantCustomerBase, chainType, chainEnv);

    // 3.根据protocolCode和chainType 获取 contractProtocol
    ContractProtocol contractProtocol =
        digitalAssetsItemBiz.getContractProtocol(
            digitalAssetsIssueReqDTO, protocolCode, contractProtocolNo);

    // 4.部署合约
    ContractTransactionResp contractTransactionResp =
        digitalAssetsBiz.deployContract(digitalAssetsIssueReqDTO);

    // 5.账户扣费
    customerInfoBiz.accountOut(merchantCustomerBase, chainEnv);

    // 6. digitalAssetsCollectionMapper 数据插入
    DigitalAssetsCollection digitalAssetsColletion = new DigitalAssetsCollection();
    // 对象赋值
    BeanUtil.copyProperties(digitalAssetsIssueReqDTO, digitalAssetsColletion);
    digitalAssetsColletion.setMerchantNo(merchantNo);
    digitalAssetsColletion.setTenantId(tenantId);
    // 写入一个NFT集合
    digitalAssetsColletion.setContractAddress(contractTransactionResp.getContractAddress());
    digitalAssetsColletion.setSponsorFlag(sponsorFlag);
    // contractProtocolCode 主题协议类型
    // 合约 metadata 文件夹
    digitalAssetsColletion.setMetadataFilePathNo(metadataFilePathNo);
    // 合约 metadata 文件模板
    digitalAssetsColletion.setMetadataTemplateNo(metadataTemplateNo);
    digitalAssetsColletion.setCreateBy(customerNo);
    digitalAssetsColletion.setContractProtocolNo(contractProtocol.getSerialNo());
    digitalAssetsColletion.setName((String) requestMap.get("name"));
    digitalAssetsColletion.setChainEnv(chainEnv);
    digitalAssetsColletion.setChainType(contractProtocol.getChainType());
    if (digitalAssetsIssueReqDTO.getDecimals() != null) {
      digitalAssetsColletion.setDecimals(digitalAssetsIssueReqDTO.getDecimals());
    } else {
      digitalAssetsColletion.setDecimals(0);
    }

    BigDecimal melo =
        new BigDecimal(Math.pow(10, digitalAssetsColletion.getDecimals().doubleValue()));
    if (digitalAssetsIssueReqDTO.getInitialSupply() != null) {
      digitalAssetsColletion.setInitialSupply(
          digitalAssetsIssueReqDTO.getInitialSupply().multiply(melo));
    } else {
      digitalAssetsColletion.setInitialSupply(new BigDecimal("0"));
    }
    digitalAssetsColletion.setTotalSupply(
        new BigDecimal((String) requestMap.get("totalSupply")).multiply(melo));
    digitalAssetsColletion.setInventory(
        new BigDecimal((String) requestMap.get("totalSupply")).multiply(melo));

    digitalAssetsColletion.setMetadataImageSameFlag(metadataImageSameFlag);
    digitalAssetsColletion.setCollectionType(digitalAssetsIssueReqDTO.getAssetsCollectionType());
    digitalAssetsCollectionMapper.insert(digitalAssetsColletion);

    log.info("DigitalAssetsService issue 相应结果:{}", JSON.toJSONString(contractTransactionResp));
    return RespBodyHandler.setRespBodyDto(contractTransactionResp);
  }

  @ShenyuDubboClient("/mint")
  @ApiDoc(desc = "mint")
  @Override
  public Map<String, Object> mint(Map<String, Object> requestMap) throws Exception {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }

    String amount = MapUtils.getString(requestMap, "amount");
    if (amount == null) {
      throw new BusinessException("100000", "未指定铸造数量");
    }

    // 1.查询资产集合
    // 1.1.根据合约地址查询合约信息--ContractProtocol
    //    String contract = (String) requestMap.get("contract");
    ContractProtocol contractProtocol = new ContractProtocol();
    //    if (contract != null) {
    //       contractProtocol =
    //          contractProtocolMapper.getContractProtocolByContract(contract);
    //    } else {
    // 1.2.查询资产集合
    String digitalAssetsCollectionNo = MapUtils.getString(requestMap, "digitalAssetsCollectionNo");
    DigitalAssetsCollection digitalAssetsCollection =
        digitalAssetsCollectionMapper.selectById(digitalAssetsCollectionNo);
    if (digitalAssetsCollection == null) {
      throw new BusinessException("100000", "未查询到数字积分合约集合资产:" + digitalAssetsCollectionNo);
    }
    contractProtocol =
        contractProtocolMapper.selectById(digitalAssetsCollection.getContractProtocolNo());
    //    }
    MintJournal mintJournal = BsinServiceContext.getReqBodyDto(MintJournal.class, requestMap);
    mintJournal.setAmount(new BigDecimal(amount));
    mintJournal.setChainType(digitalAssetsCollection.getChainType());
    mintJournal.setChainEnv(digitalAssetsCollection.getChainEnv());
    String toAddress = mintJournal.getToAddress();
    boolean addPrivilege = Boolean.valueOf((String) requestMap.get("addPrivilege"));

    // 2.找到资产商户的客户信息
    Map merchantCustomerBase =
        customerInfoBiz.getMerchantCustomerBase(merchantNo, mintJournal.getChainType());
    String privateKey = (String) merchantCustomerBase.get("privateKey");

    // 3.找到客户信息(目标铸造对象)
    Map customerBase = customerInfoBiz.getCustomerBase(customerNo, mintJournal.getChainType());
    String phone = (String) customerBase.get("phone");
    if (toAddress == null) {
      toAddress = (String) customerBase.get("walletAddress");
    }

    // 3.mintJournal
    mintJournal.setToAddress(toAddress);
    mintJournal.setToCustomerNo(customerNo);
    ContractTransactionResp contractTransactionResp =
        digitalAssetsBiz.mint(
            digitalAssetsCollection.getContractAddress(),
            privateKey,
            addPrivilege,
            mintJournal,
            contractProtocol);
    mintJournal.setAssetsType(contractProtocol.getType());
    //    mintJournal.setMetadataUrl();
    //    mintJournal.setMetadataImage();

    // 4.写入mint记录
    mintJournalMapper.insert(mintJournal);

    // 5.mint数字积分时对数字积分账户 account 进行 入账操作 用户DP余额账户入账
    //    requestMap.put("amount",amount);
    requestMap.put("ccy", digitalAssetsCollection.getSymbol());
    requestMap.put("category", AccountCategory.BALANCE.getCode());
    requestMap.put("name", AccountCategory.BALANCE.getDesc());
    Map respMap = accountService.inAccount(requestMap);

    return RespBodyHandler.setRespBodyDto(respMap);
  }

  @ShenyuDubboClient("/transfer")
  @ApiDoc(desc = "transfer")
  @Override
  public Map<String, Object> transfer(Map<String, Object> requestMap) throws Exception {
    log.info("AdminBlockChainService transferNft 请求参数:{}", JSON.toJSONString(requestMap));
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    TransferJournal transferJournal =
        BsinServiceContext.getReqBodyDto(TransferJournal.class, requestMap);
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    String tenantId = (String) requestMap.get("tenantId");
    String chainEnv = (String) requestMap.get("chainEnv");
    String chainType = (String) requestMap.get("chainType");
    String contract = (String) requestMap.get("contract");
    boolean addPrivilege = Boolean.valueOf((String) requestMap.get("addPrivilege"));
    Boolean isObtain = (Boolean) requestMap.get("isObtain");
    String privateKey = (String) requestMap.get("privateKey");
    BigInteger tokenId = (BigInteger) requestMap.get("tokenId");
    String toAddress = transferJournal.getToAddress();

    // 1.获取客户信息
    Map customerBase = customerInfoBiz.getCustomerBase(customerNo, chainType);
    String phone = (String) customerBase.get("phone");
    if (toAddress == null) {
      toAddress = (String) customerBase.get("walletAddress");
    }
    if (privateKey == null) {
      privateKey = (String) customerBase.get("walletPrivateKey");
    }
    if (toAddress == null) {
      throw new BusinessException(ResponseCode.CUSTOMER_WALLET_ISNULL);
    }

    if (privateKey == null) {
      throw new BusinessException(ResponseCode.CUSTOMER_WALLET_PRIVATEKEY_ERROR);
    }

    // 2.账户余额判断
    customerInfoBiz.checkAccountBalance(customerBase, chainType, chainEnv);

    // 3.根据contractAddress 获取 contractProtocol
    ContractProtocol contractProtocol =
        contractProtocolMapper.getContractProtocolByContract(contract);

    // 4.領取接口才限制
    if (isObtain != null && isObtain == true) {
      BsinRedisProvider.setCacheObject(
          transferJournal.getDigitalAssetsCollectionNo()
              + ":"
              + transferJournal.getToAddress()
              + "isObtained",
          "true",
              Duration.ofSeconds(120));
    }

    // 5.转账
    ContractTransactionResp contractTransactionResp =
        digitalAssetsBiz.transfer(
            contract, privateKey, addPrivilege, transferJournal, contractProtocol);

    // 6.账户扣费
    customerInfoBiz.accountOut(customerBase, chainEnv);

    // 7.登记转账记录
    transferJournal.setTokenId(tokenId);
    transferJournal.setTenantId(tenantId);
    transferJournal.setTxHash(contractTransactionResp.getTxHash());
    transferJournal.setChainEnv(chainEnv);
    transferJournal.setChainType(chainType);
    transferJournal.setAssetsType(contractProtocol.getType());
    transferJournalMapper.insert(transferJournal);
    log.info("trasaction 相应结果:{}", JSON.toJSONString(contractTransactionResp));
    return RespBodyHandler.setRespBodyDto(contractTransactionResp);
  }

  @ShenyuDubboClient("/airdrop")
  @ApiDoc(desc = "airdrop")
  @Override
  public Map<String, Object> airdrop(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @ShenyuDubboClient("/batchTransfer")
  @ApiDoc(desc = "batchTransfer")
  @Override
  public Map<String, Object> batchTransfer(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @ShenyuDubboClient("/getSponsor")
  @ApiDoc(desc = "getSponsor")
  @Override
  public Map<String, Object> getSponsor(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @ShenyuDubboClient("/setSponsor")
  @ApiDoc(desc = "setSponsor")
  @Override
  public Map<String, Object> setSponsor(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @ShenyuDubboClient("/isWhitelisted")
  @ApiDoc(desc = "isWhitelisted")
  @Override
  public Map<String, Object> isWhitelisted(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @ShenyuDubboClient("/addWhiteList")
  @ApiDoc(desc = "addWhiteList")
  @Override
  public Map<String, Object> addWhiteList(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @ShenyuDubboClient("/removeWhiteList")
  @ApiDoc(desc = "removeWhiteList")
  @Override
  public Map<String, Object> removeWhiteList(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @ShenyuDubboClient("/burn")
  @ApiDoc(desc = "burn")
  @Override
  public Map<String, Object> burn(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @ShenyuDubboClient("/getPageList")
  @ApiDoc(desc = "getPageList")
  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {
    // 发行的商户
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = loginUser.getMerchantNo();
    String tenantId = loginUser.getTenantId();
    DigitalAssetsCollection digitalAssetsColletion =
        BsinServiceContext.getReqBodyDto(DigitalAssetsCollection.class, requestMap);
    Object paginationObj =  requestMap.get("pagination");
    me.flyray.bsin.server.utils.Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj,pagination);
    Page<DigitalAssetsCollection> page =
        new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<DigitalAssetsCollection> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(DigitalAssetsCollection::getCreateTime);
    warapper.eq(ObjectUtil.isNotNull(tenantId), DigitalAssetsCollection::getTenantId, tenantId);
    warapper.eq(
        ObjectUtil.isNotNull(merchantNo), DigitalAssetsCollection::getMerchantNo, merchantNo);
    warapper.eq(
        ObjectUtil.isNotNull(digitalAssetsColletion.getName()),
        DigitalAssetsCollection::getName,
        digitalAssetsColletion.getName());
    warapper.eq(
        ObjectUtil.isNotNull(digitalAssetsColletion.getSymbol()),
        DigitalAssetsCollection::getSymbol,
        digitalAssetsColletion.getSymbol());
    warapper.eq(
        ObjectUtil.isNotNull(digitalAssetsColletion.getContractProtocolNo()),
        DigitalAssetsCollection::getContractProtocolNo,
        digitalAssetsColletion.getContractProtocolNo());
    warapper.eq(
        ObjectUtil.isNotNull(digitalAssetsColletion.getCollectionType()),
        DigitalAssetsCollection::getCollectionType,
        digitalAssetsColletion.getCollectionType());
    warapper.eq(
        ObjectUtil.isNotNull(digitalAssetsColletion.getChainEnv()),
        DigitalAssetsCollection::getChainEnv,
        digitalAssetsColletion.getChainEnv());
    warapper.eq(
        ObjectUtil.isNotNull(digitalAssetsColletion.getChainType()),
        DigitalAssetsCollection::getChainType,
        digitalAssetsColletion.getChainType());
    IPage<DigitalAssetsCollection> pageList =
        digitalAssetsCollectionMapper.selectPage(page, warapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  @ShenyuDubboClient("/getList")
  @ApiDoc(desc = "getList")
  @Override
  public Map<String, Object> getList(Map<String, Object> requestMap) {
    // 发行的商户
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = loginUser.getMerchantNo();
    String tenantId = loginUser.getTenantId();
    DigitalAssetsCollection digitalAssetsColletion =
        BsinServiceContext.getReqBodyDto(DigitalAssetsCollection.class, requestMap);
    LambdaQueryWrapper<DigitalAssetsCollection> warapper = new LambdaQueryWrapper<>();
    warapper.orderByDesc(DigitalAssetsCollection::getCreateTime);
    warapper.eq(DigitalAssetsCollection::getTenantId, tenantId);
    warapper.eq(DigitalAssetsCollection::getMerchantNo, merchantNo);
    warapper.eq(
        ObjectUtil.isNotNull(digitalAssetsColletion.getName()),
        DigitalAssetsCollection::getName,
        digitalAssetsColletion.getName());
    warapper.eq(
        ObjectUtil.isNotNull(digitalAssetsColletion.getSymbol()),
        DigitalAssetsCollection::getSymbol,
        digitalAssetsColletion.getSymbol());
    warapper.eq(
        ObjectUtil.isNotNull(digitalAssetsColletion.getContractProtocolNo()),
        DigitalAssetsCollection::getContractProtocolNo,
        digitalAssetsColletion.getContractProtocolNo());
    warapper.eq(
        ObjectUtil.isNotNull(digitalAssetsColletion.getCollectionType()),
        DigitalAssetsCollection::getCollectionType,
        digitalAssetsColletion.getCollectionType());
    List<DigitalAssetsCollection> contractProtocolList =
        digitalAssetsCollectionMapper.selectList(warapper);
    return RespBodyHandler.setRespBodyListDto(contractProtocolList);
  }

  @ShenyuDubboClient("/getDetail")
  @ApiDoc(desc = "getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    DigitalAssetsCollection digitalAssetsColletion =
        digitalAssetsCollectionMapper.selectById(serialNo);
    return RespBodyHandler.setRespBodyDto(digitalAssetsColletion);
  }

  /**
   * ERC1155协议资产上架有铸造过程 ERC721协议上架只是写入数据 1、请求参数校验 2、判断协议标准 3、不同协议走不同的逻辑实现
   *
   * @param requestMap
   * @return
   */
  @ShenyuDubboClient("/putOnShelves")
  @ApiDoc(desc = "putOnShelves")
  @Override
  @Transactional
  public Map<String, Object> putOnShelves(Map<String, Object> requestMap) {
    log.info("NFT 上架 :{}", JSON.toJSONString(requestMap));
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();

    String teantId = (String) requestMap.get("teantId");
    if (teantId == null) {
      teantId = loginUser.getTenantId();
      if (teantId == null) {
        throw new BusinessException(TENANT_ID_NOT_ISNULL);
      }
    }

    // 发行的商户
    String merchantNo = (String) requestMap.get("merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (teantId == null) {
        throw new BusinessException(MERCHANT_NO_IS_NULL);
      }
    }

    String customerNo = (String) requestMap.get("customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }

    DigitalAssetsPutShelvesDTO digitalAssetsPutShelvesDTO =
        BsinServiceContext.getReqBodyDto(DigitalAssetsPutShelvesDTO.class, requestMap);

    // 1.查找collection
    DigitalAssetsCollection digitalAssetsColletion =
        digitalAssetsCollectionMapper.selectById(
            digitalAssetsPutShelvesDTO.getDigitalAssetsCollectionNo());

    ContractProtocol contractProtocol =
        contractProtocolMapper.selectById(digitalAssetsColletion.getContractProtocolNo());
    if (contractProtocol == null) {
      throw new BusinessException(ResponseCode.NOT_FOUND_CONTRACT);
    }
    BigDecimal putOnQuantity = digitalAssetsPutShelvesDTO.getPutOnQuantity();

    // 2.更新库存: 1155每次上架库存-1
    if (contractProtocol
        .getProtocolStandards()
        .equals(ContractProtocolStandards.ERC1155.getCode())) {
      if (digitalAssetsColletion.getInventory().compareTo(new BigDecimal("0")) < 0) {
        throw new BusinessException(ResponseCode.INSUFFFICIENT_INVENTORY);
      }
      digitalAssetsColletion.setInventory(
          digitalAssetsColletion.getInventory().subtract(new BigDecimal("1")));
    } else if (contractProtocol
            .getProtocolStandards()
            .equals(ContractProtocolStandards.ERC721.getCode())
        || contractProtocol
            .getProtocolStandards()
            .equals(ContractProtocolStandards.ERC20.getCode())) {
      if (putOnQuantity.compareTo(digitalAssetsColletion.getInventory()) > 0) {
        throw new BusinessException(ResponseCode.INSUFFFICIENT_INVENTORY);
      }
      digitalAssetsColletion.setInventory(
          digitalAssetsColletion.getInventory().subtract(putOnQuantity));
    } else {
      throw new BusinessException(ResponseCode.ILLEGAL_ASSETS_PROTOCOL);
    }

    // 3.查找资产 item
    LambdaUpdateWrapper<DigitalAssetsItem> digitalAssetsItemWarapper = new LambdaUpdateWrapper<>();
    digitalAssetsItemWarapper.eq(
        DigitalAssetsItem::getDigitalAssetsCollectionNo, digitalAssetsColletion.getSerialNo());
    digitalAssetsItemWarapper.eq(
        ObjectUtil.isNotNull(digitalAssetsPutShelvesDTO.getTokenId()),
        DigitalAssetsItem::getTokenId,
        digitalAssetsPutShelvesDTO.getTokenId());
    DigitalAssetsItem digitalAssetsItem =
        digitalAssetsItemMapper.selectOne(digitalAssetsItemWarapper);
    boolean notNull = false;
    if (digitalAssetsItem != null) {
      notNull = true;
    }

    // 4.资产上架(库存(inventory库存：每次上架会增加，领取会减少)|quantity(数量：上架的数量，721每次上架会增加，不会减少(流通量))更新)
    digitalAssetsItem =
        differentStandardsProtocolPutOnShelves(
            digitalAssetsPutShelvesDTO,
            contractProtocol,
            digitalAssetsColletion,
            digitalAssetsItem);

    // 5.insert digitalAssetsItem
    // 如果领取方式为口令领取的价格为零
    DigitalAssetsItemObtainCode obtainCode = null;
    if (obtainCode != null) {
      digitalAssetsItem.setPrice(BigDecimal.ZERO);
    }
    digitalAssetsItem.setDigitalAssetsCollectionNo(
        digitalAssetsPutShelvesDTO.getDigitalAssetsCollectionNo());

    digitalAssetsItem.setAssetsType(contractProtocol.getType());
    digitalAssetsItem.setMerchantNo(merchantNo);
    digitalAssetsItem.setTenantId(teantId);
    digitalAssetsItem.setChainType(digitalAssetsColletion.getChainType());
    digitalAssetsItem.setChainEnv(digitalAssetsColletion.getChainEnv());
    digitalAssetsItem.setSerialNo(BsinSnowflake.getId());
    if (notNull
        && contractProtocol
            .getProtocolStandards()
            .equals(ContractProtocolStandards.ERC1155.getCode())) {
      log.info("alread update..................");
    } else {
      digitalAssetsItemMapper.insert(digitalAssetsItem);
    }
    log.info("上架资产参数:{}", digitalAssetsItem);

    // 6.写入商品领取码
    if (ObtainMethod.RANDOM_PASSWORD.getCode().equals(digitalAssetsItem.getObtainMethod())) {
      // putOnQuantity 根据数量生成领取码
      List<String> codes = InvertCodeGenerator.genCodes(6, putOnQuantity.longValue());
      for (int i = 0; i < putOnQuantity.longValue(); i++) {
        DigitalAssetsItemObtainCode obtainCodeParam = new DigitalAssetsItemObtainCode();
        obtainCodeParam.setPassword(codes.get(i));
        obtainCodeParam.setAssetsNo(digitalAssetsItem.getSerialNo());
        digitalAssetsItemObtainCodeMapper.insert(obtainCodeParam);
      }
    } else if (ObtainMethod.FIXED_PASSWORD.getCode().equals(digitalAssetsItem.getObtainMethod())) {
      DigitalAssetsItemObtainCode obtainCodeParam = new DigitalAssetsItemObtainCode();
      obtainCodeParam.setPassword(digitalAssetsPutShelvesDTO.getPassword());
      obtainCodeParam.setAssetsNo(digitalAssetsItem.getSerialNo());
      digitalAssetsItemObtainCodeMapper.insert(obtainCodeParam);
    } else if (ObtainMethod.BUY.getCode().equals(digitalAssetsItem.getObtainMethod())) {
      digitalAssetsItem.setPrice(new BigDecimal(digitalAssetsPutShelvesDTO.getPrice()));
    }

    // 7.修改上架状态 0、未流通 1、流通中 2、流通完成
    if (digitalAssetsColletion.getInventory().compareTo(new BigDecimal("0")) > 0) {
      digitalAssetsColletion.setStatus("1");
    } else {
      digitalAssetsColletion.setStatus("2");
    }
    digitalAssetsCollectionMapper.updateById(digitalAssetsColletion);

    // 8.更新客户资产表
    digitalAssetsItemBiz.updateCustomerDigitalAssets(
        digitalAssetsItem, customerNo, digitalAssetsPutShelvesDTO.getPutOnQuantity(), 1);

    // 9.更新库存
    BsinCacheProvider.put("waas",
            "inventory:" + digitalAssetsColletion.getSerialNo(),
            digitalAssetsColletion.getInventory().toString());

    return RespBodyHandler.RespBodyDto();
  }

  private DigitalAssetsItem differentStandardsProtocolPutOnShelves(
      DigitalAssetsPutShelvesDTO digitalAssetsPutShelvesDTO,
      ContractProtocol contractProtocol,
      DigitalAssetsCollection digitalAssetsColletion,
      DigitalAssetsItem digitalAssetsItem) {

    DigitalAssetsItem digitalAssetsItemRes = null;

    switch (ContractProtocolStandards.getInstanceById(contractProtocol.getProtocolStandards())) {
      case ERC1155:
        if (digitalAssetsItem != null) {
          throw new BusinessException(ResponseCode.TOKEN_ID_MINTED);
        }
        digitalAssetsItem = new DigitalAssetsItem();
        digitalAssetsItemRes =
            ERC1155PutOnShelves(
                digitalAssetsPutShelvesDTO,
                contractProtocol,
                digitalAssetsColletion,
                digitalAssetsItem);
        break;
      case ERC721:
      case ERC20:
        digitalAssetsItemRes =
            ERC721OrERC20PutOnShelves(digitalAssetsPutShelvesDTO, digitalAssetsItem);
        break;
      case ERC6551:
        digitalAssetsItemRes = ERC6551PutOnShelves(digitalAssetsPutShelvesDTO, digitalAssetsItem);
        break;
      default:
        throw new BusinessException(ResponseCode.ILLEGAL_ASSETS_PROTOCOL);
    }

    return digitalAssetsItemRes;
  }

  /** ERC1155 */
  private DigitalAssetsItem ERC1155PutOnShelves(
      DigitalAssetsPutShelvesDTO digitalAssetsPutShelvesDTO,
      ContractProtocol contractProtocol,
      DigitalAssetsCollection digitalAssetsColletion,
      DigitalAssetsItem digitalAssetsItem) {

    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String teantId = loginUser.getTenantId();
    // 发行的商户
    String merchantNo = loginUser.getMerchantNo();

    // 判断tokenId对应的多媒体资源是否存在
    MetadataFile metadataFile = null;
    LambdaUpdateWrapper<MetadataFile> metadataFileWarapper = new LambdaUpdateWrapper<>();
    metadataFileWarapper.eq(
        MetadataFile::getParentNo, digitalAssetsColletion.getMetadataFilePathNo());
    if (digitalAssetsColletion.getMetadataImageSameFlag() == "1") {
      metadataFileWarapper.eq(MetadataFile::getTokenId, '1');
    } else {
      metadataFileWarapper.eq(MetadataFile::getTokenId, digitalAssetsPutShelvesDTO.getTokenId());
    }
    // 排除json文件
    metadataFileWarapper.ne(MetadataFile::getFileType, '5');
    metadataFile = metadataFileMapper.selectOne(metadataFileWarapper);
    if (metadataFile == null) {
      throw new BusinessException(ResponseCode.TOKEN_ID_METADATA_IMAGE_NOT_EXISTS);
    }
    String imageUrl = metadataFile.getIpfsUrl();

    // 1.获取客户信息
    Map customerBase =
        customerInfoBiz.getCustomerBase(
            LoginInfoContextHelper.getCustomerNo(), digitalAssetsColletion.getChainType());
    String phone = (String) customerBase.get("phone");
    String toAddress = (String) customerBase.get("walletAddress");
    String privateKey = (String) customerBase.get("privateKey");
    if (toAddress == null) {
      throw new BusinessException(ResponseCode.CUSTOMER_WALLET_ISNULL);
    }
    if (privateKey == null) {
      throw new BusinessException(ResponseCode.CUSTOMER_WALLET_PRIVATEKEY_ERROR);
    }

    // 查找部署的合约
    digitalAssetsColletion.getContractAddress();
    // 获取metadataURI
    JSONObject metaDataURI =
        digitalAssetsBiz.genarateMetaDataURI(
            digitalAssetsColletion.getMetadataTemplateNo(),
            digitalAssetsColletion.getMetadataFilePathNo(),
            metadataFile,
            digitalAssetsPutShelvesDTO.getTokenId(),
            digitalAssetsPutShelvesDTO.getAssetsName(),
            digitalAssetsPutShelvesDTO.getDescription(),
            digitalAssetsPutShelvesDTO.getAttributes());

    MintJournal mintJournal = new MintJournal();
    BeanUtil.copyProperties(digitalAssetsPutShelvesDTO, mintJournal);
    mintJournal.setItemName(digitalAssetsPutShelvesDTO.getAssetsName());
    mintJournal.setAmount(digitalAssetsPutShelvesDTO.getPutOnQuantity());
    mintJournal.setChainEnv(digitalAssetsColletion.getChainEnv());
    mintJournal.setToAddress(toAddress);
    mintJournal.setChainType(digitalAssetsColletion.getChainType());
    mintJournal.setMetadataUrl((String) metaDataURI.get("fileHash"));
    BeanUtil.copyProperties(digitalAssetsPutShelvesDTO, digitalAssetsItem);

    digitalAssetsItem.setCoverImage(imageUrl);
    log.debug("铸造ERC1155!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
    ContractTransactionResp contractTransactionResp =
        digitalAssetsBiz.mint(
            digitalAssetsColletion.getContractAddress(),
            privateKey,
            true,
            mintJournal,
            contractProtocol);

    mintJournal.setTxHash(contractTransactionResp.getTxHash());
    mintJournal.setDigitalAssetsCollectionNo(digitalAssetsColletion.getSerialNo());
    // 修改带网关的
    mintJournal.setMetadataImage((String) metaDataURI.get("fileUrl"));
    mintJournal.setMetadataUrl(metadataFile.getIpfsUrl());
    // 插入mint记录
    // TODO:字段补齐：
    mintJournal.setMerchantNo(merchantNo);
    mintJournal.setTenantId(teantId);
    mintJournal.setToMinterName((String) customerBase.get("username"));
    mintJournal.setToCustomerNo((String) customerBase.get("customerNo"));
    mintJournal.setToPhone((String) customerBase.get("phone"));

    mintJournalMapper.insert(mintJournal);
    digitalAssetsItem.setAssetsName(digitalAssetsColletion.getName());
    digitalAssetsItem.setQuantity(digitalAssetsPutShelvesDTO.getPutOnQuantity());
    digitalAssetsItem.setInventory(digitalAssetsPutShelvesDTO.getPutOnQuantity());
    digitalAssetsItem.setCurrentMintTokenId(digitalAssetsPutShelvesDTO.getTokenId());
    digitalAssetsItem.setMetadataUrl(mintJournal.getMetadataUrl());

    return digitalAssetsItem;
  }

  /** ERC721|ERC20 */
  private DigitalAssetsItem ERC721OrERC20PutOnShelves(
      DigitalAssetsPutShelvesDTO digitalAssetsPutShelvesDTO, DigitalAssetsItem digitalAssetsItem) {
    //    // 判断是否已经存在上架记录，有则更新库存
    //    if (digitalAssetsItem != null) {
    //      // 更新库存
    //      digitalAssetsItem.setInventory(
    //
    // digitalAssetsItem.getInventory().add(digitalAssetsPutShelvesDTO.getPutOnQuantity()));
    //      // 更新数量
    //      digitalAssetsItem.setQuantity(
    //          digitalAssetsItem.getQuantity().add(digitalAssetsPutShelvesDTO.getPutOnQuantity()));
    //      digitalAssetsItemMapper.updateById(digitalAssetsItem);
    //      return digitalAssetsItem;
    //    } else
    {
      digitalAssetsItem = new DigitalAssetsItem();
      BeanUtil.copyProperties(digitalAssetsPutShelvesDTO, digitalAssetsItem);
      // 更新库存
      digitalAssetsItem.setInventory(digitalAssetsPutShelvesDTO.getPutOnQuantity());
      // 更新数量
      digitalAssetsItem.setQuantity(digitalAssetsPutShelvesDTO.getPutOnQuantity());
    }
    return digitalAssetsItem;
  }

  /** ERC6551 */
  private DigitalAssetsItem ERC6551PutOnShelves(
      DigitalAssetsPutShelvesDTO digitalAssetsPutShelvesDTO, DigitalAssetsItem digitalAssetsItem) {
    return digitalAssetsItem;
  }

  @ShenyuDubboClient("/pullOffShelves")
  @ApiDoc(desc = "pullOffShelves")
  @Override
  public Map<String, Object> pullOffShelves(Map<String, Object> requestMap) {

    return null;
  }

  @ShenyuDubboClient("/getDigitalAssetsMetadataImageInfo")
  @ApiDoc(desc = "getDigitalAssetsMetadataImageInfo")
  @Override
  public Map<String, Object> getDigitalAssetsMetadataImageInfo(Map<String, Object> requestMap) {
    String serialNo = (String) requestMap.get("serialNo");
    Integer tokenId = (Integer) requestMap.get("tokenId");
    String fileType = (String) requestMap.get("fileType");
    DigitalAssetsCollection digitalAssetsColletion =
        digitalAssetsCollectionMapper.selectById(serialNo);
    LambdaQueryWrapper<MetadataFile> warapper = new LambdaQueryWrapper<>();
    warapper.eq(MetadataFile::getParentNo, digitalAssetsColletion.getMetadataFilePathNo());
    warapper.eq(MetadataFile::getTokenId, tokenId);
    warapper.ne(ObjectUtil.isNotNull(fileType), MetadataFile::getFileType, fileType);
    MetadataFile metadataFile = metadataFileMapper.selectOne(warapper);
    if (metadataFile == null) {
      throw new BusinessException(ResponseCode.TOKEN_ID_METADATA_IMAGE_NOT_EXISTS);
    }
    return RespBodyHandler.setRespBodyDto(metadataFile);
  }

}
