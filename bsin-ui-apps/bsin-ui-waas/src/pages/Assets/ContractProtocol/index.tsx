import React, { useState } from 'react';
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Upload,
  Select,
  Popconfirm,
  Descriptions,
  Space,
  Tooltip,
  Divider,
} from 'antd';
import type { UploadProps } from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import {
  PlusOutlined,
  LoadingOutlined,
  InboxOutlined,
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
} from '@ant-design/icons';
import { getSessionStorageInfo } from '../../../utils/localStorageInfo';

import columnsData, { columnsDataType } from './data';
import {
  getContractProtocolPageList,
  getContractProtocolList,
  addContractProtocol,
  deleteContractProtocol,
  getContractProtocolDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';

// 上传组件
const { Dragger } = Upload;
const { Option } = Select;

export default () => {
  let bsinFileUploadUrl = process.env.bsinFileUploadUrl;
  let tenantAppType = process.env.tenantAppType;

  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [addContractProtocolModal, setAddContractProtocolModal] = useState(
    false,
  );
  // 查看模态框
  const [isViewTemplateModal, setIsViewTemplateModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 获取表单
  const [FormRef] = Form.useForm();

  // 图片的path
  const [coverImage, setCoverImage] = useState<string | null>('');

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 合约文件
  const [contractFile, setContractFileh] = useState<string | null>('');

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <Space key={record.serialNo}>
      <Tooltip title="查看详情">
        <a onClick={() => toViewContractTemplate(record)}>
          <EyeOutlined />
          详情
        </a>
      </Tooltip>
      <Divider type="vertical" />
      <Popconfirm
        title="确定要删除这个合约协议吗？"
        onConfirm={() => toDelContractTemplate(record)}
        okText="确定"
        cancelText="取消"
      >
        <Tooltip title="删除">
          <a style={{ color: '#ff4d4f' }}>
            <DeleteOutlined />
            删除
          </a>
        </Tooltip>
      </Popconfirm>
    </Space>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  // 上传合约文件
  const uploadPropsFile: UploadProps = {
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
    beforeUpload(file) {
      return new Promise((resolve) => {
        const reader = new FileReader();
        reader.readAsText(file);
        reader.onload = () => {
          console.log(reader.result);
          let JsonFile = JSON.parse(reader.result);
          console.log(JsonFile);
          FormRef.setFieldValue('protocolName', JsonFile?.contractName);
          FormRef.setFieldValue('protocolBytecode', JsonFile?.bytecode);
          FormRef.setFieldValue('protocolAbi', JSON.stringify(JsonFile?.abi));
          resolve(file as any);
        };
      });
    },
    // 拖拽
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files);
    },
    onRemove(e) {
      setContractFileh('');
    },
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
        FormRef.setFieldValue('coverImage', file?.response.data.url);
        setCoverImage(file?.response.data.url);
      } else if (file?.status === 'error') {
        message.error(`${info.file.name} file upload failed.`);
      }
    },
    // 拖拽
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files);
    },
    onRemove(e) {
      // setCoverImage('');
    },
  };
  /**
   * 以下内容为操作相关
   */

  // 新增合约模板
  const openAddContractProtocolModal = () => {
    setAddContractProtocolModal(true);
  };

  /**
   * 确认添加合约模板
   */
  const confirmTemplate = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        response.coverImage = coverImage;
        console.log(response);
        addContractProtocol(response).then((res) => {
          console.log('add', res);
          if (res?.code == 0) {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setAddContractProtocolModal(false);
          } else {
            message.error(`添加合约模板失败： ${res?.message}`);
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
    setAddContractProtocolModal(false);
  };

  /**
   * 删除模板
   */
  const toDelContractTemplate = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteContractProtocol({ serialNo });
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
    let viewRes = await getContractProtocolDetail({ serialNo });
    setIsViewTemplateModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfType = () => {
    let { type } = isViewRecord;
    if (type == `1`) {
      return '数字资产';
    } else if (type == `2`) {
      return 'PFP';
    } else if (type == `3`) {
      return '账户-DP';
    } else if (type == `4`) {
      return '数字门票';
    } else if (type == `5`) {
      return 'Pass卡';
    } else if (type == `6`) {
      return '账户-BC';
    } else if (type == `7`) {
      return '满减';
    } else if (type == `8`) {
      return '权限';
    } else if (type == `9`) {
      return '会员等级';
    } else if (type == `10`) {
      return '其他';
    } else {
      return type;
    }
  };

  const handleViewRecordOfCategory = () => {
    // 合约分类： 1、Core 2、Factory 3、Extension 4、Wrapper  5、Proxy  6、Other
    let { category } = isViewRecord;
    if (category == `1`) {
      return 'Core';
    } else if (category == `2`) {
      return 'Factory';
    } else if (category == `3`) {
      return 'Extension';
    } else if (category == `4`) {
      return 'Wrapper';
    } else if (category == `5`) {
      return 'Proxy';
    } else if (category == `6`) {
      return 'Other';
    } else {
      return category;
    }
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="智能合约协议" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getContractProtocolPageList({
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
              openAddContractProtocolModal();
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
        title="添加合约协议"
        centered
        open={addContractProtocolModal}
        onOk={confirmTemplate}
        onCancel={onCancelTemplate}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ protocolStandards: 'Other', type: '10' }}
        >
          <Form.Item label="上传合约编译文件" name="contractFile">
            <div class="el-upload__tip text-red">
              支持多个文件上传，依次点击“选择合约文件”，添加同种类型的合约
            </div>
            <Dragger {...uploadPropsFile} listType="text">
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击上传</p>
            </Dragger>
            {/* {contractFile ? contractFile : null} */}
          </Form.Item>

          <Form.Item
            label="协议标准"
            name="protocolStandards"
            rules={[{ required: true, message: '请选择协议标准!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="ERC20">ERC20</Option>
              <Option value="ERC721">ERC721</Option>
              <Option value="ERC1155">ERC1155</Option>
              <Option value="ERC3525">ERC3525</Option>
              <Option value="DaoBookCore">DaoBookCore</Option>
              <Option value="DaoBookFactory">DaoBookFactory</Option>
              <Option value="DaoBookExtension">DaoBookExtension</Option>
              <Option value="DaoBookWrapper">DaoBookWrapper</Option>
              <Option value="Other">Other</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="协议类型"
            name="type"
            rules={[{ required: true, message: '请选择协议类型!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="1">数字徽章</Option>
              <Option value="2">PFP</Option>
              <Option value="3">账户-DP</Option>
              <Option value="4">数字门票</Option>
              <Option value="5">Pass卡</Option>
              <Option value="6">账户-BC</Option>
              <Option value="7">满减</Option>
              <Option value="8">权限</Option>
              <Option value="9">会员等级</Option>
              <Option value="10">其他</Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="合约分类"
            name="category"
            rules={[{ required: true, message: '请选择合约分类!' }]}
          >
            {/* // 合约分类： 1、Core 2、Factory 3、Extension 4、Wrapper  5、Proxy  6、Other */}
            <Select style={{ width: '100%' }}>
              <Option value="1">Core</Option>
              <Option value="2">Factory</Option>
              <Option value="3">Extension</Option>
              <Option value="4">Wrapper</Option>
              <Option value="5">Proxy</Option>
              <Option value="6">Other</Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="链类型"
            name="chainType"
            rules={[{ required: true, message: '请选择链类型!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="conflux">conflux</Option>
              <Option value="polygon">polygon</Option>
              <Option value="ethereum">ethereum</Option>
              <Option value="tron">tron</Option>
              <Option value="bsc">bsc</Option>
              <Option value="evm">evm</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="协议名称"
            name="protocolName"
            rules={[{ required: true, message: '请输入协议/合约名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="协议版本号"
            name="version"
            rules={[{ required: true, message: '请输入协议/合约版本号!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="协议编号"
            name="protocolCode"
            rules={[
              { required: true, message: '请输入协议编号(bigan-erc721-pfp)!' },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="协议bytecode"
            name="protocolBytecode"
            editable="false"
            rules={[{ required: true, message: '请输入协议bytecode!' }]}
          >
            <TextArea />
          </Form.Item>
          <Form.Item
            label="协议abi字符"
            name="protocolAbi"
            rules={[{ required: true, message: '请输入协议abi字符!' }]}
          >
            <TextArea />
          </Form.Item>

          <Form.Item label="上传封面图片" name="coverImage">
            <Dragger {...uploadProps} listType="picture">
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击上传</p>
            </Dragger>
          </Form.Item>

          <Form.Item
            label="协议描述"
            name="description"
            rules={[{ required: true, message: '请输入协议描述!' }]}
          >
            <TextArea />
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看详情模态框 */}
      <Modal
        title="查看合约协议"
        width={800}
        centered
        visible={isViewTemplateModal}
        onOk={() => setIsViewTemplateModal(false)}
        onCancel={() => setIsViewTemplateModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="合约协议信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          {/* <Descriptions.Item label="协议ID">
            {isViewRecord?.serialNo}
          </Descriptions.Item> */}
          <Descriptions.Item label="合约协议类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议分类">
            {handleViewRecordOfCategory()}
          </Descriptions.Item>
          <Descriptions.Item label="封面图片">
            {isViewRecord?.coverImage}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议标准">
            {isViewRecord?.protocolStandards}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议项目编号">
            {isViewRecord?.protocolCode}
          </Descriptions.Item>
          <Descriptions.Item label="协议名称">
            {isViewRecord?.protocolName}
          </Descriptions.Item>
          <Descriptions.Item label="链类型">
            {isViewRecord?.chainType}
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
          <Descriptions.Item label="协议版本">
            {isViewRecord?.version}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议bytecode">
            {isViewRecord?.protocolBytecode}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议abi字符">
            {isViewRecord?.protocolAbi}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
