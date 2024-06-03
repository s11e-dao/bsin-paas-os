import React, { useEffect, useState } from 'react'
import { Card, Col, Row, Space, Typography } from 'antd'
import {
  IdcardOutlined,
  ReadFilled,
  VideoCameraFilled,
  DollarOutlined,
  BgColorsOutlined,
} from '@ant-design/icons'
const { Meta } = Card
import { getLocalStorageInfo } from '@/utils/localStorageInfo'

import Authentication from '../../components/Authentication/index'

export default function Home() {
  const [merchantInfo, setMerchantInfo] = useState<{}>()
  const [userInfo, setUserInfo] = useState<{}>()

  useEffect(() => {
    setMerchantInfo(getLocalStorageInfo('merchantInfo'))
    setUserInfo(getLocalStorageInfo('userInfo'))
    console.log(getLocalStorageInfo('merchantInfo')?.authenticationStatus)
    console.log(getLocalStorageInfo('userInfo')?.type)
  }, [])

  const cardStyle = {
    borderRadius: '10px', // Adjust as necessary
    margin: '10px',
    textAlign: 'center',
    boxShadow: '0 4px 8px 0 rgba(0,0,0,0.2)', // Example shadow effect
  }

  return (
    <>
      {/* 判断用户是否已经认证 */}
      {merchantInfo?.authenticationStatus != 2 || userInfo ? (
        <Authentication />
      ) : (
        <Card>
          <div style={{ padding: '30px', height: '80vh' }}>
            <div
              style={{
                margin: 'auto',
                width: '300px',
                textAlign: 'center',
                marginTop: '100px',
              }}
            >
              <Typography.Title level={1} style={{ margin: 0 }}>
                欢迎使用
              </Typography.Title>
              <p>现在，从阅读文档开启使用之旅</p>
            </div>
            <Row gutter={16}>
              <Col span={8}>
                <Card style={cardStyle}>
                  <ReadFilled style={{ fontSize: '48px', color: '#08c' }} />
                  <h3>BsinCopilot使用文档</h3>
                  <a href="https://eeihz6cbu0.feishu.cn/docx/CLMQdupJbo7RuRxNWl7cMNDfnBb?theme=LIGHT&contentTheme=DARK">单击查看用户手册</a>
                  <p>微信扫一扫，将个人微信托管于AI智能体</p>
                </Card>
              </Col>
              <Col span={8}>
                <Card style={cardStyle}>
                  <VideoCameraFilled
                    style={{ fontSize: '48px', color: '#8c0' }}
                  />
                  <h3>视频教程介绍</h3>
                  <a href="https://space.bilibili.com/538568387">单击查看视频教程</a>
                  <p>打造端到端的AI应用</p>
                </Card>
              </Col>
              {/* <Col span={8}>
                <Card style={cardStyle}>
                  <BgColorsOutlined
                    style={{ fontSize: '48px', color: '#c80' }}
                  />
                  <h3>功能描述文案</h3>
                  <p>附属信息、更多细节</p>
                </Card>
              </Col> */}
            </Row>
          </div>
        </Card>
      )}
    </>
  )
}
