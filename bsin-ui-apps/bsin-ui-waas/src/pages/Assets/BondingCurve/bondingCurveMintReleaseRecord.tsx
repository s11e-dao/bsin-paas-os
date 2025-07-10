import {
  Form,
  Tabs,
  Card,
  Button,
  Modal,
  message,
  Popconfirm,
  Descriptions,
  Input,
  Select,
  Row,
  Col,
  Typography,
} from 'antd';
import React, { useState } from 'react';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons';

import LindeChart, { DataPoint } from './lindeChart';

import TableTitle from '../../../components/TableTitle';
import columnsData, { columnsDataType } from './data';

import {
  getBondingCurveTokenJournalPageList,
  getBondingCurveTokenJournalList,
  getBondingCurveTokenTrendList,
  getTransactionDetail,
  addTransaction,
} from './service';
import LindeChartSimple from './lindeChartSimple';

const { Title } = Typography;


interface BondingCurveMintRecordProps {
  refreshTrigger?: number;
}

export default ({ refreshTrigger }: BondingCurveMintRecordProps) => {
  const { TextArea } = Input;
  const { Option } = Select;

  // 表头数据 
  const [curveTrendData, setCurveTrendData] = React.useState<DataPoint[]>([]);
  const [curveJournalData, setCurveJournalData] = React.useState<DataPoint[]>([]);
  const [allData, setAllData] = React.useState<DataPoint[]>([]);
  
  // 铸造和释放数据
  const [mintData, setMintData] = React.useState<DataPoint[]>([]);
  const [releaseData, setReleaseData] = React.useState<DataPoint[]>([]);

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();
  const mintActionRef = React.useRef<ActionType>();
  const releaseActionRef = React.useRef<ActionType>();

  /**
   * 监听页面路径设置布局
   */
  React.useEffect(() => {
    getBondingCurveTokenJournal();
    getBondingCurveTokenTrend();
    getMintData();
    getReleaseData();
  }, []);

  // 监听refreshTrigger变化，触发数据刷新
  React.useEffect(() => {
    if (refreshTrigger && refreshTrigger > 0) {
      getBondingCurveTokenJournal();
      getMintData();
      getReleaseData();
      actionRef.current?.reload();
      mintActionRef.current?.reload();
      releaseActionRef.current?.reload();
    }
  }, [refreshTrigger]);

  const getBondingCurveTokenJournal = async () => {
    try {
      const reqParams = {
        merchantNo: '',
        pageSize: '20',
        current: '1',
        limit: '40',
      };
      // const res = await getBondingCurveTokenJournalList(reqParams);
      const res = await getBondingCurveTokenJournalPageList(reqParams);
      console.log(res);
      
      // 确保 res.data 存在且是数组
      if (res && res.data && Array.isArray(res.data)) {
        let curveJournalData = res.data.map((item: any) => {
          return {
            supply: Number(item.supply) || 0,
            price: Number(item.price) || 0,
            series: "curveJournal",
          };
        });
        console.log('curveJournalData', curveJournalData);
        setCurveJournalData(curveJournalData);
        // 合并曲线数据
        setAllData([...curveJournalData, ...(curveTrendData || [])]);
      } else {
        console.warn('getBondingCurveTokenJournal: 数据格式不正确', res);
        setCurveJournalData([]);
        setAllData([...(curveTrendData || [])]);
      }
    } catch (error) {
      console.error('获取曲线日志数据失败:', error);
      setCurveJournalData([]);
      setAllData([...(curveTrendData || [])]);
    }
  };

  const getBondingCurveTokenTrend = async () => {
    try {
      const reqParams = {
        merchantNo: '',
        pageSize: '10',
        current: '1',
        limit: '10',
      };
      const res = await getBondingCurveTokenTrendList(reqParams);
      console.log(res);
      
      // 确保 res.data 存在且是数组
      if (res && res.data && Array.isArray(res.data)) {
        let curveTrendData = res.data.map((item: any) => {
          return {
            supply: Number(item.supply) || 0,
            price: Number(item.price) || 0,
            series: "curveTrend",
          };
        });
        console.log('curveTrendData', curveTrendData);
        setCurveTrendData(curveTrendData);
        // 合并曲线数据
        setAllData([...curveTrendData, ...(curveJournalData || [])]);
      } else {
        console.warn('getBondingCurveTokenTrend: 数据格式不正确', res);
        setCurveTrendData([]);
        setAllData([...(curveJournalData || [])]);
      }
    } catch (error) {
      console.error('获取曲线趋势数据失败:', error);
      setCurveTrendData([]);
      setAllData([...(curveJournalData || [])]);
    }
  };

  // 获取铸造数据
  const getMintData = async () => {
    try {
      const reqParams = {
        merchantNo: '',
        pageSize: '20',
        current: '1',
        method: 'mint',
      };
      const res = await getBondingCurveTokenJournalPageList(reqParams);
      console.log('铸造数据:', res);
      
      if (res && res.data && Array.isArray(res.data)) {
        let mintData = res.data.map((item: any) => {
          return {
            supply: Number(item.supply) || 0,
            price: Number(item.price) || 0,
            series: "铸造曲线",
          };
        });
        setMintData(mintData);
      } else {
        setMintData([]);
      }
    } catch (error) {
      console.error('获取铸造数据失败:', error);
      setMintData([]);
    }
  };

  // 获取释放数据
  const getReleaseData = async () => {
    try {
      const reqParams = {
        merchantNo: '',
        pageSize: '20',
        current: '1',
        method: 'redeem',
      };
      const res = await getBondingCurveTokenJournalPageList(reqParams);
      console.log('释放数据:', res);
      
      if (res && res.data && Array.isArray(res.data)) {
        let releaseData = res.data.map((item: any) => {
          return {
            supply: Number(item.supply) || 0,
            price: Number(item.price) || 0,
            series: "释放曲线",
          };
        });
        setReleaseData(releaseData);
      } else {
        setReleaseData([]);
      }
    } catch (error) {
      console.error('获取释放数据失败:', error);
      setReleaseData([]);
    }
  };

  // 控制新增模态框
  const [isTransactionModal, setIsTransactionModal] = useState(false);
  // 查看模态框
  const [isViewTransactionModal, setIsViewTransactionModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState<any>({});
  // 获取表单
  const [FormRef] = Form.useForm();
  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            // 调用方法
            toViewTransaction(record);
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

  /**
   * 以下内容为操作相关
   */

  // 新增模板
  const increaseTransaction = () => {
    setIsTransactionModal(true);
  };

  // 刷新曲线
  const refreshCurve = () => {
    getBondingCurveTokenJournal();
    getMintData();
    getReleaseData();
  };
  /**
   * 铸造/销毁操作
   */
  const confirmTransaction = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        try {
          const res = await addTransaction(response);
          console.log('add', res);
          if (res && res.code === 0) {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            mintActionRef.current?.reload();
            releaseActionRef.current?.reload();
            setIsTransactionModal(false);
            message.success('交易添加成功');
          } else {
            message.error(`失败： ${res?.message || '未知错误'}`);
          }
        } catch (error) {
          console.error('添加交易失败:', error);
          message.error('添加交易失败');
        }
      })
      .catch(() => { });
  };

  /**
   * 取消添加模板
   */
  const onCancelTransaction = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsTransactionModal(false);
  };

  /**
   * 查看详情
   */
  const toViewTransaction = async (record: any) => {
    let { serialNo } = record;
    let viewRes = await getTransactionDetail({ serialNo });
    setIsViewTransactionModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfMethod = () => {
    let { method } = isViewRecord;
    let typeText = method;
    return typeText;
  };

  const sampleData = [
    // 第一条线：产品A的价格曲线
    { supply: 0, price: 100, series: '产品A' },
    { supply: 100, price: 95, series: '产品A' },
    { supply: 200, price: 90, series: '产品A' },
    { supply: 300, price: 85, series: '产品A' },
    { supply: 400, price: 80, series: '产品A' },
    { supply: 500, price: 75, series: '产品A' },

    // 第二条线：产品B的价格曲线
    { supply: 0, price: 120, series: '产品B' },
    { supply: 100, price: 115, series: '产品B' },
    { supply: 200, price: 110, series: '产品B' },
    { supply: 300, price: 105, series: '产品B' },
    { supply: 400, price: 100, series: '产品B' },
    { supply: 500, price: 95, series: '产品B' },

    // 第三条线：产品C的价格曲线
    { supply: 0, price: 80, series: '产品C' },
    { supply: 100, price: 78, series: '产品C' },
    { supply: 200, price: 76, series: '产品C' },
    { supply: 300, price: 74, series: '产品C' },
    { supply: 400, price: 72, series: '产品C' },
    { supply: 500, price: 70, series: '产品C' },
  ];
  return (
    <div style={{ padding: '20px' }}>
      <Title level={2}>联合曲线铸造与释放记录</Title>

      {/* 1. 铸造曲线 */}
      <Card 
        title="铸造曲线" 
        style={{ marginBottom: '20px' }}
        extra={
          <Button 
            icon={<ReloadOutlined />} 
            onClick={() => getMintData()}
            size="small"
          >
            刷新
          </Button>
        }
      >
        <LindeChartSimple
          data={mintData}
          height={400}
          width={800}
        />
        <div style={{ marginTop: '10px' }}>
          <Typography.Text type="secondary">
            横轴：供应量，纵轴：价格 - 显示铸造操作的联合曲线变化
          </Typography.Text>
        </div>
      </Card>

      {/* 2. 铸造列表 */}
      <Card 
        title="铸造列表" 
        style={{ marginBottom: '20px' }}
        extra={
          <Button 
            icon={<PlusOutlined />} 
            onClick={increaseTransaction}
            type="primary"
            size="small"
          >
            新增铸造
          </Button>
        }
      >
        <ProTable<columnsDataType>
          headerTitle={<TableTitle title="铸造记录" />}
          scroll={{ x: 900 }}
          bordered
          size="small"
          // 表头
          columns={columns}
          actionRef={mintActionRef}
          // 请求获取的数据
          request={async (params) => {
            let res = await getBondingCurveTokenJournalPageList({
              ...params,
              method: 'mint',
            });
            console.log('铸造数据:', res);
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
          toolBarRender={() => []}
        />
      </Card>

      {/* 3. 释放曲线 */}
      <Card 
        title="释放曲线" 
        style={{ marginBottom: '20px' }}
        extra={
          <Button 
            icon={<ReloadOutlined />} 
            onClick={() => getReleaseData()}
            size="small"
          >
            刷新
          </Button>
        }
      >
        <LindeChartSimple
          data={releaseData}
          height={400}
          width={800}
        />
        <div style={{ marginTop: '10px' }}>
          <Typography.Text type="secondary">
            横轴：供应量，纵轴：价格 - 显示释放操作的联合曲线变化
          </Typography.Text>
        </div>
      </Card>

      {/* 4. 释放列表 */}
      <Card 
        title="释放列表" 
        style={{ marginBottom: '20px' }}
        extra={
          <Button 
            icon={<PlusOutlined />} 
            onClick={increaseTransaction}
            type="primary"
            size="small"
          >
            新增释放
          </Button>
        }
      >
        <ProTable<columnsDataType>
          headerTitle={<TableTitle title="释放记录" />}
          scroll={{ x: 900 }}
          bordered
          size="small"
          // 表头
          columns={columns}
          actionRef={releaseActionRef}
          // 请求获取的数据
          request={async (params) => {
            let res = await getBondingCurveTokenJournalPageList({
              ...params,
              method: 'redeem',
            });
            console.log('释放数据:', res);
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
          toolBarRender={() => []}
        />
      </Card>

      {/* 全局刷新按钮 */}
      <Row justify="center" style={{ marginTop: '20px' }}>
        <Col>
          <Button
            icon={<ReloadOutlined />}
            onClick={refreshCurve}
            type="primary"
            size="large"
          >
            刷新所有数据
          </Button>
        </Col>
      </Row>
      {/* 新增交易模态框 */}
      <Modal
        title="新增交易"
        centered
        open={isTransactionModal}
        onOk={confirmTransaction}
        onCancel={onCancelTransaction}
        width={600}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{
            type: '分享类',
            method: 'mint',
            minMintAmount: '1',
            description: 'mint',
            amount: '500',
          }}
        >
          <Form.Item
            label="交易方法"
            name="method"
            rules={[{ required: true, message: '请选择交易方法!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="mint">铸造</Option>
              <Option value="redeem">释放</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="劳动价值分类"
            name="type"
            rules={[{ required: true, message: '请选择劳动价值分类!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="分享类">分享类</Option>
              <Option value="内容生产">内容生产</Option>
              <Option value="提案类">提案类</Option>
              <Option value="日常工作类">日常工作类</Option>
              <Option value="成本支出类">成本支出类</Option>
              <Option value="独立项目类">独立项目类</Option>
              <Option value="独立任务类">独立任务类</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="劳动价值"
            name="amount"
            rules={[{ required: true, message: '请输入劳动价值!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="最低期望铸造积分数量"
            name="minMintAmount"
            rules={[
              { required: false, message: '请输入最低期望铸造积分数量!' },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="劳动价值描述"
            name="description"
            rules={[{ required: true, message: '请输入劳动价值描述!' }]}
          >
            <TextArea />
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看详情模态框 */}
      <Modal
        title="查看交易详情"
        width={800}
        centered
        open={isViewTransactionModal}
        onOk={() => setIsViewTransactionModal(false)}
        onCancel={() => setIsViewTransactionModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="交易详情" bordered>
          <Descriptions.Item label="流水号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="商户ID">
            {isViewRecord?.merchantNo}
          </Descriptions.Item>
          <Descriptions.Item label="交易方法">
            {handleViewRecordOfMethod()}
          </Descriptions.Item>
          <Descriptions.Item label="账户编号">
            {isViewRecord?.accountNo}
          </Descriptions.Item>
          {/* 0、个人账户 1、企业账户 */}
          <Descriptions.Item label="账户类型">
            {isViewRecord?.accountType}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="描述">
            {isViewRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
