import React, { useEffect, useState } from 'react'
import { Button, Input, Space, message, Form, Col, Row } from 'antd'
import ProForm, {
  ProFormSelect,
  ProFormText,
  ProFormSwitch,
  ProFormTextArea,
} from '@ant-design/pro-form'

import {
  editPromptTemplate,
  getPromptTemplateList,
} from '../../PromptTemplate/service'

import styles from './baseView.less'
import cstyles from '../style.less'

const ModelSetting: React.FC = (promptTemplateInfo) => {
  

  const [promptTemplateList, setPromptTemplateList] = useState([])
  const [promptTemplateShowList, setPromptTemplateShowList] = useState([])
  const [selectedPromptTemplate, setSelectedPromptTemplate] = useState({})

  const [promptTemplateForm] = Form.useForm()

  // 查询PromptTemplate信息
  useEffect(() => {
    console.log(promptTemplateInfo)
    console.log('11111111111')
    // 查询协议
    let params = {
      current: '1',
      pageSize: '99',
    }
    getPromptTemplateList(params).then((res) => {
      if (res?.code == '000000') {
        let promptTemplateListTmp = []
        res?.data.map((promptTemplate) => {
          let promptTemplateTmp = {
            value: promptTemplate.serialNo,
            label: promptTemplate.name,
          }
          if (
            promptTemplate.serialNo ==
            promptTemplateInfo?.promptTemplateInfo.promptTemplateNo
          ) {
            setSelectedPromptTemplate(promptTemplate)
            refreshPromptTemplateForm(promptTemplate)
          }
          promptTemplateListTmp.push(promptTemplateTmp)
        })
        setPromptTemplateShowList(promptTemplateListTmp)
        setPromptTemplateList(res?.data)
      } else {
        message.error(res?.message)
      }
    })

    console.log('2222222222')
  }, [])


  const refreshPromptTemplateForm = (nodeInfo: object) => {
    promptTemplateForm.resetFields()
    promptTemplateForm.setFieldsValue(nodeInfo)
  }


  const promptTemplateHandleFinish = async () => {
    promptTemplateForm
      .validateFields()
      .then(async () => {
        var response = promptTemplateForm.getFieldsValue()
        console.log(response)
        if (response.editable == false) {
          message.warning('该配置不支持编辑！！')
          return
        }
        let res = await editPromptTemplate(response)
        res
          ? message.success('更新提示词模版配置成功')
          : message.error('更新提示词模版配置失败！')
      })
      .catch(() => { })
  }

  const formItemLayout = {
    labelCol: { span: 9 },
    wrapperCol: { span: 15 },
  }

  return (
    <>
      <div className={cstyles.title}>模版设置</div>

      <div className={styles.baseView}>
        <Row gutter={24}>
          {/* 索引模型详情 */}
          <Col className="gutter-row" span={18}>
            {/* 提示词模版详情 */}
            <div className={styles.left}>
              <ProForm
                layout="horizontal"
                initialValues={{
                  ...promptTemplateInfo.promptTemplateInfo,
                }}
                {...formItemLayout}
                form={promptTemplateForm}
                onFinish={promptTemplateHandleFinish}
                submitter={{
                  searchConfig: {
                    submitText: '提交',
                    resetText: '重置',
                  },
                  render: (_, dom) => dom[1],
                }}
              >
                <ProForm.Group title="" size={3}>
                  <ProFormText
                    width="lg"
                    name="serialNo"
                    label="提示词模版ID"
                    disabled
                  />
                  <ProFormSwitch
                    width="lg"
                    name="editable"
                    label="是否可编辑"
                    disabled
                    tooltip="系统配置不支持编辑和删除"
                  />
                  <ProFormText width="lg" name="name" label="名称" />
                  <ProFormText
                    width="lg"
                    name="systemRole"
                    label="AI角色定义"
                  />
                </ProForm.Group>
                <ProForm.Group title="" size={3}>
                  <ProFormTextArea
                    width="lg"
                    name="systemPromptTemplate"
                    label="系统提示词"
                  />
                  <ProFormTextArea
                    width="lg"
                    name="knowledgeBase"
                    label="知识库引用提示词模版"
                  />
                  <ProFormTextArea
                    width="lg"
                    name="chatHistorySummary"
                    label="历史聊天引用提示词模版"
                  />
                </ProForm.Group>
                <ProForm.Group title="" size={3}>
                  <ProFormTextArea
                    width="lg"
                    name="summaryPromptTemplate"
                    label="历史聊天总结提示词模版"
                  />
                  <ProFormTextArea
                    width="lg"
                    name="chatBufferWindow"
                    label="聊天上下文引用提示词模版"
                  />
                  <ProFormTextArea
                    width="lg"
                    name="description"
                    label="提示词模版简介"
                    placeholder="简介"
                  />
                </ProForm.Group>
              </ProForm>
            </div>
          </Col>
        </Row>
      </div>
    </>
  )
}

export default ModelSetting
