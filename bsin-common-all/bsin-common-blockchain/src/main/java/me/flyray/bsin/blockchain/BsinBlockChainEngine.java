package me.flyray.bsin.blockchain;


import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import me.flyray.bsin.blockchain.utils.Java2ContractTypeConcert;
import me.flyray.bsin.blockchain.utils.Java2ContractTypeParameter;

/**
 * @author ：leonard
 * @date ：Created in 2022/12/24 21：00
 * @description： 区块链引擎 多链交互接口
 * 入参全部转成string类型
 * @modified By：
 */

public interface BsinBlockChainEngine {

    /**
     * 创建链上账户：根据输入的密码和网络类型
     * 返回地址和私钥
     */
    public Map<String, Object> createWallet(String password, String chainEnv) throws Exception;

    /**
     * 创建可签名的账户：根据输入网络类型、私钥、gasPrice、gasLimit
     * 返回可发起签名的账户
     */
    public Map<String, Object> createAccount(String chainEnv, String privateKey, String gasPrice, String gasLimit, String value);

    /**
     * 发送交易
     **/
    public Map<String, Object> sendRawTransaction(String rawData, String chainEnv, int timeOut) throws Exception;

    /**
     * 合约部署,无结构体构造擦参数
     **/
    public Map<String, Object> contractDeploy(String chainEnv,
                                              String privateKey,
                                              String gasPrice,
                                              String gasLimit,
                                              String value,
                                              String byteCode,
                                              Java2ContractTypeParameter functionReturnType,
                                              Java2ContractTypeParameter functionInputParams, int timeOut) throws Exception;


    /**
     * 原生代币转账
     **/
    public Map<String, Object> transfer(String chainEnv,
                                        String fromPrivateKey,
                                        String gasPrice,
                                        String gasLimit,
                                        String value,
                                        String toAddress, int timeOut) throws Exception;

    /**
     * 链上数据修改,普通数据类型参数
     **/
    public Map<String, Object> contractWrite(String chainEnv,
                                             String privateKey,
                                             String gasPrice,
                                             String gasLimit,
                                             String value,
                                             String contractAddress,
                                             String method,
                                             Java2ContractTypeParameter functionReturnType,
                                             Java2ContractTypeParameter functionInputParams, int timeOut) throws Exception;

    /**
     * 链上数据修改,结构体数据类型参数
     * @param methodId：initializeNecessaryExtension((bytes32,address,uint256,bytes32[],uint256[],address[],uint256[]))
     **/
    public Map<String, Object> contractWrite(String chainEnv,
                                             String privateKey,
                                             String gasPrice,
                                             String gasLimit,
                                             String value,
                                             String contractAddress,
                                             String method,
                                             String methodId,
                                             Java2ContractTypeParameter functionReturnType,
                                             Java2ContractTypeParameter functionInputParams, int timeOut) throws Exception;

    /**
     * 合约度读取,无结构体参数
     **/
    public Map<String, Object> contractRead(String chainEnv,
                                            String contractAddress,
                                            String method,
                                            Java2ContractTypeParameter functionReturnType,
                                            Java2ContractTypeParameter functionInputParams, int timeOut) throws Exception;


    /**
     * 合约度读取,有结构体参数
     **/
    public Map<String, Object> contractRead(String chainEnv,
                                            String contractAddress,
                                            String method,
                                            String methodId,
                                            Java2ContractTypeParameter functionReturnType,
                                            Java2ContractTypeParameter functionInputParams, int timeOut) throws Exception;

    /**
     * 获取账户的nounce值
     *
     * @param chainEnv
     * @param address
     * @return BigInteger
     */
    public BigInteger getNonce(String chainEnv, String address);


    /**
     * 根据交易hash获取交易信息
     *
     * @param txHash
     * @return
     */
    public Map<String, Object> getTransaction(String chainEnv, String txHash, int timeOut) throws Exception;

    /**
     * 获取赞助信息
     *
     * @param
     * @return
     */
    public Map<String, Object> getSponsor(Map<String, Object> requestMap) throws Exception;

    /**
     * 设置赞助
     *
     * @param
     * @return
     */
    public Map<String, Object> setSponsor(Map<String, Object> requestMap) throws Exception;

    /**
     * 添加代付白名单
     *
     * @param
     * @return
     */
    public Map<String, Object> addWhiteList(Map<String, Object> requestMap) throws Exception;


    /**
     * 查询是否是白名单
     *
     * @param
     * @return
     */
    public Map<String, Object> isWhitelisted(Map<String, Object> requestMap) throws Exception;


    /**
     * 移除代付白名单
     *
     * @param
     * @return
     */
    public Map<String, Object> removeWhiteList(Map<String, Object> requestMap) throws Exception;

    /**
     * 根据私钥获取公钥
     * 返回可发起签名的账户
     */
    public String getAddress(String chainEnv, String privateKey);

    /**
     * 获取发起签名的默认私钥
     */
    public String getPrivateKey();


    /**
     * 获取链代币余额
     */
    public String getBalance(String chainEnv, String address) throws Exception;


    /**
     * 获取链资产余额
     */
    public String getAssetBalance(String chainEnv, String contractAddress, String accountAddress, String assetProtocol, String tokenId) throws Exception;

    /**
     * 获取IPFS网关
     */
    public String getIpfsGateway();


    /**
     * 获取该网络下的零地址：
     * 返回零地址
     */
    public String getZeroAddress(String chainEnv);


    /**
     * 获取链相关参数:web3ClientVersion
     */
    public String getWeb3ClientVersion(String chainEnv);

    /**
     * 获取链相关参数:区块高度
     */
    public long getBlockHeight(String chainEnv);

    /**
     * 获取chainId
     *
     * @param chainEnv
     * @return BigInteger
     */
    public BigInteger getChainId(String chainEnv);

    /**
     * 根据国库获取dao的国库资产地址
     */

    public String getTreasuryContractAddress(String chainEnv, String daoAddressStr, String wrapperOrExtensionName, boolean byFactory, boolean isInsert);

    public String getVaultsContractAddress(String chainEnv, String daoAddressStr, String wrapperOrExtensionName, boolean byFactory, boolean isInsert);

    public String getDaoWrapperOrExtensionAddressByName(String chainEnv, String daoAddressStr, String wrapperOrExtensionName, boolean isInsert);

    public String getDaoNameByAddress(String chainEnv, String daoAddressStr, boolean isInsert);

    public String getDaoAddressByIndex(String chainEnv, String daoIndex, boolean isInsert);

    public String getDaoAddressByName(String chainEnv, String daoName, boolean isInsert);

}
