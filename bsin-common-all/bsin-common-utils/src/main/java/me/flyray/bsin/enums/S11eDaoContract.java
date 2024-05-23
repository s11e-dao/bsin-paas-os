package me.flyray.bsin.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 合约类型
 */
public enum S11eDaoContract {

    /**
     * 1、S11eDao
     */
    S11eDao("S11eDao", "S11eDaoAddress", "S11eDao核心合约", "s11e-dao-"),

    /**
     * 2、S11eDaoFactory
     */
    S11eDaoFactory("S11eDaoFactory", "S11eDaoFactoryAddress", "S11eDao的工厂合约，创建dao", "s11e-dao-factory"),

    /**
     * 3、VaultsExtension
     */
    VaultsExtension("VaultsExtension", "VaultsExtensionAddress", "S11eDao的金库资产(NFT)管理和合约", "vaults-extension"),

    /**
     * 4、VaultsExtensionFactory
     */
    VaultsExtensionFactory("VaultsExtensionFactory", "VaultsExtensionFactoryAddress", "S11eDao的金库资产(NFT)管理和合约的工厂合约", "vaults-extension-factory"),


    /**
     * 5、TreasuryExtension
     */
    TreasuryExtension("TreasuryExtension", "TreasuryExtensionAddress", "S11eDao的国库资产(ERC20)管理和合约", "treasury-extension"),

    /**
     * 6、TreasuryExtensionFactory
     */
    TreasuryExtensionFactory("TreasuryExtensionFactory", "TreasuryExtensionFactoryAddress", "S11eDao的国库资产(ERC20)管理和合约的工厂合约", "treasury-extension-factory"),

    /**
     * 7、ERC20ContinuousTokenExtension
     */
    ERC20ContinuousTokenExtension("ERC20ContinuousTokenExtension", "ERC20ContinuousTokenExtensionAddress", "ERC20连续代币协议插件合约", "erc20-continuous-token-extension-"),

    /**
     * 8、ERC20ContinuousTokenExtensionFactory
     */
    ERC20ContinuousTokenExtensionFactory("ERC20ContinuousTokenExtensionFactory", "ERC20ContinuousTokenExtensionFactoryAddress", "ERC20连续代币协议插件合约的工厂合约", "erc20-continuous-token-extension-factory"),


    /**
     * 9、ERC721TokenExtension
     */
    ERC777TokenExtension("ERC777TokenExtension", "ERC777TokenExtensionAddress", "ERC777协议插件合约", "erc777-token-extension-"),

    /**
     * 10、ERC721TokenExtensionFactory
     */
    ERC777TokenExtensionFactory("ERC777TokenExtensionFactory", "ERC777TokenExtensionFactoryAddress", "ERC777协议插件合约的工厂合约", "erc777-token-extension-factory"),


    /**
     * 20、ERC721TokenExtension
     */
    ERC721TokenExtension("ERC721TokenExtension", "ERC721TokenExtensionAddress", "ERC721协议插件合约", "erc721-token-extension-"),

    /**
     * 21、ERC721TokenExtensionFactory
     */
    ERC721TokenExtensionFactory("ERC721TokenExtensionFactory", "ERC721TokenExtensionFactoryAddress", "ERC721协议插件合约的工厂合约", "erc721-token-extension-factory"),


    /**
     * 30、ERC1155TokenExtension
     */
    ERC1155TokenExtension("ERC1155TokenExtension", "ERC1155TokenExtensionAddress", "ERC1155协议插件合约", "erc1155-token-extension-"),

    /**
     * 31、ERC1155TokenExtensionFactory
     */
    ERC1155TokenExtensionFactory("ERC1155TokenExtensionFactory", "ERC1155TokenExtensionFactoryAddress", "ERC1155协议插件合约的工厂合约", "erc1155-token-extension-factory"),


    /**
     * 40、ERC3525TokenExtension
     */
    ERC3525TokenExtension("ERC3525TokenExtension", "ERC3525TokenExtensionAddress", "ERC3525协议插件合约", "erc3525-token-extension-"),

    /**
     * 41、ERC3525TokenExtensionFactory
     */
    ERC3525TokenExtensionFactory("ERC3525TokenExtensionFactory", "ERC3525TokenExtensionFactoryAddress", "ERC3525协议插件合约的工厂合约", "erc3525-token-extension-factory"),


    /**
     * 50、ERC721TokenExtensionS11eDaoProfiles
     */
    ERC721TokenExtensionS11eDaoProfiles("ERC721TokenExtensionS11eDaoProfiles", "erc721-token-extension-s11e-dao-profilesAddress", "S11eDao ERC721协议的dao-profiles合约", "erc721-token-extension-s11e-dao-profiles"),

    /**
     * 51、ERC721TokenExtensionS11eMemberPFP
     */
    ERC721TokenExtensionS11eMemberPFP("ERC721TokenExtensionS11eMemberPFP", "erc721-token-extension-s11e-member-pfpAddress", "S11eDao ERC721协议的member-pfp合约", "erc721-token-extension-s11e-member-pfp"),

    /**
     * 60、HoldShareOnboardingWrapper
     */
    HoldShareOnboardingWrapper("HoldShareOnboardingWrapper", "HoldShareOnboardingWrapperAddress", "S11eDao 的持仓入职装饰器合约", "hold-share-onboarding-wrapper"),


    /**
     * 100、VaultsAsset
     */
    VaultsAsset("VaultsAsset", "VaultsAsset", "金库资产类合约", "vaults-assets-"),


    /**
     * 200、TreasuryAsset
     */
    TreasuryAsset("TreasuryAsset", "TreasuryAsset", "国库资产类合约", "treasury-assets-"),


    /**
     * 300、Extension
     */
    Extension("Extension", "Extension", "插件资产类合约", "extension-"),


    /**
     * 400、Core
     */
    Core("Core", "Core", "dao核心合约", "core-"),


    /**
     * 500、Wrapper
     */
    Wrapper("Wrapper", "Wrapper", "业务装饰器类合约", "wrapper-"),


    /**
     * 600、Factory
     */
    Factory("Factory", "Factory", "工厂类合约", "factory-"),


    /**
     * 700、Trade
     */
    Trade("Trade", "Trade", "工厂类合约", "trade-"),


    /**
     * 800、Test
     */
    Test("Test", "Test", "测试类合约", "test-"),


    /**
     * 1000、Zero
     */
    Zero("Zero", "Zero", "零地址/黑洞", "zero-");

    private String name;

    private String address;


    private String desc;


    private String contractPrefix;          //使用工厂合约创建的合约命名的前坠

    S11eDaoContract(String name, String address, String desc, String contractPrefix) {
        this.name = name;
        this.address = address;
        this.desc = desc;
        this.contractPrefix = contractPrefix;
    }

    public String getName() {
        return name;
    }

//    public String getAddress() {
//        return address;
//    }

    public String getDesc() {
        return desc;
    }

    public String getContractPrefix() {
        return contractPrefix;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setContractPrefix(String contractPrefix) {
        this.contractPrefix = contractPrefix;
    }


    /**
     * Json 枚举序列化
     */
    @JsonCreator
    public static S11eDaoContract getInstanceById(Integer id) {
        if (id == null) {
            return null;
        }
        for (S11eDaoContract status : values()) {
            if (id.equals(status.getName())) {
                return status;
            }
        }
        return null;
    }

    @JsonCreator
    public static S11eDaoContract getS11eDaoFactoryContractName(String name) {
        if (name == null) {
            return null;
        }
        for (S11eDaoContract status : values()) {
            if (status.getName().contains(name) && status.getName().contains("Factory")) {
                return status;
            }
        }
        return null;
    }

    @JsonCreator
    public static S11eDaoContract getS11eDaoExtensionContractName(String name) {
        if (name == null) {
            return null;
        }
        for (S11eDaoContract status : values()) {
            if (status.getName().contains(name) && !status.getName().contains("Factory")) {
                return status;
            }
        }
        return null;
    }

}
