import type { ProColumns } from '@ant-design/pro-table';

// 定义交易记录类型，对应后端Transaction.java实体
export type columnsDataType = {
  serialNo: string;                    // ID
  transactionType: string;             // 交易类型
  txHash: string;                      // 交易hash
  contractAddress?: string;            // 合约地址
  contractMethod?: string;             // 执行的合约方法
  methodInvokeWay?: number;            // 合约方法调用类型
  transactionStatus: string;           // 交易状态
  txAmount: number;                    // 交易金额
  gasFee?: number;                     // 实际消费gas费
  fee?: number;                        // 手续费
  fromAddressType?: string;            // 源地址类型
  fromAddress?: string;                // 源地址
  toAddressType?: string;              // 目标地址类型
  toAddress?: string;                  // 目标地址
  comment?: string;                    // 备注
  auditStatus?: string;                // 审核状态
  completedTime?: string;              // 交易完成时间
  outSerialNo?: string;                // 商户业务唯一标识
  bizRoleType?: string;                // 用户角色类型
  bizRoleTypeNo?: string;              // 业务角色类型编号
  payChannelConfigNo?: string;         // 支付渠道配置编号
  tenantId?: string;                   // 租户ID
  profitSharing?: boolean;             // 分账标识
  profitSharingType?: string;          // 分账类型
  profitSharingStatus?: boolean;       // 分账状态
  profitSharingAmount?: number;        // 分润总金额
  createTime?: string;                 // 创建时间
  createBy?: string;                   // 创建人
  updateTime?: string;                 // 更新时间
  updateBy?: string;                   // 更新人
};

// 产品类型定义
export type ProductType = {
  productCode: string;
  productName: string;
};

const columnsData: ProColumns<columnsDataType>[] = [
  // 搜索配置
  {
    title: '交易类型',
    width: 120,
    hideInTable: true,
    dataIndex: 'transactionType',
    valueType: 'select',
    valueEnum: {
      "1": { text: '支付' },
      "2": { text: '充值' },
      "3": { text: '转账' },
      "4": { text: '提现' },
      "5": { text: '退款' },
      "6": { text: '结算' },
      "7": { text: '收入' },
      "8": { text: '赎回' },
    },
  },
  {
    title: '交易状态',
    width: 100,
    hideInTable: true,
    dataIndex: 'transactionStatus',
    valueType: 'select',
    valueEnum: {
      "1": { text: '等待' },
      "2": { text: '成功' },
      "3": { text: '失败' },
    },
  },
  {
    title: '交易hash',
    dataIndex: 'txHash',
    hideInTable: true,
    fieldProps: {
      placeholder: '请输入交易hash',
    },
  },
  {
    title: '转出地址',
    dataIndex: 'fromAddress',
    hideInTable: true,
    fieldProps: {
      placeholder: '请输入转出地址',
    },
  },
  {
    title: '转入地址',
    dataIndex: 'toAddress',
    hideInTable: true,
    fieldProps: {
      placeholder: '请输入转入地址',
    },
  },
  {
    title: '商户业务单号',
    dataIndex: 'outSerialNo',
    hideInTable: true,
    fieldProps: {
      placeholder: '请输入商户业务单号',
    },
  },
  {
    title: '合约地址',
    dataIndex: 'contractAddress',
    hideInTable: true,
    fieldProps: {
      placeholder: '请输入合约地址',
    },
  },
  {
    title: '交易金额范围',
    dataIndex: 'txAmountRange',
    hideInTable: true,
    valueType: 'digitRange',
    fieldProps: {
      placeholder: ['最小金额', '最大金额'],
    },
  },

  // 表格显示列
  {
    title: '交易编号',
    width: 180,
    dataIndex: 'serialNo',
    hideInSearch: true,
    copyable: true,
    ellipsis: true,
  },
  {
    title: '交易hash',
    width: 200,
    dataIndex: 'txHash',
    hideInSearch: true,
    copyable: true,
    ellipsis: true,
    tooltip: '点击可复制完整hash',
  },
  {
    title: '转出地址',
    width: 180,
    dataIndex: 'fromAddress',
    hideInSearch: true,
    copyable: true,
    ellipsis: true,
  },
  {
    title: '转入地址',
    width: 180,
    dataIndex: 'toAddress',
    hideInSearch: true,
    copyable: true,
    ellipsis: true,
  },
  {
    title: '交易金额',
    width: 120,
    dataIndex: 'txAmount',
    hideInSearch: true,
    valueType: 'money',
    fieldProps: {
      precision: 2,
    },
  },
  {
    title: '交易类型',
    width: 100,
    dataIndex: 'transactionType',
    hideInSearch: true,
    valueEnum: {
      "1": { text: '支付', status: 'Default' },
      "2": { text: '充值', status: 'Success' },
      "3": { text: '转账', status: 'Processing' },
      "4": { text: '提现', status: 'Warning' },
      "5": { text: '退款', status: 'Error' },
      "6": { text: '结算', status: 'Default' },
      "7": { text: '收入', status: 'Success' },
      "8": { text: '赎回', status: 'Processing' },
    },
  },
  {
    title: '交易状态',
    width: 100,
    dataIndex: 'transactionStatus',
    hideInSearch: true,
    valueEnum: {
      "1": { text: '等待', status: 'Processing' },
      "2": { text: '成功', status: 'Success' },
      "3": { text: '失败', status: 'Error' },
    },
  },
  {
    title: 'Gas费',
    width: 100,
    dataIndex: 'gasFee',
    hideInSearch: true,
    valueType: 'money',
    fieldProps: {
      precision: 6,
    },
  },
  {
    title: '手续费',
    width: 100,
    dataIndex: 'fee',
    hideInSearch: true,
    valueType: 'money',
    fieldProps: {
      precision: 2,
    },
  },
  {
    title: '合约地址',
    width: 180,
    dataIndex: 'contractAddress',
    hideInSearch: true,
    copyable: true,
    ellipsis: true,
  },
  {
    title: '执行方法',
    width: 120,
    dataIndex: 'contractMethod',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '交易时间',
    width: 160,
    dataIndex: 'createTime',
    hideInSearch: true,
    valueType: 'dateTime',
  },
  {
    title: '完成时间',
    width: 160,
    dataIndex: 'completedTime',
    hideInSearch: true,
    valueType: 'dateTime',
  },
  {
    title: '操作',
    width: 100,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;

