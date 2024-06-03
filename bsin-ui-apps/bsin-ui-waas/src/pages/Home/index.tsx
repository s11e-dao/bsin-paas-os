import React, { useState } from 'react';
import { Card, Row, Col, Typography } from 'antd';
import {
  IdcardOutlined,
  DollarOutlined,
  BgColorsOutlined,
} from '@ant-design/icons';
import {
  getLocalStorageInfo
} from '@/utils/localStorageInfo';

import Authentication from '../../components/Authentication';

// Your component
const FeatureCards = () => {

  const [merchantInfo, setMerchantInfo] = useState<{}>();
  const [userInfo, setUserInfo] = useState<{}>();
  React.useEffect(() => {
    // 获取商户信息
    setMerchantInfo(getLocalStorageInfo('merchantInfo'))
    setUserInfo(getLocalStorageInfo('userInfo'))
    console.log(getLocalStorageInfo('merchantInfo')?.authenticationStatus)
    console.log(getLocalStorageInfo('userInfo')?.type)
  }, []);

  const cardStyle = {
    borderRadius: '10px', // Adjust as necessary
    margin: '10px',
    textAlign: 'center',
    boxShadow: '0 4px 8px 0 rgba(0,0,0,0.2)', // Example shadow effect
  };

  return (
    <>
      {/* 判断用户是否已经认证 */}
      {merchantInfo?.authenticationStatus != 2 || userInfo ? (
        <Authentication />
      ) : (
        <Card>
          <div style={{ padding: '30px', height: "80vh" }}>
            <div style={{ margin: "auto", width: "300px", textAlign: "center", marginTop: "100px" }}>
              <Typography.Title level={1} style={{ margin: 0 }}>
                欢迎使用
              </Typography.Title>
              <p>
                现在，从阅读文档开启使用之旅
              </p>
            </div>
            <Row gutter={16}>
              <Col span={8}>
                <Card style={cardStyle}>
                  <IdcardOutlined style={{ fontSize: '48px', color: '#08c' }} />
                  <h3>功能描述文案</h3>
                  <p>附属信息、更多细节</p>
                </Card>
              </Col>
              <Col span={8}>
                <Card style={cardStyle}>
                  <DollarOutlined style={{ fontSize: '48px', color: '#8c0' }} />
                  <h3>功能描述文案</h3>
                  <p>附属信息、更多细节</p>
                </Card>
              </Col>
              <Col span={8}>
                <Card style={cardStyle}>
                  <BgColorsOutlined style={{ fontSize: '48px', color: '#c80' }} />
                  <h3>功能描述文案</h3>
                  <p>附属信息、更多细节</p>
                </Card>
              </Col>
            </Row>
          </div>
        </Card>
      )}
    </>
  );
};

export default FeatureCards;
