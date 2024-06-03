package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import me.flyray.bsin.server.utils.Pagination;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.enums.ChainType;
import me.flyray.bsin.blockchain.enums.ContractProtocolStandards;
import me.flyray.bsin.blockchain.service.BsinBlockChainEngineFactory;
import me.flyray.bsin.blockchain.utils.Java2ContractTypeParameter;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Contract;
import me.flyray.bsin.domain.entity.ContractProtocol;
import me.flyray.bsin.domain.entity.CustomerProfile;
import me.flyray.bsin.domain.entity.DigitalAssetsCollection;
import me.flyray.bsin.domain.enums.AssetsCollectionType;
import me.flyray.bsin.domain.enums.ProfileType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.CustomerProfileService;
import me.flyray.bsin.facade.service.CustomerService;
import me.flyray.bsin.infrastructure.mapper.ContractMapper;
import me.flyray.bsin.infrastructure.mapper.ContractProtocolMapper;
import me.flyray.bsin.infrastructure.mapper.CustomerProfileMapper;
import me.flyray.bsin.infrastructure.mapper.DigitalAssetsCollectionMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.infrastructure.biz.CustomerInfoBiz;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author leonard
 * @date 2023/11/14 16:46
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/customerProfile", timeout = 6000)
@ApiModule(value = "customerProfile")
@Service
public class CustomerProfileServiceImpl implements CustomerProfileService {

  @Value("${bsin.s11edao.ipfs.gateway}")
  private String ipfsGateway;

  @Autowired private CustomerProfileMapper customerProfileMapper;
  @Autowired private ContractMapper contractMapper;
  @Autowired private BsinBlockChainEngineFactory bsinBlockChainEngineFactory;
  @Autowired private CustomerInfoBiz customerInfoBiz;
  @Autowired private DigitalAssetsCollectionMapper digitalAssetsCollectionMapper;
  @Autowired private ContractProtocolMapper contractProtocolMapper;

//  @DubboReference(version = "${dubbo.provider.version}")
//  private CopilotService copilotService;

  @DubboReference(version = "${dubbo.provider.version}")
  private CustomerService customerService;

  @ShenyuDubboClient("/create")
  @ApiDoc(desc = "create")
  @Override
  @Transactional
  public Map<String, Object> create(Map<String, Object> requestMap) throws Exception {
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
    CustomerProfile customerProfile =
        BsinServiceContext.getReqBodyDto(CustomerProfile.class, requestMap);
    customerProfile.setTenantId(tenantId);
    customerProfile.setMerchantNo(merchantNo);

    if (!customerProfile.getType().equals(ProfileType.BRAND.getDesc())
        && !customerProfile.getType().equals(ProfileType.INDIVIDUAL.getDesc())) {
      throw new BusinessException("100000", "Invalid profile type");
    }

    // 1.找到 S11eCore 合约
    LambdaUpdateWrapper<Contract> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(Contract::getVersion);
    warapper.last("limit 1");
    warapper.eq(Contract::getTenantId, tenantId);
    //    warapper.eq(Contract::getMerchantNo, merchantNo);  //一个租户公用一个S11eCore
    warapper.eq(Contract::getName, "S11eCore");
    warapper.eq(
        ObjectUtil.isNotNull(customerProfile.getChainEnv()),
        Contract::getChainEnv,
        customerProfile.getChainEnv());
    warapper.eq(
        ObjectUtil.isNotNull(customerProfile.getChainType()),
        Contract::getChainType,
        customerProfile.getChainType());
    Contract contractSelectOne = contractMapper.selectOne(warapper);
    if (contractSelectOne == null) {
      throw new BusinessException("100000", "need deploy S11eCore first");
    }
    String s11eCore = contractSelectOne.getContract();

    // 2.获取商户的客户信息
    Map merchantCustomerBase =
        customerInfoBiz.getMerchantCustomerBase(merchantNo, customerProfile.getChainType());
    String privateKey = ((String) merchantCustomerBase.get("privateKey"));
    String owner = ((String) merchantCustomerBase.get("walletAddress"));
    try {

      BsinBlockChainEngine bsinBlockChainEngine =
          bsinBlockChainEngineFactory.getBsinBlockChainEngineInstance(
              customerProfile.getChainType());
      // 结构体赋值
      //      struct ProfileStruct {
      //        string profileType;
      //        string name;
      //        string symbol;
      //        uint256 memberNo;
      //        address owner;
      //        string baseURI;
      //        address erc6551Registry;
      //        string externalUri;
      //      }
      Java2ContractTypeParameter functionParms =
          new Java2ContractTypeParameter.Builder()
              .addValue("string", List.of(customerProfile.getType()))
              .addParameter()
              .addValue("string", List.of(customerProfile.getName()))
              .addParameter()
              .addValue("string", List.of(customerProfile.getSymbol()))
              .addParameter()
              .addValue("uint256", List.of("0"))
              .addParameter()
              .addValue("address", List.of(owner)) // 从core会覆盖
              .addParameter()
              .addValue("string", List.of(ipfsGateway)) // 从core会覆盖
              .addParameter()
              .addValue(
                  "address",
                  List.of(
                      bsinBlockChainEngine.getZeroAddress(
                          customerProfile.getChainEnv()))) // 从core中获取
              .addParameter()
              .addValue(
                  "string",
                  List.of(
                      customerProfile.getExternalUri() == null
                          ? ""
                          : customerProfile.getExternalUri()))
              .addParameter()
              .build();

      // 2.调用 S11eCore 合约的 createProfile 方法
      Map<String, Object> result = new HashMap<>();
      String txHash = null;
      String txStatus = null;
      boolean isStatusOK = false;
      Map<String, Object> contractRWRet = new HashMap<>();

      contractRWRet =
          bsinBlockChainEngine.contractWrite(
              customerProfile.getChainEnv(),
              privateKey,
              null,
              null,
              null,
              s11eCore,
              "createProfile",
              "createProfile((string,string,string,uint256,address,string,address,string))",
              null, // functionReturnType,
              functionParms,
              60000);
      txHash = (String) contractRWRet.get("txHash");
      // 读取profile地址
      // 先读取当前profileCount
      Java2ContractTypeParameter functionReturnType =
          new Java2ContractTypeParameter.Builder().addValue("uint256", null).addParameter().build();
      contractRWRet =
          bsinBlockChainEngine.contractWrite(
              customerProfile.getChainEnv(),
              privateKey,
              null,
              null,
              null,
              s11eCore,
              "profileCount",
              functionReturnType,
              null,
              60000);
      String profileCount =
          (String) contractRWRet.get(functionReturnType.getParameterList().get(0).getType());
      BigInteger profileCountInt = new BigInteger(profileCount).subtract(new BigInteger("1"));

      System.out.println("profileCount: \n\n\n" + profileCount);
      System.out.println("profileCountInt-1: \n\n\n" + profileCountInt.toString());

      // 再根据当前 profileCount-1 回读地址
      functionReturnType =
          new Java2ContractTypeParameter.Builder().addValue("address", null).addParameter().build();
      functionParms =
          new Java2ContractTypeParameter.Builder()
              .addValue("uint256", List.of(profileCountInt.toString()))
              .addParameter()
              .build();
      contractRWRet =
          bsinBlockChainEngine.contractWrite(
              customerProfile.getChainEnv(),
              privateKey,
              null,
              null,
              null,
              s11eCore,
              "profileAddresses",
              functionReturnType,
              functionParms,
              60000);
      String profileAddress =
          (String) contractRWRet.get(functionReturnType.getParameterList().get(0).getType());
      System.out.println("profileAddress: \n\n\n" + profileAddress);

      Contract contract = new Contract();
      BeanUtil.copyProperties(customerProfile, contract);
      contract.setContract(profileAddress);
      contract.setVersion(contractSelectOne.getVersion());
      contract.setCreateBy(customerNo);
      contract.setContractProtocolNo(contractSelectOne.getContractProtocolNo());
      contract.setTxHash(txHash);
      contractMapper.insert(contract);

      Map<String, Object> setReq = new HashMap<String, Object>();

      // 更新customerBase的Profile地址
      if (ChainType.CONFLUX.getCode().equals(customerProfile.getChainType())) {
        setReq.put("profileAddress", profileAddress);
      } else {
        setReq.put("evmProfileAddress", profileAddress);
      }
      setReq.put("customerNo", customerNo);
      setReq.put("merchantNo", merchantNo);
      customerService.settingProfile(setReq);

      customerProfile.setSerialNo(BsinSnowflake.getId());
      customerProfile.setContractAddress(profileAddress);
      customerProfile.setMerchantNo(merchantNo);
      customerProfile.setCustomerNo(customerNo);
      customerProfile.setMemberNo(new BigInteger("0"));
      customerProfile.setCreateBy(customerNo);
      customerProfile.setProfileNum(profileCountInt);
      customerProfileMapper.insert(customerProfile);

      // 个人Profile创建的时候检查该用户是否拥有数字分身
      if (customerProfile.getType().equals(ProfileType.INDIVIDUAL.getDesc())) {
        requestMap.put("type", "2");
      } else if (customerProfile.getType().equals(ProfileType.BRAND.getDesc())) {
        requestMap.put("type", "1");
      }
      Map<String, Object> copilotInfoResp = null;
          // copilotService.createDigitalAvatarOrBrandOfficer(requestMap);
      // 4. 商户PassCard的TBA账户地址
      if (!"".equals(copilotInfoResp.get("data"))) {
        Map<String, Object> copilotInfo = (Map<String, Object>) copilotInfoResp.get("data");
        setReq.put("copilotNo", (String) copilotInfo.get("serialNo"));
        customerService.settingProfile(setReq);
      }
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
    return RespBodyHandler.setRespBodyDto(customerProfile);
  }

  @ShenyuDubboClient("/update")
  @ApiDoc(desc = "update")
  @Override
  public Map<String, Object> update(Map<String, Object> requestMap) throws Exception {
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

    String serialId = (String) requestMap.get("serialId");
    if (serialId == null) {
      serialId = (String) requestMap.get("customerProfileNo");
    }
    // 1.找到 customer 的 profile 合约
    // get customer S11eProfile
    CustomerProfile customerProfile = customerProfileMapper.selectById(serialId);
    if (customerProfile == null) {
      throw new BusinessException("100000", "未找到客户的profile合约！！！");
    }
    String customerProfileContractAddress = customerProfile.getContractAddress();

    // 2.获取商户的客户信息
    Map merchantCustomerBase =
        customerInfoBiz.getMerchantCustomerBase(merchantNo, customerProfile.getChainType());
    String privateKey = ((String) merchantCustomerBase.get("privateKey"));
    try {
      BsinBlockChainEngine bsinBlockChainEngine =
          bsinBlockChainEngineFactory.getBsinBlockChainEngineInstance(
              customerProfile.getChainType());
      String name = (String) requestMap.get("name");
      if (StringUtils.isNotEmpty(name)) {
        customerProfile.setName(name);
      } else {
        name = "";
      }
      String symbol = (String) requestMap.get("symbol");
      if (StringUtils.isNotEmpty(symbol)) {
        customerProfile.setSymbol(symbol);
      } else {
        symbol = "";
      }
      String externalUri = (String) requestMap.get("externalUri");
      if (StringUtils.isNotEmpty(externalUri)) {
        customerProfile.setExternalUri(externalUri);
      } else {
        externalUri = "";
      }
      String type = (String) requestMap.get("type");
      if (StringUtils.isNotEmpty(type)) {
        customerProfile.setType(type);
      } else {
        type = "";
      }

      String owner = (String) requestMap.get("owner");
      if (StringUtils.isNotEmpty(owner)) {
        customerProfile.setType(owner);
      } else {
        owner = bsinBlockChainEngine.getZeroAddress(customerProfile.getChainEnv());
      }

      String baseURI = (String) requestMap.get("baseURI");
      if (StringUtils.isNotEmpty(baseURI)) {
        customerProfile.setType(baseURI);
      } else {
        baseURI = bsinBlockChainEngine.getZeroAddress(customerProfile.getChainEnv());
      }

      String erc6551Registry = (String) requestMap.get("erc6551Registry");
      if (StringUtils.isNotEmpty(erc6551Registry)) {
        customerProfile.setType(erc6551Registry);
      } else {
        erc6551Registry = "";
      }

      // 结构体赋值
      //      struct ProfileStruct {
      //        string profileType;
      //        string name;
      //        string symbol;
      //        uint256 memberNo;
      //        address owner;
      //        string baseURI;
      //        address erc6551Registry;
      //        string externalUri;
      //      }
      Java2ContractTypeParameter functionParms =
          new Java2ContractTypeParameter.Builder()
              .addValue("string", List.of(type))
              .addParameter()
              .addValue("string", List.of(name))
              .addParameter()
              .addValue("string", List.of(symbol))
              .addParameter()
              .addValue("uint256", List.of("0"))
              .addParameter()
              .addValue("address", List.of(owner))
              .addParameter()
              .addValue("string", List.of(baseURI))
              .addParameter()
              .addValue("address", List.of(erc6551Registry))
              .addParameter()
              .addValue("string", List.of(externalUri))
              .addParameter()
              .build();

      // 2.调用 S11eProfile 合约的 createProfile 方法
      Map<String, Object> result = new HashMap<>();
      String txHash = null;
      String txStatus = null;
      boolean isStatusOK = false;
      Map<String, Object> contractRWRet = new HashMap<>();
      contractRWRet =
          bsinBlockChainEngine.contractWrite(
              customerProfile.getChainEnv(),
              privateKey,
              null,
              null,
              null,
              customerProfileContractAddress,
              "updateProfile",
              "updateProfile((string,string,string,uint256,address,string,address,string))",
              null,
              functionParms,
              60000);
      txHash = (String) contractRWRet.get("txHash");
      System.out.println("txHash: \n\n\n" + txHash);
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
    customerProfileMapper.updateById(customerProfile);
    return RespBodyHandler.setRespBodyDto(customerProfile);
  }

  @ShenyuDubboClient("/collect")
  @ApiDoc(desc = "collect")
  @Override
  public Map<String, Object> collect(Map<String, Object> requestMap) throws Exception {
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

    String serialId = (String) requestMap.get("serialId");
    if (serialId == null) {
      serialId = (String) requestMap.get("customerProfileNo");
    }

    // 1.找到 customer 的 profile 合约
    // get customer S11eProfile
    CustomerProfile customerProfile = customerProfileMapper.selectById(serialId);
    if (customerProfile == null) {
      throw new BusinessException("100000", "未找到客户的profile合约！！！");
    }
    String customerProfileContractAddress = customerProfile.getContractAddress();

    // 1.找到 PassCard  合约协议
    LambdaUpdateWrapper<ContractProtocol> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(ContractProtocol::getCreateTime);
    warapper.last("limit 1");
    warapper.eq(ContractProtocol::getTenantId, tenantId);
    warapper.eq(ContractProtocol::getProtocolName, "PassCard");
    warapper.eq(ContractProtocol::getChainType, customerProfile.getChainType());
    ContractProtocol contractProtocolSelectOne = contractProtocolMapper.selectOne(warapper);
    if (contractProtocolSelectOne == null) {
      throw new BusinessException("100000", "need upload PassCard contract protocol first");
    }
    String passCardProtocolNo = contractProtocolSelectOne.getSerialNo();
    System.out.println("passCardProtocolNo:" + passCardProtocolNo);

    // 2.获取商户的客户信息
    Map merchantCustomerBase =
        customerInfoBiz.getMerchantCustomerBase(merchantNo, customerProfile.getChainType());
    String privateKey = ((String) merchantCustomerBase.get("privateKey"));
    String walletAddress = ((String) merchantCustomerBase.get("walletAddress"));

    DigitalAssetsCollection digitalAssetsColletion =
        BsinServiceContext.getReqBodyDto(DigitalAssetsCollection.class, requestMap);
    digitalAssetsColletion.setTenantId(tenantId);
    digitalAssetsColletion.setMerchantNo(merchantNo);
    digitalAssetsColletion.setCreateBy(customerNo);

    String name = (String) requestMap.get("name");
    if (StringUtils.isNotEmpty(name)) {
      customerProfile.setName(name);
    } else {
      name = "";
    }
    String symbol = (String) requestMap.get("symbol");
    if (StringUtils.isNotEmpty(symbol)) {
      customerProfile.setSymbol(symbol);
    } else {
      symbol = "";
    }

    String supply = (String) requestMap.get("totalSupply");
    if (StringUtils.isNotEmpty(supply)) {
      customerProfile.setSymbol(symbol);
    } else {
      supply = "";
    }
    String externalUri = (String) requestMap.get("externalUri");
    if (StringUtils.isNotEmpty(externalUri)) {
      customerProfile.setExternalUri(externalUri);
    } else {
      externalUri = "";
    }

    //    struct AssetsStruct {
    //      string protocol;
    //      uint8 assetsType;
    //      address contractAddress;
    //      string name;
    //      string symbol;
    //      uint256 supply;
    //      string externalUri;
    //    }
    try {
      BsinBlockChainEngine bsinBlockChainEngine =
          bsinBlockChainEngineFactory.getBsinBlockChainEngineInstance(
              customerProfile.getChainType());

      Java2ContractTypeParameter functionParms =
          new Java2ContractTypeParameter.Builder()
              .addValue("string", List.of(ContractProtocolStandards.ERC721.getCode()))
              .addParameter()
              .addValue("uint8", List.of(AssetsCollectionType.PASS_CARD.getCode()))
              .addParameter()
              .addValue(
                  "address",
                  List.of(
                      bsinBlockChainEngine.getZeroAddress(customerProfile.getChainEnv()))) // 地址不能为空
              .addParameter()
              .addValue("string", List.of(name))
              .addParameter()
              .addValue("string", List.of(symbol))
              .addParameter()
              .addValue("uint256", List.of(supply))
              .addParameter()
              .addValue("string", List.of(externalUri))
              .addParameter()
              .build();

      // 2.调用 S11eProfile 合约的 collect 方法
      Map<String, Object> result = new HashMap<>();
      String txHash = null;
      String txStatus = null;
      boolean isStatusOK = false;
      Map<String, Object> contractRWRet = new HashMap<>();
      contractRWRet =
          bsinBlockChainEngine.contractWrite(
              customerProfile.getChainEnv(),
              privateKey,
              null,
              null,
              null,
              customerProfileContractAddress,
              "collect",
              "collect((string,uint8,address,string,string,uint256,string))",
              null,
              functionParms,
              60000);
      txHash = (String) contractRWRet.get("txHash");
      System.out.println("txHash: \n\n\n" + txHash);

      // 读取assets地址
      // 先读取当前assetsCount
      Java2ContractTypeParameter functionReturnType =
          new Java2ContractTypeParameter.Builder().addValue("uint256", null).addParameter().build();
      contractRWRet =
          bsinBlockChainEngine.contractWrite(
              customerProfile.getChainEnv(),
              privateKey,
              null,
              null,
              null,
              customerProfileContractAddress,
              "assetsCount",
              functionReturnType,
              null,
              60000);
      String assetsCount =
          (String) contractRWRet.get(functionReturnType.getParameterList().get(0).getType());

      customerProfile.setAssetsCount(new BigInteger(assetsCount));
      customerProfileMapper.updateById(customerProfile);
      BigInteger assetsCountInt = customerProfile.getAssetsCount().subtract(new BigInteger("1"));

      System.out.println("assetsCount: \n\n\n" + assetsCount);
      System.out.println("assetsCountInt-1: \n\n\n" + assetsCountInt.toString());

      // 再根据当前 assetsCount-1 回读地址
      functionReturnType =
          new Java2ContractTypeParameter.Builder().addValue("address", null).addParameter().build();
      functionParms =
          new Java2ContractTypeParameter.Builder()
              .addValue("uint256", List.of(assetsCountInt.toString()))
              .addParameter()
              .build();
      contractRWRet =
          bsinBlockChainEngine.contractWrite(
              customerProfile.getChainEnv(),
              privateKey,
              null,
              null,
              null,
              customerProfileContractAddress,
              "assetsAddress",
              functionReturnType,
              functionParms,
              60000);
      String assetsAddress =
          (String) contractRWRet.get(functionReturnType.getParameterList().get(0).getType());
      System.out.println("assetsAddress: \n\n\n" + assetsAddress);
      digitalAssetsColletion.setContractAddress(assetsAddress);

    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
    digitalAssetsColletion.setCollectionType(AssetsCollectionType.PASS_CARD.getCode());
    // 关联合约模板
    digitalAssetsColletion.setContractProtocolNo(passCardProtocolNo);
    digitalAssetsColletion.setInventory(digitalAssetsColletion.getTotalSupply());
    digitalAssetsCollectionMapper.insert(digitalAssetsColletion);

    return RespBodyHandler.setRespBodyDto(digitalAssetsColletion);
  }

  @ShenyuDubboClient("/follow")
  @ApiDoc(desc = "follow")
  @Override
  public Map<String, Object> follow(Map<String, Object> requestMap) throws Exception {
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
    String serialId = (String) requestMap.get("serialId");
    if (serialId == null) {
      serialId = (String) requestMap.get("customerProfileNo");
    }
    Map<String, Object> contractRWRet = new HashMap<>();
    // 1.找到 customer 的 profile 合约
    // get customer S11eProfile
    CustomerProfile customerProfile = customerProfileMapper.selectById(serialId);
    if (customerProfile == null) {
      contractRWRet.put("code", "100000");
      contractRWRet.put("message", "未找到客户的profile合约！！！");
      return contractRWRet;
      //      throw new BusinessException("100000", "未找到客户的profile合约！！！");

    }
    String customerProfileContractAddress = customerProfile.getContractAddress();

    // 2.获取客户信息
    Map customerBase = customerInfoBiz.getCustomerBase(customerNo, customerProfile.getChainType());
    String customerBaseAddress = ((String) customerBase.get("walletAddress"));

    // 3.获取商户的客户信息
    Map merchantCustomerBase =
        customerInfoBiz.getMerchantCustomerBase(merchantNo, customerProfile.getChainType());
    String privateKey = ((String) merchantCustomerBase.get("privateKey"));

    try {
      BsinBlockChainEngine bsinBlockChainEngine =
          bsinBlockChainEngineFactory.getBsinBlockChainEngineInstance(
              customerProfile.getChainType());

      Java2ContractTypeParameter functionParms =
          new Java2ContractTypeParameter.Builder()
              .addValue("address", List.of(customerBaseAddress))
              .addParameter()
              .build();

      // 2.调用 S11eProfile 合约的 follow 方法
      Map<String, Object> result = new HashMap<>();
      String txHash = null;
      String txStatus = null;
      boolean isStatusOK = false;
      contractRWRet =
          bsinBlockChainEngine.contractWrite(
              customerProfile.getChainEnv(),
              privateKey,
              null,
              null,
              null,
              customerProfileContractAddress,
              "follow",
              null,
              functionParms,
              60000);
      txHash = (String) contractRWRet.get("txHash");
      System.out.println("txHash: \n\n\n" + txHash);
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
    return RespBodyHandler.setRespBodyDto(contractRWRet);
  }

  @ShenyuDubboClient("/burn")
  @ApiDoc(desc = "burn")
  @Override
  public Map<String, Object> burn(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    customerProfileMapper.deleteById(serialNo);
    return RespBodyHandler.RespBodyDto();
  }

  @ShenyuDubboClient("/edit")
  @ApiDoc(desc = "edit")
  @Override
  public Map<String, Object> edit(Map<String, Object> requestMap) {
    CustomerProfile customerProfile =
        BsinServiceContext.getReqBodyDto(CustomerProfile.class, requestMap);
    customerProfileMapper.updateById(customerProfile);
    return RespBodyHandler.RespBodyDto();
  }

  @ShenyuDubboClient("/getDetail")
  @ApiDoc(desc = "getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    CustomerProfile customerProfile = customerProfileMapper.selectById(serialNo);
    return RespBodyHandler.setRespBodyDto(customerProfile);
  }

  @ShenyuDubboClient("/getPageList")
  @ApiDoc(desc = "getPageList")
  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    CustomerProfile customerProfile =
        BsinServiceContext.getReqBodyDto(CustomerProfile.class, requestMap);
    Object paginationObj =  requestMap.get("pagination");
    me.flyray.bsin.server.utils.Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj,pagination);
    Page<CustomerProfile> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<CustomerProfile> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(CustomerProfile::getCreateTime);
    warapper.eq(CustomerProfile::getTenantId, loginUser.getTenantId());
    warapper.eq(CustomerProfile::getMerchantNo, loginUser.getMerchantNo());
    warapper.eq(
            StringUtils.isNotBlank(customerProfile.getType()),
        CustomerProfile::getType,
        customerProfile.getType());
    IPage<CustomerProfile> pageList = customerProfileMapper.selectPage(page, warapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

}
