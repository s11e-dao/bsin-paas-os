import type {ProColumns} from '@ant-design/pro-table';

export type columnsCustomerIdentityDataType = {};

const columnsCustomerIdentityData: ProColumns<
    columnsCustomerIdentityDataType
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
        title: '登录名',
        width: 160,
        dataIndex: 'username',
        hideInSearch: true,
    },
    {
        title: '客户编号',
        width: 160,
        dataIndex: 'customerNo',
        hideInSearch: true,
    },
    {
        title: '租户ID',
        width: 160,
        dataIndex: 'tenantId',
        hideInSearch: true,
    },
    {
        title: '商户ID',
        width: 160,
        dataIndex: 'merchantNo',
        hideInSearch: true,
    },
    {
        title: '身份类型',
        // 商户|客户|代理商  见业务角色类型枚举
        width: 100,
        dataIndex: 'bizRoleType',
        hideInSearch: true,
    },
    {
        title: '身份类型编号',
        // 商户号|代理商号|客户号
        width: 180,
        dataIndex: 'bizRoleTypeNo',
        hideInSearch: true,
    },
    {
        title: '身份编号',
        width: 180,
        dataIndex: 'identityTypeNo',
        hideInSearch: true,
    },
    {
        title: '身份名称',
        // 商户(3) 代理商(4) 客户(5)
        width: 100,
        dataIndex: 'name',
        hideInSearch: true,
    },

    {
        title: '状态',
        // 状态： 0：禁用 1:启用
        width: 100,
        dataIndex: 'status',
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

export default columnsCustomerIdentityData;
