// 提现账户数据类型
export interface WithdrawAccountType {
  serialNo: string;
  accountType: 1 | 2 | 3; // 1=银行卡, 2=支付宝, 3=微信支付
  accountName: string;
  accountNumber: string;
  accountNum?: string;
  bankName?: string;
  swiftCode?: string;
  remark?: string;
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
  accountType: 1 | 2 | 3; // 1=银行卡, 2=支付宝, 3=微信支付
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
  accountType: 1 | 2 | 3; // 1=银行卡, 2=支付宝, 3=微信支付
  accountName: string;
  accountNum: string;
  bankName: string;
  swiftCode?: string;
  remark?: string;
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