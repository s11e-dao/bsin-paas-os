import React, { useState, useEffect } from 'react'
import {
  List,
  Modal,
  Form,
  Input,
  message,
  Button,
  Radio,
  Divider,
  Popconfirm,
  Select,
  Upload,
  Card,
} from 'antd'
import type { UploadProps } from 'antd/es/upload/interface'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
import TableTitle from '@/components/TableTitle'
import KnowledgeBaseFileColumnsData, {
  KnowledgeBaseFileColumnsItem,
} from './knowledgeBaseFileData'
import { PlusOutlined, InboxOutlined } from '@ant-design/icons'
const { Option } = Select
const { TextArea } = Input
// 上传组件
const { Dragger } = Upload
import Retrival from './retrival'


import styles from '../style.less'
import {
  getKnowledgeBaseFileList,
  delKnowledgeBaseFile,
  addKnowledgeBaseFile,
  editKnowledgeBaseFile,
  knowledgeBaseRetrieval,
} from './service'
import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '../../../utils/localStorageInfo'

const KnowledgeBaseFile: React.FC = ({ routeChange, chatUIProps }) => {
  
  let bsinFileUploadUrl = process.env.bsinFileUploadUrl
  let tenantAppType = process.env.tenantAppType
  let storeMethod = process.env.storeMethod

  // 新增模态框
  const [isAddFormModal, setIsAddFormModal] = useState(false)
  // 编辑模态框
  const [isEditFormModal, setIsEditFormModal] = useState(false)
  // 召回模态框
  const [isRetrivalFormModal, setIsRetrivalFormModal] = useState(false)
  //
  const [selectType, setSelectType] = useState('1')
  //
  const [fileObject, setFileObject] = useState({})

  // 获取新增表单信息
  const [addFormRef] = Form.useForm()

  // 获取编辑表单信息
  const [editFormRef] = Form.useForm()

  const [knowledgeBaseFileList, setKnowledgeBaseFileList] = useState([{}])

  const [knowledgeBase, setKnowledgeBase] = useState('')

  // 文件
  const [uploadFileUri, setUploadFileUri] = useState<string | null>('')
  const [uploadFileName, setUploadFileName] = useState<string | null>('')

  // 上传文件
  const uploadProps: UploadProps = {
    name: 'file',
    headers: {
      Authorization: getSessionStorageInfo('token')?.token,
    },
    action: bsinFileUploadUrl,
    data: {
      // currentPath: 'fileNo', //为空则使用CustomerNo作为文件夹
      tenantAppType: tenantAppType,
      storeMethod: storeMethod, // 本地和aliOSS
    },
    // 只能上传一个
    maxCount: 1,
    onChange(info) {
      // 控制path是否显示
      console.log('info:\n')
      console.log(info)
      // 是加载
      let { file } = info
      if (file?.status === 'done') {
        console.log('file.response:\n')
        console.log(file.response)
        message.success(`${info.file.name} file uploaded successfully.`)
        // addFormRef.setFieldValue('name', file?.name)
        addFormRef.setFieldValue('fileUri', file?.response.data.url)
        addFormRef.setFieldValue('localPath', file?.response.data.localPath)
        // setUploadFileUri(file?.response.data.url)
        // setUploadFileName(file?.name)
        addFormRef.setFieldValue('content', file.originFileObj)
      } else if (file?.status === 'error') {
        message.error(`${info.file.name} file upload failed.`)
      }
    },
    beforeUpload(file) {
      return new Promise((resolve) => {
        const name = file.name
        const isLt2M = file.size / 1024 / 1024 < 10
        const fileType = name.slice(name.lastIndexOf('.') + 1)
        if (
          !isLt2M ||
          !['pdf', 'ppt', 'pptx', 'md', 'txt', 'doc', 'docx'].includes(
            fileType?.toLowerCase(),
          )
        ) {
          message.warning(
            '文件大小不能超过10MB，且只允许上传后缀名为ppt，pdf，md，doc的文件!',
          )
          return false
        }
        const reader = new FileReader()
        reader.readAsText(file)
        console.log(file)
        // setFileObject(reader)

        reader.onload = () => {
          console.log(reader.result)
          // let JsonFile = JSON.parse(reader.result)
          // console.log(JsonFile)
          addFormRef.setFieldValue('name', file?.name)
          addFormRef.setFieldValue('fileType', fileType)
          addFormRef.setFieldValue('content', reader.result)
          resolve(file as any)
        }
      })
    },
    // 拖拽
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files)
    },
    onRemove(e) {},
  }

  useEffect(() => {
    console.log(chatUIProps)
    setKnowledgeBase(chatUIProps.aiNo)
  })

  // Table action 的引用，便于自定义触发 用于更改数据之后的表单刷新
  const actionRef = React.useRef<ActionType>()
  // 表头赋值
  const columns: ProColumns<
    KnowledgeBaseFileColumnsItem
  >[] = KnowledgeBaseFileColumnsData
  // 操作列渲染
  const optionRender = (text: any, record: any, index: number) => [
    <div key={record.serialNo}>
      <a onClick={() => toEdit(record)}>编辑</a>
      <Divider type="vertical" />
      <a onClick={() => toDetail(record)}>详情</a>
      <Divider type="vertical" />
      <Popconfirm
        title="是否删除此条数据?"
        onConfirm={() => confirmDel(record.serialNo)}
        onCancel={cancelDel}
        okText="是"
        cancelText="否"
      >
        <a>删除</a>
      </Popconfirm>
      <Divider type="vertical" />
      {/* <a onClick={() => toRetrival(record)}>召回测试</a> */}
    </div>,
  ]
  // 自定义表格头部渲染
  columns.forEach((item: any) => {
    item.dataIndex === 'option' ? (item.render = optionRender) : undefined
  })

  // 点击新增
  const confirmAdd = () => {
    addFormRef
      .validateFields()
      .then(async () => {
        var response = addFormRef?.getFieldsValue()
        // 11只用于前端区分，本质上还是 url
        if (selectType == '11') {
          response.type = '1'
        }
        console.log(response)
        let res = await addKnowledgeBaseFile({ ...response })
        if (res.code == '000000') {
          message.success('新增成功')
          // 重置表单
          addFormRef.resetFields()
          setIsAddFormModal(false)
          actionRef.current?.reload()
        } else {
          message.error(res.message)
        }
      })
      .catch(() => {})
  }

  // 点击编辑
  const toEdit = (record) => {
    // message.warning('不支持编辑！！')
    // return
    if (record.editable == false) {
      message.warning('该配置不支持编辑！！')
      return
    }
    // 数据回显
    editFormRef.setFieldsValue(record)
    setIsEditFormModal(true)
  }

  // 编辑确认
  const confirmEdit = () => {
    editFormRef
      .validateFields()
      .then(async () => {
        var response = editFormRef.getFieldsValue()
        let res = await editKnowledgeBaseFile(response)
        if (res.code == '000000') {
          message.success('编辑成功')
          // 重置表单
          editFormRef.resetFields()
          setIsEditFormModal(false)
          actionRef.current?.reload()
        } else {
          message.error(res.message)
        }
      })
      .catch(() => {})
  }

  // 点击详情
  const toDetail = (record) => {
    routeChange(record, 'knowledgeBaseFileDetail')
  }

  // 详情确认
  const confirmDetail = () => {
    editFormRef
      .validateFields()
      .then(async () => {
        var response = editFormRef.getFieldsValue()
        let res = await editKnowledgeBaseFile(response)
        if (res.code == '000000') {
          message.success('编辑成功')
          // 重置表单
          editFormRef.resetFields()
          setIsEditFormModal(false)
          actionRef.current?.reload()
        } else {
          message.error(res.message)
        }
      })
      .catch(() => {})
  }

  // 点击删除
  const confirmDel = async (serialNo: string) => {
    let res = await delKnowledgeBaseFile({ serialNo })
    if ((res.data = '000000')) {
      message.success('删除成功')
      actionRef.current?.reload()
    } else {
      message.error(res.message)
    }
  }

  // 取消删除
  const cancelDel = () => {
    message.warning('取消删除')
  }

  // 点击召回测试
  const toRetrival = (record) => {
    setIsRetrivalFormModal(true)
  }
  const confirmRetrival = () => {
    setIsRetrivalFormModal(false)
  }
  const cancelRetrival = () => {
    setIsRetrivalFormModal(false)
  }

  const typeChange = (value) => {
    console.log(value)
    setSelectType(value)
  }

  return (
    <>
      {/* 表格 */}
      <ProTable<KnowledgeBaseFileColumnsItem>
        actionRef={actionRef}
        scroll={{ x: 900 }}
        bordered
        headerTitle={<TableTitle title="知识库文件" />}
        columns={columns}
        // 请求数据
        request={async (params) => {
          params.knowledgeBaseNo = knowledgeBase
          console.log(params)
          let res = await getKnowledgeBaseFileList({
            ...params,
          })
          console.log(res)
          setKnowledgeBaseFileList(res.data)
          const result = {
            data: res.data,
            total: res.pagination?.totalSize,
          }
          return result
        }}
        toolBarRender={() => [
          <Button
            key="button"
            icon={<PlusOutlined />}
            type="primary"
            onClick={() => setIsAddFormModal(true)}
          >
            新增
          </Button>,
        ]}
        // 本地储存表格列的显示参数
        columnsState={{
          persistenceKey: 'Apps',
          persistenceType: 'localStorage',
        }}
        // 每行表格的key
        rowKey="serialNo"
        // 搜索表单布局配置
        search={{
          labelWidth: 'auto',
        }}
        // 分页相关配置
        pagination={{
          // 初始页面数据条数
          pageSize: 10,
        }}
        dateFormatter="string"
      />

      {/* 新增模态框 */}
      <Modal
        title="新增知识库文件"
        open={isAddFormModal}
        onOk={confirmAdd}
        onCancel={() => {
          setIsAddFormModal(false), addFormRef.resetFields()
        }}
        centered
      >
        <Form
          name="basic"
          form={addFormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{
            type: '1',
            knowledgeBaseNo: knowledgeBase,
          }}
        >
          <Form.Item label="绑定知识库ID" name="knowledgeBaseNo">
            <Input disabled />
          </Form.Item>
          {/* 类型：1、url 2、文件(FileSystemDocumentLoader) 3、文件夹 4、公众号 */}
          <Form.Item
            label="方式"
            name="type"
            rules={[{ required: true, message: '请选择添加方式!' }]}
          >
            {/* // 类型：1、url-不需要上传文件，直接提供url 2、文件(FileSystemDocumentLoader)-前端先上传至OSS，url的形式提交至后台 3、文件夹 4、公众号 */}
            <Select
              value="1"
              onChange={(value) => {
                typeChange(value)
              }}
            >
              <Option value="1">url</Option>
              <Option value="2">文件</Option>
              {/* <Option value="11">文件</Option> */}
              <Option value="4">公众号</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="名称"
            name="name"
            rules={[{ required: true, message: '请输入名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="文件类型"
            name="fileType"
            rules={[{ required: true, message: '请输入文件类型!' }]}
          >
            {/* // 文件类型： pdf|md|doc|... */}
            <Input disabled placeholder="根据上传文件自动填充" />
          </Form.Item>
          {selectType == '2' || selectType == '11' ? (
            <Form.Item label="知识库文件" name="fileUri">
              <Dragger {...uploadProps} listType="text">
                <p className="ant-upload-drag-icon">
                  <InboxOutlined />
                </p>
                <p className="ant-upload-text">点击上传</p>
              </Dragger>
              <Form.Item style={{marginTop: "20px"}} label="文件" name="content">
                <Input disabled />
              </Form.Item>
              <Form.Item label="本地路径" name="localPath">
                <Input disabled />
              </Form.Item>
            </Form.Item>
          ) : selectType == '1' ? (
            <Form.Item
              label="知识库文件URL"
              name="fileUri"
              rules={[{ required: true, message: '请输入知识库文件URL!' }]}
            >
              <Input placeholder="输入知识库文件的url" />
            </Form.Item>
          ) : selectType == '4' ? (
            <Form.Item
              label="公众号名称"
              name="mpName"
              rules={[{ required: true, message: '请输入公众号名称!' }]}
            >
              <Input placeholder="请输入公众号名称" />
            </Form.Item>
          ) : (
            <Form.Item
              label="文件夹"
              name="filePath"
              rules={[{ required: true, message: '请选择文件夹!' }]}
            >
              <Input placeholder="输入知识文件夹" />
            </Form.Item>
          )}
          <Form.Item
            label="知识库内容"
            name="content"
            rules={[{ required: true, message: '请输入文件内容!' }]}
          >
            <TextArea placeholder="自动根据上传的文件填充，亦可手动修改" />
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
        title="编辑知识库文件"
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
          <Form.Item label="ID" name="serialNo">
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="名称"
            name="name"
            rules={[{ required: true, message: '请输入知识库文件名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="类型"
            name="type"
            rules={[{ required: true, message: '请输入类型!' }]}
          >
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="文件类型"
            name="fileType"
            rules={[{ required: true, message: '请输入文件类型!' }]}
          >
            <Input disabled />
          </Form.Item>
          <Form.Item label="描述" name="description">
            <TextArea
              placeholder="请输入相关描述信息"
              autoSize={{ minRows: 2, maxRows: 8 }}
            />
          </Form.Item>
        </Form>
      </Modal>

      {/* 召回模态框 */}
      <Modal
        title="数据召回测试"
        open={isRetrivalFormModal}
        onOk={confirmRetrival}
        onCancel={() => setIsRetrivalFormModal(false)}
        centered
      >
        <Retrival
          chatUIProps={chatUIProps}
          knowledgeBaseFileList={knowledgeBaseFileList}
          knowledgeBaseNo={knowledgeBase}
        />
      </Modal>
    </>
  )
}

export default KnowledgeBaseFile
