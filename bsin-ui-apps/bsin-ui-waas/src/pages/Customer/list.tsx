import React, { useState } from 'react';
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Divider,
  Select,
  Popconfirm,
  Descriptions,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';

import columnsDeliveryAddressData, {
  columnsDeliveryAddressDataType,
} from './deliveryAddressData';
import {
  getCustomerPageList,
  addCustomer,
  deleteCustomer,
  getCustomerDetail,
  getCustomerAddressDetail,
} from './service';
import TableTitle from '../../components/TableTitle';

export default ({ addCurrentRecord }) => {

  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isCustomerModal, setIsCustomerModal] = useState(false);
  // 查看模态框
  const [isViewCustomerModal, setIsViewCustomerModal] = useState(false);
  const [isViewCustomerAddressModal, setIsViewCustomerAddressModal] = useState(
    false,
  );
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});

  const [isViewAddressRecord, setIsViewAddressRecord] = useState({});
  // 获取表单
  const [FormRef] = Form.useForm();

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;
  // 收货地址表头数据
  const columnsDeliveryAddress: ProColumns<
    columnsDeliveryAddressDataType
  >[] = columnsDeliveryAddressData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <div key={record.dictType}>
      <a onClick={() => addCurrentRecord(record)}>查看</a>
      <Divider type="vertical" />
      <a
        onClick={() => {
          toViewCustomerAddress(record);
        }}
      >
        收货地址
      </a>
      <Divider type="vertical" />
      <Popconfirm
        title="确定删除此条数据？?"
        onConfirm={() => toDelCustomer(record.id)}
        onCancel={() => {
          message.warning(`取消删除`);
        }}
        okText="是"
        cancelText="否"
      >
        <a>删除</a>
      </Popconfirm>
    </div>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  // 收货地址 Table action 的引用，便于自定义触发
  const deliveryAddressActionRef = React.useRef<ActionType>();

  /**
   * 以下内容为操作相关
   */

  // 新增模板
  const increaseCustomer = () => {
    setIsCustomerModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmCustomer = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        addCustomer(response).then((res) => {
          console.log('add', res);
          if (res.code === '000000') {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsCustomerModal(false);
          } else {
            message.error(`失败： ${res?.message}`);
          }
        });
      })
      .catch(() => { });
  };

  /**
   * 取消添加模板
   */
  const onCancelCustomer = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsCustomerModal(false);
  };

  /**
   * 删除模板
   */
  const toDelCustomer = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteCustomer({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === '000000') {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewCustomer = async (record) => {
    let { serialNo } = record;
    let viewRes = await getCustomerDetail({ serialNo });
    setIsViewCustomerModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  const toViewCustomerAddress = async (record) => {
    setIsViewAddressRecord(record);
    setIsViewCustomerAddressModal(true);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfType = () => {
    let { type } = isViewRecord;
    // 客户类型 0、个人客户 1、租户商家客户 2、租户(dao)客户 3、顶级平台商家客户
    if (type == '0') {
      return '个人客户';
    } else if (type == '1') {
      return '商户客户';
    } else if (type == '2') {
      return '租户客户';
    } else if (type == '3') {
      return '顶级平台商家客户';
    }
    return type;
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="客户信息" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getCustomerPageList({
            ...params,
            type: '0',
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
        toolBarRender={() => [
          <Button
            onClick={() => {
              increaseCustomer();
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            新增
          </Button>,
        ]}
      />
      {/* 新增模态框 */}
      <Modal
        title="新增"
        centered
        open={isCustomerModal}
        onOk={confirmCustomer}
        onCancel={onCancelCustomer}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ type: '0' }}
        >
          <Form.Item
            label="客户类型"
            name="type"
            rules={[{ required: true, message: '请选择客户类型!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="0">个人客户</Option>
              <Option value="1">商户客户</Option>
              <Option value="2">租户客户</Option>
              <Option value="3">顶级平台商家客户</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="客户用户名"
            name="name"
            rules={[{ required: true, message: '请输入客户用户名!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="描述"
            name="description"
            rules={[{ required: true, message: '请输入描述!' }]}
          >
            <TextArea />
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看详情模态框 */}
      <Modal
        title="查看客户详情"
        width={800}
        centered
        visible={isViewCustomerModal}
        onOk={() => setIsViewCustomerModal(false)}
        onCancel={() => setIsViewCustomerModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="客户信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="客户编号">
            {isViewRecord?.customerNo}
          </Descriptions.Item>
          <Descriptions.Item label="客户类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="登录名">
            {isViewRecord?.username}
          </Descriptions.Item>
          <Descriptions.Item label="手机号">
            {isViewRecord?.phone}
          </Descriptions.Item>
          <Descriptions.Item label="头像">
            {isViewRecord?.avatar}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="描述">
            {isViewRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>

      {/* 查看客户地址详情模态框 */}
      <Modal
        title="查看客户收货地址详情"
        width={1600}
        centered
        open={isViewCustomerAddressModal}
        onOk={() => setIsViewCustomerAddressModal(false)}
        onCancel={() => setIsViewCustomerAddressModal(false)}
      >
        <ProTable<columnsDeliveryAddressDataType>
          headerTitle={<TableTitle title="收货地址" />}
          scroll={{ x: 900 }}
          bordered
          // 表头
          columns={columnsDeliveryAddress}
          actionRef={deliveryAddressActionRef}
          // 请求获取的数据
          request={async (params) => {
            // console.log(params);
            let res = await getCustomerAddressDetail({
              ...params,
              customerNo: isViewAddressRecord?.customerNo,
            });
            console.log('😒', res);
            const result = {
              data: res.data,
              // total: res.pagination.totalSize,
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
        />
      </Modal>
    </div>
  );
};
