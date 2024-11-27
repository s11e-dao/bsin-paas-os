import React, { useState, useEffect } from 'react';
import { ArrowDownOutlined, PropertySafetyOutlined } from '@ant-design/icons';
import { Tabs, Card, Col, Row, Statistic, Button, Table, Popconfirm } from 'antd';
import ProTable from '@ant-design/pro-table';
import TableTitle from '../../../components/TableTitle';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import columnsData, { columnsDataType } from './data';

import {
  getCustomerAccountDetail
} from './service';


export default () => {

  const [customerAccount, setCustomerAccount] = useState([]);
  const [pagination, setPagination] = useState({});

  // 查询商户账户余额
  useEffect(() => {
    // category: 1
    // ccy: cny
    let params = {
      category: '1',
      ccy: 'cny',
    };
    getCustomerAccountDetail(params).then((res) => {
      console.log(res?.data)
      setCustomerAccount(res?.data);
      setPagination({
        current: res?.data?.pageNum,
        pageSize: res?.data?.pageSize,
        total: res?.data?.totalSize,
      })
    });

  }, []);

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewContractTemplate(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <Popconfirm
          title="确定删除此条模板？"
          okText="是"
          cancelText="否"
          onConfirm={() => {
            toDelContractTemplate(record);
          }}
        // onCancel={cancel}
        >
          <a>删除</a>
        </Popconfirm>
      </li>
    </ul>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  const onChange = (key: string) => {
    let params = {};
    // 根据不同的tab查询不同的数据
    if (key == "1") {
      params = {
        category: '1',
      };
    }
    if (key == "2") {
      params = {
        category: '1',
      };
    }
    if (key == "3") {
      params = {
        category: '1',
      };
    }
    getCustomerAccountDetail(params).then((res) => {
      console.log(res?.data)
      setCustomerAccount(res?.data);
      setPagination({
        current: res?.data?.pageNum,
        pageSize: res?.data?.pageSize,
        total: res?.data?.totalSize,
      })
    });
  };

  const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  const onSelectChange = (newSelectedRowKeys) => {
    console.log('selectedRowKeys changed: ', newSelectedRowKeys);
    setSelectedRowKeys(newSelectedRowKeys);
  };
  const rowSelection = {
    selectedRowKeys,
    onChange: onSelectChange,
  };

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  return (
    <>
      <Row gutter={16}>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic
              title="账户余额"
              value={customerAccount?.balance}
              precision={2}
              valueStyle={{ color: '#3f8600' }}
              prefix={<PropertySafetyOutlined />}
              suffix=""
            />
            <Button style={{ marginTop: 16 }} type="primary">
              充值
            </Button>
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic
              title="已提现金额"
              value={9.3}
              precision={2}
              valueStyle={{ color: '#cf1322' }}
              prefix={<PropertySafetyOutlined />}
              suffix=""
            />
            <Button style={{ marginTop: 16 }} type="primary">
              提现
            </Button>
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic
              title="累计收入"
              value={9.3}
              precision={2}
              valueStyle={{ color: '#cf1322' }}
              prefix={<PropertySafetyOutlined />}
              suffix=""
            />
            <p style={{ marginTop: 16 }} type="dashed">累计收入</p>
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic
              title="累计支出"
              value={11.28}
              precision={2}
              valueStyle={{ color: '#3f8600' }}
              prefix={<PropertySafetyOutlined />}
              suffix=""
            />
            <p style={{ marginTop: 16 }} type="dashed">累计支出</p>
          </Card>
        </Col>
      </Row>
      <Card bordered={false} >
        <Tabs defaultActiveKey="1" onChange={onChange}>
          <Tabs.TabPane tab="账户记录" key="1">
            <ProTable<columnsDataType>
              headerTitle={<TableTitle title="账户记录列表" />}
              scroll={{ x: 900 }}
              bordered
              // 表头
              columns={columns}
              actionRef={actionRef}
              // 请求获取的数据
              request={async (params) => {
                // console.log(params);
                let res = await getCustomerAccountDetail({
                  ...params,
                  // pageNum: params.current,
                });
                console.log('😒', res);
                const result = {
                  data: res.data,
                  total: res.pagination.totalSize,
                };
                return result;
              }}
              rowKey="serialNo"
              // 搜索框配置
              search={false}
              // 搜索表单的配置
              form={{
                ignoreRules: false,
              }}
              pagination={{
                pageSize: 10,
              }}
              toolBarRender={() => []}
            />
          </Tabs.TabPane>
          <Tabs.TabPane tab="充值记录" key="2">
          <ProTable<columnsDataType>
              headerTitle={<TableTitle title="充值记录" />}
              scroll={{ x: 900 }}
              bordered
              // 表头
              columns={columns}
              actionRef={actionRef}
              // 请求获取的数据
              request={async (params) => {
                // console.log(params);
                let res = await getCustomerAccountDetail({
                  ...params,
                  // pageNum: params.current,
                });
                console.log('😒', res);
                const result = {
                  data: res.data,
                  total: res.pagination.totalSize,
                };
                return result;
              }}
              rowKey="serialNo"
              // 搜索框配置
              search={false}
              // 搜索表单的配置
              form={{
                ignoreRules: false,
              }}
              pagination={{
                pageSize: 10,
              }}
              toolBarRender={() => []}
            />
          </Tabs.TabPane>
          <Tabs.TabPane tab="提现记录" key="3">
          <ProTable<columnsDataType>
              headerTitle={<TableTitle title="提现记录" />}
              scroll={{ x: 900 }}
              bordered
              // 表头
              columns={columns}
              actionRef={actionRef}
              // 请求获取的数据
              request={async (params) => {
                // console.log(params);
                let res = await getCustomerAccountDetail({
                  ...params,
                  // pageNum: params.current,
                });
                console.log('😒', res);
                const result = {
                  data: res.data,
                  total: res.pagination.totalSize,
                };
                return result;
              }}
              rowKey="serialNo"
              // 搜索框配置
              search={false}
              // 搜索表单的配置
              form={{
                ignoreRules: false,
              }}
              pagination={{
                pageSize: 10,
              }}
              toolBarRender={() => []}
            />
          </Tabs.TabPane>
          <Tabs.TabPane tab="API消费记录" key="4">
          <ProTable<columnsDataType>
              headerTitle={<TableTitle title="API消费记录" />}
              scroll={{ x: 900 }}
              bordered
              // 表头
              columns={columns}
              actionRef={actionRef}
              // 请求获取的数据
              request={async (params) => {
                // console.log(params);
                let res = await getCustomerAccountDetail({
                  ...params,
                  // pageNum: params.current,
                });
                console.log('😒', res);
                const result = {
                  data: res.data,
                  total: res.pagination.totalSize,
                };
                return result;
              }}
              rowKey="serialNo"
              // 搜索框配置
              search={false}
              // 搜索表单的配置
              form={{
                ignoreRules: false,
              }}
              pagination={{
                pageSize: 10,
              }}
              toolBarRender={() => []}
            />
          </Tabs.TabPane>
        </Tabs>
      </Card>
    </>
  )
};
