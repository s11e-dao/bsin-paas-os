import type { ProColumns } from '@ant-design/pro-table';

// 结算账户数据类型 - 基于数据库表 waas_chain_settlement_account
export type SettlementAccountType = {
  serialNo: string; // serial_no - 结算账户id
  tenantId?: string; // tenant_id - 租户id
  bizRoleType?: string; // biz_role_type - 角色类型：1.运营平台 2.租户平台 4.代理商 5.租户客户 6.门店 99.无
  bizRoleTypeNo?: string; // biz_role_type_no - 业务角色类型编号
  accountNum: string; // account_num - 链地址
  accountName: string; // account_name - 账户名
  accountType: number; // account_type - 账户类型：1.多签账户 2.普通账户
  remark?: string; // remark - 备注
  createBy?: string; // create_by - 创建人
  createTime?: string; // create_time - 创建时间
  updateBy?: string; // update_by - 更新人
  updateTime?: string; // update_time - 更新时间
  delFlag: string; // del_flag - 逻辑删除：0、未删除 1、已删除
  category?: string; // category - 分类：1 支付结算 2提现结算
  defaultFlag?: string; // default_flag - 默认账户
  status: string; // status - 状态：1 待审核 2 审核通过
  chainCoinNo?: string; // chain_coin_no - 币种id
};

// 为了保持向后兼容，也导出 ReceiveAccountType
export type ReceiveAccountType = SettlementAccountType;

export type columnsDataType = ReceiveAccountType;

const columnsData: ProColumns<columnsDataType>[] = [
  // 搜索表单
  {
    title: '账户名称',
    dataIndex: 'accountName',
    hideInTable: true,
    fieldProps: {
      maxLength: 100,
    },
  },
  {
    title: '账户类型',
    dataIndex: 'accountType',
    hideInTable: true,
    valueType: 'select',
    valueEnum: {
      '1': { text: '多签账户' },
      '2': { text: '普通账户' },
    },
  },
  {
    title: '分类',
    dataIndex: 'category',
    hideInTable: true,
    valueType: 'select',
    valueEnum: {
      '1': { text: '支付结算' },
      '2': { text: '提现结算' },
    },
  },
  {
    title: '状态',
    dataIndex: 'status',
    hideInTable: true,
    valueType: 'select',
    valueEnum: {
      '1': { text: '待审核' },
      '2': { text: '审核通过' },
    },
  },

  // 表格内容
  {
    title: '账户ID',
    width: 180,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '账户名称',
    width: 150,
    dataIndex: 'accountName',
    hideInSearch: true,
  },
  {
    title: '链地址',
    width: 300,
    dataIndex: 'accountNum',
    hideInSearch: true,
    copyable: true,
    ellipsis: true,
  },
  {
    title: '账户类型',
    width: 120,
    dataIndex: 'accountType',
    hideInSearch: true,
    valueType: 'select',
    valueEnum: {
      '1': { text: '多签账户' },
      '2': { text: '普通账户' },
    },
  },
  {
    title: '分类',
    width: 120,
    dataIndex: 'category',
    hideInSearch: true,
    valueType: 'select',
    valueEnum: {
      '1': { text: '支付结算' },
      '2': { text: '提现结算' },
    },
  },
  {
    title: '是否默认',
    width: 100,
    dataIndex: 'defaultFlag',
    hideInSearch: true,
    valueType: 'select',
    valueEnum: {
      '1': { text: '是', status: 'Success' },
      '0': { text: '否', status: 'Default' },
    },
  },
  {
    title: '状态',
    width: 100,
    dataIndex: 'status',
    hideInSearch: true,
    valueType: 'select',
    valueEnum: {
      '1': { text: '待审核', status: 'Processing' },
      '2': { text: '审核通过', status: 'Success' },
    },
  },
  {
    title: '创建时间',
    width: 180,
    dataIndex: 'createTime',
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

