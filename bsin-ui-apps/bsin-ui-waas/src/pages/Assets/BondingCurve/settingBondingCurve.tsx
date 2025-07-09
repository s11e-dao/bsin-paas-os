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
  Collapse,
} from 'antd';
import React, { useState, useEffect } from 'react';
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

interface SettingBondingCurveProps {
  refreshTrigger?: number;
}

export default ({ refreshTrigger }: SettingBondingCurveProps) => {
  const { TextArea } = Input;
  const { Option } = Select;

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  const getCurve = async () => {
    const reqParams = {
      merchantNo: '',
      pageSize: '100',
      current: '1',
    };
    const res = await getCurvePageList(reqParams);
    console.log(res);
  };

  // 监听refreshTrigger变化，触发表格刷新
  React.useEffect(() => {
    if (refreshTrigger && refreshTrigger > 0) {
      actionRef.current?.reload();
    }
  }, [refreshTrigger]);

  // 控制新增模态框
  const [isCurveModal, setIsCurveModal] = useState(false);

  // 编辑曲线模态框
  const [isEditCurveModal, setIsEditCurveModal] = useState(false);

  // 查看模态框
  const [isViewCurveModal, setIsViewCurveModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState<any>({});
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
        {/* <Popconfirm
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
        </Popconfirm> */}
        <a
          onClick={() => {
            // 调用方法
            toEditCurve(record);
          }}
        >
          编辑
        </a>
        <em className="ant-list-item-action-split"></em>
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
          if (res.code === 0) {
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
      .catch(() => { });
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
      .catch(() => { });
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
  const toViewCurve = async (record: any) => {
    let { serialNo } = record;
    let viewRes = await getCurveDetail({ serialNo });
    setIsViewCurveModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 编辑
   */
  const toEditCurve = async (record: any) => {
    let { serialNo } = record;
    let editRes = await getCurveDetail({ serialNo });
    console.log('editRes', editRes);
    setIsViewRecord(editRes.data);
    setIsEditCurveModal(true);
  };

  /**
   * 删除
   */
  const toDeleteCurve = async (record: any) => {
    let { serialNo } = record;
    let res = await deleteCurve({ serialNo });
    console.log('res', res);
    if (res.code === 0) {
      message.success('删除成功');
    } else {
      message.error('删除失败');
    }
    actionRef.current?.reload();
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
        width={800}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          initialValues={{
            type: '1',
            flexible: 6,
            initialPrice: '0.01',
            finalPrice: '10',
            decimals: 18,
            totalTargetToken: '21000000',
            estimatedLaborValue: '105000000',
            decayFactor: '0.9975',
            levelWidth: '2100000',
            totalLevels: 50,
            firstLevelReward: '5250000',
            releaseThreshold: '1000000',
          }}
        >
          <Collapse defaultActiveKey={['sigmoid', 'seg']}>
            <Collapse.Panel header="Sigmoid曲线参数" key="sigmoid">
              <Form.Item label="曲线名称" name="name" rules={[{ required: true, message: '请输入曲线名称!' }]}><Input /></Form.Item>
              <Form.Item label="曲线符号" name="symbol" rules={[{ required: true, message: '请输入曲线符号!' }]}><Input /></Form.Item>
              <Form.Item label="曲线小数点" name="decimals" rules={[{ required: true, message: '请输入曲线小数点!' }]}><InputNumber /></Form.Item>
              <Form.Item label="初始定价" name="initialPrice" rules={[{ required: true, message: '请输入初始定价!' }]}><Input min={0} /></Form.Item>
              <Form.Item label="最终定价" name="finalPrice" rules={[{ required: true, message: '请输入最终定价!' }]}><Input min={0} max={1000} /></Form.Item>
              <Form.Item label="供应上限" name="cap" rules={[{ required: true, message: '请输入供应上限!' }]}><Input min={0} /></Form.Item>
              <Form.Item label="拉伸变换值" name="flexible" rules={[{ required: true, message: '请输入曲线的拉伸变换值(4-8)!' }]}><InputNumber min={2} max={8} /></Form.Item>
              <Form.Item label="曲线类型" name="type" rules={[{ required: true, message: '请选择曲线类型!' }]}> <Select style={{ width: '100%' }}><Option value="0">bancor curve</Option><Option value="1">sigmoid curve</Option><Option value="2">cny</Option></Select> </Form.Item>
              <Form.Item label="曲线描述" name="description" rules={[{ required: true, message: '请输入曲线描述!' }]}><TextArea /></Form.Item>
            </Collapse.Panel>
            <Collapse.Panel header="分段衰减释放参数" key="seg">
              <Form.Item label="总积分目标" name="totalTargetToken" rules={[{ required: true, message: '请输入总积分目标!' }]}><InputNumber style={{ width: '100%' }} min={1} /></Form.Item>
              <Form.Item label="预估劳动价值" name="estimatedLaborValue" rules={[{ required: true, message: '请输入预估劳动价值!' }]}><InputNumber style={{ width: '100%' }} min={1} /></Form.Item>
              <Form.Item label="衰减系数" name="decayFactor" rules={[{ required: true, message: '请输入衰减系数!' },{ type: 'number', min: 0, max: 1, message: '衰减系数应在0-1之间' }]}><InputNumber style={{ width: '100%' }} min={0} max={1} step={0.0001} precision={4} /></Form.Item>
              <Form.Item label="档位宽度" name="levelWidth" rules={[{ required: true, message: '请输入档位宽度!' }]}><InputNumber style={{ width: '100%' }} min={1} /></Form.Item>
              <Form.Item label="档位总数" name="totalLevels" rules={[{ required: true, message: '请输入档位总数!' }]}><InputNumber style={{ width: '100%' }} min={1} /></Form.Item>
              <Form.Item label="首档奖励" name="firstLevelReward" rules={[{ required: true, message: '请输入首档奖励!' }]}><InputNumber style={{ width: '100%' }} min={1} /></Form.Item>
              <Form.Item label="释放阈值" name="releaseThreshold" rules={[{ required: true, message: '请输入释放阈值!' }]}><InputNumber style={{ width: '100%' }} min={0} /></Form.Item>
            </Collapse.Panel>
          </Collapse>
        </Form>
      </Modal>

      {/* 编辑曲线  */}
      <Modal
        title="编辑曲线"
        centered
        open={isEditCurveModal}
        onOk={EditCurve}
        onCancel={onCancelCurve}
        width={800}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          initialValues={{
            serialNo: isViewRecord?.serialNo,
            name: isViewRecord?.name,
            symbol: isViewRecord?.symbol,
            decimals: isViewRecord?.decimals,
            initialPrice: isViewRecord?.initialPrice,
            finalPrice: isViewRecord?.finalPrice,
            flexible: isViewRecord?.flexible,
            cap: isViewRecord?.cap,
            type: isViewRecord?.type,
            description: isViewRecord?.description,
            totalTargetToken: isViewRecord?.totalTargetToken,
            estimatedLaborValue: isViewRecord?.estimatedLaborValue,
            decayFactor: isViewRecord?.decayFactor,
            levelWidth: isViewRecord?.levelWidth,
            totalLevels: isViewRecord?.totalLevels,
            firstLevelReward: isViewRecord?.firstLevelReward,
            releaseThreshold: isViewRecord?.releaseThreshold,
          }}
        >
          <Collapse defaultActiveKey={['sigmoid', 'seg']}>
            <Collapse.Panel header="Sigmoid曲线参数" key="sigmoid">
              <Form.Item label="曲线编号" name="serialNo" rules={[{ required: true, message: '请输入曲线编号!' }]}><Input disabled /></Form.Item>
              <Form.Item label="曲线名称" name="name" rules={[{ required: true, message: '请输入曲线名称!' }]}><Input disabled /></Form.Item>
              <Form.Item label="曲线符号" name="symbol" rules={[{ required: true, message: '请输入曲线符号!' }]}><Input disabled /></Form.Item>
              <Form.Item label="曲线小数点" name="decimals" rules={[{ required: true, message: '请输入曲线小数点!' }]}><InputNumber disabled /></Form.Item>
              <Form.Item label="初始定价" name="initialPrice" rules={[{ required: true, message: '请输入初始定价!' }]}><Input min={0} disabled /></Form.Item>
              <Form.Item label="最终定价" name="finalPrice" rules={[{ required: true, message: '请输入最终定价!' }]}><Input min={0} max={1000} disabled /></Form.Item>
              <Form.Item label="供应上限" name="cap" rules={[{ required: true, message: '请输入供应上限!' }]}><Input min={0} disabled /></Form.Item>
              <Form.Item label="拉伸变换值" name="flexible" rules={[{ required: true, message: '请输入曲线的拉伸变换值(4-8)!' }]}><InputNumber min={2} max={8} /></Form.Item>
              <Form.Item label="曲线类型" name="type" rules={[{ required: true, message: '请选择曲线类型!' }]}> <Select style={{ width: '100%' }} disabled><Option value="0">bancor curve</Option><Option value="1">sigmoid curve</Option><Option value="2">cny</Option></Select> </Form.Item>
              <Form.Item label="曲线描述" name="description" rules={[{ required: true, message: '请输入曲线描述!' }]}><TextArea /></Form.Item>
            </Collapse.Panel>
            <Collapse.Panel header="分段衰减释放参数" key="seg">
              <Form.Item label="总积分目标" name="totalTargetToken" rules={[{ required: true, message: '请输入总积分目标!' }]}><InputNumber style={{ width: '100%' }} min={1} /></Form.Item>
              <Form.Item label="预估劳动价值" name="estimatedLaborValue" rules={[{ required: true, message: '请输入预估劳动价值!' }]}><InputNumber style={{ width: '100%' }} min={1} /></Form.Item>
              <Form.Item label="衰减系数" name="decayFactor" rules={[{ required: true, message: '请输入衰减系数!' },{ type: 'number', min: 0, max: 1, message: '衰减系数应在0-1之间' }]}><InputNumber style={{ width: '100%' }} min={0} max={1} step={0.0001} precision={4} /></Form.Item>
              <Form.Item label="档位宽度" name="levelWidth" rules={[{ required: true, message: '请输入档位宽度!' }]}><InputNumber style={{ width: '100%' }} min={1} /></Form.Item>
              <Form.Item label="档位总数" name="totalLevels" rules={[{ required: true, message: '请输入档位总数!' }]}><InputNumber style={{ width: '100%' }} min={1} /></Form.Item>
              <Form.Item label="首档奖励" name="firstLevelReward" rules={[{ required: true, message: '请输入首档奖励!' }]}><InputNumber style={{ width: '100%' }} min={1} /></Form.Item>
              <Form.Item label="释放阈值" name="releaseThreshold" rules={[{ required: true, message: '请输入释放阈值!' }]}><InputNumber style={{ width: '100%' }} min={0} /></Form.Item>
            </Collapse.Panel>
          </Collapse>
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
        <Descriptions title="曲线详情" bordered>
          <Descriptions.Item label="曲线编号">{isViewRecord?.serialNo}</Descriptions.Item>
          <Descriptions.Item label="租户ID">{isViewRecord?.tenantId}</Descriptions.Item>
          <Descriptions.Item label="商户ID">{isViewRecord?.merchantNo}</Descriptions.Item>
          <Descriptions.Item label="曲线积分名称">{isViewRecord?.name}</Descriptions.Item>
          <Descriptions.Item label="曲线积分符号">{isViewRecord?.symbol}</Descriptions.Item>
          <Descriptions.Item label="曲线积分小数点">{isViewRecord?.decimals}</Descriptions.Item>
          <Descriptions.Item label="曲线版本号">{isViewRecord?.version}</Descriptions.Item>
          <Descriptions.Item label="积分供应上限">{isViewRecord?.cap}</Descriptions.Item>
          <Descriptions.Item label="初始定价">{isViewRecord?.initialPrice}</Descriptions.Item>
          <Descriptions.Item label="最终定价">{isViewRecord?.finalPrice}</Descriptions.Item>
          <Descriptions.Item label="曲线的拉伸变换">{isViewRecord?.flexible}</Descriptions.Item>
          <Descriptions.Item label="曲线类型">{handleViewRecordOfType()}</Descriptions.Item>
          <Descriptions.Item label="账户编号">{isViewRecord?.accountNo}</Descriptions.Item>
          <Descriptions.Item label="积分曲线状态">{isViewRecord?.status}</Descriptions.Item>
          <Descriptions.Item label="创建时间">{isViewRecord?.createTime}</Descriptions.Item>
          <Descriptions.Item label="曲线描述">{isViewRecord?.description}</Descriptions.Item>
          
          {/* 新增：分段衰减释放参数详情 */}
          <Descriptions.Item label="总积分目标">{isViewRecord?.totalTargetToken}</Descriptions.Item>
          <Descriptions.Item label="预估劳动价值">{isViewRecord?.estimatedLaborValue}</Descriptions.Item>
          <Descriptions.Item label="衰减系数">{isViewRecord?.decayFactor}</Descriptions.Item>
          <Descriptions.Item label="档位宽度">{isViewRecord?.levelWidth}</Descriptions.Item>
          <Descriptions.Item label="档位总数">{isViewRecord?.totalLevels}</Descriptions.Item>
          <Descriptions.Item label="首档奖励">{isViewRecord?.firstLevelReward}</Descriptions.Item>
          <Descriptions.Item label="释放阈值">{isViewRecord?.releaseThreshold}</Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
