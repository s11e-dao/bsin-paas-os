import React, { useState, useEffect } from 'react';
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
} from 'antd';
import type { UploadProps } from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import {
  PlusOutlined,
  LoadingOutlined,
  InboxOutlined,
} from '@ant-design/icons';

import columnsData, { columnsDataType } from './data';
import {
  getContractPageList,
  getContractList,
  addContract,
  deleteContract,
  getContractDetail,
  deployContract,
} from './service';

import {
  getContractProtocolPageList,
  getContractProtocolList,
  addContractProtocol,
  deleteContractProtocol,
  getContractProtocolDetail,
} from '../ContractProtocol/service';

import { getMetadataTemplatePageList } from './../MetadataTemplate/service';
import { getMetadataFileList } from './../MetadataList/service';

import TableTitle from '../../../components/TableTitle';

// 上传组件
const { Dragger } = Upload;
const { Option } = Select;

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [addContractModal, setAddContractModal] = useState(false);
  // 查看模态框
  const [isViewContractModal, setIsViewContractModal] = useState(false);

  // 部署合约模态框
  const [isDeployContractModal, setIsDeployContractModal] = useState(false);

  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 获取表单
  const [FormRef] = Form.useForm();

  const [contractProtocolList, setContractProtocolList] = useState([]);
  const [contractProtocolChoosed, setContractProtocolChoosed] = useState({});
  const [metadataTemplateList, setMetadataTemplateList] = useState([]);
  const [metadataFilePathList, setMetadataFilePath] = useState([]);
  const [protocolCode, setProtocolCode] = useState('');
  const [protocolStandards, setProtocolStandards] = useState('');
  const [protocolChange, setProtocolChange] = useState(false);
  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 合约文件
  const [contractFile, setContractFileh] = useState<string | null>('');

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewContract(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <a
          onClick={() => {
            openAddContractModal();
          }}
        >
          添加合约
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <Popconfirm
          title="确定删除此条模板？"
          okText="是"
          cancelText="否"
          onConfirm={() => {
            toDelContract(record);
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

  useEffect(() => {
    // 查询协议
    let params = {
      current: '1',
      pageSize: '99',
    };
    getContractProtocolList(params).then((res) => {
      setContractProtocolList(res?.data);
    });
    getMetadataTemplatePageList(params).then((res) => {
      setMetadataTemplateList(res?.data);
    });
    getMetadataFileList(params).then((res) => {
      setMetadataFilePath(res?.data);
    });
  }, []);

  /**
   * 以下内容为操作相关
   */

  // 新增合约
  const openAddContractModal = () => {
    setAddContractModal(true);
  };

  /**
   * 确认添加合约
   */
  const confirmAddContract = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        addContract(response).then((res) => {
          console.log('add', res);
          if (res?.code == '000000') {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setAddContractModal(false);
          } else {
            message.error(`添加合约失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  /**
   * 确认部署合约
   */
  const confirmDeployContract = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        deployContract(response).then((res) => {
          console.log('add', res);
          if (res?.code == '000000') {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsDeployContractModal(false);
          } else {
            message.error(`部署合约失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  /**
   * 取消添加模板
   */
  const onCancelAddContract = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setAddContractModal(false);
  };

  /**
   * 取消添加模板
   */
  const onCancelDeployContract = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsDeployContractModal(false);
  };

  /**
   * 删除模板
   */
  const toDelContract = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteContract({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === '000000') {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewContract = async (record) => {
    let { serialNo } = record;
    let viewRes = await getContractDetail({ serialNo });
    setIsViewContractModal(true);
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

  const protocolCodeChange = (value) => {
    console.log(value);
    contractProtocolList?.map((contractProtocol) => {
      if (contractProtocol?.protocolCode == value) {
        setContractProtocolChoosed(contractProtocol);
        FormRef.setFieldValue('contractProtocolNo', contractProtocol.serialNo);
        console.log(contractProtocol);
      }
    });
    setProtocolCode(value);
    setProtocolChange(true);
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="智能合约列表" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getContractPageList({
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
              setIsDeployContractModal(true);
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            部署合约
          </Button>,
        ]}
      />
      {/* 新增合约模态框：将已经存在的合约加入数据库 */}
      <Modal
        title="添加合约"
        centered
        open={addContractModal}
        onOk={confirmAddContract}
        onCancel={onCancelAddContract}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ protocolStandards: 'ERC1155', type: '1' }}
        >
          <Form.Item
            label="资产类型"
            name="protocolCode"
            rules={[{ required: true, message: '请选择资产类型!' }]}
          >
            <Select
              style={{ width: '100%' }}
              onChange={(value) => {
                protocolCodeChange(value);
              }}
            >
              <Option value="1">请选择合约协议</Option>
              {contractProtocolList?.map((contractProtocol) => {
                console.log(contractProtocol?.protocolStandards);
                return (
                  <Option value={contractProtocol?.protocolCode}>
                    {(contractProtocol?.serialNo).slice(-4) +
                      '-' +
                      contractProtocol?.protocolCode}
                  </Option>
                );
              })}
            </Select>
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
            label="协议编号"
            name="protocolCode"
            rules={[{ required: true, message: '请选择协议编号)!' }]}
          >
            <Input />
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

      {/* 部署合约模态框 */}
      <Modal
        title="部署合约"
        centered
        open={isDeployContractModal}
        onOk={confirmDeployContract}
        onCancel={onCancelDeployContract}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{}}
        >
          <Form.Item
            label="合约协议"
            name="contractProtocolNo"
            rules={[{ required: true, message: '请选合约协议!' }]}
          >
            <Select
              style={{ width: '100%' }}
              onChange={(value) => {
                protocolCodeChange(value);
              }}
            >
              <Option value="1">请选择合约协议</Option>
              {contractProtocolList?.map((contractProtocol) => {
                console.log(contractProtocol?.protocolStandards);
                return (
                  <Option value={contractProtocol?.serialNo}>
                    {(contractProtocol?.version).slice(-4) +
                      '-' +
                      contractProtocol?.protocolName +
                      '-' +
                      contractProtocol?.chainType}
                  </Option>
                );
              })}
            </Select>
          </Form.Item>

          <Form.Item
            label="链网络"
            name="chainEnv"
            rules={[{ required: true, message: '请选择链网络!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="main">main</Option>
              <Option value="test">test</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="合约描述"
            name="description"
            rules={[{ required: true, message: '请输入合约描述!' }]}
          >
            <TextArea />
          </Form.Item>
        </Form>
      </Modal>

      {/* 查看详情模态框 */}
      <Modal
        title="查看合约信息"
        width={800}
        centered
        visible={isViewContractModal}
        onOk={() => setIsViewContractModal(false)}
        onCancel={() => setIsViewContractModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="合约信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="合约编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="合约名称">
            {isViewRecord?.name}
          </Descriptions.Item>
          <Descriptions.Item label="合约地址">
            {isViewRecord?.contract}
          </Descriptions.Item>
          <Descriptions.Item label="交易hash">
            {isViewRecord?.txHash}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议编号">
            {isViewRecord?.contractProtocolNo}
          </Descriptions.Item>
          <Descriptions.Item label="合约类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="合约分类">
            {handleViewRecordOfCategory()}
          </Descriptions.Item>
          <Descriptions.Item label="合约标准">
            {isViewRecord?.protocolStandards}
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
          <Descriptions.Item label="版本">
            {isViewRecord?.version}
          </Descriptions.Item>
          <Descriptions.Item label="合约描述">
            {isViewRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
