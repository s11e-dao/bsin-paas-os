import columnsData, { AppColumnsItem } from './data'
import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
import {
  getEmbeddingModelPageList,
  getEmbeddingModelList,
  delEmbeddingModel,
  addEmbeddingModel,
  editEmbeddingModel,
  getEmbeddingModelDetail,
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

const { TextArea } = Input

export const embeddingFormItemComponent = () => {
  return (
    <>
      <Form.Item label="ID" name="serialNo">
        <Input disabled />
      </Form.Item>
      <Form.Item
        label="名称"
        name="name"
        rules={[{ required: true, message: '请输入EmbeddingModel名称!' }]}
      >
        {/* 索引模型类型：1、embedding-2  2、M3E  3、AllMiniLmL6V2  4、BgeSmallZh 5、QwenTextEmbeddingV2 */}
        <Select value="AllMiniLmL6V2">
          {/* <Option value="embedding-2">embedding-2</Option> */}
          {/* <Option value="M3E">M3E</Option> */}
          <Option value="AllMiniLmL6V2">AllMiniLmL6V2</Option>
          <Option value="BgeSmallZh">BgeSmallZh</Option>
          <Option value="QwenTextEmbeddingV2">QwenTextEmbeddingV2</Option>
        </Select>
      </Form.Item>
      <Form.Item
        label="状态"
        name="status"
        rules={[{ required: true, message: '请选择EmbeddingModel状态!' }]}
      >
        <Radio.Group value="1">
          <Radio value="0">禁用</Radio>
          <Radio value="1">启用</Radio>
        </Radio.Group>
      </Form.Item>
      <Form.Item
        label="访问权限"
        name="accessAuthority"
        rules={[{ required: true, message: '请选择EmbeddingModel访问权限!' }]}
      >
        <Radio.Group value="1">
          <Radio value="1">private</Radio>
          <Radio value="2">public</Radio>
        </Radio.Group>
      </Form.Item>
      <Form.Item
        label="检索方式"
        name="retrievalMethod"
        rules={[{ required: true, message: '请选择检索方式!' }]}
      >
        <Select value="1">
          <Option value="1">语义检索</Option>
          <Option value="2">增强语义检索</Option>
          <Option value="3">混合检索</Option>
        </Select>
      </Form.Item>

      <Form.Item
        label="分词模型"
        name="tokenizerModel"
        rules={[{ required: true, message: '请选择分词模型!' }]}
      >
        {/* TODO: */}
        <Select value="1">
          <Option value="OpenAi">OpenAi</Option>
          <Option value="Bert">Bert</Option>
          <Option value="Qwen">Qwen</Option>
        </Select>
      </Form.Item>

      <Form.Item
        label="文档分词器"
        name="documentSplitter"
        rules={[{ required: true, message: '请选择文档分词器!' }]}
      >
        {/* 文档分词器：1、ByCharacter 2、ByLine  3、ByParagraph 4、ByRegex 5、BySentence 6、ByWord */}
        <Select value="ByCharacter">
          <Option value="ByCharacter">ByCharacter</Option>
          <Option value="ByLine">ByLine</Option>
          <Option value="ByParagraph">ByParagraph</Option>
          <Option value="ByRegex">ByRegex</Option>
          <Option value="BySentence">BySentence</Option>
          <Option value="ByWord">ByWord</Option>
        </Select>
      </Form.Item>

      <Form.Item
        label="topK"
        name="maxResults"
        rules={[{ required: true, message: '请输入最多检索结果!' }]}
      >
        <InputNumber />
      </Form.Item>
      <Form.Item
        label="相似度"
        name="minScore"
        rules={[{ required: true, message: '请输入相似度!' }]}
      >
        <InputNumber step={0.1} />
      </Form.Item>
      <Form.Item
        label="引用上限"
        name="quoteLimit"
        rules={[{ required: true, message: '请输入引用上限!' }]}
      >
        <InputNumber />
      </Form.Item>

      <Form.Item
        label="空搜索回复"
        name="emptyResp"
        rules={[{ message: '请输入空搜索回复!' }]}
      >
        <Input placeholder="无回复，保持空即可" />
      </Form.Item>
      <Form.Item
        label="向量维度"
        name="dimension"
        rules={[{ required: true, message: '请选择向量维度!' }]}
      >
        <Select value="512" disabled>
          <Option value="384">384</Option>
          <Option value="512">512</Option>
        </Select>
      </Form.Item>

      <Form.Item
        label="apiKey"
        name="apiKey"
        rules={[{ message: '请输入apiKey!' }]}
        tooltip="使用openAI|Qwen embedding模型需要根据情况填写"
      >
        <Input.Password />
      </Form.Item>

      <Form.Item
        label="apiBaseUrl"
        name="apiBaseUrl"
        rules={[{ message: '请输入apiBaseUrl!' }]}
        tooltip="使用openAI|Qwen embedding模型需要根据情况填写"
      >
        <Input />
      </Form.Item>

      <Form.Item
        label="proxyUrl"
        name="proxyUrl"
        rules={[{ message: '请输入proxyUrl!' }]}
        tooltip="使用openAI|Qwen embedding模型需要根据情况填写"
      >
        <Input />
      </Form.Item>

      <Form.Item
        label="proxyPort"
        name="proxyPort"
        rules={[{ message: '请输入proxyPort!' }]}
        tooltip="使用openAI|Qwen embedding模型需要根据情况填写"
      >
        <Input />
      </Form.Item>

      <Form.Item
        label="分段最多token数"
        name="segmentSizeInTokens"
        rules={[{ required: true, message: '请输入分段最多token数!' }]}
      >
        <InputNumber />
      </Form.Item>
      <Form.Item
        label="最大重叠token数"
        name="overlapSizeInTokens"
        rules={[{ required: true, message: '请输入最大重叠token数!' }]}
      >
        <InputNumber />
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

export default () => {
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
          toViewEmbeddingModel(record)
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
        let res = await addEmbeddingModel({ ...response })
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
    setApiKey(record.apiKey)
    let { serialNo } = record
    let viewRes = await getEmbeddingModelDetail({ serialNo })
    editFormRef.setFieldsValue(viewRes.data)
    console.log(viewRes.data)
    setIsEditFormModal(true)
  }

  // 编辑确认
  const confirmEdit = (record) => {
    editFormRef
      .validateFields()
      .then(async () => {
        var formInfo = editFormRef.getFieldsValue()
        if (apiKey == formInfo.apiKey) {
          formInfo.apiKey = null
        }
        // formInfo.serialNo = record.serialNo
        let res = await editEmbeddingModel(formInfo)
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
    let res = await delEmbeddingModel({ serialNo })
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
  const toViewEmbeddingModel = async (record) => {
    let { serialNo } = record
    let viewRes = await getEmbeddingModelDetail({ serialNo })
    // 数据回显
    viewFormRef.setFieldsValue(viewRes.data)
    console.log(viewRes.data)
    setIsViewFormModal(true)
  }

  return (
    <div>
      {/* 表格 */}
      <ProTable<AppColumnsItem>
        actionRef={actionRef}
        scroll={{ x: 900 }}
        bordered
        headerTitle={<TableTitle title="索引模型列表" />}
        columns={columns}
        // 请求数据
        request={async (params) => {
          let res = await getEmbeddingModelPageList({
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
          {embeddingFormItemComponent}
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
          {embeddingFormItemComponent}
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
          {embeddingFormItemComponent}
        </Form>
      </Modal>
    </div>
  )
}
