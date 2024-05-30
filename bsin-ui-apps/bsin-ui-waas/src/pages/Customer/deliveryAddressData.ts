import type { ProColumns } from '@ant-design/pro-table';

export type columnsDeliveryAddressDataType = {};

const columnsDeliveryAddressData: ProColumns<
  columnsDeliveryAddressDataType
>[] = [
  // 配置搜索框

  // table里面的内容
  {
    title: 'ID',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '姓名',
    width: 160,
    dataIndex: 'consigneeName',
    hideInSearch: true,
  },
  {
    title: '电话',
    width: 160,
    dataIndex: 'consigneePhone',
    hideInSearch: true,
  },
  {
    title: '省份ID',
    width: 100,
    dataIndex: 'provinceId',
    hideInSearch: true,
  },
  {
    title: '城市ID',
    width: 100,
    dataIndex: 'cityId',
    hideInSearch: true,
  },
  {
    title: '区/县ID',
    width: 100,
    dataIndex: 'areaId',
    hideInSearch: true,
  },
  {
    title: '详细地址',
    width: 160,
    dataIndex: 'detailAddress',
    hideInSearch: true,
  },
  {
    title: '排序',
    width: 160,
    dataIndex: 'sort',
    hideInSearch: true,
  },
  {
    title: '区/县ID',
    width: 100,
    dataIndex: 'areaId',
    hideInSearch: true,
  },
  {
    title: '默认地址',
    width: 100,
    dataIndex: 'defaultAddress',
    hideInSearch: true,
    valueEnum: {
      '0': {
        text: '否',
      },
      '1': {
        text: '是',
      },
    },
  },
  {
    title: '省份名称',
    width: 160,
    dataIndex: 'provinceName',
    hideInSearch: true,
  },
  {
    title: '城市名称',
    width: 160,
    dataIndex: 'cityName',
    hideInSearch: true,
  },
  {
    title: '区域名称',
    width: 160,
    dataIndex: 'areaName',
    hideInSearch: true,
  },
  {
    title: '更新时间',
    width: 240,
    dataIndex: 'updateTime',
    hideInSearch: true,
  },
  {
    title: '创建时间',
    width: 240,
    dataIndex: 'createTime',
    hideInSearch: true,
  },
];

export default columnsDeliveryAddressData;
