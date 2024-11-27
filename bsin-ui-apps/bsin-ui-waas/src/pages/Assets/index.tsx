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
  getTransferJournalPageList,
  getTransferJournalDetail,
  addTransferJournal,
  deleteTransferJournal,
} from './service';
import TableTitle from '../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isTransferJournalModal, setIsTransferJournalModal] = useState(false);
  // 查看模态框
  const [isViewTransferJournalModal, setIsViewTransferJournalModal] = useState(
    false,
  );
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
            toViewTransferJournal(record);
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
            toDelTransferJournal(record);
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

  // 转让
  const increaseTransferJournal = () => {
    setIsTransferJournalModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmTransferJournal = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        addTransferJournal(response).then((res) => {
          if (res?.code == 0) {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsTransferJournalModal(false);
          } else {
            message.error(`转让失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  /**
   * 取消添加模板
   */
  const onCancelTransferJournal = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsTransferJournalModal(false);
  };

  /**
   * 删除模板
   */
  const toDelTransferJournal = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteTransferJournal({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === 0) {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewTransferJournal = async (record) => {
    let { serialNo } = record;
    let viewRes = await getTransferJournalDetail({ serialNo });
    setIsViewTransferJournalModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfType = () => {
    let { type } = isViewRecord;
    let typeText = type;
    return typeText;
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="资产转让" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getTransferJournalPageList({
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
              increaseTransferJournal();
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            添加记录
          </Button>,
        ]}
      />
      {/* 新增合约模板模态框 */}
      <Modal
        title="转让"
        centered
        visible={isTransferJournalModal}
        onOk={confirmTransferJournal}
        onCancel={onCancelTransferJournal}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ type: 'ERC20' }}
        >
          <Form.Item
            label="资产集合编号"
            name="digitalAssetsCollectionNo"
            rules={[{ required: true, message: '请输入资产集合编号名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="tokenId"
            name="tokenId"
            rules={[{ required: true, message: '请输入tokenId!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="amount"
            name="amount"
            rules={[{ required: true, message: '请输入amount!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="fromAddress"
            name="fromAddress"
            rules={[{ required: true, message: '请输入fromAddress!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="toAddress"
            name="toAddress"
            rules={[{ required: true, message: '请输入toAddress!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="交易Hash"
            name="txHash"
            rules={[{ required: true, message: '请输入txHash!' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看详情模态框 */}
      <Modal
        title="查看转让"
        width={800}
        centered
        visible={isViewTransferJournalModal}
        onOk={() => setIsViewTransferJournalModal(false)}
        onCancel={() => setIsViewTransferJournalModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="转让信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="商户ID">
            {isViewRecord?.merchantNo}
          </Descriptions.Item>
          <Descriptions.Item label="协议编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="资产集合编号">
            {isViewRecord?.digitalAssetsCollectionNo}
          </Descriptions.Item>
          <Descriptions.Item label="交易Hash">
            {isViewRecord?.txHash}
          </Descriptions.Item>
          <Descriptions.Item label="fromAddress">
            {isViewRecord?.fromAddress}
          </Descriptions.Item>
          <Descriptions.Item label="toAddress">
            {isViewRecord?.toAddress}
          </Descriptions.Item>
          <Descriptions.Item label="fromCustomerNo">
            {isViewRecord?.fromCustomerNo}
          </Descriptions.Item>
          <Descriptions.Item label="toCustomerNo">
            {isViewRecord?.toCustomerNo}
          </Descriptions.Item>
          <Descriptions.Item label="toPhone">
            {isViewRecord?.toPhone}
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
