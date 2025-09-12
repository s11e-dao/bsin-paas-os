import React, { useState, useEffect } from 'react';
import { useLocation } from '@umijs/max';
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Select,
  Popconfirm,
  Descriptions,
  Divider,
  QRCode,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import type { FormInstance } from 'antd';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import { getAdsPageList, addAds, deleteAds, getAdsDetail } from './service';
import TableTitle from '../../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  const location = useLocation();
  // 控制新增模态框
  const [isTemplateModal, setIsTemplateModal] = useState(false);
  // 查看模态框
  const [isViewTemplateModal, setIsViewTemplateModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 充值模态框
  const [isRechargeModal, setIsRechargeModal] = useState(false);
  // 充值信息
  const [rechargeInfo, setRechargeInfo] = useState({
    address: '',
    chainName: '',
    coin: ''
  });
  // 获取表单
  const [FormRef] = Form.useForm();
  // 搜索表单
  const searchFormRef = React.useRef<FormInstance>();

  // 获取URL参数并填充钱包ID
  useEffect(() => {
    const urlParams = new URLSearchParams(location.search);
    const walletSerialNo = urlParams.get('walletSerialNo');
    if (walletSerialNo && searchFormRef.current) {
      searchFormRef.current.setFieldsValue({
        serialNo: walletSerialNo
      });
    }
  }, [location.search]);

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
      <a onClick={() => {
            toRecharge(record);
          }}>充值</a>
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
        addAds(response).then((res) => {
          console.log('add', res);
          if (res.code === 0 ) {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsTemplateModal(false);
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
    let { serialNo } = record;
    let delRes = await deleteAds({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === 0) {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewContractTemplate = async (record) => {
    let { serialNo } = record;
    let viewRes = await getAdsDetail({ serialNo });
    setIsViewTemplateModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 充值
   */
  const toRecharge = (record) => {
    const { address, chainName, coin } = record;
    setRechargeInfo({
      address,
      chainName,
      coin
    });
    setIsRechargeModal(true);
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
        headerTitle={<TableTitle title="链账户" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getAdsPageList({
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
        // 关联搜索表单
        formRef={searchFormRef}
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
            新增
          </Button>,
        ]}
      />
      {/* 新增合约模板模态框 */}
      <Modal
        title="新增"
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
          initialValues={{ type: '1' }}
        >
          <Form.Item
            label="类型"
            name="type"
            rules={[{ required: true, message: '请选择协议类型!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="1">首页</Option>
              <Option value="2">数字资产页</Option>
              <Option value="3">其他</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="名称"
            name="title"
            rules={[{ required: true, message: '请输入名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="链接"
            name="linkUrl"
            rules={[{ required: true, message: '请输入链接!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="图片"
            name="imageUrl"
            rules={[{ required: true, message: '请输入链接!' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看详情模态框 */}
      <Modal
        title="查看"
        width={800}
        centered
        open={isViewTemplateModal}
        onOk={() => setIsViewTemplateModal(false)}
        onCancel={() => setIsViewTemplateModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="合约协议信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="协议编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="协议名称">
            {isViewRecord?.templateName}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议bytecode">
            {isViewRecord?.templateBytecode}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议abi字符">
            {isViewRecord?.templateAbi}
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
      {/* 充值二维码模态框 */}
      <Modal
        title="充值"
        centered
        open={isRechargeModal}
        onOk={() => setIsRechargeModal(false)}
        onCancel={() => setIsRechargeModal(false)}
        footer={[
          <Button key="ok" type="primary" onClick={() => setIsRechargeModal(false)}>
            确定
          </Button>,
        ]}
      >
        <div style={{ textAlign: 'center', padding: '20px' }}>
          <div style={{ marginBottom: '16px', fontSize: '14px', fontWeight: 'bold' }}>
            充值地址
          </div>
          <div style={{ marginBottom: '20px', wordBreak: 'break-all', fontSize: '14px', color: '#666' }}>
            {rechargeInfo.address}
          </div>
          <QRCode 
            value={rechargeInfo.address} 
            size={200}
            style={{ margin: '0 auto 20px' }}
          />
          <div style={{ 
            display: 'flex', 
            justifyContent: 'center', 
            alignItems: 'center',
            gap: '20px',
            fontSize: '14px'
          }}>
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <span style={{ color: '#666', marginRight: '6px' }}>链名:</span>
              <span style={{ fontWeight: 'bold', color: '#1890ff' }}>{rechargeInfo.chainName}</span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <span style={{ color: '#666', marginRight: '6px' }}>币种:</span>
              <span style={{ fontWeight: 'bold', color: '#52c41a' }}>{rechargeInfo.coin}</span>
            </div>
          </div>
        </div>
      </Modal>
    </div>
  );
};
