import React, { useState } from 'react';
import { history } from '@umijs/max';
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Select,
  Popconfirm,
  Descriptions,
  Divider,
  Radio,
  Tag,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData from './data';
import type { columnsDataType } from './data';
import {
  getWalletPageList,
  addWallet,
  editWallet,
  deleteWallet,
  getWalletDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;

  // 控制新增/编辑模态框
  const [isModalVisible, setIsModalVisible] = useState(false);
  // 查看模态框
  const [isViewModalVisible, setIsViewModalVisible] = useState(false);
  // 查看详情数据
  const [viewRecord, setViewRecord] = useState<any>({});
  // 编辑模式标识
  const [isEditMode, setIsEditMode] = useState(false);
  // 当前编辑的记录ID
  const [currentSerialNo, setCurrentSerialNo] = useState('');
  // 获取表单
  const [form] = Form.useForm();

  /**
   * 业务角色类型配置
   */
  const getBizRoleTypeConfig = (bizRoleType: string) => {
    const configMap: { [key: string]: { color: string; text: string } } = {
      '1': { color: 'blue', text: '运营平台' },
      '2': { color: 'orange', text: '租户平台' },
      '4': { color: 'green', text: '合伙人' },
      '5': { color: 'red', text: '租户客户' },
      '6': { color: 'purple', text: '门店' },
      '99': { color: 'default', text: '无' },
    };
    return configMap[bizRoleType] || { color: 'default', text: '-' };
  };

  /**
   * 以下内容为表格相关
   */

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  // 表头数据
  const columns: any[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <div key={record.serialNo}>
      <a onClick={() => handleView(record)}>查看</a>
      <Divider type="vertical" />
      <a onClick={() => handleEdit(record)}>编辑</a>
      <Divider type="vertical" />
      <a onClick={() => handleViewAccounts(record)}>钱包账户</a>
      <Divider type="vertical" />
      <Popconfirm
        title="确认删除此钱包吗?"
        onConfirm={() => handleDelete(record.serialNo)}
        onCancel={() => {
          message.warning('取消删除');
        }}
        okText="确认"
        cancelText="取消"
      >
        <a style={{ color: 'red' }}>删除</a>
      </Popconfirm>
    </div>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  /**
   * 新增钱包
   */
  const handleAdd = () => {
    setIsEditMode(false);
    setCurrentSerialNo('');
    form.resetFields();
    setIsModalVisible(true);
  };

  /**
   * 编辑钱包
   */
  const handleEdit = async (record: any) => {
    setIsEditMode(true);
    setCurrentSerialNo(record.serialNo);
    const { serialNo } = record;
    const viewRes = await getWalletDetail({ serialNo });
    if (viewRes.code === 0) {
      form.setFieldsValue({
        ...viewRes.data,
      });
      setIsModalVisible(true);
    } else {
      message.error(`获取详情失败：${viewRes?.message}`);
    }
  };

  /**
   * 确认添加或编辑
   */
  const handleSubmit = () => {
    form.validateFields()
      .then(async () => {
        const values = form.getFieldsValue();
        const reqParam = {
          ...values,
          serialNo: isEditMode ? currentSerialNo : undefined,
        };

        const apiCall = isEditMode ? editWallet : addWallet;
        const res = await apiCall(reqParam);

        if (res.code === 0) {
          message.success(isEditMode ? '编辑成功' : '添加成功');
          form.resetFields();
          setIsModalVisible(false);
          actionRef.current?.reload();
        } else {
          message.error(`操作失败：${res?.message}`);
        }
      })
      .catch(() => { });
  };

  /**
   * 取消添加或编辑
   */
  const handleCancel = () => {
    form.resetFields();
    setIsModalVisible(false);
  };

  /**
   * 删除钱包
   */
  const handleDelete = async (serialNo: string) => {
    const delRes = await deleteWallet({ serialNo });
    if (delRes.code === 0) {
      message.success('删除成功');
      actionRef.current?.reload();
    } else {
      message.error(`删除失败：${delRes?.message}`);
    }
  };

  /**
   * 查看详情
   */
  const handleView = async (record: any) => {
    const { serialNo } = record;
    const viewRes = await getWalletDetail({ serialNo });
    if (viewRes.code === 0) {
      setViewRecord(viewRes.data);
      setIsViewModalVisible(true);
    } else {
      message.error(`获取详情失败：${viewRes?.message}`);
    }
  };

  /**
   * 查看钱包账户
   */
  const handleViewAccounts = (record: any) => {
    // 使用路由跳转到链账户页面，传递钱包ID作为参数
    history.push(`/wallet/chain-account?walletSerialNo=${record.serialNo}`);
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="钱包管理" />}
        scroll={{ x: 1800 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          const res = await getWalletPageList({
            ...params,
          });
          const result = {
            data: res.data || [],
            total: res.pagination?.totalSize || 0,
            success: res.code === 0,
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
          showSizeChanger: true,
        }}
        toolBarRender={() => [
          <Button
            onClick={handleAdd}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            添加钱包
          </Button>,
        ]}
      />

      {/* 新增/编辑钱包模态框 */}
      <Modal
        title={isEditMode ? '编辑钱包' : '添加钱包'}
        width={800}
        centered
        open={isModalVisible}
        onOk={handleSubmit}
        onCancel={handleCancel}
        okText="确认"
        cancelText="取消"
      >
        <Form
          name="walletForm"
          form={form}
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 16 }}
          initialValues={{ type: '1', category: '1', env: 'EVM', walletTag: 'DEPOSIT', status: '1' }}
        >
          <Form.Item
            label="钱包名称"
            name="walletName"
            rules={[{ required: true, message: '请输入钱包名称!' }]}
          >
            <Input placeholder="请输入钱包名称" maxLength={128} disabled={isEditMode} />
          </Form.Item>

          <Form.Item
            label="钱包类型"
            name="type"
            rules={[{ required: true, message: '请选择钱包类型!' }]}
          >
            <Radio.Group disabled={isEditMode}>
              <Radio value="1">默认钱包</Radio>
              <Radio value="2">自定义钱包</Radio>
            </Radio.Group>
          </Form.Item>

          <Form.Item
            label="业务角色类型"
            name="bizRoleType"
            rules={[{ required: true, message: '请选择业务角色类型!' }]}
          >
            <Select placeholder="请选择业务角色类型">
              <Option value="1">运营平台</Option>
              <Option value="2">租户平台</Option>
              <Option value="4">合伙人</Option>
              <Option value="5">租户客户</Option>
              <Option value="6">门店</Option>
              <Option value="99">无</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="钱包分类"
            name="category"
            rules={[{ required: true, message: '请选择钱包分类!' }]}
          >
            <Radio.Group disabled={isEditMode}>
              <Radio value="1">MPC</Radio>
              <Radio value="2">多签</Radio>
              <Radio value="3">EOA钱包</Radio>
            </Radio.Group>
          </Form.Item>

          <Form.Item
            label="钱包环境"
            name="env"
            rules={[{ required: true, message: '请输入钱包环境!' }]}
          >
            <Input placeholder="例如：EVM" disabled={isEditMode} />
          </Form.Item>

          <Form.Item
            label="钱包标签"
            name="walletTag"
            rules={[{ required: true, message: '请选择钱包标签!' }]}
          >
            <Select placeholder="请选择钱包标签">
              <Option value="NONE">无</Option>
              <Option value="DEPOSIT">寄存</Option>
              <Option value="GATHER">归集</Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="钱包状态"
            name="status"
            rules={[{ required: true, message: '请选择钱包状态!' }]}
          >
            <Radio.Group>
              <Radio value="1">正常</Radio>
              <Radio value="2">冻结</Radio>
              <Radio value="3">注销</Radio>
            </Radio.Group>
          </Form.Item>

          <Form.Item
            label="外部用户ID"
            name="outUserId"
          >
            <Input placeholder="请输入外部用户ID" maxLength={64} />
          </Form.Item>

          <Form.Item
            label="备注"
            name="remark"
          >
            <TextArea rows={3} placeholder="请输入备注信息" maxLength={500} />
          </Form.Item>
        </Form>
      </Modal>

      {/* 查看详情模态框 */}
      <Modal
        title="钱包详情"
        width={900}
        centered
        open={isViewModalVisible}
        onOk={() => setIsViewModalVisible(false)}
        onCancel={() => setIsViewModalVisible(false)}
        footer={[
          <Button key="close" type="primary" onClick={() => setIsViewModalVisible(false)}>
            关闭
          </Button>
        ]}
      >
        <Descriptions bordered column={2}>
          <Descriptions.Item label="钱包ID" span={2}>
            {viewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="钱包名称">
            {viewRecord?.walletName}
          </Descriptions.Item>
          <Descriptions.Item label="钱包类型">
            <Tag color={viewRecord?.type === '1' ? 'blue' : 'orange'}>
              {viewRecord?.type === '1' ? '默认钱包' : viewRecord?.type === '2' ? '自定义钱包' : '-'}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="钱包状态">
            <Tag color={viewRecord?.status === '1' ? 'green' : viewRecord?.status === '2' ? 'red' : 'default'}>
              {viewRecord?.status === '1' ? '正常' : viewRecord?.status === '2' ? '冻结' : '注销'}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="钱包分类">
            {viewRecord?.category === '1' ? 'MPC' : viewRecord?.category === '2' ? '多签' : 'EOA钱包'}
          </Descriptions.Item>
          <Descriptions.Item label="钱包环境">
            {viewRecord?.env}
          </Descriptions.Item>
          <Descriptions.Item label="钱包标签">
            {viewRecord?.walletTag === 'NONE' ? '无' : viewRecord?.walletTag === 'DEPOSIT' ? '寄存' : '归集'}
          </Descriptions.Item>
          <Descriptions.Item label="余额">
            {viewRecord?.balance || '0'}
          </Descriptions.Item>
          <Descriptions.Item label="外部用户ID">
            {viewRecord?.outUserId || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="业务角色类型">
            {(() => {
              const config = getBizRoleTypeConfig(viewRecord?.bizRoleType);
              return <Tag color={config.color}>{config.text}</Tag>;
            })()}
          </Descriptions.Item>
          <Descriptions.Item label="业务角色编号">
            {viewRecord?.bizRoleTypeNo || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="租户ID">
            {viewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="备注" span={2}>
            {viewRecord?.remark || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {viewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {viewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="更新者">
            {viewRecord?.updateBy || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="更新时间">
            {viewRecord?.updateTime || '-'}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};

