package me.flyray.bsin.infrastructure.biz;

import static me.flyray.bsin.constants.ResponseCode.GENERATE_MATADATA_FILE_FAILED;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.dto.ContractTransactionResp;
import me.flyray.bsin.blockchain.enums.ContractProtocolStandards;
import me.flyray.bsin.blockchain.service.BsinBlockChainEngineFactory;
import me.flyray.bsin.blockchain.utils.Java2ContractTypeParameter;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.entity.Contract;
import me.flyray.bsin.domain.entity.ContractProtocol;
import me.flyray.bsin.domain.entity.DigitalAssetsCollection;
import me.flyray.bsin.domain.entity.MetadataFile;
import me.flyray.bsin.domain.entity.MetadataTemplate;
import me.flyray.bsin.domain.entity.MintJournal;
import me.flyray.bsin.domain.entity.TransferJournal;
import me.flyray.bsin.domain.enums.AssetsCollectionType;
import me.flyray.bsin.enums.FileType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.request.DigitalAssetsIssueReqDTO;
import me.flyray.bsin.infrastructure.mapper.ContractMapper;
import me.flyray.bsin.infrastructure.mapper.MetadataFileMapper;
import me.flyray.bsin.infrastructure.mapper.MetadataTemplateMapper;
import me.flyray.bsin.oss.ipfs.BsinIpfsService;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author bolei
 * @date 2023/6/29 18:55
 * @desc
 */
@Slf4j
@Component
public class DigitalAssetsBiz {

  @Autowired private BsinBlockChainEngineFactory bsinBlockChainEngineFactory;
  @Autowired private ContractMapper contractMapper;
  @Autowired private MetadataFileMapper metadataFileMapper;
  @Autowired private MetadataTemplateMapper metadataTemplateMapper;
  @Autowired private BsinIpfsService bsinIpfsService;
  @Autowired private CustomerInfoBiz customerInfoBiz;

  @Value("${bsin.jiujiu.aesKey}")
  private String aesKey;

  @Value("${bsin.jiujiu.tenantAppType}")
  private String tenantAppType;

  public ContractTransactionResp deployContract(DigitalAssetsIssueReqDTO digitalAssetsIssueReqDTO) {

    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = digitalAssetsIssueReqDTO.getMerchantNo();
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = loginUser.getCustomerNo();
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }

    String tenantId = digitalAssetsIssueReqDTO.getTenantId();
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
      if (tenantId == null) {
        throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
      }
    }
    log.debug(
        "DigitalAssetsBiz contractDeploy 请求参数:{}", JSON.toJSONString(digitalAssetsIssueReqDTO));
    String chainType = digitalAssetsIssueReqDTO.getChainType();
    BsinBlockChainEngine bsinBlockChainEngine =
        bsinBlockChainEngineFactory.getBsinBlockChainEngineInstance(chainType);

    String contractProtocolNo = digitalAssetsIssueReqDTO.getContractProtocolNo();
    String chainEnv = digitalAssetsIssueReqDTO.getChainEnv();
    String privateKey = digitalAssetsIssueReqDTO.getPrivateKey();
    String owner = digitalAssetsIssueReqDTO.getOwnerAddress();
    String name = digitalAssetsIssueReqDTO.getName();
    String createBy = digitalAssetsIssueReqDTO.getCreateBy();
    String symbol = digitalAssetsIssueReqDTO.getSymbol();
    String cap = digitalAssetsIssueReqDTO.getCap();
    String totalSupply = String.valueOf(digitalAssetsIssueReqDTO.getTotalSupply());
    String initialSupply = String.valueOf(digitalAssetsIssueReqDTO.getInitialSupply());
    String baseURI = digitalAssetsIssueReqDTO.getBaseURI();
    String protocolType = digitalAssetsIssueReqDTO.getProtocolType();
    String protocolStandards = digitalAssetsIssueReqDTO.getProtocolStandards();
    String protocolCode = digitalAssetsIssueReqDTO.getProtocolCode();
    String byteCode = digitalAssetsIssueReqDTO.getProtocolBytecode();

    ContractTransactionResp contractTransactionResp = new ContractTransactionResp();
    String contractAddress = null;

    String txHash = null;
    String txStatus = null;
    boolean isStatusOK = false;

    Map<String, Object> contractDeployRet = new HashMap<>();
    Map<String, Object> contractWriteRet = new HashMap<>();
    try {
      if (ContractProtocolStandards.ERC20.getCode().equals(protocolStandards)) {
        Java2ContractTypeParameter constructParms;
        /*
        string memory _name,
        string memory _symbol,
        uint256 _initailSupply,
        address _owner
        */
        constructParms =
            new Java2ContractTypeParameter.Builder()
                .addValue("string", List.of(name))
                .addParameter() // 0、string name
                .addValue("string", List.of(symbol))
                .addParameter() // 1、string symbol
                .addValue("uint256", List.of(initialSupply))
                .addParameter() // 2、uint256 initialSupply
                .addValue("address", List.of(owner))
                .addParameter() // 3、address _owner
                .build();

        contractDeployRet =
            bsinBlockChainEngine.contractDeploy(
                chainEnv, privateKey, null, null, null, byteCode, null, constructParms, 60000);
        contractAddress = (String) contractDeployRet.get("deployedAddress");
      } else if (ContractProtocolStandards.ERC721.getCode().equals(protocolStandards)
          || ContractProtocolStandards.ERC1155.getCode().equals(protocolStandards)) {
        // PAAS CARD
        Java2ContractTypeParameter constructParms;
        if (AssetsCollectionType.PASS_CARD.getCode().equals(protocolType)) {
          // get Erc6551Registry
          LambdaUpdateWrapper<Contract> warapper = new LambdaUpdateWrapper<>();
          warapper.orderByDesc(Contract::getCreateTime);
          warapper.eq(Contract::getTenantId, tenantId);
//          warapper.eq(Contract::getMerchantNo, merchantNo);
          warapper.eq(Contract::getName, "ERC6551Registry");
          warapper.eq(Contract::getChainEnv, chainEnv);
          warapper.eq(Contract::getChainType, chainType);
          Contract contractSelectOne = contractMapper.selectOne(warapper);
          if (contractSelectOne == null) {
            throw new BusinessException("100000", "need deploy ERC6551Registry first");
          }
          String erc6551Registry = contractSelectOne.getContract();

          constructParms =
              new Java2ContractTypeParameter.Builder()
                  .addValue("string", List.of(name))
                  .addParameter() // 0、string name
                  .addValue("string", List.of(symbol))
                  .addParameter() // 1、string symbol
                  .addValue("string", List.of(baseURI))
                  .addParameter() // 2、string baseURI
                  .addValue("uint256", List.of(totalSupply))
                  .addParameter() // 3、uint256 supply
                  .addValue("address", List.of(erc6551Registry))
                  .addParameter() // 4、address _erc6551Registry
                  .addValue("address", List.of(owner))
                  .addParameter() // 5、address _owner
                  .build();
        } else {
          constructParms =
              new Java2ContractTypeParameter.Builder()
                  .addValue("string", List.of(name))
                  .addParameter() // 0、string _name
                  .addValue("string", List.of(symbol))
                  .addParameter() // 1、string _symbol
                  .addValue("string", List.of(baseURI))
                  .addParameter() // 2、string _baseURI
                  .addValue("uint256", List.of(totalSupply))
                  .addParameter() // 3、uint256 _supply
                  .addValue("address", List.of(owner))
                  .addParameter() // 4、address _owner
                  .build();
        }
        contractDeployRet =
            bsinBlockChainEngine.contractDeploy(
                chainEnv, privateKey, null, null, null, byteCode, null, constructParms, 60000);
        contractAddress = (String) contractDeployRet.get("deployedAddress");
      } else if (ContractProtocolStandards.ERC3525TokenExtension.getCode()
          .equals(protocolStandards)) {
        throw new BusinessException(ResponseCode.NOT_FOUND_CONTRACT_BYTECODE);
      }
      txHash = (String) contractDeployRet.get("txHash");
      txStatus = (String) contractDeployRet.get("txStatus");
      isStatusOK = (boolean) contractDeployRet.get("isStatusOK");
      contractTransactionResp.setContractAddress(contractAddress);
      contractTransactionResp.setTxHash(txHash);
      contractTransactionResp.setTxStatus(txStatus);
      contractTransactionResp.setStatusOK(isStatusOK);
      //            if (!isStatusOK) {
      //                throw new BusinessException(ResponseCode.DEPLOY_CONTRACT_ERROR);
      //            }
    } catch (Exception e) {
      log.error(e.toString());
      throw new BusinessException("999999", e.toString());
    }
    // 讲部署的合约信息写入合约表
    String contractNo = BsinSnowflake.getId();
    Contract contractInstance = new Contract();
    contractInstance.setContractProtocolNo(contractProtocolNo);
    contractInstance.setContract(contractAddress);
    contractInstance.setSerialNo(contractNo);
    contractInstance.setTxHash(txHash);
    contractInstance.setCreateBy(createBy);
    contractInstance.setCreateTime(new Date());
    contractInstance.setChainEnv(chainEnv);
    contractInstance.setChainType(chainType);
    contractInstance.setTenantId(tenantId);
    contractInstance.setMerchantNo(merchantNo);
    contractInstance.setDescription(digitalAssetsIssueReqDTO.getDescription());

    contractMapper.insert(contractInstance);
    log.info("DigitalAssetsBiz contractDeploy 相应结果:{}", JSON.toJSONString(contractTransactionResp));
    return contractTransactionResp;
  }

  public ContractTransactionResp mint(
      String contract,
      String privateKey,
      boolean addPrivilege,
      MintJournal mintJournal,
      ContractProtocol contractProtocol) {

    log.debug("DigitalAssetsBiz mintNft 请求参数:{}", JSON.toJSONString(mintJournal));

    String chainType = contractProtocol.getChainType();
    String chainEnv = mintJournal.getChainEnv();
    String toAddress = mintJournal.getToAddress();

    String contractAddress = contract;
    BigInteger tokenIdInt = mintJournal.getTokenId();
    String tokenURI = mintJournal.getMetadataUrl();
    String amount = mintJournal.getAmount().stripTrailingZeros().toPlainString();
    String tokenId = String.valueOf(tokenIdInt);
    String protocolStandards = contractProtocol.getProtocolStandards();
    String addPrivilegeStr = "false";
    if (addPrivilege) {
      addPrivilegeStr = "true";
    }

    BsinBlockChainEngine bsinBlockChainEngine =
        bsinBlockChainEngineFactory.getBsinBlockChainEngineInstance(chainType);
    if (toAddress == null) {
      toAddress = bsinBlockChainEngine.getAddress(chainType, privateKey);
    }
    String txHash = null;
    String txStatus = null;
    boolean isStatusOK = false;

    Map<String, Object> contractWriteRet = new HashMap<>();
    ContractTransactionResp contractTransactionResp = new ContractTransactionResp();

    try {
      if (ContractProtocolStandards.ERC20.getCode().equals(protocolStandards)) {
        log.debug("ERC20铸造");
        Java2ContractTypeParameter functionParms =
            new Java2ContractTypeParameter.Builder()
                .addValue("address", List.of(toAddress))
                .addParameter() // 0、address toAddress
                .addValue("uint256", List.of(amount))
                .addParameter() // 0、address toAddress
                .build();
        contractWriteRet =
            bsinBlockChainEngine.contractWrite(
                chainEnv,
                privateKey,
                null,
                null,
                null,
                contractAddress,
                "mint",
                null,
                functionParms,
                60000);
      } else if (ContractProtocolStandards.ERC721.getCode().equals(protocolStandards)) {
        log.debug("ERC721 铸造");
        //                DynamicBytes b = new DynamicBytes("".getBytes());
        Java2ContractTypeParameter functionParms =
            new Java2ContractTypeParameter.Builder()
                .addValue("address", List.of(toAddress))
                .addParameter() // 0、address toAddress
                .addValue("uint256", List.of(tokenId))
                .addParameter() //  1、uint256 tokenId
                .addValue("string", List.of(tokenURI))
                .addParameter() // 2、string tokenURI
                .addValue("bool", List.of(addPrivilegeStr))
                .addParameter() // 3、bool addPrivilege
                //                .addValue("bytes", null).addParameter()     //  4、bytes data
                .build();
        contractWriteRet =
            bsinBlockChainEngine.contractWrite(
                chainEnv,
                privateKey,
                null,
                null,
                null,
                contractAddress,
                "mint",
                null,
                functionParms,
                60000);

      } else if (ContractProtocolStandards.ERC1155.getCode().equals(protocolStandards)) {
        // 特殊处理,只取ipfs的CID
        //                DynamicBytes b = new DynamicBytes("".getBytes());
        // 同质化铸造
        Java2ContractTypeParameter functionParms =
            new Java2ContractTypeParameter.Builder()
                .addValue("address", List.of(toAddress))
                .addParameter() // 0、address toAddress
                .addValue("uint256", List.of(tokenId))
                .addParameter() //  1、uint256 tokenId
                .addValue("uint256", List.of(amount.toString()))
                .addParameter() //  2、uint256 _amount
                .addValue("string", List.of(tokenURI))
                .addParameter() // 3、string tokenURI
                .addValue("bool", List.of(addPrivilegeStr))
                .addParameter() // 4、bool addPrivilege
                .addValue("bytes", null)
                .addParameter() //  5、bytes data
                .build();
        contractWriteRet =
            bsinBlockChainEngine.contractWrite(
                chainEnv,
                privateKey,
                null,
                null,
                null,
                contractAddress,
                "mint",
                null,
                functionParms,
                60000);

      } else {
        throw new BusinessException(ResponseCode.ILLEGAL_ASSETS_PROTOCOL);
      }
      //            if (!isStatusOK) {
      //                throw new BusinessException(ResponseCode.DEPLOY_CONTRACT_ERROR);
      //            }
    } catch (Exception e) {
      log.error(e.toString());
      throw new BusinessException("999999", e.toString());
      //      if (e.toString().contains("-32015")) {
      //        throw new BusinessException(ResponseCode.NOT_ENOUGH_GAS_FEE);
      //      } else {
      //        throw new BusinessException("999999", e.toString());
      //      }
    }
    mintJournal.setTxHash((String) contractWriteRet.get("txHash"));
    contractTransactionResp.setContractAddress((String) contractWriteRet.get("contractAddress"));
    contractTransactionResp.setStatusOK((boolean) contractWriteRet.get("isStatusOK"));
    contractTransactionResp.setTxHash((String) contractWriteRet.get("txHash"));
    contractTransactionResp.setTxStatus((String) contractWriteRet.get("txStatus"));
    return contractTransactionResp;
  }

  public ContractTransactionResp transfer(
      String contract,
      String privateKey,
      boolean addPrivilege,
      TransferJournal transferJournal,
      ContractProtocol contractProtocol) {

    log.debug("DigitalAssetsBiz transfer 请求参数:{}", JSON.toJSONString(transferJournal));
    String chainType = contractProtocol.getChainType();
    String chainEnv = transferJournal.getChainEnv();
    String fromAddress = transferJournal.getFromAddress();
    String toAddress = transferJournal.getToAddress();
    String protocolStandards = contractProtocol.getProtocolStandards();
    String contractAddress = contract;
    BigInteger tokenIdInt = transferJournal.getTokenId();
    BigInteger amount = transferJournal.getAmount();

    String tokenId = String.valueOf(tokenIdInt);

    BsinBlockChainEngine bsinBlockChainEngine =
        bsinBlockChainEngineFactory.getBsinBlockChainEngineInstance(chainType);
    Map<String, Object> result = new HashMap<>();
    String txHash = null;
    String txStatus = null;
    boolean isStatusOK = false;
    String addPrivilegeStr = "false";
    if (addPrivilege) {
      addPrivilegeStr = "true";
    }

    fromAddress = bsinBlockChainEngine.getAddress(chainEnv, privateKey);

    Map<String, Object> contractWriteRet = new HashMap<>();
    ContractTransactionResp contractTransactionResp = new ContractTransactionResp();
    try {
      if (ContractProtocolStandards.ERC721TokenExtension.getCode().equals(protocolStandards)
          || ContractProtocolStandards.ERC721.getCode().equals(protocolStandards)) {
        // 转账时添加白名单，前提是需要拥有owner权限，否则交易会回滚
        Java2ContractTypeParameter functionParms =
            new Java2ContractTypeParameter.Builder()
                .addValue("address", List.of(fromAddress))
                .addParameter() // 0、address fromAddress
                .addValue("address", List.of(toAddress))
                .addParameter() //  1、address toAddress
                .addValue("uint256", List.of(tokenId))
                .addParameter() //  2、uint256 tokenId
                .build();
        if (addPrivilege) {
          functionParms =
              new Java2ContractTypeParameter.Builder()
                  .addValue("address", List.of(fromAddress))
                  .addParameter() // 0、address fromAddress
                  .addValue("address", List.of(toAddress))
                  .addParameter() //  1、address toAddress
                  .addValue("uint256", List.of(tokenId))
                  .addParameter() //  2、uint256 tokenId
                  .addValue("bool", List.of(addPrivilegeStr))
                  .addParameter() // 3、bool addPrivilege
                  .build();
        }
        contractWriteRet =
            bsinBlockChainEngine.contractWrite(
                chainEnv,
                privateKey,
                null,
                null,
                null,
                contractAddress,
                "safeTransferFrom",
                null,
                functionParms,
                60000);
      } else if (ContractProtocolStandards.ERC1155TokenExtension.getCode().equals(protocolStandards)
          || ContractProtocolStandards.ERC1155.getCode().equals(protocolStandards)) {
        //        function safeTransferFrom(
        //                address from,
        //                address to,
        //                uint256 id,
        //                uint256 amount,
        //                bool _addPrivilege,
        //                bytes memory data
        //        )
        //                DynamicBytes b = new DynamicBytes("".getBytes());
        Java2ContractTypeParameter functionParms =
            new Java2ContractTypeParameter.Builder()
                .addValue("address", List.of(fromAddress))
                .addParameter() // 0、address fromAddress
                .addValue("address", List.of(toAddress))
                .addParameter() //  1、address toAddress
                .addValue("uint256", List.of(tokenId))
                .addParameter() //  2、uint256 tokenId
                .addValue("uint256", List.of(amount.toString()))
                .addParameter() //  3、uint256 amount
                .addValue("bytes", null)
                .addParameter() // 4、bool addPrivilege
                .build();

        if (addPrivilege) {
          functionParms =
              new Java2ContractTypeParameter.Builder()
                  .addValue("address", List.of(fromAddress))
                  .addParameter() // 0、address fromAddress
                  .addValue("address", List.of(toAddress))
                  .addParameter() //  1、address toAddress
                  .addValue("uint256", List.of(tokenId))
                  .addParameter() //  2、uint256 tokenId
                  .addValue("uint256", List.of(amount.toString()))
                  .addParameter() //  3、uint256 amount
                  .addValue("bool", List.of(addPrivilegeStr))
                  .addParameter() // 4、bool addPrivilege
                  .addValue("bytes", null)
                  .addParameter() // 5、bytes data
                  .build();
        }
        contractWriteRet =
            bsinBlockChainEngine.contractWrite(
                chainEnv,
                privateKey,
                null,
                null,
                null,
                contractAddress,
                "safeTransferFrom",
                null,
                functionParms,
                60000);
      }
      //            if (!isStatusOK) {
      //                throw new BusinessException(ResponseCode.DEPLOY_CONTRACT_ERROR);
      //            }
    } catch (Exception e) {
      log.error(e.toString());
      //      if (e.toString().contains("-32015")) {
      //        throw new BusinessException(ResponseCode.NOT_ENOUGH_GAS_FEE);
      //      } else {
      throw new BusinessException("999999", e.toString());
      //      }
    }
    contractTransactionResp.setContractAddress((String) contractWriteRet.get("contractAddress"));
    contractTransactionResp.setStatusOK((boolean) contractWriteRet.get("isStatusOK"));
    contractTransactionResp.setTxHash((String) contractWriteRet.get("txHash"));
    contractTransactionResp.setTxStatus((String) contractWriteRet.get("txStatus"));

    log.debug("DigitalAssetsBiz transfer 响应参数:{}", JSON.toJSONString(contractTransactionResp));
    return contractTransactionResp;
  }

  // 获取TBA address
  public String getTbaAddress(
      String contractAddress, BigInteger tokenId, String chainType, String chainEnv)
      throws Exception {
    Map<String, Object> contractReadRet = new HashMap<String, Object>();
    try {
      BsinBlockChainEngine bsinBlockChainEngine =
          bsinBlockChainEngineFactory.getBsinBlockChainEngineInstance(chainType);
      Java2ContractTypeParameter functionParms =
          new Java2ContractTypeParameter.Builder()
              .addValue("uint256", List.of(tokenId.toString()))
              .addParameter()
              .build();
      Java2ContractTypeParameter functionReturnType =
          new Java2ContractTypeParameter.Builder().addValue("address", null).addParameter().build();

      contractReadRet =
          bsinBlockChainEngine.contractRead(
              chainEnv, contractAddress, "tbaAccount", functionReturnType, functionParms, 60000);
      return (String) contractReadRet.get(functionReturnType.getParameterList().get(0).getType());
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
  }

  private String getIpfsCid(String tokenURI) {
    String tmpStr = tokenURI.substring(0, tokenURI.indexOf("/ipfs/"));
    log.info(tmpStr);
    String metadataCID = tokenURI.substring(tmpStr.length() + 6, tokenURI.length());
    log.info(metadataCID);
    return metadataCID;
  }

  public MintJournal generateMintjournal(
      DigitalAssetsCollection digitalAssetsColletion,
      BigInteger tokenId,
      String assetsName,
      String description,
      String attributes) {

    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String teantId = loginUser.getTenantId();
    // 发行的商户
    String merchantNo = loginUser.getMerchantNo();
    String customerNo = loginUser.getCustomerNo();

    // 判断tokenId对应的多媒体资源是否存在
    MetadataFile metadataFile = null;
    LambdaUpdateWrapper<MetadataFile> metadataFileWarapper = new LambdaUpdateWrapper<>();
    metadataFileWarapper.eq(
        MetadataFile::getParentNo, digitalAssetsColletion.getMetadataFilePathNo());
    if (digitalAssetsColletion.getMetadataImageSameFlag() == "1") {
      metadataFileWarapper.eq(MetadataFile::getTokenId, '1');
    } else {
      metadataFileWarapper.eq(MetadataFile::getTokenId, tokenId);
    }
    // 排除json文件
    metadataFileWarapper.ne(MetadataFile::getFileType, '5');
    metadataFile = metadataFileMapper.selectOne(metadataFileWarapper);
    if (metadataFile == null) {
      log.error("metadataFile:{}", metadataFile.toString());
      throw new BusinessException(ResponseCode.TOKEN_ID_METADATA_IMAGE_NOT_EXISTS);
    }
    String imageUrl = metadataFile.getIpfsUrl();

    // 1.获取客户信息
    Map customerBase =
        customerInfoBiz.getCustomerBase(customerNo, digitalAssetsColletion.getChainType());
    String phone = (String) customerBase.get("phone");
    String toAddress = (String) customerBase.get("walletAddress");
    String privateKey = (String) customerBase.get("privateKey");

    // 查找部署的合约
    digitalAssetsColletion.getContractAddress();
    // 获取metadataURI
    JSONObject metaDataURI =
        genarateMetaDataURI(
            digitalAssetsColletion.getMetadataTemplateNo(),
            digitalAssetsColletion.getMetadataFilePathNo(),
            metadataFile,
            tokenId,
            assetsName,
            description,
            attributes);
    MintJournal mintJournal = new MintJournal();
    mintJournal.setItemName(assetsName);
    mintJournal.setTokenId(tokenId);
    mintJournal.setAmount(new BigDecimal("1"));
    mintJournal.setChainEnv(digitalAssetsColletion.getChainEnv());
    mintJournal.setToAddress(toAddress);
    mintJournal.setMetadataUrl((String) metaDataURI.get("fileHash"));
    mintJournal.setMetadataImage(imageUrl);
    return mintJournal;
  }

  /** 1.根据metadata模板和image生成metadata 2.上传至文件服务器(local+ipfs+数据库)--租户的文件目录下 3.返回metadataURI */
  public JSONObject genarateMetaDataURI(
      String metadataTemplateNo,
      String metadataFilePathNo,
      MetadataFile metadataFile,
      BigInteger tokenId,
      String name,
      String description,
      String attributes) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    // 发行的商户
    String merchantNo = loginUser.getMerchantNo();
    // 发行的客户
    String customerNo = loginUser.getCustomerNo();
    //    // 客户类型
    //    String customerType = loginUser.getCustomerType();

    // 租户类型
    //    String tenantAppType = loginUser.getTenantAppType();

    JSONObject result = null;
    String metadataURI = null;

    //    // 1.判断tokenId对应的多媒体资源是否存在
    //    MetadataFile metadataFile = null;
    //    LambdaUpdateWrapper<MetadataFile> metadataFileWarapper = new LambdaUpdateWrapper<>();
    //    metadataFileWarapper.eq(MetadataFile::getParentNo, metadataFilePathNo);
    //    if (metadataImageSameFlag == "1") {
    //      metadataFileWarapper.eq(MetadataFile::getTokenId, '1');
    //    } else {
    //      metadataFileWarapper.eq(MetadataFile::getTokenId, tokenId);
    //    }
    //    metadataFile = metadataFileMapper.selectOne(metadataFileWarapper);
    //    if (metadataFile == null) {
    //      throw new BusinessException(ResponseCode.TOKEN_ID_METADATA_IMAGE_NOT_EXISTS);
    //    }
    //    // 2.如果主题的元数据编号存在就基于元数据固定铸造,json文件存在则直接铸造
    //    if (metadataFile.getFileType() == "6" && metadataFile.getFileUrl()!=null){
    //      String fileUrl = metadataFile.getFileUrl();
    //      result.put("fileUrl", fileUrl);
    //      result.put("fileHash", bsinIpfsService.getIpfsCid(fileUrl));
    //      return result;
    //    }

    MetadataTemplate metadataTemplate;
    if (StringUtils.isBlank(metadataTemplateNo)) {
      throw new BusinessException(ResponseCode.NOT_FOUND_NFT_METADATA_TEMPLATE);
    } else {
      metadataTemplate = metadataTemplateMapper.selectById(metadataTemplateNo);
    }

    if (metadataTemplate == null) {
      throw new BusinessException(ResponseCode.NOT_FOUND_NFT_METADATA_TEMPLATE);
    }
    JSONObject json =
        (JSONObject) com.alibaba.fastjson.JSON.parse(metadataTemplate.getTemplateContent());
    json.put("tokenId", tokenId);

    // 3. 根据 metadataFilePathNo 找到文件夹中对应的 tokenId 的图片作为 image
    if (metadataFile.getIpfsUrl() == null) {
      throw new BusinessException(ResponseCode.TOKEN_ID_METADATA_IMAGE_NOT_EXISTS);
    }
    // 1155 721自定义铸造的情况方便社区自定义属性和描述
    if (StringUtils.isNotBlank(description)) {
      json.put("description", description);
    }
    if (StringUtils.isNotBlank(name)) {
      json.put("name", name);
    }
    if (StringUtils.isNotBlank(attributes)) {
      JSONArray attributesArray = (JSONArray) JSONObject.parse(attributes);
      json.put("attributes", attributesArray);
    }
    json.put("image", metadataFile.getIpfsUrl());
    // 4、上传metadata信息至IPFS和服务器 --> 获得 metadataURI
    String fileName = tokenId.toString() + ".json";
    String content = JSON.toJSONString(json);

    MultipartFile multipartFile = null;
    // TODO: 上传文件至ipfs和服务器
    JSONObject uploadResponse;
    try {
      multipartFile = bsinIpfsService.string2multipartFile(content, fileName);
      uploadResponse =
          bsinIpfsService.ipfsAndServiceUpload(
              multipartFile, tenantId, metadataFilePathNo, "3", tenantAppType);
      metadataURI = (String) uploadResponse.get("fileUrl");
      uploadResponse.put("imageUrl", metadataFile.getIpfsUrl());
    } catch (Exception e) {
      throw new BusinessException(GENERATE_MATADATA_FILE_FAILED);
    }
    if (metadataURI == null) {
      throw new BusinessException(GENERATE_MATADATA_FILE_FAILED);
    }
    // 5、插入metadata信息至数据库
    metadataFile.setSerialNo(BsinSnowflake.getId());
    metadataFile.setTenantId(tenantId);
    metadataFile.setFileName("tokenId-" + fileName);
    metadataFile.setTokenId(tokenId);
    metadataFile.setFileType(FileType.JSON.getCode());
    metadataFile.setParentNo(metadataFilePathNo);
    metadataFile.setDirFlag("0");
    //    metadataFile.setImageUrl(metadataFile.getFileUrl());
    metadataFile.setIpfsUrl(metadataURI);
    metadataFile.setMetadataContent(content);
    metadataFile.setCreateBy(customerNo);
    metadataFileMapper.insert(metadataFile);
    return uploadResponse;
  }
}
