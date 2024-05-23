import columnsData, { AppColumnsItem } from './data'
import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'

import defaultFlag from '../../assets/defaultFlag.jpg'
import notDefaultFlag from '../../assets/notDefaultFlag.jpg'
// import ChatWithCopilot from './components/chatWithCopilot'
import {
  getCopilotList,
  delCopilot,
  addCopilot,
  editCopilot,
  getCopilotDetail,
  getCopilotPageList,
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
  Descriptions,
  Upload,
  List,
  Space,
  Select,
  Radio,
  Avatar,
  Switch,
  Tabs,
} from 'antd'
const { Meta } = Card
const { Option } = Select

import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '../../utils/localStorageInfo'

import type { UploadProps } from 'antd'
import {
  EditOutlined,
  EllipsisOutlined,
  DeleteOutlined,
  SettingOutlined,
  InboxOutlined,
  CopyOutlined,
  CloseCircleOutlined,
  PlusOutlined,
} from '@ant-design/icons'

import { useModel } from 'umi'
import styles from './index.css'

// 样式设计参考：https://fastgpt.run/dataset/list

const { Dragger } = Upload

export default ({ addCurrentRecord, addCopilotList }) => {
  let bsinFileUploadUrl = process.env.bsinFileUploadUrl
  let tenantAppType = process.env.tenantAppType

  const { TextArea } = Input

  const [loading, setLoading] = useState(true)

  // 新增模态框
  const [isAddFormModal, setIsAddFormModal] = useState(false)
  // 编辑模态框
  const [isEditFormModal, setIsEditFormModal] = useState(false)
  // 删除模态框
  const [isDeleteFormModal, setIsDeleteFormModal] = useState(false)
  // 复制模态框
  const [isCopyFormModal, setIsCopyFormModal] = useState(false)

  // 存储编辑的ID
  const [copilotNo, setCopilotNo] = useState('')
  // 获取新增表单信息
  const addFormRef: any = React.createRef()
  // 获取编辑表单信息
  const [editFormRef] = Form.useForm()
  // 获取编辑表单信息
  const [delFormRef] = Form.useForm()
  // 获取复制表单信息
  const [copyFormRef] = Form.useForm()

  const [copilotList, setCopilotList] = useState([])
  const [brandOfficerCopilotList, setBrandOfficerCopilotList] = useState([])
  const [digitalAvatarCopilotList, setDigitalAvatarCopilotList] = useState([])
  const [universalCopilotList, setUniversalCopilotList] = useState([])
  const [copilotTypeList, setCopilotTypeList] = useState([])
  const [avatar, setAvatar] = useState('')
  const [currentRecord, setCurrentRecord] = useState({})

  // const copilotTypeList = [
  //   { id: 1, name: '品牌官', copilotList: [] },
  //   { id: 2, name: '数字分身', copilotList: [] },
  //   { id: 3, name: '通用copilot', copilotList: [] },
  // ]
  useEffect(() => {
    // 查询协议
    let params = {
      current: '1',
      pageSize: '99',
    }
    getCopilotPageList(params).then((res) => {
      if (res?.code == '000000') {
        setCopilotList(res?.data)
        addCopilotList(res?.data)

        let brandOfficerCopilotListTmp:
          | any[]
          | ((prevState: never[]) => never[]) = []
        let digitalAvatarCopilotListTmp:
          | any[]
          | ((prevState: never[]) => never[]) = []
        let universalCopilotListTmp:
          | any[]
          | ((prevState: never[]) => never[]) = []
        res?.data.map((copilot) => {
          if (copilot.type == '1') {
            brandOfficerCopilotListTmp.push(copilot)
          } else if (copilot.type == '2') {
            digitalAvatarCopilotListTmp.push(copilot)
          } else if (copilot.type == '3') {
            universalCopilotListTmp.push(copilot)
          }
        })
        setBrandOfficerCopilotList(brandOfficerCopilotListTmp)
        setDigitalAvatarCopilotList(digitalAvatarCopilotListTmp)
        setUniversalCopilotList(universalCopilotListTmp)
        const copilotTypeList = [
          { id: 1, name: '品牌官', copilotList: brandOfficerCopilotListTmp },
          { id: 2, name: '数字分身', copilotList: digitalAvatarCopilotListTmp },
          { id: 3, name: '通用copilot', copilotList: universalCopilotListTmp },
        ]
        setCopilotTypeList(copilotTypeList)
      }
      setLoading(false)
    })
  }, [copilotNo])

  // 点击新增
  const confirmAdd = () => {
    addFormRef.current
      .validateFields()
      .then(async () => {
        var response = addFormRef.current?.getFieldsValue()
        if (avatar != null) {
          response.avatar = avatar
        }
        let res = await addCopilot({ ...response })
        if (res.code == '000000') {
          message.success('新增成功')
          // 重置表单
          addFormRef.current.resetFields()
          setIsAddFormModal(false)
          setAvatar('')
          setCopilotNo(res.data?.serialNo)
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
    setCopilotNo(record.serialNo)
    setIsEditFormModal(true)
    // 数据回显
    editFormRef.setFieldsValue(record)
  }

  // 编辑确认
  const confirmEdit = async (record) => {
    editFormRef
      .validateFields()
      .then(async () => {
        var response = editFormRef.getFieldsValue()
        let res = await editCopilot(response)
        if (res.code == '000000') {
          message.success('编辑成功')
          setAvatar('')
          // 重置表单
          editFormRef.resetFields()
          setIsEditFormModal(false)
          // 用于刷新
          setCopilotNo('')
        } else {
          message.error(res.message)
        }
      })
      .catch(() => {})
  }

  // 点击删除
  const toDelete = (record) => {
    if (record.editable == false) {
      message.warning('该配置不支持删除！！')
      return
    }
    setIsDeleteFormModal(true)
    setCopilotNo(record.serialNo)
    // 数据回显
    delFormRef.setFieldsValue(record)
  }

  // 确认删除
  const confirmDel = async () => {
    delFormRef
      .validateFields()
      .then(async () => {
        var response = delFormRef.getFieldsValue()
        if (response.editable == false) {
          message.warning('该配置不支持删除！！')
          return
        }
        let res = await delCopilot(response)
        if ((res.data = '000000')) {
          message.success('删除成功')
          // 重置表单
          delFormRef.resetFields()
          setIsDeleteFormModal(false)
          // 用于刷新
          setCopilotNo('')
        } else {
          message.error(res.message)
        }
      })
      .catch(() => {})
  }

  // 取消删除
  const cancelDel = () => {
    message.warning('取消删除')
  }

  /**
   * 查看详情
   */
  const toDetail = async (record) => {
    addCurrentRecord(record)
  }

  /**
   * 复制
   */
  const toCopy = async (record) => {
    setCurrentRecord(record)
    copyFormRef.setFieldsValue(record)
    setIsCopyFormModal(true)
  }
  // 确认复制
  const confirmCopy = async () => {
    currentRecord.tenantId = null
    currentRecord.merchantNo = null
    currentRecord.customerNo = null
    console.log(currentRecord)
    let res = await addCopilot({ ...currentRecord })
    if (res.code == '000000') {
      message.success('复制成功')
      // 重置表单
      setIsCopyFormModal(false)
      copyFormRef.resetFields()
      setCopilotNo(res.data?.serialNo)
    } else {
      message.error(res.message)
    }
  }

  // 上传封面图片
  const uploadProps: UploadProps = {
    name: 'file',
    headers: {
      Authorization: getSessionStorageInfo('token')?.token,
    },
    action: bsinFileUploadUrl,
    data: {
      // currentPath: 'fileNo', //为空则使用CustomerNo作为文件夹
      tenantAppType: tenantAppType,
      thumbnailSize: '200,200', //缩略图尺寸，为空则无缩略图
      // imgSize: '200,200', //存储尺寸，为空则原图尺寸储存
    },
    // 只能上传一个
    maxCount: 1,
    onChange(info) {
      // 控制path是否显示
      console.log(info)
      // 是加载
      let { file } = info
      if (file?.status === 'done') {
        console.log(file.response)
        message.success(`${info.file.name} file uploaded successfully.`)
        // addFormRef.setFieldValue('avatar', file?.response.data.url)
        // editFormRef.setFieldValue('avatar', file?.response.data.url)
        setAvatar(file?.response.data.url)
      } else if (file?.status === 'error') {
        message.error(`${info.file.name} file upload failed.`)
      }
    },
    // 拖拽
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files)
    },
    onRemove(e) {},
  }
  const formItemComponent = () => {
    return (
      <>
        <Form.Item label="CopilotID" name="serialNo">
          <Input disabled />
        </Form.Item>
        <Form.Item
          label="Copilot名称"
          name="name"
          rules={[{ required: true, message: '请输入Copilot名称!' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="类型"
          name="type"
          rules={[{ required: true, message: '请选择Copilot类型!' }]}
        >
          <Select style={{ width: '100%' }}>
            <Option value="1">品牌官</Option>
            <Option value="2">数字分身</Option>
            <Option value="3">通用copilot</Option>
          </Select>
        </Form.Item>
        <Form.Item label="头像" name="avatar">
          <Input />
        </Form.Item>
        <Form.Item
          label="访问权限"
          name="accessAuthority"
          rules={[{ required: true, message: '请选择访问权限!' }]}
        >
          <Radio.Group value="0">
            <Radio value="1">private</Radio>
            <Radio value="2">public</Radio>
          </Radio.Group>
        </Form.Item>
        <Form.Item label="默认Copilot" name="defaultFlag">
          <Switch />
        </Form.Item>
        <Form.Item
          label="流式回复"
          name="streaming"
          tooltip="和绑定的llm相关,不可编辑"
        >
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
    <Card>
      <Button
        type="primary"
        onClick={() => {
          console.log('')
          setIsAddFormModal(true)
        }}
        className={styles.btn}
      >
        新增
      </Button>
      <Descriptions title="我的Copilot"></Descriptions>
      <Tabs defaultActiveKey="1">
        {copilotTypeList.map((type) => (
          <Tabs.TabPane tab={type.name} key={type.id}>
            <Space
              size="middle"
              direction={'vertical'}
              style={{ display: 'flex', flexWrap: 'wrap' }}
            >
              <List
                loading={loading}
                rowKey="id"
                grid={{
                  gutter: 16,
                  xs: 1,
                  sm: 2,
                  md: 2,
                  lg: 3,
                  xl: 4,
                  xxl: 5,
                }}
                dataSource={type.copilotList}
                renderItem={(item) => {
                  if (item && item.serialNo) {
                    return (
                      <List.Item key={item.serialNo}>
                        <Card
                          style={{ width: 300 }}
                          cover={
                            <img
                              style={{ width: '100%', height: '260px' }}
                              alt="example"
                              src={item.avatar}
                            />
                          }
                          actions={[
                            <EditOutlined
                              key="edit"
                              title="编辑"
                              onClick={() => {
                                toEdit(item)
                              }}
                            />,
                            <EllipsisOutlined
                              key="ellipsis"
                              title="详情"
                              onClick={() => {
                                toDetail(item)
                              }}
                            />,
                            <CopyOutlined
                              key="copy"
                              title="复制"
                              onClick={() => {
                                toCopy(item)
                              }}
                            />,
                            <DeleteOutlined
                              key="delete"
                              title="删除"
                              onClick={() => {
                                toDelete(item)
                              }}
                            />,
                          ]}
                        >
                          <Meta
                            avatar={
                              <Avatar
                                src={
                                  item.defaultFlag == true
                                    ? defaultFlag
                                    : notDefaultFlag
                                }
                              />
                            }
                            title={item.name}
                            description={item.description}
                          />
                        </Card>
                      </List.Item>
                    )
                  }
                }}
              />
            </Space>
          </Tabs.TabPane>
        ))}
      </Tabs>
      {/* 新增Copilot */}
      <Modal
        width={690}
        title="创建一个Copilot"
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
          initialValues={{ type: '3' }}
        >
          <Form.Item
            label="Copilot类型"
            name="type"
            rules={[{ required: true, message: '请选择Copilot类型!' }]}
          >
            {/* copilot类型：1、品牌官 2、数字分身 3、知识库问答机器人 */}
            <Select style={{ width: '100%' }}>
              <Option value="1">品牌官</Option>
              <Option value="2">数字分身</Option>
              <Option value="3">知识库问答机器人</Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="Copilot名字"
            name="name"
            rules={[{ required: true, message: '请输入Copilot名字!' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item label="封面图" name="avatar">
            <Dragger {...uploadProps} listType="picture">
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击上传封面</p>
            </Dragger>
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
        title="编辑Copilot"
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
          initialValues={{ type: '3' }}
        >
          {formItemComponent}
        </Form>
      </Modal>
      {/* 删除 */}
      <Modal
        title="删除Copilot"
        open={isDeleteFormModal}
        onOk={confirmDel}
        onCancel={() => setIsDeleteFormModal(false)}
        centered
      >
        <Form
          name="basic"
          form={delFormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          initialValues={{ remember: true }}
        >
          {formItemComponent}
        </Form>
      </Modal>
      {/* 查看模态框 */}
      <Modal
        title="复制copilot"
        open={isCopyFormModal}
        onOk={confirmCopy}
        onCancel={() => setIsCopyFormModal(false)}
        centered
      >
        <Form
          name="basic"
          form={copyFormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          initialValues={{}}
        >
          {formItemComponent}
        </Form>
      </Modal>
    </Card>
  )
}
