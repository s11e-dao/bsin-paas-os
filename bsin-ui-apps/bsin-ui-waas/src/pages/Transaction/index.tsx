import React, { useState, useRef } from 'react';
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Select,
  Popconfirm,
  Descriptions,
  Tag,
  Divider
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getTransactionPageList,
  addTransaction,
  deleteTransaction,
  getTransactionDetail,
  getProductList,
} from './service';
import TableTitle from '../../components/TableTitle';
import { hex_md5 } from '../../utils/md5';
import { formatAmount } from '@/utils/common';

const { TextArea } = Input;
const { Option } = Select;

/**
 * 交易页面组件
 */
const TransactionPage: React.FC = () => {
  // 控制新增模态框
  const [isAddModalVisible, setIsAddModalVisible] = useState(false);
  // 查看模态框
  const [isViewModalVisible, setIsViewModalVisible] = useState(false);
  // 查看记录
  const [viewRecord, setViewRecord] = useState<columnsDataType>({} as columnsDataType);
  // 产品列表
  const [productList, setProductList] = useState<any[]>([]);
  // 获取表单
  const [form] = Form.useForm();

  // Table action 的引用，便于自定义触发
  const actionRef = useRef<ActionType>();

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender = (text: any, record: columnsDataType, index: number) => (
    <div key={record.serialNo}>
      <a onClick={() => {
        handleViewDetail(record);
      }}>查看</a>
      <Divider type="vertical" />
      <Popconfirm
        title="是否删除此条数据？"
        onConfirm={() => {
          handleDelete(record);
        }}
        onCancel={() => {
          message.warning('取消删除！');
        }}
        okText="是"
        cancelText="否"
      >
        <a>删除</a>
      </Popconfirm>
    </div>
  );

  // 交易类型自定义渲染
  const transactionTypeRender = (text: any, record: columnsDataType, index: number) => {
    let tag = <Tag color="purple">资金归集</Tag>;
    
    if (record.transactionTypeRender === 0) {
      tag = <Tag color="blue">转入</Tag>;
    } else if (record.transactionTypeRender === 1) {
      tag = <Tag color="cyan">转出</Tag>;
    }
  
    return <div key={record.userId}>{tag}</div>;
  };

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    if (item.dataIndex === 'action') {
      item.render = actionRender;
    }
    if (item.dataIndex === 'transactionType') {
      item.render = transactionTypeRender;
    }
  });

  /**
   * 新增交易
   */
  const handleAdd = () => {
    setIsAddModalVisible(true);
    getProductList({}).then((res) => {
      setProductList(res?.data || []);
    }).catch((error) => {
      console.error('获取产品列表失败:', error);
      message.error('获取产品列表失败');
    });
  };

  /**
   * 确认新增
   */
  const handleConfirmAdd = async () => {
    try {
      const values = await form.validateFields();
      const response = await addTransaction(values);
      
      if (response.success) {
        message.success('添加成功');
        setIsAddModalVisible(false);
        form.resetFields();
        actionRef.current?.reload();
      } else {
        message.error(response.message || '添加失败');
      }
    } catch (error) {
      console.error('添加失败:', error);
      message.error('添加失败');
    }
  };

  /**
   * 取消新增
   */
  const handleCancelAdd = () => {
    setIsAddModalVisible(false);
    form.resetFields();
  };

  /**
   * 删除交易
   */
  const handleDelete = async (record: columnsDataType) => {
    try {
      const response = await deleteTransaction({ serialNo: record.serialNo });
      
      if (response.success) {
        message.success('删除成功');
        actionRef.current?.reload();
      } else {
        message.error(response.message || '删除失败');
      }
    } catch (error) {
      console.error('删除失败:', error);
      message.error('删除失败');
    }
  };

  /**
   * 查看详情
   */
  const handleViewDetail = async (record: columnsDataType) => {
    try {
      const { serialNo } = record;
      const response = await getTransactionDetail({ serialNo });
      setViewRecord(response.data || record);
      setIsViewModalVisible(true);
    } catch (error) {
      console.error('获取详情失败:', error);
      message.error('获取详情失败');
    }
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="交易管理" />}
        scroll={{ x: 1400 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          try {
            const res = await getTransactionPageList({
              ...params,
              pagination: {
                pageNum: params.current,
                pageSize: params.pageSize,
              },
            });
            
            const result = {
              data: res.data?.records || res.data || [],
              total: res.data?.total || res.pagination?.totalSize || 0,
            };
            return result;
          } catch (error) {
            console.error('获取交易列表失败:', error);
            return {
              data: [],
              total: 0,
            };
          }
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
        // 分页配置
        pagination={{
          defaultPageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
        }}
        // 工具栏配置
        toolBarRender={() => [
          <Button
            key="add"
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleAdd}
          >
            新增
          </Button>,
        ]}
        // 表格配置
        options={{
          density: true,
          fullScreen: true,
          reload: true,
          setting: true,
        }}
      />

      {/* 新增模态框 */}
      <Modal
        title="新增交易"
        open={isAddModalVisible}
        onOk={handleConfirmAdd}
        onCancel={handleCancelAdd}
        width={600}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{}}
        >
          <Form.Item
            name="productId"
            label="产品"
            rules={[{ required: true, message: '请选择产品' }]}
          >
            <Select placeholder="请选择产品">
              {productList.map((product: any) => (
                <Option key={product.id} value={product.id}>
                  {product.name}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            name="amount"
            label="金额"
            rules={[{ required: true, message: '请输入金额' }]}
          >
            <Input placeholder="请输入金额" type="number" />
          </Form.Item>
          <Form.Item
            name="description"
            label="描述"
          >
            <TextArea placeholder="请输入描述" rows={4} />
          </Form.Item>
        </Form>
      </Modal>

      {/* 查看详情模态框 */}
      <Modal
        title="交易详情"
        open={isViewModalVisible}
        onCancel={() => setIsViewModalVisible(false)}
        footer={null}
        width={800}
      >
        <Descriptions column={2} bordered>
          <Descriptions.Item label="交易ID">{viewRecord.serialNo}</Descriptions.Item>
          <Descriptions.Item label="产品ID">{viewRecord.productId}</Descriptions.Item>
          <Descriptions.Item label="金额">{formatAmount(viewRecord.amount)}</Descriptions.Item>
          <Descriptions.Item label="交易类型">
            {viewRecord.transactionTypeRender === 0 ? '转入' : 
             viewRecord.transactionTypeRender === 1 ? '转出' : '资金归集'}
          </Descriptions.Item>
          <Descriptions.Item label="状态">{viewRecord.status}</Descriptions.Item>
          <Descriptions.Item label="创建时间">{viewRecord.createTime}</Descriptions.Item>
          <Descriptions.Item label="描述" span={2}>{viewRecord.description}</Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};

export default TransactionPage;
