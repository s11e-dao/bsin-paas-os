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
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getPayWayPageList,
  addPayWay,
  deletePayWay,
  getPayWayDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';
import { hex_md5 } from '../../../utils/md5';

export default () => {

  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isPayWayModal, setIsPayWayModal] = useState(false);
  // 查看模态框
  const [isViewPayWayModal, setIsViewPayWayModal] = useState(false);
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
    <div key={record.dictType}>
      <a onClick={() => toViewPayWay(record)}>查看</a>
      <Divider type="vertical" />
      <Popconfirm
        title="是否删除此条数据?"
        onConfirm={() => toDelPayWay(record)}
        onCancel={() => {
          message.warning(`取消删除`);
        }}
        okText="是"
        cancelText="否"
      >
        <a>删除</a>
      </Popconfirm>
    </div>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  // 新增模板
  const increasePayWay = () => {
    setIsPayWayModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmPayWay = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        let reqParam = {
          ...response
        };
        addPayWay(reqParam).then((res) => {
          console.log('add', res);
          if (res.code === 0 || res.code === "000000") {
            message.success('添加成功');
            // 重置输入的表单
            FormRef.resetFields();
            setIsPayWayModal(false);
            actionRef.current?.reload();
          } else {
            message.error(`失败： ${res?.message}`);
          }
        });
      })
      .catch(() => { });
  };

  /**
   * 取消添加模板
   */
  const onCancelPayWay = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsPayWayModal(false);
  };

  /**
   * 删除模板
   */
  const toDelPayWay = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deletePayWay({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === 0) {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewPayWay = async (record) => {
    console.log(record);
    let { serialNo } = record;
    let viewRes = await getPayWayDetail({ serialNo });
    setIsViewPayWayModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfType = () => {
    let { type } = isViewRecord;
    // 客户类型 0、个人客户 1、租户商家客户 2、租户(dao)客户 3、顶级平台商家客户
    let typeText = type;
    if (typeText == '4') {
      return '超级节点';
    } else if (typeText == '2') {
      return '普通节点';
    } else {
      return typeText;
    }
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="支付方式" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getPayWayPageList({
            ...params,
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
              increasePayWay();
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            添加
          </Button>,
        ]}
      />
      {/* 新增合约模板模态框 */}
      <Modal
        title="添加支付渠道"
        centered
        open={isPayWayModal}
        onOk={confirmPayWay}
        onCancel={onCancelPayWay}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{}}
        >
          <Form.Item
            label="支付方式名称"
            name="payWayName"
            rules={[{ required: true, message: '请输入支付方式名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="支付方式代码"
            name="payWayCode"
            rules={[{ required: true, message: '请输入支付方式编码!' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看详情模态框 */}
      <Modal
        title="详情"
        width={800}
        centered
        open={isViewPayWayModal}
        onOk={() => setIsViewPayWayModal(false)}
        onCancel={() => setIsViewPayWayModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="节点信息">
          <Descriptions.Item label="节点号">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="支付渠道名称">
            {isViewRecord?.payWayName}
          </Descriptions.Item>
          <Descriptions.Item label="支付方式代码">
            {/* 例如： WX_JSAPI", "WX_H5", "WX_APP", "ALI_BAR", "ALI_APP", "ALI_WAP“ */}
            {isViewRecord?.payWayCode}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="更新时间">
            {isViewRecord?.updateTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
