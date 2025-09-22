import React, { useState } from 'react';
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Select,
  Popconfirm,
  Descriptions,
  Card,
  Row,
  Col,
  Pagination,
  Tag,
  Typography,
  Space,
  Divider,
  Avatar,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined, EyeOutlined, ShopOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getDigitalAssetsCollectionPageList,
  deleteDigitalAssetsCollection,
  getDigitalAssetsCollectionDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';

const { Text, Title } = Typography;
const { Meta } = Card;

// 定义接口类型
interface AssetRecord {
  serialNo: string;
  name: string;
  symbol: string;
  totalSupply: string;
  inventory: string;
  collectionType: string;
  status: string;
  contractAddress: string;
  createBy: string;
  createTime: string;
  tenantId?: string;
  contractProtocolNo?: string;
  chainType?: string;
  chainEnv?: string;
  sponsorFlag?: string;
  bondingCurveFlag?: string;
  metadataImageSameFlag?: string;
  [key: string]: any;
}

interface Props {
  setCurrentContent: (content: string) => void;
  putOnShelves: (record: AssetRecord) => void;
}

export default ({ setCurrentContent, putOnShelves }: Props) => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isTemplateModal, setIsTemplateModal] = useState(false);
  // 查看模态框
  const [isViewTemplateModal, setIsViewTemplateModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState<AssetRecord>({} as AssetRecord);
  // 获取表单
  const [FormRef] = Form.useForm();
  
  // 卡片相关状态
  const [dataSource, setDataSource] = useState<AssetRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchParams, setSearchParams] = useState({});

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: AssetRecord, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewContractTemplate(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>

      <li>
        <a
          onClick={() => {
            putOnShelves(record);
          }}
        >
          上架
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
    </ul>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>(null);

  /**
   * 删除模板
   */
  const toDelContractTemplate = async (record: AssetRecord) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteDigitalAssetsCollection({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === 0) {
      // 删除成功刷新表单
      fetchData({ current, pageSize, ...searchParams });
    }
  };

  /**
   * 查看详情
   */
  const toViewContractTemplate = async (record: AssetRecord) => {
    let { serialNo } = record;
    let viewRes = await getDigitalAssetsCollectionDetail({ serialNo });
    setIsViewTemplateModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data || {});
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfCollectionType = () => {
    let { collectionType } = isViewRecord || {};
    if (collectionType == `1`) {
      return '数字资产';
    } else if (collectionType == `2`) {
      return 'PFP';
    } else if (collectionType == `3`) {
      return '账户-DP';
    } else if (collectionType == `4`) {
      return '数字门票';
    } else if (collectionType == `5`) {
      return 'Pass卡';
    } else if (collectionType == `6`) {
      return '账户-BC';
    } else if (collectionType == `7`) {
      return '满减';
    } else if (collectionType == `8`) {
      return '权限';
    } else if (collectionType == `9`) {
      return '会员等级';
    } else {
      return collectionType || '未知类型';
    }
  };

  const handleViewRecordOfSponsorFlag = () => {
    let { sponsorFlag } = isViewRecord || {};
    if (sponsorFlag == '0') {
      return '否';
    } else if (sponsorFlag == '1') {
      return '是';
    } else {
      return sponsorFlag || '-';
    }
  };
  const handleViewRecordOfBondingCurveFlag = () => {
    let { bondingCurveFlag } = isViewRecord || {};
    if (bondingCurveFlag == '0') {
      return '否';
    } else if (bondingCurveFlag == '1') {
      return '是';
    } else {
      return bondingCurveFlag || '-';
    }
  };

  const handleViewRecordOfMetadataImageSameFlag = () => {
    let { metadataImageSameFlag } = isViewRecord || {};
    if (metadataImageSameFlag == '0') {
      return '否';
    } else if (metadataImageSameFlag == '1') {
      return '是';
    } else {
      return metadataImageSameFlag || '-';
    }
  };

  // 获取集合类型文本
  const getCollectionTypeText = (type: string) => {
    const typeMap: { [key: string]: string } = {
      '1': '数字徽章',
      '2': 'PFP',
      '3': '账户-DP',
      '4': '数字门票',
      '5': 'Pass卡',
      '6': '账户-BC',
      '7': '满减',
      '8': '权限',
    };
    return typeMap[type] || type;
  };

  // 获取状态文本和颜色
  const getStatusInfo = (status: string) => {
    const statusMap: { [key: string]: { text: string; color: string } } = {
      '0': { text: '未流通', color: 'default' },
      '1': { text: '流通中', color: 'processing' },
      '2': { text: '流通完成', color: 'success' },
    };
    return statusMap[status] || { text: status, color: 'default' };
  };

  // 数据获取函数
  const fetchData = async (params: any) => {
    setLoading(true);
    try {
      let res = await getDigitalAssetsCollectionPageList({
        ...params,
        current: params.current || 1,
        pageSize: params.pageSize || 10,
      });
      console.log('😒', res);
      
      if (res && res.code === 0) {
        setDataSource(res.data || []);
        setTotal(res.pagination?.totalSize || 0);
      } else {
        console.error('API返回错误:', res);
        setDataSource([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('获取数据失败:', error);
      setDataSource([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  // 搜索表单提交
  const onFinish = (values: any) => {
    console.log('搜索参数:', values);
    const newSearchParams = { ...values };
    setSearchParams(newSearchParams);
    setCurrent(1);
    fetchData({ current: 1, pageSize, ...newSearchParams });
  };

  // 重置搜索
  const onReset = () => {
    FormRef.resetFields();
    setSearchParams({});
    setCurrent(1);
    fetchData({ current: 1, pageSize });
  };

  // 分页变化
  const onPageChange = (page: number, size: number) => {
    setCurrent(page);
    setPageSize(size);
    fetchData({ current: page, pageSize: size, ...searchParams });
  };

  // 初始化数据
  React.useEffect(() => {
    fetchData({ current: 1, pageSize: 10 });
  }, []);

  return (
    <div>
      {/* 标题和操作按钮 */}
      <Card style={{ marginBottom: 16 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Title level={4} style={{ margin: 0 }}>数字资产集合</Title>
          <Space>
          <Button
            onClick={async () => {
              console.log('res');
              setCurrentContent('issueAssets');
            }}
            icon={<PlusOutlined />}
            type="primary"
          >
            发行
            </Button>
          <Button
            onClick={async () => {
              console.log('res');
              setCurrentContent('editor');
            }}
            icon={<PlusOutlined />}
            type="primary"
          >
            editor
            </Button>
          </Space>
        </div>
      </Card>

      {/* 搜索表单 */}
      <Card style={{ marginBottom: 16 }}>
        <Form
          form={FormRef}
          onFinish={onFinish}
        >
          <Row gutter={[16, 16]} justify="center" align="bottom">
            <Col xs={24} sm={12} md={5} lg={5}>
              <Form.Item label="资产集合名称" name="name">
                <Input placeholder="请输入资产集合名称" />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={4} lg={4}>
              <Form.Item label="链网络环境" name="chainEnv">
                <Select placeholder="请选择链网络环境">
                  <Option value="test">测试网</Option>
                  <Option value="main">正式网</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={4} lg={4}>
              <Form.Item label="资产类型" name="collectionType">
                <Select placeholder="请选择资产类型">
                  <Option value="1">数字徽章</Option>
                  <Option value="2">PFP</Option>
                  <Option value="3">账户-DP</Option>
                  <Option value="4">数字门票</Option>
                  <Option value="5">Pass卡</Option>
                  <Option value="6">账户-BC</Option>
                  <Option value="7">满减</Option>
                  <Option value="8">权限</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={4} lg={4}>
              <Form.Item label="资产符号" name="symbol">
                <Input placeholder="请输入资产符号" />
              </Form.Item>
            </Col>
            <Col xs={24} sm={24} md={7} lg={7}>
              <Form.Item>
                <Space size="middle">
                  <Button type="primary" htmlType="submit">
                    搜索
                  </Button>
                  <Button onClick={onReset}>
                    重置
                  </Button>
                </Space>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Card>

      {/* 卡片列表 */}
      <div style={{ marginBottom: 16 }}>
        <Row gutter={[16, 16]}>
          {dataSource.filter(record => record && record.serialNo).map((record) => {
            const statusInfo = getStatusInfo(record.status || '0');
            const typeText = getCollectionTypeText(record.collectionType || '0');
            
            return (
              <Col xs={24} sm={12} md={8} lg={6} key={record.serialNo}>
                <Card
                  loading={loading}
                  style={{ width: '100%', height: '100%' }}
                  styles={{ body: { paddingBottom: 12 } }}
                  cover={
                    <div
                      style={{
                        height: 120,
                        background: `linear-gradient(135deg, ${statusInfo.color === 'processing' ? '#1890ff' : 
                                    statusInfo.color === 'success' ? '#52c41a' : '#d9d9d9'} 0%, 
                                    ${statusInfo.color === 'processing' ? '#40a9ff' : 
                                    statusInfo.color === 'success' ? '#73d13d' : '#f0f0f0'} 100%)`,
                      }}
                    />
                  }
                  actions={[
                    <EyeOutlined
                      key="view"
                      title="查看详情"
                      onClick={() => toViewContractTemplate(record)}
                    />,
                    <ShopOutlined
                      key="shelves"
                      title="上架资产"
                      onClick={() => putOnShelves(record)}
                    />,
                  ]}
                >
                  <Meta
                    avatar={
                      <Avatar
                        style={{
                          backgroundColor: statusInfo.color === 'processing' ? '#1890ff' : 
                                         statusInfo.color === 'success' ? '#52c41a' : '#d9d9d9',
                          color: '#fff',
                        }}
                        size="large"
                      >
                        {(record.symbol || 'N')[0].toUpperCase()}
                      </Avatar>
                    }
                    title={
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Text strong ellipsis style={{ maxWidth: '70%' }}>
                          {record.name || '未知名称'}
                        </Text>
                        <Tag color={statusInfo.color}>
                          {statusInfo.text}
                        </Tag>
                      </div>
                    }
                    description={
                      <div>
                        <div style={{ marginBottom: 8 }}>
                          <Text type="secondary" style={{ fontSize: 12 }}>
                          合约地址: 
                          </Text>
                          <Text copyable style={{ fontSize: 12, marginLeft: 4 }}>
                            {record.contractAddress}
                          </Text>
                        </div>
                        
                        <div style={{ marginBottom: 8 }}>
                          <Space size="small" wrap>
                            <Tag color="blue">{record.symbol || '-'}</Tag>
                            <Tag color="green">{typeText}</Tag>
                          </Space>
                        </div>
                        
                      </div>
                    }
                  />
                </Card>
              </Col>
            );
          })}
        </Row>
        
        {/* 空状态 */}
        {!loading && dataSource.length === 0 && (
          <Card style={{ textAlign: 'center', padding: '40px' }}>
            <Text type="secondary">暂无数据</Text>
          </Card>
        )}
      </div>

      {/* 分页 */}
      {total > 0 && (
        <Card>
          <Pagination
            current={current}
            pageSize={pageSize}
            total={total}
            showSizeChanger
            showQuickJumper
            showTotal={(total, range) => `第 ${range[0]}-${range[1]} 条/共 ${total} 条`}
            onChange={onPageChange}
            style={{ textAlign: 'right' }}
          />
        </Card>
      )}

      {/* 查看详情模态框 */}
      <Modal
        title="数字资产信息"
        width={800}
        centered
        open={isViewTemplateModal}
        onOk={() => setIsViewTemplateModal(false)}
        onCancel={() => setIsViewTemplateModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions>
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="资产编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="数字资产类型">
            {handleViewRecordOfCollectionType()}
          </Descriptions.Item>
          <Descriptions.Item label="资产名称">
            {isViewRecord?.name}
          </Descriptions.Item>
          <Descriptions.Item label="资产合约地址">
            {isViewRecord?.contractAddress}
          </Descriptions.Item>
          <Descriptions.Item label="资产符号">
            {isViewRecord?.symbol}
          </Descriptions.Item>
          <Descriptions.Item label="资产总供应量">
            {isViewRecord?.totalSupply}
          </Descriptions.Item>
          <Descriptions.Item label="库存">
            {isViewRecord?.stock}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议">
            {isViewRecord?.contractProtocolNo}
          </Descriptions.Item>
          <Descriptions.Item label="资产链类型">
            {isViewRecord?.chainType}
          </Descriptions.Item>
          <Descriptions.Item label="资产链链网络环境">
            {isViewRecord?.chainEnv}
          </Descriptions.Item>
          <Descriptions.Item label="资产赞助">
            {/* {isViewRecord?.sponsorFlag} */}
            {handleViewRecordOfSponsorFlag()}
          </Descriptions.Item>
          <Descriptions.Item label="是否基于联合曲线铸造">
            {/* {isViewRecord?.bondingCurveFlag} */}
            {handleViewRecordOfBondingCurveFlag()}
          </Descriptions.Item>
          <Descriptions.Item label="是否是同质化铸造NFT">
            {/* {isViewRecord?.metadataImageSameFlag} */}
            {handleViewRecordOfMetadataImageSameFlag()}
          </Descriptions.Item>

          <Descriptions.Item label="创建者">
            {isViewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
