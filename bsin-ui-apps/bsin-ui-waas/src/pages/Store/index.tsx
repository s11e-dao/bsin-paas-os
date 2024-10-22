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
  getStorePageList,
  openStore,
  editStore,
  deleteStore,
  getStoreDetail,
} from './service';
import TableTitle from '../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [addStoreModal, setAddStoreModal] = useState(false);
  // 查看模态框
  const [isViewTemplateModal, setIsViewTemplateModal] = useState(false);
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
            toViewStore(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <Popconfirm
          title="确定删除此条记录？"
          okText="是"
          cancelText="否"
          onConfirm={() => {
            toDelStore(record);
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

  // 新增弹框
  const openAddStoreModal = () => {
    setAddStoreModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmOpenStore = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        openStore(response).then((res) => {
          console.log('add', res);

          if (res.code === '000000' || res.code === 0) {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setAddStoreModal(false);
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
  const onCancelOpenStore = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setAddStoreModal(false);
  };

  /**
   * 删除模板
   */
  const toDelStore = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteStore({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === '000000' || delRes.code === 0) {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewStore = async (record) => {
    let { serialNo } = record;
    let viewRes = await getStoreDetail({ serialNo });
    setIsViewTemplateModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，店鋪類型
   */
  const handleViewRecordOfType = () => {
    let { type } = isViewRecord;    
    if (type == '0') {
      return '总店';
    } else if (type == '1') {
      return '非总店';
    } else {
      return type;
    }
  };

  /**
   * 详情，经营模式  1、直营，2、加盟
   */
  const handleViewRecordOfBusinessModel = () => {
    let { businessModel } = isViewRecord;    
    if (businessModel == '1') {
      return '直营';
    } else if (type == '2') {
      return '加盟';
    } else {
      return businessModel;
    }
  };
  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="门店列表" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getStorePageList({
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
              openAddStoreModal();
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            开店
          </Button>,
        ]}
      />
      {/* 新增店铺模板模态框 */}
      <Modal
        title="新增"
        centered
        open={addStoreModal}
        onOk={confirmOpenStore}
        onCancel={onCancelOpenStore}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
        >
          <Form.Item
            label="店铺名称"
            name="storeName"
            rules={[{ required: true, message: '请输入店铺名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="店铺logo"
            name="logo"
            rules={[{ required: true, message: '请上传店铺logo!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="店铺地址"
            name="address"
            rules={[{ required: true, message: '请输入店铺地址!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="店铺经度"
            name="longitude"
            rules={[{ required: true, message: '请输入店铺经度!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="店铺纬度"
            name="latitude"
            rules={[{ required: true, message: '请输入店铺纬度!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="店铺营业时间"
            name="businessHours"
            rules={[{ required: true, message: '请输入店铺营业时间!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="店铺描述"
            name="description"
            rules={[{ required: true, message: '请输入店铺描述!' }]}
          >
            <TextArea />
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看详情模态框 */}
      <Modal
        title="详情"
        width={800}
        centered
        open={isViewTemplateModal}
        onOk={() => setIsViewTemplateModal(false)}
        onCancel={() => setIsViewTemplateModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="店鋪信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="店铺编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="店铺类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="经营模式">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="店铺名称">
            {isViewRecord?.storeName}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="店铺描述">
            {isViewRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
