import columnsData, { AppColumnsItem } from './data'
import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'

import type { UploadProps } from 'antd/es/upload/interface'

import {
  getKnowledgeBaseList,
  delKnowledgeBase,
  addKnowledgeBase,
  editKnowledgeBase,
  getKnowledgeBaseDetail,
  getKnowledgeBasePageList,
} from './service'
import TableTitle from '@/components/TableTitle'
import {
  Button,
  Modal,
  Popconfirm,
  message,
  Form,
  Input,
  Divider,
  Card,
  Descriptions,
  Upload,
  List,
  Space,
  Select,
  Radio,
  Avatar,
  Switch,
} from 'antd'
const { Option } = Select
const { Meta } = Card

import {
  EditOutlined,
  EllipsisOutlined,
  DeleteOutlined,
  SettingOutlined,
  InboxOutlined,
  CopyOutlined,
  CloseCircleOutlined,
} from '@ant-design/icons'

import defaultFlag from '../../assets/defaultFlag.jpg'
import notDefaultFlag from '../../assets/notDefaultFlag.jpg'

import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '../../utils/localStorageInfo'
import { PlusOutlined } from '@ant-design/icons'
import styles from './index.css'

import './style.less'

// 样式设计参考：https://fastgpt.run/dataset/list

const { Dragger } = Upload

export default ({ addCurrentRecord, addKnowledgeBaseList, addChatUIProps }) => {
  let bsinFileUploadUrl = process.env.bsinFileUploadUrl
  let tenantAppType = process.env.tenantAppType

  const { TextArea } = Input

  const [loading, setLoading] = useState(true)

  // 新增模态框
  const [isAddFormModal, setIsAddFormModal] = useState(false)
  // 编辑模态框
  const [isEditFormModal, setIsEditFormModal] = useState(false)
  // 删除模态框
  const [isDeleteFormModal, setIsDeleteFormModal] = useState(false)
  // 复制模态框
  const [isCopyFormModal, setIsCopyFormModal] = useState(false)
  // 存储编辑的ID
  const [knowledgeBaseNo, setKnowledgeBaseNo] = useState('')
  // 获取新增表单信息
  const addFormRef: any = React.createRef()
  // 获取编辑表单信息
  const [editFormRef] = Form.useForm()
  // 获取复制表单信息
  const [copyFormRef] = Form.useForm()
  // 获取复制表单信息
  const [delFormRef] = Form.useForm()

  const [knowledgeBaseList, setKnowledgeBaseList] = useState([])

  const [currentRecord, setCurrentRecord] = useState({})
  const [coverImage, setCoverImage] = useState('')

  useEffect(() => {
    // 查询协议
    let params = {
      current: '1',
      pageSize: '99',
    }
    getKnowledgeBasePageList(params).then((res) => {
      if (res?.code == '000000') {
        setKnowledgeBaseList(res?.data)
        addKnowledgeBaseList(res?.data)
      }
      setLoading(false)
    })
  }, [knowledgeBaseNo])

  // 点击新增
  const confirmAdd = () => {
    addFormRef.current
      .validateFields()
      .then(async () => {
        var response = addFormRef.current?.getFieldsValue()
        if (coverImage != null) {
          response.coverImage = coverImage
        }
        console.log(coverImage)
        let res = await addKnowledgeBase({ ...response })
        if (res.code == '000000') {
          // 重置表单
          addFormRef.current.resetFields()
          setIsAddFormModal(false)
          setCoverImage('')
          setKnowledgeBaseNo(res.data?.serialNo)
        } else {
          message.error(res.message)
        }
      })
      .catch(() => { })
  }

  // 点击编辑
  const toEdit = (record) => {
    if (record.editable == false) {
      message.warning('该配置不支持编辑！！')
      return
    }
    // 存储id
    setKnowledgeBaseNo(record.serialNo)
    setCurrentRecord(record)
    setIsEditFormModal(true)
    // 数据回显
    editFormRef.setFieldsValue(record)
  }

  // 编辑确认
  const confirmEdit = async () => {
    editFormRef
      .validateFields()
      .then(async () => {
        var response = editFormRef.getFieldsValue()
        response.serialNo = knowledgeBaseNo
        // console.log(response)
        let res = await editKnowledgeBase(response)

        if (res.code == '000000') {
          message.success('编辑成功')
          // 重置表单
          editFormRef.resetFields()
          setIsEditFormModal(false)
          // 用于刷新
          setKnowledgeBaseNo('')
          setCoverImage('')
        } else {
          message.error(res.message)
        }
      })
      .catch(() => { })
  }

  // 点击删除
  const toDelete = (record) => {
    if (record.editable == false) {
      message.warning('该配置不支持删除！！')
      return
    }
    setIsDeleteFormModal(true)
    // 存储id
    setKnowledgeBaseNo(record.serialNo)
    // 数据回显
    delFormRef.setFieldsValue(record)
  }

  // 确认删除
  const confirmDel = async () => {
    delFormRef
      .validateFields()
      .then(async () => {
        var response = delFormRef.getFieldsValue()
        // response.serialNo = knowledgeBaseNo
        if (response.editable == false) {
          message.warning('该配置不支持删除！！')
          return
        }
        if (coverImage != null) {
          response.coverImage = coverImage
        }
        console.log(response)
        let res = await delKnowledgeBase(response)
        if ((res.data = '000000')) {
          // 重置表单
          delFormRef.resetFields()
          // 用于刷新
          setKnowledgeBaseNo('')
          setIsDeleteFormModal(false)
        } else {
          message.error(res.message)
        }
      })
      .catch(() => { })
  }

  // 取消删除
  const cancelDel = () => {
    message.warning('取消删除')
  }

  /**
   * 查看详情
   */
  const toDetail = async (record) => {
    addCurrentRecord(record)
  }

  /**
   * 复制
   */
  const toCopy = async (record) => {
    setCurrentRecord(record)
    copyFormRef.setFieldsValue(record)
    setIsCopyFormModal(true)
  }
  // 确认复制
  const confirmCopy = async () => {
    currentRecord.tenantId = null
    currentRecord.merchantNo = null
    currentRecord.customerNo = null
    console.log(currentRecord)
    let res = await addKnowledgeBase({ ...currentRecord })
    if (res.code == '000000') {
      message.success('复制成功')
      // 重置表单
      setIsCopyFormModal(false)
      copyFormRef.resetFields()
      setKnowledgeBaseNo(res.data.serialNo)
    } else {
      message.error(res.message)
    }
  }

  // 上传封面图片
  const uploadProps: UploadProps = {
    name: 'file',
    headers: {
      Authorization: getSessionStorageInfo('token')?.token,
    },
    action: bsinFileUploadUrl,
    data: {
      // currentPath: 'fileNo', //为空则使用CustomerNo作为文件夹
      tenantAppType: tenantAppType,
      thumbnailSize: '200,200', //缩略图尺寸，为空则无缩略图
      // imgSize: '200,200', //存储尺寸，为空则原图尺寸储存
    },
    // 只能上传一个
    maxCount: 1,
    onChange(info) {
      // 控制path是否显示
      console.log(info)
      // 是加载
      let { file } = info
      if (file?.status === 'done') {
        console.log('file.response')
        console.log(file.response)
        message.success(`${info.file.name} file uploaded successfully.`)
        // addFormRef.setFieldValue('coverImage', file?.response.data.url)
        console.log('file?.response.data.url')
        console.log(file?.response.data.url)
        setCoverImage(file?.response.data.url)
      } else if (file?.status === 'error') {
        message.error(`${info.file.name} file upload failed.`)
      }
    },
    // 拖拽
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files)
    },
    onRemove(e) { },
  }
  const formItemComponent = () => {
    return (
      <>
        <Form.Item label="知识库ID" name="serialNo">
          <Input disabled />
        </Form.Item>
        <Form.Item
          label="知识库名称"
          name="name"
          rules={[{ required: true, message: '请输入知识库名称!' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item label="封面图片" name="coverImage">
          <Input />
        </Form.Item>

        <Form.Item
          label="访问权限"
          name="accessAuthority"
          rules={[{ required: true, message: '请选择访问权限!' }]}
        >
          <Radio.Group value="0">
            <Radio value="1">private</Radio>
            <Radio value="2">public</Radio>
          </Radio.Group>
        </Form.Item>
        <Form.Item label="默认知识库" name="defaultFlag">
          <Switch />
        </Form.Item>
        <Form.Item label="描述" name="description">
          <TextArea
            placeholder="请输入相关描述信息"
            autoSize={{ minRows: 2, maxRows: 8 }}
          />
        </Form.Item>
      </>
    )
  }

  // 使用状态来追踪当前活动的Tab
  const [activeTab, setActiveTab] = useState(1);

  // 切换Tab的事件处理函数
  const handleTabClick = (tabNumber: any) => {
    setActiveTab(tabNumber);
  };

  return (
    <Card>
      <Button
        type="primary"
        onClick={() => {
          console.log('')
          setIsAddFormModal(true)
        }}
        className={styles.btn}
      >
        新增
      </Button>
      <div
        style={{
          backgroundColor: '#f1f1f1',
          width: '360px',
          marginBottom: '15px',
          height: '40px',
          lineHeight: '40px',
          borderRadius: '10px',
          display: 'flex',
          alignItems: 'center' /* 在交叉轴上居中 */,
          justifyContent: 'center' /* 在主轴上居中 */,
        }}
      >
        <p
          onClick={() => handleTabClick(1)}
          className={activeTab === 1 ? 'navItemA' : 'navItem'}
        >
          指令数据集
        </p>
        <p
          onClick={() => handleTabClick(2)}
          className={activeTab === 2 ? 'navItemA' : 'navItem'}
        >
          文档数据集
        </p>
        <p
          onClick={() => handleTabClick(3)}
          className={activeTab === 3 ? 'navItemA' : 'navItem'}
        >
          业务数据集
        </p>
      </div>
      <Space
        size="middle"
        direction={'vertical'}
        style={{ display: 'flex', marginTop: '30px', flexWrap: 'wrap' }}
      >
        <List
          loading={loading}
          rowKey="id"
          grid={{ gutter: 16, column: 4 }}
          dataSource={knowledgeBaseList}
          renderItem={(item) => {
            if (item && item.serialNo) {
              return (
                <List.Item key={item.serialNo}>
                  <Card
                    actions={[
                      <EditOutlined
                        key="edit"
                        title="编辑"
                        onClick={() => {
                          toEdit(item)
                        }}
                      />,
                      <EllipsisOutlined
                        key="ellipsis"
                        title="详情"
                        onClick={() => {
                          toDetail(item)
                        }}
                      />,
                      <CopyOutlined
                        key="copy"
                        title="复制"
                        onClick={() => {
                          toCopy(item)
                        }}
                      />,

                      <DeleteOutlined
                        key="delete"
                        title="删除"
                        onClick={() => {
                          toDelete(item)
                        }}
                      />,
                    ]}
                  >
                    <Meta
                      avatar={
                        <Avatar
                          src={item?.coverImage}
                        />
                      }
                      title={item.name}
                      description={item.description}
                    />
                  </Card>
                </List.Item>
              )
            }
            return (
              <List.Item>
                <Button type="dashed" className={styles.newButton}>
                  <PlusOutlined /> 创建一个知识库
                </Button>
              </List.Item>
            )
          }}
        />
      </Space>
      {/* 新增知识库 */}
      <Modal
        width={690}
        title="创建一个知识库"
        open={isAddFormModal}
        onOk={confirmAdd}
        onCancel={() => {
          setIsAddFormModal(false), addFormRef.current.resetFields()
        }}
        centered
      >
        <Form
          name="basic"
          ref={addFormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          initialValues={{ type: '1' }}
        >
          <Form.Item
            label="知识库类型"
            name="type"
            rules={[{ required: true, message: '请选择知识库类型!' }]}
          >
            <Select value="1">
              <Option value="1">通用知识库</Option>
              <Option value="2">web站点同步</Option>
              <Option value="3">公众号爬取</Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="知识库名字"
            name="name"
            rules={[{ required: true, message: '请输入知识库名字!' }]}
          >
            <Input placeholder="给知识库起个名称" />
          </Form.Item>

          <Form.Item label="封面图" name="coverImage">
            <Dragger {...uploadProps} listType="picture">
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击上传封面</p>
            </Dragger>
          </Form.Item>

          <Form.Item label="描述" name="description">
            <TextArea
              placeholder="请输入相关描述信息"
              autoSize={{ minRows: 2, maxRows: 8 }}
            />
          </Form.Item>
        </Form>
      </Modal>
      {/* 编辑模态框 */}
      <Modal
        title="编辑知识库"
        open={isEditFormModal}
        onOk={confirmEdit}
        onCancel={() => setIsEditFormModal(false)}
        centered
      >
        <Form
          name="basic"
          form={editFormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          initialValues={{}}
        >
          {formItemComponent}
        </Form>
      </Modal>
      {/* 删除 */}
      <Modal
        title="删除知识库"
        open={isDeleteFormModal}
        onOk={confirmDel}
        onCancel={() => setIsDeleteFormModal(false)}
        centered
      >
        <Form
          name="basic"
          form={delFormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          initialValues={{}}
        >
          {formItemComponent}
        </Form>
      </Modal>
      {/* 查看模态框 */}
      <Modal
        title="复制知识库"
        open={isCopyFormModal}
        onOk={confirmCopy}
        onCancel={() => setIsCopyFormModal(false)}
        centered
      >
        <Form
          name="basic"
          form={copyFormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          initialValues={{}}
        >
          {formItemComponent}
        </Form>
      </Modal>
    </Card>
  )
}
