
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

      // ************************Profile***********************
      {
        path: '/profile',
        component: '@/pages/Profile/index',
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
        redirect: '/assets/di-overview',
      },
      // 资产总览
      // {
      //   path: '/assets/di-overview',
      //   component: '@/pages/Assets/Overview/index',
      // },
      // 数字资产总览
      {
        path: '/assets/di-overview',
        component: '@/pages/Assets/DIDRWADashboard/index',
      },
      // 资产集合
      {
        path: '/assets/assets-collection',
        component: '@/pages/Assets/AssetsCollection/index',
      },
      // 资产列表
      {
        path: '/assets/assets-item',
        component: '@/pages/Assets/AssetsItem/index',
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
      // ***********************分账***********************
      {
        path: '/revenue-share',
        redirect: '/revenue-share/revenue-share-list',
      },
      // 分账明细
      {
        path: '/revenue-share/revenue-share-list',
        component: '@/pages/revenueShare/RevenueShareList/index',
      },
      // 分账配置
      {
        path: '/revenue-share/revenue-share-config',
        component: '@/pages/revenueShare/RevenueShareConfig/index',
      },
      // // 分账配置列表
      // {
      //   path: '/revenue-share/revenue-share-config-list',
      //   component: '@/pages/revenueShare/RevenueShareConfigList/index',
      // },
      // 让利配置
      {
        path: '/revenue-share/profit-sharing-config',
        component: '@/pages/revenueShare/ProfitSharingConfig/index',
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
