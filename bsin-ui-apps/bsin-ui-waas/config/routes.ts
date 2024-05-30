
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
        name: '用户中心',
        path: '/userCenter',
        component: '@/pages/UserCenter/index',
        wrappers: [
          '@/wrappers/auth',
        ],
      },
      {
        name: '非基座运行',
        path: '/uncontainer',
        component: '@/pages/uncontainer'
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
        component: '@/pages/Merchant/MerchantSetting/index',
      },
      // 商户账户
      {
        path: '/merchant/merchant-account',
        component: '@/pages/Merchant/MerchantAccount/index',
      },
      {
        path: '/merchant/merchant-product',
        component: '@/pages/Merchant/MerchantProduct/index',
      },
      // api费用配置（应用审核）
      {
        path: '/merchant/product-audit',
        component: '@/pages/Merchant/ApiTxFeeConfig/index',
      },
      // 商户认证信息审核
      {
        path: '/merchant/merchant-audit',
        component: '@/pages/Merchant/EnterpriseInfo/index',
      },
      // 商户服务订阅
      {
        path: '/merchant/service-subscribe',
        component: '@/pages/Merchant/ServiceSubscribe/index',
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
        path: '/customer/member-grade',
        component: '@/pages/Customer/MemberGrade/index',
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
      // 权益管理
      {
        path: '/customer/equity-list',
        component: '@/pages/Customer/EquityList/index',
      },
      // 条件管理
      {
        path: '/customer/condition-list',
        component: '@/pages/Customer/ConditionList/index',
      },
      // 事件配置
      {
        path: '/customer/event',
        component: '@/pages/Customer/Event/index',
      },
      // **************************钱包**************************
      {
        path: '/wallet',
        redirect: '/wallet/wallet-list',
      },
      {
        path: '/wallet/wallet-list',
        component: '@/pages/Wallet/index',
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
      // pass card
      {
        path: '/assets/pass-card',
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
        path: '/transaction/transaction-list',
        component: '@/pages/Transaction/index',
      },
    ]
  }

];
export default routes;
