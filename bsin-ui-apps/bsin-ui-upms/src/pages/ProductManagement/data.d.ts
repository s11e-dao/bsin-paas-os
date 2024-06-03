// 定义表格数据类型
export type DictColumnsItem = {
  productId: string;
  productName: string;
  productCode: string;
  remark: string;
  createTime: string;
  id: string | number;
};

// 产品项查看表格数据
export type DictItemColumnsItem = {
  id: string | number;
  baseFlag: string | number;
  bizRoleType: string | number;
  appCode: string;
  url: string;
  appLanguage: string;
  createBy: string;
  createTime: string;
  logo: string;
  status: number;
  member: string;
  remark: string;
  updateTime: string;
};
