import columnsData, { AppColumnsItem } from './data'
import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
import {
  getSensitiveWordsList,
  delSensitiveWords,
  addSensitiveWords,
  editSensitiveWords,
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
  Tree,
  Switch,
  InputNumber,
  Radio,
  Select,
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
  // 获取新增表单信息
  const addFormRef: any = React.createRef()
  // 获取编辑表单信息
  const [editFormRef] = Form.useForm()

  // Table action 的引用，便于自定义触发 用于更改数据之后的表单刷新
  const actionRef = React.useRef<ActionType>()
  // 表头赋值
  const columns: ProColumns<AppColumnsItem>[] = columnsData
  // 操作列渲染
  const optionRender = (text: any, record: any, index: number) => [
    <div key={record.roleId}>
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
    addFormRef.current
      .validateFields()
      .then(async () => {
        var response = addFormRef.current?.getFieldsValue()
        let res = await addSensitiveWords({ ...response })
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
  const toEdit = (record) => {
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
        let res = await editSensitiveWords(response)

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
    let res = await delSensitiveWords({ serialNo })
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
  const formItemComponent = () => {
    return (
      <>
        <Form.Item label="敏感词ID" name="serialNo">
          <Input disabled />
        </Form.Item>
        <Form.Item
          label="敏感词名称"
          name="name"
          rules={[{ required: true, message: '请输入敏感词名称!' }]}
        >
          <Input placeholder="起个名字吧！" />
        </Form.Item>

        <Form.Item label="敏感词内容" name="content">
          <TextArea
            placeholder="请输入敏感词内容，使用换行符分离：eg.\n敏感词1\n敏感词2\n"
            autoSize={{ minRows: 8 }}
          />
        </Form.Item>

        <Form.Item
          label="状态"
          name="status"
          rules={[{ required: true, message: '请选择状态!' }]}
        >
          <Radio.Group value="1">
            <Radio value="0">禁用</Radio>
            <Radio value="1">启用</Radio>
          </Radio.Group>
        </Form.Item>
        <Form.Item
          label="访问权限"
          name="accessAuthority"
          rules={[{ required: true, message: '请选择访问权限!' }]}
        >
          <Radio.Group value="1">
            <Radio value="1">private</Radio>
            <Radio value="2">public</Radio>
          </Radio.Group>
        </Form.Item>
        <Form.Item label="描述" name="description">
          <TextArea placeholder="请输入相关描述信息" />
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
        headerTitle={<TableTitle title="敏感词" />}
        columns={columns}
        // 请求数据
        request={async (params) => {
          let res = await getSensitiveWordsList({
            ...params,
            appId,
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
        title="新增敏感词"
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
          initialValues={{ status: '1', accessAuthority: '1' }}
        >
          {formItemComponent}
        </Form>
      </Modal>
      {/* 编辑模态框 */}
      <Modal
        title="编辑敏感词"
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
          initialValues={{}}
        >
          {formItemComponent}
        </Form>
      </Modal>
    </div>
  )
}
