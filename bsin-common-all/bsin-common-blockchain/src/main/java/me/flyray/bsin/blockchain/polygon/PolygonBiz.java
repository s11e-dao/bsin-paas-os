package me.flyray.bsin.blockchain.polygon;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.flyray.bsin.blockchain.utils.Java2ContractTypeParameter;
import org.springframework.stereotype.Component;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.enums.ChainType;
import me.flyray.bsin.blockchain.utils.Java2ContractTypeConcert;

/**
 * @author bolei
 * @date 2023/6/26 17:47
 * @desc
 */
@Slf4j
@Component
public class PolygonBiz implements BsinBlockChainEngine {

  private String ipfsGateway;

  private String privateKey;

  // 设置需要的矿工费
  BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L);
  BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);

  private static final String WALLET_PASSWORD = "";
  private static final BigInteger ACCOUNT_UNLOCK_DURATION = BigInteger.valueOf(30);

  private static final int SLEEP_DURATION = 15000;
  private static final int ATTEMPTS = 10;

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
  long chainId = 137;
  long chainIdTest = 80001;

  public PolygonBiz() {
    //        this.testnetEndpointAddress = "https://rpc.ankr.com/polygon_mumbai";
    this.testnetEndpointAddress = "https://rpc-mumbai.maticvigil.com";
    this.mainnetEndpointAddress = "https://rpc.ankr.com/polygon";
    //        this.privateKey = "54818949dd977e2ec85c00522ed3996936eebe83d621a635fcd032c3e8bf65bf";
    //        this.fromAddress = "0x362919Fa458d7a2282d88C77500810654741241b";

    this.privateKey = "744491fd10127b82c7256051c96dfdd316ab718b3c173bce2c910b2a5ea0ca";
    this.fromAddress = "0xC122a281b2E211B8dCB586492033667a678d14C5";

    this.web3 = Web3j.build(new HttpService(mainnetEndpointAddress));
    this.web3Test = Web3j.build(new HttpService(testnetEndpointAddress));

    //        OkHttpClient.Builder builder = new OkHttpClient.Builder();
    //        builder.connectTimeout(30 * 1000, TimeUnit.MILLISECONDS);
    //        builder.writeTimeout(30 * 1000, TimeUnit.MILLISECONDS);
    //        builder.readTimeout(30 * 1000, TimeUnit.MILLISECONDS);
    //        OkHttpClient httpClient = builder.build();
    //        this.web3 = Web3j.build(new HttpService(mainnetEndpointAddress, httpClient, false));
    //        this.web3Test = Web3j.build(new HttpService(mainnetEndpointAddress, httpClient,
    // false));

    chainType = ChainType.POLYGON.getCode();
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
