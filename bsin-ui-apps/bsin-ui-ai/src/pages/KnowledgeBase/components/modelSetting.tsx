import React, { useEffect, useState } from 'react'
import { UploadOutlined } from '@ant-design/icons'
import { Button, Input, Space, message, Form, Col, Row } from 'antd'
import ProForm, {
  ProFormDependency,
  ProFormFieldSet,
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
import { editKnowledgeBase, getKnowledgeBaseList } from '../service'
import { editLLMInfo, getLLMList } from '../../LLM/service'
import {
  editEmbeddingModel,
  getEmbeddingModelList,
} from '../../EmbeddingModel/service'
import {
  editPromptTemplate,
  getPromptTemplateList,
} from '../../PromptTemplate/service'

import type { UploadChangeParam } from 'antd/es/upload'
import type { RcFile, UploadFile, UploadProps } from 'antd/es/upload/interface'
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons'

import styles from './BaseView.less'
import cstyles from '../style.less'

const ModelSetting: React.FC = (knowledgeBaseInfo) => {

  const [initialEmbeddingMode, setInitialEmbeddingMode] = useState(
    '请选择索引模型',
  )
 
  const [embeddingModelList, setEmbeddingModelList] = useState([])

  const [embeddingModelShowList, setEmbeddingModelShowList] = useState([])

  const [selectedEmbeddingModel, setSelectedEmbeddingModel] = useState({})

  const [embeddingModelForm] = Form.useForm()

  // 查询KnowledgeBase信息
  useEffect(() => {
    console.log(knowledgeBaseInfo)
    // 查询协议
    let params = {
      current: '1',
      pageSize: '99',
    }

    getEmbeddingModelList(params).then((res) => {
      let embeddingModelListTmp = []
      res?.data.map((embeddingModel) => {
        let embeddingModelTmp = {
          value: embeddingModel.serialNo,
          label: embeddingModel.name,
        }
        if (
          embeddingModel.serialNo == knowledgeBaseInfo?.record.embeddingModelNo
        ) {
          setSelectedEmbeddingModel(embeddingModel)
          refreshEmbeddingModelForm(embeddingModel)
        }
        console.log(embeddingModelTmp)
        embeddingModelListTmp.push(embeddingModelTmp)
      })
      console.log(embeddingModelListTmp)
      setEmbeddingModelShowList(embeddingModelListTmp)
      setEmbeddingModelList(res?.data)
    })
    
  }, [])


  const embeddingModelNoNoChange = (e: Event) => {
    embeddingModelList.map((embeddingModel) => {
      if (embeddingModel?.serialNo == e) {
        console.log(embeddingModel)
        setSelectedEmbeddingModel(embeddingModel)
        refreshEmbeddingModelForm(embeddingModel)
      }
    })
  }

  const refreshEmbeddingModelForm = (nodeInfo: object) => {
    embeddingModelForm.setFieldsValue(nodeInfo)
    embeddingModelForm.setFieldValue("serialNo",knowledgeBaseInfo?.record.serialNo)
    embeddingModelForm.setFieldValue("embeddingModelNo",nodeInfo.serialNo)
  }

  const embeddingModelHandleFinish = async () => {
    embeddingModelForm
      .validateFields()
      .then(async () => {
        var response = embeddingModelForm.getFieldsValue()
        // 禁止在这里编辑
        response.apiKey = null
        console.log(response)
        
        let res = await editKnowledgeBase(response)
        res
          ? message.success('更新索引模型配置成功')
          : message.error('更新索引模型配置失败！')
      })
      .catch(() => { })
  }

  const formItemLayout = {
    labelCol: { span: 9 },
    wrapperCol: { span: 15 },
  }

  return (
    <>
      <div className={cstyles.title}>模型设置</div>
      <div className={styles.baseView}>
        <Row gutter={24}>
          {/* 索引模型详情 */}
          <Col className="gutter-row" span={18}>
            <div className={styles.left}>
              <ProForm
                layout="horizontal"
                initialValues={{
                  ...selectedEmbeddingModel,
                  serialNo: knowledgeBaseInfo?.record.serialNo
                }}
                {...formItemLayout}
                form={embeddingModelForm}
                onFinish={embeddingModelHandleFinish}
                submitter={{
                  searchConfig: {
                    submitText: '提交',
                    resetText: '重置',
                  },
                  render: (_, dom) => dom[1],
                }}
              >
                <ProFormText
                  width="lg"
                  name="serialNo"
                  label="knowledgeBaseID"
                  disabled
                />

                <ProFormSelect
                  width="lg"
                  name="embeddingModelNo"
                  label="索引模型"
                  rules={[
                    {
                      required: true,
                      message: '请选择索引模型!',
                    },
                  ]}
                  onChange={(e) => {
                    embeddingModelNoNoChange(e)
                  }}
                  initialValue={initialEmbeddingMode}
                  options={embeddingModelShowList}
                />

                <ProFormSwitch
                  width="lg"
                  name="editable"
                  label="是否可编辑"
                  disabled
                  tooltip="系统配置不支持编辑和删除"
                />

                {/* 检索方式：1.语义检索 2.增强语义检索 3.混合检索 */}
                <ProFormSelect
                  width="lg"
                  name="retrievalMethod"
                  label="检索方式"
                  options={[
                    {
                      label: '语义检索',
                      value: '1',
                    },
                    {
                      label: '增强语义检索',
                      value: '2',
                    },
                    {
                      label: '混合检索',
                      value: '3',
                    },
                  ]}
                />
                <ProFormText width="lg" name="maxResults" label="检索结果" />
                <ProFormText width="lg" name="minScore" label="相似度" />
                <ProFormText
                  width="lg"
                  name="quoteLimit"
                  label="引用token上限"
                />
                <ProFormText
                  width="lg"
                  name="emptyResp"
                  label="空搜索回复"
                  placeholder="无回复，保持空即可"
                />
                <ProFormSelect
                  width="lg"
                  name="dimension"
                  label="向量维度"
                  options={[
                    {
                      label: '384',
                      value: 384,
                    },
                    {
                      label: '512',
                      value: 512,
                    },
                  ]}
                />
                <ProFormText
                  width="lg"
                  name="segmentSizeInTokens"
                  label="分段最多token数"
                />
                <ProFormText
                  width="lg"
                  name="overlapSizeInTokens"
                  label="最大重叠token数"
                />
                <ProFormSelect
                  width="lg"
                  name="tokenizerModel"
                  label="分词模型"
                  options={[
                    {
                      label: 'OpenAi',
                      value: 'OpenAi',
                    },
                    {
                      label: 'Bert',
                      value: 'Bert',
                    },
                    {
                      label: 'Qwen',
                      value: 'Qwen',
                    },
                  ]}
                />
                <ProFormSelect
                  width="lg"
                  name="documentSplitter"
                  label="文档分词器"
                  options={[
                    {
                      label: 'ByCharacter',
                      value: 'ByCharacter',
                    },
                    {
                      label: 'ByLine',
                      value: 'ByLine',
                    },
                    {
                      label: 'ByParagraph',
                      value: 'ByParagraph',
                    },
                    {
                      label: 'ByRegex',
                      value: 'ByRegex',
                    },
                    {
                      label: 'BySentence',
                      value: 'BySentence',
                    },
                    {
                      label: 'ByWord',
                      value: 'ByWord',
                    },
                  ]}
                />

                <ProFormTextArea
                  width="lg"
                  name="description"
                  label="索引模型简介"
                  placeholder="索引模型简介"
                />
              </ProForm>
            </div>
          </Col>
        </Row>
      </div>
    </>
  )
}

export default ModelSetting
