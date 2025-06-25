import columnsData, { AppColumnsItem } from './data'
import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
import {
  getToolPageList,
  delTool,
  addTool,
  editTool,
  getToolDetail,
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
} from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useModel } from 'umi'

export default () => {
  
  // 获取appId
  const { appId } = useModel('@@qiankunStateFromMaster')
  const { TextArea } = Input

  // 新增模态框
  const [isAddFormModal, setIsAddFormModal] = useState(false)
  // 编辑模态框
  const [isEditFormModal, setIsEditFormModal] = useState(false)
  // 存储编辑的ID
  const [ToolId, setToolId] = useState(false)
  // 获取新增表单信息
  const addFormRef: any = React.createRef()
  // 获取编辑表单信息
  const [editFormRef] = Form.useForm()
  // 保存请求的全部数据
  const [menuList, setMenuList] = useState([])
  // 保存请求的勾选的数据
  const [menuListChecked, setMenuListChecked] = useState([])
  // tree的操作，每次操作保留的key值
  const [isKey, setIsKey] = useState([])
  // 菜单授权保留每行值
  const [isMenuInfo, setIsMenuInfo] = useState({})

  // Table action 的引用，便于自定义触发 用于更改数据之后的表单刷新
  const actionRef = React.useRef<ActionType>()
  // 表头赋值
  const columns: ProColumns<AppColumnsItem>[] = columnsData
  // 操作列渲染
  const optionRender = (text: any, record: any, index: number) => [
    <div key={record.serialNo}>
      <a onClick={() => edit(record)}>编辑</a>
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
          detail(record)
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
        let res = await addTool({ ...response })
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
  const edit = (record: any) => {
    setIsEditFormModal(true)
    // 存储id
    setToolId(record.serialNo)
    // 数据回显
    editFormRef.setFieldsValue(record)
  }

  // 编辑确认
  const confirmEdit = () => {
    editFormRef
      .validateFields()
      .then(async () => {
        var formInfo = editFormRef.getFieldsValue()
        // formInfo.serialNo = ToolId
        let res = await editTool(formInfo)
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
  const confirmDel = async (record: any) => {
    if (record.editable == false) {
      message.warning('该配置不支持删除！！')
      return
    }
    let { serialNo } = record
    let res = await delTool({ serialNo })
    if (res.code == '000000') {
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

  // 菜单授权
  const detail = async (record: any) => {
    const { serialNo } = record
    // 请求出全部的数据
    const { data: treeData } = await getToolDetail({ serialNo })
  }

  const formItemComponent = () => {
    return (
      <>
        <Form.Item
          label="工具名称"
          name="name"
          rules={[{ required: true, message: '请输入工具!' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="工具编号"
          name="code"
          rules={[{ required: true, message: '请输入工具编号!' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="工具KEY"
          name="key"
          rules={[{ required: true, message: '请输入工具KEY!' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="请求地址"
          name="url"
          rules={[{ required: true, message: '请输入请求地址!' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="接口异常回复"
          name="exceptionResponse"
          rules={[{ required: true, message: '请启输入接口异常回复!' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="自我介绍词汇"
          name="selfIntroductionCopyWriting"
          rules={[{ required: true, message: '请输入自我介绍词汇!' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item label="是否可编辑" name="editable">
          <Switch disabled />
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
    <div>
      {/* 表格 */}
      <ProTable<AppColumnsItem>
        actionRef={actionRef}
        scroll={{ x: 900 }}
        bordered
        headerTitle={<TableTitle title="MCP通用工具" />}
        columns={columns}
        // 请求数据
        request={async (params) => {
          try {
            let res = await getToolPageList({
              ...params,
              appId,
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
            console.error('获取工具列表失败:', error)
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
        title="新增Tool"
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
          initialValues={{ remember: true }}
        >
          {formItemComponent()}
        </Form>
      </Modal>
      {/* 编辑模态框 */}
      <Modal
        title="编辑工具"
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
          {formItemComponent()}
        </Form>
      </Modal>
    </div>
  )
}
