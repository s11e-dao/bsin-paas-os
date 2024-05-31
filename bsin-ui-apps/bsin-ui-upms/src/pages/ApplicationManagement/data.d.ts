export type Member = {
  avatar: string;
  name: string;
  id: string;
};

export type BasicListItemDataType = {
  appId: string;
  appCode: string;
  appName: string;
  avatar: string;
  cover: string;
  status: 'normal' | 'exception' | 'active' | 'success';
  createTime: string;
  logo: string;
  url: string;
};
