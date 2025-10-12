import React, { useState } from 'react';
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Popconfirm,
  Divider,
  Descriptions,
  Radio,
  Tag,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getChainCoinPageList,
  addChainCoin,
  editChainCoin,
  deleteChainCoin,
  getChainCoinDetail,
} from './service';
import TableTitle from '../../components/TableTitle';

export default () => {

  const { TextArea } = Input;
  
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
   * 以下内容为表格相关
   */

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <div key={record.serialNo}>
      <a onClick={() => handleView(record)}>查看</a>
      <Divider type="vertical" />
      <a onClick={() => handleEdit(record)}>编辑</a>
      <Divider type="vertical" />
      <Popconfirm
        title="确认删除此币种吗?"
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
   * 新增币种
   */
  const handleAdd = () => {
    setIsEditMode(false);
    setCurrentSerialNo('');
    form.resetFields();
    setIsModalVisible(true);
  };

  /**
   * 编辑币种
   */
  const handleEdit = async (record: any) => {
    setIsEditMode(true);
    setCurrentSerialNo(record.serialNo);
    const { serialNo } = record;
    const viewRes = await getChainCoinDetail({ serialNo });
    if (viewRes.code === 0) {
      form.setFieldsValue({
        ...viewRes.data,
        coinDecimal: viewRes.data.coinDecimal?.toString(),
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
        
        const apiCall = isEditMode ? editChainCoin : addChainCoin;
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
      .catch(() => {});
  };

  /**
   * 取消添加或编辑
   */
  const handleCancel = () => {
    form.resetFields();
    setIsModalVisible(false);
  };

  /**
   * 删除币种
   */
  const handleDelete = async (serialNo: string) => {
    const delRes = await deleteChainCoin({ serialNo });
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
    const viewRes = await getChainCoinDetail({ serialNo });
    if (viewRes.code === 0) {
      setViewRecord(viewRes.data);
      setIsViewModalVisible(true);
    } else {
      message.error(`获取详情失败：${viewRes?.message}`);
    }
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="币种管理" />}
        scroll={{ x: 1600 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          const res = await getChainCoinPageList({
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
            添加币种
          </Button>,
        ]}
      />
      
      {/* 新增/编辑币种模态框 */}
      <Modal
        title={isEditMode ? '编辑币种' : '添加币种'}
        width={800}
        centered
        open={isModalVisible}
        onOk={handleSubmit}
        onCancel={handleCancel}
        okText="确认"
        cancelText="取消"
      >
        <Form
          name="chainCoinForm"
          form={form}
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 16 }}
          initialValues={{ status: 0, type: 1 }}
        >
          <Form.Item
            label="链上货币Key"
            name="chainCoinKey"
            rules={[{ required: true, message: '请输入链上货币Key!' }]}
          >
            <Input placeholder="请输入链上货币Key" maxLength={50} />
          </Form.Item>
          
          <Form.Item
            label="币种名称"
            name="chainCoinName"
            rules={[{ required: true, message: '请输入币种名称!' }]}
          >
            <Input placeholder="请输入币种名称" maxLength={100} />
          </Form.Item>
          
          <Form.Item
            label="币种简称"
            name="shortName"
          >
            <Input placeholder="请输入币种简称" maxLength={50} />
          </Form.Item>
          
          <Form.Item
            label="币种符号"
            name="coin"
            rules={[{ required: true, message: '请输入币种符号!' }]}
          >
            <Input placeholder="例如：USDT、ETH" maxLength={20} />
          </Form.Item>
          
          <Form.Item
            label="链名"
            name="chainName"
            rules={[{ required: true, message: '请输入链名!' }]}
          >
            <Input placeholder="例如：Ethereum、BSC" maxLength={50} />
          </Form.Item>
          
          <Form.Item
            label="智能合约地址"
            name="contractAddress"
            rules={[{ required: true, message: '请输入智能合约地址!' }]}
          >
            <Input placeholder="请输入智能合约地址" />
          </Form.Item>
          
          <Form.Item
            label="币种精度"
            name="coinDecimal"
            rules={[{ required: true, message: '请输入币种精度!' }]}
          >
            <Input placeholder="例如：18" />
          </Form.Item>
          
          <Form.Item
            label="单位"
            name="unit"
          >
            <Input placeholder="请输入单位" maxLength={20} />
          </Form.Item>
          
          <Form.Item
            label="状态"
            name="status"
            rules={[{ required: true, message: '请选择状态!' }]}
          >
            <Radio.Group>
              <Radio value={"0"}>下架</Radio>
              <Radio value={"1"}>上架</Radio>
            </Radio.Group>
          </Form.Item>
          
          <Form.Item
            label="类型"
            name="type"
            rules={[{ required: true, message: '请选择类型!' }]}
          >
            <Radio.Group>
              <Radio value={"1"}>默认</Radio>
              <Radio value={"2"}>自定义</Radio>
            </Radio.Group>
          </Form.Item>
          
          <Form.Item
            label="Logo URL"
            name="logoUrl"
          >
            <Input placeholder="请输入币种Logo URL" />
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
        title="币种详情"
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
          <Descriptions.Item label="币种ID" span={2}>
            {viewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="链上货币Key">
            {viewRecord?.chainCoinKey}
          </Descriptions.Item>
          <Descriptions.Item label="币种名称">
            {viewRecord?.chainCoinName}
          </Descriptions.Item>
          <Descriptions.Item label="币种简称">
            {viewRecord?.shortName || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="币种符号">
            {viewRecord?.coin}
          </Descriptions.Item>
          <Descriptions.Item label="链名">
            {viewRecord?.chainName}
          </Descriptions.Item>
          <Descriptions.Item label="币种精度">
            {viewRecord?.coinDecimal?.toString()}
          </Descriptions.Item>
          <Descriptions.Item label="单位">
            {viewRecord?.unit || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="状态">
            <Tag color={viewRecord?.status === "1" ? 'green' : 'default'}>
              {viewRecord?.status === "1" ? '上架' : '下架'}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="类型">
            <Tag color={viewRecord?.type === "1" ? 'blue' : 'orange'}>
              {viewRecord?.type === "1" ? '默认' : '自定义'}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="智能合约地址" span={2}>
            {viewRecord?.contractAddress}
          </Descriptions.Item>
          <Descriptions.Item label="Logo URL" span={2}>
            {viewRecord?.logoUrl || '-'}
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
