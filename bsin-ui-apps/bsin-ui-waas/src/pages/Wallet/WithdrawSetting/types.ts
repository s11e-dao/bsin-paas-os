// 提现账户数据类型
export interface WithdrawAccountType {
  serialNo: string;
  accountType: 'bank' | 'alipay' | 'wechat';
  accountName: string;
  accountNumber: string;
  bankName?: string;
  bankBranch?: string;
  status: 'active' | 'inactive' | 'pending';
  isDefault: boolean;
  createTime: string;
  updateTime: string;
}

// 提现设置类型
export interface WithdrawSettingType {
  serialNo: string;
  minAmount: number;
  maxAmount: number;
  dailyLimit: number;
  monthlyLimit: number;
  feeRate: number;
  feeType: 'percentage' | 'fixed';
  status: 'active' | 'inactive';
  createTime: string;
  updateTime: string;
}

// 提现记录类型
export interface WithdrawRecordType {
  serialNo: string;
  accountSerialNo: string;
  accountName: string;
  accountType: 'bank' | 'alipay' | 'wechat';
  amount: number;
  fee: number;
  actualAmount: number;
  status: 'pending' | 'processing' | 'success' | 'failed';
  createTime: string;
  updateTime: string;
  remark?: string;
}

// 表单值类型
export interface WithdrawAccountFormValues {
  accountType: 'bank' | 'alipay' | 'wechat';
  accountName: string;
  accountNumber: string;
  bankName: string;
  bankBranch?: string;
}

// API 响应类型
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  pagination?: {
    current: number;
    pageSize: number;
    total: number;
    totalSize: number;
  };
} 