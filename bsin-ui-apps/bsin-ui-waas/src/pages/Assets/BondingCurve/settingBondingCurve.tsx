import {
  Form,
  Tabs,
  Card,
  Button,
  Modal,
  message,
  Popconfirm,
  Descriptions,
  Input,
  Select,
  InputNumber,
} from 'antd';
import React, { useState } from 'react';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';

import TableTitle from '../../../components/TableTitle';
import columnsCurveData, { columnsDataType } from './curveData';

import {
  getCurveList,
  getCurvePageList,
  getCurveDetail,
  addCurve,
  editCurve,
  deleteCurve,
} from './service';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;

  const getCurve = async () => {
    const reqParams = {
      merchantNo: '',
      pageSize: '100',
      current: '1',
    };
    const res = await getCurvePageList(reqParams);
    console.log(res);
  };

  // 控制新增模态框
  const [isCurveModal, setIsCurveModal] = useState(false);

  // 编辑曲线模态框
  const [isEditCurveModal, setIsEditCurveModal] = useState(false);

  // 查看模态框
  const [isViewCurveModal, setIsViewCurveModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 获取表单
  const [FormRef] = Form.useForm();
  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsCurveData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            // 调用方法
            toViewCurve(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <Popconfirm
          title="确定编辑此条记录？"
          okText="是"
          cancelText="否"
          onConfirm={() => {
            // 调用方法
            toEditCurve(record);
          }}
          // onCancel={cancel}
        >
          <a>编辑</a>
        </Popconfirm>
      </li>
      <li>
        <Popconfirm
          title="确定删除此条记录？"
          okText="是"
          cancelText="否"
          onConfirm={() => {
            // 调用方法
            toDeleteCurve(record);
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

  // 新增曲线
  const increaseCurve = () => {
    setIsCurveModal(true);
  };

  /**
   * 新增曲线
   */
  const confirmCurve = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        addCurve(response).then((res) => {
          console.log('add', res);
          if (res.code === '000000') {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsCurveModal(false);
          } else {
            message.error(`失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  /**
   * 编辑曲线
   */
  const EditCurve = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);

        editCurve(response).then((res) => {
          console.log('edit', res);
        });
        // 重置输入的表单
        FormRef.resetFields();
        // 刷新proTable
        actionRef.current?.reload();
        setIsEditCurveModal(false);
      })
      .catch(() => {});
  };

  /**
   * 取消添加模板
   */
  const onCancelCurve = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsCurveModal(false);
    setIsEditCurveModal(false);
  };

  /**
   * 查看详情
   */
  const toViewCurve = async (record) => {
    let { serialNo } = record;
    let viewRes = await getCurveDetail({ serialNo });
    setIsViewCurveModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 编辑
   */
  const toEditCurve = async (record) => {
    let { serialNo } = record;
    let viewRes = await getCurveDetail({ serialNo });
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
    setIsEditCurveModal(true);
  };

  /**
   * 删除
   */
  const toDeleteCurve = async (record) => {
    let { serialNo } = record;
    let res = await deleteCurve({ serialNo });
    console.log('res', res);
    if (res.code === '000000') {
      message.success('删除成功');
    } else {
      message.error('删除成功');
    }
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
      <div style={{ marginTop: '20px' }}>
        <ProTable<columnsDataType>
          headerTitle={<TableTitle title="曲线列表" />}
          scroll={{ x: 900 }}
          bordered
          // 表头
          columns={columns}
          actionRef={actionRef}
          // 请求获取的数据
          request={async (params) => {
            // console.log(params);
            let res = await getCurvePageList({
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
                increaseCurve();
              }}
              key="button"
              icon={<PlusOutlined />}
              type="primary"
            >
              新增联合曲线积分
            </Button>,
          ]}
        />
      </div>
      {/* 新增曲线  */}
      <Modal
        title="新增曲线"
        centered
        open={isCurveModal}
        onOk={confirmCurve}
        onCancel={onCancelCurve}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{
            type: '1',
            flexible: 6,
            initialPrice: '0.01',
            finalPrice: '10',
            decimals: 18,
          }}
        >
          <Form.Item
            label="曲线名称"
            name="name"
            rules={[{ required: true, message: '请输入曲线名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="曲线符号"
            name="symbol"
            rules={[{ required: true, message: '请输入曲线符号!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="曲线小数点"
            name="decimals"
            rules={[{ required: true, message: '请输入曲线小数点!' }]}
          >
            <InputNumber />
          </Form.Item>

          <Form.Item
            label="初始定价"
            name="initialPrice"
            rules={[{ required: true, message: '请输入初始定价!' }]}
          >
            <Input min={0} />
          </Form.Item>

          <Form.Item
            label="最终定价"
            name="finalPrice"
            rules={[{ required: true, message: '请输入最终定价!' }]}
          >
            <Input min={0} max={1000} />
          </Form.Item>

          <Form.Item
            label="供应上限"
            name="cap"
            rules={[{ required: true, message: '请输入供应上限!' }]}
          >
            <Input min={0} />
          </Form.Item>
          {/* ，越大代表压缩的最厉害，中间（x坐标cap/2点周围）加速度越大；越小越接近匀加速。理想的S曲线 flexible的取值为4-6。 */}
          <Form.Item
            label="拉伸变换值"
            name="flexible"
            rules={[
              { required: true, message: '请输入曲线的拉伸变换值(4-8)!' },
            ]}
          >
            <InputNumber min={2} max={8} />
          </Form.Item>
          <Form.Item
            label="曲线类型"
            name="type"
            rules={[{ required: true, message: '请选择曲线类型!' }]}
          >
            {/* 0、bancor curve  1、sigmoid curve  */}
            <Select style={{ width: '100%' }}>
              <Option value="0">bancor curve</Option>
              <Option value="1">sigmoid curve</Option>
              <Option value="2">cny</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="曲线描述"
            name="description"
            rules={[{ required: true, message: '请输入曲线描述!' }]}
          >
            <TextArea />
          </Form.Item>
        </Form>
      </Modal>

      {/* 编辑曲线  */}
      <Modal
        title="编辑曲线"
        centered
        open={isEditCurveModal}
        onOk={EditCurve}
        onCancel={onCancelCurve}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单读取数据库记录值
          initialValues={{
            serialNo: isViewRecord?.serialNo,
            name: isViewRecord?.name,
            symbol: isViewRecord?.symbol,
            decimals: isViewRecord?.decimals,
            initialPrice: isViewRecord?.initialPrice,
            finalPrice: isViewRecord?.finalPrice,
            cap: isViewRecord?.cap,
            type: isViewRecord?.type,
            description: isViewRecord?.description,
          }}
        >
          <Form.Item
            label="曲线编号"
            name="serialNo"
            rules={[{ required: true, message: '请输入曲线编号!' }]}
          >
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="曲线名称"
            name="name"
            rules={[{ required: true, message: '请输入曲线名称!' }]}
          >
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="曲线符号"
            name="symbol"
            rules={[{ required: true, message: '请输入曲线符号!' }]}
          >
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="曲线小数点"
            name="decimals"
            rules={[{ required: true, message: '请输入曲线小数点!' }]}
          >
            <InputNumber disabled />
          </Form.Item>

          <Form.Item
            label="初始定价"
            name="initialPrice"
            rules={[{ required: true, message: '请输入初始定价!' }]}
          >
            <Input min={0} disabled />
          </Form.Item>

          <Form.Item
            label="最终定价"
            name="finalPrice"
            rules={[{ required: true, message: '请输入最终定价!' }]}
          >
            <Input min={0} max={1000} disabled />
          </Form.Item>

          <Form.Item
            label="供应上限"
            name="cap"
            rules={[{ required: true, message: '请输入供应上限!' }]}
          >
            <Input min={0} disabled />
          </Form.Item>
          {/* ，越大代表压缩的最厉害，中间（x坐标cap/2点周围）加速度越大；越小越接近匀加速。理想的S曲线 flexible的取值为4-6。 */}
          <Form.Item
            label="拉伸变换值"
            name="flexible"
            rules={[
              { required: true, message: '请输入曲线的拉伸变换值(4-8)!' },
            ]}
          >
            <InputNumber min={2} max={8} />
          </Form.Item>
          <Form.Item
            label="曲线类型"
            name="type"
            rules={[{ required: true, message: '请选择曲线类型!' }]}
          >
            {/* 0、bancor curve  1、sigmoid curve  */}
            <Select style={{ width: '100%' }} disabled>
              <Option value="0">bancor curve</Option>
              <Option value="1">sigmoid curve</Option>
              <Option value="2">cny</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="曲线描述"
            name="description"
            rules={[{ required: true, message: '请输入曲线描述!' }]}
          >
            <TextArea />
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看详情模态框 */}
      <Modal
        title="查看曲线详情"
        width={800}
        centered
        visible={isViewCurveModal}
        onOk={() => setIsViewCurveModal(false)}
        onCancel={() => setIsViewCurveModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="曲线详情">
          <Descriptions.Item label="曲线编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="商户ID">
            {isViewRecord?.merchantNo}
          </Descriptions.Item>
          <Descriptions.Item label="曲线积分名称">
            {isViewRecord?.name}
          </Descriptions.Item>
          <Descriptions.Item label="曲线积分符号">
            {isViewRecord?.symbol}
          </Descriptions.Item>
          <Descriptions.Item label="曲线积分小数点">
            {isViewRecord?.decimals}
          </Descriptions.Item>
          <Descriptions.Item label="曲线版本号">
            {isViewRecord?.version}
          </Descriptions.Item>
          <Descriptions.Item label="积分供应上限">
            {isViewRecord?.cap}
          </Descriptions.Item>
          <Descriptions.Item label="初始定价">
            {isViewRecord?.initialPrice}
          </Descriptions.Item>
          <Descriptions.Item label="最终定价">
            {isViewRecord?.finalPrice}
          </Descriptions.Item>
          <Descriptions.Item label="曲线的拉伸变换">
            {isViewRecord?.flexible}
          </Descriptions.Item>
          <Descriptions.Item label="曲线类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="账户编号">
            {isViewRecord?.accountNo}
          </Descriptions.Item>
          <Descriptions.Item label="积分曲线状态">
            {isViewRecord?.status}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="曲线描述">
            {isViewRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
