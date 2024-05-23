import React, { useEffect, useState } from 'react'
import { Button, Input, Space, message, Form, Col, Row } from 'antd'
import ProForm, {
  ProFormSelect,
  ProFormText,
  ProFormSwitch,
  ProFormTextArea,
} from '@ant-design/pro-form'

import { useRequest } from 'umi'
import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '@/utils/localStorageInfo'

import type { UploadChangeParam } from 'antd/es/upload'
import type { RcFile, UploadFile, UploadProps } from 'antd/es/upload/interface'
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons'

import { getCopilotList, editCopilot } from '../service'
import { editLLMInfo, getLLMList } from '../../LLM/service'
import {
  editEmbeddingModel,
  getEmbeddingModelList,
} from '../../EmbeddingModel/service'
import {
  editPromptTemplate,
  getPromptTemplateList,
} from '../../PromptTemplate/service'
import {
  editKnowledgeBase,
  getKnowledgeBaseList,
} from '../../KnowledgeBase/service'

import styles from './BaseView.less'
import cstyles from '../style.less'

const ModelSetting: React.FC = (copilotInfo) => {
  const [initialLlm, setInitialLlm] = useState('您还未创建大语言模型！')

  const [initialPromptTemplate, setInitialPromptTemplate] = useState(
    '您还未创建提示词模版！',
  )
  const [initialKnowledgeBase, setInitialKnowledgeBase] = useState(
    '您还未创建知识库！',
  )

  const [llmList, setLlmList] = useState([])
  const [promptTemplateList, setPromptTemplateList] = useState([])
  const [knowledgeBaseList, setKnowledgeBaseList] = useState([])

  const [llmShowList, setLlmShowList] = useState([])
  const [promptTemplateShowList, setPromptTemplateShowList] = useState([])
  const [knowledgeBaseShowList, setKnowledgeBaseShowList] = useState([])

  const [selectedLlm, setSelectedLlm] = useState({})
  const [selectedPromptTemplate, setSelectedPromptTemplate] = useState({})
  const [selectedKnowledgeBase, setSelectedKnowledgeBase] = useState({})

  const [llmForm] = Form.useForm()
  const [promptTemplateForm] = Form.useForm()
  const [knowledgeBaseForm] = Form.useForm()
  const [copilotForm] = Form.useForm()

  // 查询Copilot信息
  useEffect(() => {
    console.log(copilotInfo)
    // 查询协议
    let params = {
      current: '1',
      pageSize: '99',
    }
    getKnowledgeBaseList(params).then((res) => {
      if (res?.code == '000000') {
        let knowledgeBaseListTmp = []
        res?.data.map((knowledgeBase) => {
          let knowledgeBaseTmp = {
            value: knowledgeBase.serialNo,
            label: knowledgeBase.name,
          }
          if (
            knowledgeBase.serialNo == copilotInfo?.copilotInfo.knowledgeBaseNo
          ) {
            console.log(knowledgeBase.name)
            setSelectedKnowledgeBase(knowledgeBase)
            setInitialKnowledgeBase(knowledgeBase.serialNo)
            refreshKnowledgeBaseForm(knowledgeBase)
          }
          knowledgeBaseListTmp.push(knowledgeBaseTmp)
        })
        setKnowledgeBaseShowList(knowledgeBaseListTmp)
        setKnowledgeBaseList(res?.data)
      } else {
        message.error(res?.message)
      }
    })

    getPromptTemplateList(params).then((res) => {
      if (res?.code == '000000') {
        let promptTemplateListTmp = []
        res?.data.map((promptTemplate) => {
          let promptTemplateTmp = {
            value: promptTemplate.serialNo,
            label: promptTemplate.name,
          }
          if (
            promptTemplate.serialNo == copilotInfo?.copilotInfo.promptTemplateNo
          ) {
            setSelectedPromptTemplate(promptTemplate)
            setInitialPromptTemplate(promptTemplate.serialNo)
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

    getLLMList(params).then((res) => {
      if (res?.code == '000000') {
        let llmListTmp = []
        res?.data.map((llm) => {
          let llmTmp = {
            value: llm.serialNo,
            label: llm.name + '(' + llm.description + ')',
          }
          if (llm.serialNo == copilotInfo?.copilotInfo.llmNo) {
            setSelectedLlm(llm)
            setInitialLlm(llm.serialNo)
            refreshLlmForm(llm)
          }
          llmListTmp.push(llmTmp)
        })
        setLlmShowList(llmListTmp)
        setLlmList(res?.data)
      } else {
        message.error(res?.message)
      }
    })
  }, [])

  const llmNoChange = (e: Event) => {
    llmList.map((llm) => {
      if (llm?.serialNo == e) {
        setSelectedLlm(llm)
        refreshLlmForm(llm)
      }
    })
  }

  const refreshLlmForm = (nodeInfo: object) => {
    llmForm.resetFields()
    llmForm.setFieldsValue(nodeInfo)
    llmForm.setFieldValue('llmNo', nodeInfo.serialNo)
  }

  const promptTemplateNoChange = (e: Event) => {
    console.log(promptTemplateList)
    console.log(e)
    promptTemplateList.map((promptTemplate) => {
      if (promptTemplate?.serialNo == e) {
        setSelectedPromptTemplate(promptTemplate)
        refreshPromptTemplateForm(promptTemplate)
      }
    })
  }

  const refreshPromptTemplateForm = (nodeInfo: object) => {
    promptTemplateForm.resetFields()
    promptTemplateForm.setFieldsValue(nodeInfo)
    promptTemplateForm.setFieldValue('promptTemplateNo', nodeInfo.serialNo)
  }

  const knowledgeBaseNoChange = (e: Event) => {
    console.log(knowledgeBaseList)
    console.log(e)
    knowledgeBaseList.map((knowledgeBase) => {
      if (knowledgeBase?.serialNo == e) {
        setSelectedKnowledgeBase(knowledgeBase)
        refreshKnowledgeBaseForm(knowledgeBase)
      }
    })
  }

  const refreshKnowledgeBaseForm = (nodeInfo: object) => {
    knowledgeBaseForm.resetFields()
    knowledgeBaseForm.setFieldsValue(nodeInfo)
  }

  const handleFinish = async () => {
    copilotForm
      .validateFields()
      .then(async () => {
        var response = copilotForm.getFieldsValue()
        console.log(response)
        if (response.editable == false) {
          message.warning('该配置不支持编辑！！')
          return
        }
        let res = await editCopilot(response)
        res
          ? message.success('更新模型配置成功')
          : message.error('更新模型配置失败！')
      })
      .catch(() => {})
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
      .catch(() => {})
  }

  const formItemLayout = {
    labelCol: { span: 9 },
    wrapperCol: { span: 15 },
  }

  return (
    <>
      <div className={cstyles.title}>模型设置</div>
      <ProForm
        // style={{ display: 'flex' }}
        layout="vertical"
        onFinish={handleFinish}
        submitter={{
          searchConfig: {
            submitText: '更新模型设置',
          },
          render: (_, dom) => dom[1],
        }}
        initialValues={{
          ...copilotInfo.copilotInfo,
        }}
        form={copilotForm}
      >
        <ProFormText width="md" name="serialNo" label="copilotID" disabled />
        <ProFormSwitch
          width="md"
          name="editable"
          label="是否可编辑"
          disabled
          tooltip="系统配置不支持编辑和删除"
        />
        <ProFormSelect
          width="md"
          name="knowledgeBaseNo"
          label="知识库ID"
          rules={[
            {
              required: true,
              message: '请选择提示词模版!',
            },
          ]}
          onChange={(e) => {
            knowledgeBaseNoChange(e)
          }}
          initialValue={initialKnowledgeBase}
          options={knowledgeBaseShowList}
        />
        <ProFormSelect
          width="md"
          name="llmNo"
          label="大语言模型"
          rules={[
            {
              required: true,
              message: '请选择大语言模型!',
            },
          ]}
          onChange={(e) => {
            llmNoChange(e)
          }}
          initialValue={initialLlm}
          options={llmShowList}
        />
        <ProFormSelect
          width="md"
          name="promptTemplateNo"
          label="提示词模版"
          rules={[
            {
              required: true,
              message: '请选择提示词模版!',
            },
          ]}
          onChange={(e) => {
            promptTemplateNoChange(e)
          }}
          initialValue={initialPromptTemplate}
          options={promptTemplateShowList}
        />
      </ProForm>
    </>
  )
}

export default ModelSetting
