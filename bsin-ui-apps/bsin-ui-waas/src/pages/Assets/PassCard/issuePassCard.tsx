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
import { collectPassCard } from './service';
import { getContractProtocolList } from './../ContractProtocol/service';
import { getMetadataTemplatePageList } from './../MetadataTemplate/service';
import { getMetadataFileList } from './../MetadataList/service';
import styles from './index.css';
import { issueDigitalAssetsCollection } from '../AssetsCollection/service';
import { getCustomerProfilePageList } from '../../Profile/service';

export default ({ setCurrentContent }) => {
  const [contractProtocolList, setContractProtocolList] = useState([]);
  const [customerProfileList, setCustomerProfileList] = useState([]);
  const [contractProtocolChoosed, setContractProtocolChoosed] = useState({});
  const [metadataTemplateList, setMetadataTemplateList] = useState([]);
  const [metadataFilePathList, setMetadataFilePath] = useState([]);
  const [protocolCode, setProtocolCode] = useState('');
  const [issueMethod, setIssueMethod] = useState('');
  const [protocolStandards, setProtocolStandards] = useState('');
  const [protocolChange, setProtocolChange] = useState(false);

  useEffect(() => {
    // 查询合约模板协议
    let params = {
      current: '1',
      pageSize: '99',
    };
    getCustomerProfilePageList(params).then((res) => {
      setCustomerProfileList(res?.data);
    });
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
        let requestParam = FormRef.getFieldsValue();

        if (issueMethod == '1') {
          console.log(requestParam);
          collectPassCard(requestParam).then((res) => {
            console.log('issue', res);
            if (res?.code == '000000') {
              // 返回列表
              setCurrentContent('passCard');
            } else {
              message.error(`发行失败： ${res?.message}`);
            }
          });
        } else {
          requestParam.assetsCollectionType = contractProtocolChoosed.type;
          console.log(requestParam);
          issueDigitalAssetsCollection(requestParam).then((res) => {
            console.log('issue', res);
            if (res?.code == '000000') {
              // 返回列表
              setCurrentContent('passCard');
            } else {
              message.error(`发行失败： ${res?.message}`);
            }
          });
        }
        // 重置输入的表单
        // FormRef.resetFields();
      })
      .catch(() => {});
  };

  const protocolCodeChange = (value) => {
    contractProtocolList.map((contractProtocol) => {
      if (contractProtocol?.protocolCode == value) {
        setContractProtocolChoosed(contractProtocol);
        console.log(contractProtocol);
        FormRef.setFieldValue('contractProtocolNo', contractProtocol.serialNo);
      }
    });
    setProtocolCode(value);
    setProtocolChange(true);
  };

  const customerProfileNoChange = (value) => {
    console.log('customerProfileNoChange', value);
    FormRef.setFieldValue('contractProtocolNo', value);
  };

  const issueMethodChange = (value) => {
    console.log('issueMethodChange:' + value);
    setIssueMethod(value);
  };

  return (
    <>
      <Card style={{ marginBottom: 16 }}>
        <Button
          type="primary"
          onClick={() => {
            setCurrentContent('passCard');
          }}
          className={styles.btn}
        >
          返回
        </Button>
        <Descriptions title="发行数字会员卡"></Descriptions>
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
              label="发行方式"
              name="issueMethod"
              rules={[{ required: true, message: '请选择发行方式!' }]}
            >
              <Select
                style={{ width: '100%' }}
                onChange={(value) => {
                  issueMethodChange(value);
                }}
              >
                <Option value="0">请选择发行方式</Option>
                <Option value="1"> 通过profile发行</Option>
                <Option value="2"> 通过数字资产发行</Option>
              </Select>
            </Form.Item>

            {issueMethod == '2' ? (
              <Form.Item
                label="会员卡协议"
                name="protocolCode"
                rules={[{ required: true, message: '请选择协议类型!' }]}
              >
                <Select
                  style={{ width: '100%' }}
                  onChange={(value) => {
                    protocolCodeChange(value);
                  }}
                >
                  <Option value="1">请选择会员卡类型</Option>
                  {contractProtocolList.map((contractProtocol) => {
                    //筛选出ERC721/1155的合约
                    console.log(contractProtocol?.protocolStandards);
                    // 合约类型: 1、数字徽章 2、PFP 3、数字积分 4、数字门票 5、pass卡 6、徽章/门票
                    if (
                      (contractProtocol?.protocolStandards == 'ERC721' ||
                        contractProtocol?.protocolStandards == 'ERC1155' ||
                        contractProtocol?.protocolStandards == 'ERC3525') &&
                      contractProtocol?.type == '5'
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
            ) : (
              <Form.Item
                label="profile列表"
                name="customerProfileNo"
                rules={[{ required: true, message: '请选择协议类型!' }]}
              >
                <Select
                  style={{ width: '100%' }}
                  onChange={(value) => {
                    customerProfileNoChange(value);
                  }}
                >
                  <Option value="1">请选择profile编号</Option>
                  {customerProfileList?.map((customerProfile) => {
                    return (
                      <Option value={customerProfile?.serialNo}>
                        {(customerProfile?.serialNo).slice(-4) +
                          '-' +
                          customerProfile?.name}
                      </Option>
                    );
                  })}
                </Select>
              </Form.Item>
            )}

            <Form.Item
              label="合约协议编号"
              name="contractProtocolNo"
              rules={[{ required: true, message: '请选输入协议编号!' }]}
            >
              <Input disabled />
            </Form.Item>
            <Form.Item
              label="会员卡名称"
              name="name"
              rules={[{ required: true, message: '请输入会员卡名称!' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              label="会员卡符号"
              name="symbol"
              rules={[{ required: true, message: '请输入会员卡符号!' }]}
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
              label="会员卡描述"
              name="description"
              rules={[{ required: true, message: '请输入会员卡描述!' }]}
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
                {metadataFilePathList?.map((metadataFilePath) => {
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
              label="基于联合曲线发行"
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

            <Form.Item wrapperCol={{ offset: 5, span: 12 }}>
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
