import type { ProColumns } from '@ant-design/pro-table';

export type columnsDataType = {
  serialNo: string;
  payWayCode: string;
  payWayType: string;
  payWayName: string;
  tenantId: string;
  createTime: string;
  updateTime: string;
  createBy?: string;
};

const columnsData: ProColumns<columnsDataType>[] = [
  // 配置搜索框
  {
    title: '支付方式名称',
    dataIndex: 'payWayName',
    hideInTable: true,
    fieldProps: {
      maxLength: 50,
      placeholder: '请输入支付方式名称',
    },
  },
  {
    title: '支付方式类型',
    dataIndex: 'payWayType',
    hideInTable: true,
    fieldProps: {
      placeholder: '请选择支付方式类型',
    },
  },
  {
    title: '支付方式编码',
    dataIndex: 'payWayCode',
    hideInTable: true,
    fieldProps: {
      maxLength: 32,
      placeholder: '请输入支付方式编码',
    },
  },

  // table里面的内容
  {
    title: 'ID',
    width: 190,
    dataIndex: 'serialNo',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '支付方式名称',
    width: 160,
    dataIndex: 'payWayName',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '支付方式类型',
    width: 160,
    dataIndex: 'payWayType',
    hideInSearch: true,
    ellipsis: true,
    render: (text: any) => {
      const codeMap: { [key: string]: string } = {
        'WX_H5': '微信H5',
        'WX_JSAPI': '微信公众号',
        'WX_LITE': '微信小程序',
        'WX_NATIVE': '微信扫码',
        'WX_BAR': '微信条码',
        'WX_APP': '微信APP',
        'WX': '微信支付',
        'ALI': '支付宝支付',
        'ALI_APP': '支付宝App',
        'ALI_BAR': '支付宝条码',
        'ALI_JSAPI': '支付宝生活号',
        'ALI_LITE': '支付宝小程序',
        'ALI_PC': '支付宝PC网站',
        'ALI_QR': '支付宝二维码',
        'ALI_WAP': '支付宝WAP',
        'XLALILITE': '信联支付宝支付',
        'YSF_BAR': '云闪付条码',
        'YSF_JSAPI': '云闪付jsapi',
        'YSF_LITE': '云闪付小程序',
        'UP_QR': '银联二维码(主扫)',
        'UP_BAR': '银联二维码(被扫)',
        'UP_APP': '银联App支付',
        'QR_CASHIER': '聚合',
        'QQ_PAY': '钱包',
        'PP_PC': 'PayPal支付',
        'ICBC_APP': '工行APP支付'
      };
      return codeMap[text] || text;
    },
  },
  {
    title: '支付方式编码',
    width: 160,
    dataIndex: 'payWayCode',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '租户ID',
    width: 160,
    dataIndex: 'tenantId',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '创建时间',
    width: 160,
    dataIndex: 'createTime',
    hideInSearch: true,
    valueType: 'dateTime',
  },
  {
    title: '更新时间',
    width: 160,
    dataIndex: 'updateTime',
    hideInSearch: true,
    valueType: 'dateTime',
  },
  {
    title: '操作',
    width: 200,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
