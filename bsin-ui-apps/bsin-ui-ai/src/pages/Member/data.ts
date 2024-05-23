import type { ProColumns } from '@ant-design/pro-table';
import { Space, Table, Tag } from 'antd';

// 定义请求返回数据类型
export type AppColumnsItem = {
  roleName: string;
  roleCode: string;
  appId: string;
  appName: string;
  roleId: string;
  remark: string;
  updateTime: string;
  createBy: string;
  templateEnable: boolean;
  systemRoleEnable: boolean;
  contextEnable: boolean;
  createTime: string;
  option: string;
  updateBy: string;
};

// 定义表头
let columnsData: ProColumns<AppColumnsItem>[] = [
  {
    title: 'openId',
    dataIndex: 'openId',
    hideInTable: true,
  },
  {
    title: '会员名称',
    dataIndex: 'name',
    hideInTable: true,
  },
  // 上方查询，下方表头
  {
    title: 'openId',
    fixed: 'left',
    width: 240,
    hideInSearch: true,
    dataIndex: 'openId',
  },
  {
    title: '会员名称',
    hideInSearch: true,
    dataIndex: 'name',
    width: 140,
  },
  {
    title: '性别',
    width: 200,
    hideInSearch: true,
    dataIndex: 'gender',
  },
  {
    title: '手机号',
    hideInSearch: true,
    width: 140,
    dataIndex: 'phone',
  },
  {
    title: '微信号',
    hideInSearch: true,
    width: 140,
    dataIndex: 'weixinNo',
  },
  {
    title: '所在城市',
    hideInSearch: true,
    width: 140,
    dataIndex: 'city',
  },
  {
    title: '身高',
    hideInSearch: true,
    width: 140,
    dataIndex: 'height',
  },
  {
    title: '年龄',
    hideInSearch: true,
    width: 140,
    dataIndex: 'age',
  },
  {
    title: '生日',
    hideInSearch: true,
    width: 140,
    dataIndex: 'birthday',
  },
  {
    title: '学历',
    hideInSearch: true,
    width: 140,
    dataIndex: 'education',
  },
  {
    title: '婚姻状况',
    hideInSearch: true,
    width: 140,
    dataIndex: 'maritalStatus',
  },
  {
    title: '收入',
    hideInSearch: true,
    width: 140,
    dataIndex: 'income',
  },
  {
    title: '星座',
    hideInSearch: true,
    width: 140,
    dataIndex: 'constellation',
  },
  {
    title: '性格',
    hideInSearch: true,
    width: 140,
    dataIndex: 'character',
  },
  {
    title: '兴趣',
    hideInSearch: true,
    width: 140,
    dataIndex: 'interest',
  },
  {
    title: '爱情观',
    hideInSearch: true,
    width: 140,
    dataIndex: 'loveView',
  },
  {
    title: '伴侣要求',
    hideInSearch: true,
    width: 140,
    dataIndex: 'mateRequirements',
  },
  {
    title: '自我介绍词汇',
    hideInSearch: true,
    width: 140,
    dataIndex: 'selfIntroductionCopyWriting',
  },
  {
    title: '创建时间',
    width: 180,
    hideInSearch: true,
    dataIndex: 'createTime',
  },
  {
    title: '更新时间',
    hideInSearch: true,
    width: 180,
    dataIndex: 'updateTime',
  },
  {
    title: '操作',
    width: 180,
    fixed: 'right',
    hideInSearch: true,
    dataIndex: 'option',
  },
];
export default columnsData;
