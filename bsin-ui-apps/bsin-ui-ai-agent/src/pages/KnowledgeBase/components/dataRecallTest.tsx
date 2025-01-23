import React, { useState, useEffect } from 'react'
import { Input, Button, Card, List, Tag, Space, Typography, Row, Col, Table } from 'antd'
import KnowledgeBaseFileList from './knowledgeBaseFileList'
import KnowledgeBaseFileDetail from './knowledgeBaseFileDetail'

import Retrival from './retrival'

const { TextArea } = Input
const { Title, Text } = Typography

const DataRecallTest: React.FC = ({ chatUIProps }) => {

  const [currentContent, setCurrentContent] = useState([])
  const [knowledgeBase, setKnowledgeBase] = useState('')
  const [inputText, setInputText] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  // 记录列表数据
  const recordColumns = [
    {
      title: '数据源',
      dataIndex: 'source',
      key: 'source',
      render: (text) => <Text>{text}</Text>,
    },
    {
      title: '文本',
      dataIndex: 'text',
      key: 'text',
    },
    {
      title: '时间',
      dataIndex: 'time',
      key: 'time',
    },
  ];

  const recordData = [
    {
      key: '1',
      source: 'Retrieval Test',
      text: '厕所',
      time: '2025-01-22 22:19',
    },
    {
      key: '2',
      source: 'Retrieval Test',
      text: 'retrieval',
      time: '2025-01-22 21:58',
    },
  ];

  useEffect(() => {
    console.log(chatUIProps)
    setKnowledgeBase(chatUIProps.aiNo)
  }, [chatUIProps])

  const handleTest = () => {
    setIsLoading(true)
    // 这里可以调用检索方法
    setTimeout(() => {
      setIsLoading(false)
    }, 1000)
  }

  return (
    <div style={{ padding: '20px' }}>
      <Row gutter={16}>
        {/* 左侧区域 */}
        <Col span={12}>
          <Space direction="vertical" size="large" style={{ width: '100%' }}>
            {/* 输入区域 */}
            <div>
              <Title level={5}>召回测试</Title>
              <Space direction="vertical" style={{ width: '100%' }}>
                <TextArea
                  value={inputText}
                  onChange={(e) => setInputText(e.target.value)}
                  placeholder="根据给定的查询文本测试知识的召回效果..."
                  autoSize={{ minRows: 3, maxRows: 6 }}
                />
                <div style={{ textAlign: 'right' }}>
                  <Button type="primary" onClick={handleTest} loading={isLoading}>
                    测试
                  </Button>
                </div>
              </Space>
            </div>

            {/* 记录区域 */}
            <div>
              <Title level={5}>记录</Title>
              <Table 
                columns={recordColumns} 
                dataSource={recordData}
                pagination={false}
                size="small"
              />
            </div>
          </Space>
        </Col>

        {/* 右侧结果显示区域 */}
        <Col span={12}>
          <div>
            <Space style={{ marginBottom: '16px' }}>
              <Title level={5} style={{ margin: 0 }}>召回结果</Title>
              <Tag>1个召回段落</Tag>
            </Space>
            <List
              itemLayout="vertical"
              dataSource={currentContent}
              renderItem={(item, index) => (
                <List.Item>
                  <Card 
                    type="inner" 
                    title={`Chunk-${String(index + 1).padStart(2, '0')} ${item.wordCount || '86'}字符`}
                    style={{ marginBottom: '8px' }}
                  >
                    <Space direction="vertical" style={{ width: '100%' }}>
                      <Text>{item.content}</Text>
                      <Space wrap>
                        <Text type="secondary">相关度：</Text>
                        <Tag color="blue">{item.score?.toFixed(2) || '0.10'}</Tag>
                        {item.tags?.map((tag, i) => (
                          <Tag key={i}>{tag}</Tag>
                        ))}
                      </Space>
                      {item.file && (
                        <Space>
                          <Text type="secondary">文件：</Text>
                          <Button type="link" size="small" style={{ padding: 0 }}>
                            {item.file}
                          </Button>
                        </Space>
                      )}
                    </Space>
                  </Card>
                </List.Item>
              )}
              locale={{ emptyText: '暂无数据' }}
            />
          </div>
        </Col>
      </Row>
    </div>
  )
}

export default DataRecallTest
