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
  Tag,
  Divider,
  Radio,
  Space,
  DatePicker,
  Card,
  Statistic,
  Row,
  Col
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType, ProductType } from './data';
import {
  getTransactionPageList,
  addTransaction,
  deleteTransaction,
  getTransactionDetail,
  getProductList,
} from './service';
import TableTitle from '../../components/TableTitle';
import { hex_md5 } from '../../utils/md5';
import type { Dayjs } from 'dayjs';

export default () => {

  const { TextArea } = Input;
  const { Option } = Select;
  const { RangePicker } = DatePicker;
  
  // 控制新增模态框
  const [isTemplateModal, setIsTemplateModal] = useState(false);
  // 查看模态框
  const [isViewTemplateModal, setIsViewTemplateModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState<columnsDataType | {}>({});
  const [productList, setProductList] = useState<ProductType[]>([]);
  // 获取表单
  const [FormRef] = Form.useForm();

  // 筛选状态
  const [filterStatus, setFilterStatus] = useState('');
  const [filterType, setFilterType] = useState('');
  const [dateRange, setDateRange] = useState<[Dayjs | null, Dayjs | null] | []>([]);
  
  // 统计信息
  const [statistics, setStatistics] = useState({
    total: 0,
    success: 0,
    waiting: 0,
    failed: 0,
    totalAmount: 0
  });

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <div key={record.serialNo}>
      <a onClick={() => {
            toViewContractTemplate(record);
          }}>查看</a>
      <Divider type="vertical" />
      <Popconfirm
        title="是否删除此条数据？"
        onConfirm={() => {
          toDelContractTemplate(record);
        }}
        onCancel={() => {
          message.warning(`取消删除！`);
        }}
        okText="是"
        cancelText="否"
      >
        <a>删除</a>
      </Popconfirm>
    </div>
  );

  // 交易类型自定义渲染
  const transactionTypeRender = (text: any, record: columnsDataType, index: number) => {
    const typeMap: { [key: string]: { text: string, color: string } } = {
      '1': { text: '支付', color: 'blue' },
      '2': { text: '充值', color: 'green' },
      '3': { text: '转账', color: 'orange' },
      '4': { text: '提现', color: 'red' },
      '5': { text: '退款', color: 'purple' },
      '6': { text: '结算', color: 'cyan' },
      '7': { text: '收入', color: 'lime' },
      '8': { text: '赎回', color: 'gold' },
    };
    
    const typeInfo = typeMap[record.transactionType] || { text: '未知', color: 'default' };
    return <Tag color={typeInfo.color}>{typeInfo.text}</Tag>;
  };

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
    item.dataIndex === 'transactionType' ? (item.render = transactionTypeRender) : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  /**
   * 筛选功能相关
   */
  
  // 交易状态筛选
  const handleStatusFilter = (value: string) => {
    setFilterStatus(value);
    actionRef.current?.reload();
  };

  // 交易类型筛选
  const handleTypeFilter = (value: string) => {
    setFilterType(value);
    actionRef.current?.reload();
  };

  // 时间范围筛选
  const handleDateRangeFilter = (dates: [Dayjs | null, Dayjs | null] | null) => {
    setDateRange(dates || []);
    actionRef.current?.reload();
  };

  // 重置所有筛选
  const resetFilters = () => {
    setFilterStatus('');
    setFilterType('');
    setDateRange([]);
    actionRef.current?.reload();
  };

  // 新增模板
  const increaseTemplate = () => {
    setIsTemplateModal(true);
    getProductList({}).then((res: any) => {
      setProductList(res?.data || []);
    });
  };

  /**
   * 确认添加模板
   */
  const confirmTemplate = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        let reqParam = {
          ...response,
        };
        addTransaction(reqParam).then((res: any) => {
          console.log('add', res);
          if (res.code === 0 ) {
            message.success('添加成功');
            // 重置输入的表单
            FormRef.resetFields();
            setIsTemplateModal(false);
            actionRef.current?.reload();
          } else {
            message.error(`失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  /**
   * 取消添加模板
   */
  const onCancelTemplate = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsTemplateModal(false);
  };

  /**
   * 删除交易记录
   */
  const toDelContractTemplate = async (record: columnsDataType) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteTransaction({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === 0) {
      message.success('删除成功');
      // 删除成功刷新表单
      actionRef.current?.reload();
    } else {
      message.error(`删除失败： ${delRes?.message}`);
    }
  };

  /**
   * 查看详情
   */
  const toViewContractTemplate = async (record: columnsDataType) => {
    console.log(record);
    let { serialNo } = record;
    let viewRes = await getTransactionDetail({ serialNo });
    setIsViewTemplateModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data || {});
  };

  /**
   * 获取交易类型文本
   */
  const handleViewRecordOfTransactionType = () => {
    const record = isViewRecord as columnsDataType;
    const typeMap: { [key: string]: string } = {
      '1': '支付',
      '2': '充值', 
      '3': '转账',
      '4': '提现',
      '5': '退款',
      '6': '结算',
      '7': '收入',
      '8': '赎回',
    };
    return typeMap[record.transactionType] || '未知类型';
  };

  /**
   * 获取交易状态文本
   */
  const handleViewRecordOfTransactionStatus = () => {
    const record = isViewRecord as columnsDataType;
    const statusMap: { [key: string]: string } = {
      '1': '等待',
      '2': '成功',
      '3': '失败',
    };
    return statusMap[record.transactionStatus] || '未知状态';
  };

  /**
   * 获取审核状态文本
   */
  const handleViewRecordOfAuditStatus = () => {
    const record = isViewRecord as columnsDataType;
    if (!record.auditStatus) return '-';
    const auditMap: { [key: string]: string } = {
      '1': '待审核',
      '2': '审核成功',
      '3': '审核拒绝',
    };
    return auditMap[record.auditStatus] || '未知状态';
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          console.log('请求参数:', params);
          
          // 构建请求参数
          let requestParams: any = {
            ...params,
            // 租户客户类型
            type: '3',
          };

          // 添加筛选条件
          if (filterStatus) {
            requestParams.transactionStatus = filterStatus;
          }
          if (filterType) {
            requestParams.transactionType = filterType;
          }
          if (dateRange && dateRange.length === 2) {
            requestParams.startTime = dateRange[0]?.format('YYYY-MM-DD HH:mm:ss');
            requestParams.endTime = dateRange[1]?.format('YYYY-MM-DD HH:mm:ss');
          }

          let res = await getTransactionPageList(requestParams);
          console.log('响应数据:', res);
          
          // 计算统计信息
          const data = res.data || [];
          const stats = {
            total: data.length,
            success: data.filter((item: any) => item.transactionStatus === '2').length,
            waiting: data.filter((item: any) => item.transactionStatus === '1').length,
            failed: data.filter((item: any) => item.transactionStatus === '3').length,
            totalAmount: data.reduce((sum: number, item: any) => sum + (item.txAmount || 0), 0)
          };
          setStatistics(stats);
          
          const result = {
            data: data,
            total: res.pagination?.totalSize || 0,
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
        headerTitle={
          <div>
            <TableTitle title="交易列表" />
            
            {/* 统计信息卡片 */}
            <Card size="small" style={{ marginTop: 16, marginBottom: 8 }}>
              <Row gutter={16}>
                <Col span={4}>
                  <Statistic 
                    title="总交易数" 
                    value={statistics.total} 
                    valueStyle={{ color: '#1890ff' }}
                  />
                </Col>
                <Col span={4}>
                  <Statistic 
                    title="成功交易" 
                    value={statistics.success} 
                    valueStyle={{ color: '#52c41a' }}
                  />
                </Col>
                <Col span={4}>
                  <Statistic 
                    title="等待交易" 
                    value={statistics.waiting} 
                    valueStyle={{ color: '#faad14' }}
                  />
                </Col>
                <Col span={4}>
                  <Statistic 
                    title="失败交易" 
                    value={statistics.failed} 
                    valueStyle={{ color: '#f5222d' }}
                  />
                </Col>
                <Col span={8}>
                  <Statistic 
                    title="总交易金额" 
                    value={statistics.totalAmount} 
                    precision={2}
                    prefix="¥"
                    valueStyle={{ color: '#722ed1' }}
                  />
                </Col>
              </Row>
            </Card>

            {/* 筛选条件卡片 */}
            <Card 
              size="small" 
              style={{ marginBottom: 16 }}
              title={
                <Space>
                  <span>筛选条件</span>
                  <Button size="small" onClick={resetFilters}>
                    重置筛选
                  </Button>
                </Space>
              }
            >
              <Space wrap>
                <div>
                  <span style={{ marginRight: 8 }}>交易状态：</span>
                  <Radio.Group 
                    value={filterStatus} 
                    onChange={(e) => handleStatusFilter(e.target.value)}
                    size="small"
                  >
                    <Radio.Button value="">全部</Radio.Button>
                    <Radio.Button value="1">等待</Radio.Button>
                    <Radio.Button value="2">成功</Radio.Button>
                    <Radio.Button value="3">失败</Radio.Button>
                  </Radio.Group>
                </div>
                
                <div>
                  <span style={{ marginRight: 8 }}>交易类型：</span>
                  <Radio.Group 
                    value={filterType} 
                    onChange={(e) => handleTypeFilter(e.target.value)}
                    size="small"
                  >
                    <Radio.Button value="">全部</Radio.Button>
                    <Radio.Button value="1">支付</Radio.Button>
                    <Radio.Button value="2">充值</Radio.Button>
                    <Radio.Button value="3">转账</Radio.Button>
                    <Radio.Button value="4">提现</Radio.Button>
                    <Radio.Button value="5">退款</Radio.Button>
                    <Radio.Button value="6">结算</Radio.Button>
                    <Radio.Button value="7">收入</Radio.Button>
                    <Radio.Button value="8">赎回</Radio.Button>
                  </Radio.Group>
                </div>
                
                <div>
                  <span style={{ marginRight: 8 }}>交易时间：</span>
                  <RangePicker
                    showTime
                    format="YYYY-MM-DD HH:mm:ss"
                    value={dateRange.length === 2 ? dateRange as [Dayjs | null, Dayjs | null] : undefined}
                    onChange={handleDateRangeFilter}
                    size="small"
                    placeholder={['开始时间', '结束时间']}
                  />
                </div>
              </Space>
            </Card>
          </div>
        }
        toolBarRender={() => [
          <Button
            onClick={() => {
              increaseTemplate();
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            添加
          </Button>,
        ]}
      />
      {/* 新增交易模态框 */}
      <Modal
        title="添加交易"
        centered
        open={isTemplateModal}
        onOk={confirmTemplate}
        onCancel={onCancelTemplate}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 16 }}
          // 表单默认值
          initialValues={{ transactionType: '1', transactionStatus: '1' }}
        >
          <Form.Item
            label="交易类型"
            name="transactionType"
            rules={[{ required: true, message: '请选择交易类型!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="1">支付</Option>
              <Option value="2">充值</Option>
              <Option value="3">转账</Option>
              <Option value="4">提现</Option>
              <Option value="5">退款</Option>
              <Option value="6">结算</Option>
              <Option value="7">收入</Option>
              <Option value="8">赎回</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="交易金额"
            name="txAmount"
            rules={[{ required: true, message: '请输入交易金额!' }]}
          >
            <Input placeholder="请输入交易金额" addonBefore="¥" type="number" />
          </Form.Item>
          <Form.Item
            label="转出地址"
            name="fromAddress"
            rules={[{ required: true, message: '请输入转出地址!' }]}
          >
            <Input placeholder="请输入转出地址" />
          </Form.Item>
          <Form.Item
            label="转入地址"
            name="toAddress"
            rules={[{ required: true, message: '请输入转入地址!' }]}
          >
            <Input placeholder="请输入转入地址" />
          </Form.Item>
          <Form.Item
            label="交易hash"
            name="txHash"
          >
            <Input placeholder="请输入交易hash" />
          </Form.Item>
          <Form.Item
            label="合约地址"
            name="contractAddress"
          >
            <Input placeholder="请输入合约地址" />
          </Form.Item>
          <Form.Item
            label="执行方法"
            name="contractMethod"
          >
            <Input placeholder="请输入执行方法" />
          </Form.Item>
          <Form.Item
            label="商户业务单号"
            name="OutOrderNo"
          >
            <Input placeholder="请输入商户业务单号" />
          </Form.Item>
          <Form.Item
            label="备注"
            name="comment"
          >
            <TextArea rows={3} placeholder="请输入备注信息" />
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看详情模态框 */}
      <Modal
        title="交易详情"
        width={1000}
        centered
        open={isViewTemplateModal}
        onOk={() => setIsViewTemplateModal(false)}
        onCancel={() => setIsViewTemplateModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="交易详情" bordered column={2}>
          <Descriptions.Item label="交易编号">
            {(isViewRecord as columnsDataType)?.serialNo || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="交易hash">
            {(isViewRecord as columnsDataType)?.txHash || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="交易类型">
            {handleViewRecordOfTransactionType()}
          </Descriptions.Item>
          <Descriptions.Item label="交易状态">
            {handleViewRecordOfTransactionStatus()}
          </Descriptions.Item>
          <Descriptions.Item label="交易金额">
            ¥{(isViewRecord as columnsDataType)?.txAmount || 0}
          </Descriptions.Item>
          <Descriptions.Item label="Gas费">
            {(isViewRecord as columnsDataType)?.gasFee ? `¥${(isViewRecord as columnsDataType).gasFee}` : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="手续费">
            {(isViewRecord as columnsDataType)?.fee ? `¥${(isViewRecord as columnsDataType).fee}` : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="审核状态">
            {handleViewRecordOfAuditStatus()}
          </Descriptions.Item>
          <Descriptions.Item label="转出地址">
            {(isViewRecord as columnsDataType)?.fromAddress || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="转入地址">
            {(isViewRecord as columnsDataType)?.toAddress || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="合约地址">
            {(isViewRecord as columnsDataType)?.contractAddress || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="执行方法">
            {(isViewRecord as columnsDataType)?.contractMethod || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="商户业务单号">
            {(isViewRecord as columnsDataType)?.OutOrderNo || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="租户ID">
            {(isViewRecord as columnsDataType)?.tenantId || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="分账标识">
            {(isViewRecord as columnsDataType)?.profitSharing ? '是' : '否'}
          </Descriptions.Item>
          <Descriptions.Item label="分润金额">
            {(isViewRecord as columnsDataType)?.profitSharingAmount ? `¥${(isViewRecord as columnsDataType).profitSharingAmount}` : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="交易时间">
            {(isViewRecord as columnsDataType)?.createTime || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="完成时间">
            {(isViewRecord as columnsDataType)?.completedTime || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="备注" span={2}>
            {(isViewRecord as columnsDataType)?.comment || '-'}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
