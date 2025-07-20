import React, { useState } from 'react';
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Select,
  Popconfirm,
  Divider,
  Descriptions,
  Tooltip,
  Space,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined, EditOutlined, EyeOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getPayWayPageList,
  addPayWay,
  editPayWay,
  deletePayWay,
  getPayWayDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';

const PayWay: React.FC = () => {
  const { TextArea } = Input;
  const { Option } = Select;
  
  // 控制模态框状态
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [isViewModalVisible, setIsViewModalVisible] = useState(false);
  const [modalTitle, setModalTitle] = useState('添加支付方式');
  const [isEdit, setIsEdit] = useState(false);
  const [loading, setLoading] = useState(false);
  
  // 数据状态
  const [viewRecord, setViewRecord] = useState<columnsDataType>({} as columnsDataType);
  const [currentRecord, setCurrentRecord] = useState<columnsDataType>({} as columnsDataType);
  
  // 获取表单
  const [formRef] = Form.useForm();

  /**
   * 以下内容为表格相关
   */
  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;
  
  // 操作行数据 自定义操作行
  const actionRender = (text: any, record: columnsDataType, index: number) => (
    <Space key={record.serialNo}>
      <Tooltip title="查看详情">
        <a onClick={() => handleView(record)}>
          <EyeOutlined />
        </a>
      </Tooltip>
      <Divider type="vertical" />
      <Tooltip title="编辑">
        <a onClick={() => handleEdit(record)}>
          <EditOutlined />
        </a>
      </Tooltip>
      <Divider type="vertical" />
      <Popconfirm
        title="确定要删除这个支付方式吗？"
        onConfirm={() => handleDelete(record)}
        okText="确定"
        cancelText="取消"
      >
        <Tooltip title="删除">
          <a style={{ color: '#ff4d4f' }}>
            <DeleteOutlined />
          </a>
        </Tooltip>
      </Popconfirm>
    </Space>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    if (item.dataIndex === 'action') {
      item.render = actionRender;
    }
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  /**
   * 打开新增模态框
   */
  const handleAdd = () => {
    setModalTitle('添加支付方式');
    setIsEdit(false);
    setCurrentRecord({} as columnsDataType);
    formRef.resetFields();
    setIsModalVisible(true);
  };

  /**
   * 打开编辑模态框
   */
  const handleEdit = (record: columnsDataType) => {
    setModalTitle('编辑支付方式');
    setIsEdit(true);
    setCurrentRecord(record);
    formRef.setFieldsValue(record);
    setIsModalVisible(true);
  };

  /**
   * 查看详情
   */
  const handleView = async (record: columnsDataType) => {
    try {
      const res = await getPayWayDetail({ serialNo: record.serialNo });
      if (res?.data) {
        setViewRecord(res.data);
        setIsViewModalVisible(true);
      }
    } catch (error) {
      message.error('获取详情失败');
    }
  };

  /**
   * 删除支付方式
   */
  const handleDelete = async (record: columnsDataType) => {
    try {
      const res = await deletePayWay({ serialNo: record.serialNo });
      if (res.code === 0 || res.code === '000000') {
        message.success('删除成功');
        actionRef.current?.reload();
      } else {
        message.error(res.message || '删除失败');
      }
    } catch (error) {
      message.error('删除失败');
    }
  };

  /**
   * 确认保存
   */
  const handleSave = async () => {
    try {
      await formRef.validateFields();
      setLoading(true);
      
      const values = formRef.getFieldsValue();
      const requestData = {
        ...values,
      };
      
      const res = isEdit 
        ? await editPayWay({ ...requestData, serialNo: currentRecord.serialNo })
        : await addPayWay(requestData);
        
      if (res.code === 0 || res.code === '000000') {
        message.success(isEdit ? '更新成功' : '添加成功');
        setIsModalVisible(false);
        actionRef.current?.reload();
      } else {
        message.error(res.message || '操作失败');
      }
    } catch (error) {
      console.error('保存失败:', error);
    } finally {
      setLoading(false);
    }
  };

  /**
   * 取消操作
   */
  const handleCancel = () => {
    setIsModalVisible(false);
    formRef.resetFields();
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="支付方式管理" />}
        scroll={{ x: 1200 }}
        bordered
        columns={columns}
        actionRef={actionRef}
        request={async (params) => {
          try {
            const res = await getPayWayPageList({
              ...params,
              pagination: {
                pageNum: params.current,
                pageSize: params.pageSize,
              },
            });
            return {
              data: res.data?.records || res.data || [],
              total: res.data?.total || res.pagination?.totalSize || 0,
            };
          } catch (error) {
            console.error('获取列表失败:', error);
            return { data: [], total: 0 };
          }
        }}
        rowKey="serialNo"
        search={{
          labelWidth: 'auto',
          collapsed: false,
          collapseRender: (collapsed) => (collapsed ? '展开' : '收起'),
        }}
        form={{
          ignoreRules: false,
        }}
        pagination={{
          pageSize: 10,
        }}
        toolBarRender={() => [
          <Button
            key="refresh"
            icon={<ReloadOutlined />}
            onClick={() => actionRef.current?.reload()}
          >
            刷新
          </Button>,
          <Button
            key="add"
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleAdd}
          >
            添加支付方式
          </Button>,
        ]}
      />
      
      {/* 新增/编辑模态框 */}
      <Modal
        title={modalTitle}
        open={isModalVisible}
        onOk={handleSave}
        onCancel={handleCancel}
        confirmLoading={loading}
        width={600}
        destroyOnClose
      >
        <Form
          form={formRef}
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 16 }}
          layout="horizontal"
        >
          <Form.Item
            label="支付方式名称"
            name="payWayName"
            rules={[
              { required: true, message: '请输入支付方式名称!' },
              { max: 50, message: '支付方式名称不能超过50个字符!' },
            ]}
            tooltip="支付方式的显示名称，如：微信支付、支付宝"
          >
            <Input placeholder="请输入支付方式名称" />
          </Form.Item>
          <Form.Item
            label="支付方式编码"
            name="payWayCode"
            rules={[
              { required: true, message: '请选择支付方式编码!' },
            ]}
            tooltip="支付方式的唯一标识代码"
          >
            <Select 
              placeholder="请选择支付方式编码"
              disabled={isEdit}
            >
              <Option value="wxPay">微信支付</Option>
              <Option value="aliPay">支付宝支付</Option>
              <Option value="brandsPoint">品牌积分支付</Option>
              <Option value="fireDiamond">火钻支付</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
      
      {/* 查看详情模态框 */}
      <Modal
        title="支付方式详情"
        open={isViewModalVisible}
        onCancel={() => setIsViewModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setIsViewModalVisible(false)}>
            关闭
          </Button>,
        ]}
        width={800}
      >
        <Descriptions bordered column={2}>
          <Descriptions.Item label="ID" span={1}>
            {viewRecord.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="支付方式名称" span={1}>
            {viewRecord.payWayName}
          </Descriptions.Item>
          <Descriptions.Item label="支付方式编码" span={1}>
            {viewRecord.payWayCode === 'wxPay' ? '微信支付' :
             viewRecord.payWayCode === 'aliPay' ? '支付宝支付' :
             viewRecord.payWayCode === 'brandsPoint' ? '品牌积分支付' :
             viewRecord.payWayCode === 'fireDiamond' ? '火钻支付' :
             viewRecord.payWayCode}
          </Descriptions.Item>
          <Descriptions.Item label="租户ID" span={1}>
            {viewRecord.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间" span={1}>
            {viewRecord.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="更新时间" span={1}>
            {viewRecord.updateTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};

export default PayWay;