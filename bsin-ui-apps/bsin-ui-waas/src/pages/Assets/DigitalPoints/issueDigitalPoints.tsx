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
import { issueDigitalPoints } from './service';
import { getContractProtocolList } from './../ContractProtocol/service';
import { getMetadataTemplatePageList } from './../MetadataTemplate/service';
import { getMetadataFileList } from './../MetadataList/service';
import styles from './index.css';

export default ({ setCurrentContent }) => {
  const [contractProtocolList, setContractProtocolList] = useState([]);
  const [contractProtocolChoosed, setContractProtocolChoosed] = useState({});
  const [protocolCode, setProtocolCode] = useState('');
  const [protocolStandards, setProtocolStandards] = useState('');
  const [protocolChange, setProtocolChange] = useState(false);

  useEffect(() => {
    // 查询合约模板协议
    let params = {
      current: '1',
      pageSize: '99',
    };
    getContractProtocolList(params).then((res) => {
      setContractProtocolList(res?.data);
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
        response.assetsCollectionType = contractProtocolChoosed.type;
        console.log(response);
        issueDigitalPoints(response).then((res) => {
          console.log('issue', res);
          if (res?.code == '000000') {
            // 返回列表
            setCurrentContent('digitalPoints');
            // 重置输入的表单
            FormRef.resetFields();
          } else {
            message.error(`发行失败： ${res?.message}`);
          }
        });
      })
      .catch(() => { });
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
    <>
      <Card style={{ marginBottom: 16 }}>
        <Button
          type="primary"
          onClick={() => {
            setCurrentContent('digitalPoints');
          }}
          className={styles.btn}
        >
          返回
        </Button>
        <Descriptions title="发行数字积分"></Descriptions>
        <div className={styles.addForm}>
          <Form
            name="basic"
            form={FormRef}
            labelCol={{ span: 5 }}
            wrapperCol={{ span: 7 }}
            // 表单默认值
            initialValues={{
              chainEnv: 'test',
              chainType: 'conflux',
              bondingCurveFlag: '0',
              sponsorFlag: '0',
              decimals: 18,
              initialSupply: '0',
              totalSupply: '21000000',
            }}
          >
            <Form.Item
              label="资产类型"
              name="protocolCode"
              rules={[{ required: true, message: '请选择协议类型!' }]}
            >
              <Select
                style={{ width: '100%' }}
                onChange={(value) => {
                  protocolCodeChange(value);
                }}
              >
                <Option value="1">请选择资产类型</Option>
                {contractProtocolList?.map((contractProtocol) => {
                  //筛选出ERC20的合约
                  if (contractProtocol?.protocolStandards == 'ERC20') {
                    return (
                      <Option value={contractProtocol?.protocolCode}>
                        {(contractProtocol?.serialNo).slice(-4) +
                          '-' +
                          contractProtocol?.protocolCode}
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
              label="积分名称"
              name="name"
              rules={[{ required: true, message: '请输入积分名称!' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              label="积分符号"
              name="symbol"
              rules={[{ required: true, message: '请输入积分符号!' }]}
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
              label="小数位数"
              name="decimals"
              rules={[{ required: true, message: '请输入小数位数!' }]}
            >
              <InputNumber min={0} max={18} />
            </Form.Item>

            <Tooltip
              placement="rightTop"
              title="发行时初始铸造量"
            // arrow={mergedArrow}
            >
              <Form.Item
                label="初始供应量"
                name="initialSupply"
                rules={[{ required: true, message: '请输入初始供应量!' }]}
              >
                <Input min={0} />
              </Form.Item>
            </Tooltip>

            <Form.Item
              label="资产描述"
              name="description"
              rules={[{ required: true, message: '请输入资产描述!' }]}
            >
              <Input />
            </Form.Item>

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

            <Form.Item wrapperCol={{ offset: 7, span: 12 }}>
              <Button type="primary" htmlType="submit"
                onClick={() => {
                  confirmIssue();
                }}>
                发行
              </Button>
            </Form.Item>
          </Form>
        </div>
      </Card>
    </>
  );
};
