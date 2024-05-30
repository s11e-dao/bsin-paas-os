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
  InputNumber,
  Radio,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getTransferJournalPageList,
  addTransferJournal,
  deleteTransferJournal,
  getTransferJournalDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isTemplateModal, setIsTemplateModal] = useState(false);
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
            toViewContractTemplate(record);
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
            toDelContractTemplate(record);
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
  const increaseTemplate = () => {
    setIsTemplateModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmTemplate = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        addTransferJournal(response).then((res) => {
          console.log('add', res);
          if (res?.code == '000000') {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsTemplateModal(false);
          } else {
            message.error(`添加合约协议失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  /**
   * 取消添加模板
   */
  const onCancelTemplate = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsTemplateModal(false);
  };

  /**
   * 删除模板
   */
  const toDelContractTemplate = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteTransferJournal({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === '000000') {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewContractTemplate = async (record) => {
    let { serialNo } = record;
    let viewRes = await getTransferJournalDetail({ serialNo });
    setIsViewTemplateModal(true);
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
        headerTitle={<TableTitle title="转账记录" />}
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
              increaseTemplate();
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            转账
          </Button>,
        ]}
      />
      {/* 新增合约模板模态框 */}
      <Modal
        title="添加合约协议"
        centered
        visible={isTemplateModal}
        onOk={confirmTemplate}
        onCancel={onCancelTemplate}
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
            label="数字资产编号"
            name="digitalAssetsCollectionNo"
            rules={[{ required: true, message: '请输入数字资产编号!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="tokenId"
            name="tokenId"
            rules={[{ required: true, message: '请输入tokenId!' }]}
          >
            <InputNumber min={1} />
          </Form.Item>
          <Form.Item
            label="amount"
            name="amount"
            rules={[{ required: true, message: '请输入amount!' }]}
          >
            <InputNumber min={1} />
          </Form.Item>
          //TODO: 支持search
          <Form.Item
            label="接受客户号"
            name="toCustomerNo"
            rules={[{ required: true, message: '请输入接收客户号!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="接收地址"
            name="toAddress"
            rules={[{ required: true, message: '请输入接收地址!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="是否将改地址加入白名单"
            name="addPrivilege"
            rules={[
              { required: true, message: '请选择是否将改地址加入白名单!' },
            ]}
          >
            <Radio.Group>
              <Radio value="0">是</Radio>
              <Radio value="1">否</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item
            label="描述"
            name="description"
            rules={[{ required: true, message: '请输入铸造描述!' }]}
          >
            <TextArea />
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看详情模态框 */}
      <Modal
        title="查看转移详情"
        width={800}
        centered
        visible={isViewTemplateModal}
        onOk={() => setIsViewTemplateModal(false)}
        onCancel={() => setIsViewTemplateModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="铸造记录">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="转账流水号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="交易Hash">
            {isViewRecord?.txHash}
          </Descriptions.Item>
          <Descriptions.Item label="tokenId">
            {isViewRecord?.tokenId}
          </Descriptions.Item>
          <Descriptions.Item label="交易数量">
            {isViewRecord?.amount}
          </Descriptions.Item>
          <Descriptions.Item label="metadataImage">
            {isViewRecord?.metadataImage}
          </Descriptions.Item>
          <Descriptions.Item label="metadataUrl">
            {isViewRecord?.metadataUrl}
          </Descriptions.Item>
          <Descriptions.Item label="接收人手机号">
            {isViewRecord?.toPhone}
          </Descriptions.Item>
          <Descriptions.Item label="接收人姓名">
            {isViewRecord?.toMinterName}
          </Descriptions.Item>
          <Descriptions.Item label="发送地址">
            {isViewRecord?.fromAddress}
          </Descriptions.Item>
          <Descriptions.Item label="接收地址">
            {isViewRecord?.toAddress}
          </Descriptions.Item>
          <Descriptions.Item label="发送客户号">
            {isViewRecord?.fromCustomerNo}
          </Descriptions.Item>
          <Descriptions.Item label="接收客户号">
            {isViewRecord?.toCustomerNo}
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
