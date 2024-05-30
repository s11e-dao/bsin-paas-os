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
  Upload,
} from 'antd';
import type { UploadProps } from 'antd/es/upload/interface';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined, InboxOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getMerchantProductPageList,
  addMerchantProduct,
  editMerchantProduct,
  deleteMerchantProduct,
  getMerchantProductDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';
import { hex_md5 } from '@/utils/md5';
import {
  getLocalStorageInfo,
  getSessionStorageInfo,
} from '@/utils/localStorageInfo';

export default () => {
  let fileUrl = process.env.fileUrl;
  let bsinFileUploadUrl = process.env.bsinFileUploadUrl;

  let tenantAppType = process.env.tenantAppType;

  // 上传组件
  const { Dragger } = Upload;

  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增、编辑模态框title
  const [addModalTitle, setAddModalTitle] = React.useState('添加');
  // 控制新增模态框
  const [registerMerchantModal, setRegisterMerchantModal] = useState(false);
  // 查看模态框
  const [isViewTemplateModal, setIsViewTemplateModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 选择的数据
  const [checkItem, setCheckItem] = useState({});

  const [logoUrl, setLogoUrl] = useState('');
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
        <a
          onClick={() => {
            handleEditModel(record);
          }}
        >
          编辑
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
  const openRegisterMerchantModal = () => {
    setRegisterMerchantModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmRegisterMerchant = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        let reqParam = {
          ...response,
        };
        console.log(getLocalStorageInfo('userInformation'));
        console.log(reqParam);

        if (addModalTitle === '新增') {
          addMerchantProduct(reqParam).then((res) => {
            console.log('add', res);
            if (res.code === '000000') {
              message.success('添加成功');
              // 重置输入的表单
              FormRef.resetFields();
              // 刷新proTable
              actionRef.current?.reload();
              setRegisterMerchantModal(false);
            } else {
              message.error(`失败： ${res?.message}`);
            }
          });
        } else {
          reqParam.serialNo = checkItem.serialNo;
          editMerchantProduct(reqParam).then((res) => {
            console.log('add', res);
            if (res.code === '000000') {
              message.success('修改成功');
              // 重置输入的表单
              FormRef.resetFields();
              // 刷新proTable
              actionRef.current?.reload();
              setRegisterMerchantModal(false);
            } else {
              message.error(`失败： ${res?.message}`);
            }
          });
        }
      })
      .catch(() => {});
  };

  /**
   * 取消添加模板
   */
  const onCancelTemplate = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setRegisterMerchantModal(false);
  };

  // 点击编辑
  const handleEditModel = (record: DictColumnsItem) => {
    console.log('handleEditModel', record);
    FormRef.setFieldsValue(record);
    setAddModalTitle('编辑');
    setCheckItem(record);
    setRegisterMerchantModal(true);
  };

  /**
   * 删除模板
   */
  const toDelContractTemplate = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteMerchantProduct({ serialNo });
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
    let viewRes = await getMerchantProductDetail({ serialNo });
    setIsViewTemplateModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(record);
  };

  const handleViewRecordOfStatus = () => {
    let { status } = isViewRecord;
    if (status == '0') {
      return '正常';
    } else if (status == '1') {
      return '进冻结行中';
    } else {
      return status;
    }
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
        FormRef.setFieldValue('', file?.response.data.url);
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
    onRemove(e) {},
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="产品列表" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getMerchantProductPageList({
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
              openRegisterMerchantModal();
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
        title={addModalTitle}
        centered
        open={registerMerchantModal}
        onOk={confirmRegisterMerchant}
        onCancel={onCancelTemplate}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ productType: '1' }}
        >
          <Form.Item
            label="产品名称"
            name="productName"
            rules={[{ required: true, message: '请输入产品名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="产品类型"
            name="productType"
            rules={[{ required: true, message: '请输入产品类型!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="1">应用</Option>
              <Option value="2">接口</Option>
            </Select>
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
            label="产品描述"
            name="productDescription"
            rules={[{ required: true, message: '请输入产品描述!' }]}
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
        <Descriptions title="商户产品">
          <Descriptions.Item label="租户号">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="商户号">
            {isViewRecord?.merchantNo}
          </Descriptions.Item>
          <Descriptions.Item label="产品名称">
            {isViewRecord?.productName}
          </Descriptions.Item>
          <Descriptions.Item label="产品ID">
            {isViewRecord?.productId}
          </Descriptions.Item>
          <Descriptions.Item label="产品密钥">
            {isViewRecord?.productSecret}
          </Descriptions.Item>
          <Descriptions.Item label="通知地址">
            {isViewRecord?.notifyUrl}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
