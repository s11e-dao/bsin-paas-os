import React, { useState } from 'react';
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Select,
  Popconfirm,
  Descriptions,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getCustomerAccountPageList,
  addCustomerAccount,
  deleteCustomerAccount,
  getCustomerAccountDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isAccountModal, setIsAccountModal] = useState(false);
  // 查看模态框
  const [isViewAccountModal, setIsViewAccountModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 获取表单
  const [FormRef] = Form.useForm();

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewCharge(record);
          }}
        >
          充值
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <a
          onClick={() => {
            toViewAccount(record);
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
            toDelAccount(record);
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

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  /**
   * 以下内容为操作相关
   */

  // 新增模板
  const increaseAccount = () => {
    setIsAccountModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmAccount = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        addCustomerAccount(response).then((res) => {
          console.log('add', res);
          if (res.code === '000000') {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsAccountModal(false);
          } else {
            message.error(`失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  /**
   * 取消添加模板
   */
  const onCancelAccount = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsAccountModal(false);
  };

  /**
   * 删除模板
   */
  const toDelAccount = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteCustomerAccount({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === '000000') {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewAccount = async (record) => {
    let { serialNo } = record;
    let viewRes = await getCustomerAccountDetail({ serialNo });
    setIsViewAccountModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 查看详情
   */
  const toViewCharge = async (record) => {
    let { serialNo } = record;
    setIsAccountModal(true);
  };
  
  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfType = () => {
    let { type } = isViewRecord;
    // 账户类型 0、个人账户 1、企业账户 2 租户(dao)账户
    if (type == '0') {
      return '个人账户';
    } else if (type == '1') {
      return '企业账户';
    } else if (type == '2') {
      return ' 租户(dao)账户';
    } else {
      return type;
    }
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfStatus = () => {
    let { status } = isViewRecord;
    // 账户状态 0、正常 1、冻结
    if (status == '0') {
      return '正常';
    } else if (status == '1') {
      return '冻结';
    } else {
      return status;
    }
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfCategory = () => {
    let { category } = isViewRecord;
    // 账户类别
    if (category == '0') {
      return '正常';
    } else if (category == '1') {
      return '冻结';
    } else {
      return category;
    }
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="账户列表" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getCustomerAccountPageList({
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
              increaseAccount();
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            手动出入账
          </Button>,
        ]}
      />
      {/* 手动出入账 */}
      <Modal
        title="手动出入账"
        centered
        open={isAccountModal}
        onOk={confirmAccount}
        onCancel={onCancelAccount}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ type: '1' }}
        >
          <Form.Item
            label="客户号"
            name="customerNo"
            rules={[{ required: true, message: '请输入客户号!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="账户号"
            name="name"
            rules={[{ required: true, message: '请输入账户号!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="金额"
            name="name"
            rules={[{ required: true, message: '请输入金额!' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>

      {/* 查看详情模态框 */}
      <Modal
        title="查看客户账户"
        width={800}
        centered
        open={isViewAccountModal}
        onOk={() => setIsViewAccountModal(false)}
        onCancel={() => setIsViewAccountModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="客户账户信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="协议编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="客户账户类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="账户名称">
            {isViewRecord?.name}
          </Descriptions.Item>
          <Descriptions.Item label="账户状态">
            {handleViewRecordOfStatus()}
          </Descriptions.Item>
          <Descriptions.Item label="账户类别">
            {handleViewRecordOfCategory()}
          </Descriptions.Item>
          <Descriptions.Item label="账户余额">
            {isViewRecord?.balance}
          </Descriptions.Item>
          <Descriptions.Item label="累计金额">
            {isViewRecord?.cumulativeAmount}
          </Descriptions.Item>
          <Descriptions.Item label="冻结金额">
            {isViewRecord?.freezeAmount}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="协议描述">
            {isViewRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
