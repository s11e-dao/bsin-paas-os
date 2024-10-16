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
  Divider
} from 'antd';
import type { UploadProps } from 'antd/es/upload/interface';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined, InboxOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getMerchantAppList,
  getMerchantAuthorizableAppList,
  subscribeApps
} from './service';

import TableTitle from '../../components/TableTitle';
import { hex_md5 } from '@/utils/md5';
import {
  getLocalStorageInfo,
  getSessionStorageInfo,
} from '@/utils/localStorageInfo';

export default ({ subscribeFunction, setCurrentContent }) => {
  let bsinFileUploadUrl = process.env.bsinFileUploadUrl;

  let tenantAppType = process.env.tenantAppType;

  // 上传组件
  const { Dragger } = Upload;

  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [registerMerchantModal, setRegisterMerchantModal] = useState(false);
  // 查看模态框
  const [isViewTemplateModal, setIsViewTemplateModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 租户的岗位集合
  const [authorizableAppList, setAuthorizableAppList] = useState([]);

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
    <div key={record.dictType}>
      <a onClick={() => toViewContractTemplate(record)}>查看</a>
      <Divider type="vertical" />
      <a
        onClick={() => {
          subscribeFunction(record)
        }}
      >
        订阅功能
      </a>
      <Divider type="vertical" />
      <Popconfirm
        title="确定删除此条数据？?"
        onConfirm={() => toDelContractTemplate(record.id)}
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
   * 取消添加模板
   */
  const onCancelTemplate = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setRegisterMerchantModal(false);
  };

  /**
   * 删除模板
   */
  const toDelContractTemplate = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteMerchant({ serialNo });
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
    let viewRes = await getMerchantDetail({ serialNo });
    setIsViewTemplateModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfType = () => {
    let { type } = isViewRecord;
    // 商户类型：1、品牌商户 2、社区商户（供销社）
    if (type == '1') {
      return '品牌商户';
    } else if (type == '2') {
      return '社区商户（供销社）';
    } else {
      return status;
    }
  };

  const handleViewRecordOfStatus = () => {
    let { status } = isViewRecord;
    if (status == '0') {
      return '正常';
    } else if (status == '1') {
      return '进冻结行中';
    } else if (status == '2') {
      return '待审核';
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
    onRemove(e) { },
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="服务列表" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getMerchantAppList({
            orgId: getLocalStorageInfo('merchantInfo')?.merchantName || "" // 商户名称
          });
          console.log('😒', res);
          const result = {
            data: res.data
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
        pagination={false}
        toolBarRender={() => [
          <Button
            onClick={() => {
              setCurrentContent("serviceSubscribe")
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            订阅服务
          </Button>,
        ]}
      />

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
        <Descriptions title="商户信息">
          <Descriptions.Item label="客户号">
            {isViewRecord?.customerNo}
          </Descriptions.Item>
          <Descriptions.Item label="商户ID">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="商户类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="商户名称">
            {isViewRecord?.merchantName}
          </Descriptions.Item>
          <Descriptions.Item label="logo">
            {isViewRecord?.logoUrl}
          </Descriptions.Item>
          <Descriptions.Item label="企业工商号">
            {isViewRecord?.businessNo}
          </Descriptions.Item>
          <Descriptions.Item label="法人姓名">
            {isViewRecord?.legalPersonName}
          </Descriptions.Item>
          <Descriptions.Item label="联系电话">
            {isViewRecord?.phone}
          </Descriptions.Item>
          <Descriptions.Item label="商户审核状态">
            {handleViewRecordOfSauthenticationStatus()}
          </Descriptions.Item>
          <Descriptions.Item label="商户状态">
            {handleViewRecordOfStatus()}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="商户描述">
            {isViewRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
