import React, { useState } from 'react';
import { Input, Card, Tabs, Row, Col, Badge, List, Space, Typography, Layout } from 'antd';
import { 
  SearchOutlined, 
  AppstoreOutlined, 
  CompassOutlined, 
  DatabaseOutlined, 
  HeartOutlined, 
  SyncOutlined,
  PictureOutlined
} from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';

const { Search } = Input;
const { TabPane } = Tabs;
const { Text, Title } = Typography;
const { Content } = Layout;

const DataAssetPage = () => {
  const [activeTab, setActiveTab] = useState('history');

  // Mock data for datasets
  const datasets = [
    { id: 1, title: '人口库_基础库_个人房屋', code: 'ods.ods_rkk_jck_grfw', type: 'history' },
    { id: 2, title: '人口库_基础库_人口基础信息', code: 'ods.ods_rkk_jck_rkjcxx', type: 'history' },
    { id: 3, title: 't_policy_info', code: 't_policy_info', type: 'history' },
    { id: 4, title: '【ads】单位账户综合信息', code: 'ads.ads_corp_accinfo_zh', type: 'history' },
    { id: 5, title: '【DWD】贷款账总余额', code: 'dwd.dwd_loan_txbalance', type: 'history' },
    { id: 6, title: '【dws】贷款计划', code: 'dws.dws_loan_plan', type: 'history' },
    { id: 7, title: '【ads】贷款计划', code: 'ads.ads_loan_plan', type: 'history' },
    { id: 8, title: '人口库_基础库_户籍人口', code: 'ods.ods_rkk_jck_hjrk', type: 'history' },
    { id: 9, title: '人口库_基础库_学生信息', code: 'history' },
    { id: 10, title: 'ods-人口信息', code: 'ods.ods_zsj_sbss_population', type: 'history' },
    { id: 11, title: '【ads】个人缴存明细', code: 'ads.ads_per_jcdetail', type: 'hot' },
    { id: 12, title: '【ads】财务会计账簿', code: 'ads.ads_fin_core_acct', type: 'hot' },
    { id: 13, title: '【dws】单位账户综合信息', code: 'dws.dws_corp_accinfo_zh', type: 'hot' },
    { id: 14, title: '【dws】个人缴存明细', code: 'dws.dws_per_jcdetail', type: 'hot' },
    { id: 15, title: '【ods】财务会计账簿', code: 'ods.t_fin_core_acct', type: 'hot' },
  ];

  // Filter datasets based on active tab
  const getDatasetsByType = (type) => {
    return datasets.filter(dataset => dataset.type === type);
  };

  // Category cards data
  const categories = [
    { title: '接入层', count: 30, icon: <CompassOutlined />, color: '#36cfc9' },
    { title: '数据体系', count: 235, icon: <DatabaseOutlined />, color: '#f759ab' },
    { title: '服务应用', count: 60, icon: <HeartOutlined />, color: '#52c41a' },
    { title: '共享层', count: 1, icon: <SyncOutlined />, color: '#1890ff' },
  ];

  return (
    <PageContainer title={false} header={{ breadcrumb: {} }}>
      <Layout style={{ background: '#fff' }}>
        <Content style={{ padding: '0 24px' }}>
          {/* Search Bar */}
          <div style={{ textAlign: 'center', marginBottom: 24 }}>
            <Search
              placeholder="请输入您想查的资产名称"
              enterButton={<SearchOutlined />}
              size="large"
              style={{ maxWidth: 600, width: '100%' }}
            />
          </div>

          {/* Category Cards */}
          <Row gutter={16} style={{ marginBottom: 24 }}>
            {categories.map((category, index) => (
              <Col key={index} xs={24} sm={12} md={6} lg={4.8}>
                <Card 
                  hoverable 
                  bodyStyle={{ 
                    padding: '16px', 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'space-between',
                    border: `1px solid ${category.color}20`,
                    borderRadius: '4px',
                    background: `${category.color}10`
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <div style={{ 
                      backgroundColor: category.color, 
                      color: 'white', 
                      borderRadius: '4px', 
                      width: 32, 
                      height: 32, 
                      display: 'flex', 
                      alignItems: 'center', 
                      justifyContent: 'center',
                      marginRight: 12
                    }}>
                      {category.icon}
                    </div>
                    <Text style={{ color: category.color, fontWeight: 'bold' }}>{category.title}</Text>
                  </div>
                  <Badge count={category.count} style={{ backgroundColor: category.color }} />
                </Card>
              </Col>
            ))}
          </Row>

          {/* Tabs and List */}
          <Card>
            <Tabs activeKey={activeTab} onChange={setActiveTab}>
              <TabPane tab="历史足迹" key="history" />
              <TabPane tab="我的收藏" key="favorite" />
            </Tabs>
            
            <div style={{ display: 'flex' }}>
              <div style={{ flex: 3 }}>
                <List
                  grid={{ gutter: 16, xs: 1, sm: 1, md: 2, lg: 2, xl: 2, xxl: 2 }}
                  dataSource={getDatasetsByType(activeTab)}
                  renderItem={item => (
                    <List.Item>
                      <Card 
                        hoverable
                        size="small"
                        style={{ marginBottom: 16 }}
                      >
                        <div style={{ display: 'flex', alignItems: 'center' }}>
                          <div style={{ 
                            backgroundColor: '#1890ff', 
                            borderRadius: '4px', 
                            width: 40, 
                            height: 40, 
                            display: 'flex', 
                            alignItems: 'center', 
                            justifyContent: 'center',
                            marginRight: 12
                          }}>
                            <PictureOutlined style={{ color: 'white', fontSize: 20 }} />
                          </div>
                          <div>
                            <div>{item.title}</div>
                            <Text type="secondary" style={{ fontSize: 12 }}>{item.code}</Text>
                          </div>
                        </div>
                      </Card>
                    </List.Item>
                  )}
                />
              </div>
              
              <div style={{ flex: 1, marginLeft: 24 }}>
                <Card title="热门" style={{ marginBottom: 16 }}>
                  <List
                    size="small"
                    dataSource={getDatasetsByType('hot')}
                    renderItem={item => (
                      <List.Item>
                        <div style={{ display: 'flex', alignItems: 'center' }}>
                          <div style={{ 
                            backgroundColor: '#1890ff', 
                            borderRadius: '4px', 
                            width: 32, 
                            height: 32, 
                            display: 'flex', 
                            alignItems: 'center', 
                            justifyContent: 'center',
                            marginRight: 12
                          }}>
                            <PictureOutlined style={{ color: 'white', fontSize: 16 }} />
                          </div>
                          <div>
                            <div style={{ fontSize: 13 }}>{item.title}</div>
                            <Text type="secondary" style={{ fontSize: 11 }}>{item.code}</Text>
                          </div>
                        </div>
                      </List.Item>
                    )}
                  />
                </Card>
              </div>
            </div>
          </Card>
        </Content>
      </Layout>
    </PageContainer>
  );
};

export default DataAssetPage;