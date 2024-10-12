import columnsData, { AppColumnsItem } from './data'
import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
import {
  getLLMPageList,
  getLLMList,
  delLLMInfo,
  addLLMInfo,
  editLLMInfo,
  getLLMDetail,
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

  // 记录appSecret是否发生编辑
  const [apiKey, setApiKey] = useState('')
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
      <a
        onClick={() => {
          toViewLlm(record)
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
        let res = await addLLMInfo({ ...response })
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
  const toEdit = async (record) => {
    if (record.editable == false) {
      message.warning('该配置不支持编辑！！')
      return
    }
    // let { serialNo } = record
    // let viewRes = await getLLMDetail({ serialNo })
    // editFormRef.setFieldsValue(viewRes.data)
    // console.log(viewRes.data)
    setApiKey(record.apiKey)
    console.log(record.apiKey)
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
        if (apiKey == formInfo.apiKey) {
          formInfo.apiKey = null
        }
        console.log(formInfo.apiKey)
        let res = await editLLMInfo(formInfo)
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
    let res = await delLLMInfo({ serialNo })
    res ? message.success('删除成功') : message.error('删除失败！')
    actionRef.current?.reload()
  }

  // 取消删除
  const cancelDel = () => {
    message.warning('取消删除')
  }

  /**
   * 查看详情
   */
  const toViewLlm = async (record) => {
    let { serialNo } = record
    let viewRes = await getLLMDetail({ serialNo })
    // 数据回显
    viewFormRef.setFieldsValue(viewRes.data)
    console.log(viewRes.data)
    setIsViewFormModal(true)
  }

  const formItemComponent = () => {
    return (
      <>
        <Form.Item label="LLM编号" name="serialNo">
          <Input disabled />
        </Form.Item>
        <Form.Item
          label="名称"
          name="name"
          // 大语言模型名称：1、GPT-3.5-Turbo 2、Bert 3、qwen-turbo 4、qwen-plus 5、baichuan-7b-v1 6、ChatGLM 7、qinafan 8、Bedrock
          rules={[{ required: true, message: '请选择LLM!' }]}
        >
          <Select style={{ width: '100%' }}>
            <Option value="GPT-3.5-Turbo">GPT-3.5-Turbo</Option>
            <Option value="Bert">Bert</Option>
            <Option value="qwen-turbo">qwen-turbo</Option>
            <Option value="qwen-plus">qwen-plus</Option>
            <Option value="baichuan-7b-v1">baichuan-7b-v1</Option>
            <Option value="ChatGLM">ChatGLM</Option>
            <Option value="qianfan">qianfan</Option>
            <Option value="Bedrock">Bedrock</Option>
            <Option value="VertexAiGemini">VertexAiGemini</Option>
          </Select>
        </Form.Item>
        <Form.Item
          label="类型"
          name="type"
          // 大语言模型类型：1、OpenAI 2、Google 3、dashScope
          rules={[{ required: true, message: '请选择类型!' }]}
        >
          <Select style={{ width: '100%' }}>
            <Option value="OpenAI">OpenAI</Option>
            <Option value="Google">Google</Option>
            <Option value="DashScope">DashScope</Option>
            <Option value="Baidu">Baidu</Option>
            <Option value="Zhipuai">Zhipuai</Option>
          </Select>
        </Form.Item>
        <Form.Item
          label="状态"
          name="status"
          rules={[{ required: true, message: '请选择LLM状态!' }]}
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
        <Form.Item
          label="api-key"
          name="apiKey"
          rules={[{ required: true, message: '请输入模型KEY!' }]}
          tooltip="系统不托管用户秘钥，数据库加密存储，显示数据为加密后的数据，修改则直接覆盖原来的秘钥"
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          label="secret-key"
          name="secretKey"
          tooltip="百度千帆大模型字段"
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="api-url"
          name="apiBaseUrl"
          rules={[{ required: true, message: '请输入模型KEY!' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item label="代理地址" name="proxyUrl">
          <Input placeholder="代理地址，不使用代理时为空" />
        </Form.Item>
        <Form.Item label="代理端口" name="proxyPort">
          <Input placeholder="代理端口，不使用代理时为空" />
        </Form.Item>
        <Form.Item
          label="temperature"
          name="temperature"
          rules={[{ required: true, message: '请输入temperature!' }]}
        >
          <InputNumber min={0} max={1} defaultValue={0.7} />
        </Form.Item>
        <Form.Item
          label="上下文对话数量"
          name="maxMessages"
          rules={[{ required: true, message: '请输入上下文对话数量!' }]}
        >
          <InputNumber />
        </Form.Item>
        <Form.Item
          label="触发总结对话数量"
          name="maxSummaryMessages"
          rules={[{ required: true, message: '请输入触发总结对话数量!' }]}
        >
          <InputNumber />
        </Form.Item>
        <Form.Item label="请求最多token" name="maxRequestTokens">
          <InputNumber />
        </Form.Item>
        <Form.Item label="回复最多token" name="maxRespTokens">
          <InputNumber />
        </Form.Item>
        <Form.Item label="流式回复" name="streaming">
          <Switch />
        </Form.Item>
        <Form.Item label="是否参考搜索结果" name="enableSearch">
          <Switch />
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
        headerTitle={<TableTitle title="LLM列表" />}
        columns={columns}
        // 请求数据
        request={async (params) => {
          let res = await getLLMPageList({
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
        title="新增LLM"
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
            status: '1',
            name: 'GPT-3.5-Turbo',
            accessAuthority: '1',
            temperature: 0.7,
            maxMessages: 10,
            maxSummaryMessages: 10,
            maxRequestTokens: 4094,
            maxRespTokens: 4096,
            streaming: false,
            enableSearch: true,
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
          initialValues={{}}
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
