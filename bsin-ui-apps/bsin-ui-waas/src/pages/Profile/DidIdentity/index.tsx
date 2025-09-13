import React, { useState } from 'react';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import {
  PlusOutlined,
  LoadingOutlined,
  InboxOutlined,
} from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
// ipfs
import {
  getMetadataTemplatePageList,
  addMetadataTemplate,
  deleteMetadataTemplate,
  getMetadataTemplateDetail,
} from './service';
import TableTitle from '@/components/TableTitle';
import {
  Input,
  Modal,
  message,
  Form,
  Button,
  Upload,
  InputNumber,
  Popconfirm,
  Select,
  Descriptions,
  Divider,
} from 'antd';
import type { UploadProps } from 'antd';

const { TextArea } = Input;
// 上传组件
const { Dragger } = Upload;
const { Option } = Select;

export default () => {
  let ipfsApiUrl = process.env.ipfsApiUrl;
  let ipfsGatewayUrl = process.env.ipfsGatewauUrl;

  let metaDatajsonTemp = {
    name: 's11e-DAO',
    description: 's11e-DAO 出品',
    external_url: '1',
    image: '身份形象的URI',
  };
  // 控制新增模态框
  const [isIncreaseModal, setIsIncreaseModal] = useState(false);

  // 查看模态框
  const [isViewTemplateModal, setIsViewTemplateModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;
  // 图片的path
  const [metaDataImagePath, setMetaDataImagePath] = useState<string | null>('');
  // 控制textarea字段更新
  const [metaDatajson, setMetaDatajson] = useState(metaDatajsonTemp);
  // ipfs地址

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <div key={record.serialNo}>
      <a onClick={() => {
            toViewMetadataTemplate(record);
          }}>查看</a>
      <Divider type="vertical" />
      <Popconfirm
        title="是否删除此条数据？"
        onConfirm={() => {
          toDelMetadataTemplate(record);
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

  // 赋值表格操作行
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });
  // 上传图片
  const uploadProps: UploadProps = {
    name: 'file',
    // 只能上传一个
    maxCount: 1,
    onChange(info) {
      // 控制path是否显示
      console.log(info);
      // 是加载
      if (info.fileList.length) {
        let { file } = info;
        console.log(file);
      }
    },
    beforeUpload() {
      return false;
    },
    // 拖拽
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files);
    },
    onRemove(e) {
      setMetaDataImagePath('');
    },
  };

  /**
   * 点击新增按钮
   */
  const openAddModal = () => {
    setIsIncreaseModal(true);
  };

  /**
   * 新增模态框确定按钮
   */
  const confirmIncrease = async () => {
    let formInfo = await addFormRef.validateFields();
    console.log(formInfo);
    if (!formInfo.templateContent) {
      formInfo.templateContent = JSON.stringify(metaDatajson, null, 2);
    }
    // if (!metaDataImagePath) return message.info('请上传图片');
    let res = await addMetadataTemplate({
      ...formInfo,
      // metadataImage: ipfsGatewayUrl + metaDataImagePath,
    });
    console.log(res);
    if (res.code === 0 ) {
      message.success('添加元数据成功');
      addFormRef.resetFields();
      setMetaDataImagePath(null);
      actionRef.current?.reload();
      setIsIncreaseModal(false);
    } else {
      message.error(`添加元数据失败： ${res?.message}`);
    }
  };

  /**
   * 新增模态框取消按钮
   */
  const onCancelIncrease = () => {
    addFormRef.resetFields();
    setMetaDataImagePath(null);
    setIsIncreaseModal(false);
  };

  /**
   * 删除模板
   */
  const toDelMetadataTemplate = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteMetadataTemplate({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === 0) {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewMetadataTemplate = async (record) => {
    let { serialNo } = record;
    let viewRes = await getMetadataTemplateDetail({ serialNo });
    setIsViewTemplateModal(true);
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

  // 表格的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();
  // 添加用户数据表单验证 获取表单
  const [addFormRef] = Form.useForm();
  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="DID身份" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          console.log(params);

          let res = await getMetadataTemplatePageList({ ...params });
          console.log(res);
          console.log('🎉');
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
              openAddModal();
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            新增元数据模板
          </Button>,
        ]}
      />
      {/* 新增模态框 */}
      <Modal
        centered
        open={isIncreaseModal}
        onOk={confirmIncrease}
        onCancel={onCancelIncrease}
        width={650}
        title="新增"
      >
        <Form
          name="basic"
          form={addFormRef}
          labelCol={{ span: 8 }}
          wrapperCol={{ span: 12 }}
          initialValues={{ remember: true }}
          autoComplete="off"
        >
          <Form.Item
            label="模板名称"
            name="templateName"
            rules={[{ required: true, message: '请填写模板名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="模板编号"
            name="templateCode"
            rules={[{ required: true, message: '请填写模板编号!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="模板描述"
            name="description"
            rules={[{ required: true, message: '请填写模板描述!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="上传图片" name="templateUrl">
            <Dragger {...uploadProps} listType="picture">
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击上传</p>
            </Dragger>
            {metaDataImagePath ? ipfsGatewayUrl + metaDataImagePath : null}
          </Form.Item>

          <Form.Item label="模板内容" name="templateContent">
            <TextArea
              autoSize={{ minRows: 9, maxRows: 9 }}
              defaultValue={JSON.stringify(metaDatajson, null, 2)}
              style={{ marginTop: 20 }}
            />
          </Form.Item>
        </Form>
      </Modal>

      {/* 查看DID身份详情模态框 */}
      <Modal
        title="查看DID身份详情"
        width={800}
        centered
        open={isViewTemplateModal}
        onOk={() => setIsViewTemplateModal(false)}
        onCancel={() => setIsViewTemplateModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="DID身份信息">
          <Descriptions.Item label="ID">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="DID名称">
            {isViewRecord?.name}
          </Descriptions.Item>
          <Descriptions.Item label="DID标识符">
            {isViewRecord?.did}
          </Descriptions.Item>
          <Descriptions.Item label="符号">
            {isViewRecord?.symbol}
          </Descriptions.Item>
          <Descriptions.Item label="描述">
            {isViewRecord?.description}
          </Descriptions.Item>
          <Descriptions.Item label="业务角色类型">
            {isViewRecord?.bizRoleType}
          </Descriptions.Item>
          <Descriptions.Item label="业务角色编号">
            {isViewRecord?.bizRoleTypeNo}
          </Descriptions.Item>
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="更新时间">
            {isViewRecord?.updateTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
