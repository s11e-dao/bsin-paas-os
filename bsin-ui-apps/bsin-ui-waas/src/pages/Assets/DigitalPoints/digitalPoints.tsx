import React, { useState } from 'react';
import QRCode from 'qrcode.react';
import { ArrowDownOutlined, ArrowUpOutlined } from '@ant-design/icons';
import {
  Form,
  Input,
  Modal,
  message,
  Radio,
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
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import columnsTradingData, { columnsTradingDataType } from './tradingData';
import columnsReleaseData, { columnsReleaseDataType } from './releaseData';
import columnsItemData, { columnsItemDataType } from './itemData';

import columnsObtainCodeData, {
  columnsObtainCodeDataType,
} from '../AssetsItem/obtaincodeData';

import {
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
  getDigitalPointsTradingDetail,
  getDigitalPointsReleasePageList,
  getDigitalPointsReleaseDetail,
  getDigitalPointsTradingPageList,
  getDigitalPointsDetail,
  editTokenParam,
  getTokenParam,
} from './service';

import TableTitle from '../../../components/TableTitle';
import sloganLogo from '../../../assets/s11e-slogan.png';

import styles from './index.css';

export default ({ setCurrentContent, putOnShelves, configAssetsItem }) => {
  let biganH5 = process.env.biganH5Url;

  const { TextArea } = Input;
  const { Option } = Select;
  // 查看模态框
  const [isViewAssetsItemModal, setIsViewAssetsItemModal] = useState(false);
  // 查看collection模态框
  const [
    isViewAssetsCollectionModal,
    setIsViewAssetsCollectionModal,
  ] = useState(false);
  // 查看交易详情模态框
  const [isViewTradingModal, setIsViewTradingModal] = useState(false);
  // 查看释放详情模态框
  const [isViewReleaseModal, setIsViewReleaseModal] = useState(false);

  // 查看释放详情模态框
  const [isViewConfigModel, setIsviewConfigModel] = useState(false);

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

  // 数字积分参数详情
  const [isViewTokenParamRecord, setIsViewTokenParamRecord] = useState({});

  // 查看集合详情
  const [isViewCollectionRecord, setIsViewCollectionRecord] = useState({});

  const [isViewItemRecord, setIsViewItemRecord] = useState({});

  // 查看交易记录
  const [isViewTradingRecord, setIsViewTradingRecord] = useState({});

  // 查看释放记录
  const [isViewReleaseRecord, setIsViewReleaseRecord] = useState({});

  // 查看领取码
  const [isViewObtainCodeRecord, setIsViewObtainCodeRecord] = useState({});

  // 领取码记录
  const [isViewObtainQRCodeRecord, setIsViewObtainQRCodeRecord] = useState({});

  // 获取表单
  const [FormRef] = Form.useForm();

  /**
   * 以下内容为表格相关
   */

  // 数字积分发行表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;
  // 数字积分上架表头数据
  const columnsItem: ProColumns<columnsItemDataType>[] = columnsData;

  // 交易表头数据
  const columnsTrading: ProColumns<
    columnsTradingDataType
  >[] = columnsTradingData;

  // 释放表头数据
  const columnsRelease: ProColumns<
    columnsReleaseDataType
  >[] = columnsReleaseData;

  // 表头数据
  const columnsObtainCode: ProColumns<
    columnsObtainCodeDataType
  >[] = columnsObtainCodeData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0, display: 'flex', justifyContent: "space-around" }}>
      <li>
        <a
          onClick={() => {
            toViewAssetsCollection(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <a
          onClick={() => {
            openConfigModel(record);
          }}
        >
          更新配置
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

  const actionItemRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewAssetsItem(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <a
          onClick={() => {
            toConfigActivityEquity(record);
          }}
        >
          权益配置
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <a
          onClick={() => {
            toViewAssetsItemClaimQRCode(record);
          }}
        >
          领取二维码
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <a
          onClick={() => {
            toViewAssetsItemClaimCode(record);
          }}
        >
          领取口令
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
    </ul>
  );

  // 交易记录
  const actionTradingRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewTradingItem(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
    </ul>
  );

  // 释放记录
  const actionReleaseRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewReleaseDetail(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
    </ul>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  // 自定义item数据的表格头部数据
  columnsItemData.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionItemRender) : undefined;
  });

  // 自定义交易数据的表格头部数据
  columnsTrading.forEach((item: any) => {
    item.dataIndex === 'action'
      ? (item.render = actionTradingRender)
      : undefined;
  });
  // 自定义释放数据的表格头部数据
  columnsRelease.forEach((item: any) => {
    item.dataIndex === 'action'
      ? (item.render = actionReleaseRender)
      : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  const itemActionRef = React.useRef<ActionType>();

  // 领取码 Table action 的引用，便于自定义触发
  const obtainCodeActionRef = React.useRef<ActionType>();

  // 开卡 Table action 的引用，便于自定义触发
  const tradingActionRef = React.useRef<ActionType>();
  // 流转 Table action 的引用，便于自定义触发
  const releaseActionRef = React.useRef<ActionType>();

  /**
   * 以下内容为操作相关
   */

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
   * 查看详情
   */
  const toViewAssetsItem = async (record) => {
    let { serialNo } = record;
    let viewRes = await getDigitalAssetsItemDetail({ serialNo });
    setIsViewAssetsItemModal(true);
    console.log('viewRes', viewRes);
    setIsViewItemRecord(viewRes.data);
  };

  /**
   * 去配置权益
   */
  const toConfigActivityEquity = async (record) => {
    // 条件分类：1、会员等级 2 数字资产 3 任务 4 活动
    record.category = '2';
    configAssetsItem(record, 'configEquity');
  };

  const toViewAssetsCollection = async (record) => {
    let { serialNo } = record;
    let viewRes = await getDigitalAssetsCollectionDetail({ serialNo });
    setIsViewAssetsCollectionModal(true);
    console.log('viewRes', viewRes);
    setIsViewCollectionRecord(viewRes.data);
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
   * 查看交易详情
   */
  const toViewTradingItem = async (record) => {
    let { serialNo } = record;
    let viewRes = await getDigitalPointsTradingDetail({ serialNo });
    setIsViewTradingModal(true);
    console.log('viewRes', viewRes);
    setIsViewTradingRecord(viewRes.data);
  };

  /**
   * 查看释放详情
   */
  const toViewReleaseDetail = async (record) => {
    let { serialNo } = record;
    let viewRes = await getDigitalPointsReleaseDetail({ serialNo });
    setIsViewReleaseModal(true);
    console.log('viewRes', viewRes);
    setIsViewReleaseRecord(viewRes.data);
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
  const handleViewRecordOfCollectionType = () => {
    let { collectionType } = isViewCollectionRecord;
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
      return collectionType;
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

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfAssetsType = () => {
    let { digitalAssetsItem } = isViewCollectionRecord;
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
  const handleViewRecordOfMultimediaType = () => {
    let { multimediaType } = isViewTradingRecord;
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

  /**
   * 数字积分配置
   */
  const confirmlConfig = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let request = FormRef.getFieldsValue();
        request.totalSupply =
          request.totalSupply * Math.pow(10, request.decimals);
        request.reservedAmount =
          request.reservedAmount * Math.pow(10, request.decimals);
        request.unitReleaseAmout =
          request.unitReleaseAmout * Math.pow(10, request.decimals);
        console.log(request);
        editTokenParam(request).then((res) => {
          console.log('config', res);
          if (res?.code == 0) {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            setIsviewConfigModel(false);
          } else {
            message.error(`数字积分配置失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  /**
   * 取消配置数字积分参数
   */
  const onCancelConfig = () => {
    FormRef.resetFields();
    setIsviewConfigModel(false);
  };

  const openConfigModel = async (record) => {
    let { serialNo } = record;
    let params = {
      digitalAssetsCollectionNo: serialNo,
    };
    console.log(record);
    let viewRes = await getTokenParam(params);
    setIsViewTokenParamRecord(viewRes.data);
    setIsviewConfigModel(true);
  };

  return (
    <div>
      <Row gutter={16}>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic
              title="实际收入"
              value={11.28}
              precision={2}
              valueStyle={{ color: '#3f8600' }}
              prefix={<ArrowUpOutlined />}
              suffix="%"
            />
            <Button
              style={{ marginTop: 16 }}
              type="primary"
              onClick={async () => {
                console.log('res');
                setCurrentContent('issueDigitalPoints');
              }}
            >
              发行
            </Button>
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic
              title="收入金额"
              value={9.3}
              precision={2}
              valueStyle={{ color: '#cf1322' }}
              prefix={<ArrowDownOutlined />}
              suffix="%"
            />
            <Button
              style={{ marginTop: 16 }}
              type="dashed"
            >
              国库资金
            </Button>
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic
              title="社区收入"
              value={9.3}
              precision={2}
              valueStyle={{ color: '#cf1322' }}
              prefix={<ArrowDownOutlined />}
              suffix="%"
            />
            <Button style={{ marginTop: 16 }} type="dashed">
              社区收入
            </Button>
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic
              title="社区支出"
              value={11.28}
              precision={2}
              valueStyle={{ color: '#3f8600' }}
              prefix={<ArrowUpOutlined />}
              suffix="%"
            />
            <Button style={{ marginTop: 16 }} type="dashed">
              社区支出
            </Button>
          </Card>
        </Col>
        <Card bordered={false} style={{ width: '100%' }}>
          <Tabs defaultActiveKey="1">
            <Tabs.TabPane tab="交易流水" key="1">
              {/* 交易记录表格 */}
              <ProTable<columnsTradingDataType>
                headerTitle={<TableTitle title="数字积分交易记录" />}
                scroll={{ x: 900 }}
                bordered
                // 表头
                columns={columnsTrading}
                actionRef={tradingActionRef}
                // 请求获取的数据
                request={async (params) => {
                  let res = await getDigitalPointsTradingPageList({
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
            <Tabs.TabPane tab="释放记录" key="2">
              {/* 释放记录表格 */}
              <ProTable<columnsReleaseDataType>
                headerTitle={<TableTitle title="数字积分释放记录" />}
                scroll={{ x: 900 }}
                bordered
                // 表头
                columns={columnsRelease}
                actionRef={releaseActionRef}
                // 请求获取的数据
                request={async (params) => {
                  let res = await getDigitalPointsReleasePageList({
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
            <Tabs.TabPane tab="数字积分集合列表" key="3">
              {/* Pro表格 */}
              <ProTable<columnsDataType>
                headerTitle={<TableTitle title="数字积分发行记录" />}
                scroll={{ x: 900 }}
                bordered
                // 表头
                columns={columns}
                actionRef={actionRef}
                // 请求获取的数据
                request={async (params) => {
                  // console.log(params);
                  // 品牌商户发行资产类型 1、数字徽章 2、PFP 3、数字积分 4、数字门票 5、pass卡 6、徽章/门票
                  params.collectionType = '3';
                  let res = await getDigitalAssetsCollectionPageList({
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
              />{' '}
            </Tabs.TabPane>

            <Tabs.TabPane tab="数字积分item列表" key="4">
              {/* Pro表格 */}
              <ProTable<columnsItemDataType>
                headerTitle={<TableTitle title="数字积分上架记录" />}
                scroll={{ x: 900 }}
                bordered
                // 表头
                columns={columnsItemData}
                actionRef={itemActionRef}
                // 请求获取的数据
                request={async (params) => {
                  // console.log(params);
                  // 品牌商户发行资产类型 1、数字徽章 2、PFP 3、数字积分 4、数字门票 5、pass卡 6、徽章/门票
                  params.assetsTypes = ['3'];
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
              />{' '}
            </Tabs.TabPane>
          </Tabs>
        </Card>
      </Row>

      {/* 配置数字积分参数模态框 */}
      <Modal
        title="数字积分配置"
        width={800}
        centered
        open={isViewConfigModel}
        onOk={confirmlConfig}
        onCancel={onCancelConfig}
      >
        <Form
          name="basic"
          form={FormRef}
          style={{ minWidth: 600 }}
          labelCol={{ span: 8 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{
            serialNo: isViewTokenParamRecord?.serialNo,
            digitalAssetsCollectionNo:
              isViewTokenParamRecord?.digitalAssetsCollectionNo,
            reservedAmount: isViewTokenParamRecord?.reservedAmount,
            totalSupply: isViewTokenParamRecord?.totalSupply,
            captureTotalValue: isViewTokenParamRecord?.captureTotalValue,
            releaseMethod: isViewTokenParamRecord?.releaseMethod,
            unitReleaseTriggerValue:
              isViewTokenParamRecord?.unitReleaseTriggerValue,
            unitReleaseAmout: isViewTokenParamRecord?.unitReleaseAmout,
            issuerType: isViewTokenParamRecord?.issuerType,
            issueMethod: isViewTokenParamRecord?.issueMethod,
            releaseCycle: isViewTokenParamRecord?.releaseCycle,
            exchangeRate: isViewTokenParamRecord?.exchangeRate,
            status: isViewTokenParamRecord?.status,
            anchoringValue: isViewTokenParamRecord?.anchoringValue,
            circulation: isViewTokenParamRecord?.circulation,
            decimals: isViewTokenParamRecord?.decimals,
            description: isViewTokenParamRecord?.description,
          }}
        >
          <Form.Item
            label="ID"
            name="serialNo"
            rules={[{ required: true, message: '请输入参数编号!' }]}
          >
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="数字资产编号"
            name="digitalAssetsCollectionNo"
            rules={[{ required: true, message: '请输入数字资产编号!' }]}
          >
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="预留量"
            name="reservedAmount"
            rules={[{ required: true, message: '请输入预留量!' }]}
          >
            <Input min={1} />
          </Form.Item>
          <Form.Item
            label="总供应量"
            name="totalSupply"
            rules={[{ required: true, message: '请输入总供应量!' }]}
          >
            <Input disabled />
          </Form.Item>

          <Form.Item
            label="预估捕获的总劳动价值"
            name="captureTotalValue"
            rules={[{ required: true, message: '请输入预估捕获的总劳动价值!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="释放方式"
            name="releaseMethod"
            rules={[{ required: true, message: '请选择释放方式!' }]}
          >
            {/* 1、曲线价值释放， 2、购买释放， 3、周期释放 */}
            <Radio.Group>
              <Radio value="1">曲线价值释放</Radio>
              <Radio value="2">购买释放</Radio>
              <Radio value="3">周期释放</Radio>
            </Radio.Group>
          </Form.Item>

          <Form.Item
            label="单元释放的触发价值"
            name="unitReleaseTriggerValue"
            rules={[{ required: true, message: '请输入单元释放的触发价值!' }]}
          >
            <Input min={1} />
          </Form.Item>
          {/* <Form.Item
            label="单元释放的token数量"
            name="unitReleaseAmout"
            rules={[{ required: true, message: '请输入单元释放的token数量!' }]}
          >
            <Input min={1} />
          </Form.Item> */}
          <Form.Item
            label="释放周期"
            name="releaseCycle"
            rules={[{ required: true, message: '请输入释放周期(天)!' }]}
          >
            <Input min={1} />
          </Form.Item>

          <Form.Item
            label="兑换比例(数字积分:联合曲线积分)"
            name="exchangeRate"
            rules={[{ required: true, message: '请输入兑换比例!' }]}
          >
            <Input min={1} />
          </Form.Item>

          <Form.Item
            label="发行方式"
            name="issueMethod"
            rules={[{ required: true, message: '请选择释放方式!' }]}
          >
            {/* 发行方式：1 购买发行、2 自定义发行 */}
            <Radio.Group>
              <Radio value="1">购买发行</Radio>
              <Radio value="2">自定义发行</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item
            label="发行方类型"
            name="issuerType"
            rules={[{ required: true, message: '请选择释放方式!' }]}
          >
            {/* 发行方类型：1、平台 2、租户 3、商户 */}
            <Radio.Group>
              <Radio value="1">平台</Radio>
              <Radio value="2">租户</Radio>
              <Radio value="3">商户</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item
            label="状态"
            name="status"
            rules={[{ required: true, message: '请选择释放方式!' }]}
          >
            {/* 状态：0 未启用 1 启用 */}
            <Radio.Group>
              <Radio value="0">未启用</Radio>
              <Radio value="1">启用</Radio>
            </Radio.Group>
          </Form.Item>

          <Form.Item
            label="锚定法币价值"
            name="anchoringValue"
            rules={[{ required: true, message: '请输入锚定法币价值!' }]}
          >
            <Input min={1} />
          </Form.Item>

          <Form.Item
            label="流通量"
            name="circulation"
            rules={[{ required: true, message: '请输入流通量!' }]}
          >
            <Input disabled />
          </Form.Item>

          <Form.Item
            label="小数点"
            name="decimals"
            rules={[{ required: true, message: '请输入小数点!' }]}
          >
            <Input disabled />
          </Form.Item>

          <Form.Item
            label="描述"
            name="description"
            rules={[{ required: true, message: '请输入描述!' }]}
          >
            <TextArea />
          </Form.Item>
        </Form>
      </Modal>

      {/* 查看详情模态框 */}
      <Modal
        title="查看数字积分集合"
        width={800}
        centered
        open={isViewAssetsCollectionModal}
        onOk={() => setIsViewAssetsCollectionModal(false)}
        onCancel={() => setIsViewAssetsCollectionModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="数字资产信息">
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
        title="查看数字资产Item详情"
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
        title="查看交易详情"
        width={800}
        centered
        open={isViewTradingModal}
        onOk={() => setIsViewTradingModal(false)}
        onCancel={() => setIsViewTradingModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="铸造记录">
          <Descriptions.Item label="租户ID">
            {isViewTradingRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="铸造编号">
            {isViewTradingRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="交易Hash">
            {isViewTradingRecord?.txHash}
          </Descriptions.Item>
          <Descriptions.Item label="tokenId">
            {isViewTradingRecord?.tokenId}
          </Descriptions.Item>
          <Descriptions.Item label="铸造数量">
            {isViewTradingRecord?.amount}
          </Descriptions.Item>
          <Descriptions.Item label="多媒体类型">
            {handleViewRecordOfMultimediaType()}
          </Descriptions.Item>
          <Descriptions.Item label="资产名称">
            {isViewTradingRecord?.itemName}
          </Descriptions.Item>
          <Descriptions.Item label="metadataImage">
            {isViewTradingRecord?.metadataImage}
          </Descriptions.Item>
          <Descriptions.Item label="metadataUrl">
            {isViewTradingRecord?.metadataUrl}
          </Descriptions.Item>
          <Descriptions.Item label="铸造人手机号">
            {isViewTradingRecord?.toPhone}
          </Descriptions.Item>
          <Descriptions.Item label="铸造人姓名">
            {isViewTradingRecord?.toMinterName}
          </Descriptions.Item>
          <Descriptions.Item label="接收地址">
            {isViewTradingRecord?.toAddress}
          </Descriptions.Item>
          <Descriptions.Item label="接收客户号">
            {isViewTradingRecord?.toCustomerNo}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewTradingRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewTradingRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="协议描述">
            {isViewTradingRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
      <Modal
        title="查看释放详情"
        width={800}
        centered
        visible={isViewReleaseModal}
        onOk={() => setIsViewReleaseModal(false)}
        onCancel={() => setIsViewReleaseModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="铸造记录">
          <Descriptions.Item label="租户ID">
            {isViewReleaseRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="转账流水号">
            {isViewReleaseRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="交易Hash">
            {isViewReleaseRecord?.txHash}
          </Descriptions.Item>
          <Descriptions.Item label="tokenId">
            {isViewReleaseRecord?.tokenId}
          </Descriptions.Item>
          <Descriptions.Item label="交易数量">
            {isViewReleaseRecord?.amount}
          </Descriptions.Item>
          <Descriptions.Item label="metadataImage">
            {isViewReleaseRecord?.metadataImage}
          </Descriptions.Item>
          <Descriptions.Item label="metadataUrl">
            {isViewReleaseRecord?.metadataUrl}
          </Descriptions.Item>
          <Descriptions.Item label="接收人手机号">
            {isViewReleaseRecord?.toPhone}
          </Descriptions.Item>
          <Descriptions.Item label="接收人姓名">
            {isViewReleaseRecord?.toMinterName}
          </Descriptions.Item>
          <Descriptions.Item label="发送地址">
            {isViewReleaseRecord?.fromAddress}
          </Descriptions.Item>
          <Descriptions.Item label="接收地址">
            {isViewReleaseRecord?.toAddress}
          </Descriptions.Item>
          <Descriptions.Item label="发送客户号">
            {isViewReleaseRecord?.fromCustomerNo}
          </Descriptions.Item>
          <Descriptions.Item label="接收客户号">
            {isViewReleaseRecord?.toCustomerNo}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewReleaseRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewReleaseRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="协议描述">
            {isViewReleaseRecord?.description}
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
                      //TODO: add chainType field
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
