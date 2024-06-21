import React, { useState, useRef, useEffect } from 'react'
import type { ProFormInstance } from '@ant-design/pro-form'
import { EditableProTable } from '@ant-design/pro-table'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProForm, {
  StepsForm,
  ProFormDigit,
  ProFormText,
  ProFormTextArea,
  ProFormSelect,
  ProFormCheckbox,
  ProFormCheckboxGroupProps,
  ProFormDateRangePicker,
  // ProCard,
} from '@ant-design/pro-form'
import {
  Modal,
  message,
  Button,
  Row,
  Col,
  Card,
  Result,
  Tooltip,
  Form,
  Space,
} from 'antd'
import {
  PlusOutlined,
  QuestionCircleOutlined,
  InboxOutlined,
} from '@ant-design/icons'
import {
  getLocalStorageInfo,
  setLocalStorageInfo,
} from '@/utils/localStorageInfo'
import styles from '../../../Setting/components/baseView.less'

import wechatPayQrCode from '../../../../assets/wechatPayQrCode.jpg'
import aliPayQrCode from '../../../../assets/aliPayQrCode.jpg'

import ImageViewAndUpload from '../../../../components/ImageViewAndUpload'
import {
  getMerchantAuthorizableAppList,
  subscribeApps,
  getServiceSubscribePageList,
  getAiFunctionSubscribePageList,
  getAiFunctionSubscribeDetail,
  getAiFunctionSubscribablePageList,
  createFunctionServiceOrder,
} from './service'

const waitTime = (time: number = 100) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(true)
    }, time)
  })
}

export default ({ serviceAppRecord, setCurrentContent }) => {
  // 可订阅功能集合
  const [subscribableFunctionList, setSubscribableFunctionList] = useState([])
  const [
    subscribableFunctionShowList,
    setSubscribableFunctionShowList,
  ] = useState([])
  const [
    initialSubscribableFunction,
    setInitialSubscribableFunction,
  ] = useState('您还未创建功能套餐！')

  const [payReceipt, setPayReceipt] = useState<string>()

  const [selectedFunction, setSelectedFunction] = useState({})

  const [functionForm] = Form.useForm()

  const setUploadAvatarUrl = (
    payReceipt: React.SetStateAction<string | undefined>,
  ) => {
    setPayReceipt(payReceipt)
  }
  useEffect(() => {
    // 查询合约模板协议
    let params = {
      current: '1',
      pageSize: '99',
    }

    // 配置提示信息
    const menuSortText = (
      <>
        <div>主菜单：大菜单</div>
        <div>菜单：大菜单内的子菜单</div>
        <div>按钮：配置按钮</div>
      </>
    )

    // // 查询商户可授权应用
    // let params1 = {
    //   orgCode: getLocalStorageInfo('merchantInfo')?.merchantName,
    // }
    // getMerchantAuthorizableAppList(params1).then((res) => {
    //   console.log(res?.data)
    //   let typeNoListTemp = []
    //   if (res?.code == '000000') {
    //     res?.data.map((item) => {
    //       console.log(item)
    //       let typeNoJson = {
    //         label: item.appName,
    //         value: item.appId,
    //       }
    //       typeNoListTemp.push(typeNoJson)
    //     })
    //     setSubscribableFunctionList(typeNoListTemp)
    //   }

    // 查询商户可授权应用
    getAiFunctionSubscribablePageList(params).then((res) => {
      console.log(res?.data)
      if (res?.code == '000000') {
        let subscribableFunctionListTmp = []
        res?.data.map((subscribableFunction) => {
          let subscribableFunctionTmp = {
            value: subscribableFunction.serialNo,
            label: subscribableFunction.name,
          }
          // if (subscribableFunction.serialNo == knowledgeBaseInfo?.record.subscribableFunctionNo) {
          //   setSelectedFunction(subscribableFunction)
          //   setInitialSubscribableFunction(subscribableFunction.serialNo)
          //   refreshFunctionForm(subscribableFunction)
          // }
          subscribableFunctionListTmp.push(subscribableFunctionTmp)
        })
        setSubscribableFunctionShowList(subscribableFunctionListTmp)
        setSubscribableFunctionList(res?.data)
      }
    })
  }, [])

  const functionNoChange = (e: Event) => {
    console.log(e)
    subscribableFunctionList.map((subscribableFunction) => {
      console.log(subscribableFunction.serialNo)
      if (subscribableFunction?.serialNo == e) {
        console.log('匹配')
        setSelectedFunction(subscribableFunction)
        refreshFunctionForm(subscribableFunction)
      }
    })
  }
  const refreshFunctionForm = (nodeInfo: object) => {
    functionForm.resetFields()
    functionForm.setFieldsValue(nodeInfo)
  }

  return (
    <>
      <Card
        title="功能订阅"
        extra={
          <Button
            type="primary"
            onClick={() => {
              setCurrentContent('subscribeList')
            }}
          >
            返回
          </Button>
        }
      >
        <StepsForm<{
          name: string
        }>
          onFinish={async (values) => {
            console.log(values)
            // await waitTime(1000)
            if (payReceipt == null || payReceipt == '') {
              message.warning('请上传支付凭证截图')
            } else {
              selectedFunction.customerNo = null
              selectedFunction.tenantId = null
              selectedFunction.merchantNo = null
              createFunctionServiceOrder({
                ...selectedFunction,
                payReceipt: payReceipt,
              })
              message.success('提交成功，待审核通过后即可使用')
            }
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
                    用户政策 {'>'}
                  </Button>
                )
              }

              if (props.step === 1) {
                return [
                  <Button key="pre" onClick={() => props.onPre?.()}>
                    返回功能订阅
                  </Button>,
                  <Button
                    type="primary"
                    key="goToTwo"
                    onClick={() => props.onSubmit?.()}
                  >
                    去支付 {'>'}
                  </Button>,
                ]
              }
              if (props.step === 2) {
                return [
                  <Button key="pre" onClick={() => props.onPre?.()}>
                    返回支付
                  </Button>,
                  <Button
                    type="primary"
                    key="goToFour"
                    onClick={() => props.onSubmit?.()}
                  >
                    支付完成 {'>'}
                  </Button>,
                ]
              }

              return [
                <Button key="pre" onClick={() => props.onPre?.()}>
                  {'<'} 返回支付界面
                </Button>,
                <Button
                  type="primary"
                  key="goToFour"
                  onClick={() => props.onSubmit?.()}
                >
                  提交 √
                </Button>,
              ]
            },
          }}
        >
          <StepsForm.StepForm<{
            name: string
          }>
            name="subscribeFunction"
            title="订阅功能"
            onFinish={async ({ name }) => {
              console.log(name)
              // await waitTime(2000)
              return true
            }}
          >
            <ProFormSelect
              width="sm"
              name="serialNo"
              label="功能列表"
              tooltip="根据自己需求，选择订阅套餐"
              rules={[
                {
                  required: true,
                  message: '请选择订阅的服务套餐!',
                },
              ]}
              onChange={(e) => {
                functionNoChange(e)
              }}
              // initialValue={initialSubscribableFunction}
              options={subscribableFunctionShowList}
            />
            <ProForm
              layout="vertical"
              initialValues={{
                ...selectedFunction,
              }}
              form={functionForm}
              submitter={{
                // 配置按钮文本
                searchConfig: {
                  resetText: '重置',
                  submitText: '提交',
                },
                // 配置按钮的属性
                resetButtonProps: {
                  style: {
                    // 隐藏重置按钮
                    display: 'none',
                  },
                },
                submitButtonProps: {},

                // 完全自定义整个区域
                render: (props, doms) => {
                  console.log(props)
                  return []
                },
              }}
            >
              <ProFormText width="md" name="serialNo" label="订单ID" disabled />
              <ProForm.Group title="" size={3}>
                <ProFormText
                  width="md"
                  name="copilotNum"
                  label="智能体数量"
                  tooltip="可创建智能体数量"
                  disabled
                />
                <ProFormText
                  width="md"
                  name="knowledgeBaseNum"
                  label="知识库数量"
                  tooltip="可创建个人知识库数量"
                  disabled
                />
                <ProFormText
                  width="md"
                  name="knowledgeBaseFileNum"
                  label="知识库文件数量"
                  tooltip="每个知识库可添加的文件数量"
                  disabled
                />
              </ProForm.Group>
              <ProForm.Group title="" size={3}>
                <ProFormText
                  width="md"
                  name="mpNum"
                  label="公众号数量"
                  tooltip="可绑定的微信公众号(订阅号|服务号)数量"
                  disabled
                />
                <ProFormText
                  width="md"
                  name="cpNum"
                  label="企业微信数量"
                  tooltip="可创建的企业微信AI机器人数量"
                  disabled
                />
                <ProFormText
                  width="md"
                  name="wechatNum"
                  label="个人微信数量"
                  tooltip="可创建的微信AI分身数量,一个微信AI分身只能绑定一个微信号，首次登录即完成唯一绑定"
                  disabled
                />
              </ProForm.Group>
              <ProForm.Group title="" size={3}>
                <ProFormText
                  width="md"
                  name="tokenBalance"
                  label="token数量"
                  tooltip="购买token计费套餐有效"
                  disabled
                />
                <ProFormText
                  width="md"
                  name="price"
                  label="订阅价格(￥)"
                  tooltip="需要支付的费用"
                  disabled
                />
                <ProFormText
                  width="md"
                  name="serviceDuration"
                  label="服务时间(天)"
                  tooltip="从订单审核通过后开始计时"
                  disabled
                />
              </ProForm.Group>
              <ProFormTextArea
                name="description"
                label="功能服务详情"
                disabled
              />
            </ProForm>
          </StepsForm.StepForm>
          <StepsForm.StepForm<{
            checkbox: string
          }>
            name="userAgreement"
            title="用户政策"
          >
            <div style={{ marginBottom: "50px" }}>本商品为虚拟服务商品，不支持退款，购买前请了解清楚！</div>
          </StepsForm.StepForm>
          <StepsForm.StepForm<{
            checkbox: string
          }>
            name="pay"
            title="支付"
          >
            <Space direction="horizontal" style={{marginBottom: "30px"}}>
              <img
                className="wechatPayQrCode"
                height="200"
                width="200"
                alt=""
                src={wechatPayQrCode}
              />
              <img
                className="wechatPayQrCode"
                height="200"
                width="200"
                alt=""
                src={aliPayQrCode}
              />
            </Space>
          </StepsForm.StepForm>
          <StepsForm.StepForm style={{textAlign: "center"}} name="finish" title="完成">

            <div className={styles.payVoucher}>
              <ImageViewAndUpload
                describe={'上传支付凭证'}
                avatar={null}
                setUploadAvatarUrl={setUploadAvatarUrl}
              />
            </div>
            <div style={{ margin: "30px" }}>tips: 点击“上传支付凭证”，将刚才付款成功截图上传</div>
          </StepsForm.StepForm>
        </StepsForm>
      </Card>
    </>
  )
}
