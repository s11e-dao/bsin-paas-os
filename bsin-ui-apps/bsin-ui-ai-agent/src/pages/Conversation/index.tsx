import columnsData, { AppColumnsItem } from './data'
import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
import {
  getconversationPageList,
  getconversationList,
  delconversationInfo,
  addconversationInfo,
  editconversationInfo,
  getconversationDetail,
  messagePush,
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
  Switch,
  InputNumber,
  Radio,
  Select,
} from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useModel } from 'umi'

const { Option } = Select
export default () => {
  const { TextArea } = Input

  // 新增模态框
  const [isAddFormModal, setIsAddFormModal] = useState(false)
  // 编辑模态框
  const [isEditFormModal, setIsEditFormModal] = useState(false)
  // 查看模态框
  const [isViewFormModal, setIsViewFormModal] = useState(false)
  // 推送消息模态框
  const [isPushMessageModal, setIsPushMessageModal] = useState(false)

  // 获取新增表单信息
  const addFormRef: any = React.createRef()
  // 获取编辑表单信息
  const [editFormRef] = Form.useForm()

  // 获取编辑表单信息
  const [viewFormRef] = Form.useForm()
  
  // 获取推送消息表单信息
  const [pushMessageFormRef] = Form.useForm()

  // Table action 的引用，便于自定义触发 用于更改数据之后的表单刷新
  const actionRef = React.useRef<ActionType>()
  // 表头赋值
  const columns: ProColumns<AppColumnsItem>[] = columnsData
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
      <a
        onClick={() => {
          toViewConversation(record)
        }}
      >
        详情
      </a>
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
        let res = await addconversationInfo({ ...response })
        if (res.code == 0 || res.code == '0' || res.code == '000000') {
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
  const toEdit = async (record: any) => {
    editFormRef.setFieldsValue(record)
    console.log(record)
    setIsEditFormModal(true)
  }

  // 编辑确认
  const confirmEdit = () => {
    editFormRef
      .validateFields()
      .then(async () => {
        var formInfo = editFormRef.getFieldsValue()
        let res = await editconversationInfo(formInfo)
        if (res.code == 0 || res.code == '0' || res.code == '000000') {
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
  const confirmDel = async (record: any) => {
    let { serialNo } = record
    let res = await delconversationInfo({ serialNo })
    res ? message.success('删除成功') : message.error('删除失败！')
    actionRef.current?.reload()
  }

  // 取消删除
  const cancelDel = () => {
    message.warning('取消删除')
  }

  // 推送消息
  const handlePushMessage = () => {
    pushMessageFormRef
      .validateFields()
      .then(async () => {
        try {
          const formValues = pushMessageFormRef.getFieldsValue()
          message.loading('正在推送消息...', 0)
          
          let res = await messagePush({
            title: formValues.title,
            content: formValues.content,
          })
          
          message.destroy()
          
          if (res && (res.code === 0 || res.code === '0' || res.code === '000000')) {
            message.success('消息推送成功')
            // 重置表单并关闭模态框
            pushMessageFormRef.resetFields()
            setIsPushMessageModal(false)
            // 刷新表格数据
            actionRef.current?.reload()
          } else {
            message.error(res?.message || '消息推送失败')
          }
        } catch (error) {
          message.destroy()
          message.error('消息推送失败，请稍后重试')
          console.error('推送消息错误:', error)
        }
      })
      .catch(() => {})
  }

  /**
   * 查看详情
   */
  const toViewConversation = async (record: any) => {
    let { serialNo } = record
    let viewRes = await getconversationDetail({ serialNo })
    // 数据回显
    viewFormRef.setFieldsValue(viewRes.data)
    console.log(viewRes.data)
    setIsViewFormModal(true)
  }

  const formItemComponent = () => {
    return (
      <>
        <Form.Item label="对话ID" name="serialNo">
          <Input disabled />
        </Form.Item>
        <Form.Item
          label="会话标题"
          name="title"
          rules={[{ required: true, message: '请输入会话标题!' }]}
        >
          <Input placeholder="请输入会话标题" />
        </Form.Item>
        <Form.Item
          label="智能体编号"
          name="agentNo"
          rules={[{ required: true, message: '请输入智能体编号!' }]}
        >
          <Input placeholder="请输入智能体编号" />
        </Form.Item>
        <Form.Item
          label="智能体名称"
          name="agentName"
          rules={[{ required: true, message: '请输入智能体名称!' }]}
        >
          <Input placeholder="请输入智能体名称" />
        </Form.Item>
        <Form.Item
          label="用户类型编号"
          name="bizRoleTypeNo"
        >
          <Input placeholder="请输入用户类型编号" />
        </Form.Item>
        <Form.Item
          label="用户类型"
          name="bizRoleType"
        >
          <Input placeholder="请输入用户类型" />
        </Form.Item>
        <Form.Item
          label="状态"
          name="status"
          rules={[{ required: true, message: '请选择对话状态!' }]}
        >
          <Radio.Group>
            <Radio value={1}>活跃</Radio>
            <Radio value={2}>已结束</Radio>
            <Radio value={3}>已删除</Radio>
            <Radio value={4}>已归档</Radio>
          </Radio.Group>
        </Form.Item>
        <Form.Item
          label="智能体头像"
          name="agentIconUrl"
        >
          <Input placeholder="请输入智能体头像URL" />
        </Form.Item>
        <Form.Item
          label="未读消息数"
          name="unreadCount"
        >
          <Input placeholder="未读消息数" />
        </Form.Item>
        <Form.Item
          label="最后消息ID"
          name="lastMessageNo"
        >
          <Input placeholder="最后一条消息ID" disabled />
        </Form.Item>
        <Form.Item
          label="最后消息内容"
          name="lastMessageContent"
        >
          <TextArea
            placeholder="最后一条消息内容预览"
            autoSize={{ minRows: 2, maxRows: 4 }}
            disabled
          />
        </Form.Item>
      </>
    )
  }

  return (
    <div>
      {/* 表格 */}
      <ProTable<AppColumnsItem>
        actionRef={actionRef}
        scroll={{ x: 900 }}
        bordered
        headerTitle={<TableTitle title="对话管理" />}
        columns={columns}
        // 请求数据
        request={async (params) => {
          try {
            let res = await getconversationPageList({
              ...params,
            })
            console.log(res)

            // 确保返回的数据格式正确
            if (!res) {
              return { data: [], total: 0, success: false }
            }

            // 验证 data 字段是数组
            const data = Array.isArray(res.data) ? res.data : []
            const total = res.pagination?.totalSize || 0

            return {
              data,
              total,
              success: true,
            }
          } catch (error) {
            console.error('获取对话列表失败:', error)
            return {
              data: [],
              total: 0,
              success: false,
            }
          }
        }}
        toolBarRender={() => [
          <Button
            key="button"
            icon={<PlusOutlined />}
            type="primary"
            onClick={() => setIsPushMessageModal(true)}
          >
            推送数据
          </Button>,
        ]}
        // 本地储存表格列的显示参数
        columnsState={{
          persistenceKey: 'Conversations',
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
        title="新增对话"
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
          initialValues={{
            status: 1,
            unreadCount: '0',
          }}
        >
          {formItemComponent()}
        </Form>
      </Modal>
      {/* 编辑模态框 */}
      <Modal
        title="编辑对话"
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
          {formItemComponent()}
        </Form>
      </Modal>

      {/* 查看模态框 */}
      <Modal
        title="查看对话"
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
          {formItemComponent()}
        </Form>
      </Modal>

      {/* 推送消息模态框 */}
      <Modal
        title="推送系统消息"
        open={isPushMessageModal}
        onOk={handlePushMessage}
        onCancel={() => {
          setIsPushMessageModal(false)
          pushMessageFormRef.resetFields()
        }}
        centered
        width={600}
      >
        <Form
          name="pushMessage"
          form={pushMessageFormRef}
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 16 }}
          initialValues={{
            title: '',
            content: '',
          }}
        >
          <Form.Item
            label="消息标题"
            name="title"
            rules={[{ required: true, message: '请输入消息标题!' }]}
          >
            <Input placeholder="请输入消息标题" />
          </Form.Item>
          <Form.Item
            label="消息内容"
            name="content"
            rules={[{ required: true, message: '请输入消息内容!' }]}
          >
            <TextArea
              placeholder="请输入消息内容"
              autoSize={{ minRows: 4, maxRows: 8 }}
            />
          </Form.Item>
          <Form.Item>
            <div style={{ color: '#666', fontSize: '12px' }}>
              消息将推送给当前租户下所有已启用且已发布的智能体，每个智能体将创建一个新的对话记录。
            </div>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
