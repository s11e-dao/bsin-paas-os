import columnsData, { AppColumnsItem } from './data'
import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
import {
  getWxmpUserPageList,
  delWxmpUserInfo,
  addWxmpUserInfo,
  editWxmpUserInfo,
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
  Card,
} from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useModel } from 'umi'
import UserDetail from './detail'

export default () => {
  // 获取appId
  const { appId } = useModel('@@qiankunStateFromMaster')
  const { TextArea } = Input

  // 新增模态框
  const [isAddFormModal, setIsAddFormModal] = useState(false)
  // 编辑模态框
  const [isEditFormModal, setIsEditFormModal] = useState(false)
  // 存储编辑的ID
  const [LLMId, setLLMId] = useState(false)
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
  // 控制列表/跟详情展示
  const [detailFlag, setDetailFlag] = useState(true)
  const [openId, setOpenId] = useState('')

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
        let res = await addWxmpUserInfo({ ...response })

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
    setLLMId(record.serialNo)
    // 数据回显
    editFormRef.setFieldsValue(record)
  }

  // 编辑确认
  const confirmEdit = () => {
    editFormRef
      .validateFields()
      .then(async () => {
        var formInfo = editFormRef.getFieldsValue()
        formInfo.serialNo = LLMId
        let res = await editWxmpUserInfo(formInfo)

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
    if (record.editable == false) {
      message.warning('该配置不支持删除！！')
      return
    }
    let { serialNo } = record

    let res = await delWxmpUserInfo({ serialNo })
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

  // 详情
  const detail = async (record) => {
    const { serialNo, openId } = record
    setOpenId(openId)
    setDetailFlag(false)
  }

  return (
    <div>
      {/* 表格 */}
      {detailFlag ? (
        <div>
          <ProTable<AppColumnsItem>
            actionRef={actionRef}
            scroll={{ x: 900 }}
            bordered
            headerTitle={<TableTitle title="会员信息" />}
            columns={columns}
            // 请求数据
            request={async (params) => {
              let res = await getWxmpUserPageList({
                ...params,
              })
              console.log(res)

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
            title="新增模型"
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
              <Form.Item
                label="模型名称"
                name="name"
                rules={[{ required: true, message: '请输入模型!' }]}
              >
                <Input />
              </Form.Item>
              <Form.Item
                label="模型编号"
                name="code"
                rules={[{ required: true, message: '请输入模型编号!' }]}
              >
                <Input />
              </Form.Item>
              <Form.Item
                label="模型KEY"
                name="key"
                rules={[{ required: true, message: '请输入模型KEY!' }]}
              >
                <Input />
              </Form.Item>
              <Form.Item
                label="模型"
                name="model"
                rules={[{ required: true, message: '请输入模型!' }]}
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
                label="maxToken"
                name="maxToken"
                rules={[{ required: true, message: '请输入maxToken!' }]}
              >
                <InputNumber min={2000} defaultValue={2000} />
              </Form.Item>
              <Form.Item
                label="预回复"
                name="preResponse"
                rules={[{ required: true, message: '请输入预回复!' }]}
              >
                <Input />
              </Form.Item>
              <Form.Item
                label="上下文条数"
                name="contextLimitNum"
                rules={[{ required: true, message: '请输入上下文条数!' }]}
              >
                <InputNumber min={2} defaultValue={10} />
              </Form.Item>
              <Form.Item
                label="temperature"
                name="temperature"
                rules={[{ required: true, message: '请输入temperature!' }]}
              >
                <InputNumber min={0} max={2} step="0.1" defaultValue={0.7} />
              </Form.Item>
              <Form.Item label="模板推送" name="templateEnable">
                <Switch />
              </Form.Item>
              <Form.Item label="启用上下文" name="contextEnable">
                <Switch />
              </Form.Item>

              <Form.Item label="systemRole上下文" name="systemRoleEnable">
                <Switch />
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
              <Form.Item
                label="模型名称"
                name="name"
                rules={[{ required: true, message: '请输入模型!' }]}
              >
                <Input />
              </Form.Item>
              <Form.Item
                label="模型编号"
                name="code"
                rules={[{ required: true, message: '请输入模型编号!' }]}
              >
                <Input />
              </Form.Item>
              <Form.Item
                label="模型KEY"
                name="key"
                rules={[{ required: true, message: '请输入模型KEY!' }]}
              >
                <Input />
              </Form.Item>
              <Form.Item
                label="模型"
                name="model"
                rules={[{ required: true, message: '请输入模型!' }]}
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
                label="maxToken"
                name="maxToken"
                rules={[{ required: true, message: '请输入maxToken!' }]}
              >
                <InputNumber min={2000} defaultValue={2000} />
              </Form.Item>
              <Form.Item
                label="预回复"
                name="preResponse"
                rules={[{ required: true, message: '请输入预回复!' }]}
              >
                <Input />
              </Form.Item>
              <Form.Item
                label="上下文条数"
                name="contextLimitNum"
                rules={[{ required: true, message: '请输入上下文条数!' }]}
              >
                <InputNumber min={2} defaultValue={10} />
              </Form.Item>
              <Form.Item
                label="temperature"
                name="temperature"
                rules={[{ required: true, message: '请输入temperature!' }]}
              >
                <InputNumber min={0} max={2} step="0.1" defaultValue={0.7} />
              </Form.Item>
              <Form.Item label="模板推送" name="templateEnable">
                <Switch />
              </Form.Item>
              <Form.Item label="启用上下文" name="contextEnable">
                <Switch />
              </Form.Item>

              <Form.Item label="systemRole上下文" name="systemRoleEnable">
                <Switch />
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

              <Form.Item label="描述" name="description">
                <TextArea
                  placeholder="请输入相关描述信息"
                  autoSize={{ minRows: 2, maxRows: 8 }}
                />
              </Form.Item>
            </Form>
          </Modal>
        </div>
      ) : (
        <Card>
          <UserDetail
            openId={openId}
            closeHandle={() =>
              // 是否需要保存
              setDetailFlag(true)
            }
          />
        </Card>
      )}
    </div>
  )
}
