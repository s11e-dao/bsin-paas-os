import columnsData, { AppColumnsItem } from './data'
import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
import defaultFlag from '../../assets/defaultFlag.jpg'
import notDefaultFlag from '../../assets/notDefaultFlag.jpg'
import {
  getPromptTemplatePageList,
  getPromptTemplateList,
  delPromptTemplate,
  addPromptTemplate,
  editPromptTemplate,
  getPromptTemplateDetail,
} from './service'
import {
  Button,
  Modal,
  Popconfirm,
  Space,
  message,
  Form,
  Input,
  Card,
  Descriptions,
  Upload,
  Divider,
  Switch,
  InputNumber,
  Radio,
  Select,
  Avatar,
  List,
  Tabs,
} from 'antd'
const { Meta } = Card
const { Option } = Select
import {
  EditOutlined,
  EllipsisOutlined,
  DeleteOutlined,
  SettingOutlined,
  CopyOutlined,
  InboxOutlined,
  CloseCircleOutlined,
  PlusOutlined,
} from '@ant-design/icons'
import type { UploadProps } from 'antd'
import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '../../utils/localStorageInfo'
import styles from './index.css'

export default ({ addCurrentRecord, addPromptTemplateList }) => {
  let bsinFileUploadUrl = process.env.bsinFileUploadUrl
  let tenantAppType = process.env.tenantAppType

  const { TextArea } = Input
  const [loading, setLoading] = useState(true)

  // 新增模态框
  const [isAddFormModal, setIsAddFormModal] = useState(false)
  // 编辑模态框
  const [isEditFormModal, setIsEditFormModal] = useState(false)
  // 查看模态框
  const [isViewFormModal, setIsViewFormModal] = useState(false)
  // 删除模态框
  const [isDeleteFormModal, setIsDeleteFormModal] = useState(false)
  // 复制模态框
  const [isCopyFormModal, setIsCopyFormModal] = useState(false)
  const [customerPromptTemplateList, setCustomerPromptTemplateList] = useState(
    [],
  )
  const [sysPromptTemplateList, setSysPromptTemplateList] = useState([])
  const [promptTemplateTypeList, setPromptTemplateTypeList] = useState([])
  const [coverImage, setCoverImage] = useState('')
  const [promptTemplateList, setPromptTemplateList] = useState([])
  // 存储编辑的ID
  const [promptTemplateNo, setPromptTemplateNo] = useState('')

  const [currentRecord, setCurrentRecord] = useState({})

  // 获取新增表单信息
  const addFormRef: any = React.createRef()
  // 获取编辑表单信息
  const [editFormRef] = Form.useForm()

  // 获取刪除表单信息
  const [delFormRef] = Form.useForm()

  // 获取编辑表单信息
  const [viewFormRef] = Form.useForm()
  // 获取复制表单信息
  const [copyFormRef] = Form.useForm()

  useEffect(() => {
    // 查询协议
    let params = {
      current: '1',
      pageSize: '99',
    }
    getPromptTemplatePageList(params).then((res) => {
      if (res?.code == '000000') {
        setPromptTemplateList(res?.data)
        addPromptTemplateList(res?.data)
        let customerPromptTemplateListTmp:
          | any[]
          | ((prevState: never[]) => never[]) = []
        let sysPromptTemplateListTmp:
          | any[]
          | ((prevState: never[]) => never[]) = []
        res?.data.map((promptTemplate) => {
          if (promptTemplate.type == '0') {
            customerPromptTemplateListTmp.push(promptTemplate)
          } else if (promptTemplate.type == '1') {
            sysPromptTemplateListTmp.push(promptTemplate)
          }
        })
        setCustomerPromptTemplateList(customerPromptTemplateListTmp)
        setSysPromptTemplateList(sysPromptTemplateListTmp)
        const promptTemplateTypeList = [
          {
            id: 1,
            name: '我的提示词模版',
            promptTemplateList: customerPromptTemplateListTmp,
          },
          {
            id: 2,
            name: '平台提示词模版',
            promptTemplateList: sysPromptTemplateListTmp,
          },
        ]
        setPromptTemplateTypeList(promptTemplateTypeList)
      }
      setLoading(false)
    })
  }, [promptTemplateNo])

  // 点击新增
  const confirmAdd = () => {
    addFormRef.current
      .validateFields()
      .then(async () => {
        var response = addFormRef.current?.getFieldsValue()
        let res = await addPromptTemplate({ ...response })
        if (res.code == '000000') {
          message.success('新增成功')
          // 重置表单
          addFormRef.current.resetFields()
          setIsAddFormModal(false)
        } else {
          message.error(res.message)
        }
      })
      .catch(() => {})
  }

  // 点击编辑
  const toEdit = async (record) => {
    if (record.editable == false) {
      message.warning('该配置不支持编辑！！')
      return
    }
    setPromptTemplateNo(record.serialNo)
    editFormRef.setFieldsValue(record)
    console.log(record)
    setIsEditFormModal(true)
  }

  // 编辑确认
  const confirmEdit = (record) => {
    editFormRef
      .validateFields()
      .then(async () => {
        var formInfo = editFormRef.getFieldsValue()
        let res = await editPromptTemplate(formInfo)
        if (res.code == '000000') {
          message.success('编辑成功')
          setCoverImage('')
          // 重置表单
          editFormRef.resetFields()
          setIsEditFormModal(false)
          // 用于刷新
          setPromptTemplateNo('')
        } else {
          message.error(res.message)
        }
      })
      .catch(() => {})
  }
  // 点击删除
  const toDelete = (record) => {
    if (record.editable == false) {
      message.warning('该配置不支持删除！！')
      return
    }
    setIsDeleteFormModal(true)
    setPromptTemplateNo(record.serialNo)
    // 数据回显
    delFormRef.setFieldsValue(record)
  }

  // 点击删除
  const confirmDel = async () => {
    delFormRef
      .validateFields()
      .then(async () => {
        var response = delFormRef.getFieldsValue()
        if (response.editable == false) {
          message.warning('该配置不支持删除！！')
          return
        }
        let res = await delPromptTemplate(response)
        if ((res.data = '000000')) {
          message.success('删除成功')
          // 重置表单
          delFormRef.resetFields()
          setIsDeleteFormModal(false)
          // 用于刷新
          setPromptTemplateNo('')
        } else {
          message.error(res.message)
        }
      })
      .catch(() => {})
  }

  /**
   * 查看详情
   */
  const toDetail = async (record) => {
    addCurrentRecord(record)
    setIsCopyFormModal(true)
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
    let res = await addPromptTemplate({ ...currentRecord })
    if (res.code == '000000') {
      message.success('复制成功')
      // 重置表单
      setIsCopyFormModal(false)
      copyFormRef.resetFields()
      setPromptTemplateNo(res.data.serialNo)
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
        console.log(file.response)
        message.success(`${info.file.name} file uploaded successfully.`)
        setCoverImage(file?.response.data.url)
      } else if (file?.status === 'error') {
        message.error(`${info.file.name} file upload failed.`)
      }
    },
    // 拖拽
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files)
    },
    onRemove(e) {},
  }

  const formItemComponent = () => {
    return (
      <>
        <Form.Item label="ID" name="serialNo">
          <Input disabled />
        </Form.Item>
        <Form.Item
          label="名称"
          name="name"
          rules={[{ required: true, message: '请输入提示词模版名称!' }]}
        >
          <Input placeholder="给模版起个名字吧" />
        </Form.Item>
        <Form.Item
          label="状态"
          name="status"
          rules={[{ required: true, message: '请选择提示词模版状态!' }]}
        >
          <Radio.Group value="1">
            <Radio value="0">禁用</Radio>
            <Radio value="1">启用</Radio>
          </Radio.Group>
        </Form.Item>
        <Form.Item
          label="访问权限"
          name="accessAuthority"
          rules={[{ required: true, message: '请选择提示词模版访问权限!' }]}
        >
          <Radio.Group value="1">
            <Radio value="1">private</Radio>
            <Radio value="2">public</Radio>
          </Radio.Group>
        </Form.Item>
        <Form.Item
          label="提示词模版类型"
          name="type"
          rules={[{ required: true, message: '请选择类型!' }]}
        >
          <Select value="0" disabled>
            <Option value="0">个人模版</Option>
            <Option value="1">系统模版</Option>
          </Select>
        </Form.Item>
        <Form.Item
          label="系统角色"
          name="systemRole"
          rules={[{ required: true, message: '请输入角色!' }]}
        >
          <Input placeholder="AI角色定义" />
        </Form.Item>
        <Form.Item
          label="系统提示词模版"
          name="systemPromptTemplate"
          rules={[{ required: true, message: '请输入系统提示词!' }]}
        >
          <TextArea placeholder="被放置在上下文数组的最前面,role为system,用于引导模型" />
        </Form.Item>
        <Form.Item label="限定词" name="determiner">
          <TextArea placeholder="与系统提示词类似位置会被放置在问题前,拥有更强的引导作用,可以为空" />
        </Form.Item>
        <Form.Item label="知识库引用提示词模版" name="knowledgeBase">
          <TextArea placeholder="用于引导模型参考召回的知识库内容,可以为空" />
        </Form.Item>
        <Form.Item label="历史聊天记录引用提示词模版" name="chatHistorySummary">
          <TextArea placeholder="用于引导模型参考的永久存储的历史聊天记录,可以为空" />
        </Form.Item>
        <Form.Item
          label="历史聊天记录总结提示词模版"
          name="summaryPromptTemplate"
        >
          <TextArea placeholder="用于引导模型对聊天记录进行总结存储,可以为空" />
        </Form.Item>
        <Form.Item label="聊天上下文引用提示词模版" name="chatBufferWindow">
          <TextArea placeholder="用于引导模型参考的最近K条聊天记录,可以为空" />
        </Form.Item>
        <Form.Item label="是否可编辑" name="editable">
          <Switch disabled />
        </Form.Item>
        <Form.Item label="封面图片" name="coverImage">
          <Input />
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
      <Descriptions title="提示词模版"></Descriptions>
      <Tabs defaultActiveKey="1">
        {promptTemplateTypeList.map((type) => (
          <Tabs.TabPane tab={type.name} key={type.id}>
            <Space
              size="middle"
              direction={'vertical'}
              style={{ display: 'flex', flexWrap: 'wrap' }}
            >
              <List
                loading={loading}
                rowKey="id"
                grid={{
                  gutter: 16,
                  xs: 1,
                  sm: 2,
                  md: 2,
                  lg: 3,
                  xl: 4,
                  xxl: 5,
                }}
                dataSource={type.promptTemplateList}
                renderItem={(item) => {
                  if (item && item.serialNo) {
                    return (
                      <List.Item key={item.serialNo}>
                        <Card
                          style={{ width: 300 }}
                          cover={
                            <img
                              style={{ width: '100%', height: '260px' }}
                              alt="example"
                              src={item.coverImage}
                            />
                          }
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
                                src={
                                  item.defaultFlag == true
                                    ? defaultFlag
                                    : notDefaultFlag
                                }
                              />
                            }
                            title={item.name}
                            description={item.description}
                          />
                        </Card>
                      </List.Item>
                    )
                  }
                }}
              />
            </Space>
          </Tabs.TabPane>
        ))}
      </Tabs>

      {/* 新增模态框 */}
      <Modal
        title="新增提示词模版"
        open={isAddFormModal}
        width={700}
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
          wrapperCol={{ span: 20 }}
          initialValues={{
            type: '3',
            status: '1',
            accessAuthority: '1',
            maxResults: 3,
            minScore: 0.7,
            quoteLimit: 1000,
            dimension: '512',
            segmentSizeInTokens: 100,
            overlapSizeInTokens: 0,
          }}
        >
          {formItemComponent}
        </Form>
      </Modal>
      {/* 编辑模态框 */}
      <Modal
        title="编辑模版"
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
          initialValues={{ remember: true }}
        >
          {formItemComponent}
        </Form>
      </Modal>

      {/* 查看模态框 */}
      <Modal
        title="查看模版"
        open={isViewFormModal}
        onOk={() => setIsViewFormModal(false)}
        onCancel={() => setIsViewFormModal(false)}
        centered
      >
        <Form
          name="basic"
          form={viewFormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          initialValues={{ remember: true }}
        >
          {formItemComponent}
        </Form>
      </Modal>

      {/* 查看模态框 */}
      <Modal
        title="复制模板"
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

      {/* 删除模板模态框 */}
      <Modal
        title="删除模板"
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
    </Card>
  )
}
