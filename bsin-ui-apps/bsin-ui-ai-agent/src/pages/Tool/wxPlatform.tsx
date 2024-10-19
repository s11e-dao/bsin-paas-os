import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import {
  ProCard,
  ProForm,
  ProFormCheckbox,
  ProFormDatePicker,
  ProFormDateRangePicker,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  StepsForm,
} from '@ant-design/pro-components'
import ProTable from '@ant-design/pro-table'

import defaultFlag from '../../assets/defaultFlag.jpg'
import notDefaultFlag from '../../assets/notDefaultFlag.jpg'
import {
  getWxPlatformPageList,
  getWxPlatformList,
  addWxPlatform,
  editWxPlatform,
  delWxPlatform,
  getWxPlatformDetail,
  startPlatform,
  addWxPlatformMenu,
  getWxPlatformMenuList,
  syncMpMenu,
  removeMpMenu,
} from './service'
import { getCopilotList } from '../Copilot/service'
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
  InputNumber,
} from 'antd'
// import { Icon } from 'antd'
import {
  EditOutlined,
  DeleteOutlined,
  SettingOutlined,
  InboxOutlined,
  CloseCircleOutlined,
  StopTwoTone,
  StarOutlined, //五角星
  PlusOutlined,
  EllipsisOutlined,
  CopyOutlined,
  PlayCircleFilled,
  PlayCircleTwoTone,
  PlayCircleOutlined,
} from '@ant-design/icons'

import { useModel } from 'umi'
const { Meta } = Card
const { Option } = Select
import styles from './index.css'
import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '@/utils/localStorageInfo'
import type { UploadProps } from 'antd'
const { Dragger } = Upload

export default ({ routerChange }) => {

  let bsinFileUploadUrl = process.env.bsinFileUploadUrl
  let tenantAppType = process.env.tenantAppType

  const { TextArea } = Input

  // 记录appSecret是否发生编辑
  const [appSecret, setAppSecret] = useState('')

  // 新增模态框
  const [isAddFormModal, setIsAddFormModal] = useState(false)

  const [oprationPlatformType, setOprationPlatformType] = useState('wechat')

  const [isAddMenuFormModal, setIsAddMenuFormModal] = useState(false)
  // 编辑模态框
  const [isEditFormModal, setIsEditFormModal] = useState(false)
  // 查看模态框
  const [isViewFormModal, setIsViewFormModal] = useState(false)
  // 配置模态框
  const [isSettingFormModal, setIsSettingFormModal] = useState(false)
  // 复制模态框
  const [isCopyFormModal, setIsCopyFormModal] = useState(false)
  // 删除模态框
  const [isDeleteFormModal, setIsDeleteFormModal] = useState(false)
  // 启动模态框
  const [isOperateFormModal, setIsOperateFormModal] = useState(false)

  // 启动模态框
  const [
    isUserAgreementAndPrivacyPolicyFormModal,
    setIsUserAgreementAndPrivacyPolicyFormModal,
  ] = useState(false)

  // 查看登录二维码模态框
  const [isViewLoginQRFormModal, setIsViewLoginQRFormModal] = useState(false)
  const [currentRecord, setCurrentRecord] = useState({})
  const [currentOperation, setCurrentOperation] = useState({})

  // 存储编辑的ID
  const [wxPlatformNo, setWxPlatformNo] = useState('')
  // 获取新增表单信息
  const addFormRef: any = React.createRef()
  const addMenuFormRef: any = React.createRef()

  // 获取编辑表单信息
  const [editFormRef] = Form.useForm()
  // 获取删除表单信息
  const [delFormRef] = Form.useForm()

  // 获取启动表单信息
  const [operationFormRef] = Form.useForm()

  // 获取编辑表单信息
  const [viewFormRef] = Form.useForm()
  // 获取编辑表单信息
  const [settingFormRef] = Form.useForm()
  // 获取编辑表单信息
  const [viewLoginQrFormRef] = Form.useForm()
  // 获取复制表单信息
  const [copyFormRef] = Form.useForm()
  // 获取复制表单信息
  const [userAgreementAndPrivacyPolicyFormRef] = Form.useForm()

  const [merchantInfo, setMerchantInfo] = useState({})
  const [coverImage, setCoverImage] = useState('')
  // 微信平台类别：1、mp(公众号服务订阅号)、2、miniapp(小程序)、 3、cp(企业号|企业微信)、4、pay(微信支付)、5、open(微信开放平台)、6、wechat(个人微信)
  const [tabIndex, setTabIndex] = useState('1')

  // Table action 的引用，便于自定义触发 用于更改数据之后的表单刷新
  const actionRef = React.useRef<ActionType>()

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
        // addFormRef.setFieldValue('coverImage', file?.response.data.url)
        // editFormRef.setFieldValue('coverImage', file?.response.data.url)
        setCoverImage(file?.response.data.url)
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

  const [loading, setLoading] = useState(true)
  const [copilotList, setCopilotList] = useState([])
  const [wxPlatformList, setWxPlatformList] = useState([])
  const [wxMpPlatformList, setMpWxPlatformList] = useState([])
  const [wxPlatformMenuTemplateList, setWxPlatformMenuTemplateList] = useState(
    [],
  )
  const [wxPlatformTypeList, setWxPlatformTypeList] = useState([])

  // 查询Copilot文件列表
  useEffect(() => {
    const merchantInfoTmp = getLocalStorageInfo('merchantInfo')
    console.log('merchantInfo')
    setMerchantInfo(merchantInfoTmp)

    getCopilotList({}).then((res) => {
      if (res.code == 0) {
        setCopilotList(res.data)
        console.log(res.data)
      } else {
        message.error(res.message)
      }
    })

    let mpWxPlatformListTmp: any[] | ((prevState: never[]) => never[]) = []
    let miniappWxPlatformListTmp: any[] | ((prevState: never[]) => never[]) = []
    let cpWxPlatformListTmp: any[] | ((prevState: never[]) => never[]) = []
    let payWxPlatformListTmp: any[] | ((prevState: never[]) => never[]) = []
    let openWxPlatformListTmp: any[] | ((prevState: never[]) => never[]) = []
    let wechatWxPlatformListTmp: any[] | ((prevState: never[]) => never[]) = []
    let wxPlatformMenuTenplateListTmp:
      | any[]
      | ((prevState: never[]) => never[]) = []
    let wxPlatformMenuListTmp: never[] = []

    // 查询协议
    let params = {
      current: '1',
      pageSize: '99',
    }
    getWxPlatformList(params).then((res) => {
      if (res?.code == 0) {
        setWxPlatformList(res?.data)

        res?.data.map((wxPlatform) => {
          if (wxPlatform.type == 'mp') {
            mpWxPlatformListTmp.push(wxPlatform)
          } else if (wxPlatform.type == 'miniapp') {
            miniappWxPlatformListTmp.push(wxPlatform)
          } else if (wxPlatform.type == 'cp') {
            cpWxPlatformListTmp.push(wxPlatform)
          } else if (wxPlatform.type == 'pay') {
            payWxPlatformListTmp.push(wxPlatform)
          } else if (wxPlatform.type == 'open') {
            openWxPlatformListTmp.push(wxPlatform)
          } else if (wxPlatform.type == 'wechat') {
            wechatWxPlatformListTmp.push(wxPlatform)
          } else if (wxPlatform.type == 'menu') {
            wxPlatformMenuTenplateListTmp.push(wxPlatform)
          }
        })

        setWxPlatformMenuTemplateList(wxPlatformMenuTenplateListTmp)

        const wxPlatformTypeList = [
          {
            id: 1,
            name: '订阅|服务号',
            wxPlatformList: mpWxPlatformListTmp,
          },
          {
            id: 2,
            name: '小程序',
            wxPlatformList: miniappWxPlatformListTmp,
          },
          {
            id: 3,
            name: '企业微信',
            wxPlatformList: cpWxPlatformListTmp,
          },
          {
            id: 4,
            name: '微信支付',
            wxPlatformList: payWxPlatformListTmp,
          },
          {
            id: 5,
            name: '微信开发平台',
            wxPlatformList: openWxPlatformListTmp,
          },
          {
            id: 6,
            name: '个人微信',
            wxPlatformList: wechatWxPlatformListTmp,
          },
          {
            id: 7,
            name: '菜单模版',
            wxPlatformList: wxPlatformMenuTenplateListTmp,
          },
          // {
          //   id: 8,
          //   name: '菜单',
          //   wxPlatformList: wxPlatformMenuListTmp,
          // },
        ]

        console.log(wxPlatformTypeList)
        setMpWxPlatformList(mpWxPlatformListTmp)
        setWxPlatformTypeList(wxPlatformTypeList)
      } else {
        message.error(res.message)
      }
      setLoading(false)
    })
  }, [wxPlatformNo, tabIndex])

  // 点击新增
  const confirmAdd = () => {
    addFormRef.current
      .validateFields()
      .then(async () => {
        var formInfo = addFormRef.current?.getFieldsValue()
        if (coverImage != null) {
          formInfo.coverImage = coverImage
        }
        let res = await addWxPlatform({ ...formInfo })
        if (res.code == '000000') {
          message.success('新增成功')
          // 重置表单
          addFormRef.current.resetFields()
          setIsAddFormModal(false)
          actionRef.current?.reload()
          setWxPlatformNo(res.data?.serialNo)
          setCoverImage('')
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
    // 记录原来的值
    setAppSecret(record.appSecret)

    let { serialNo } = record
    let res = await getWxPlatformDetail({ serialNo })
    if (res.code == '000000') {
      editFormRef.setFieldsValue(res.data)
      console.log(res.data)
      setIsEditFormModal(true)
      //刷新用
      setWxPlatformNo('')
    } else {
      message.error('获取数据失败！！')
    }
    // editFormRef.setFieldsValue(record)
    // console.log(record)
    // setIsEditFormModal(true)
  }

  // 编辑确认
  const confirmEdit = () => {
    console.log('confirmEdit')
    editFormRef
      .validateFields()
      .then(async () => {
        var formInfo = editFormRef.getFieldsValue()
        if (formInfo.editable == false) {
          message.warning('该配置不支持编辑！！')
          return
        }
        if (coverImage != null && coverImage != '') {
          formInfo.coverImage = coverImage
        }
        console.log(formInfo)

        // 未发生改变则不修改，清空
        if (formInfo.appSecret == appSecret) {
          formInfo.coverImage = null
        }

        let res = await editWxPlatform({ ...formInfo })
        if (res.code == '000000') {
          setCoverImage('')
          message.success('编辑成功')
          // 重置表单
          editFormRef.resetFields()
          // 用于刷新
          setWxPlatformNo(res.data.serialNo)
          setIsEditFormModal(false)
        } else {
          message.error(res.message)
        }
      })
      .catch(() => {})
  }

  // 点击删除
  const toDelete = (record) => {
    setIsDeleteFormModal(true)
    setWxPlatformNo(record.serialNo)
    // 数据回显
    delFormRef.setFieldsValue(record)
  }

  // 点击删除
  const confirmDel = async () => {
    delFormRef
      .validateFields()
      .then(async () => {
        var response = delFormRef.getFieldsValue()
        if (response.editable == false) {
          message.warning('该配置不支持删除！！')
          return
        }
        let res = await delWxPlatform(response)
        if (res.code == '000000') {
          message.success('删除成功')
          // 重置表单
          delFormRef.resetFields()
          setIsDeleteFormModal(false)
          // 用于刷新
          setWxPlatformNo('')
        } else {
          message.error(res.message)
        }
      })
      .catch(() => {})
  }

  // 取消删除
  const cancelOperate = () => {
    setIsOperateFormModal(false)
    setIsDeleteFormModal(false)
    // 重置表单
    delFormRef.resetFields()
    // 重置表单
    editFormRef.resetFields()
    // 重置表单
    addFormRef.resetFields()
    message.warning('取消删除')
  }

  /**
   * 查看详情
   */
  const toViewWxPlatform = async (record) => {
    let { serialNo, type } = record
    let viewRes = await getWxPlatformDetail({ serialNo })
    // 数据回显
    viewFormRef.setFieldsValue(viewRes.data)
    console.log(viewRes.data)
    setIsViewFormModal(true)
  }

  /**
   * 查看详情
   */
  const toSettingWxPlatform = async (record) => {
    let { serialNo, type } = record
    if (type == 'menu') {
      let viewRes = await getWxPlatformDetail({ serialNo })
      // 数据回显
      settingFormRef.setFieldsValue(viewRes.data)
      routerChange(viewRes.data, 'wxPlatformMenuEdit')
      console.log(viewRes.data)
      // setIsSettingFormModal(true)
    } else {
      message.info('只支持菜单模版配置')
    }
  }

  /**
   * 操作
   */
  const toOperatePlatform = async (record) => {
    setIsOperateFormModal(true)
    setWxPlatformNo(record.serialNo)
    // 数据回显
    operationFormRef.setFieldsValue(record)
  }

  /**
   * 用户协议和隐私政策
   * User Agreement and Privacy Policy
   */
  const toUserAgreementAndPrivacyPolicy = async () => {
    setIsUserAgreementAndPrivacyPolicyFormModal(true)
  }

  const confirmUserAgreementAndPrivacyPolicy = async () => {
    let { serialNo, type, operation } = currentOperation
    let res = await startPlatform({
      serialNo,
      customerNo: merchantInfo?.customerNo,
      operation: operation,
    })
    if (res.code == '000000') {
      message.success('启动成功，请扫码登录')
      // 数据回显
      viewLoginQrFormRef.setFieldsValue(res.data)
      setIsViewLoginQRFormModal(true)
      // setWxPlatformNo('')
      // 重置表单
      setIsOperateFormModal(false)
      setIsUserAgreementAndPrivacyPolicyFormModal(false)
      operationFormRef.resetFields()
    } else {
      message.error(res.message)
    }
  }
  /**
   * 操作
   */
  const confirmOperateWxPlatform = async (record) => {
    operationFormRef
      .validateFields()
      .then(async () => {
        var response = operationFormRef.getFieldsValue()
        let { serialNo, type, operation } = response
        console.log(type)
        setCurrentOperation(response)
        if (type == 'wechat') {
          //微信登录提示风险告知
          if (operation == 'loginWechat') {
            setIsUserAgreementAndPrivacyPolicyFormModal(true)
          } else if (operation == 'logoutWechat') {
            let res = await startPlatform({
              serialNo,
              customerNo: merchantInfo?.customerNo,
              operation: operation,
            })
            if (res.code == '000000') {
              if (operation == 'loginWechat') {
                message.success('启动成功，请扫码登录')
                // 数据回显
                viewLoginQrFormRef.setFieldsValue(res.data)
                setIsViewLoginQRFormModal(true)
                // setWxPlatformNo('')
                // 重置表单
              } else {
                message.success('退出成功')
              }
              setIsOperateFormModal(false)
              operationFormRef.resetFields()
            } else {
              message.error(res.message)
            }
          } else {
            message.info('不支持的操作，个人微信配置才登录登出操作！！')
          }
        } else if (type == 'mp') {
          if (operation == 'syncMpMenu') {
            let res = await syncMpMenu({
              serialNo,
              customerNo: merchantInfo?.customerNo,
              operation: operation,
            })
            if (res.code == '000000') {
              message.success('菜单同步成功')
              setIsOperateFormModal(false)
              operationFormRef.resetFields()
            } else {
              message.error(res.message)
            }
          } else if (operation == 'removeMpMenu') {
            let res = await removeMpMenu({
              serialNo,
              customerNo: merchantInfo?.customerNo,
              operation: operation,
            })
            if (res.code == '000000') {
              message.success('移除菜单成功')
              setIsOperateFormModal(false)
              operationFormRef.resetFields()
            } else {
              message.error(res.message)
            }
          } else {
            message.info('不支持的操作，公众号配置才能同步菜单！！')
          }
        } else {
          message.info('不支持的操作！！')
        }
      })
      .catch(() => {})
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
    currentRecord.appSecret = null
    currentRecord.token = null
    currentRecord.isCopy = true
    console.log(currentRecord)
    let res = await addWxPlatform({ ...currentRecord })
    if (res.code == '000000') {
      message.success('复制成功')
      // 重置表单
      setIsCopyFormModal(false)
      copyFormRef.resetFields()
      setWxPlatformNo(res.data.serialNo)
    } else {
      message.error(res.message)
    }
  }

  const formItemComponent = () => {
    return (
      <>

        <Form.Item
          label="名称"
          name="name"
          rules={[{ required: false, message: '请输入名称!' }]}
        >
          <Input placeholder="起个名字吧！" />
        </Form.Item>

        <Form.Item
          label="平台类型"
          name="type"
          rules={[{ required: true, message: '请选择平台类型!' }]}
        >
          <Select style={{ width: '100%' }}>
            <Option value="">请选择平台类型</Option>
            <Option value="mp">公众号</Option>
            <Option value="miniapp">小程序</Option>
            <Option value="cp">企业微信</Option>
            <Option value="pay">微信支付</Option>
            <Option value="open">微信开放平台</Option>
            <Option value="wechat">个人微信</Option>
            <Option value="menu">菜单模版</Option>
          </Select>
        </Form.Item>

        {/* 企业微信 */}
        {tabIndex == '7' ? (
          <Form.Item
            label="绑定的微信公众号ID"
            name="bindingWxPlatformNo"
            rules={[{ required: true, message: '请选择要绑定微信公众号ID!' }]}
          >
            <Select
              style={{ width: '100%' }}
              onChange={(value) => {}}
              defaultValue=""
            >
              <Option value="">请选择微信公众号平台ID</Option>
              {wxMpPlatformList.map((wxMpPlatform) => {
                return (
                  <Option value={wxMpPlatform?.serialNo}>
                    {wxMpPlatform?.name}
                  </Option>
                )
              })}
            </Select>
          </Form.Item>
        ) : (
          <Form.Item
            label="智能体"
            name="copilotNo"
            rules={[{ required: true, message: '请选择要绑定的智能体!' }]}
            tooltip="该智能体是一个虚拟AI助手,可以设定角色，绑定自己的知识库，设置提示词模版，选择大语言模型..."
          >
            <Select
              style={{ width: '100%' }}
              onChange={(value) => {
                // knowledgeBaseFileChange(value)
              }}
              defaultValue="receiver"
            >
              <Option value="receiver">请选择智能体</Option>
              {copilotList.map((copilot) => {
                return (
                  <Option value={copilot?.serialNo}>{copilot?.name}</Option>
                )
              })}
            </Select>
          </Form.Item>
        )}

        <Form.Item
          label="封面图"
          name="coverImage"
          tooltip="添加或修改上传应用封面图像"
        >
          <Dragger {...uploadProps} listType="picture">
            <p className="ant-upload-drag-icon">
              <InboxOutlined />
            </p>
            <p className="ant-upload-text">点击上传封面</p>
          </Dragger>
        </Form.Item>
        {/* 企业微信 */}
        {tabIndex == '3' ? (
          <>
            <Form.Item
              label="corpID"
              name="corpId"
              tooltip="企业微信后台配置获取"
              rules={[{ required: false, message: '请输入corpID!' }]}
            >
              <Input.Password />
            </Form.Item>
            <Form.Item
              label="agentID"
              name="agentId"
              tooltip="企业微信后台配置获取"
              rules={[{ required: false, message: '请输入agentID!' }]}
            >
              <Input.Password />
            </Form.Item>
          </>
        ) : null}

        {/* 微信公众号 */}
        {tabIndex == '1' ? (
          <>
            <Form.Item
              label="appID"
              name="appId"
              tooltip="微信公众号后台配置获取"
              rules={[{ required: false, message: '请输入appID!' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              label="appSecret"
              name="appSecret"
              tooltip="微信公众号后台配置获取,系统不托管用户秘钥，数据库加密存储，显示数据为加密后的数据，修改则直接覆盖原来的秘钥"
              rules={[{ required: false, message: '请输入appSecret!' }]}
            >
              <Input.Password />
            </Form.Item>
            <Form.Item
              label="aesKey"
              name="aesKey"
              tooltip="微信公众号后台配置获取"
              rules={[{ required: false, message: '请输入aesKey!' }]}
            >
              <Input.Password placeholder="不启用加密，则保持空" />
            </Form.Item>
            <Form.Item
              label="token"
              name="token"
              tooltip="微信公众号后台配置获取"
              rules={[{ required: false, message: '请输入token!' }]}
            >
              <Input.Password />
            </Form.Item>

            <Form.Item
              label="关注回复"
              name="subscribeResponse"
              tooltip="关注公众号后自动回复的消息"
            >
              <TextArea
                placeholder="为空则不会回复"
                autoSize={{ minRows: 2, maxRows: 8 }}
              />
            </Form.Item>

            <Form.Item
              label="菜单模版"
              name="menuNo"
              tooltip="选菜单模版，设置同步公众号首页菜单列表"
            >
              <Select
                style={{ width: '100%' }}
                onChange={(value) => {
                  // wxPlatformMenuTemplateFileChange(value)
                }}
                defaultValue=""
              >
                <Option value="">请选择菜单模版</Option>
                {wxPlatformMenuTemplateList.map((wxPlatformMenuTemplate) => {
                  return (
                    <Option value={wxPlatformMenuTemplate?.serialNo}>
                      {wxPlatformMenuTemplate?.name}
                    </Option>
                  )
                })}
              </Select>
            </Form.Item>

            <Form.Item
              label="预回复"
              name="preResp"
              tooltip="预回复，收到消息后，先回复一条消息:为空则不预回复"
            >
              <TextArea
                placeholder="为空则不启用预回复"
                autoSize={{ minRows: 2, maxRows: 8 }}
              />
            </Form.Item>

            <Form.Item
              label="启用模版"
              name="templateEnable"
              tooltip="使用自定义模版回复"
            >
              <Switch />
            </Form.Item>
            <Form.Item
              label="接口异常回复"
              name="exceptionResp"
              tooltip="copilot接口异常后的回复"
            >
              <TextArea
                placeholder="请输入相关接口异常回复信息"
                autoSize={{ minRows: 2, maxRows: 8 }}
              />
            </Form.Item>
          </>
        ) : null}
        {/* 个人微信 */}
        {tabIndex == '6' ? (
          <>
            <Form.Item
              label="消息前缀"
              name="preResp"
              tooltip="每次回复前自动添加到回复消息的前面(eg. 来自数字分身的回复:)"
            >
              <TextArea
                placeholder="为空则不启用预回复"
                autoSize={{ minRows: 2, maxRows: 8 }}
              />
            </Form.Item>

            <Form.Item
              label="开启群聊@"
              name="groupChat"
              tooltip="此功能开启后，群里有人@自己会回复群里的消息(注：需要购买的服务功能中支持群聊功能才能开启成功)"
            >
              <Switch />
            </Form.Item>

            <Form.Item
              label="历史聊天记录总结"
              name="historyChatSummary"
              tooltip="此功能开启后，会将微信聊天记录加密存储在向量数据库中，这样AI分身jiu具备记忆功能 (注：需要购买的服务功能中支持历史聊天记录总结才能开启成功)"
            >
              <Switch />
            </Form.Item>

            <Form.Item
              label="微信ID"
              name="wxNo"
              tooltip="微信登录后自动获取"
              rules={[{ required: false }]}
            >
              <Input placeholder="微信登录后自动获取" disabled />
            </Form.Item>

            <Form.Item
              label="微信昵称"
              name="nickname"
              tooltip="微信登录后自动获取"
              rules={[{ required: false }]}
            >
              <Input placeholder="微信登录后自动获取" disabled />
            </Form.Item>

            <Form.Item
              label="微信登录UUID"
              name="uuid"
              tooltip="微信登录后自动获取"
              rules={[{ required: false }]}
            >
              <Input placeholder="微信登录后自动获取" disabled />
            </Form.Item>

            <Form.Item
              label="登录状态"
              name="loginStatus"
              tooltip="个人微信登录状态，自动更新"
              rules={[{ required: false }]}
            >
              <Input placeholder="微信登录后自动获取" disabled />
            </Form.Item>
          </>
        ) : (
          <Form.Item
            label="状态"
            name="status"
            rules={[{ required: false, message: '请选择状态!' }]}
          >
            <Radio.Group value="1">
              <Radio value="0">禁用</Radio>
              <Radio value="1">启用</Radio>
            </Radio.Group>
          </Form.Item>
        )}
        {/* 菜单模版 */}
        {tabIndex == '7' ? null : (
          <Form.Item
            label="请求间隔"
            name="requestIntervalLimit"
            tooltip="微信平台自动回复间隔(s)，为空则不限制，防止微信被封，限制请求频率"
            rules={[{ required: false }]}
          >
            <InputNumber placeholder="" step={1} min={0} max={6000} />
          </Form.Item>
        )}

        <Form.Item
          label="是否可编辑"
          name="editable"
          tooltip="若为系统配置不支持编辑"
        >
          <Switch disabled />
        </Form.Item>

        <Form.Item label="是否默认" name="defaultFlag">
          <Switch />
        </Form.Item>

        <Form.Item
          label="描述"
          name="description"
          tooltip="该应用的简单描述信息"
        >
          <TextArea
            placeholder="请输入相关描述信息"
            autoSize={{ minRows: 2, maxRows: 8 }}
          />
        </Form.Item>
        
      </>
    )
  }

  const tabCkickHandler = (params) => {
    console.log(params)
    if (params == 1) {
      setOprationPlatformType('mp')
      // addFormRef.setFieldValue('type', 'mp')
    } else if (params == 2) {
      setOprationPlatformType('miniapp')
      // addFormRef.setFieldValue('type', 'miniapp')
    } else if (params == 3) {
      setOprationPlatformType('cp')
      // addFormRef.setFieldValue('type', 'cp')
    } else if (params == 4) {
      setOprationPlatformType('pay')
      // addFormRef.setFieldValue('type', 'pay')
    } else if (params == 5) {
      setOprationPlatformType('open')
      // addFormRef.setFieldValue('type', 'open')
    } else if (params == 6) {
      setOprationPlatformType('wechat')
      // addFormRef.setFieldValue('type', 'wechat')
    } else if (params == 7) {
      setOprationPlatformType('menu')
      // addFormRef.setFieldValue('type', 'menu')
    } else {
      setOprationPlatformType('')
      // addFormRef.setFieldValue('type', '')
    }
    console.log(oprationPlatformType)
    setTabIndex(params)
  }

  return (
    <Card>
      <Button
        type="primary"
        onClick={() => {
          if (tabIndex == '7') {
            setIsAddFormModal(true)
          } else {
            setIsAddFormModal(true)
          }
        }}
        className={styles.btn}
      >
        新增
      </Button>
      <Descriptions title="我的微信平台"></Descriptions>
      <Tabs
        defaultActiveKey={tabIndex}
        onTabClick={(params) => tabCkickHandler(params)}
      >
        <>
          {wxPlatformTypeList.map((wxPlatformType) => (
            <Tabs.TabPane tab={wxPlatformType.name} key={wxPlatformType.id}>
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
                  dataSource={wxPlatformType.wxPlatformList}
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
                                src={
                                  item.coverImage == null
                                    ? 'https://s11edao.oss-cn-beijing.aliyuncs.com/logo/BsinCopilot.png'
                                    : item.coverImage
                                }
                              />
                            }
                            actions={[
                              <PlayCircleOutlined
                                key="start"
                                title="启动"
                                onClick={() => {
                                  toOperatePlatform(item)
                                }}
                              />,
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
                                  // toViewWxPlatform(item)
                                  toSettingWxPlatform(item)
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
        </>
      </Tabs>
      {/* 新增模态框 */}
      <Modal
        title="新增"
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
            // type: oprationPlatformType,
            type: '',
            status: '1',
            templateEnable: '0',
            groupChat: false,
            historyChatSummary: false,
          }}
        >
          {formItemComponent}
        </Form>
      </Modal>
      {/* 编辑模态框 */}
      <Modal
        title="编辑"
        open={isEditFormModal}
        onOk={confirmEdit}
        onCancel={() => {
          setIsEditFormModal(false)
        }}
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
        title="查看"
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

      {/* 查看登录二维码模态框 */}
      <Modal
        title="微信扫一扫登录"
        open={isViewLoginQRFormModal}
        onOk={() => setIsViewLoginQRFormModal(false)}
        onCancel={() => setIsViewLoginQRFormModal(false)}
        centered
      >
        <img
          className="loginQrUrl"
          alt=""
          src={viewLoginQrFormRef.getFieldValue('loginQrUrl')}
        />
      </Modal>

      {/* 删除 */}
      <Modal
        title="删除配置"
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
          initialValues={{}}
        >
          {/* 个人微信 */}
          {tabIndex == '6' ? (
            <>
              <Descriptions title="提醒：已登录过的微信配置删除后不支持再次创建，再次创建需要额外收费！！！"></Descriptions>
            </>
          ) : null}

          <Form.Item label="WxPlatformID" name="serialNo">
            <Input disabled />
          </Form.Item>
          <Form.Item label="WxPlatform名称" name="name">
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="平台类型"
            name="type"
            rules={[{ required: true, message: '请选择平台类型!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="mp">公众号</Option>
              <Option value="miniapp">小程序</Option>
              <Option value="cp">企业微信</Option>
              <Option value="pay">微信支付</Option>
              <Option value="open">微信开放平台</Option>
              <Option value="wechat">个人微信</Option>
            </Select>
          </Form.Item>
          <Form.Item label="是否可编辑" name="editable">
            <Switch disabled />
            ``{' '}
          </Form.Item>
        </Form>
      </Modal>

      {/* 启动 */}
      <Modal
        title="操作应用"
        open={isOperateFormModal}
        onOk={confirmOperateWxPlatform}
        onCancel={() => setIsOperateFormModal(false)}
        centered
      >
        <Form
          name="basic"
          form={operationFormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          initialValues={{ operation: 'loginWechat' }}
        >
          <Form.Item label="WxPlatformID" name="serialNo">
            <Input disabled />
          </Form.Item>
          <Form.Item label="WxPlatform名称" name="name">
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="平台类型"
            name="type"
            rules={[{ required: true, message: '请选择平台类型!' }]}
          >
            <Select style={{ width: '100%' }} disabled>
              <Option value="mp">公众号</Option>
              <Option value="miniapp">小程序</Option>
              <Option value="cp">企业微信</Option>
              <Option value="pay">微信支付</Option>
              <Option value="open">微信开放平台</Option>
              <Option value="wechat">个人微信</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="操作类型"
            name="operation"
            rules={[{ required: true, message: '请选择操作类型!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="loginWechat">登录微信分身</Option>
              <Option value="logoutWechat">退出微信分身</Option>
              <Option value="syncMpMenu">同步公众号菜单</Option>
              <Option value="removeMpMenu">移除公众号菜单</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看模态框 */}
      <Modal
        title="复制配置"
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
      {/* 用户协议与隐私政策模态框 */}
      <Modal
        width={620}
        title="用户协议与隐私政策"
        open={isUserAgreementAndPrivacyPolicyFormModal}
        onCancel={() => setIsUserAgreementAndPrivacyPolicyFormModal(false)}
        centered
        footer={
          [] // 设置footer为空，去掉 取消 确定默认按钮
        }
      >
        <ProCard>
          <StepsForm<{
            name: string
          }>
            onFinish={async (values) => {
              console.log(values)
              confirmUserAgreementAndPrivacyPolicy()
              // message.success('提交成功')
            }}
            formProps={{
              validateMessages: {
                required: '此项为必填项',
              },
            }}
            submitter={{
              render: (props) => {
                if (props.step === 0) {
                  return (
                    <Button type="primary" onClick={() => props.onSubmit?.()}>
                      隐私政策 {'>'}
                    </Button>
                  )
                }

                if (props.step === 1) {
                  return [
                    <Button key="pre" onClick={() => props.onPre?.()}>
                      用户协议
                    </Button>,
                    <Button
                      type="primary"
                      key="goToTree"
                      onClick={() => props.onSubmit?.()}
                    >
                      同意启动 {'>'}
                    </Button>,
                  ]
                }
              },
            }}
          >
            <StepsForm.StepForm<{
              name: string
            }>
              name="base"
              title="用户协议"
              onFinish={async ({ name }) => {
                console.log(name)
                return true
              }}
            >
              <div style={{marginBottom: "20px"}}>1、需要您微信扫码授微信登录至服务器</div>
            </StepsForm.StepForm>
            <StepsForm.StepForm<{
              checkbox: string
            }>
              name="checkbox"
              title="隐私政策"
            >
              <div style={{marginBottom: "20px"}}>
                1、使用微信扫码登录后，我们只会对您的聊天信息进行转发，并请求大语言模型<br/>
                2、使用中会获取您的微信昵称<br/>
                3、当您开通群聊功能后，当群内有人@您时，会处理群众信息<br/>
                4、当您开通历史聊天记录总结后，会将关键的聊天信息加密存储在向量数据库中，用于打造永久分身机器人
              </div>
            </StepsForm.StepForm>
          </StepsForm>
        </ProCard>
      </Modal>
    </Card>
  )
}
