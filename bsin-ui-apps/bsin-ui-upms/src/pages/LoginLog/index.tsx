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
  InputNumber,
  Upload,
} from 'antd';
import type { UploadProps } from 'antd/es/upload/interface';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined, InboxOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getAdsPageList,
  addAds,
  deleteAds,
  editAds,
  getAdsDetail,
} from './service';
import TableTitle from '../../components/TableTitle';
import { getSessionStorageInfo } from '../../utils/localStorageInfo';

export default () => {
  let bsinFileUploadUrl = process.env.bsinFileUploadUrl;
  let tenantAppType = process.env.tenantAppType;

  const { TextArea } = Input;
  const { Option } = Select;

  // 上传组件
  const { Dragger } = Upload;

  // 控制新增模态框
  const [isAdsModal, setIsAdsModal] = useState(false);
  // 查看模态框
  const [isViewAdsModal, setIsViewAdsModal] = useState(false);
  // 编辑模态框
  const [isEditAdsModal, setIsEditAdsModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 获取表单
  const [FormRef] = Form.useForm();

  // 图片
  const [imageUrl, setImageUrl] = useState<string | null>('');

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
        FormRef.setFieldValue('coverImage', file?.response.data.url);
        setImageUrl(file?.response.data.url);
      } else if (file?.status === 'error') {
        message.error(`${info.file.name} file upload failed.`);
      }
    },
    // 拖拽
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files);
    },
    onRemove(e) {},
  };

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
            toViewAds(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <a
          onClick={() => {
            toEditAds(record);
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
            toDelAds(record);
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
  const increaseAds = () => {
    setIsAdsModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmAddAds = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();

        response.imageUrl = imageUrl;
        console.log(response);
        addAds(response).then((res) => {
          console.log('add', res);
          if (res?.code == '000000') {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsAdsModal(false);
          } else {
            message.error(`添加失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  const confirmEditAds = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        editAds(response).then((res) => {
          if (res?.code == '000000') {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsEditAdsModal(false);
          } else {
            message.error(`编辑失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  /**
   * 取消添加模板
   */
  const onCancelAds = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsAdsModal(false);
    setIsEditAdsModal(false);
  };

  /**
   * 删除模板
   */
  const toDelAds = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteAds({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === '000000') {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewAds = async (record) => {
    let { serialNo } = record;
    let viewRes = await getAdsDetail({ serialNo });
    setIsViewAdsModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  const toEditAds = async (record) => {
    let { serialNo } = record;
    let viewRes = await getAdsDetail({ serialNo });
    setIsEditAdsModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfType = () => {
    let { type } = isViewRecord;
    if (type == '1') {
      return '商户首页';
    } else if (type == '2') {
      return '数字人首页';
    } else if (type == '3') {
      return '数字资产页';
    } else {
      return type;
    }
  };

  const handleViewRecordOfStatus = () => {
    let { status } = isViewRecord;
    if (status == '0') {
      return '禁用';
    } else if (status == '1') {
      return '启用';
    } else {
      return status;
    }
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="登录日志" />}
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
        pagination={{
          pageSize: 10,
        }}
        toolBarRender={() => []}
      />

      {/* 新增合约模板模态框 */}
      <Modal
        title="新增"
        centered
        open={isAdsModal}
        onOk={confirmAddAds}
        onCancel={onCancelAds}
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
              <Option value="1">商户首页</Option>
              <Option value="2">数字人首页</Option>
              <Option value="3">数字资产页</Option>
              <Option value="4">其他</Option>
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

          <Form.Item label="广告图片" name="imageUrl">
            <Dragger {...uploadProps} listType="picture">
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击上传</p>
            </Dragger>
          </Form.Item>

          <Form.Item
            label="描述"
            name="description"
            rules={[{ required: true, message: '请输入描述!' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>

      {/* 编辑广告模态框 */}
      <Modal
        title="编辑"
        centered
        open={isEditAdsModal}
        onOk={confirmEditAds}
        onCancel={onCancelAds}
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
        open={isViewAdsModal}
        onOk={() => setIsViewAdsModal(false)}
        onCancel={() => setIsViewAdsModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="广告信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="广告编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="状态">
            {handleViewRecordOfStatus()}
          </Descriptions.Item>
          <Descriptions.Item label="名称">
            {isViewRecord?.title}
          </Descriptions.Item>
          <Descriptions.Item label="图片">
            {isViewRecord?.imageUrl}
          </Descriptions.Item>
          <Descriptions.Item label="链接">
            {isViewRecord?.linkUrl}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="更新时间">
            {isViewRecord?.updateTime}
          </Descriptions.Item>
          <Descriptions.Item label="描述">
            {isViewRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
