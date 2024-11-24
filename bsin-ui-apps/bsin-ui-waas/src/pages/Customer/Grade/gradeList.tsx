import React, { useState, useEffect } from 'react';
import {
  Card,
  Button,
  Space,
  Table,
  Tag,
  Form,
  Modal,
  Input,
  Descriptions,
  message,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';

import {
  getGradeList,
  addGrade,
  editGrade,
  deleteGrade,
  getGradeDetail,
} from './service';

export default ({ setCurrentContent, configGrade }) => {
  // 获取表单
  const [FormRef] = Form.useForm();

  interface DataType {
    key: string;
    name: string;
    age: number;
    address: string;
    tags: string[];
  }

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
          if (res.code === 0 ) {
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
          if (res.code === 0 ) {
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
    setIsEditMemberGradeModal(false);
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

  const columns: ColumnsType<DataType> = [
    {
      title: '等级ID',
      width: 190,
      dataIndex: 'serialNo',
      fixed: 'left',
    },
    {
      title: '等级名称',
      width: 160,
      dataIndex: 'name',
    },
    {
      title: '等级级数',
      width: 160,
      dataIndex: 'gradeNum',
    },
    {
      title: '等级编号',
      width: 160,
      dataIndex: 'gradeCode',
    },
    {
      title: '等级图标',
      width: 160,
      dataIndex: 'gradeImage',
    },
    {
      title: '等级描述',
      width: 160,
      dataIndex: 'description',
    },
    {
      title: 'Tags',
      key: 'tags',
      dataIndex: 'tags',
      // render: (_, { tags }) => (
      //   <>
      //     {tags.map((tag) => {
      //       let color = tag.length > 5 ? 'geekblue' : 'green';
      //       if (tag === 'loser') {
      //         color = 'volcano';
      //       }
      //       return (
      //         <Tag color={color} key={tag}>
      //           {tag.toUpperCase()}
      //         </Tag>
      //       );
      //     })}
      //   </>
      // ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
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
      ),
    },
  ];

  return (
    <div>
      <Card>
        <Space direction="vertical" size="middle" style={{ display: 'flex' }}>
          <Space size={[8, 16]} wrap>
            <Button
              type="primary"
              onClick={() => {
                addGradeModal();
              }}
            >
              添加等级
            </Button>
          </Space>
          <Table
            onRow={(record) => {
              return {
                onClick: (event) => {
                  console.log(event);
                }, // 点击行
              };
            }}
            columns={columns}
            dataSource={memberGradeList}
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
          initialValues={{ type: '0', typeNo: '0' }}
        >
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
            rules={[{ required: true, message: '请输入等级图标!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="等级描述"
            name="description"
            rules={[{ required: true, message: '请输入等级描述!' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>

      {/* 查看详情模态框 */}
      <Modal
        title="查看会员等级"
        width={800}
        centered
        visible={isViewMemberGradeModal}
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
            rules={[{ required: true, message: '请输入等级图标!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="等级描述"
            name="description"
            rules={[{ required: true, message: '请输入等级描述!' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>

      {/* isEditMemberGradeModal */}
    </div>
  );
};
