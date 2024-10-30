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
  Tag,
  Divider
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getTransactionPageList,
  addTransaction,
  deleteTransaction,
  getTransactionDetail,
  getProductList,
} from './service';
import TableTitle from '../../components/TableTitle';
import { hex_md5 } from '../../utils/md5';

export default () => {

  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isTemplateModal, setIsTemplateModal] = useState(false);
  // 查看模态框
  const [isViewTemplateModal, setIsViewTemplateModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  const [productList, setProductList] = useState([]);
  // 获取表单
  const [FormRef] = Form.useForm();

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <div key={record.serialNo}>
      <a onClick={() => {
            toViewContractTemplate(record);
          }}>查看</a>
      <Divider type="vertical" />
      <Popconfirm
        title="是否删除此条数据？"
        onConfirm={() => {
          toDelContractTemplate(record);
        }}
        onCancel={() => {
          message.warning(`取消删除！`);
        }}
        okText="是"
        cancelText="否"
      >
        <a>删除</a>
      </Popconfirm>
    </div>
  );

  // 交易类型自定义渲染
  const transactionTypeRender = (text: any, record: any, index: number) => {
    let tag = <Tag color="purple">资金归集</Tag>;
    
    if(record.transactionTypeRender === 0){
      tag = <Tag color="blue">转入</Tag>;
    }else if(record.transactionTypeRender === 1){
      tag = <Tag color="cyan">转出</Tag>;
    }
  
    return <div key={record.userId}>{tag}</div>;
  };

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
    item.dataIndex === 'transactionType' ? (item.render = transactionTypeRender) : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  // 新增模板
  const increaseTemplate = () => {
    setIsTemplateModal(true);
    getProductList({}).then((res) => {
      setProductList(res?.data);
    });
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
        let reqParam = {
          ...response,
          password: hex_md5(response.password),
        };
        addTransaction(reqParam).then((res) => {
          console.log('add', res);
          if (res.code === '000000' || res.code === 0) {
            message.success('添加成功');
            // 重置输入的表单
            FormRef.resetFields();
            setIsTemplateModal(false);
            actionRef.current?.reload();
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
    let { customerNo } = record;
    let delRes = await deleteTransaction({ customerNo });
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
    console.log(record);
    let { serialNo } = record;
    let viewRes = await getTransactionDetail({ serialNo });
    setIsViewTemplateModal(true);
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
        headerTitle={<TableTitle title="交易列表" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getTransactionPageList({
            ...params,
            // 租户客户类型
            type: '3',
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
            添加
          </Button>,
        ]}
      />
      {/* 新增合约模板模态框 */}
      <Modal
        title="添加节点"
        centered
        open={isTemplateModal}
        onOk={confirmTemplate}
        onCancel={onCancelTemplate}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ productCode: '0' }}
        >
          <Form.Item
            label="节点产品"
            name="productCode"
            rules={[{ required: true, message: '请选择节点产品!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="0">请选择节点产品</Option>
              {productList?.map((product) => {
                return (
                  <Option value={product?.productCode}>
                    {product?.productName}
                  </Option>
                );
              })}
            </Select>
          </Form.Item>
          <Form.Item
            label="节点名称"
            name="tenantName"
            rules={[{ required: true, message: '请输入节点名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="节点编号"
            name="tenantCode"
            rules={[{ required: true, message: '请输入节点编号!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="登录密码"
            name="password"
            rules={[{ required: true, message: '请输入登录密码!' }]}
          >
            <Input.Password placeholder="请输入登录密码" />
          </Form.Item>
          <Form.Item
            label="节点描述"
            name="description"
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
        <Descriptions title="节点信息">
          <Descriptions.Item label="节点号">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="节点名称">
            {isViewRecord?.tenantName}
          </Descriptions.Item>
          <Descriptions.Item label="节点类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="节点描述">
            {isViewRecord?.description}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
