import type { ProColumns } from '@ant-design/pro-table';

export type columnsProfileDataType = {
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

const columnsProfileData: ProColumns<columnsProfileDataType>[] = [
  // 配置搜索框
  {
    title: '名称',
    dataIndex: 'name',
    hideInTable: true,
  },
  {
    title: '类型',
    dataIndex: 'type',
    hideInTable: true,
    valueEnum: {
      Brand: {
        text: 'Brand',
      },
      Individual: {
        text: 'Individual',
      },
    },
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: 'ID',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left', 
    hideInSearch: true,
  },
  {
    title: '名称',
    width: 160,
    dataIndex: 'name',
    hideInSearch: true,
  },
  {
    title: 'profile编号',
    width: 160,
    dataIndex: 'profileNum',
    hideInSearch: true,
  },
  {
    title: '会员数量',
    width: 160,
    dataIndex: 'memberNo',
    hideInSearch: true,
  },
  {
    title: '符号',
    width: 160,
    dataIndex: 'symbol',
    hideInSearch: true,
  },
  {
    title: '类型',
    width: 160,
    dataIndex: 'type',
    hideInSearch: true,
  },
  {
    title: 'externalUri',
    width: 160,
    dataIndex: 'externalUri',
    hideInSearch: true,
  },
  {
    title: '合约地址',
    width: 400,
    dataIndex: 'contractAddress',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '描述',
    width: 320,
    dataIndex: 'description',
    hideInSearch: true,
  },
  {
    title: '创建者',
    width: 160,
    dataIndex: 'createBy',
    hideInSearch: true,
  },
  {
    title: '创建时间',
    width: 160,
    dataIndex: 'createTime',
    hideInSearch: true,
  },
  {
    title: '操作',
    width: 100,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsProfileData;
