import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  Radio,
  message,
  Button,
  Select,
  Card,
  InputNumber,
  Descriptions,
  Tooltip,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import { issueDigitalAssetsCollection } from './service';
import { getContractProtocolList } from './../ContractProtocol/service';
import { getMetadataTemplatePageList } from './../MetadataTemplate/service';
import { getMetadataFileList } from './../MetadataList/service';
import styles from './index.css';

export default ({ setCurrentContent }) => {
  
  const [contractProtocolList, setContractProtocolList] = useState([]);

  const [contractProtocolChoosed, setContractProtocolChoosed] = useState({});

  const [metadataTemplateList, setMetadataTemplateList] = useState([]);

  const [metadataFilePathList, setMetadataFilePath] = useState([]);

  const [protocolCode, setProtocolCode] = useState('');
  const [protocolStandards, setProtocolStandards] = useState('');
  const [protocolChange, setProtocolChange] = useState(false);

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

  const { Option } = Select;
  // 获取表单
  const [FormRef] = Form.useForm();

  /**
   * 确认添加模板
   */
  const confirmIssue = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        response.assetsCollectionType = contractProtocolChoosed?.type;
        issueDigitalAssetsCollection(response).then((res) => {
          console.log('issue', res);
          if (res?.code == 0) {
            // 返回列表
            setCurrentContent('assetsCollection');
            // 重置输入的表单
            FormRef.resetFields();
          } else {
            message.error(`发行失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  // const mergedArrow = useMemo(() => {
  //   if (arrow === 'Hide') {
  //     return false;
  //   }

  //   if (arrow === 'Show') {
  //     return true;
  //   }

  //   return {
  //     pointAtCenter: true,
  //   };
  // }, [arrow]);

  const protocolCodeChange = (value) => {
    console.log(value);
    contractProtocolList.map((contractProtocol) => {
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
    <>
      <Card style={{ marginBottom: 16 }}>
        <Button
          type="primary"
          onClick={() => {
            setCurrentContent('assetsCollection');
          }}
          className={styles.btn}
        >
          返回
        </Button>
        <Descriptions title="发行数字资产"></Descriptions>
        <div className={styles.addForm}>
          <Form
            name="basic"
            form={FormRef}
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 7 }}
            // 表单默认值
            initialValues={{
              chainEnv: 'test',
              metadataImageSameFlag: '0',
              bondingCurveFlag: '0',
              sponsorFlag: '0',
              chainType: 'conflux',
              decimals: 0,
              totalSupply: '1000',
              initialSupply: '0',
              protocolCode: '1',
              metadataTemplateNo: '1',
              metadataFilePathNo: '1',
              collectionType: '',
            }}
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
                <Option value="1">请选择资产类型</Option>
                {contractProtocolList.map((contractProtocol) => {
                  //筛选出ERC721/1155的合约
                  console.log(contractProtocol?.protocolStandards);
                  if (
                    contractProtocol?.protocolStandards == 'ERC721' ||
                    contractProtocol?.protocolStandards == 'ERC1155'
                  ) {
                    return (
                      <Option value={contractProtocol?.protocolCode}>
                        {(contractProtocol?.serialNo).slice(-4) +
                          '-' +
                          contractProtocol?.protocolCode +
                          '-' +
                          contractProtocol?.version}
                      </Option>
                    );
                  }
                })}
              </Select>
            </Form.Item>
            <Form.Item
              label="合约协议编号"
              name="contractProtocolNo"
              rules={[{ required: true, message: '请选输入协议编号!' }]}
            >
              <Input disabled />
            </Form.Item>
            {protocolChange ? (
              <Form.Item
                label="集合资产协议标准"
                name="collectionStandards"
                rules={[{ required: false, message: '请输入集合资产类型!' }]}
              >
                <Input
                  defaultValue={contractProtocolChoosed?.protocolStandards}
                  disabled
                />
              </Form.Item>
            ) : null}

            {protocolChange ? (
              <Form.Item
                label="集合资产类型"
                name="assetsCollectionType"
                rules={[{ required: false, message: '请输入集合资产类型!' }]}
              >
                <Input defaultValue={contractProtocolChoosed?.type} disabled />
              </Form.Item>
            ) : null}

            <Form.Item
              label="集合名称"
              name="name"
              rules={[{ required: true, message: '请输入集合名称!' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              label="集合符号"
              name="symbol"
              rules={[{ required: true, message: '请输入集合符号!' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              label="总供应量"
              name="totalSupply"
              rules={[{ required: true, message: '请输入总量!' }]}
            >
              <Input min={0} />
            </Form.Item>
            <Form.Item
              label="资产描述"
              name="description"
              rules={[{ required: true, message: '请输入资产描述!' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              label="元数据模板"
              name="metadataTemplateNo"
              rules={[{ required: true, message: '请选择元数据模板!' }]}
            >
              <Select style={{ width: '100%' }}>
                <Option value="1">请选择元数据模板</Option>
                {metadataTemplateList?.map((metadataTemplate) => {
                  return (
                    <Option value={metadataTemplate?.serialNo}>
                      {metadataTemplate?.templateName}
                    </Option>
                  );
                })}
              </Select>
            </Form.Item>
            <Form.Item
              label="元数据图片路径"
              name="metadataFilePathNo"
              rules={[{ required: true, message: '请选择元数据图片路径!' }]}
            >
              <Select style={{ width: '100%' }}>
                <Option value="1">请选择元数据图片路径</Option>
                {metadataFilePathList.map((metadataFilePath) => {
                  return (
                    <Option value={metadataFilePath?.serialNo}>
                      {metadataFilePath?.fileName}
                    </Option>
                  );
                })}
              </Select>
            </Form.Item>
            <Form.Item
              label="baseURI"
              name="baseURI"
              rules={[{ required: true, message: '请输入元数据前缀!' }]}
            >
              <Input placeholder="http://ipfs.s11edao.com/ipfs/" />
            </Form.Item>
            {protocolCode.match('ERC1155') || protocolCode.match('erc1155') ? (
              <Form.Item
                label="是否是同质化铸造NFT"
                name="metadataImageSameFlag"
                rules={[
                  { required: true, message: '请选择是否是同质化铸造NFT!' },
                ]}
              >
                <Radio.Group value="0">
                  <Radio value="0">否</Radio>
                  <Radio value="1">是</Radio>
                </Radio.Group>
              </Form.Item>
            ) : null}
            <Form.Item
              label="是否基于联合曲线发行"
              name="bondingCurveFlag"
              rules={[
                { required: true, message: '请选择是否基于联合曲线发行!' },
              ]}
            >
              <Radio.Group value="0">
                <Radio value="0">否</Radio>
                <Radio value="1">是</Radio>
              </Radio.Group>
            </Form.Item>
            <Form.Item
              label="是否赞助合约"
              name="sponsorFlag"
              rules={[{ required: true, message: '请选择是否赞助合约!' }]}
            >
              <Radio.Group value="0">
                <Radio value="0">否</Radio>
                <Radio value="1">是</Radio>
              </Radio.Group>
            </Form.Item>
            <Form.Item
              label="发行环境"
              name="chainEnv"
              rules={[{ required: true, message: '请选择发行环境!' }]}
            >
              <Radio.Group value="test">
                <Radio value="test">测试网</Radio>
                <Radio value="main">正式网</Radio>
              </Radio.Group>
            </Form.Item>
            <Form.Item
              label="发行区块链"
              name="chainType"
              rules={[{ required: true, message: '请选择发行区块链!' }]}
            >
              <Select style={{ width: '100%' }}>
                <Option value="conflux">树图</Option>
                <Option value="polygon">polygon</Option>
                <Option value="bsc">币安</Option>
                <Option value="tron">波场</Option>
                <Option value="wenchang">文昌链</Option>
              </Select>
            </Form.Item>
            <Form.Item label=" ">
              <Button
                type="primary"
                onClick={() => {
                  confirmIssue();
                }}
              >
                发行
              </Button>
            </Form.Item>
          </Form>
        </div>
      </Card>
    </>
  );
};
