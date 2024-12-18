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
    title: '活动名称',
    dataIndex: 'nane',
    hideInTable: true,
  },
  {
    title: '活动状态',
    width: 160,
    dataIndex: 'status',
    valueType: 'select',
    hideInTable: true,
    // 活动状态：活动状态：1、待发布 2、已发布 3、已结束  4、已过期
    valueEnum: {
      '1': {
        text: '待发布',
      },
      '2': {
        text: '已发布',
      },
      '3': {
        text: '已结束',
      },
      '4': {
        text: '已过期',
      },
    },
  },
  {
    title: '参与方式',
    width: 160,
    dataIndex: 'participationMethod',
    hideInTable: true,
    // 活动参与方式：1、报名 2、支付 3、领券
    valueEnum: {
      '1': {
        text: '报名',
      },
      '2': {
        text: '支付',
      },
      '3': {
        text: '领券',
      },
    },
  },
  // 活动类型：1、优惠券活动 2、线下活动 3、联名活动
  {
    title: '活动类型',
    width: 160,
    dataIndex: 'type',
    hideInTable: true,
    valueEnum: {
      '1': {
        text: '优惠券活动',
      },
      '2': {
        text: '线下活动',
      },
      '3': {
        text: '联名活动',
      },
    },
  },
  {
    title: '活动地点',
    dataIndex: 'activityAddress',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: '活动ID',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '活动名称',
    width: 160,
    dataIndex: 'name',
    hideInSearch: true,
  },
  {
    title: '活动开始时间',
    width: 160,
    dataIndex: 'startTime',
    hideInSearch: true,
  },
  {
    title: '活动结束时间',
    width: 160,
    dataIndex: 'endTime',
    hideInSearch: true,
  },
  {
    title: '活动地址',
    width: 160,
    dataIndex: 'activityAddress',
    hideInSearch: true,
  },
  {
    title: '活动描述',
    width: 160,
    dataIndex: 'description',
    hideInSearch: true,
  },
  {
    title: '活动状态',
    width: 160,
    dataIndex: 'status',
    hideInSearch: true,
    // 活动状态：1、待发布 2、已发布 3、已结束
    valueEnum: {
      '1': {
        text: '待发布',
      },
      '2': {
        text: '已发布',
      },
      '3': {
        text: '已结束',
      },
    },
  },
  {
    title: '参与方式',
    width: 160,
    dataIndex: 'participationMethod',
    hideInSearch: true,
    // 活动参与方式：1、报名 2、支付 3、领券
    valueEnum: {
      '1': {
        text: '报名',
      },
      '2': {
        text: '支付',
      },
      '3': {
        text: '领券',
      },
    },
  },
  // 活动类型：1、优惠券活动 2、线下活动 3、联名活动
  {
    title: '活动类型',
    width: 160,
    dataIndex: 'type',
    hideInSearch: true,
    valueEnum: {
      '1': {
        text: '优惠券活动',
      },
      '2': {
        text: '线下活动',
      },
      '3': {
        text: '联名活动',
      },
    },
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
    width: 360,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
