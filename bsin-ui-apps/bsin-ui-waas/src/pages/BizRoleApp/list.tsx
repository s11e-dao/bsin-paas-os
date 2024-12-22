import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  Modal,
  Divider,
  message,
  Button,
  Select,
  Popconfirm,
  Descriptions,
  Upload,
  Drawer,
  Card,
  Alert,
  Radio,
  Space
} from 'antd';
import type { UploadProps } from 'antd/es/upload/interface';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined, InboxOutlined } from '@ant-design/icons';
import { EditOutlined, EllipsisOutlined, SettingOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getBizRoleAppPageList,
  addBizRoleApp,
  editBizRoleApp,
  deleteBizRoleApp,
  getBizRoleAppDetail,
  getBizRoleAppPayChannelConfig,
} from './service';
import PayConfig from './payConfig';

import {
  getPayWayList,
} from '../pay/PayWay/service';

import {
  getPayInterfaceList,
} from '../pay/PayChannelInterface/service';

import TableTitle from '../../components/TableTitle';
import { hex_md5 } from '@/utils/md5';
import {
  getLocalStorageInfo,
  getSessionStorageInfo,
} from '@/utils/localStorageInfo';
import BizRoleApp from '.';

const { Meta } = Card;
const { TextArea } = Input;

export default ({ setCurrentContent }) => {

  let fileUrl = process.env.fileUrl;
  let bsinFileUploadUrl = process.env.bsinFileUploadUrl;

  let tenantAppType = process.env.tenantAppType;

  const [formRef] = Form.useForm();
  const [payChannelConfigFormRef] = Form.useForm();
  // 上传组件
  const { Dragger } = Upload;

  const { TextArea } = Input;
  const { Option } = Select;
  const [loading, setLoading] = useState(true)
  // 控制新增、编辑模态框title
  const [addModalTitle, setAddModalTitle] = React.useState('添加');
  // 控制新增模态框
  const [isAddBizRoleAppModal, setIsAddBizRoleAppModal] = useState(false);
  // 控制支付配置模态框
  const [payConfigModal, setPayConfigModal] = useState(false);
  // 查看模态框
  const [isViewBizRoleAppModal, setIsViewBizRoleAppModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 选择的数据
  const [checkItem, setCheckItem] = useState({});

  const [payChannelList, setPayChannelList] = useState([])

  const [currentPayChannel, setCurrentPayChannel] = useState({})
  const [currenBizRoleApp, setCurrenBizRoleApp] = useState({})

  const [logoUrl, setLogoUrl] = useState('');

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <div key={record.dictType}>
      <a
        onClick={() => {
          // handlePayConfigModel(record);
          showPayConfigDrawer(record)
        }}
      >
        支付配置
      </a>
      <Divider type="vertical" />
      <a
        onClick={async () => {
          console.log('res');
          setCurrentContent('appConfig');
        }}
      >
        渠道应用配置
      </a>
      <Divider type="vertical" />
      <a onClick={() => toViewBizRoleApp(record)}>查看</a>
      <Divider type="vertical" />
      <a
        onClick={() => {
          handleEditModel(record);
        }}
      >
        编辑
      </a>
      <Divider type="vertical" />
      <Popconfirm
        title="确定删除此条数据？?"
        onConfirm={() => toDelBizRoleApp(record.id)}
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


  useEffect(() => {

  }, [])

  /**
   * 以下内容为操作相关
   */

  // 新增模板
  const addBizRoleAppModal = () => {
    setIsAddBizRoleAppModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmAddBizRoleApp = () => {
    // 获取输入的表单值
    formRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = formRef.getFieldsValue();
        console.log(response);
        let reqParam = {
          ...response,
        };
        console.log(getLocalStorageInfo('userInfo'));
        console.log(reqParam);

        if (addModalTitle === '新增') {
          addBizRoleApp(reqParam).then((res) => {
            console.log('add', res);
            if (res.code === 0) {
              message.success('添加成功');
              // 重置输入的表单
              formRef.resetFields();
              // 刷新proTable
              actionRef.current?.reload();
              setIsAddBizRoleAppModal(false);
            } else {
              message.error(`失败： ${res?.message}`);
            }
          });
        } else {
          reqParam.serialNo = checkItem.serialNo;
          addBizRoleApp(reqParam).then((res) => {
            console.log('add', res);
            if (res.code === 0) {
              message.success('修改成功');
              // 重置输入的表单
              formRef.resetFields();
              // 刷新proTable
              actionRef.current?.reload();
              setIsAddBizRoleAppModal(false);
            } else {
              message.error(`失败： ${res?.message}`);
            }
          });
        }
      })
      .catch(() => { });
  };

  /**
   * 取消添加模板
   */
  const onCancelBizRoleApp = () => {
    // 重置输入的表单
    formRef.resetFields();
    setIsAddBizRoleAppModal(false);
  };

  // 点击编辑
  const handleEditModel = (record: DictColumnsItem) => {
    console.log('handleEditModel', record);
    formRef.setFieldsValue(record);
    setAddModalTitle('编辑');
    setCheckItem(record);
    setIsAddBizRoleAppModal(true);
  };

  // 点击支付配置
  const handlePayConfigModel = (record: DictColumnsItem) => {
    console.log('handlePayConfigModel', record);
    formRef.setFieldsValue(record);
    let reqParam = {
      bizRoleAppId: record.serialNo,
    };
    getBizRoleAppPayChannelConfig(reqParam).then((res) => {
      console.log('getBizRoleAppPayChannelConfig', res);
      if (res.code === 0) {
        setCheckItem(res.data);
        payChannelConfigFormRef.setFieldsValue(res.data);
        setPayConfigModal(true);
      } else {
        message.error(`失败： ${res?.message}`);
      }
    });

  };

  /**
   * 删除模板
   */
  const toDelBizRoleApp = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteBizRoleApp({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === 0 || delRes.code === 0) {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewBizRoleApp = async (record) => {
    let { serialNo } = record;
    let viewRes = await getBizRoleAppDetail({ serialNo });
    setIsViewBizRoleAppModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(record);
  };


  // 认证状态   1: 待认证  2：认证成功  3：认证失败
  const handleViewRecordOfSauthenticationStatus = () => {
    let { authenticationStatus } = isViewRecord;
    if (authenticationStatus == '1') {
      return '待认证';
    } else if (authenticationStatus == '2') {
      return '认证成功';
    } else if (authenticationStatus == '3') {
      return '认证失败';
    } else {
      return authenticationStatus;
    }
  };

  // 状态: 0-停用, 1-启用
  const handleViewRecordOfPayChannelConfigStatus = () => {
    let { status } = isViewRecord;
    if (status == '0') {
      return '停用';
    } else if (status == '1') {
      return '启用';
    } else {
      return status;
    }
  };
  // 上传图片
  const uploadProps: UploadProps = {
    name: 'file',
    headers: {
      Authorization: getSessionStorageInfo('token')?.token,
    },
    action: bsinFileUploadUrl,
    data: {
      // currentPath: 'fileNo', //为空则使用CustomerNo作为文件夹
      tenantAppType: tenantAppType,
    },
    // 只能上传一个
    maxCount: 1,
    onChange(info) {
      // 控制path是否显示
      console.log(info);
      // 是加载
      let { file } = info;
      if (file?.status === 'done') {
        console.log(file.response);
        message.success(`${info.file.name} file uploaded successfully.`);
        formRef.setFieldValue('', file?.response.data.url);
        setLogoUrl(file?.response.data.url);
      } else if (file?.status === 'error') {
        setLogoUrl('');
        message.error(`${info.file.name} file upload failed.`);
      }
    },
    // 拖拽
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files);
    },
    onRemove(e) { },
  };

  const [openPayConfig, setOpenPayConfig] = useState(false);

  const showPayConfigDrawer = (record) => {
    setCurrenBizRoleApp(record)
    setOpenPayConfig(true);
    // 查询支付方式
    getPayInterfaceList({}).then((res) => {
      if (res?.code == 0 || res?.code == 0) {
        setPayChannelList(res?.data)
      }
    })
  };

  const onClosePayConfig = () => {
    setOpenPayConfig(false);
  };

  // 展示支付接口配置
  const showChildrenDrawer = (record) => {
    setChildrenDrawer(true);
    setCurrentPayChannel(record)

  };

  const onChildrenDrawerClose = () => {
    setChildrenDrawer(false);
  };

  const [childrenDrawer, setChildrenDrawer] = useState(false);


  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="接入应用列表" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getBizRoleAppPageList({
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
              addBizRoleAppModal();
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            新增
          </Button>,
        ]}
      />

      <Drawer title="支付通道" width={800} closable={false} onClose={onClosePayConfig} open={openPayConfig}>
        <Space size="middle" style={{ display: 'flex' }}>
          {payChannelList?.map((payChannel) => {
            return (
              <Card
                style={{ width: 200 }}
                styles={{ cover: { height: 100 } }}
                cover={
                  <img
                    style={{ height: 100 }}
                    alt="example"
                    src="https://gw.alipayobjects.com/zos/rmsportal/JiqGstEfoWAOHiTxclqi.png"
                  />
                }
                actions={[
                  <SettingOutlined key="setting"
                    onClick={() => {
                      showChildrenDrawer(payChannel)
                    }} />
                ]}
              >
                <Meta
                  title={payChannel?.payChannelName}
                />
              </Card>
            );
          })}
        </Space>
        <Drawer
          title="支付参数配置"
          width={736}
          closable={false}
          onClose={onChildrenDrawerClose}
          open={childrenDrawer}
        >
          <PayConfig currenBizRoleApp={currenBizRoleApp}  currentPayChannel={currentPayChannel} />
        </Drawer>
      </Drawer>

      {/* 新增|编辑模态框 */}
      <Modal
        title={addModalTitle}
        centered
        open={isAddBizRoleAppModal}
        onOk={confirmAddBizRoleApp}
        onCancel={onCancelBizRoleApp}
      >
        <Form
          name="basic"
          form={formRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ appChannel: '0' }}
        >
          <Form.Item
            label="应用名称"
            name="appName"
            rules={[{ required: true, message: '请输入应用名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="应用渠道"
            name="appChannel"
            rules={[{ required: true, message: '请选择应用渠道!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="0">请选择应用渠道</Option>
              <Option value="1">app应用</Option>
              <Option value="2">API接口</Option>
              <Option value="3">微信公众号</Option>
              <Option value="4">微信小程序</Option>
              <Option value="5">企业微信</Option>
              <Option value="6">微信支付</Option>
              <Option value="7">微信开放平台</Option>
              <Option value="8">个人微信</Option>
              <Option value="9">公众号菜单</Option>
              <Option value="10">其他</Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="appId"
            name="appId"
            rules={[{ required: true, message: '请输入appId!' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="appSecret"
            name="appSecret"
            rules={[{ required: true, message: '请输入appSecret!' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="token"
            name="token"
            rules={[{ required: true, message: '请输入token!' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="aesKey"
            name="aesKey"
            rules={[{ required: true, message: '请输入aesKey!' }]}
          >
            <Input />
          </Form.Item>


          <Form.Item
            label="服务回调地址"
            name="notifyUrl"
            rules={[{ required: true, message: '请输入服务回调地址!' }]}
          >
            <Input />
          </Form.Item>

          {/* <Form.Item label="商户logo" name="logoUrl">
            <Dragger {...uploadProps} listType="picture">
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击上传</p>
            </Dragger>
          </Form.Item> */}

          <Form.Item
            label="应用描述"
            name="appDescription"
            rules={[{ required: true, message: '请输入应用描述!' }]}
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
        open={isViewBizRoleAppModal}
        onOk={() => setIsViewBizRoleAppModal(false)}
        onCancel={() => setIsViewBizRoleAppModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="商户应用">
          <Descriptions.Item label="租户号">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="商户号">
            {isViewRecord?.bizRoleTypeNo}
          </Descriptions.Item>
          <Descriptions.Item label="应用名称">
            {isViewRecord?.appName}
          </Descriptions.Item>
          <Descriptions.Item label="应用ID">
            {isViewRecord?.appId}
          </Descriptions.Item>
          <Descriptions.Item label="应用密钥">
            {isViewRecord?.appSecret}
          </Descriptions.Item>
          <Descriptions.Item label="通知地址">
            {isViewRecord?.notifyUrl}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
