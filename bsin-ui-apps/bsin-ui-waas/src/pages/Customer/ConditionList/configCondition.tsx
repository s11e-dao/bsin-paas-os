import React, { useState } from 'react';
import {
  Form,
  Input,
  Modal,
  Radio,
  message,
  Button,
  Select,
  Card,
  Popconfirm,
  Descriptions,
  DatePicker,
  Divider,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './conditionData';

import {
  getConditionPageList,
  getConditionListByCategoryNo,
  getConditionDetail,
  deleteConditionConfig,
  configCondition,
} from './service';

import TableTitle from '../../../components/TableTitle';
import styles from './index.css';

const { RangePicker } = DatePicker;

export default ({ setCurrentContent, record }) => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isConditionModal, setIsConditionModal] = useState(false);
  // 查看模态框
  const [isViewConditionModal, setIsViewConditionModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 任务起止时间
  const [conditionList, setConditionList] = useState([]);
  const [basedOnModel, setbasedOnModel] = useState('0');

  const [conditionCategory, setConditionCategory] = useState('0');

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
      <a onClick={() => toViewCondition(record)}>查看</a>
      <Divider type="vertical" />
      <Popconfirm
        title="确定删除此条数据？?"
        onConfirm={() => toDelCondition(record.id)}
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

  /**
   * 以下内容为操作相关
   */

  const baseModelChange = (e: Event) => {
    console.log(e);
    // 根据点击选择展示不同的输入框
    setbasedOnModel(e.target.value);
  };

  // 新增模板
  const increaseCondition = () => {
    setIsConditionModal(true);
  };

  /**
   * 确认添加任务
   */
  const confirmCondition = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        response.categoryNo = record.serialNo;
        response.category = conditionCategory;
        configCondition(response).then((res) => {
          console.log('add', res);
          if (res.code === '000000' || res.code === 0) {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsConditionModal(false);
          } else {
            message.error(`失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  /**
   * 取消添加条件
   */
  const onCancelCondition = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsConditionModal(false);
  };

  /**
   * 删除模板
   */
  const toDelCondition = async (record) => {
    console.log('record', record);
    let { conditionRelationshipNo } = record;
    let delRes = await deleteConditionConfig({
      serialNo: conditionRelationshipNo,
    });
    console.log('delRes', delRes);
    if (delRes.code === '000000') {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewCondition = async (record) => {
    let { serialNo } = record;
    let viewRes = await getConditionDetail({ serialNo });
    setIsViewConditionModal(true);
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

  const typeOnChange = (value) => {
    console.log(value);
    // 币种
    let params = {
      pageSize: 99,
      current: 1,
      type: value,
    };
    // 请求后台获取条件
    getConditionPageList(params).then((res) => {
      console.log(res);
      let conditionListTemp = [];
      res?.data.map((item) => {
        console.log(item);
        let conditionJson = {
          serialNo: item.serialNo,
          name: item.name,
        };
        conditionListTemp.push(conditionJson);
      });
      setConditionList(conditionListTemp);
    });
  };

  return (
    <>
      <Card style={{ marginBottom: 16 }}>
        <Button
          type="primary"
          onClick={() => {
            setCurrentContent('taskList');
          }}
          className={styles.btn}
        >
          返回
        </Button>
        <Descriptions title="配置条件"></Descriptions>
        {/* Pro表格 */}

        <ProTable<columnsDataType>
          headerTitle={<TableTitle title="任务条件列表" />}
          scroll={{ x: 900 }}
          search={false}
          bordered
          // 表头
          columns={columns}
          actionRef={actionRef}
          // 请求获取的数据
          request={async (params) => {
            // console.log(params);
            params.categoryNo = record.serialNo;
            setConditionCategory(record.category);
            let res = await getConditionListByCategoryNo({
              ...params,
              // pageNum: params.current,
            });
            console.log('😒', res);
            const result = {
              data: res.data,
            };
            return result;
          }}
          rowKey="serialNo"
          // 搜索表单的配置
          form={{
            ignoreRules: false,
          }}
          pagination={false}
          toolBarRender={() => [
            <Button
              onClick={() => {
                increaseCondition();
              }}
              key="button"
              icon={<PlusOutlined />}
              type="primary"
            >
              添加条件
            </Button>,
          ]}
        />
        {/* 新增条件模板模态框 */}
        <Modal
          title="添加条件"
          centered
          open={isConditionModal}
          onOk={confirmCondition}
          onCancel={onCancelCondition}
        >
          <Form
            name="basic"
            form={FormRef}
            labelCol={{ span: 7 }}
            wrapperCol={{ span: 14 }}
            // 表单默认值
            initialValues={{ type: '0', typeNo: '0' }}
          >
            <Form.Item
              label="条件类型"
              name="type"
              rules={[{ required: true, message: '请选择条件类型!' }]}
            >
              <Select
                style={{ width: '100%' }}
                onChange={(value) => typeOnChange(value)}
              >
                <Option value="0">请选择条件类型</Option>
                <Option value="1">数字徽章</Option>
                <Option value="2">PFP</Option>
                <Option value="3">账户-DP</Option>
                <Option value="4">数字门票</Option>
                <Option value="5">Pass卡</Option>
                <Option value="6">账户-BC</Option>
                <Option value="7">满减</Option>
                <Option value="8">权限</Option>
                <Option value="9">会员等级</Option>
              </Select>
            </Form.Item>
            <Form.Item
              label="条件"
              name="typeNo"
              rules={[{ required: true, message: '请选择条件!' }]}
            >
              <Select style={{ width: '100%' }}>
                <Option value="0">请选择条件</Option>
                {conditionList.map((condition) => {
                  return (
                    <Option value={condition?.serialNo}>
                      {condition?.name}
                    </Option>
                  );
                })}
              </Select>
            </Form.Item>
          </Form>
        </Modal>

        {/* 查看详情模态框 */}
        <Modal
          title="查看条件详情"
          width={800}
          centered
          visible={isViewConditionModal}
          onOk={() => setIsViewConditionModal(false)}
          onCancel={() => setIsViewConditionModal(false)}
        >
          {/* 详情信息 */}
          <Descriptions title="任务详细信息">
            <Descriptions.Item label="租户ID">
              {isViewRecord?.tenantId}
            </Descriptions.Item>
            <Descriptions.Item label="商户ID">
              {isViewRecord?.merchantNo}
            </Descriptions.Item>
            <Descriptions.Item label="条件编号">
              {isViewRecord?.serialNo}
            </Descriptions.Item>
            <Descriptions.Item label="条件名称">
              {isViewRecord?.name}
            </Descriptions.Item>
            <Descriptions.Item label="条件备注">
              {isViewRecord?.remark}
            </Descriptions.Item>
            <Descriptions.Item label="类型类型">
              {handleViewRecordOfType()}
            </Descriptions.Item>
            <Descriptions.Item label="数量">
              {isViewRecord?.amount}
            </Descriptions.Item>
            <Descriptions.Item label="条件类型编号">
              {isViewRecord?.typeNo}
            </Descriptions.Item>
            <Descriptions.Item label="更新时间">
              {isViewRecord?.updateTime}
            </Descriptions.Item>
            <Descriptions.Item label="创建者">
              {isViewRecord?.createBy}
            </Descriptions.Item>
            <Descriptions.Item label="创建时间">
              {isViewRecord?.createTime}
            </Descriptions.Item>
            <Descriptions.Item label="条件描述">
              {isViewRecord?.description}
            </Descriptions.Item>
          </Descriptions>
        </Modal>
      </Card>
    </>
  );
};
