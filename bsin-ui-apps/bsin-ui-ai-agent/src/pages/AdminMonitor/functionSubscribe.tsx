import columnsWechatMonitorData, {
  columnsFunctionSubscribeDataType,
} from '../Setting/components/serviceSubscribe/functionSubscribeData'
import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
import {
  getAllFunctionSubscribeList,
  auditFunctionServiceOrder,
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
const { Option } = Select
import { PlusOutlined } from '@ant-design/icons'
import { startPlatform } from '../Tool/service'

export default () => {
  const { TextArea } = Input

  // 新增模态框
  const [isAddFormModal, setIsAddFormModal] = useState(false)
  // 审核模态框
  const [isAuditFormModal, setIsAuditFormModal] = useState(false)
  // 查看模态框
  const [isViewFormModal, setIsViewFormModal] = useState(false)

  // 获取新增表单信息
  const addFormRef: any = React.createRef()
  // 获取审核表单信息
  const [auditFormRef] = Form.useForm()

  // 获取审核表单信息
  const [viewFormRef] = Form.useForm()

  // Table action 的引用，便于自定义触发 用于更改数据之后的表单刷新
  const actionRef = React.useRef<ActionType>()
  // 表头赋值
  const columns: ProColumns<
    columnsFunctionSubscribeDataType
  >[] = columnsWechatMonitorData
  // 操作列渲染
  const optionRender = (text: any, record: any, index: number) => [
    <div key={record.roleId}>
      <Divider type="vertical" />
      <a
        onClick={() => {
          toAudit(record)
        }}
      >
        审核
      </a>
    </div>,
  ]
  // 自定义表格头部渲染
  columns.forEach((item: any) => {
    item.dataIndex === 'option' ? (item.render = optionRender) : undefined
  })

  // 点击审核
  const toAudit = async (record) => {
    // 应用|功能状态：0、待缴费 1、待审核  2、正常 3、欠费停止 4、冻结
    // if (record.status != '1') {
    //   message.warning('不满足审核条件')
    //   return
    // }
    auditFormRef.setFieldsValue(record)
    console.log(record)
    setIsAuditFormModal(true)
  }

  // 审核确认
  const confirmAudit = () => {
    auditFormRef
      .validateFields()
      .then(async () => {
        var formInfo = auditFormRef.getFieldsValue()
        let res = await auditFunctionServiceOrder(formInfo)
        res ? message.success('审核成功') : message.error('审核失败！')
        // 重置表单
        auditFormRef.resetFields()
        setIsAuditFormModal(false)
        actionRef.current?.reload()
      })
      .catch(() => {})
  }
  // 审核确认
  const cancelAudit = () => {
    // 重置表单
    auditFormRef.resetFields()
    setIsAuditFormModal(false)
  }

  return (
    <div>
      {/* 表格 */}
      <ProTable<columnsFunctionSubscribeDataType>
        actionRef={actionRef}
        scroll={{ x: 900 }}
        bordered
        headerTitle={<TableTitle title="订单列表" />}
        columns={columns}
        // 请求数据
        request={async (params) => {
          let res = await getAllFunctionSubscribeList({
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

      {/* 审核模态框 */}
      <Modal
        title="订阅审核"
        open={isAuditFormModal}
        onOk={confirmAudit}
        onCancel={cancelAudit}
        centered
      >
        <Form
          name="basic"
          form={auditFormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          initialValues={{ auditStatus: 'pass' }}
        >
          <Form.Item label="订单ID" name="serialNo">
            <Input disabled />
          </Form.Item>
          <Form.Item label="订单名称" name="name">
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="当前订单状态"
            name="status"
            rules={[{ required: true, message: '当前订单状态!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="0">待缴费</Option>
              <Option value="1">待审核</Option>
              <Option value="2">正常</Option>
              <Option value="3">欠费停止</Option>
              <Option value="4">冻结</Option>
            </Select>
          </Form.Item>
          <Form.Item label="服务时长" name="serviceDuration" tooltip="单位：天">
            <Input disabled />
          </Form.Item>

          <Form.Item
            label="支付凭证"
            name="payReceipt"
            tooltip="请核对支付凭证后再通过审核"
          >
            <img style={{width: "80px", height: "80px"}}
              className="payReceipt"
              alt=""
              src={auditFormRef.getFieldValue('payReceipt')}
            />
          </Form.Item>

          <Form.Item
            label="审核"
            name="auditStatus"
            rules={[{ required: true, message: '请选择操作类型!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="pass">通过</Option>
              <Option value="reject">拒绝</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
