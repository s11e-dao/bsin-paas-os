
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
      // 商户认证
      {
        path: '/merchant/merchant-auth',
        component: '@/pages/Merchant/MerchantAuth/index',
      },
      // 商户账户
      {
        path: '/merchant/merchant-account',
        component: '@/pages/BizRoleApp/index',
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
      // 等级管理
      {
        path: '/customer/grade-list',
        component: '@/pages/Customer/Grade/index',
      },
      // 客户账户
      {
        path: '/customer/account-list',
        component: '@/pages/Customer/AccountList/index',
      },
      // 客户账户流水
      {
        path: '/customer/account-journal',
        component: '@/pages/Customer/AccountJournal/index',
      },
      // **************************权益条件管理**************************
      // 权益管理
      {
        path: '/equity-list',
        component: '@/pages/Customer/EquityList/index',
      },
      // 条件管理
      {
        path: '/condition-list',
        component: '@/pages/Customer/ConditionList/index',
      },
      // **************************事件、事件模型**************************
      // 事件配置
      {
        path: '/event',
        component: '@/pages/Event/index',
      },
      // **************************钱包**************************
      {
        path: '/wallet',
        redirect: '/wallet/platfor-wallet',
      },
      // 系统钱包
      {
        path: '/wallet/sys-wallet',
        component: '@/pages/Wallet/index',
      },
      // 平台钱包
      {
        path: '/wallet/platfor-wallet',
        component: '@/pages/Wallet/PlatforWallet/index',
      },
      // 商户钱包
      {
        path: '/wallet/merchant-wallet',
        component: '@/pages/Wallet/MerchantWallet/index',
      },
      // 客户钱包
      {
        path: '/wallet/customer-wallet',
        component: '@/pages/Wallet/CustomerWallet/index',
      },
      // 代理商钱包
      {
        path: '/wallet/sys-agent-wallet',
        component: '@/pages/Wallet/SysAgentWallet/index',
      },
      // **************************币种*************************
      {
        path: '/currency-management',
        component: '@/pages/Currency/index',
      },
      // **************************资产*************************
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
      // **************************交易*************************
      {
        path: '/transaction',
        component: '@/pages/Transaction/index',
      },
    ]
  }

];
export default routes;
