import React, { useState, useEffect } from 'react';
import {
  Card,
  Button,
  Space,
  Select,
  Tag,
  Form,
  Modal,
  Input,
  Descriptions,
  message,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import TableTitle from '../../components/TableTitle';
import {
  getGradeList,
  getGradePageList,
  addGrade,
  editGrade,
  deleteGrade,
  getGradeDetail,
} from './service';
import columnsData, { columnsDataType } from './gradeData';

export default ({ setCurrentContent, configGrade }) => {

  const { Option } = Select;
  const { TextArea } = Input;

  // 获取表单
  const [FormRef] = Form.useForm();

  interface DataType {
    key: string;
    name: string;
    age: number;
    address: string;
    tags: string[];
  }

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <div key={record.dictType}>
      <Space size="middle">
        <a
          onClick={() => {
            toViewMemberGrade(record);
          }}
        >
          查看
        </a>
        <a
          onClick={() => {
            toEditMemberGrade(record);
          }}
        >
          编辑
        </a>
        <a
          onClick={() => {
            toConfigMemberGradeCondition(record);
          }}
        >
          条件配置
        </a>
        <a
          onClick={() => {
            toConfigMemberGradeEquity(record);
          }}
        >
          权益配置
        </a>
      </Space>
    </div>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  const [memberGradeList, setGradeList] = useState<DataType[]>([]);

  const [memberGradeModal, setGradeModal] = useState(false);

  // 查看模态框
  const [isViewMemberGradeModal, setIsViewMemberGradeModal] = useState(false);

  // 编辑模态框
  const [isEditMemberGradeModal, setIsEditMemberGradeModal] = useState(false);

  // 配置条件模态框
  const [
    isConfigConditionMemberGradeModal,
    setIsConfigConditionMemberGradeModal,
  ] = useState(false);

  // 配置权益模态框
  const [
    isConfigEquityMemberGradeModal,
    setIsConfigEquityMemberGradeModal,
  ] = useState(false);

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});

  useEffect(() => {
    let param = {};
    getGradeList(param).then((res) => {
      setGradeList(res?.data);
    });
  }, []);

  const addGradeModal = () => {
    setGradeModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmAdd = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        addGrade(response).then((res) => {
          console.log('add', res);
          if (res.code === 0) {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            // actionRef.current?.reload();
            setGradeModal(false);
          } else {
            message.error(`失败： ${res?.message}`);
          }
        });
      })
      .catch(() => { });
  };

  /**
   * 确认编辑模板
   */
  const confirmEdit = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        editGrade(response).then((res) => {
          console.log('add', res);
          if (res.code === 0) {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            setIsEditMemberGradeModal(false);
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
  const onCancelAdd = () => {
    // 重置输入的表单
    setGradeModal(false);
  };

  /**
   * 取消编辑模板
   */
  const onCancelEdit = () => {
    // 重置输入的表单
    setGradeModal(false);
  };

  /**
   * 查看详情
   */
  const toViewMemberGrade = async (record) => {
    let { serialNo } = record;
    let viewRes = await getGradeDetail({ serialNo });
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
    setIsViewMemberGradeModal(true);
  };

  /**
   * 编辑等级
   */
  const toEditMemberGrade = async (record) => {
    let { serialNo } = record;
    let viewRes = await getGradeDetail({ serialNo });
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
    setIsEditMemberGradeModal(true);
  };

  /**
   * 配置条件
   */
  const toConfigMemberGradeCondition = async (record) => {
    // 条件分类：1、会员等级 2 数字资产 3 任务 4 活动
    record.category = '1';
    configGrade(record, 'configCondition');
  };

  /**
   * 配置权益
   */
  const toConfigMemberGradeEquity = async (record) => {
    // 条件分类：1、会员等级 2 数字资产 3 任务 4 活动
    record.category = '1';
    configGrade(record, 'configEquity');
  };

  const items = [
    { label: '菜单项一', key: 'item-1' }, // 菜单项务必填写 key
    { label: '菜单项二', key: 'item-2' },
  ];

  return (
    <div>
      <Card>
        <Space direction="vertical" size="middle" style={{ display: 'flex' }}>
          <ProTable<columnsDataType>
            headerTitle={<TableTitle title="等级信息" />}
            scroll={{ x: 900 }}
            bordered
            // 表头
            columns={columns}
            actionRef={actionRef}
            // 请求获取的数据
            request={async (params) => {
              // console.log(params);
              if (params.gradeNum == null && params.name == null) {

              }
              let res = await getGradePageList({
                ...params,
              });
              console.log('😒', res);
              const result = {
                data: res.data,
                total: res.pagination.totalSize,
              };
              console.log(result);
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
                  addGradeModal();
                }}
                key="button"
                icon={<PlusOutlined />}
                type="primary"
              >
                新增
              </Button>,
            ]}
          />

        </Space>
      </Card>
      {/* 新增等级模板模态框 */}
      <Modal
        title="添加等级"
        centered
        open={memberGradeModal}
        onOk={confirmAdd}
        onCancel={onCancelAdd}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ bizRoleType: '0' }}
        >
          <Form.Item
            label="等级名称"
            name="name"
            rules={[{ required: true, message: '请输入等级名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="等级业务角色"
            name="bizRoleType"
            rules={[{ required: true, message: '请选择等级业务角色!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="0">请选择等级业务角色</Option>
              <Option value="1">平台租户</Option>
              <Option value="2">商户</Option>
              <Option value="3">代理商</Option>
              <Option value="4">客户</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="等级级别"
            name="gradeNum"
            rules={[{ required: true, message: '请输入等级级别!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="等级图标"
            name="gradeImage"
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="等级描述"
            name="description"
          >
            <TextArea />
          </Form.Item>
        </Form>
      </Modal>

      {/* 查看详情模态框 */}
      <Modal
        title="查看会员等级"
        width={800}
        centered
        open={isViewMemberGradeModal}
        onOk={() => setIsViewMemberGradeModal(false)}
        onCancel={() => setIsViewMemberGradeModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="会员等级详情">
          <Descriptions.Item label="等级ID">
            {isViewRecord?.grade?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="等级名称">
            {isViewRecord?.grade?.name}
          </Descriptions.Item>
          <Descriptions.Item label="等级">
            {isViewRecord?.grade?.gradeNum}
          </Descriptions.Item>
          <Descriptions.Item label="等级图标">
            {isViewRecord?.grade?.gradeImage}
          </Descriptions.Item>
          <Descriptions.Item label="等级描述">
            {isViewRecord?.grade?.description}
          </Descriptions.Item>
          <Descriptions.Item label="租户ID">
            {isViewRecord?.grade?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="商户Id">
            {isViewRecord?.grade?.merchantNo}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewRecord?.grade?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="更新时间">
            {isViewRecord?.grade?.updateTime}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.grade?.createTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>

      {/* 编辑等级模态框 */}
      <Modal
        title="编辑等级"
        centered
        open={isEditMemberGradeModal}
        onOk={confirmEdit}
        onCancel={onCancelEdit}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          // 表单读取数据库记录值
          initialValues={{
            serialNo: isViewRecord?.grade?.serialNo,
            name: isViewRecord?.grade?.name,
            gradeNum: isViewRecord?.grade?.gradeNum,
            gradeImage: isViewRecord?.grade?.gradeImage,
            description: isViewRecord?.grade?.description,
          }}
        >
          <Form.Item
            label="等级编号"
            name="serialNo"
            rules={[{ required: true, message: '请输入等级编号!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="等级名称"
            name="name"
            rules={[{ required: true, message: '请输入等级名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="等级级别"
            name="gradeNum"
            rules={[{ required: true, message: '请输入等级级别!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="等级图标"
            name="gradeImage"
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="等级描述"
            name="description"
          >
            <TextArea />
          </Form.Item>
        </Form>
      </Modal>

      {/* isEditMemberGradeModal */}
    </div>
  );
};
