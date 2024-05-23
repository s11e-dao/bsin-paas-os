package me.flyray.bsin.blockchain.bsc;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.flyray.bsin.blockchain.enums.ChainEnv;
import me.flyray.bsin.blockchain.utils.Java2ContractTypeParameter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.*;
import org.web3j.crypto.*;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.enums.ChainType;
import me.flyray.bsin.blockchain.utils.Java2ContractTypeConcert;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.exception.BusinessException;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import static java.lang.Thread.sleep;
import static me.flyray.bsin.constants.ResponseCode.*;

/**
 * @author bolei
 * @date 2023/8/2 19:58
 * @desc
 */
@Slf4j
@Component
public class BscBiz implements BsinBlockChainEngine {
  private String ipfsGateway;
  private String privateKey;
  private String fromAddress;
  private String testnetEndpointAddress;
  private String mainnetEndpointAddress;
  private String chainType;
  private Web3j web3;
  private Web3j web3Test;
  private BigInteger gasLimit;
  private BigInteger storageLimit;
  private BigInteger gasPrice;
  private BigInteger value = BigInteger.ZERO;
  private BigInteger epochHeight;
  private long chainId;
  private long chainIdTset;

  public BscBiz() {
    //        this.testnetEndpointAddress = "https://data-seed-prebsc-1-s2.bnbchain.org:8545";
    this.testnetEndpointAddress = "https://rpc.ankr.com/bsc_testnet_chapel";
    this.mainnetEndpointAddress = "https://bsc-dataseed.bnbchain.org";
    this.privateKey = "6e88746088c5d054f983131babd915eac0ea2764879c5b732044bfed903fe70c";
    this.fromAddress = "0x362919Fa458d7a2282d88C77500810654741241b";
    //        this.web3Test = Web3.create(this.confluxRpcUrlTest, 3, 10000);
    //        this.web3 = Web3.create(this.confluxRpcUrlMain, 3, 10000);
    this.web3 = Web3j.build(new HttpService(mainnetEndpointAddress));
    this.web3Test = Web3j.build(new HttpService(testnetEndpointAddress));
    chainType = ChainType.BSC.getCode();

    chainIdTset = 56; // TestChain
    chainId = 97; // main
  }

  private Map<String, Object> loadWallet(String password, String json)
      throws IOException,
          CipherException,
          InvalidAlgorithmParameterException,
          NoSuchAlgorithmException,
          NoSuchProviderException {

    Credentials credentials = WalletUtils.loadJsonCredentials(password, json);
    String address = credentials.getAddress();
    BigInteger publicKey = credentials.getEcKeyPair().getPublicKey();
    BigInteger privateKey = credentials.getEcKeyPair().getPrivateKey();

    String sPrivatekeyInHex = privateKey.toString(16);
    log.info(sPrivatekeyInHex);
    log.info("address: " + address);
    log.info("publicKey: " + publicKey);
    log.info("hex privateKey: " + sPrivatekeyInHex);
    log.info("decimal privateKey: " + privateKey);

    Map<String, Object> map = new HashMap<>();
    map.put("address", address);
    map.put("publicKey", String.valueOf(publicKey));
    map.put("privateKey", sPrivatekeyInHex);
    return map;
  }

  @Override
  public Map<String, Object> createWallet(String password, String chainEnv) throws Exception {
    // 返回参数
    Map<String, Object> result = new HashMap<>();
    WalletFile walletFile;
    ECKeyPair ecKeyPair = Keys.createEcKeyPair();
    walletFile = Wallet.createStandard(password, ecKeyPair);
    ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    String jsonStr = objectMapper.writeValueAsString(walletFile);
    log.info("keystore json file " + jsonStr);
    // 钱包信息
    result = loadWallet(password, jsonStr);
    return result;
  }

  @Override
  public Map<String, Object> createAccount(
      String chainEnv, String privateKey, String gasPrice, String gasLimit, String value) {
    Map map = new HashMap();
    //        Account.Option opt = new Account.Option();
    //        Account account;
    //        if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
    //            account = Account.create(web3, privateKey);
    //            opt.withChainId(1029);
    //        } else {
    //            account = Account.create(web3Test, privateKey);
    //            opt.withChainId(1);
    //        }
    //        if (gasPrice != null) {
    //            opt.withGasPrice(new BigInteger(gasPrice));
    //        }
    //        // gasLimit和storageLimit为空时使用estimateGasAndCollateral 自动预估
    //        if (gasLimit != null) {
    //            opt.withGasLimit(new BigInteger(gasLimit));
    //        }
    //        map.put("opt", opt);
    //        map.put("account", account);
    return map;
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
    try {
      Credentials credentials = Credentials.create(privateKey);
      String signAddressStr = credentials.getAddress();
      Type[] inputs = parseInputParams(functionInputParams);

      // 1.构造参数
      String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.asList(inputs));

      // 2.获取nounce
      BigInteger nonce = getNonce(chainEnv, signAddressStr);
      System.out.println("nounct: " + nonce);

      // 3. value赋值
      BigInteger valueInterger = new BigInteger("0");
      if (value != null) {
        valueInterger = new BigInteger(value);
      }
      System.out.println("value: " + valueInterger);

      // 4.设置gasPrice和gasLimit
      BigInteger gasPriceInteger;
      BigInteger gasLimitInteger;
      if (gasPrice == null) {
        EthGasPrice ethGasPrice;
        if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
          ethGasPrice = web3.ethGasPrice().sendAsync().get();
        } else {
          ethGasPrice = web3Test.ethGasPrice().sendAsync().get();
        }
        gasPriceInteger = ethGasPrice.getGasPrice();
      } else {
        gasPriceInteger = new BigInteger(gasPrice);
      }
      System.out.println("gasPrice: " + gasPriceInteger);
      if (gasLimit == null) {
        org.web3j.protocol.core.methods.request.Transaction transaction =
            org.web3j.protocol.core.methods.request.Transaction.createContractTransaction(
                signAddressStr, nonce, gasPriceInteger, byteCode + encodedConstructor);
        gasLimitInteger = getTransactionGasLimit(chainEnv, transaction);
      } else {
        gasLimitInteger = new BigInteger(gasLimit);
      }
      System.out.println("gasLimit: " + gasLimitInteger);

      // 5.创建RawTransaction交易对象
      RawTransaction rawTransaction =
          RawTransaction.createContractTransaction(
              nonce,
              gasPriceInteger,
              gasLimitInteger,
              valueInterger,
              byteCode + encodedConstructor);

      // 6.签名Transaction
      byte[] signMessage =
          TransactionEncoder.signMessage(
              rawTransaction, getChainId(chainEnv).longValue(), credentials);
      String hexValue = Numeric.toHexString(signMessage);

      // 7.发送交易
      Map ret = sendRawTransaction(hexValue, chainEnv, 120000);
      result.put("receipt", ret.get("receipt"));
      result.put("txHash", ret.get("txHash"));
      result.put("contractAddress", ret.get("contractAddress"));
      return result;
    } catch (Exception e) {
      log.error(e.toString());
      throw new BusinessException("999999", e.toString());
    }
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
  public BigInteger getNonce(String chainEnv, String address) {
    try {
      EthGetTransactionCount getNonce;
      if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
        //                getNonce = web3.ethGetTransactionCount(address,
        // DefaultBlockParameterName.PENDING).send();
        getNonce = web3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
      } else {
        getNonce =
            web3Test.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
      }
      if (getNonce == null) {
        throw new RuntimeException("net error");
      }
      return getNonce.getTransactionCount();
    } catch (IOException e) {
      throw new RuntimeException("net error");
    }
  }

  @Override
  public Map<String, Object> sendRawTransaction(String rawData, String chainEnv, int timeOut)
      throws Exception {
    Map result = new HashMap();
    EthSendTransaction ethSendTransaction;
    String txHash;
    EthGetTransactionReceipt transactionReceipt;
    int loop = 0;
    int delay = timeOut / 1000;
    int count = timeOut % 1000;
    if (delay <= 0) {
      delay = 10;
    }
    if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
      ethSendTransaction = web3.ethSendRawTransaction(rawData).sendAsync().get();
      txHash = ethSendTransaction.getTransactionHash();
      if (txHash == null) {
        throw new BusinessException(HASH_NON_EXISTENT);
      }
      transactionReceipt = web3.ethGetTransactionReceipt(txHash).send();

      while (!transactionReceipt.getTransactionReceipt().isPresent()) {
        sleep(delay);
        loop++;
        if (loop > count) {
          loop = 0;
          throw new BusinessException(TRANSACTION_CONFIRMED_TIMEOUT);
        }
        transactionReceipt = web3.ethGetTransactionReceipt(txHash).send();
      }
    } else {
      ethSendTransaction = web3Test.ethSendRawTransaction(rawData).sendAsync().get();
      txHash = ethSendTransaction.getTransactionHash();
      if (txHash == null) {
        throw new BusinessException(HASH_NON_EXISTENT);
      }
      transactionReceipt = web3Test.ethGetTransactionReceipt(txHash).send();
      while (!transactionReceipt.getTransactionReceipt().isPresent()) {
        sleep(delay);
        loop++;
        if (loop > count) {
          loop = 0;
          throw new BusinessException(TRANSACTION_CONFIRMED_TIMEOUT);
        }
        transactionReceipt = web3Test.ethGetTransactionReceipt(txHash).send();
      }
    }
    result.put("receipt", transactionReceipt.getTransactionReceipt());
    result.put("txHash", txHash);
    result.put(
        "contractAddress", transactionReceipt.getTransactionReceipt().get().getContractAddress());
    System.out.println(txHash);
    return result;
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
      Credentials credentials = Credentials.create(privateKey);
      String signAddressStr = credentials.getAddress();

      Type[] inputs = parseInputParams(functionInputParams);

      // 返回值不为空，先用ContractCall模拟调用获取返回值
      if (functionReturnType != null) {
        Map<String, Object> contractRWret =
            contractRead(
                chainEnv, contractAddress, method, functionReturnType, functionInputParams, 60000);
        result.put(
            functionReturnType.getParameterList().get(0).getType(),
            contractRWret.get(functionReturnType.getParameterList().get(0).getType()));
      }

      // 1.创建Function对象，调用智能合约transfer方法
      Function function;
      if (inputs == null) {
        function =
            new Function(
                method, Collections.EMPTY_LIST, Arrays.asList(new TypeReference<Type>() {}));
      } else {
        function =
            new Function(
                method, Arrays.asList(inputs), Arrays.asList(new TypeReference<Type>() {}));
      }
      String encodedFunction = FunctionEncoder.encode(function);

      // 2.获取nounce
      BigInteger nonce = getNonce(chainEnv, signAddressStr);
      System.out.println("nounct: " + nonce);

      // 3. value赋值
      BigInteger valueInterger = new BigInteger("0");
      if (value != null) {
        valueInterger = new BigInteger(value);
      }
      System.out.println("value: " + valueInterger);

      // 4.设置gasPrice和gasLimit
      BigInteger gasPriceInteger;
      BigInteger gasLimitInteger;
      if (gasPrice == null) {
        EthGasPrice ethGasPrice;
        if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
          ethGasPrice = web3.ethGasPrice().sendAsync().get();
        } else {
          ethGasPrice = web3Test.ethGasPrice().sendAsync().get();
        }
        gasPriceInteger = ethGasPrice.getGasPrice();
      } else {
        gasPriceInteger = new BigInteger(gasPrice);
      }
      System.out.println("gasPrice: " + gasPriceInteger);

      if (gasLimit == null) {
        //                org.web3j.protocol.core.methods.request.Transaction transaction = new
        // org.web3j.protocol.core.methods.request.Transaction(
        //                        fromAddress, null, null, null, contractAddress, null, data);
        org.web3j.protocol.core.methods.request.Transaction transaction =
            org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                signAddressStr, contractAddress, encodedFunction);
        gasLimitInteger = getTransactionGasLimit(chainEnv, transaction);
      } else {
        gasLimitInteger = new BigInteger(gasLimit);
      }
      System.out.println("gasLimit: " + gasLimitInteger);

      // 5.创建RawTransaction交易对象
      //            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce,
      // DefaultGasProvider.GAS_PRICE,DefaultGasProvider.GAS_LIMIT, contractAddress,
      // valueInterger,encodedFunction);
      RawTransaction rawTransaction =
          RawTransaction.createTransaction(
              nonce,
              gasPriceInteger,
              gasLimitInteger,
              contractAddress,
              valueInterger,
              encodedFunction);

      // 6.签名Transaction
      byte[] signMessage;
      signMessage =
          TransactionEncoder.signMessage(
              rawTransaction, getChainId(chainEnv).longValue(), credentials);
      String hexValue = Numeric.toHexString(signMessage);

      // 7.发送交易检查hash
      Map ret = sendRawTransaction(hexValue, chainEnv, 60000);
      result.put("receipt", ret.get("receipt"));
      result.put("txHash", ret.get("txHash"));
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
    return null;
  }

  // https://blog.csdn.net/qq_36838406/article/details/118386159?utm_source=miniapp_weixin
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
    //        Type[] outputs = parseInputParams(functionReturnType);
    // 1.创建Function对象，调用智能合约transfer方法
    Function function;
    if (inputs == null) {
      function =
          new Function(
              method, Collections.EMPTY_LIST, Arrays.asList(new TypeReference<Uint256>() {}));
    } else {
      function =
          new Function(
              method, Arrays.asList(inputs), Arrays.asList(new TypeReference<Uint256>() {}));
    }
    String encodedFunction = FunctionEncoder.encode(function);

    org.web3j.protocol.core.methods.response.EthCall response;
    if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
      response =
          web3.ethCall(
                  org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                      null, contractAddress, encodedFunction),
                  DefaultBlockParameterName.LATEST)
              .sendAsync()
              .get();
    } else {
      response =
          web3Test
              .ethCall(
                  org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                      null, contractAddress, encodedFunction),
                  DefaultBlockParameterName.LATEST)
              .sendAsync()
              .get();
    }
    Assert.isNull(response.getError(), "callContractTransaction error");
    List<Type> results =
        FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
    for (Type ret : results) {
      System.out.println(ret.getValue());
    }

    String ret = null;
    switch (functionReturnType.getParameterList().get(0).getType()) {
      case "address":
        String addresHex = null;
        ret = results.get(0).toString();
        break;

      case "address[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "string":
        ret = results.get(0).toString();
        break;

      case "string[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "bool":
        ret = results.get(0).toString();
        break;

      case "bool[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "uint8":
        ret = results.get(0).toString();
        break;

      case "uint8[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "uint32":
        ret = results.get(0).toString();
        break;

      case "uint32[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "uint128":
        ret = results.get(0).toString();
        break;

      case "uint128[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "uint256":
        ret = results.get(0).toString();
        break;

      case "uint256[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);

      case "bytes32":
        ret = results.get(0).toString();
        break;

      case "bytes32[]":
        // TODO:
        throw new BusinessException(ResponseCode.SODILITY_TYPE_CONCERT_ERROR);
      case "bytes":
        ret = results.get(0).toString();
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
      String methodId,
      Java2ContractTypeParameter functionReturnType,
      Java2ContractTypeParameter functionInputParams,
      int timeOut)
      throws Exception {
    return null;
  }

  @Override
  public Map<String, Object> getTransaction(String chainEnv, String txHash, int timeOut)
      throws Exception {
    int loop = 0;
    int delay = timeOut / 1000;
    int count = timeOut % 1000;
    if (delay <= 0) {
      delay = 10;
    }
    // 通过hash获取交易
    Optional<Transaction> transactions;
    if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
      transactions = web3.ethGetTransactionByHash(txHash).send().getTransaction();
      while (!transactions.isPresent()) {
        sleep(delay);
        loop++;
        if (loop > count) {
          loop = 0;
          throw new BusinessException(HASH_NON_EXISTENT);
        }
        transactions = web3.ethGetTransactionByHash(txHash).send().getTransaction();
      }
    } else {
      transactions = web3Test.ethGetTransactionByHash(txHash).send().getTransaction();
      while (!transactions.isPresent()) {
        sleep(delay);
        loop++;
        if (loop > count) {
          loop = 0;
          throw new BusinessException(HASH_NON_EXISTENT);
        }
        transactions = web3Test.ethGetTransactionByHash(txHash).send().getTransaction();
      }
    }
    Map result = new HashMap();
    Transaction transactionInfo = transactions.get();
    result.put("transaction", JSONObject.toJSON(transactionInfo));
    System.out.println(JSONObject.toJSON(transactionInfo));
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
    return null;
  }

  @Override
  public Map<String, Object> removeWhiteList(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @Override
  public String getAddress(String chainEnv, String privateKey) {
    //        Account account;
    //        if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
    //            account = Account.create(web3, privateKey);
    //        } else {
    //            account = Account.create(web3Test, privateKey);
    //        }
    //        return account.getAddress().toString();
    return null;
  }

  @Override
  public String getPrivateKey() {
    return privateKey;
  }

  public BigInteger getTransactionGasLimit(
      String chainEnv, org.web3j.protocol.core.methods.request.Transaction transaction) {
    try {
      EthEstimateGas ethEstimateGas;
      if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
        ethEstimateGas = web3.ethEstimateGas(transaction).send();
      } else {
        ethEstimateGas = web3Test.ethEstimateGas(transaction).send();
      }
      if (ethEstimateGas.hasError()) {
        throw new RuntimeException(ethEstimateGas.getError().getMessage());
      }
      return ethEstimateGas.getAmountUsed();
    } catch (IOException e) {
      throw new RuntimeException("net error");
    }
  }

  @Override
  public String getBalance(String chainEnv, String address) throws Exception {
    try {
      EthGetBalance ethGetBalance;
      if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
        ethGetBalance = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
      } else {
        ethGetBalance = web3Test.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
      }
      return Convert.fromWei(new BigDecimal(ethGetBalance.getBalance()), Convert.Unit.ETHER)
          .toString();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
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
    return this.ipfsGateway;
  }

  @Override
  public String getZeroAddress(String chainEnv) {
    return "0x0000000000000000000000000000000000000000";
  }

  @Override
  public String getWeb3ClientVersion(String chainEnv) {
    return null;
  }

  @Override
  public long getBlockHeight(String chainEnv) {
    EthBlockNumber blockNumber = null;
    try {
      if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
        blockNumber = web3.ethBlockNumber().send();
      } else {
        blockNumber = web3Test.ethBlockNumber().send();
      }
      long blockHeight = blockNumber.getBlockNumber().longValue();
      System.out.println(blockHeight);
      return blockHeight;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  @Override
  public BigInteger getChainId(String chainEnv) {
    BigInteger chainId = null;
    try {
      EthChainId ethChainId;
      if (ChainEnv.MAIN.getCode().equals(chainEnv)) {
        ethChainId = web3.ethChainId().sendAsync().get();
        chainId = ethChainId.getChainId();
      } else {
        ethChainId = web3Test.ethChainId().sendAsync().get();
        chainId = ethChainId.getChainId();
      }
      System.out.println(chainId);
      return chainId;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return chainId;
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

  private Type[] parseInputParams(List<Java2ContractTypeConcert> functionInputParams) {
    if (functionInputParams != null) {
      int inputLength = functionInputParams.size();
      Type[] inputs = new Type[inputLength];
      int i = 0;
      for (Java2ContractTypeConcert inputParam : functionInputParams) {
        switch (inputParam.getType()) {
          case "address":
            inputs[i] = new Address((String) inputParam.getValue().get("0"));
            break;

          case "address[]":
            List<org.web3j.abi.datatypes.Address> addressArry = new ArrayList<>();
            for (Integer j = 0; j < inputParam.getValue().size(); j++) {
              addressArry.add(new Address((String) inputParam.getValue().get(j.toString())));
            }
            DynamicArray<Address> addtrsss =
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
            inputs[i] = new Bool(Boolean.parseBoolean((String) inputParam.getValue().get("0")));
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
            inputs[i] = new Address((String) inputParamMap.getParameter().get(0));
            break;

          case "address[]":
            List<org.web3j.abi.datatypes.Address> addressArry = new ArrayList<>();
            for (Integer j = 0; j < inputParamMap.getParameter().size(); j++) {
              addressArry.add(new Address((String) inputParamMap.getParameter().get(j)));
            }
            DynamicArray<Address> addtrsss =
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
}
