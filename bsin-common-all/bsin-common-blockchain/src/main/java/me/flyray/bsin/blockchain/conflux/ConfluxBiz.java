package me.flyray.bsin.blockchain.conflux;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint128;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.ObjectMapperFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import conflux.web3j.Account;
import conflux.web3j.CfxUnit;
import conflux.web3j.Web3;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.internals.SponsorWhitelistControl;
import conflux.web3j.response.Receipt;
import conflux.web3j.types.Address;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.enums.ChainEnv;
import me.flyray.bsin.blockchain.enums.ChainType;
import me.flyray.bsin.blockchain.utils.BsinDefaultFunctionEncoder;
import me.flyray.bsin.blockchain.utils.Java2ContractTypeConcert;
import me.flyray.bsin.blockchain.utils.Java2ContractTypeParameter;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.exception.BusinessException;

/**
 * @author bolei
 * @date 2023/6/26 15:52
 * @desc
 */
@Slf4j
@Component
public class ConfluxBiz implements BsinBlockChainEngine {

  private String ipfsGateway;
  private String privateKey;
  private String confluxRpcUrlTest;
  private String confluxRpcUrlMain;
  private String chainType;
  private Web3 web3;
  private Web3 web3Test;
  private BigInteger gasLimit;
  private BigInteger storageLimit;
  private BigInteger gasPrice;
  private BigInteger value = BigInteger.ZERO;
  private BigInteger epochHeight;
  private BigInteger chainId;

  // web3j 编码类

  public ConfluxBiz() {
    //        this.web3Test = Web3.create(this.confluxRpcUrlTest, 3, 10000);
    //        this.web3 = Web3.create(this.confluxRpcUrlMain, 3, 10000);
    this.confluxRpcUrlTest = "https://test.confluxrpc.com/v2";
    this.confluxRpcUrlMain = "https://test.confluxrpc.com/v2";
    this.privateKey = "6e88746088c5d054f983131babd915eac2764879c5b732044bfed903fe70c";
    this.ipfsGateway = "https://ipfs.s11edao.com/ipfs/";
    this.web3Test = Web3.create("https://test.confluxrpc.com/v2", 3, 10000);
    this.web3 = Web3.create("https://test.confluxrpc.com/v2", 3, 10000);
    chainType = ChainType.CONFLUX.getCode();
  }

  //    private static CofluxWeb3 cofluxSingleton;
  //
  //    public static CofluxWeb3 getInstance() {
  //        return SingletonHander.cofluxSingleton;
  //    }
  //
  //    private static class SingletonHander {
  //        private static final CofluxWeb3 cofluxSingleton = new CofluxWeb3();
  //    }

  @Override
  public Map<String, Object> createWallet(String password, String chainEnv) throws Exception {
    WalletFile walletFile;
    ECKeyPair ecKeyPair = Keys.createEcKeyPair();
    walletFile = Wallet.createStandard(password, ecKeyPair);
    log.info("conflux wallet address: {}", walletFile.getAddress());
    ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    String jsonStr = objectMapper.writeValueAsString(walletFile);
    log.info("keystore json file: {}", jsonStr);
    Credentials credentials = WalletUtils.loadJsonCredentials(password, jsonStr);
    String address = credentials.getAddress();
    System.out.println("address=" + address);
    BigInteger privateKey = credentials.getEcKeyPair().getPrivateKey();
    String sPrivatekeyInHex = privateKey.toString(16);
    System.out.println(sPrivatekeyInHex);
    BigInteger publicKey = credentials.getEcKeyPair().getPublicKey();
    System.out.println("public key=" + publicKey);
    Map map = createAccount(chainEnv, sPrivatekeyInHex, null, null, null);
    Account account = (Account) map.get("account");
    // 返回参数
    Map<String, Object> result = new HashMap<>();
    result.put("keystoreJson", jsonStr);
    result.put("address", account.getAddress().toString());
    result.put("privateKey", sPrivatekeyInHex);
    return result;
  }

  @Override
  public Map<String, Object> createAccount(
      String chainEnv, String privateKey, String gasPrice, String gasLimit, String value) {
    Map map = new HashMap();
    Account.Option opt = new Account.Option();
    Account account;
    if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
      account = Account.create(web3, privateKey);
      opt.withChainId(1029);
    } else {
      account = Account.create(web3Test, privateKey);
      opt.withChainId(1);
    }
    if (gasPrice != null) {
      opt.withGasPrice(new BigInteger(gasPrice));
    }
    // gasLimit和storageLimit为空时使用estimateGasAndCollateral 自动预估
    if (gasLimit != null) {
      opt.withGasLimit(new BigInteger(gasLimit));
    }
    if (value != null) {
      opt.withValue(new BigInteger(value));
    }
    map.put("opt", opt);
    map.put("account", account);
    return map;
  }

  @Override
  public Map<String, Object> sendRawTransaction(String rawData, String chainEnv, int timeOut)
      throws Exception {
    Map result = new HashMap();
    //        EthSendTransaction ethSendTransaction;
    //        String txHash;
    //        EthGetTransactionReceipt transactionReceipt;
    //        int loop = 0;
    //        int delay = timeOut / 1000;
    //        int count = timeOut % 1000;
    //        if (delay <= 0) {
    //            delay = 10;
    //        }
    //        if (ChainEnv.MAIN.getCode().
    //                equals(chainEnv)) {
    //            ethSendTransaction = web3.ethSendRawTransaction(rawData).sendAsync().get();
    //            txHash = ethSendTransaction.getTransactionHash();
    //            if (txHash == null) {
    //                throw new BusinessException(HASH_NON_EXISTENT);
    //            }
    //            transactionReceipt =
    //                    web3.ethGetTransactionReceipt(txHash).send();
    //
    //            while (!transactionReceipt.getTransactionReceipt().isPresent()) {
    //                sleep(delay);
    //                loop++;
    //                if (loop > count) {
    //                    loop = 0;
    //                    throw new BusinessException(TRANSACTION_CONFIRMED_TIMEOUT);
    //                }
    //                transactionReceipt =
    //                        web3.ethGetTransactionReceipt(txHash).send();
    //            }
    //        } else {
    //            ethSendTransaction = web3Test.ethSendRawTransaction(rawData).sendAsync().get();
    //            txHash = ethSendTransaction.getTransactionHash();
    //            if (txHash == null) {
    //                throw new BusinessException(HASH_NON_EXISTENT);
    //            }
    //            transactionReceipt =
    //                    web3Test.ethGetTransactionReceipt(txHash).send();
    //            while (!transactionReceipt.getTransactionReceipt().isPresent()) {
    //                sleep(delay);
    //                loop++;
    //                if (loop > count) {
    //                    loop = 0;
    //                    throw new BusinessException(TRANSACTION_CONFIRMED_TIMEOUT);
    //                }
    //                transactionReceipt =
    //                        web3Test.ethGetTransactionReceipt(txHash).send();
    //            }
    //        }
    //        result.put("receipt", transactionReceipt.getTransactionReceipt());
    //        result.put("txHash", txHash);
    //        result.put("contractAddress",
    // transactionReceipt.getTransactionReceipt().get().getContractAddress());
    //        System.out.println(txHash);
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
    Map result = new HashMap();
    Account.Option opt = new Account.Option();
    Account account;
    if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
      account = Account.create(web3, privateKey);
      opt.withChainId(1029);
    } else {
      account = Account.create(web3Test, privateKey);
      opt.withChainId(1);
    }
    if (gasPrice != null) {
      opt.withGasPrice(new BigInteger(gasPrice));
    }
    // gasLimit和storageLimit为空时使用estimateGasAndCollateral 自动预估
    if (gasLimit != null) {
      opt.withGasLimit(new BigInteger(gasLimit));
    }
    if (value != null) {
      opt.withValue(new BigInteger(value));
    }

    Type[] inputs = parseInputParams(functionInputParams);
    String txHash = null;
    if (inputs == null) {
      txHash = account.deploy(opt, byteCode);
    } else {
      Arrays.asList(inputs);
      txHash = account.deploy(opt, byteCode, inputs);
    }
    Receipt transactionReceipt = getTransactionReceipt(chainEnv, txHash);
    short txStatus = transactionReceipt.getOutcomeStatus();
    log.debug("deploy交易状态： " + txStatus);
    log.debug(transactionReceipt.toString());
    log.debug("txHash: " + txHash);
    String deployedAddress = transactionReceipt.getContractCreated().get().getAddress();
    // TODO:isStatusOK
    boolean isStatusOK = true;
    result.put("txHash", txHash);
    result.put("deployedAddress", deployedAddress);
    result.put("txStatus", (String) String.valueOf(txStatus));
    if (txStatus == 0x01) {
      isStatusOK = true;
    }
    result.put("isStatusOK", isStatusOK);
    return result;
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
    Map result = new HashMap();
    Map map = createAccount(chainEnv, privateKey, gasPrice, gasLimit, value);
    Account from = (Account) map.get("account");
    String txHash = from.transfer(new Address(toAddress), new BigInteger(value));
    Receipt transactionReceipt = getTransactionReceipt(chainEnv, txHash);
    short txStatus = transactionReceipt.getOutcomeStatus();
    log.debug("transfer交易状态： " + txStatus);
    log.debug(transactionReceipt.toString());
    log.debug("txHash: " + txHash);
    // TODO:isStatusOK
    boolean isStatusOK = true;
    result.put("txHash", txHash);
    result.put("txStatus", (String) String.valueOf(txStatus));
    if (txStatus == 0x01) {
      isStatusOK = true;
    }
    result.put("isStatusOK", isStatusOK);

    return result;
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
    Map result = new HashMap();
    try {
      Account.Option opt = new Account.Option();
      Account account;
      if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
        account = Account.create(web3, privateKey);
        opt.withChainId(1029);
      } else {
        account = Account.create(web3Test, privateKey);
        opt.withChainId(1);
      }
      if (gasPrice != null) {
        opt.withGasPrice(new BigInteger(gasPrice));
      }
      // gasLimit和storageLimit为空时使用estimateGasAndCollateral 自动预估
      if (gasLimit != null) {
        opt.withGasLimit(new BigInteger(gasLimit));
      }
      if (value != null) {
        opt.withValue(new BigInteger(value));
      }
      Type[] inputs = parseInputParams(functionInputParams);
      String txHash = null;

      // 返回值不为空，先用ContractCall模拟调用获取返回值
      if (functionReturnType != null) {
        Map<String, Object> contractRWret =
            contractRead(
                chainEnv, contractAddress, method, functionReturnType, functionInputParams, 60000);
        result.put(
            functionReturnType.getParameterList().get(0).getType(),
            contractRWret.get(functionReturnType.getParameterList().get(0).getType()));
      }

      if (inputs == null) {
        txHash = account.call(opt, new Address(contractAddress), method);
      } else {
        Arrays.asList(inputs);
        txHash = account.call(opt, new Address(contractAddress), method, inputs);
      }
      Receipt transactionReceipt = getTransactionReceipt(chainEnv, txHash);
      short txStatus = transactionReceipt.getOutcomeStatus();
      log.debug(contractAddress + "" + method + "交易状态： " + txStatus);
      log.debug(transactionReceipt.toString());
      // TODO:isStatusOK
      boolean isStatusOK = false;
      result.put("Receipt", transactionReceipt);
      result.put("txHash", txHash);
      result.put("txStatus", (String) String.valueOf(txStatus));
      if (txStatus == 0x01) {
        isStatusOK = true;
      }
      result.put("isStatusOK", isStatusOK);
      return result;
    } catch (Exception e) {
      log.error(e.toString());
      throw new BusinessException("999999", e.toString());
    }
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
    Map result = new HashMap();
    try {
      Account.Option opt = new Account.Option();
      Account account;
      if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
        account = Account.create(web3, privateKey);
        opt.withChainId(1029);
      } else {
        account = Account.create(web3Test, privateKey);
        opt.withChainId(1);
      }
      if (gasPrice != null) {
        opt.withGasPrice(new BigInteger(gasPrice));
      }
      // gasLimit和storageLimit为空时使用estimateGasAndCollateral 自动预估
      if (gasLimit != null) {
        opt.withGasLimit(new BigInteger(gasLimit));
      }
      if (value != null) {
        opt.withValue(new BigInteger(value));
      }

      // 返回值不为空，先用ContractCall模拟调用获取返回值
      if (functionReturnType != null) {
        Map<String, Object> contractRWret =
            contractRead(
                chainEnv,
                contractAddress,
                method,
                methodId,
                functionReturnType,
                functionInputParams,
                60000);
        result.put(
            functionReturnType.getParameterList().get(0).getType(),
            contractRWret.get(functionReturnType.getParameterList().get(0).getType()));
      }

      Type[] inputs = parseInputParams(functionInputParams);
      Arrays.asList(inputs);
      DynamicStruct dynamicStruct = new DynamicStruct(inputs);
      Function function =
          new Function(method, Arrays.asList(dynamicStruct), Collections.emptyList());
      methodId = BsinDefaultFunctionEncoder.buildMethodId(methodId);
      log.debug(methodId);
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(methodId);
      List<Type> parameters = function.getInputParameters();
      String callData = BsinDefaultFunctionEncoder.encodeParameters(parameters, stringBuilder);

      String txHash = account.callWithData(opt, new Address(contractAddress), callData);
      Receipt transactionReceipt = getTransactionReceipt(chainEnv, txHash);
      short txStatus = transactionReceipt.getOutcomeStatus();
      log.debug(contractAddress + "" + method + " 交易状态： " + txStatus);
      log.debug(transactionReceipt.toString());
      log.debug("txHash: " + txHash);
      // TODO:isStatusOK
      boolean isStatusOK = true;
      result.put("txHash", txHash);
      result.put("txStatus", (String) String.valueOf(txStatus));
      if (txStatus == 0x01) {
        isStatusOK = true;
      }
      result.put("isStatusOK", isStatusOK);

      return result;
    } catch (Exception e) {
      log.error(e.toString());
      throw new BusinessException("999999", e.toString());
    }
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
    if (functionReturnType == null) {
      throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);
    }
    Map result = new HashMap();
    Type[] inputs = parseInputParams(functionInputParams);
    Arrays.asList(inputs);
    DynamicStruct dynamicStruct = new DynamicStruct(inputs);
    Function function = new Function(method, Arrays.asList(dynamicStruct), Collections.emptyList());
    methodId = BsinDefaultFunctionEncoder.buildMethodId(methodId);
    log.debug(methodId);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(methodId);
    List<Type> parameters = function.getInputParameters();
    //    String callData = BsinDefaultFunctionEncoder.encodeParameters(parameters, stringBuilder);
    Type[] finalInputs = parameters.toArray(new Type[parameters.size()]);
    ContractCall contractCall = createContractCall(chainEnv, new Address(contractAddress));
    String ret = null;
    switch (functionReturnType.getParameterList().get(0).getType()) {
      case "address":
        String addresHex = null;
        if (inputs == null) {
          addresHex = contractCall.callAndGet(org.web3j.abi.datatypes.Address.class, method);
        } else {
          Arrays.asList(inputs);
          addresHex =
              contractCall.callAndGet(org.web3j.abi.datatypes.Address.class, method, finalInputs);
        }
        ret = convertAddress(chainEnv, addresHex);
        break;

      case "address[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "string":
        if (inputs == null) {
          ret = contractCall.callAndGet(Utf8String.class, method);
        } else {
          ret = contractCall.callAndGet(Utf8String.class, method, finalInputs);
        }
        break;

      case "string[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "bool":
        Boolean tmpBool = null;
        if (inputs == null) {
          tmpBool = contractCall.callAndGet(Bool.class, method);
        } else {
          tmpBool = contractCall.callAndGet(Bool.class, method, finalInputs);
        }
        ret = tmpBool.toString();
        break;

      case "bool[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "uint8":
        BigInteger tmp8 = null;
        if (inputs == null) {
          tmp8 = contractCall.callAndGet(Uint8.class, method);
        } else {
          tmp8 = contractCall.callAndGet(Uint8.class, method, finalInputs);
        }
        ret = tmp8.toString();
        break;

      case "uint8[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "uint32":
        BigInteger tmp32 = null;
        if (inputs == null) {
          tmp32 = contractCall.callAndGet(Uint32.class, method);
        } else {
          tmp32 = contractCall.callAndGet(Uint32.class, method, finalInputs);
        }
        ret = tmp32.toString();
        break;

      case "uint32[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "uint128":
        BigInteger tmp128 = null;
        if (inputs == null) {
          tmp128 = contractCall.callAndGet(Uint128.class, method);
        } else {
          tmp128 = contractCall.callAndGet(Uint128.class, method, finalInputs);
        }
        ret = tmp128.toString();
        break;

      case "uint128[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "uint256":
        BigInteger tmp256 = null;
        if (inputs == null) {
          tmp256 = contractCall.callAndGet(Uint256.class, method);
        } else {
          tmp256 = contractCall.callAndGet(Uint256.class, method, finalInputs);
        }
        ret = tmp256.toString();
        break;

      case "uint256[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "bytes32":
        byte[] tmpByte32 = null;
        if (inputs == null) {
          tmpByte32 = contractCall.callAndGet(Bytes32.class, method);
        } else {
          tmpByte32 = contractCall.callAndGet(Bytes32.class, method, finalInputs);
        }
        ret = new String(tmpByte32);
        break;

      case "bytes32[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);
      case "bytes":
        byte[] tmpBytes = null;
        if (inputs == null) {
          tmpBytes = contractCall.callAndGet(DynamicBytes.class, method);
        } else {
          tmpBytes = contractCall.callAndGet(DynamicBytes.class, method, finalInputs);
        }
        ret = new String(tmpBytes);
        break;

      case "bytes[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      default:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);
    }
    result.put(functionReturnType.getParameterList().get(0).getType(), ret);

    return result;
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
    if (functionReturnType == null) {
      throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);
    }
    Map result = new HashMap();
    Type[] inputs = parseInputParams(functionInputParams);

    ContractCall contractCall = createContractCall(chainEnv, new Address(contractAddress));
    String ret = null;
    switch (functionReturnType.getParameterList().get(0).getType()) {
      case "address":
        String addresHex = null;
        if (inputs == null) {
          addresHex = contractCall.callAndGet(org.web3j.abi.datatypes.Address.class, method);
        } else {
          Arrays.asList(inputs);
          addresHex =
              contractCall.callAndGet(org.web3j.abi.datatypes.Address.class, method, inputs);
        }
        ret = convertAddress(chainEnv, addresHex);
        break;

      case "address[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "string":
        if (inputs == null) {
          ret = contractCall.callAndGet(Utf8String.class, method);
        } else {
          ret = contractCall.callAndGet(Utf8String.class, method, inputs);
        }
        break;

      case "string[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "bool":
        Boolean tmpBool = null;
        if (inputs == null) {
          tmpBool = contractCall.callAndGet(Bool.class, method);
        } else {
          tmpBool = contractCall.callAndGet(Bool.class, method, inputs);
        }
        ret = tmpBool.toString();
        break;

      case "bool[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "uint8":
        BigInteger tmp8 = null;
        if (inputs == null) {
          tmp8 = contractCall.callAndGet(Uint8.class, method);
        } else {
          tmp8 = contractCall.callAndGet(Uint8.class, method, inputs);
        }
        ret = tmp8.toString();
        break;

      case "uint8[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "uint32":
        BigInteger tmp32 = null;
        if (inputs == null) {
          tmp32 = contractCall.callAndGet(Uint32.class, method);
        } else {
          tmp32 = contractCall.callAndGet(Uint32.class, method, inputs);
        }
        ret = tmp32.toString();
        break;

      case "uint32[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "uint128":
        BigInteger tmp128 = null;
        if (inputs == null) {
          tmp128 = contractCall.callAndGet(Uint128.class, method);
        } else {
          tmp128 = contractCall.callAndGet(Uint128.class, method, inputs);
        }
        ret = tmp128.toString();
        break;

      case "uint128[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "uint256":
        BigInteger tmp256 = null;
        if (inputs == null) {
          tmp256 = contractCall.callAndGet(Uint256.class, method);
        } else {
          tmp256 = contractCall.callAndGet(Uint256.class, method, inputs);
        }
        ret = tmp256.toString();
        break;

      case "uint256[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "bytes32":
        byte[] tmpByte32 = null;
        if (inputs == null) {
          tmpByte32 = contractCall.callAndGet(Bytes32.class, method);
        } else {
          tmpByte32 = contractCall.callAndGet(Bytes32.class, method, inputs);
        }
        ret = new String(tmpByte32);
        break;

      case "bytes32[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);
      case "bytes":
        byte[] tmpBytes = null;
        if (inputs == null) {
          tmpBytes = contractCall.callAndGet(DynamicBytes.class, method);
        } else {
          tmpBytes = contractCall.callAndGet(DynamicBytes.class, method, inputs);
        }
        ret = new String(tmpBytes);
        break;

      case "bytes[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      default:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);
    }
    result.put(functionReturnType.getParameterList().get(0).getType(), ret);

    return result;
  }

  @Override
  public Map<String, Object> getTransaction(String chainEnv, String txHash, int timeOut)
      throws Exception {
    Receipt transactionReceipt = getTransactionReceipt(chainEnv, txHash);
    short txStatus = transactionReceipt.getOutcomeStatus();
    log.debug("transfer交易状态： " + txStatus);
    log.debug(transactionReceipt.toString());
    log.debug("txHash: " + txHash);
    // TODO:isStatusOK
    boolean isStatusOK = true;
    Map result = new HashMap();
    result.put("txHash", txHash);
    result.put("txStatus", (String) String.valueOf(txStatus));
    result.put("transaction", JSONObject.toJSON(transactionReceipt));
    if (txStatus == 0x01) {
      isStatusOK = true;
    }
    result.put("isStatusOK", isStatusOK);
    return result;
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
    Map<String, Object> result = new HashMap<>();
    String contractAddressStr = (String) requestMap.get("contractAddress");
    String userAddressStr = (String) requestMap.get("userAddress");
    String chainEnv = (String) requestMap.get("chainEnv");
    Address contractAddress = new Address(contractAddressStr);
    SponsorWhitelistControl sc = null;
    if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
      sc = new SponsorWhitelistControl(web3);
    } else {
      sc = new SponsorWhitelistControl(web3Test);
    }
    if (userAddressStr != null) {
      Address userAddress = new Address(userAddressStr);
      boolean isWhitelisted =
          sc.isWhitelisted(contractAddress.getABIAddress(), userAddress.getABIAddress());
      log.info("isWhitelisted:{}", isWhitelisted);
      result.put("isWhitelisted", isWhitelisted);
    }
    boolean isAllWhitelisted = sc.isAllWhitelisted(contractAddress.getABIAddress());
    result.put("isAllWhitelisted", isAllWhitelisted);
    return result;
  }

  @Override
  public Map<String, Object> removeWhiteList(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @Override
  public String getAddress(String chainEnv, String privateKey) {
    Account account;
    if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
      account = Account.create(web3, privateKey);
    } else {
      account = Account.create(web3Test, privateKey);
    }
    return account.getAddress().toString();
  }

  @Override
  public String getPrivateKey() {
    return this.privateKey;
  }

  @Override
  public String getBalance(String chainEnv, String address) throws Exception {
    String blanceCFX = null;
    BigInteger balance = null;
    if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
      balance = this.web3.getBalance(new Address(address)).sendAndGet();
    } else {
      balance = this.web3Test.getBalance(new Address(address)).sendAndGet();
    }
    System.out.println("balance in Drip: " + balance);
    // 格式转化 drip-cfx
    blanceCFX = CfxUnit.drip2Cfx(balance).toPlainString();
    System.out.println("balance in CFX: " + blanceCFX);
    return blanceCFX;
  }

  @Override
  public String getAssetBalance(
      String chainEnv,
      String contractAddress,
      String accountAddress,
      String assetProtocol,
      String tokenId)
      throws Exception {

    String assetBalance = null;
    Java2ContractTypeParameter functionInputParams =
        new Java2ContractTypeParameter.Builder()
            .addValue("address", List.of(accountAddress))
            .addParameter() // 0、address account
            .build();

    Java2ContractTypeParameter functionReturnType =
        new Java2ContractTypeParameter.Builder()
            .addValue("uint256", null)
            .addParameter()
            .addValue("uint256", List.of("12345"))
            .addParameter()
            .addValue("bool", List.of("false"))
            .addParameter()
            .build();

    Map<String, Object> contractRWret = new HashMap<String, Object>();
    if (assetProtocol.equals("ERC20") || assetProtocol.equals("ERC721")) {
      // ERC20 function balanceOf(address account) external view returns (uint256);
      contractRWret =
          contractRead(
              chainEnv,
              contractAddress,
              "balanceOf",
              functionReturnType,
              functionInputParams,
              60000);
      assetBalance =
          (String) contractRWret.get(functionReturnType.getParameterList().get(0).getType());
    } else if (assetProtocol.equals("ERC1155")) {
      // 1、uint256 tokenId
      functionInputParams =
          new Java2ContractTypeParameter.Builder()
              .addValue("address", List.of(accountAddress))
              .addParameter() // 0、address account
              .addValue("uint256", List.of(tokenId))
              .addParameter() // 0、address account
              .build();
      // ERC1155 function balanceOf(address account, uint256 id) external view returns (uint256);
      contractRWret =
          contractRead(
              chainEnv,
              contractAddress,
              "balanceOf",
              functionReturnType,
              functionInputParams,
              60000);
      assetBalance =
          (String) contractRWret.get(functionReturnType.getParameterList().get(0).getType());
    } else {
      throw new BusinessException(ResponseCode.ILLEGAL_ASSETS_PROTOCOL);
    }

    return assetBalance;
  }

  @Override
  public String getIpfsGateway() {
    return this.ipfsGateway;
  }

  @Override
  public String getZeroAddress(String chainEnv) {
    String address = null;
    if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
      address = "cfx:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa6f0vrcsw";
    } else if (ChainEnv.TEST.getCode().equals(chainEnv)) {
      address = "cfxtest:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa6f0vrcsw";
    } else {
      throw new BusinessException(ResponseCode.NOT_SUPPORTED_ChAIN_ENV);
    }
    return address;
  }

  @Override
  public String getWeb3ClientVersion(String chainEnv) {
    String clientVersion = null;
    try {
      if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
        clientVersion = web3.getClientVersion().sendAndGet();
      } else {
        clientVersion = web3Test.getClientVersion().sendAndGet();
      }
      log.info("web3ClientVersion: ", clientVersion);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return clientVersion;
  }

  @Override
  public long getBlockHeight(String chainEnv) {
    BigInteger blockHeight = null;
    try {
      if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
        blockHeight = web3.getEpochNumber().sendAndGet();
      } else {
        blockHeight = web3Test.getEpochNumber().sendAndGet();
      }
      log.info("blockHeight: ", blockHeight);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return blockHeight.longValue();
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

  private Receipt getTransactionReceipt(String chainEnv, String txHash)
      throws InterruptedException {
    Receipt transactionReceipt;
    if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
      transactionReceipt = web3.waitForReceipt(txHash);
    } else {
      transactionReceipt = web3Test.waitForReceipt(txHash);
    }
    return transactionReceipt;
  }

  private Type[] parseInputParams(List<Java2ContractTypeConcert> functionInputParams) {
    if (functionInputParams != null) {
      int inputLength = functionInputParams.size();
      Type[] inputs = new Type[inputLength];
      int i = 0;
      for (Java2ContractTypeConcert inputParam : functionInputParams) {
        switch (inputParam.getType()) {
          case "address":
            inputs[i] = new Address((String) inputParam.getValue().get("0")).getABIAddress();
            break;

          case "address[]":
            List<org.web3j.abi.datatypes.Address> addressArry = new ArrayList<>();
            for (Integer j = 0; j < inputParam.getValue().size(); j++) {
              addressArry.add(
                  new Address((String) inputParam.getValue().get(j.toString())).getABIAddress());
            }
            DynamicArray<org.web3j.abi.datatypes.Address> addtrsss =
                new DynamicArray<>(org.web3j.abi.datatypes.Address.class, addressArry);
            inputs[i] = addtrsss;
            break;

          case "string":
            inputs[i] = new Utf8String((String) inputParam.getValue().get("0"));
            break;

          case "string[]":
            List<Utf8String> stringsArry = new ArrayList<>();
            for (Integer j = 0; j < inputParam.getValue().size(); j++) {
              stringsArry.add(new Utf8String((String) inputParam.getValue().get(j.toString())));
            }
            DynamicArray<Utf8String> stringss = new DynamicArray<>(Utf8String.class, stringsArry);
            inputs[i] = stringss;
            break;

          case "bool":
            inputs[i] = new Bool((boolean) inputParam.getValue().get("0"));
            break;

          case "bool[]":
            // TODO:
            throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

          case "uint8":
            inputs[i] =
                new Uint8(new BigDecimal((String) inputParam.getValue().get("0")).toBigInteger());
            break;

          case "uint8[]":
            List<Uint8> uint8Arry = new ArrayList<>();
            for (Integer j = 0; j < inputParam.getValue().size(); j++) {
              uint8Arry.add(
                  new Uint8(
                      new BigDecimal((String) inputParam.getValue().get(j.toString()))
                          .toBigInteger()));
            }
            DynamicArray<Uint8> uint8s = new DynamicArray<>(Uint8.class, uint8Arry);
            inputs[i] = uint8s;
            break;

          case "uint32":
            inputs[i] =
                new Uint32(new BigDecimal((String) inputParam.getValue().get("0")).toBigInteger());
            break;

          case "uint32[]":
            List<Uint32> uint32Arry = new ArrayList<>();
            for (Integer j = 0; j < inputParam.getValue().size(); j++) {
              uint32Arry.add(
                  new Uint32(
                      new BigDecimal((String) inputParam.getValue().get(j.toString()))
                          .toBigInteger()));
            }
            DynamicArray<Uint32> uint32s = new DynamicArray<>(Uint32.class, uint32Arry);
            inputs[i] = uint32s;
            break;

          case "uint128":
            inputs[i] =
                new Uint128(new BigDecimal((String) inputParam.getValue().get("0")).toBigInteger());
            break;

          case "uint128[]":
            List<Uint128> uint128Arry = new ArrayList<>();
            for (Integer j = 0; j < inputParam.getValue().size(); j++) {
              uint128Arry.add(
                  new Uint128(
                      new BigDecimal((String) inputParam.getValue().get(j.toString()))
                          .toBigInteger()));
            }
            DynamicArray<Uint128> uint128s = new DynamicArray<>(Uint128.class, uint128Arry);
            inputs[i] = uint128s;
            break;

          case "uint256":
            inputs[i] =
                new Uint256(new BigDecimal((String) inputParam.getValue().get("0")).toBigInteger());
            break;

          case "uint256[]":
            List<Uint256> uint256Arry = new ArrayList<>();
            for (Integer j = 0; j < inputParam.getValue().size(); j++) {
              uint256Arry.add(
                  new Uint256(
                      new BigDecimal((String) inputParam.getValue().get(j.toString()))
                          .toBigInteger()));
            }
            DynamicArray<Uint256> uint256s = new DynamicArray<>(Uint256.class, uint256Arry);
            inputs[i] = uint256s;
            break;

          case "bytes32":
            inputs[i] =
                new Bytes32(
                    Hash.sha3(
                        ((String) inputParam.getValue().get("0"))
                            .getBytes(StandardCharsets.UTF_8)));
            break;

          case "bytes32[]":
            List<Bytes32> bytes32Arry = new ArrayList<>();
            for (Integer j = 0; j < inputParam.getValue().size(); j++) {
              byte[] byteArry =
                  Hash.sha3(
                      ((String) inputParam.getValue().get("0")).getBytes(StandardCharsets.UTF_8));
              Bytes32 bytes32 = new Bytes32(byteArry);
              bytes32Arry.add(bytes32);
            }
            DynamicArray<Bytes32> bytes32s = new DynamicArray<>(Bytes32.class, bytes32Arry);
            inputs[i] = bytes32s;
            break;

          case "bytes":
            inputs[i] = new DynamicBytes(((String) inputParam.getValue().get("0")).getBytes());
            break;

          case "bytes[]":
            List<DynamicBytes> bytesArry = new ArrayList<>();
            for (Integer j = 0; j < inputParam.getValue().size(); j++) {
              byte[] byteArry =
                  Hash.sha3(
                      ((String) inputParam.getValue().get("0")).getBytes(StandardCharsets.UTF_8));
              DynamicBytes bytes32 = new DynamicBytes(byteArry);
              bytesArry.add(bytes32);
            }
            DynamicArray<DynamicBytes> bytess = new DynamicArray<>(DynamicBytes.class, bytesArry);
            inputs[i] = bytess;
            inputs[i] = new DynamicBytes(((String) inputParam.getValue().get("0")).getBytes());
            break;

          default:
            throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);
        }
        i++;
      }
      return inputs;
    }
    return null;
  }

  private Type[] parseInputParams(Java2ContractTypeParameter functionInputParams) {
    if (functionInputParams != null) {
      int inputLength = functionInputParams.getParameterList().size();
      Type[] inputs = new Type[inputLength];
      int i = 0;
      for (Java2ContractTypeParameter.ParameterStruct inputParamMap :
          functionInputParams.getParameterList()) {
        switch (inputParamMap.getType()) {
          case "address":
            inputs[i] = new Address((String) inputParamMap.getParameter().get(0)).getABIAddress();
            break;

          case "address[]":
            List<org.web3j.abi.datatypes.Address> addressArry = new ArrayList<>();
            for (Integer j = 0; j < inputParamMap.getParameter().size(); j++) {
              addressArry.add(
                  new org.web3j.abi.datatypes.Address(
                      (String) inputParamMap.getParameter().get(j)));
            }
            DynamicArray<org.web3j.abi.datatypes.Address> addtrsss =
                new DynamicArray<>(org.web3j.abi.datatypes.Address.class, addressArry);
            inputs[i] = addtrsss;
            break;

          case "string":
            inputs[i] = new Utf8String((String) inputParamMap.getParameter().get(0));
            break;

          case "string[]":
            List<Utf8String> stringsArry = new ArrayList<>();
            for (Integer j = 0; j < inputParamMap.getParameter().size(); j++) {
              stringsArry.add(new Utf8String((String) inputParamMap.getParameter().get(j)));
            }
            DynamicArray<Utf8String> stringss = new DynamicArray<>(Utf8String.class, stringsArry);
            inputs[i] = stringss;
            break;

          case "bool":
            inputs[i] =
                new Bool(Boolean.parseBoolean((String) inputParamMap.getParameter().get(0)));
            break;

          case "bool[]":
            // TODO:
            throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

          case "uint8":
            inputs[i] =
                new Uint8(
                    new BigDecimal((String) inputParamMap.getParameter().get(0)).toBigInteger());
            break;

          case "uint8[]":
            List<Uint8> uint8Arry = new ArrayList<>();
            for (Integer j = 0; j < inputParamMap.getParameter().size(); j++) {
              uint8Arry.add(
                  new Uint8(new BigDecimal(inputParamMap.getParameter().get(j)).toBigInteger()));
            }
            DynamicArray<Uint8> uint8s = new DynamicArray<>(Uint8.class, uint8Arry);
            inputs[i] = uint8s;
            break;

          case "uint32":
            inputs[i] =
                new Uint32(
                    new BigDecimal((String) inputParamMap.getParameter().get(0)).toBigInteger());
            break;

          case "uint32[]":
            List<Uint32> uint32Arry = new ArrayList<>();
            for (Integer j = 0; j < inputParamMap.getParameter().size(); j++) {
              uint32Arry.add(
                  new Uint32(
                      new BigDecimal((String) inputParamMap.getParameter().get(j)).toBigInteger()));
            }
            DynamicArray<Uint32> uint32s = new DynamicArray<>(Uint32.class, uint32Arry);
            inputs[i] = uint32s;
            break;

          case "uint128":
            inputs[i] =
                new Uint128(
                    new BigDecimal((String) inputParamMap.getParameter().get(0)).toBigInteger());
            break;

          case "uint128[]":
            List<Uint128> uint128Arry = new ArrayList<>();
            for (Integer j = 0; j < inputParamMap.getParameter().size(); j++) {
              uint128Arry.add(
                  new Uint128(
                      new BigDecimal((String) inputParamMap.getParameter().get(j)).toBigInteger()));
            }
            DynamicArray<Uint128> uint128s = new DynamicArray<>(Uint128.class, uint128Arry);
            inputs[i] = uint128s;
            break;

          case "uint256":
            inputs[i] =
                new Uint256(
                    new BigDecimal((String) inputParamMap.getParameter().get(0)).toBigInteger());
            break;

          case "uint256[]":
            List<Uint256> uint256Arry = new ArrayList<>();
            for (Integer j = 0; j < inputParamMap.getParameter().size(); j++) {
              uint256Arry.add(
                  new Uint256(
                      new BigDecimal((String) inputParamMap.getParameter().get(j)).toBigInteger()));
            }
            DynamicArray<Uint256> uint256s = new DynamicArray<>(Uint256.class, uint256Arry);
            inputs[i] = uint256s;
            break;

          case "bytes32":
            inputs[i] =
                new Bytes32(
                    Hash.sha3(
                        ((String) inputParamMap.getParameter().get(0))
                            .getBytes(StandardCharsets.UTF_8)));
            break;

          case "bytes32[]":
            List<Bytes32> bytes32Arry = new ArrayList<>();
            for (Integer j = 0; j < inputParamMap.getParameter().size(); j++) {
              byte[] byteArry =
                  Hash.sha3(
                      ((String) inputParamMap.getParameter().get(0))
                          .getBytes(StandardCharsets.UTF_8));
              Bytes32 bytes32 = new Bytes32(byteArry);
              bytes32Arry.add(bytes32);
            }
            DynamicArray<Bytes32> bytes32s = new DynamicArray<>(Bytes32.class, bytes32Arry);
            inputs[i] = bytes32s;
            break;

          case "bytes":
            inputs[i] = new DynamicBytes(((String) inputParamMap.getParameter().get(0)).getBytes());
            break;

          case "bytes[]":
            List<DynamicBytes> bytesArry = new ArrayList<>();
            for (Integer j = 0; j < inputParamMap.getParameter().size(); j++) {
              byte[] byteArry =
                  Hash.sha3(
                      ((String) inputParamMap.getParameter().get(0))
                          .getBytes(StandardCharsets.UTF_8));
              DynamicBytes bytes32 = new DynamicBytes(byteArry);
              bytesArry.add(bytes32);
            }
            DynamicArray<DynamicBytes> bytess = new DynamicArray<>(DynamicBytes.class, bytesArry);
            inputs[i] = bytess;
            inputs[i] = new DynamicBytes(((String) inputParamMap.getParameter().get(0)).getBytes());
            break;

          default:
            throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);
        }
        i++;
      }
      return inputs;
    }
    return null;
  }

  private List<TypeReference<?>> parseOutputParams(
      List<Java2ContractTypeConcert> functionOutputParams) {
    List<TypeReference<?>> outputs = new ArrayList<>();
    if (functionOutputParams != null) {
      for (Java2ContractTypeConcert outputParam : functionOutputParams) {
        switch (outputParam.getType()) {
          case "address":
            outputs.add(new TypeReference<org.web3j.abi.datatypes.Address>() {});
            break;

          case "address[]":
            // TODO:
            throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

          case "string":
            outputs.add(new TypeReference<Utf8String>() {});
            break;

          case "string[]":
            // TODO:
            throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

          case "uint8":
            outputs.add(new TypeReference<Uint8>() {});
            break;

          case "uint8[]":
            // TODO:
            throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

          case "uint128":
            outputs.add(new TypeReference<Uint128>() {});
            break;

          case "uint128[]":
            // TODO:
            throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

          case "uint256":
            outputs.add(new TypeReference<Uint256>() {});
            break;

          case "uint256[]":
            // TODO:
            throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

          case "bytes32":
            outputs.add(new TypeReference<Bytes32>() {});
            break;

          case "bytes32[]":
            // TODO:
            throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

          case "bytes":
            outputs.add(new TypeReference<DynamicBytes>() {});
            break;
          default:
            throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);
        }
      }
    }
    return outputs;
  }

  private ContractCall createContractCall(String chainEnv, Address contractAddress) {
    ContractCall contractCall = null;
    if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
      contractCall = new ContractCall(web3, contractAddress);
    } else if (ChainEnv.TEST.getCode().equals(chainEnv)) {
      contractCall = new ContractCall(web3Test, contractAddress);
    } else {
      throw new BusinessException(ResponseCode.NOT_SUPPORTED_ChAIN_ENV);
    }
    return contractCall;
  }

  private String convertAddress(String chainEnv, String hexAddress) {
    String address = null;
    if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
      address = new Address(hexAddress, 1029).toString();
    } else if (ChainEnv.TEST.getCode().equals(chainEnv)) {
      address = new Address(hexAddress, 1).toString();
    } else {
      throw new BusinessException(ResponseCode.NOT_SUPPORTED_ChAIN_ENV);
    }
    return address;
  }
}
