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
    title: '业务角色类型',
    width: 120,
    hideInTable: true,
    dataIndex: 'bizRoleType',
    valueType: 'select',
    valueEnum: {
      "1": {
        text: '系统运营',
      },
      "2": {
        text: '平台租户',
      },
      "3": {
        text: '商户',
      },
      "4": {
        text: '代理商',
      },
      "5": {
        text: '客户',
      },
      "6": {
        text: '门店',
      },
    },
  },
  {
    title: '等级Id',
    dataIndex: 'gradeNo',
    hideInTable: true,
  },
  {
    title: '等级编号',
    dataIndex: 'gradeNum',
    hideInTable: true,
  },
  {
    title: '等级名称',
    dataIndex: 'name',
    hideInTable: true,
  },
  // table里面的内容
  {
    title: '等级ID',
    width: 190,
    hideInSearch: true,
    dataIndex: 'serialNo',
    fixed: 'left',
  },
  {
    title: '业务角色类型',
    width: 120,
    hideInSearch: true,
    dataIndex: 'bizRoleType',
    valueType: 'select',
    valueEnum: {
      "1": {
        text: '系统运营',
      },
      "2": {
        text: '平台租户',
      },
      "3": {
        text: '商户',
      },
      "4": {
        text: '代理商',
      },
      "5": {
        text: '客户',
      },
      "6": {
        text: '门店',
      },
    },
  },
  {
    title: '等级名称',
    width: 160,
    hideInSearch: true,
    dataIndex: 'name',
  },
  {
    title: '等级级数',
    width: 160,
    hideInSearch: true,
    dataIndex: 'gradeNum',
  },
  {
    title: '等级编号',
    width: 160,
    hideInSearch: true,
    dataIndex: 'gradeCode',
  },
  {
    title: '等级图标',
    width: 160,
    hideInSearch: true,
    dataIndex: 'gradeImage',
  },
  {
    title: '等级描述',
    width: 160,
    hideInSearch: true,
    dataIndex: 'description',
  },
  {
    title: 'Tags',
    key: 'tags',
    hideInSearch: true,
    dataIndex: 'tags',
  },
  {
    title: '操作',
    width: 300,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
