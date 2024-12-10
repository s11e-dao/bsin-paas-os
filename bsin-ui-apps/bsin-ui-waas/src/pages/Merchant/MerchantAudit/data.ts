import type { ProColumns } from '@ant-design/pro-table';

export type columnsDataType = {
  order: number;
  acName: string;
  custNo: string;
  acNo: string;
  balance: string;
  custType: string;
  openAcDate: string;
  status: string;
  startTime: string;
  endTime: string;
};

const columnsData: ProColumns<columnsDataType>[] = [
  // 配置搜索框
  {
    title: '商戶号',
    dataIndex: 'serialNo',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '企业名称',
    dataIndex: 'merchantName',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: '商户号',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  // {
  //   title: '客户号',
  //   width: 190,
  //   dataIndex: 'customerNo',
  //   hideInSearch: true,
  // },
  {
    title: '企业名称',
    width: 190,
    dataIndex: 'merchantName',
    hideInSearch: true,
  },
  {
    title: '认证状态',
    width: 100,
    hideInSearch: true,
    dataIndex: 'authenticationStatus',
    valueType: 'select',
    valueEnum: {
      0: { text: '未认证', status: 'Default' },
      1: { text: '待审核', status: 'running' },
      2: { text: '认证成功', status: 'Success' },
      3: { text: '认证失败', status: 'Success' },
    },
  },
  {
    title: '状态',
    width: 100,
    hideInSearch: true,
    dataIndex: 'status',
    valueType: 'select',
    valueEnum: {
      0: { text: '正常', status: 'Default' },
      1: { text: '冻结', status: 'running' },
      2: { text: ' 待审核', status: 'audit' },
    },
  },
  {
    title: '企业工商号',
    width: 160,
    dataIndex: 'businessNo',
    hideInSearch: true,
  },
  {
    title: '法人姓名',
    width: 190,
    dataIndex: 'legalPersonName',
    hideInSearch: true,
  },
  {
    title: '创建时间',
    width: 160,
    dataIndex: 'createTime',
    hideInSearch: true,
  },
  {
    title: '法人证件类型',
    width: 160,
    dataIndex: 'legalPersonCredType',
    hideInSearch: true,
  },
  {
    title: '法人证件号',
    width: 160,
    dataIndex: 'legalPersonCredNo',
    hideInSearch: true,
  },
  {
    title: '营业执照图片',
    width: 160,
    dataIndex: 'businessLicenceImg',
    hideInSearch: true,
    valueType: 'image',
  },
  {
    title: '联系电话',
    width: 160,
    dataIndex: 'phone',
    hideInSearch: true,
  },
  {
    title: '公司网址',
    width: 160,
    dataIndex: 'netAddress',
    hideInSearch: true,
  },
  {
    title: '企业地址',
    width: 160,
    dataIndex: 'merchantAddress',
    hideInSearch: true,
  },
  {
    title: '经营范围',
    width: 160,
    dataIndex: 'businessScope',
    hideInSearch: true,
  },
  {
    title: '操作',
    width: 160,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
