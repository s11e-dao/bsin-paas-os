import columnsData, { AppColumnsItem } from './data'
import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
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
  Col,
  Row,
  Space,
  Select,
  Radio,
  Avatar,
} from 'antd'
const { Meta } = Card
const { Option } = Select

import type { UploadProps } from 'antd'
import {
  EditOutlined,
  EllipsisOutlined,
  SettingOutlined,
} from '@ant-design/icons'

import { PlusOutlined } from '@ant-design/icons'
import { useModel } from 'umi'

// 样式设计参考：https://fastgpt.run/dataset/list

export default () => {
  // 获取appId
  const { appId } = useModel('@@qiankunStateFromMaster')
  const { TextArea } = Input

  // 新增模态框
  const [isAddFormModal, setIsAddFormModal] = useState(false)
  // 编辑模态框
  const [isEditFormModal, setIsEditFormModal] = useState(false)
  // 存储编辑的ID
  const [roleId, setRoleId] = useState(false)
  // 获取新增表单信息
  const addFormRef: any = React.createRef()
  // 获取编辑表单信息
  const [editFormRef] = Form.useForm()

  const [knowledgeBaseList, setKnowledgeBaseList] = useState([])

  useEffect(() => {
    // 查询协议
    let params = {
      current: '1',
      pageSize: '99',
    }
    getKnowledgeBasePageList(params).then((res) => {
      setKnowledgeBaseList(res?.data)
    })
  }, [])

  // Table action 的引用，便于自定义触发 用于更改数据之后的表单刷新
  const actionRef = React.useRef<ActionType>()
  // 表头赋值
  const columns: ProColumns<AppColumnsItem>[] = columnsData
  // 操作列渲染
  const optionRender = (text: any, record: any, index: number) => [
    <div key={record.roleId}>
      <a onClick={() => edit(record)}>编辑</a>
      <Divider type="vertical" />
      <Popconfirm
        title="是否删除此条数据?"
        onConfirm={() => confirmDel(record.roleId)}
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
    addFormRef.current
      .validateFields()
      .then(async () => {
        var response = addFormRef.current?.getFieldsValue()
        let res = await addKnowledgeBase({ ...response, appId })
        if (res.code == '000000') {
          message.success('新增成功')
          // 重置表单
          addFormRef.current.resetFields()
          setIsAddFormModal(false)
          actionRef.current?.reload()
        } else {
          message.error(res.message)
        }
      })
      .catch(() => {})
  }

  // 点击编辑
  const edit = (record) => {
    setIsEditFormModal(true)
    // 存储id
    setRoleId(record.roleId)
    // 数据回显
    editFormRef.setFieldsValue(record)
  }

  // 编辑确认
  const confirmEdit = () => {
    editFormRef
      .validateFields()
      .then(async () => {
        var response = editFormRef.getFieldsValue()
        response.roleId = roleId
        let res = await editKnowledgeBase(response)
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
  const confirmDel = async (roleId: string) => {
    let res = await delKnowledgeBase({ roleId })
    res ? message.success('删除成功') : message.error('删除失败！')
    actionRef.current?.reload()
  }

  // 取消删除
  const cancelDel = () => {
    message.warning('取消删除')
  }

  return (
    <Card>
      <Space size="middle" style={{ display: 'flex', flexWrap: 'wrap' }}>
        {/* {knowledgeBaseList.map((knowledgeBase) => { */}
        <Card
          style={{ width: 300 }}
          cover={
            <img
              alt="example"
              src="https://xsgames.co/randomusers/avatar.php?g=pixel"
            />
          }
          actions={[
            <SettingOutlined key="setting" />,
            <EditOutlined key="edit" title="编辑" />,
            <EllipsisOutlined key="ellipsis" title="详情" />,
          ]}
        >
          <Meta
            avatar={
              <Avatar src="https://xsgames.co/randomusers/avatar.php?g=pixel" />
            }
            title="titile"
            description="description"
          />
        </Card>
        {/* })} */}
      </Space>
      {/* 新增模态框 */}
      <Modal
        title="创建一个ChunkModel"
        visible={isAddFormModal}
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
          initialValues={{ remember: true }}
        >
          <Form.Item
            label="知识库类型"
            name="type"
            rules={[{ required: true, message: '请选择知识库类型!' }]}
          >
            <Select value="0">
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
            <Input />
          </Form.Item>

          <Form.Item
            label="索引模型"
            name="embeddingModel"
            rules={[{ required: true, message: '请选择索引模型!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="1">Embedding-2</Option>
              <Option value="2">M3E</Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="文件处理模型"
            name="splitModel"
            rules={[{ required: true, message: '请选择文件处理模型!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="1">openAI-16k</Option>
              <Option value="2">??</Option>
            </Select>
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
        visible={isEditFormModal}
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
          <Form.Item
            label="知识库名称"
            name="name"
            rules={[{ required: true, message: '请输入知识库名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="描述" name="description">
            <TextArea
              placeholder="请输入相关描述信息"
              autoSize={{ minRows: 2, maxRows: 8 }}
            />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}
