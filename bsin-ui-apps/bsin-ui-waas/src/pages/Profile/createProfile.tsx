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
  Upload,
} from 'antd';
import type { UploadProps } from 'antd';
import { InboxOutlined } from '@ant-design/icons';
import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '../../utils/localStorageInfo';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import { createProfile } from './service';
import { getMetadataFileList } from '../Assets/MetadataList/service';
import styles from './index.css';

const { Option } = Select;
const { TextArea } = Input;
const { Dragger } = Upload;
export default ({ setCurrentContent }) => {
  let bsinFileUploadUrl = process.env.bsinFileUploadUrl;
  let tenantAppType = process.env.tenantAppType;

  const [metadataFilePathList, setMetadataFilePath] = useState([]);
  // 图片的path
  const [metaDataImagePath, setMetaDataImagePath] = useState<string | null>('');

  useEffect(() => {
    // 查询合约模板协议
    let params = {
      current: '1',
      pageSize: '99',
    };
    getMetadataFileList(params).then((res) => {
      setMetadataFilePath(res?.data);
    });
  }, []);

  const { Option } = Select;
  // 获取表单
  const [FormRef] = Form.useForm();

  /**
   * 确认创建
   */
  const confirmCreate = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        createProfile(response)
          .then(async (res) => {
            console.log(res);
            if (res?.code == 0) {
              // 返回列表
              setCurrentContent('profile');
              // 重置输入的表单
              FormRef.resetFields();
            } else {
              console.log(res?.message);
              message.error(`创建失败： ${res?.message}`);
            }
          })
          .catch(() => {});
      })
      .catch(() => {});
  };

  const typeChange = (value) => {
    console.log(value);
  };

  // 上传图片
  const uploadProps: UploadProps = {
    name: 'file',
    headers: {
      Authorization: getSessionStorageInfo('token')?.token,
    },
    action: bsinFileUploadUrl,
    data: {
      // currentPath: fileNo,
      tenantAppType: tenantAppType,
      storeMethod: '3', // ipfs+OSS
    },
    // 只能上传一个
    maxCount: 1,
    onChange(info) {
      // 控制path是否显示
      console.log('info:', info);
      // 是加载
      let { file } = info;
      if (file?.status === 'done') {
        console.log(file.response);
        message.success(`${info.file.name} file uploaded successfully.`);
        // if (file?.response.data != 0) {
        //   message.error(
        //     `${info.file.name} file uploaded failed. ${file?.response.data.message}`,
        //   );
        // } else {
        FormRef.setFieldValue('fileUrl', file?.response.data.fileUrl);
        FormRef.setFieldValue('ipfsUrl', file?.response.data.ipfsUrl);
        // }
      } else if (file?.status === 'error') {
        message.error(`${info.file.name} file upload failed.`);
      }
    },
    // 拖拽
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files);
    },
    onRemove(e) {
      setMetaDataImagePath('');
    },
  };

  return (
    <>
      <Card style={{ marginBottom: 16 }}>
        <Button
          type="primary"
          onClick={() => {
            setCurrentContent('profile');
          }}
          className={styles.btn}
        >
          返回
        </Button>
        <Descriptions title="创建Profile"></Descriptions>
        <div className={styles.addForm}>
          <Form
            name="basic"
            form={FormRef}
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 7 }}
            // 表单默认值
            initialValues={{
              chainEnv: 'test',
              chainType: 'conflux',
              type: 'Brand',
              externalUri: '0',
            }}
          >
            <Form.Item
              label="profile类型"
              name="type"
              rules={[{ required: true, message: '请选择profile类型!' }]}
            >
              <Select
                style={{ width: '100%' }}
                onChange={(value) => {
                  typeChange(value);
                }}
              >
                <Option value="0">请选择Profile类型</Option>
                <Option value="Brand">Brand</Option>
                <Option value="Indicidual">Indicidual</Option>
              </Select>
            </Form.Item>

            <Form.Item
              label="profile external URI"
              name="externalUri"
              rules={[
                { required: true, message: '请选择profile external URI!' },
              ]}
            >
              <Select style={{ width: '100%' }}>
                <Option value="0">请选择Profile external URI</Option>
                {metadataFilePathList?.map((metadataFilePath) => {
                  return (
                    <Option value={metadataFilePath?.serialNo}>
                      {metadataFilePath?.fileName}
                    </Option>
                  );
                })}
              </Select>
            </Form.Item>

            <Form.Item label="上传logo" name="fileUrl">
              <Dragger {...uploadProps} listType="picture">
                <p className="ant-upload-drag-icon">
                  <InboxOutlined />
                </p>
                <p className="ant-upload-text">点击上传</p>
              </Dragger>
              {metaDataImagePath ? metaDataImagePath : null}
            </Form.Item>

            <Form.Item
              label="Profile名称"
              name="name"
              rules={[{ required: true, message: '请输入Profile名称!' }]}
            >
              <Input />
            </Form.Item>

            <Form.Item
              label="Profile符号"
              name="symbol"
              rules={[{ required: true, message: '请输入Profile符号!' }]}
            >
              <Input />
            </Form.Item>

            <Form.Item
              label="Profile描述"
              name="description"
              rules={[{ required: true, message: '请输入Profile描述!' }]}
            >
              <Input />
            </Form.Item>

            <Form.Item
              label="链环境"
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
                onClick={async () => {
                  await confirmCreate();
                }}
              >
                创建
              </Button>
            </Form.Item>
          </Form>
        </div>
      </Card>
    </>
  );
};
