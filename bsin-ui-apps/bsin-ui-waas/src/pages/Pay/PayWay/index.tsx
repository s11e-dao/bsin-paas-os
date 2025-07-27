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
              label="支付方式类型"
              name="payWayType"
              rules={[
                { required: true, message: '请选择支付方式类型!' },
              ]}
              tooltip="支付方式的类型"
            >
              <Select 
                placeholder="请选择支付方式类型"
                disabled={isEdit}
                onChange={(value) => {
                  // 当选择支付方式类型时，自动设置对应的编码
                  formRef.setFieldsValue({ payWay: value });
                }}
              >
                <Option value="WX_H5">微信H5</Option>
                <Option value="WX_JSAPI">微信公众号</Option>
                <Option value="WX_LITE">微信小程序</Option>
                <Option value="WX_NATIVE">微信扫码</Option>
                <Option value="WX_BAR">微信条码</Option>
                <Option value="WX_APP">微信APP</Option>
                <Option value="WX">微信支付</Option>
                <Option value="ALI">支付宝支付</Option>
                <Option value="ALI_APP">支付宝App</Option>
                <Option value="ALI_BAR">支付宝条码</Option>
                <Option value="ALI_JSAPI">支付宝生活号</Option>
                <Option value="ALI_LITE">支付宝小程序</Option>
                <Option value="ALI_PC">支付宝PC网站</Option>
                <Option value="ALI_QR">支付宝二维码</Option>
                <Option value="ALI_WAP">支付宝WAP</Option>
                <Option value="XLALILITE">信联支付宝支付</Option>
                <Option value="YSF_BAR">云闪付条码</Option>
                <Option value="YSF_JSAPI">云闪付jsapi</Option>
                <Option value="YSF_LITE">云闪付小程序</Option>
                <Option value="UP_QR">银联二维码(主扫)</Option>
                <Option value="UP_BAR">银联二维码(被扫)</Option>
                <Option value="UP_APP">银联App支付</Option>
                <Option value="QR_CASHIER">聚合</Option>
                <Option value="QQ_PAY">钱包</Option>
                <Option value="PP_PC">PayPal支付</Option>
                <Option value="ICBC_APP">工行APP支付</Option>
              </Select>
            </Form.Item>

            <Form.Item
              label="支付方式编码"
              name="payWay"
              rules={[
                { required: true, message: '请输入支付方式编码!' },
                { max: 50, message: '支付方式编码不能超过50个字符!' },
              ]}
              tooltip="支付方式的编码（根据选择的类型自动生成）"
            >
              <Input placeholder="支付方式编码将根据选择的类型自动生成" disabled={true}/>
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
                      <Descriptions.Item label="支付方式类型" span={1}>
                                     {viewRecord.payWayType === 'WX_H5' ? '微信H5' :
                          viewRecord.payWayType === 'WX_JSAPI' ? '微信公众号' :
                          viewRecord.payWayType === 'WX_LITE' ? '微信小程序' :
                          viewRecord.payWayType === 'WX_NATIVE' ? '微信扫码' :
                          viewRecord.payWayType === 'WX_BAR' ? '微信条码' :
                          viewRecord.payWayType === 'WX_APP' ? '微信APP' :
                          viewRecord.payWayType === 'WX' ? '微信支付' :
                          viewRecord.payWayType === 'ALI' ? '支付宝支付' :
                          viewRecord.payWayType === 'ALI_APP' ? '支付宝App' :
                          viewRecord.payWayType === 'ALI_BAR' ? '支付宝条码' :
                          viewRecord.payWayType === 'ALI_JSAPI' ? '支付宝生活号' :
                          viewRecord.payWayType === 'ALI_LITE' ? '支付宝小程序' :
                          viewRecord.payWayType === 'ALI_PC' ? '支付宝PC网站' :
                          viewRecord.payWayType === 'ALI_QR' ? '支付宝二维码' :
                          viewRecord.payWayType === 'ALI_WAP' ? '支付宝WAP' :
                          viewRecord.payWayType === 'XLALILITE' ? '信联支付宝支付' :
                          viewRecord.payWayType === 'YSF_BAR' ? '云闪付条码' :
                          viewRecord.payWayType === 'YSF_JSAPI' ? '云闪付jsapi' :
                          viewRecord.payWayType === 'YSF_LITE' ? '云闪付小程序' :
                          viewRecord.payWayType === 'UP_QR' ? '银联二维码(主扫)' :
                          viewRecord.payWayType === 'UP_BAR' ? '银联二维码(被扫)' :
                          viewRecord.payWayType === 'UP_APP' ? '银联App支付' :
                          viewRecord.payWayType === 'QR_CASHIER' ? '聚合' :
                          viewRecord.payWayType === 'QQ_PAY' ? '钱包' :
                          viewRecord.payWayType === 'PP_PC' ? 'PayPal支付' :
                          viewRecord.payWayType === 'ICBC_APP' ? '工行APP支付' :
                          viewRecord.payWayType}
            </Descriptions.Item>
            <Descriptions.Item label="支付方式编码" span={1}>
              {viewRecord.payWay}
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