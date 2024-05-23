package me.flyray.bsin.blockchain.protocol;

import java.util.Map;

/**
 * @author ：leonard
 * @date ：Created in 2022/12/24 21：00
 * @description： s11e protocol 协议接口
 * @modified By：
 */

public interface S11eProtocolService {


    /***************************************************
     * 读取链上数据
     **************************************************/
    /**
     * 1、getS11eDaoAddress: 获取 S11eDao 的合约地址
     */
    public Map<String, Object> getS11eDaoAddress(Map<String, Object> requestMap) throws Exception;

    /**
     * 2、getS11eDaoFactoryAddress: 获取 S11eDaoFactory 的合约地址
     */
    public Map<String, Object> getS11eDaoFactoryAddress(Map<String, Object> requestMap) throws Exception;


    /**
     * 3、getERC721TokenExtensionFactoryAddress: 获取 gERC721TokenExtensionFactory 的合约地址
     */
    public Map<String, Object> getERC721TokenExtensionFactoryAddress(Map<String, Object> requestMap) throws Exception;


    /**
     * 4、getERC1155TokenExtensionFactoryAddress: 获取 gERC1155TokenExtensionFactory 的合约地址
     */
    public Map<String, Object> getERC1155TokenExtensionFactoryAddress(Map<String, Object> requestMap) throws Exception;


    //TODO: getS11eDaoAllFactoryAddress 所有Factory地址


    /**
     * 4、getS11eDaoAllContractAddress: 获取 s11edao 所有的合约地址
     */
    public Map<String, Object> getS11eDaoAllContractAddress(Map<String, Object> requestMap) throws Exception;

    /**
     * 4、getDaoAllContractAddress: 获取 dao组织所部属的所有的合约地址
     */
    public Map<String, Object> getDaoAllContractAddress(Map<String, Object> requestMap) throws Exception;


    /**
     * 100、getDaoAddress: 调用 S11eDaoFactory 合约，通过 dao 的名称获取 改dao组织的合约地址
     */
    public Map<String, Object> getDaoAddress(Map<String, Object> requestMap) throws Exception;

    /**
     * 100、daoContract: 调用 daoContract 合约，通过 插件的名称获取 改dao组织的插件合约地址
     */
    public Map<String, Object> getDaoWrapperOrExtensionAddressByName(Map<String, Object> requestMap) throws Exception;


    /***************************************************
     * 写入链上数据
     **************************************************/
    /**
     * 1、create dao: 调用 S11eDaoFactory 合约，clone模式创建一个 dao
     */
    public Map<String, Object> createDao(Map<String, Object> requestMap) throws Exception;


    /**
     * 10、config dao: 配置 HoldShareOnboardingWrapper 持仓加入dao的参数
     */
    public Map<String, Object> holdShareOnboardingConfigDao(Map<String, Object> requestMap) throws Exception;

}
