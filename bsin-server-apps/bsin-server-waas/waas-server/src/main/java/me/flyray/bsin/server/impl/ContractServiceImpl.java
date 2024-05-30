package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.service.BsinBlockChainEngineFactory;
import me.flyray.bsin.blockchain.utils.Java2ContractTypeParameter;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Contract;
import me.flyray.bsin.domain.entity.ContractProtocol;
import me.flyray.bsin.domain.enums.ProtocolName;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.ContractService;
import me.flyray.bsin.infrastructure.mapper.ContractMapper;
import me.flyray.bsin.infrastructure.mapper.ContractProtocolMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.infrastructure.biz.CustomerInfoBiz;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author leonard
 * @date 2023/10/13
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/contract", timeout = 6000)
@ApiModule(value = "contract")
@Service
public class ContractServiceImpl implements ContractService {

  @Value("${bsin.s11edao.ipfs.gateway}")
  private String ipfsGateway;

  @Autowired private ContractMapper contractMapper;
  @Autowired private ContractProtocolMapper contractProtocolMapper;
  @Autowired private BsinBlockChainEngineFactory bsinBlockChainEngineFactory;
  @Autowired private CustomerInfoBiz customerInfoBiz;

  @Override
  public Map<String, Object> deploy(Map<String, Object> requestMap) throws Exception {
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

    String chainEnv = MapUtils.getString(requestMap, "chainEnv");
    String contractProtocolNo = MapUtils.getString(requestMap, "contractProtocolNo");

    if (contractProtocolNo == null) {
      throw new BusinessException(ResponseCode.ID_NOT_ISNULL);
    }
    // 1.找到合约协议
    ContractProtocol contractProtocol = contractProtocolMapper.selectById(contractProtocolNo);
    if (contractProtocol == null) {
      throw new BusinessException(ResponseCode.NOT_FOUND_CONTRACT_BYTECODE);
    }
    String chainType = contractProtocol.getChainType();
    String protocolStandards = contractProtocol.getProtocolStandards();
    String protocolCode = contractProtocol.getProtocolCode();
    String byteCode = contractProtocol.getProtocolBytecode();

    // 2.获取商户的客户信息
    Map merchantCustomerBase = customerInfoBiz.getMerchantCustomerBase(merchantNo, chainType);
    String privateKey = ((String) merchantCustomerBase.get("privateKey"));

    Map<String, Object> result = new HashMap<>();
    String contractAddress = null;
    String txHash = null;
    String txStatus = null;
    boolean isStatusOK = false;
    Map<String, Object> contractDeployRet = new HashMap<>();
    Map<String, Object> contractWriteRet = new HashMap<>();
    Java2ContractTypeParameter constructParms;

    BsinBlockChainEngine bsinBlockChainEngine =
        bsinBlockChainEngineFactory.getBsinBlockChainEngineInstance(chainType);

    if (contractProtocol.getProtocolName().equals(ProtocolName.S11E_CORE.getCode())) {
      /*
      string memory _baseURI,
      address _erc6551Registry
      */
      // get Erc6551Registry
      LambdaUpdateWrapper<Contract> warapper = new LambdaUpdateWrapper<>();
      warapper.orderByDesc(Contract::getCreateTime);
      warapper.eq(Contract::getTenantId, tenantId);
//      warapper.eq(Contract::getMerchantNo, merchantNo);
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
              .addValue("string", List.of(ipfsGateway))
              .addParameter()
              .addValue("address", List.of(erc6551Registry))
              .addParameter()
              .build();
      contractDeployRet =
          bsinBlockChainEngine.contractDeploy(
              chainEnv, privateKey, null, null, null, byteCode, null, constructParms, 60000);
      contractAddress = (String) contractDeployRet.get("deployedAddress");
      txHash = (String) contractDeployRet.get("txHash");
    } else if (contractProtocol.getProtocolName().equals(ProtocolName.S11E_DAO.getCode())
        || contractProtocol.getProtocolName().equals(ProtocolName.ERC6551_ACCOUNT.getCode())) {
      contractDeployRet =
          bsinBlockChainEngine.contractDeploy(
              chainEnv, privateKey, null, null, null, byteCode, null, null, 60000);
      contractAddress = (String) contractDeployRet.get("deployedAddress");
      txHash = (String) contractDeployRet.get("txHash");
      // TODO: need initialize
      //      function initialize(
      //              address _creator,
      //              address _payer,
      //              string calldata _name
      //        )
    } else if (contractProtocol.getProtocolName().equals(ProtocolName.ERC6551_REGISTRY.getCode())) {
      /*
      address _implementation
      */
      // get ERC6551Account
      LambdaUpdateWrapper<Contract> warapper = new LambdaUpdateWrapper<>();
      warapper.orderByDesc(Contract::getCreateTime);
      warapper.eq(Contract::getTenantId, tenantId);
      warapper.eq(Contract::getMerchantNo, merchantNo);
      warapper.eq(Contract::getName, "ERC6551Account");
      warapper.eq(Contract::getChainEnv, chainEnv);
      warapper.eq(Contract::getChainType, chainType);
      Contract contractSelectOne = contractMapper.selectOne(warapper);
      if (contractSelectOne == null) {
        throw new BusinessException("100000", "need deploy ERC6551Account first");
      }
      String ERC6551Account = contractSelectOne.getContract();
      constructParms =
          new Java2ContractTypeParameter.Builder()
              .addValue("address", List.of(ERC6551Account))
              .addParameter()
              .build();
      contractDeployRet =
          bsinBlockChainEngine.contractDeploy(
              chainEnv, privateKey, null, null, null, byteCode, null, constructParms, 60000);
      contractAddress = (String) contractDeployRet.get("deployedAddress");
      txHash = (String) contractDeployRet.get("txHash");
    } else if (contractProtocol.getProtocolName().equals(ProtocolName.S11E_PROFILE.getCode())) {
      /*
        ProfileStruct memory _profileStruct,
        string memory _baseURI,
        address _erc6551Registry
      */

      // get Erc6551Registry
      LambdaUpdateWrapper<Contract> warapper = new LambdaUpdateWrapper<>();
      warapper.orderByDesc(Contract::getCreateTime);
      warapper.eq(Contract::getTenantId, tenantId);
//      warapper.eq(Contract::getMerchantNo, merchantNo);
      warapper.eq(Contract::getName, "ERC6551Registry");
      warapper.eq(Contract::getChainEnv, chainEnv);
      warapper.eq(Contract::getChainType, chainType);
      Contract contractSelectOne = contractMapper.selectOne(warapper);
      if (contractSelectOne == null) {
        throw new BusinessException("100000", "need deploy ERC6551Registry first");
      }
      String erc6551Registry = contractSelectOne.getContract();
      if (contractSelectOne == null) {
        throw new BusinessException("100000", "need deploy S11eProfile first");
      }
      String S11eProfile = contractSelectOne.getContract();
      constructParms =
          new Java2ContractTypeParameter.Builder()
              .addValue("string", List.of(ipfsGateway))
              .addParameter()
              .addValue("address", List.of(erc6551Registry))
              .addParameter()
              .build();
      contractDeployRet =
          bsinBlockChainEngine.contractDeploy(
              chainEnv, privateKey, null, null, null, byteCode, null, constructParms, 60000);
      contractAddress = (String) contractDeployRet.get("deployedAddress");
      txHash = (String) contractDeployRet.get("txHash");
    } else {
      throw new BusinessException("100000", " not supported contract protocol..");
    }
    Contract contract = new Contract();
    BeanUtil.copyProperties(contractProtocol, contract);
    contract.setSerialNo(BsinSnowflake.getId());
    contract.setName(contractProtocol.getProtocolName());
    contract.setMerchantNo(merchantNo);
    contract.setContract(contractAddress);
    contract.setTxHash(txHash);
    contract.setChainEnv(chainEnv);
    contract.setContractProtocolNo(contractProtocolNo);
    contract.setTxHash(txHash);
    contractMapper.insert(contract);
    return RespBodyHandler.setRespBodyDto(contract);
  }

  @Override
  public Map<String, Object> add(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    log.info(loginUser.toString());
    Contract contract = BsinServiceContext.getReqBodyDto(Contract.class, requestMap);
    contractMapper.insert(contract);
    return RespBodyHandler.setRespBodyDto(contract);
  }

  @Override
  public Map<String, Object> delete(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    contractMapper.deleteById(serialNo);
    return RespBodyHandler.RespBodyDto();
  }

  @Override
  public Map<String, Object> edit(Map<String, Object> requestMap) {
    Contract contract = BsinServiceContext.getReqBodyDto(Contract.class, requestMap);
    contractMapper.updateById(contract);
    return RespBodyHandler.RespBodyDto();
  }

  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String merchantNo = loginUser.getMerchantNo();

    Contract contract = BsinServiceContext.getReqBodyDto(Contract.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<Contract> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<Contract> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(Contract::getCreateTime);
    warapper.eq(Contract::getTenantId, tenantId);
    warapper.eq(Contract::getMerchantNo, merchantNo);
    warapper.eq(
        ObjectUtil.isNotNull(contract.getTxHash()), Contract::getTxHash, contract.getTxHash());
    warapper.eq(
        ObjectUtil.isNotNull(contract.getContract()),
        Contract::getContract,
        contract.getContract());
    warapper.eq(
        ObjectUtil.isNotNull(contract.getStatus()), Contract::getStatus, contract.getStatus());
    warapper.eq(
        ObjectUtil.isNotNull(contract.getChainEnv()),
        Contract::getChainEnv,
        contract.getChainEnv());
    warapper.eq(
        ObjectUtil.isNotNull(contract.getChainType()),
        Contract::getChainType,
        contract.getChainType());
    IPage<Contract> pageList = contractMapper.selectPage(page, warapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  @Override
  public Map<String, Object> getList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String merchantNo = loginUser.getMerchantNo();

    Contract contract = BsinServiceContext.getReqBodyDto(Contract.class, requestMap);
    LambdaQueryWrapper<Contract> warapper = new LambdaQueryWrapper<>();
    warapper.orderByDesc(Contract::getCreateTime);
    warapper.eq(Contract::getTenantId, tenantId);
    warapper.eq(Contract::getMerchantNo, merchantNo);
    warapper.eq(
        ObjectUtil.isNotNull(contract.getTxHash()), Contract::getTxHash, contract.getTxHash());
    warapper.eq(
        ObjectUtil.isNotNull(contract.getContract()),
        Contract::getContract,
        contract.getContract());
    warapper.eq(
        ObjectUtil.isNotNull(contract.getStatus()), Contract::getStatus, contract.getStatus());
    warapper.eq(
        ObjectUtil.isNotNull(contract.getChainEnv()),
        Contract::getChainEnv,
        contract.getChainEnv());
    warapper.eq(
        ObjectUtil.isNotNull(contract.getChainType()),
        Contract::getChainType,
        contract.getChainType());
    List<Contract> contractProtocolList = contractMapper.selectList(warapper);
    return RespBodyHandler.setRespBodyListDto(contractProtocolList);
  }

  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    Contract contract = contractMapper.selectById(serialNo);
    return RespBodyHandler.setRespBodyDto(contract);
  }
}
