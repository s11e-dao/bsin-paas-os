import React, { useState, useEffect } from 'react';
import QRCode from 'qrcode.react';
import { ArrowDownOutlined, ArrowUpOutlined } from '@ant-design/icons';
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Select,
  Popconfirm,
  Descriptions,
  InputNumber,
  Tabs,
  Card,
  Col,
  Row,
  Statistic,
  Table,
  Space,
  Tooltip,
  Divider,
  Avatar,
  Tag,
  Typography,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined, ShopOutlined, SettingOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import columnsOpenCardData, { columnsOpenCardDataType } from './openCardData';
import columnsTransferData, { columnsTransferDataType } from './transferData';
import columnsItemData, { columnsItemDataType } from './itemData';
import columnsObtainCodeData, {
  columnsObtainCodeDataType,
} from '../AssetsItem/obtaincodeData';

import {
  addDigitalAssetsItem,
  deleteDigitalAssetsItem,
  getDigitalAssetsItemDetail,
  getDigitalAssetsItemObtainCodePageList,
  getDigitalAssetsItemPageList,
} from '../AssetsItem/service';

import {
  getDigitalAssetsCollectionPageList,
  putOnShelvesDigitalAssetsCollection,
  getDigitalAssetsCollectionDetail,
} from '../AssetsCollection/service';

import {
  getDigitalPassCardOpenPageList,
  getDigitalPassCardOpenDetail,
  getDigitalPassCardDetail,
  getDigitalPassCardTransferPageList,
  getDigitalPassCardTransferDetail,
  openPassCard,
  openPassCardByProfileFollow,
} from './service';

import { getCustomerProfilePageList } from '../../Profile/service';

import TableTitle from '../../../components/TableTitle';
import sloganLogo from '../../../assets/s11e-slogan.png';

import styles from './index.css';
import { EyeOutlined, EditOutlined, QrcodeOutlined, KeyOutlined, UploadOutlined } from '@ant-design/icons';

const { Text, Title } = Typography; 

export default ({ setCurrentContent, putOnShelves, configAssetsItem }) => {
  let biganH5 = process.env.biganH5Url;

  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isOpenPassCardModal, setIsOpenPassCardModal] = useState(false);
  // 查看模态框
  const [isViewAssetsItemModal, setIsViewAssetsItemModal] = useState(false);

  // 查看collection模态框
  const [
    isViewAssetsCollectionModal,
    setIsViewAssetsCollectionModal,
  ] = useState(false);

  // 查看开卡详情模态框
  const [isViewPassCardOpenModal, setIsViewPassCardOpenModal] = useState(false);
  // 查看流转详情模态框
  const [
    isViewPassCardTransferModal,
    setIsViewPassCardTransferModal,
  ] = useState(false);

  // 查看领取二维码模态框
  const [
    isViewAssetsItemObtainQRCodeModal,
    setIsViewAssetsItemObtainQRCodeModal,
  ] = useState(false);

  // 查看领取码模态框
  const [
    isViewAssetsItemObtainCodeModal,
    setIsViewAssetsItemObtainCodeModal,
  ] = useState(false);

  // 查看
  const [isViewCollectionRecord, setIsViewCollectionRecord] = useState({});

  const [isViewItemRecord, setIsViewItemRecord] = useState({});

  // 卡片数据源和加载状态
  const [cardDataSource, setCardDataSource] = useState([]);
  const [cardLoading, setCardLoading] = useState(false);
  
  // 当前选中的tab
  const [activeTab, setActiveTab] = useState('3'); // 默认选中"数字会员卡集合列表"

  // 查看会员卡开卡详情
  const [isViewOpenRecord, setIsViewOpenRecord] = useState({});

  // 查看会员卡开卡详情
  const [isViewTransferRecord, setIsViewTransferRecord] = useState({});

  // 查看领取码
  const [isViewObtainCodeRecord, setIsViewObtainCodeRecord] = useState({});

  // 查看领取码
  const [isViewObtainQRCodeRecord, setIsViewObtainQRCodeRecord] = useState({});

  // 商户发行的会员卡列表
  const [merchantPassCardList, setMerchantPassCardList] = useState([]);
  const [choosedPassCardSerialNo, setChoosedPassCardSerialNo] = useState('');

  const [openMethod, setOpenMethod] = useState('');

  const [customerProfileList, setCustomerProfileList] = useState([]);

  useEffect(() => {
    // 查询合约模板协议
    let params = {
      current: '1',
      pageSize: '99',
    };
    getCustomerProfilePageList(params).then((res) => {
      setCustomerProfileList(res?.data);
    });
    
    // 加载卡片数据
    loadCardData();
    
    // 恢复保存的tab状态
    const savedTab = localStorage.getItem('passCardActiveTab');
    if (savedTab) {
      setActiveTab(savedTab);
      localStorage.removeItem('passCardActiveTab'); // 使用后清除
    }
  }, []);

  // 加载卡片数据
  const loadCardData = async () => {
    setCardLoading(true);
    try {
      const params = {
        current: 1,
        pageSize: 100,
        assetsType: '5', // pass卡类型
      };
      const res = await getDigitalAssetsCollectionPageList(params);
      setCardDataSource(res.data || []);
    } catch (error) {
      console.error('加载卡片数据失败:', error);
    } finally {
      setCardLoading(false);
    }
  };

  // 获取状态信息
  const getStatusInfo = (status: string) => {
    const statusMap = {
      '0': { text: '草稿', color: 'default' },
      '1': { text: '已发布', color: 'processing' },
      '2': { text: '已上架', color: 'success' },
      '3': { text: '已下架', color: 'warning' },
    };
    return statusMap[status] || { text: '未知', color: 'default' };
  };

  // 获取集合类型文本
  const getCollectionTypeText = (collectionType: string) => {
    const typeMap = {
      '1': '数字徽章',
      '2': 'PFP',
      '3': '数字积分',
      '4': '数字门票',
      '5': 'Pass卡',
      '6': '徽章/门票',
    };
    return typeMap[collectionType] || collectionType;
  };

  // 获取表单
  const [FormRef] = Form.useForm();

  /**
   * 以下内容为表格相关
   */

  // 会员卡发行表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 会员卡发行表头数据
  const columnsOpenCard: ProColumns<
    columnsOpenCardDataType
  >[] = columnsOpenCardData;

  // 会员卡转移表头数据
  const columnsTransfer: ProColumns<
    columnsTransferDataType
  >[] = columnsTransferData;

  // 领取码表头数据
  const columnsObtainCode: ProColumns<
    columnsObtainCodeDataType
  >[] = columnsObtainCodeData;

  // 会员卡发行操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <Space>
      <Tooltip title="查看">
        <a onClick={() => toViewAssetsCollection(record)}>
          <EyeOutlined /> 查看
        </a>
      </Tooltip>
      <Divider type="vertical" />
      <Tooltip title="上架">
        <a onClick={() => putOnShelves(record)}>
          <UploadOutlined /> 上架
        </a>
      </Tooltip>
    </Space>
  );

  const actionItemRender: any = (text: any, record: any, index: number) => (
    <Space>
      <Tooltip title="查看">
        <a onClick={() => toViewAssetsItem(record)}>
          <EyeOutlined />
        </a>
      </Tooltip>
      <Divider type="vertical" />
      <Tooltip title="权益配置">
        <a onClick={() => toConfigActivityEquity(record)}>
          <EditOutlined /> 权益配置
        </a>
      </Tooltip>
      <Divider type="vertical" />
      <Tooltip title="领取二维码">
        <a onClick={() => toViewAssetsItemClaimQRCode(record)}>
          <QrcodeOutlined /> 领取二维码
        </a>
      </Tooltip>
      <Divider type="vertical" />
      <Tooltip title="领取口令">
        <a onClick={() => toViewAssetsItemClaimCode(record)}>
          <KeyOutlined /> 领取口令
        </a>
      </Tooltip>
    </Space>
  );

  // 开卡记录
  const actionOpenCardRender: any = (text: any, record: any, index: number) => (
    <Space>
      <Tooltip title="查看">
        <a onClick={() => toViewPassCardOpenDetail(record)}>
          <EyeOutlined /> 查看
        </a>
      </Tooltip>
    </Space>
  );

  // 流转记录
  const actionTransferRender: any = (text: any, record: any, index: number) => (
    <Space>
      <Tooltip title="查看">
        <a onClick={() => toViewPassCardTransferDetail(record)}>
          <EyeOutlined /> 查看
        </a>
      </Tooltip>
    </Space>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  // 自定义item数据的表格头部数据
  columnsItemData.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionItemRender) : undefined;
  });

  // 自定义开卡数据的表格头部数据
  columnsOpenCard.forEach((item: any) => {
    item.dataIndex === 'action'
      ? (item.render = actionOpenCardRender)
      : undefined;
  });
  // 自定义流转数据的表格头部数据
  columnsTransfer.forEach((item: any) => {
    item.dataIndex === 'action'
      ? (item.render = actionTransferRender)
      : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  const itemActionRef = React.useRef<ActionType>();

  // 领取码 Table action 的引用，便于自定义触发
  const obtainCodeActionRef = React.useRef<ActionType>();
  // 开卡 Table action 的引用，便于自定义触发
  const openPassCardActionRef = React.useRef<ActionType>();
  // 流转 Table action 的引用，便于自定义触发
  const transferPassCardActionRef = React.useRef<ActionType>();

  /**
   * 以下内容为操作相关
   */

  // 开卡，执行 profile 的 follow 操作
  const toOpenPassCard = () => {
    // 请求商户发行的会员卡
    let params = {
      current: '1',
      pageSize: '99',
      // assetsType: '5',
      assetsTypes: ['5'],
    };
    // getDigitalAssetsCollectionPageList(params).then((res) => {
    //   setMerchantPassCardList(res?.data);
    // });
    getDigitalAssetsItemPageList(params).then((res) => {
      setMerchantPassCardList(res?.data);
    });

    setIsOpenPassCardModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmOpenPassCard = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let requestParam = FormRef.getFieldsValue();
        console.log(requestParam);

        if (openMethod == '1') {
          console.log(requestParam);
          openPassCardByProfileFollow(requestParam).then((res) => {
            console.log('issue', res);
            if (res?.code == 0) {
              // 重置输入的表单
              // FormRef.resetFields();
              // 刷新proTable
              actionRef.current?.reload();
              setIsOpenPassCardModal(false);
            } else {
              message.error(`开卡失败： ${res?.message}`);
            }
          });
        } else {
          openPassCard(requestParam).then((res) => {
            console.log('issue', res);
            if (res?.code == 0) {
              // 重置输入的表单
              // FormRef.resetFields();
              // 刷新proTable
              actionRef.current?.reload();
              setIsOpenPassCardModal(false);
            } else {
              message.error(`开卡失败： ${res?.message}`);
            }
          });
        }
      })
      .catch(() => {});
  };

  /**
   * 取消开卡
   */
  const onCancelOpenPassCard = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsOpenPassCardModal(false);
  };

  /**
   * 去配置权益
   */
  const toConfigActivityEquity = async (record) => {
    // 保存当前tab状态到localStorage
    localStorage.setItem('passCardActiveTab', activeTab);
    // 条件分类：1、会员等级 2 数字资产 3 任务 4 活动
    record.category = '2';
    configAssetsItem(record, 'configEquity');
  };

  /**
   * 删除模板
   */
  const toDelAssetsItem = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteDigitalAssetsItem({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === 0) {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看会员卡item详情
   */

  const toViewAssetsItem = async (record) => {
    let { serialNo } = record;
    let viewRes = await getDigitalAssetsItemDetail({ serialNo });
    setIsViewAssetsItemModal(true);
    console.log('viewRes', viewRes);
    setIsViewItemRecord(viewRes.data);
  };

  const toViewAssetsCollection = async (record) => {
    let { serialNo } = record;
    let viewRes = await getDigitalAssetsCollectionDetail({ serialNo });
    setIsViewAssetsCollectionModal(true);
    console.log('viewRes', viewRes);
    setIsViewCollectionRecord(viewRes.data);
  };

  /**
   * 查看开卡详情详情
   */
  const toViewPassCardOpenDetail = async (record) => {
    let { serialNo } = record;
    let viewRes = await getDigitalPassCardOpenDetail({ serialNo });
    setIsViewPassCardOpenModal(true);
    console.log('viewRes', viewRes);
    setIsViewOpenRecord(viewRes.data);
  };

  /**
   * 查看流转详情详情
   */
  const toViewPassCardTransferDetail = async (record) => {
    let { serialNo } = record;
    let viewRes = await getDigitalPassCardTransferDetail({ serialNo });
    setIsViewPassCardTransferModal(true);
    console.log('viewRes', viewRes);
    setIsViewTransferRecord(viewRes.data);
  };

  /**
   * 查看领取二维码
   */
  const toViewAssetsItemClaimQRCode = async (record) => {
    let { serialNo } = record;
    let viewRes = await getDigitalAssetsItemDetail({ serialNo });
    console.log('viewObtainCodeRes', viewRes);
    setIsViewObtainQRCodeRecord(viewRes.data);
    setIsViewAssetsItemObtainQRCodeModal(true);
  };

  /**
   * 查看领取口令
   */
  const toViewAssetsItemClaimCode = async (record) => {
    setIsViewObtainCodeRecord(record);
    setIsViewAssetsItemObtainCodeModal(true);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfAssetsType = () => {
    let { digitalAssetsItem } = isViewItemRecord;
    let assetsType = digitalAssetsItem?.assetsType;
    if (assetsType == `1`) {
      return '数字资产';
    } else if (assetsType == `2`) {
      return 'PFP';
    } else if (assetsType == `3`) {
      return '账户-DP';
    } else if (assetsType == `4`) {
      return '数字门票';
    } else if (assetsType == `5`) {
      return 'Pass卡';
    } else if (assetsType == `6`) {
      return '账户-BC';
    } else if (assetsType == `7`) {
      return '满减';
    } else if (assetsType == `8`) {
      return '权限';
    } else if (assetsType == `9`) {
      return '会员等级';
    } else {
      return assetsType;
    }
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfMultimediaType = () => {
    let { multimediaType } = isViewOpenRecord;
    // 多媒体类型： 1 图片  2 gif 3 视频 4 音频 5 json 6 文件夹
    let typeText = multimediaType;
    if (typeText == '1') {
      return '图片';
    } else if (typeText == '2') {
      return 'gif';
    } else if (typeText == '3') {
      return '视频';
    } else if (typeText == '4') {
      return '音频';
    } else if (typeText == '5') {
      return 'json';
    } else if (typeText == '6') {
      return '文件夹';
    }
    return typeText;
  };

  const handleViewRecordOfObtainMethod = () => {
    let { obtainMethod } = isViewItemRecord;
    if (obtainMethod == '1') {
      return '免费领取/空投';
    } else if (obtainMethod == '2') {
      return '购买';
    } else if (obtainMethod == '3') {
      return '固定口令领取';
    } else if (obtainMethod == '4') {
      return '随机口令';
    } else if (obtainMethod == '5') {
      return '盲盒';
    } else {
      return obtainMethod;
    }
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfCollectionType = () => {
    let { assetsType } = isViewCollectionRecord;
    if (assetsType == `1`) {
      return '数字资产';
    } else if (assetsType == `2`) {
      return 'PFP';
    } else if (assetsType == `3`) {
      return '账户-DP';
    } else if (assetsType == `4`) {
      return '数字门票';
    } else if (assetsType == `5`) {
      return 'Pass卡';
    } else if (assetsType == `6`) {
      return '账户-BC';
    } else if (assetsType == `7`) {
      return '满减';
    } else if (assetsType == `8`) {
      return '权限';
    } else if (assetsType == `9`) {
      return '会员等级';
    } else {
      return assetsType;
    }
  };
  const handleViewRecordOfSponsorFlag = () => {
    let { sponsorFlag } = isViewCollectionRecord;
    if (sponsorFlag == '0') {
      return '否';
    } else if (sponsorFlag == '1') {
      return '是';
    } else {
      return sponsorFlag;
    }
  };
  const handleViewRecordOfBondingCurveFlag = () => {
    let { bondingCurveFlag } = isViewCollectionRecord;
    if (bondingCurveFlag == '0') {
      return '否';
    } else if (bondingCurveFlag == '1') {
      return '是';
    } else {
      return bondingCurveFlag;
    }
  };

  const handleViewRecordOfMetadataImageSameFlag = () => {
    let { metadataImageSameFlag } = isViewCollectionRecord;
    if (metadataImageSameFlag == '0') {
      return '否';
    } else if (metadataImageSameFlag == '1') {
      return '是';
    } else {
      return 'null';
    }
  };

  const typeOnChange = (value) => {
    console.log(value);
    setChoosedPassCardSerialNo(value);
    // let params = {
    //   pageSize: 99,
    //   current: 1,
    //   type: value,
    // };
    // // 请求后台获取条件
    // getConditionPageList(params).then((res) => {
    //   console.log(res);
    //   let conditionListTemp = [];
    //   res?.data.map((item) => {
    //     console.log(item);
    //     let conditionJson = {
    //       serialNo: item.serialNo,
    //       name: item.name,
    //     };
    //     conditionListTemp.push(conditionJson);
    //   });
    //   setConditionList(conditionListTemp);
    // });
  };

  const openMethodChange = (value) => {
    console.log('openMethodChange:' + value);
    setOpenMethod(value);
  };

  const customerProfileNoChange = (value) => {
    console.log('customerProfileNoChange', value);
    FormRef.setFieldValue('contractProtocolNo', value);
  };

  return (
    <div>
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}>
          <Card bordered={false} style={{ height: 180, display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <Statistic
              title="会员卡发行量"
              value={11.28}
              precision={2}
              valueStyle={{ color: '#3f8600' }}
              prefix={<ArrowUpOutlined />}
              suffix="%"
            />
            <Button
              style={{ marginTop: 16, height: 32 }}
              type="primary"
              onClick={async () => {
                setCurrentContent('issuePassCard');
              }}
            >
              发行会员卡
            </Button>
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false} style={{ height: 180, display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <Statistic
              title="开卡数量"
              value={9.3}
              precision={2}
              valueStyle={{ color: '#cf1322' }}
              prefix={<ArrowDownOutlined />}
              suffix="%"
            />
            <Button
              style={{ marginTop: 16, height: 32 }}
              type="primary"
              onClick={async () => {
                toOpenPassCard();
              }}
            >
              开卡
            </Button>
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false} style={{ height: 180, display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <Statistic
              title="会员收入"
              value={9.3}
              precision={2}
              valueStyle={{ color: '#cf1322' }}
              prefix={<ArrowDownOutlined />}
              suffix="%"
            />
            <Button style={{ marginTop: 16, height: 32 }} type="dashed">
              社区收入
            </Button>
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false} style={{ height: 180, display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <Statistic
              title="会员支出"
              value={11.28}
              precision={2}
              valueStyle={{ color: '#3f8600' }}
              prefix={<ArrowUpOutlined />}
              suffix="%"
            />
            <Button style={{ marginTop: 16, height: 32 }} type="dashed">
              社区支出
            </Button>
          </Card>
        </Col>
        <Card bordered={false} style={{ width: '100%', marginTop: '10px' }}>
          <Tabs activeKey={activeTab} onChange={setActiveTab}>
            <Tabs.TabPane tab="开卡记录" key="1">
              {/* 会员卡开卡记录表格 */}
              <ProTable<columnsOpenCardDataType>
                headerTitle={<TableTitle title="数字会员卡开卡记录" />}
                scroll={{ x: 900 }}
                bordered
                // 表头
                columns={columnsOpenCard}
                actionRef={openPassCardActionRef}
                // 请求获取的数据
                request={async (params) => {
                  // console.log(params);
                  // 资产类型：1、数字徽章 2、PFP 3、积分 4、门票 5、pass卡
                  params.assetsType = '5';
                  let res = await getDigitalPassCardOpenPageList({
                    ...params,
                    pageNum: params.current,
                  });
                  console.log('😒', res);
                  const result = {
                    data: res.data,
                    total: res.pagination.totalSize,
                  };
                  return result;
                }}
                rowKey="serialNo"
                // 搜索框配置
                search={{
                  labelWidth: 'auto',
                }}
                // 搜索表单的配置
                form={{
                  ignoreRules: false,
                }}
                pagination={{
                  pageSize: 10,
                }}
              />
            </Tabs.TabPane>
            <Tabs.TabPane tab="流转记录" key="2">
              {/* <Table columns={columnsTransfer} dataSource={data} /> */}
              {/* 会员卡开卡记录表格 */}
              <ProTable<columnsTransferDataType>
                headerTitle={<TableTitle title="数字会员卡流转记录" />}
                scroll={{ x: 900 }}
                bordered
                // 表头
                columns={columnsTransfer}
                actionRef={transferPassCardActionRef}
                // 请求获取的数据
                request={async (params) => {
                  // console.log(params);
                  // 资产类型：1、数字徽章 2、PFP 3、积分 4、门票 5、pass卡
                  params.assetsType = '5';
                  let res = await getDigitalPassCardTransferPageList({
                    ...params,
                    pageNum: params.current,
                  });
                  console.log('😒', res);
                  const result = {
                    data: res.data,
                    total: res.pagination.totalSize,
                  };
                  return result;
                }}
                rowKey="serialNo"
                // 搜索框配置
                search={{
                  labelWidth: 'auto',
                }}
                // 搜索表单的配置
                form={{
                  ignoreRules: false,
                }}
                pagination={{
                  pageSize: 10,
                }}
              />
            </Tabs.TabPane>
            <Tabs.TabPane tab="数字会员卡集合列表" key="3">
              {/* 会员卡发行记录卡片 */}
              <div style={{ marginBottom: 16 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                  <TableTitle title="数字会员卡发行记录" />
                  <Button 
                    type="primary" 
                    icon={<PlusOutlined />}
                    onClick={loadCardData}
                    loading={cardLoading}
                  >
                    刷新数据
                  </Button>
                </div>
                <Row gutter={[16, 16]}>
                  {cardDataSource.filter(record => record && record.serialNo).map((record) => {
                    const statusInfo = getStatusInfo(record.status || '0');
                    const typeText = getCollectionTypeText(record.collectionType || '5');
                    
                    return (
                      <Col xs={24} sm={12} md={8} lg={6} key={record.serialNo}>
                        <Card
                          loading={cardLoading}
                          style={{ width: '100%', height: '100%', minWidth: 280 }}
                          styles={{ body: { paddingBottom: 12 } }}
                          cover={
                            <div
                              style={{
                                height: 120,
                                background: `linear-gradient(135deg, ${statusInfo.color === 'processing' ? '#1890ff' : 
                                            statusInfo.color === 'success' ? '#52c41a' : 
                                            statusInfo.color === 'warning' ? '#faad14' : '#d9d9d9'} 0%, 
                                            ${statusInfo.color === 'processing' ? '#40a9ff' : 
                                            statusInfo.color === 'success' ? '#73d13d' : 
                                            statusInfo.color === 'warning' ? '#ffc53d' : '#f0f0f0'} 100%)`,
                              }}
                            />
                          }
                          actions={[
                            <EyeOutlined
                              key="view"
                              title="查看详情"
                              onClick={() => toViewAssetsCollection(record)}
                            />,
                            <SettingOutlined
                              key="config"
                              title="权益配置"
                              onClick={() => toConfigActivityEquity(record)}
                            />,
                            <ShopOutlined
                              key="shelves"
                              title="上架资产"
                              onClick={() => putOnShelves(record)}
                            />,
                          ]}
                        >
                          <Card.Meta
                            avatar={
                              <Avatar
                                style={{
                                  backgroundColor: statusInfo.color === 'processing' ? '#1890ff' : 
                                                 statusInfo.color === 'success' ? '#52c41a' : 
                                                 statusInfo.color === 'warning' ? '#faad14' : '#d9d9d9',
                                  color: '#fff',
                                }}
                                size="large"
                              >
                                {(record.symbol || 'P')[0].toUpperCase()}
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
                                
                                <div style={{ marginBottom: 8 }}>
                                  <Text type="secondary" style={{ fontSize: 12 }}>
                                    ID: {record.serialNo}
                                  </Text>
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
                {!cardLoading && cardDataSource.length === 0 && (
                  <Card style={{ textAlign: 'center', padding: '40px' }}>
                    <Text type="secondary">暂无数字会员卡数据</Text>
                  </Card>
                )}
              </div>
            </Tabs.TabPane>
            <Tabs.TabPane tab="数字会员卡item列表" key="4">
              {/* 会员卡发行记录表格 */}
              <ProTable<columnsItemDataType>
                headerTitle={<TableTitle title="数字会员卡上架记录" />}
                scroll={{ x: 900 }}
                bordered
                // 表头
                columns={columnsItemData}
                actionRef={itemActionRef}
                request={async (params) => {
                  // 品牌商户发行资产类型 1、数字徽章 2、PFP 3、数字积分 4、数字门票 5、pass卡 6、徽章/门票
                  params.assetsTypes = ['5'];
                  let res = await getDigitalAssetsItemPageList({
                    ...params,
                    pageNum: params.current,
                  });
                  console.log('😒', res);
                  const result = {
                    data: res.data,
                    total: res.pagination.totalSize,
                  };
                  return result;
                }}
                rowKey="serialNo"
                // 搜索框配置
                search={{
                  labelWidth: 'auto',
                }}
                // 搜索表单的配置
                form={{
                  ignoreRules: false,
                }}
                pagination={{
                  pageSize: 10,
                }}
              />
            </Tabs.TabPane>
          </Tabs>
        </Card>
      </Row>

      <Modal
        title="开通会员卡"
        centered
        open={isOpenPassCardModal}
        onOk={confirmOpenPassCard}
        // onOk={() => setIsOpenPassCardModal(false)}
        onCancel={() => setIsOpenPassCardModal(false)}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ type: '0', typeNo: '0' }}
        >
          {/* <Form.Item
            label="开卡方式"
            name="openMethod"
            rules={[{ required: true, message: '请选择开卡方式!' }]}
          >
            <Select
              style={{ width: '100%' }}
              onChange={(value) => {
                openMethodChange(value);
              }}
            >
              <Option value="0">请选择发行方式</Option>
              <Option value="1"> 通过品牌profile发行</Option>
              <Option value="2"> 通过数字资产Item发行</Option>
            </Select>
          </Form.Item> */}

          {openMethod == '2' ? null : (
            <Form.Item
              label="profile列表"
              name="customerProfileNo"
              rules={[{ required: true, message: '请选择协议类型!' }]}
            >
              <Select
                style={{ width: '100%' }}
                onChange={(value) => {
                  customerProfileNoChange(value);
                }}
              >
                <Option value="1">请选择profile编号</Option>
                {customerProfileList?.map((customerProfile) => {
                  return (
                    <Option value={customerProfile?.serialNo}>
                      {(customerProfile?.serialNo).slice(-4) +
                        '-' +
                        customerProfile?.name}
                    </Option>
                  );
                })}
              </Select>
            </Form.Item>
          )}

          <Form.Item
            label="会员卡资产编号"
            name="digitalAssetsItemNo"
            rules={[{ required: true, message: '请选择会员卡资产编号!' }]}
          >
            <Select
              style={{ width: '100%' }}
              onChange={(value) => {
                typeOnChange(value);
              }}
            >
              <Option value="1">请选择会员卡资产</Option>
              {merchantPassCardList?.map((merchantPassCard) => {
                return (
                  <Option value={merchantPassCard?.serialNo}>
                    {(merchantPassCard?.serialNo).slice(-4) +
                      '-' +
                      merchantPassCard?.name}
                  </Option>
                );
              })}
            </Select>
          </Form.Item>

          <Form.Item
            label="数量"
            name="amount"
            rules={[{ required: true, message: '请输入数量!' }]}
          >
            <InputNumber min={1} />
          </Form.Item>

          <Form.Item
            label="客户编号"
            name="customerNo"
            rules={[{ required: true, message: '请输入客户ID!' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>

      {/* 查看详情模态框 */}
      <Modal
        title="查看数字会员卡集合详情"
        width={800}
        centered
        open={isViewAssetsCollectionModal}
        onOk={() => setIsViewAssetsCollectionModal(false)}
        onCancel={() => setIsViewAssetsCollectionModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions>
          <Descriptions.Item label="租户ID">
            {isViewCollectionRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="资产编号">
            {isViewCollectionRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="数字资产类型">
            {handleViewRecordOfCollectionType()}
          </Descriptions.Item>
          <Descriptions.Item label="资产名称">
            {isViewCollectionRecord?.name}
          </Descriptions.Item>
          <Descriptions.Item label="资产合约地址">
            {isViewCollectionRecord?.contractAddress}
          </Descriptions.Item>
          <Descriptions.Item label="资产符号">
            {isViewCollectionRecord?.symbol}
          </Descriptions.Item>
          <Descriptions.Item label="资产总供应量">
            {isViewCollectionRecord?.totalSupply}
          </Descriptions.Item>
          <Descriptions.Item label="库存">
            {isViewCollectionRecord?.stock}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议">
            {isViewCollectionRecord?.contractProtocolNo}
          </Descriptions.Item>
          <Descriptions.Item label="资产链类型">
            {isViewCollectionRecord?.chainType}
          </Descriptions.Item>
          <Descriptions.Item label="资产链链网络环境">
            {isViewCollectionRecord?.chainEnv}
          </Descriptions.Item>
          <Descriptions.Item label="资产赞助">
            {/* {isViewCollectionRecord?.sponsorFlag} */}
            {handleViewRecordOfSponsorFlag()}
          </Descriptions.Item>
          <Descriptions.Item label="是否基于联合曲线铸造">
            {/* {isViewCollectionRecord?.bondingCurveFlag} */}
            {handleViewRecordOfBondingCurveFlag()}
          </Descriptions.Item>
          <Descriptions.Item label="是否是同质化铸造NFT">
            {/* {isViewCollectionRecord?.metadataImageSameFlag} */}
            {handleViewRecordOfMetadataImageSameFlag()}
          </Descriptions.Item>

          <Descriptions.Item label="创建者">
            {isViewCollectionRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewCollectionRecord?.createTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>

      {/* 查看详情模态框 */}
      <Modal
        title="查看数字会员卡Item详情"
        width={800}
        centered
        open={isViewAssetsItemModal}
        onOk={() => setIsViewAssetsItemModal(false)}
        onCancel={() => setIsViewAssetsItemModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="数字资产信息">
          <Descriptions.Item label="租户ID">
            {isViewItemRecord?.digitalAssetsItem?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="资产编号">
            {isViewItemRecord?.digitalAssetsItem?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="数字资产类型">
            {handleViewRecordOfAssetsType()}
          </Descriptions.Item>
          <Descriptions.Item label="资产名称">
            {isViewItemRecord?.digitalAssetsItem?.assetsName}
          </Descriptions.Item>
          <Descriptions.Item label="tokenId">
            {isViewItemRecord?.digitalAssetsItem?.tokenId}
          </Descriptions.Item>
          <Descriptions.Item label="数字资产数量">
            {isViewItemRecord?.digitalAssetsItem?.quantity}
          </Descriptions.Item>
          <Descriptions.Item label="数字资产库存">
            {isViewItemRecord?.digitalAssetsItem?.inventory}
          </Descriptions.Item>
          <Descriptions.Item label="数字资产价格">
            {isViewItemRecord?.digitalAssetsItem?.price}
          </Descriptions.Item>
          <Descriptions.Item label="获取方式">
            {/* {isViewItemRecord?.obtainMethod} */}
            {handleViewRecordOfObtainMethod()}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewItemRecord?.digitalAssetsItem?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewItemRecord?.digitalAssetsItem?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="资产描述">
            {isViewItemRecord?.digitalAssetsItem?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>

      <Modal
        title="查看流转详情"
        width={800}
        centered
        open={isViewPassCardTransferModal}
        onOk={() => setIsViewPassCardTransferModal(false)}
        onCancel={() => setIsViewPassCardTransferModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="铸造记录">
          <Descriptions.Item label="租户ID">
            {isViewTransferRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="转账流水号">
            {isViewTransferRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="交易Hash">
            {isViewTransferRecord?.txHash}
          </Descriptions.Item>
          <Descriptions.Item label="tokenId">
            {isViewTransferRecord?.tokenId}
          </Descriptions.Item>
          <Descriptions.Item label="交易数量">
            {isViewTransferRecord?.amount}
          </Descriptions.Item>
          <Descriptions.Item label="metadataImage">
            {isViewTransferRecord?.metadataImage}
          </Descriptions.Item>
          <Descriptions.Item label="metadataUrl">
            {isViewTransferRecord?.metadataUrl}
          </Descriptions.Item>
          <Descriptions.Item label="接收人手机号">
            {isViewTransferRecord?.toPhone}
          </Descriptions.Item>
          <Descriptions.Item label="接收人姓名">
            {isViewTransferRecord?.toMinterName}
          </Descriptions.Item>
          <Descriptions.Item label="发送地址">
            {isViewTransferRecord?.fromAddress}
          </Descriptions.Item>
          <Descriptions.Item label="接收地址">
            {isViewTransferRecord?.toAddress}
          </Descriptions.Item>
          <Descriptions.Item label="发送客户号">
            {isViewTransferRecord?.fromCustomerNo}
          </Descriptions.Item>
          <Descriptions.Item label="接收客户号">
            {isViewTransferRecord?.toCustomerNo}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewTransferRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewTransferRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="协议描述">
            {isViewTransferRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>

      <Modal
        title="查看开卡详情"
        width={800}
        centered
        open={isViewPassCardOpenModal}
        onOk={() => setIsViewPassCardOpenModal(false)}
        onCancel={() => setIsViewPassCardOpenModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="开卡详情">
          <Descriptions.Item label="租户ID">
            {isViewOpenRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="铸造编号">
            {isViewOpenRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="交易Hash">
            {isViewOpenRecord?.txHash}
          </Descriptions.Item>
          <Descriptions.Item label="tokenId">
            {isViewOpenRecord?.tokenId}
          </Descriptions.Item>
          <Descriptions.Item label="铸造数量">
            {isViewOpenRecord?.amount}
          </Descriptions.Item>
          <Descriptions.Item label="多媒体类型">
            {handleViewRecordOfMultimediaType()}
          </Descriptions.Item>
          <Descriptions.Item label="资产名称">
            {isViewOpenRecord?.itemName}
          </Descriptions.Item>
          <Descriptions.Item label="metadataImage">
            {isViewOpenRecord?.metadataImage}
          </Descriptions.Item>
          <Descriptions.Item label="metadataUrl">
            {isViewOpenRecord?.metadataUrl}
          </Descriptions.Item>
          <Descriptions.Item label="铸造人手机号">
            {isViewOpenRecord?.toPhone}
          </Descriptions.Item>
          <Descriptions.Item label="铸造人姓名">
            {isViewOpenRecord?.toMinterName}
          </Descriptions.Item>
          <Descriptions.Item label="接收地址">
            {isViewOpenRecord?.toAddress}
          </Descriptions.Item>
          <Descriptions.Item label="接收客户号">
            {isViewOpenRecord?.toCustomerNo}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewOpenRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewOpenRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="协议描述">
            {isViewOpenRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>

      {/* 查看领取码模态框 */}
      <Modal
        title="查看领取码"
        width={800}
        centered
        open={isViewAssetsItemObtainCodeModal}
        onOk={() => setIsViewAssetsItemObtainCodeModal(false)}
        onCancel={() => setIsViewAssetsItemObtainCodeModal(false)}
      >
        {/*  领取码查看表格 */}
        <ProTable<columnsObtainCodeDataType>
          headerTitle={<TableTitle title="领取码" />}
          scroll={{ x: 900 }}
          bordered
          // 表头
          columns={columnsObtainCode}
          actionRef={obtainCodeActionRef}
          // 请求获取的数据
          request={async (params) => {
            // console.log(params);
            let res = await getDigitalAssetsItemObtainCodePageList({
              ...params,
              assetsNo: isViewObtainCodeRecord?.serialNo,
            });
            console.log('😒', res);
            const result = {
              data: res.data,
              total: res.pagination.totalSize,
            };
            return result;
          }}
          rowKey="serialNo"
          // 搜索框配置
          search={false}
          // 搜索表单的配置
          form={{
            ignoreRules: false,
          }}
          pagination={{
            pageSize: 10,
          }}
        />
      </Modal>

      {/* 查看领取二维码模态框 */}
      <Modal
        title="查看领取二维码"
        // width={800}
        footer={[]}
        centered
        open={isViewAssetsItemObtainQRCodeModal}
        onOk={() => setIsViewAssetsItemObtainQRCodeModal(false)}
        onCancel={() => setIsViewAssetsItemObtainQRCodeModal(false)}
      >
        <div className="">
          <div className={styles.frame}>
            <div className={styles.header}>
              <div>
                <img
                  className={styles.nftImg}
                  src={isViewObtainQRCodeRecord?.digitalAssetsItem?.coverImage}
                />
              </div>
            </div>

            <div className={styles.content}>
              <span className={styles.content_top}>
                {isViewObtainQRCodeRecord?.digitalAssetsItem?.assetsName}
              </span>
              <div style={{ height: '175px' }}>
                <div className={styles.content_left}>
                  <span className={styles.first_section}>
                    {isViewObtainQRCodeRecord?.digitalAssetsItem?.description}
                  </span>
                </div>
                <div className={styles.content_rigth}>
                  <div className={styles.qrcode}>
                    <QRCode
                      className={styles.obtainCode}
                      value={
                        biganH5 +
                        '?chainEnv=' +
                        isViewObtainQRCodeRecord?.digitalAssetsItem?.chainEnv +
                        '&serialNo=' +
                        isViewObtainQRCodeRecord?.digitalAssetsItem?.serialNo
                      }
                      size={230}
                      id="qrCode"
                    />
                  </div>
                  <span>扫码领取NFT</span>
                </div>
              </div>
              <img className={styles.logo} src={sloganLogo} />
            </div>
          </div>
        </div>
      </Modal>
    </div>
  );
};
