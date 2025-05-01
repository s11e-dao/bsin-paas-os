
const routes = [
  {
    path: '/',
    component: '@/layouts/index',
    routes: [
      {
        path: '/',
        redirect: '/home',
      },
      {
        name: '首页',
        path: '/home',
        component: '@/pages/Home/index',
        wrappers: [
          '@/wrappers/auth',
        ],
      },
      {
        name: '非基座运行',
        path: '/uncontainer',
        component: '@/pages/uncontainer'
      },
      // **************审核************
      {
        path: '/audit-center',
        redirect: '/audit-center/app-audit',
      },
      // api费用配置（应用审核）
      {
        path: '/audit-center/app-audit',
        component: '@/pages/ApiTxFeeConfig/index',
      },
      // **************平台************
      {
        path: '/platform',
        redirect: '/platform/platform-list',
      },
      // 平台列表
      {
        path: '/platform/platform-list',
        component: '@/pages/Platform/index',
      },
      // 平台接入应用
      {
        path: '/platform/platform-app',
        component: '@/pages/BizRoleApp/index',
      },
      // 平台服务订阅
      {
        path: '/platform/platform-service-subscribe',
        component: '@/pages/ServiceSubscribe/index',
      },
      // **************系统代理商************
      {
        path: '/sysAgent',
        redirect: '/sysAgent/sysAgent-list',
      },
      // 系统代理商列表
      {
        path: '/sysAgent/sysAgent-list',
        component: '@/pages/SysAgent/index',
      },
      // **************商户***************
      {
        path: '/merchant',
        redirect: '/merchant/merchant-list',
      },
      // 商户列表
      {
        path: '/merchant/merchant-list',
        component: '@/pages/Merchant/index',
      },
      {
        path: '/merchant/merchant-audit',
        component: '@/pages/Merchant/MerchantAudit/index',
      },
      // 商户认证
      {
        path: '/merchant/merchant-auth',
        component: '@/pages/Merchant/MerchantAuth/index',
      },
      // 商户接入应用：小程序、公众号、app
      {
        path: '/merchant/merchant-app',
        component: '@/pages/BizRoleApp/index',
      },
      // 商户服务订阅
      {
        path: '/merchant/service-subscribe',
        component: '@/pages/ServiceSubscribe/index',
      },
      // ******************线上商城********************
      {
        path: '/merchant/store-online',
        component: '@/pages/StoreOnline/index',
      },
      // ******************门店********************
      // 门店列表
      {
        path: '/merchant/store-list',
        component: '@/pages/Store/index',
      },
      // 门店账户
      {
        path: '/merchant/store-account',
        component: '@/pages/Store/StoreAccount/index',
      },
      // 门店员工
      {
        path: '/merchant/staff',
        component: '@/pages/Store/Staff/index',
      },
      // 门店设置
      {
        path: '/merchant/store-setting',
        component: '@/pages/Store/StoreSetting/index',
      },
      // **************************客户**************************
      {
        path: '/customer',
        redirect: '/customer/customer-list',
      },
      // 客户列表
      {
        path: '/customer/customer-list',
        component: '@/pages/Customer/index',
      },
      // 会员列表
      {
        path: '/customer/member-list',
        component: '@/pages/Customer/MemberList/index',
      },
      // 客户资产
      {
        path: '/customer/customer-assets',
        component: '@/pages/Customer/CustomerAssets/index',
      },
      // 会员卡列表
      {
        path: '/customer/pass-card',
        component: '@/pages/Customer/PassCard/index',
      },
      // 会员卡列表
      {
        path: '/customer/member-config',
        component: '@/pages/Customer/MemberConfig/index',
      },
      // ************************Profile***********************
      {
        path: '/profile',
        component: '@/pages/Profile/index',
      },
      // **************************业务角色等级管理**************************
      {
        path: '/grade-list',
        component: '@/pages/Grade/index',
      },
      // **************************权益条件管理**************************
      // 权益管理
      {
        path: '/equity-list',
        component: '@/pages/conditionAndEquity/EquityList/index',
      },
      // 条件管理
      {
        path: '/condition-list',
        component: '@/pages/conditionAndEquity/ConditionList/index',
      },
     
      // **************************钱包**************************
      {
        path: '/wallet',
        redirect: '/wallet/e-wallet',
      },
      // 电子钱包
      {
        path: '/wallet/e-wallet',
        component: '@/pages/Wallet/E-wallet/index',
      },
      // 链钱包
      {
        path: '/wallet/chain-wallet',
        component: '@/pages/Wallet/ChainWallet/index',
      },
      // 电子账户
      {
        path: '/wallet/account-list',
        component: '@/pages/Wallet/AccountList/index',
      },
      // 账户流水
      {
        path: '/wallet/account-journal',
        component: '@/pages/Wallet/AccountJournal/index',
      },
      // ****************************收银************************
      {
        path: '/cashier',
        component: '@/pages/Cashier/index',
      },
      // **************************币种*************************
      {
        path: '/currency-management',
        component: '@/pages/Currency/index',
      },
      // **************************数字资产*************************
      {
        path: '/assets',
        redirect: '/assets/assets-collection',
      },
      // 资产集合
      {
        path: '/assets/assets-collection',
        component: '@/pages/Assets/AssetsCollection/index',
      },
      // 资产列表
      {
        path: '/assets/assets-list',
        component: '@/pages/Assets/AssetsList/index',
      },
      // 数字积分
      {
        path: '/assets/digital-points',
        component: '@/pages/Assets/DigitalPoints/index',
      },
      // 联合曲线积分
      {
        path: '/assets/bonding-curve',
        component: '@/pages/Assets/BondingCurve/index',
      },
      // pass card
      {
        path: '/assets/pass-cards',
        component: '@/pages/Assets/PassCard/index',
      },
      // 资产元数据
      {
        path: '/assets/assets-metadata-list',
        component: '@/pages/Assets/MetadataList/index',
      },
      // 资产元数据模板
      {
        path: '/assets/metadata-template',
        component: '@/pages/Assets/MetadataTemplate/index',
      },
      // 资产铸造流水
      {
        path: '/assets/mint-journal',
        component: '@/pages/Assets/MintJournal/index',
      },
      // 资产转账流水
      {
        path: '/assets/transfer-journal',
        component: '@/pages/Assets/TransferJournal/index',
      },
      // 合约协议
      {
        path: '/assets/contract-protocol',
        component: '@/pages/Assets/ContractProtocol/index',
      },
      // 合约协议库
      {
        path: '/assets/protocol-lib',
        component: '@/pages/Assets/ContractProtocolLib/index',
      },
      // 合约列表
      {
        path: '/assets/contract-list',
        component: '@/pages/Assets/Contract/index',
      },
      // **************************数据资产*************************
      
      {
        path: '/data-assets',
        redirect: '/data-assets/overview',
      },
      {
        path: '/data-assets/overview',
        component: '@/pages/dataAssets/Overview/index',
      },
      // 数据分类
      {
        path: '/data-assets/data-category',
        component: '@/pages/dataAssets/DataCategory/index',
      },
      // 数据价值配置
      {
        path: '/data-assets/value-config',
        component: '@/pages/dataAssets/DataValueConfig/index',
      },
      // **************************支付*************************
      {
        path: '/pay',
        redirect: '/pay/pay-channel-interface',
      },
      // 支付接口
      {
        path: '/pay/pay-channel-interface',
        component: '@/pages/pay/PayChannelInterface/index',
      },
      // 支付方式
      {
        path: '/pay/pay-way',
        component: '@/pages/pay/PayWay/index',
      },
      // **************************交易*************************
      {
        path: '/transaction',
        component: '@/pages/Transaction/index',
      },
    ]
  }

];
export default routes;
