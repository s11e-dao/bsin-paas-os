import React, { useState } from 'react';
import QRCode from 'qrcode.react';
import { ArrowDownOutlined, ArrowUpOutlined } from '@ant-design/icons';
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Select,
  Popconfirm,
  Descriptions,
  Tabs,
  Card,
  Col,
  Row,
  Statistic,
  Table,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import columnsMemberData, { columnsMemberDataType } from './memberData';
import columnsTransferData, { columnsTransferDataType } from './transferData';
import columnsFollowData, { columnsFollowDataType } from './followData';
import columnsProfileData, { columnsProfileDataType } from './profileData';

import {
  getCustomerProfilePageList,
  getCustomerProfileTransferPageList,
  getCustomerProfileMemberPageList,
  getCustomerProfileFollowPageList,
  getCustomerProfileTransferDetail,
  getCustomerProfileMemberDetail,
  getCustomerProfileFollowDetail,
  getCustomerProfileDetail,
  updateProfile,
} from './service';

import TableTitle from '../../components/TableTitle';

export default ({ setCurrentContent, configAssetsItem }) => {

  let biganH5 = process.env.biganH5Url;
  const { TextArea } = Input;
  const { Option } = Select;
  // 查看模态框
  const [isViewProfileModal, setIsViewProfileModal] = useState(false);
  const [isViewMemberModal, setIsViewMemberModal] = useState(false);
  const [isViewTransferModal, setIsViewTransferModal] = useState(false);
  const [isViewFollowModal, setIsViewFollowModal] = useState(false);

  const [isViewMemberRecord, setIsViewMemberRecord] = useState({});
  const [isViewProfileRecord, setIsViewProfileRecord] = useState({});
  const [isViewTransferRecord, setIsViewTransferRecord] = useState({});
  const [isViewFollowRecord, setIsViewFollowRecord] = useState({});

  // 获取表单
  const [FormRef] = Form.useForm();

  /**
   * 以下内容为表格相关
   */
  const columnsMember: ProColumns<columnsMemberDataType>[] = columnsMemberData;
  const columnsProfile: ProColumns<
    columnsProfileDataType
  >[] = columnsProfileData;
  const columnsTransfer: ProColumns<
    columnsTransferDataType
  >[] = columnsTransferData;
  const columnsFollow: ProColumns<columnsFollowDataType>[] = columnsFollowData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewMemberDetail(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
    </ul>
  );
  const actionProfileRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewProfileDetail(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
    </ul>
  );
  const actionTransferRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewTransferDetail(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
    </ul>
  );

  // Follow记录
  const actionFollowRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewFollowDetail(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
    </ul>
  );

  // 自定义数据的表格头部数据
  columnsMember.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  columnsProfile.forEach((item: any) => {
    item.dataIndex === 'action'
      ? (item.render = actionProfileRender)
      : undefined;
  });

  columnsTransfer.forEach((item: any) => {
    item.dataIndex === 'action'
      ? (item.render = actionTransferRender)
      : undefined;
  });
  columnsFollow.forEach((item: any) => {
    item.dataIndex === 'action'
      ? (item.render = actionFollowRender)
      : undefined;
  });

  // Table action 的引用，便于自定义触发
  const transferActionRef = React.useRef<ActionType>();
  const profileActionRef = React.useRef<ActionType>();
  const memberActionRef = React.useRef<ActionType>();
  const followActionRef = React.useRef<ActionType>();

  /**
   * 以下内容为操作相关
   */

  const toViewTransferDetail = async (record) => {
    let { serialNo } = record;
    let viewRes = await getCustomerProfileTransferDetail({ serialNo });
    setIsViewTransferModal(true);
    console.log('viewRes', viewRes);
    setIsViewTransferRecord(viewRes.data);
  };

  const toViewProfileDetail = async (record) => {
    let { serialNo } = record;
    let viewRes = await getCustomerProfileDetail({ serialNo });
    setIsViewProfileModal(true);
    console.log('viewRes', viewRes);
    setIsViewProfileRecord(viewRes.data);
  };

  const toViewFollowDetail = async (record) => {
    let { serialNo } = record;
    let viewRes = await getCustomerProfileFollowDetail({ serialNo });
    setIsViewFollowModal(true);
    console.log('viewRes', viewRes);
    setIsViewFollowRecord(viewRes.data);
  };

  const toViewMemberDetail = async (record) => {
    let { serialNo } = record;
    let viewRes = await getCustomerProfileMemberDetail({ serialNo });
    setIsViewMemberModal(true);
    console.log('viewRes', viewRes);
    setIsViewMemberRecord(viewRes.data);
  };
  return (
    <div>
      <Row gutter={16} style={{ marginBottom: '16px' }}>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic
              title="profile数量"
              value={11.28}
              precision={2}
              valueStyle={{ color: '#3f8600' }}
              prefix={<ArrowUpOutlined />}
              suffix="%"
            />
            <Button
              style={{ marginTop: 16 }}
              type="primary"
              onClick={async () => {
                console.log('res');
                setCurrentContent('createProfile');
              }}
            >
              创建
            </Button>
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic
              title="活跃指数"
              value={9.3}
              precision={2}
              valueStyle={{ color: '#cf1322' }}
              prefix={<ArrowDownOutlined />}
              suffix="%"
            />
            <Button style={{ marginTop: 16 }} type="primary">
              配置规则
            </Button>
          </Card>
        </Col>
      </Row>
      <Card bordered={false} style={{ width: '100%' }}>
        <Tabs defaultActiveKey="1">
          <Tabs.TabPane tab="创建记录" key="1">
            {/* Pro表格 */}
            <ProTable<columnsProfileDataType>
              headerTitle={<TableTitle title="创建记录" />}
              scroll={{ x: 900 }}
              bordered
              // 表头
              columns={columnsProfile}
              actionRef={profileActionRef}
              // 请求获取的数据
              request={async (params) => {
                params.collectionType = '3';
                let res = await getCustomerProfilePageList({
                  ...params,
                  pageNum: params.current,
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
              search={{
                labelWidth: 'auto',
              }}
              // 搜索表单的配置
              form={{
                ignoreRules: false,
              }}
              pagination={{
                pageSize: 10,
              }}
            />{' '}
          </Tabs.TabPane>
          <Tabs.TabPane tab="profile流通记录" key="2">
            {/* 交易记录表格 */}
            <ProTable<columnsTransferDataType>
              headerTitle={<TableTitle title="profile流通记录" />}
              scroll={{ x: 900 }}
              bordered
              // 表头
              columns={columnsTransfer}
              actionRef={transferActionRef}
              // 请求获取的数据
              request={async (params) => {
                let res = await getCustomerProfileTransferPageList({
                  ...params,
                  pageNum: params.current,
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
              search={{
                labelWidth: 'auto',
              }}
              // 搜索表单的配置
              form={{
                ignoreRules: false,
              }}
              pagination={{
                pageSize: 10,
              }}
            />
          </Tabs.TabPane>

          <Tabs.TabPane tab="Follow记录" key="3">
            <ProTable<columnsFollowDataType>
              headerTitle={<TableTitle title="profile Follow记录" />}
              scroll={{ x: 900 }}
              bordered
              // 表头
              columns={columnsFollow}
              actionRef={followActionRef}
              // 请求获取的数据
              request={async (params) => {
                let res = await getCustomerProfileFollowPageList({
                  ...params,
                  pageNum: params.current,
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
              search={{
                labelWidth: 'auto',
              }}
              // 搜索表单的配置
              form={{
                ignoreRules: false,
              }}
              pagination={{
                pageSize: 10,
              }}
            />
          </Tabs.TabPane>

          <Tabs.TabPane tab="memeber" key="4">
            {/* Pro表格 */}
            <ProTable<columnsMemberDataType>
              headerTitle={<TableTitle title="memeber列表" />}
              scroll={{ x: 900 }}
              bordered
              // 表头
              columns={columnsMember}
              actionRef={memberActionRef}
              // 请求获取的数据
              request={async (params) => {
                // console.log(params);
                // 品牌商户发行资产类型 1、数字徽章 2、PFP 3、数字积分 4、数字门票 5、pass卡 6、徽章/门票
                params.assetsTypes = ['3'];
                let res = await getCustomerProfileMemberPageList({
                  ...params,
                  pageNum: params.current,
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
              search={{
                labelWidth: 'auto',
              }}
              // 搜索表单的配置
              form={{
                ignoreRules: false,
              }}
              pagination={{
                pageSize: 10,
              }}
            />{' '}
          </Tabs.TabPane>
        </Tabs>
      </Card>

      {/* 查看Member详情模态框 */}
      <Modal
        title="查看Member"
        width={800}
        centered
        open={isViewMemberModal}
        onOk={() => setIsViewMemberModal(false)}
        onCancel={() => setIsViewMemberModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="数字资产信息">
          <Descriptions.Item label="租户ID">
            {isViewMemberRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="资产编号">
            {isViewMemberRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewMemberRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewMemberRecord?.createTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
      {/* 查看Profile详情模态框 */}
      <Modal
        title="查看Profile详情"
        width={800}
        centered
        visible={isViewProfileModal}
        onOk={() => setIsViewProfileModal(false)}
        onCancel={() => setIsViewProfileModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="Profile信息">
          <Descriptions.Item label="ID">
            {isViewTransferRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="名称">
            {isViewTransferRecord?.name}
          </Descriptions.Item>
          <Descriptions.Item label="profile编号">
            {isViewTransferRecord?.profileNum}
          </Descriptions.Item>
          <Descriptions.Item label="会员数量">
            {isViewTransferRecord?.memberNo}
          </Descriptions.Item>
          <Descriptions.Item label="符号">
            {isViewTransferRecord?.symbol}
          </Descriptions.Item>
          <Descriptions.Item label="类型">
            {isViewTransferRecord?.type}
          </Descriptions.Item>
          <Descriptions.Item label="externalUri">
            {isViewTransferRecord?.externalUri}
          </Descriptions.Item>
          <Descriptions.Item label="合约地址">
            {isViewTransferRecord?.contractAddress}
          </Descriptions.Item>
          <Descriptions.Item label="描述">
            {isViewTransferRecord?.description}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewTransferRecord?.createBy}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
      <Modal
        title="查看流通详情"
        width={800}
        centered
        visible={isViewTransferModal}
        onOk={() => setIsViewTransferModal(false)}
        onCancel={() => setIsViewTransferModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="铸造记录">
          <Descriptions.Item label="租户ID">
            {isViewTransferRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="铸造编号">
            {isViewTransferRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="交易Hash">
            {isViewTransferRecord?.txHash}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
      <Modal
        title="查看Follow详情"
        width={800}
        centered
        visible={isViewFollowModal}
        onOk={() => setIsViewFollowModal(false)}
        onCancel={() => setIsViewFollowModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="铸造记录">
          <Descriptions.Item label="租户ID">
            {isViewFollowRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="转账流水号">
            {isViewFollowRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="交易Hash">
            {isViewFollowRecord?.txHash}
          </Descriptions.Item>
          <Descriptions.Item label="tokenId">
            {isViewFollowRecord?.tokenId}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewFollowRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewFollowRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="协议描述">
            {isViewFollowRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
