import React, { useState, useRef, useEffect } from 'react'
import type { ProFormInstance } from '@ant-design/pro-form'
import { EditableProTable } from '@ant-design/pro-table'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProForm, {
  StepsForm,
  ProFormDigit,
  ProFormText,
  ProFormSelect,
  ProFormCheckbox,
} from '@ant-design/pro-form'
import { Modal, message, Button, Row, Col, Card, Checkbox } from 'antd'
import type { CheckboxValueType } from 'antd/es/checkbox/Group'
import {
  getLocalStorageInfo,
  setLocalStorageInfo,
} from '@/utils/localStorageInfo'

import {
  getMerchantAuthorizableAppList,
  subscribeApps,
  getServiceSubscribePageList,
} from './service'

const waitTime = (time: number = 100) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(true)
    }, time)
  })
}

export default ({ setCurrentContent }) => {
  const [checkedList, setCheckedList] = useState<CheckboxValueType[]>()

  const onChange = (list: CheckboxValueType[]) => {
    console.log(list)
    setCheckedList(list)
  }

  // 可授权应用集合
  const [authorizableAppList, setAuthorizableAppList] = useState([])

  useEffect(() => {
    // 查询商户可授权应用
    let params = {
      orgCode: getLocalStorageInfo('merchantInfo')?.merchantName,
    }
    getMerchantAuthorizableAppList(params).then((res) => {
      console.log(res?.data)
      let typeNoListTemp = []
      if (res?.code == '000000') {
        res?.data.map((item) => {
          console.log(item)
          let typeNoJson = {
            label: item.appName,
            value: item.appId,
          }
          typeNoListTemp.push(typeNoJson)
        })
        setAuthorizableAppList(typeNoListTemp)
      }
    })
  }, [])

  /**
   * 确认订阅服务
   */
  const confirmRegisterMerchant = (checkedAppIds) => {
    // 获取表单结果
    let reqParam = {
      orgCode: getLocalStorageInfo('merchantInfo')?.merchantName,
      appIds: checkedAppIds,
    }
    subscribeApps(reqParam).then((res) => {
      if (res.code === '000000' || res.code === 0) {
        message.success('添加成功')
      } else {
        message.error(`失败： ${res?.message}`)
      }
    })
  }

  return (
    <>
      <Card
        title="服务订阅"
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
            await waitTime(1000)
            // 执行提交
            confirmRegisterMerchant(values.appIds)
            //如果有返回值为true,点击最后一步的提交按钮之后，则返回至第一步
            //return true
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
                    去支付 {'>'}
                  </Button>
                )
              }

              if (props.step === 1) {
                return [
                  <Button key="pre" onClick={() => props.onPre?.()}>
                    返回第一步
                  </Button>,
                  <Button
                    type="primary"
                    key="goToTree"
                    onClick={() => props.onSubmit?.()}
                  >
                    支付完成 {'>'}
                  </Button>,
                ]
              }

              return [
                <Button key="gotoTwo" onClick={() => props.onPre?.()}>
                  {'<'} 返回支付界面
                </Button>,
                <Button
                  type="primary"
                  key="goToTree"
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
            name="base"
            title="订阅服务"
            onFinish={async ({ name }) => {
              console.log(name)
              await waitTime(2000)
              return true
            }}
          >
            <ProFormCheckbox.Group
              name="appIds"
              label="可授权服务"
              width="lg"
              rules={[
                {
                  required: true,
                },
              ]}
              options={authorizableAppList}
            />
          </StepsForm.StepForm>
          <StepsForm.StepForm<{
            checkbox: string
          }>
            name="checkbox"
            title="支付"
          >
            <ProFormCheckbox.Group
              name="checkbox1"
              label="迁移类型"
              width="lg"
              options={['结构迁移', '全量迁移', '增量迁移', '全量校验']}
            />
          </StepsForm.StepForm>
          <StepsForm.StepForm name="time" title="完成">
            <ProFormCheckbox.Group
              name="checkbox2"
              label="部署单元"
              rules={[
                {
                  required: true,
                },
              ]}
              options={['部署单元1', '部署单元2', '部署单元3']}
            />
          </StepsForm.StepForm>
        </StepsForm>
      </Card>
    </>
  )
}
