import type { ProColumns } from '@ant-design/pro-table';

export type columnsDataType = {
  serialNo: string;
  payWayCode: string;
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
    title: '支付方式编码',
    width: 160,
    dataIndex: 'payWayCode',
    hideInSearch: true,
    ellipsis: true,
    render: (text: any) => {
      const codeMap: { [key: string]: string } = {
        'wxPay': '微信支付',
        'aliPay': '支付宝支付',
        'brandsPoint': '品牌积分支付',
        'fireDiamond': '火钻支付'
      };
      return codeMap[text] || text;
    },
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
