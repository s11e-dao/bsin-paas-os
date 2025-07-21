import React, { useState, useEffect } from 'react';
import { ArrowDownOutlined, PropertySafetyOutlined } from '@ant-design/icons';
import { Tabs, Card, Col, Row, Statistic, Button, Table, Popconfirm } from 'antd';
import ProTable from '@ant-design/pro-table';
import TableTitle from '../../../components/TableTitle';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import columnsData, { columnsDataType } from './data';

import { eWalletService } from './service';

interface OverviewProps {
  setCurrentContent: (content: string) => void;
}

export default ({ setCurrentContent }: OverviewProps) => {

  const [balanceAccount, setBalanceAccount] = useState({});
  const [accumulatedIncomeAccount, setAccumulatedIncomeAccount] = useState({});
  const [accumulatedExpenditureAccount, setAccumulatedExpenditureAccount] = useState({});
  const [accumulatedWithdrawAccount, setAccumulatedWithdrawAccount] = useState({});
  const [orderType, setOrderType] = useState("");
  

  // 查询商户账户余额
  useEffect(() => {

    eWalletService.getCategoryAccounts({}).then((res) => {
      console.log(res?.data)
      setBalanceAccount(res?.data?.balance)
      setAccumulatedIncomeAccount(res?.data?.accumulatedIncome)
      setAccumulatedExpenditureAccount(res?.data?.accumulatedExpenditure)
      setAccumulatedWithdrawAccount(res?.data?.accumulatedWithdraw)
    });

  }, []);

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  const onChange = (key: string) => {
    setOrderType(key)
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
              value={balanceAccount?.balance}
              precision={2}
              valueStyle={{ color: '#3f8600' }}
              prefix={<PropertySafetyOutlined />}
              suffix=""
            />
            <Button 
              style={{ marginTop: 16 }} 
              type="primary"
              onClick={() => setCurrentContent('recharge')}
            >
              充值
            </Button>
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic
              title="已提现金额"
              value={accumulatedWithdrawAccount?.balance}
              precision={2}
              valueStyle={{ color: '#cf1322' }}
              prefix={<PropertySafetyOutlined />}
              suffix=""
            />
            <Button 
              style={{ marginTop: 16 }} 
              type="primary"
              onClick={() => setCurrentContent('withdraw')}
            >
              提现
            </Button>
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic
              title="累计收入"
              value={accumulatedIncomeAccount?.balance}
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
              value={accumulatedExpenditureAccount?.balance}
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
          <Tabs.TabPane tab="账户记录" key="">
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
                let res = await eWalletService.getAccountJournalPageList({
                  ...params,
                  orderType: orderType,
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
                let res = await eWalletService.getAccountJournalPageList({
                  ...params,
                  orderType: orderType,
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
                let res = await getAccountJournalPageList({
                  ...params,
                  orderType: orderType,
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
                let res = await getAccountJournalPageList({
                  ...params,
                  orderType: orderType,
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
