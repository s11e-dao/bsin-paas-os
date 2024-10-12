import columnsWechatMonitorData, {
  WechatMonitorColumnsItem,
} from './wechatMonitordata'
import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
import { getWechatLoginList } from './service'
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
const { Option } = Select
import { PlusOutlined } from '@ant-design/icons'
import { startPlatform } from '../Tool/service'

export default () => {
  const { TextArea } = Input

  // 新增模态框
  const [isAddFormModal, setIsAddFormModal] = useState(false)
  // 编辑模态框
  const [isEditFormModal, setIsEditFormModal] = useState(false)
  // 查看模态框
  const [isViewFormModal, setIsViewFormModal] = useState(false)

  // 获取新增表单信息
  const addFormRef: any = React.createRef()
  // 获取编辑表单信息
  const [editFormRef] = Form.useForm()

  // 获取编辑表单信息
  const [viewFormRef] = Form.useForm()

  // Table action 的引用，便于自定义触发 用于更改数据之后的表单刷新
  const actionRef = React.useRef<ActionType>()
  // 表头赋值
  const columns: ProColumns<
    WechatMonitorColumnsItem
  >[] = columnsWechatMonitorData
  // 操作列渲染
  const optionRender = (text: any, record: any, index: number) => [
    <div key={record.roleId}>
      <Divider type="vertical" />
      <Popconfirm
        title="是否下线?"
        onConfirm={() => confirmForceLogout(record)}
        onCancel={cancelForceLogout}
        okText="是"
        cancelText="否"
      >
        <a>强制下线</a>
      </Popconfirm>
    </div>,
  ]
  // 自定义表格头部渲染
  columns.forEach((item: any) => {
    item.dataIndex === 'option' ? (item.render = optionRender) : undefined
  })

  // 点击新增
  const confirmAdd = () => {
    addFormRef.current
    // .validateFields()
    // .then(async () => {
    //   var response = addFormRef.current?.getFieldsValue()
    //   let res = await addAdminMonitor({ ...response })
    //   res ? message.success('新增成功') : message.error('新增失败！')
    //   // 重置表单
    //   addFormRef.current.resetFields()
    //   setIsAddFormModal(false)
    //   actionRef.current?.reload()
    // })
    // .catch(() => {})
  }

  // 点击编辑
  const toEdit = async (record) => {
    // if (record.editable == false) {
    //   message.warning('该配置不支持编辑！！')
    //   return
    // }
    // let { serialNo } = record
    // let viewRes = await getAdminMonitorDetail({ serialNo })
    // editFormRef.setFieldsValue(viewRes.data)
    // console.log(viewRes.data)
    // setIsEditFormModal(true)
  }

  // 编辑确认
  const confirmEdit = (record) => {
    // editFormRef
    //   .validateFields()
    //   .then(async () => {
    //     var formInfo = editFormRef.getFieldsValue()
    //     // formInfo.serialNo = record.serialNo
    //     let res = await editAdminMonitor(formInfo)
    //     res ? message.success('编辑成功') : message.error('编辑失败！')
    //     // 重置表单
    //     editFormRef.resetFields()
    //     setIsEditFormModal(false)
    //     actionRef.current?.reload()
    //   })
    //   .catch(() => {})
  }

  // 点击强制下线
  const confirmForceLogout = async (record) => {
    let { wxPlatformNo, alive } = record
    if (!alive) {
      message.info('已经处于离线状态啦！')
      return
    }
    let res = await startPlatform({
      serialNo: wxPlatformNo,
      loginIn: 'false',
    })
    if (res.code == '000000') {
      message.success('强制下线成功')
    } else {
      message.error(res.message)
    }
  }

  // 取消删除
  const cancelForceLogout = () => {
    message.warning('取消操作')
  }

  /**
   * 查看详情
   */
  const toViewAdminMonitor = async (record) => {
    // let { serialNo } = record
    // let viewRes = await getAdminMonitorDetail({ serialNo })
    // // 数据回显
    // viewFormRef.setFieldsValue(viewRes.data)
    // console.log(viewRes.data)
    // setIsViewFormModal(true)
  }

  const formItemComponent = () => {
    return (
      <>
        <Form.Item label="ID" name="wxPlatformNo">
          <Input disabled />
        </Form.Item>
        <Form.Item label="微信昵称" name="nickname">
          <Input disabled />
        </Form.Item>
        <Form.Item
          label="绑定的智能体编号"
          name="copilotNo"
          rules={[{ required: true, message: '请输入智能体编号!' }]}
        >
          <Input disabled />
        </Form.Item>
        <Form.Item
          label="请求间隔时间"
          name="requestIntervalLimit"
          rules={[{ required: true, message: '请输入请求间隔时间!' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="预回复消息"
          name="preResp"
          rules={[{ required: true, message: '请输入预回复消息!' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item label="群聊支持" name="groupChat">
          <Switch />
        </Form.Item>
        <Form.Item label="历史聊天记录总结" name="historyChatSummary">
          <Switch />
        </Form.Item> 
        <Form.Item label="服务到期时间" name="expirationTime">
          <Input />
        </Form.Item>
        <Form.Item label="登录操作" name="loginIn">
          <Input disabled />
        </Form.Item>
        <Form.Item label="在线状态" name="alive">
          <Switch />
        </Form.Item>
      </>
    )
  }

  return (
    <div>
      {/* 表格 */}
      <ProTable<WechatMonitorColumnsItem>
        actionRef={actionRef}
        scroll={{ x: 900 }}
        bordered
        headerTitle={<TableTitle title="微信登录列表" />}
        columns={columns}
        // 请求数据
        request={async (params) => {
          let res = await getWechatLoginList({
            ...params,
          })
          console.log(res)
          if (res.code == '000000') {
          } else {
            message.error(res.message)
          }
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
            新建
          </Button>,
        ]}
        // 本地储存表格列的显示参数
        columnsState={{
          persistenceKey: 'Apps',
          persistenceType: 'localStorage',
        }}
        // 每行表格的key
        rowKey="roleId"
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
        title="新增索引模型"
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
            name: 'AllMiniLmL6V2',
            status: '1',
            accessAuthority: '1',
            retrievalMethod: '1',
            tokenizerModel: 'OpenAi',
            maxResults: 3,
            minScore: 0.7,
            quoteLimit: 1000,
            dimension: '512',
            segmentSizeInTokens: 100,
            overlapSizeInTokens: 0,
            documentSplitter: 'ByCharacter',
          }}
        >
          {formItemComponent}
        </Form>
      </Modal>
      {/* 编辑模态框 */}
      <Modal
        title="编辑模型"
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
        title="查看模型"
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
    </div>
  )
}
