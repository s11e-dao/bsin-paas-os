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
  // 复制模态框
  const [isCopyFormModal, setIsCopyFormModal] = useState(false)
  const [customerPromptTemplateList, setCustomerPromptTemplateList] = useState<any[]>(
    [],
  )
  const [sysPromptTemplateList, setSysPromptTemplateList] = useState<any[]>([])
  const [promptTemplateTypeList, setPromptTemplateTypeList] = useState<any[]>([])
  const [coverImage, setCoverImage] = useState('')
  const [promptTemplateList, setPromptTemplateList] = useState<any[]>([])
  // 存储编辑的ID
  const [promptTemplateNo, setPromptTemplateNo] = useState('')

  const [currentRecord, setCurrentRecord] = useState<any>({})
  const [currentTabKey, setCurrentTabKey] = useState<string>('1')

  // 获取新增表单信息
  const addFormRef: any = React.createRef()
  // 获取编辑表单信息
  const [editFormRef] = Form.useForm()

  // 获取编辑表单信息
  const [viewFormRef] = Form.useForm()
  // 获取复制表单信息
  const [copyFormRef] = Form.useForm()

  // 处理标签页切换
  const handleTabChange = (activeKey: string) => {
    setCurrentTabKey(activeKey)
    setLoading(true)
    // 查询协议
    let params = {
      current: '1',
      pageSize: '99',
      platformFlag: activeKey === '1' ? false : true, // 根据标签页传递platformFlag参数
    }
    getPromptTemplatePageList(params).then((res) => {
      let customerPromptTemplateListTmp: any[] = []
      let sysPromptTemplateListTmp: any[] = []
      
      if (res?.code == '000000' && res?.data) {
        setPromptTemplateList(res?.data)
        addPromptTemplateList(res?.data)
        res?.data.map((promptTemplate) => {
          if (promptTemplate.type == '0') {
            customerPromptTemplateListTmp.push(promptTemplate)
          } else if (promptTemplate.type == '1' || promptTemplate.type == '2' || promptTemplate.type == '3') {
            sysPromptTemplateListTmp.push(promptTemplate)
          }
        })
      } else {
        // 即使没有数据也要清空列表
        setPromptTemplateList([])
        addPromptTemplateList([])
      }
      
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
      setLoading(false)
    }).catch(() => {
      // 处理请求异常
      setCustomerPromptTemplateList([])
      setSysPromptTemplateList([])
      setPromptTemplateList([])
      addPromptTemplateList([])
      const promptTemplateTypeList = [
        {
          id: 1,
          name: '我的提示词模版',
          promptTemplateList: [],
        },
        {
          id: 2,
          name: '平台提示词模版',
          promptTemplateList: [],
        },
        {
          id: 3,
          name: 'C端应用提示词模版',
          promptTemplateList: [],
        },
        {
          id: 4,
          name: 'B端应用提示词模版',
          promptTemplateList: [],
        },
      ]
      setPromptTemplateTypeList(promptTemplateTypeList)
      setLoading(false)
    })
  }

  useEffect(() => {
    // 查询协议
    let params = {
      current: '1',
      pageSize: '99',
      platformFlag: false, // 默认查询我的提示词模版
    }
    getPromptTemplatePageList(params).then((res) => {
      let customerPromptTemplateListTmp: any[] = []
      let sysPromptTemplateListTmp: any[] = []
      
      if (res?.code == '000000' && res?.data) {
        setPromptTemplateList(res?.data)
        addPromptTemplateList(res?.data)
        res?.data.map((promptTemplate) => {
          if (promptTemplate.type == '0') {
            customerPromptTemplateListTmp.push(promptTemplate)
          } else if (promptTemplate.type == '1' || promptTemplate.type == '2' || promptTemplate.type == '3') {
            sysPromptTemplateListTmp.push(promptTemplate)
          }
        })
      } else {
        // 即使没有数据也要清空列表
        setPromptTemplateList([])
        addPromptTemplateList([])
      }
      
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
        }
      ]
      setPromptTemplateTypeList(promptTemplateTypeList)
      setLoading(false)
    }).catch(() => {
      // 处理请求异常
      setCustomerPromptTemplateList([])
      setSysPromptTemplateList([])
      setPromptTemplateList([])
      addPromptTemplateList([])
      const promptTemplateTypeList = [
        {
          id: 1,
          name: '我的提示词模版',
          promptTemplateList: [],
        },
        {
          id: 2,
          name: '平台提示词模版',
          promptTemplateList: [],
        },
      ]
      setPromptTemplateTypeList(promptTemplateTypeList)
      setLoading(false)
    })
  }, [promptTemplateNo])

  // 点击新增
  const confirmAdd = () => {
    addFormRef.current
      .validateFields()
      .then(async () => {
        var response = addFormRef.current?.getFieldsValue()
        // 确保type值正确
        if (currentTabKey === '1') {
          response.type = '0' // 我的提示词模版固定为0
        }
        let res = await addPromptTemplate({ ...response })
        if (res.code == '000000') {
          message.success('新增成功')
          // 重置表单
          addFormRef.current.resetFields()
          setIsAddFormModal(false)
          // 用于刷新列表
          setPromptTemplateNo(res.data?.serialNo)
        } else {
          message.error(res.message)
        }
      })
      .catch(() => {})
  }

  // 点击编辑
  const toEdit = async (record: any) => {
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
  const confirmEdit = (record: any) => {
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


  // 直接删除函数
  const handleDelete = async (record: any) => {
    if (record.editable == false) {
      message.warning('该配置不支持删除！！')
      return
    }
    let res = await delPromptTemplate(record)
    if (res.code == '000000') {
      message.success('删除成功')
      // 用于刷新
      setPromptTemplateNo(record.serialNo)
    } else {
      message.error(res.message)
    }
  }

  // 取消删除
  const cancelDelete = () => {
    message.info('已取消删除')
  }

  /**
   * 查看详情
   */
  const toDetail = async (record: any) => {
    addCurrentRecord(record)
    setIsCopyFormModal(true)
  }

  /**
   * 复制
   */
  const toCopy = async (record: any) => {
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
          {currentTabKey === '1' ? (
            <Input value="客户自定义" disabled />
          ) : (
            <Select placeholder="请选择提示词模版类型">
              <Option value="1">系统提示词</Option>
              <Option value="2">C端app应用提示词</Option>
              <Option value="3">B端app应用提示词</Option>
            </Select>
          )}
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
          // 根据当前标签页设置默认类型
          const defaultType = currentTabKey === '1' ? '0' : '1'
          // 重置表单并设置默认值
          addFormRef.current?.resetFields()
          addFormRef.current?.setFieldsValue({ 
            type: defaultType,
            status: '1',
            accessAuthority: '1',
            maxResults: 3,
            minScore: 0.7,
            quoteLimit: 1000,
            dimension: '512',
            segmentSizeInTokens: 100,
            overlapSizeInTokens: 0,
          })
          setIsAddFormModal(true)
        }}
        className={styles.btn}
      >
        新增
      </Button>
      <Descriptions title="提示词模版"></Descriptions>
      <Tabs defaultActiveKey="1" onChange={handleTabChange}>
        {promptTemplateTypeList.map((promptTemplate) => (
          <Tabs.TabPane tab={promptTemplate.name} key={promptTemplate.id}>
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
                dataSource={promptTemplate.promptTemplateList}
                renderItem={(item) => {
                  if (item && item.serialNo) {
                    return (
                      <List.Item key={item.serialNo}>
                        <Card
                          style={{ width: 300 }}
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

                            <Popconfirm
                              title="是否删除此条数据?"
                              onConfirm={() => handleDelete(item)}
                              onCancel={cancelDelete}
                              okText="是"
                              cancelText="否"
                            >
                              <DeleteOutlined
                                key="delete"
                                title="删除"
                              />
                            </Popconfirm>,
                          ]}
                        >
                          <Meta
                            avatar={
                              <Avatar
                                src={item.coverImage}
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
            type: '0',
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

    </Card>
  )
}
