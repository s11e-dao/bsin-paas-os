import React, { useState, useEffect } from 'react';
import {
  Breadcrumb,
  Card,
  Button,
  Space,
  Table,
  Tag,
  Modal,
  Form,
  Input,
  Select,
  InputNumber,
  Upload,
  message,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import type { UploadProps } from 'antd';
import { InboxOutlined } from '@ant-design/icons';
import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '../../../utils/localStorageInfo';
import styles from './styles.less';

import { getMetadataFileList, makeDirectory, saveFile } from './service';
// 渲染图片和预览
import 'viewerjs/dist/viewer.css';
import ImageViewer from 'viewerjs';

const { Option } = Select;
const { TextArea } = Input;
const { Dragger } = Upload;

export default () => {
  let bsinFileUploadUrl = process.env.bsinFileUploadUrl;
  let tenantAppType = process.env.tenantAppType;

  interface DataType {
    serialNo: string;
    fileName: string;
    fileCode: string;
    fileType: string;
    fileUrl: string;
    ipfsUrl: string;
    tokenId: number;
    tenantId: string;
    fileDescription: string;
  }

  // 文件目录弹框
  const [dirModal, setDirModal] = useState(false);
  // 文件弹框
  const [fileModal, setFileModal] = useState(false);
  const [fileDirName, setFileDirName] = useState('');
  const [fileNo, setFileNo] = useState('');
  const [FormRef] = Form.useForm();

  // 图片的path
  const [metaDataImagePath, setMetaDataImagePath] = useState<string | null>('');

  const [dirDataList, setDirDataList] = useState<DataType[]>([]);

  useEffect(() => {
    let param = {};
    getMetadataFileList(param).then((res) => {
      setDirDataList(res?.data);
    });
  }, []);

  // 点击放大图片预览
  function initImageViwer(
    box = 'img-list', // 注意class不要忘记了
    option = {},
    callBack: (arg0: any[]) => any,
  ) {
    setTimeout(() => {
      const viewList: any[] = [];
      const el = document.querySelectorAll(`.${box}`);
      if (el.length) {
        el.forEach((z, x) => {
          viewList[x] = new ImageViewer(z, option);
        });
        callBack && callBack(viewList);
      }
    }, 1000);
  }

  //点击复制
  const copy = (e) => {
    console.log(e);
    const range = document.createRange();

    window.getSelection()?.removeAllRanges();

    //这个地方有时候会进行省略,做下判断如果是省略部分,就直接return,否则复制
    if (e.target.innerText.indexOf('...') != -1) {
      return;
    } else {
      range.selectNode(e.target);
    }
    window.getSelection()?.addRange(range);
    const sucful = document.execCommand('copy');
    if (sucful) {
      message.success('复制成功');
    }
  };

  const columns: ColumnsType<DataType> = [
    {
      title: '文件名称',
      dataIndex: 'fileName',
      key: 'fileName',
      width: 180,
      render: (text) => <a>{text}</a>,
    },
    {
      title: '文件类型',
      dataIndex: 'fileType',
      key: 'fileType',
      width: 120,
      render: (_, { fileType }) => {
        console.log(fileType);
        // 1 图片  2 gif 3 视频 4 音频 5 json 6文件夹
        let name = '图片';
        if (fileType === '1') {
          name = '图片';
        } else if (fileType === '2') {
          name = 'gif';
        } else if (fileType === '3') {
          name = '视频';
        } else if (fileType === '4') {
          name = '音频';
        } else if (fileType === '5') {
          name = 'json';
        } else if (fileType === '6') {
          name = '文件夹';
        }
        return <>{name}</>;
      },
    },
    {
      title: 'tokenId',
      dataIndex: 'tokenId',
      key: 'tokenId',
      width: 120,
    },
    {
      title: '预览',
      dataIndex: 'fileUrl',
      key: 'fileUrl', 
      // valueType: 'image',
      width: 120,
      ellipsis: true, //ellipsis设置成true ，那么表格就会变成fixed，不会被内容撑开，会一直按照自己定义的宽度
      render: (_, { fileUrl }) => {
        return (
          <div className="img-list" style={{ display: 'flex' }}>
            {/* {text.map((items, index) => { */}
            <div
              key={1}
              className="common-img-list"
              style={{
                width: '100px',
                height: '100px',
                marginLeft: '4px',
                overflow: 'hidden',
              }}
            >
              <img
                style={{ width: '100%' }}
                src={fileUrl}
                onClick={() => {
                  initImageViwer(); // 点击放大图片
                }}
              />
            </div>
            {/* })} */}
          </div>
        );
      },
    },
    {
      title: '文件地址',
      dataIndex: 'fileUrl',
      key: 'fileUrl',
      width: 300,
      ellipsis: true, //ellipsis设置成true ，那么表格就会变成fixed，不会被内容撑开，会一直按照自己定义的宽度
      // render: (text) => <a>{text}</a>,
      // onCellClick: copy,
    },
    {
      title: 'ipfs地址',
      dataIndex: 'ipfsUrl',
      key: 'ipfsUrl',
      width: 300,
      ellipsis: true, //ellipsis设置成true ，那么表格就会变成fixed，不会被内容撑开，会一直按照自己定义的宽度
      // render: (text) => <a>{text}</a>,
    },
    {
      title: '文件描述',
      dataIndex: 'fileDescription',
      key: 'fileDescription',
    },
  ];

  /**
   * 确认添加目录
   */
  const confirmAddDir = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        // 设置平台
        response.tenantAppType = tenantAppType;
        console.log(response);
        makeDirectory(response).then((res) => {
          console.log('add', res);
          let param = {};
          getMetadataFileList(param).then((res) => {
            if (res?.code == '000000') {
              setDirDataList(res?.data);
              // 重置输入的表单
              FormRef.resetFields();
              // 刷新proTable
              setDirModal(false);
            } else {
              message.error(`添加目录失败： ${res?.message}`);
            }
          });
        });
      })
      .catch(() => {});
  };

  /**
   * 取消添加目录
   */
  const onCancelAddDir = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setDirModal(false);
  };

  /**
   * 确认添加文件
   */
  const confirmAddFile = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        let reqParam = {
          ...response,
          parentNo: fileNo,
        };
        console.log('reqParam: ' + reqParam);
        // if (reqParam?.fileUrl == null) {
        //   message.error(`file upload failed.`);
        // } else {
        saveFile(reqParam).then((res) => {
          console.log('add', res);
          let param = {
            serialNo: fileNo,
          };
          getMetadataFileList(param).then((res) => {
            setDirDataList(res?.data);
          });
        });
        // }
        // 重置输入的表单
        FormRef.resetFields();
        // 刷新proTable
        setFileModal(false);
      })
      .catch(() => {});
  };

  /**
   * 取消添加文件
   */
  const onCancelAddFile = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setFileModal(false);
  };

  const clickCreateDir = () => {
    console.log('创建NFT集合目录');
    setDirModal(true);
  };

  const clickRoot = () => {
    console.log('点击了跟目录');
    setFileDirName('');
    // 查询文件夹
    let param = {};
    getMetadataFileList(param).then((res) => {
      setDirDataList(res?.data);
    });
  };

  const clickUploadImg = () => {
    console.log('上传NFT图片');
    setFileModal(true);
  };

  const clickFileDir = () => {
    let param = {
      serialNo: fileNo,
    };
    getMetadataFileList(param).then((res) => {
      setDirDataList(res?.data);
    });
  };

  // 上传图片
  const uploadProps: UploadProps = {
    name: 'file',
    headers: {
      Authorization: getSessionStorageInfo('token')?.token,
    },
    action: bsinFileUploadUrl,
    data: {
      currentPath: fileNo,
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
        // if (file?.response.data != '000000') {
        //   message.error(
        //     `${info.file.name} file uploaded failed. ${file?.response.data.message}`,
        //   );
        // } else {
        FormRef.setFieldValue('fileUrl', file?.response.data.url);
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

  /**
   * 文件类型详情，模板类型对应
   */
  const handleViewRecordOfFileType = () => {
    let { fileType } = isViewRecord;
    let typeText = fileType;
    return typeText;
  };

  return (
    <div>
      <Card>
        <Space direction="vertical" size="middle" style={{ display: 'flex' }}>
          <Space size={[8, 16]} wrap>
            <Button type="primary" onClick={clickCreateDir}>
              创建NFT集合目录
            </Button>
            {fileNo ? (
              <Button type="primary" onClick={clickUploadImg}>
                上传NFT图片
              </Button>
            ) : (
              <></>
            )}
          </Space>
          <Breadcrumb separator="">
            <Breadcrumb.Item>文件位置</Breadcrumb.Item>
            <Breadcrumb.Separator>:</Breadcrumb.Separator>
            <Breadcrumb.Item className={styles.cursor} onClick={clickRoot}>
              根目录
            </Breadcrumb.Item>
            <Breadcrumb.Separator />
            <Breadcrumb.Item className={styles.cursor} onClick={clickFileDir}>
              {fileDirName}
            </Breadcrumb.Item>
          </Breadcrumb>
          <Table
            onRow={(record) => {
              return {
                // 点击行
                onClick: (event) => {
                  console.log(event);
                  console.log(record);
                  if (record.fileType == '6') {
                    setFileDirName(record.fileName);
                    setFileNo(record.serialNo);

                    let param = {
                      serialNo: record.serialNo,
                    };
                    getMetadataFileList(param).then((res) => {
                      setDirDataList(res?.data);
                    });
                  }
                  // else if (record.ipfsUrl != '') {
                  //   const w = window.open(record.fileUrl);
                  //   w.location.href = record.ipfsUrl;
                  // } else if (record.fileUrl != '') {
                  //   const w = window.open(record.fileUrl);
                  //   w.location.href = record.fileUrl;
                  // }
                  else {
                    console.log('copy');
                    copy(event);
                  }
                },
              };
            }}
            columns={columns}
            dataSource={dirDataList}
          />
        </Space>
      </Card>
      {/* 新增模态框 */}
      <Modal
        title="新增"
        centered
        open={dirModal}
        onOk={confirmAddDir}
        onCancel={onCancelAddDir}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ type: 'ERC20' }}
        >
          <Form.Item
            label="文件夹名称"
            name="fileName"
            rules={[{ required: true, message: '请输入文件夹名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="文件夹描述"
            name="fileDescription"
            rules={[{ required: true, message: '请输入文件夹描述!' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
      {/* 上传图片 */}
      <Modal
        title="新增"
        centered
        open={fileModal}
        onOk={confirmAddFile}
        onCancel={onCancelAddFile}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ fileType: '1' }}
        >
          <Form.Item
            label="文件名称"
            name="fileName"
            rules={[{ required: true, message: '请输入文件名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="文件类型"
            name="fileType"
            rules={[{ required: true, message: '请选择文件类型!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="1">图片</Option>
              <Option value="2">gif</Option>
              <Option value="3">视频</Option>
              <Option value="4">音频</Option>
              <Option value="5">json</Option>
              <Option value="6">文件夹</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="文件描述"
            name="fileDescription"
            rules={[{ required: true, message: '请输入文件描述!' }]}
          >
            <TextArea />
          </Form.Item>
          <Form.Item
            label="tokenId"
            name="tokenId"
            rules={[{ required: true, message: '请输入tokenId!' }]}
          >
            <InputNumber min={1} />
          </Form.Item>
          <Form.Item
            label="ipfsUrl"
            name="ipfsUrl"
            rules={[{ required: true, message: '请输入文件描述!' }]}
          >
            <Input disabled />
          </Form.Item>
          <Form.Item label="上传图片" name="fileUrl">
            <Dragger {...uploadProps} listType="picture">
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击上传</p>
            </Dragger>
            {metaDataImagePath ? metaDataImagePath : null}
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};
