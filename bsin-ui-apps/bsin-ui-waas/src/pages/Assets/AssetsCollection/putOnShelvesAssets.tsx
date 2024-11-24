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
  Image,
  Upload,
} from 'antd';
import type { UploadProps } from 'antd/es/upload/interface';
// 上传组件
const { Dragger } = Upload;

import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined, InboxOutlined } from '@ant-design/icons';
import { getSessionStorageInfo } from '../../../utils/localStorageInfo';
import columnsData, { columnsDataType } from './data';
import {
  putOnShelvesDigitalAssetsCollection,
  getDigitalAssetsMetadataImageInfo,
} from './service';
import { getContractProtocolDetail } from '../ContractProtocol/service';
import styles from './index.css';

export default ({ setCurrentContent, assetsCollectionRecord }) => {
  let bsinFileUploadUrl = process.env.bsinFileUploadUrl;
  let tenantAppType = process.env.tenantAppType;

  const [contractProtocol, setContractProtocol] = useState({});

  const [obtainMethod, setObtainMethod] = useState('');

  const [metadataFile, setMetadataFile] = useState([]);

  // 图片的path
  const [coverImage, setCoverImage] = useState<string | null>('');

  useEffect(() => {
    // 查询协议
    let params = {
      current: '1',
      pageSize: '99',
    };

    // 根据上架的集合的协议编号查询对应的协议
    let contractProtocolParams = {
      serialNo: assetsCollectionRecord.contractProtocolNo,
    };
    getContractProtocolDetail(contractProtocolParams).then((res) => {
      setContractProtocol(res?.data);
    });
  }, []);

  const { Option } = Select;
  // 获取表单
  const [FormRef] = Form.useForm();

  /**
   * 确认上架
   */
  const confirmPutOnShelves = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let formInfo = FormRef.getFieldsValue();
        console.log(formInfo);

        // if (!formInfo.templateContent) {
        //   formInfo.templateContent = JSON.stringify(metaDatajson, null, 2);
        // }
        formInfo.coverImage = coverImage;
        putOnShelvesDigitalAssetsCollection(formInfo).then((res) => {
          console.log('issue', res);
          if (res?.code == 0) {
            // 返回列表
            setCurrentContent('assetsCollection');
            // 重置输入的表单
            FormRef.resetFields();
          } else {
            message.error(`上架失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
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

  const obtainMethodChange = (e: Event) => {
    console.log(e);
    // 根据点击选择展示不同的输入框
    setObtainMethod(e.target.value);
  };

  const tokenIdChange = (value) => {
    console.log(value);
    // 根据tokenId查询对应的图片地址
    let params = {
      serialNo: assetsCollectionRecord.serialNo,
      tokenId: value,
      // 不查询的文件类型： 1 图片  2 gif 3 视频 4 音频 5 json 6 文件夹
      fileType: '5',
    };
    getDigitalAssetsMetadataImageInfo(params).then((res) => {
      console.log(res);
      if (res.code == 0) {
        setMetadataFile(res.data);
      } else {
        setMetadataFile([]);
      }
    });
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
        <Descriptions title="上架数字资产"></Descriptions>
        <div className={styles.addForm}>
          <Form
            name="basic"
            form={FormRef}
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 11 }}
            // 表单默认值
            initialValues={{
              totalSupply: '',
              obtainMethod: '1',
              multimediaType: '1',
              digitalAssetsCollectionNo: assetsCollectionRecord.serialNo,
              name: assetsCollectionRecord.name,
              assetsType: assetsCollectionRecord.collectionType,
              inventory: assetsCollectionRecord.inventory,
              // coverImage: metadataFile?.coverImage
            }}
          >
            <Form.Item label="资产集合ID" name="digitalAssetsCollectionNo">
              <Input defaultValue={assetsCollectionRecord.serialNo} disabled />
            </Form.Item>

            <Form.Item label="资产集合名称" name="name">
              <Input defaultValue={assetsCollectionRecord.name} disabled />
            </Form.Item>

            <Form.Item label="资产类型" name="assetsType">
              <InputNumber
                defaultValue={assetsCollectionRecord.collectionType}
                disabled
              />
            </Form.Item>

            <Form.Item label="资产类型标准" name="assetsProtocolStandards">
              <Input
                defaultValue={assetsCollectionRecord.protocolStandards}
                disabled
              />
            </Form.Item>

            <Form.Item label="可上架数量" name="inventory">
              <Input defaultValue={assetsCollectionRecord.inventory} disabled />
            </Form.Item>

            {contractProtocol.protocolStandards == 'ERC1155' ? (
              <Form.Item
                label="上架tokenId"
                name="tokenId"
                rules={[{ required: true, message: '请输入tokenId!' }]}
              >
                <InputNumber
                  min={1}
                  onChange={(e) => {
                    tokenIdChange(e);
                  }}
                />
              </Form.Item>
            ) : null}
            <Form.Item
              label="上架数量"
              name="putOnQuantity"
              rules={[{ required: true, message: '请输入上架数量!' }]}
            >
              <InputNumber min={1} />
            </Form.Item>
            {assetsCollectionRecord.collectionType == '5' ? (
              <Form.Item
                label="获取方式"
                name="obtainMethod"
                rules={[{ required: true, message: '请选择获取方式!' }]}
              >
                <Radio.Group
                  value="0"
                  onChange={(e) => {
                    obtainMethodChange(e);
                  }}
                >
                  <Radio value="2">购买</Radio>
                </Radio.Group>
              </Form.Item>
            ) : (
              <Form.Item
                label="获取方式"
                name="obtainMethod"
                rules={[{ required: true, message: '请选择获取方式!' }]}
              >
                <Radio.Group
                  value="0"
                  onChange={(e) => {
                    obtainMethodChange(e);
                  }}
                >
                  <Radio value="1">免费领取/空投</Radio>
                  <Radio value="2">购买</Radio>
                  <Radio value="3">固定口令领取</Radio>
                  <Radio value="4">随机口令</Radio>
                  <Radio value="5">盲盒</Radio>
                  <Radio value="6">活动</Radio>
                </Radio.Group>
              </Form.Item>
            )}

            {obtainMethod == '3' ? (
              <Form.Item
                label="输入口令"
                name="password"
                rules={[{ required: true, message: '请输入输入口令!' }]}
              >
                <Input />
              </Form.Item>
            ) : null}

            {obtainMethod == '2' ? (
              <Form.Item
                label="输入价格"
                name="price"
                rules={[{ required: true, message: '请输入价格!' }]}
              >
                <InputNumber />
              </Form.Item>
            ) : null}

            <Form.Item
              label="封面多媒体类型"
              name="multimediaType"
              rules={[{ required: true, message: '请选择封面多媒体类型!' }]}
            >
              <Select style={{ width: '100%' }}>
                <Option value="1">图片</Option>
                <Option value="2">gif</Option>
                <Option value="3">视频</Option>
              </Select>
            </Form.Item>

            {contractProtocol.protocolStandards == 'ERC721' ? (
              <Form.Item label="资产及封面图片" name="coverImage">
                <Image width={200} src={metadataFile?.fileUrl} />
                <Input defaultValue={metadataFile?.fileUrl} disabled />
              </Form.Item>
            ) : null}

            {contractProtocol.protocolStandards == 'ERC721' ? (
              <Form.Item label="上传封面图片" name="coverImage">
                <Dragger {...uploadProps} listType="picture">
                  <p className="ant-upload-drag-icon">
                    <InboxOutlined />
                  </p>
                  <p className="ant-upload-text">点击上传</p>
                </Dragger>
                {/* {coverImage ? ipfsGatewayUrl + coverImage : null} */}
              </Form.Item>
            ) : null}

            <Form.Item
              label="资产名称"
              name="assetsName"
              rules={[{ required: true, message: '请输入资产名称!' }]}
            >
              <Input />
            </Form.Item>

            <Form.Item
              label="资产描述"
              name="description"
              rules={[{ required: true, message: '请输资产描述!' }]}
            >
              <Input />
            </Form.Item>

            <Form.Item label=" ">
              <Button
                type="primary"
                onClick={() => {
                  confirmPutOnShelves();
                }}
              >
                上架
              </Button>
            </Form.Item>
          </Form>
        </div>
      </Card>
    </>
  );
};
