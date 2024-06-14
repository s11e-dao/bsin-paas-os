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
  getApiFeeConfigPageList,
  getApiFeeConfigDetails,
  editApiFeeConfig,
} from './service';
import TableTitle from '../../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制编辑模态框
  const [editModal, setEditModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 获取表单
  const [editFormRef] = Form.useForm();

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      {record.status == '0' ? (
        <li>
          <a
            onClick={() => {
              toEditApiFeeConfig(record);
            }}
          >
            审核
          </a>
        </li>
      ) : null}
    </ul>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  /**
   * 确认api费用配置
   */
  const confirmApiFeeConfig = () => {
    // 获取输入的表单值
    editFormRef
      .validateFields()
      .then(async () => {
        // 获取表单结果
        let response = editFormRef.getFieldsValue();
        console.log(response);
        editApiFeeConfig(response).then((res) => {
          console.log('add', res);
          if (res.code === '000000') {
            // 重置输入的表单
            editFormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setEditModal(false);
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
  const onCancelApiFeeConfig = () => {
    // 重置输入的表单
    editFormRef.resetFields();
    setEditModal(false);
  };

  // 点击编辑
  const toEditApiFeeConfig = async (record) => {
    let { serialNo } = record;
    let viewRes = await getApiFeeConfigDetails({ serialNo });
    setEditModal(true);
    // 存储id
    //setRoleId(record.roleId);
    // 数据回显
    editFormRef.setFieldsValue(record);
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="商户应用api费用配置" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getApiFeeConfigPageList({
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
      />
      {/* 新增合约模板模态框 */}
      <Modal
        title="配置费用"
        centered
        visible={editModal}
        onOk={confirmApiFeeConfig}
        onCancel={onCancelApiFeeConfig}
      >
        <Form
          name="basic"
          form={editFormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ type: '0' }}
        >
          <Form.Item label="ID" name="serialNo">
            <Input disabled />
          </Form.Item>
          <Form.Item label="租户ID" name="tenantId">
            <Input disabled />
          </Form.Item>
          <Form.Item label="产品ID" name="productId">
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="费用"
            name="fee"
            rules={[{ required: true, message: '请输入费用!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="免费次数"
            name="freeTimes"
            rules={[{ required: true, message: '请输入免费次数!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="状态"
            name="status"
            rules={[{ required: true, message: '请输入状态' }]}
          >
            <Select>
              <Option value={0}>待审核</Option>
              <Option value={1}>已生效</Option>
            </Select>
          </Form.Item>
          <Form.Item label="创建时间" name="createTime">
            <Input disabled />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};
