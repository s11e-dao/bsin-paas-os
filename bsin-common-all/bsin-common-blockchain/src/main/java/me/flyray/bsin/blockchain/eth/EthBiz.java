package me.flyray.bsin.blockchain.eth;

import me.flyray.bsin.blockchain.enums.ChainEnv;
import me.flyray.bsin.blockchain.utils.Java2ContractTypeParameter;
import me.flyray.bsin.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.utils.Java2ContractTypeConcert;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import static java.lang.Thread.sleep;
import static me.flyray.bsin.constants.ResponseCode.HASH_NON_EXISTENT;
import static me.flyray.bsin.constants.ResponseCode.TRANSACTION_CONFIRMED_TIMEOUT;

/**
 * @author bolei
 * @date 2023/8/2 19:59
 * @desc
 */
@Slf4j
@Component
public class EthBiz implements BsinBlockChainEngine {

  @Override
  public Map<String, Object> createWallet(String password, String chainEnv) throws Exception {
    return null;
  }

  @Override
  public Map<String, Object> createAccount(
      String chainEnv, String privateKey, String gasPrice, String gasLimit, String value) {
    return null;
  }

  @Override
  public Map<String, Object> sendRawTransaction(String rawData, String chainEnv, int timeOut)
      throws Exception {
    Map result = new HashMap();
    return result;
  }

  @Override
  public Map<String, Object> contractDeploy(
      String chainEnv,
      String privateKey,
      String gasPrice,
      String gasLimit,
      String value,
      String byteCode,
      Java2ContractTypeParameter functionReturnType,
      Java2ContractTypeParameter functionInputParams,
      int timeOut)
      throws Exception {
    return null;
  }

  @Override
  public Map<String, Object> transfer(
      String chainEnv,
      String fromPrivateKey,
      String gasPrice,
      String gasLimit,
      String value,
      String toAddress,
      int timeOut)
      throws Exception {
    return null;
  }

  @Override
  public Map<String, Object> contractWrite(
      String chainEnv,
      String privateKey,
      String gasPrice,
      String gasLimit,
      String value,
      String contractAddress,
      String method,
      Java2ContractTypeParameter functionReturnType,
      Java2ContractTypeParameter functionInputParams,
      int timeOut)
      throws Exception {
    return null;
  }

  @Override
  public Map<String, Object> contractWrite(
      String chainEnv,
      String privateKey,
      String gasPrice,
      String gasLimit,
      String value,
      String contractAddress,
      String method,
      String methodId,
      Java2ContractTypeParameter functionReturnType,
      Java2ContractTypeParameter functionInputParams,
      int timeOut)
      throws Exception {
    return null;
  }

  @Override
  public Map<String, Object> contractRead(
      String chainEnv,
      String contractAddress,
      String method,
      Java2ContractTypeParameter functionReturnType,
      Java2ContractTypeParameter functionInputParams,
      int timeOut)
      throws Exception {
    return null;
  }

  @Override
  public Map<String, Object> contractRead(
      String chainEnv,
      String contractAddress,
      String method,
      String methodId,
      Java2ContractTypeParameter functionReturnType,
      Java2ContractTypeParameter functionInputParams,
      int timeOut)
      throws Exception {
    return null;
  }

  @Override
  public Map<String, Object> getTransaction(String chainEnv, String txHash, int timeOut) {
    return null;
  }

  @Override
  public Map<String, Object> getSponsor(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @Override
  public Map<String, Object> setSponsor(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @Override
  public Map<String, Object> addWhiteList(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @Override
  public Map<String, Object> isWhitelisted(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @Override
  public Map<String, Object> removeWhiteList(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @Override
  public BigInteger getNonce(String chainEnv, String address) {
    //        try {
    //            EthGetTransactionCount getNonce;
    //            if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
    //                getNonce = web3.ethGetTransactionCount(address,
    // DefaultBlockParameterName.PENDING).send();
    //                getNonce = web3.ethGetTransactionCount(address,
    // DefaultBlockParameterName.LATEST).send();
    //            } else {
    //                getNonce = web3.ethGetTransactionCount(address,
    // DefaultBlockParameterName.LATEST).send();
    //            }
    //            if (getNonce == null) {
    //                throw new RuntimeException("net error");
    //            }
    //            return getNonce.getTransactionCount();
    //        } catch (IOException e) {
    //            throw new RuntimeException("net error");
    //        }
    return null;
  }

  @Override
  public String getAddress(String chainEnv, String privateKey) {
    return null;
  }

  @Override
  public String getPrivateKey() {
    return null;
  }

  @Override
  public String getBalance(String chainEnv, String address) throws Exception {
    return null;
  }

  @Override
  public String getAssetBalance(
      String chainEnv,
      String contractAddress,
      String accountAddress,
      String assetProtocol,
      String tokenId)
      throws Exception {
    return null;
  }

  @Override
  public String getIpfsGateway() {
    return null;
  }

  @Override
  public String getZeroAddress(String chainEnv) {
    return null;
  }

  @Override
  public String getWeb3ClientVersion(String chainEnv) {
    return null;
  }

  @Override
  public long getBlockHeight(String chainEnv) {
    return 0;
  }

  @Override
  public BigInteger getChainId(String chainEnv) {
    return null;
  }

  @Override
  public String getTreasuryContractAddress(
      String chainEnv,
      String daoAddressStr,
      String wrapperOrExtensionName,
      boolean byFactory,
      boolean isInsert) {
    return null;
  }

  @Override
  public String getVaultsContractAddress(
      String chainEnv,
      String daoAddressStr,
      String wrapperOrExtensionName,
      boolean byFactory,
      boolean isInsert) {
    return null;
  }

  @Override
  public String getDaoWrapperOrExtensionAddressByName(
      String chainEnv, String daoAddressStr, String wrapperOrExtensionName, boolean isInsert) {
    return null;
  }

  @Override
  public String getDaoNameByAddress(String chainEnv, String daoAddressStr, boolean isInsert) {
    return null;
  }

  @Override
  public String getDaoAddressByIndex(String chainEnv, String daoIndex, boolean isInsert) {
    return null;
  }

  @Override
  public String getDaoAddressByName(String chainEnv, String daoName, boolean isInsert) {
    return null;
  }
}
