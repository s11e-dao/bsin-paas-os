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
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
import TableTitle from '@/components/TableTitle'
import KnowledgeBaseFileChunkColumnsData, {
  KnowledgeBaseFileChunkColumnsItem,
} from './knowledgeBaseFileChunkData'
import {
  PlusOutlined,
  InboxOutlined,
  BackwardOutlined,
} from '@ant-design/icons'
const { Option } = Select
const { TextArea } = Input

import {
  getKnowledgeBaseFileChunkList,
  delKnowledgeBaseFileChunk,
  addKnowledgeBaseFileChunk,
  editKnowledgeBaseFileChunk,
} from './service'

const KnowledgeBaseFileDetail: React.FC = ({
  routeChange,
  knowledgeBaseFileRecord,
}) => {
  
  // 新增模态框
  const [isAddFormModal, setIsAddFormModal] = useState(false)
  // 编辑模态框
  const [isEditFormModal, setIsEditFormModal] = useState(false)
  // 编辑模态框
  const [isDelFormModal, setIsDelFormModal] = useState(false)

  const [knowledgeBaseFileNo, setKnowledgeBaseFileNo] = useState('')
  const [knowledgeBaseNo, setKnowledgeBaseNo] = useState('')

  const [knowledgeBaseFileChunkList, setKnowledgeBaseFileChunkList] = useState([
    {},
  ])

  // 获取新增表单信息
  const [addFormRef] = Form.useForm()
  // 获取编辑表单信息
  const [editFormRef] = Form.useForm()

  useEffect(() => {
    console.log(knowledgeBaseFileRecord)
    setKnowledgeBaseFileNo(knowledgeBaseFileRecord.serialNo)
    setKnowledgeBaseNo(knowledgeBaseFileRecord.knowledgeBaseNo)
  })

  // Table action 的引用，便于自定义触发 用于更改数据之后的表单刷新
  const actionRef = React.useRef<ActionType>()
  // 表头赋值
  const columns: ProColumns<
    KnowledgeBaseFileChunkColumnsItem
  >[] = KnowledgeBaseFileChunkColumnsData
  // 操作列渲染
  const optionRender = (text: any, record: any, index: number) => [
    <div key={record.serialNo}>
      <a onClick={() => toEdit(record)}>编辑</a>
      <Divider type="vertical" />
      <Popconfirm
        title="是否删除此条数据?"
        onConfirm={() => confirmDel(record)}
        onCancel={cancelDel}
        okText="是"
        cancelText="否"
      >
        <a>删除</a>
      </Popconfirm>
      <Divider type="vertical" />
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
        console.log(response)
        let res = await addKnowledgeBaseFileChunk({ ...response })
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
        let res = await editKnowledgeBaseFileChunk(response)
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
  const confirmDel = async (record) => {
    let res = await delKnowledgeBaseFileChunk({ ...record })
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

  return (
    <>
      <Button
        key="button"
        icon={<BackwardOutlined />}
        type="primary"
        onClick={() => {
          routeChange(knowledgeBaseFileRecord, 'knowledgeBaseFileList')
        }}
      >
        返回
      </Button>
      {/* 表格 */}
      <ProTable<KnowledgeBaseFileChunkColumnsItem>
        actionRef={actionRef}
        scroll={{ x: 900 }}
        bordered
        headerTitle={<TableTitle title="知识库文件索引" />}
        columns={columns}
        // 请求数据
        request={async (params) => {
          params.knowledgeBaseFileNo = knowledgeBaseFileNo
          console.log(params)
          let res = await getKnowledgeBaseFileChunkList({
            ...params,
          })
          console.log(res)
          setKnowledgeBaseFileChunkList(res.data)
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
        title="新增知识库文件索引"
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
            knowledgeBaseNo: knowledgeBaseNo,
            knowledgeBaseFileNo: knowledgeBaseFileNo,
          }}
        >
          <Form.Item label="绑定知识库ID" name="knowledgeBaseNo">
            <Input disabled />
          </Form.Item>
          <Form.Item label="绑定知识库文件ID" name="knowledgeBaseFileNo">
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="索引内容"
            name="chunkText"
            rules={[{ required: true, message: '请输入索引内容!' }]}
          >
            <TextArea autoSize={{ minRows: 16 }} />
          </Form.Item>

          <Form.Item label="chunk补充内容" name="content">
            <TextArea
              placeholder="作为索引内容的补充延升"
              autoSize={{ minRows: 16 }}
            />
          </Form.Item>
        </Form>
      </Modal>
      {/* 编辑模态框 */}
      <Modal
        title="编辑chunk"
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
          // 表单默认值
          initialValues={{
            knowledgeBaseNo: knowledgeBaseNo,
            knowledgeBaseFileNo: knowledgeBaseFileNo,
          }}
        >
          <Form.Item label="绑定知识库ID" name="knowledgeBaseNo">
            <Input disabled />
          </Form.Item>
          <Form.Item label="绑定知识库文件ID" name="knowledgeBaseFileNo">
            <Input disabled />
          </Form.Item>
          <Form.Item label="chunkNo" name="chunkNo">
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="索引内容"
            name="chunkText"
            rules={[{ required: true, message: '请输入索引内容!' }]}
          >
            <TextArea autoSize={{ minRows: 16 }} />
          </Form.Item>
          <Form.Item label="chunk补充内容" name="chunkContent">
            <TextArea autoSize={{ minRows: 16 }} />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}

export default KnowledgeBaseFileDetail
