import React, { useState, useRef } from 'react';
import {
  Modal,
  Descriptions,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import columnsData, { RevenueShareDataType } from './data';
import {
  getRevenueSharePageList,
  getRevenueShareDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';

const RevenueShareList: React.FC = () => {
  // 查看模态框状态
  const [isViewModalVisible, setIsViewModalVisible] = useState(false);
  // 查看记录
  const [viewRecord, setViewRecord] = useState<RevenueShareDataType>({} as RevenueShareDataType);

  // Table action 的引用，便于自定义触发
  const actionRef = useRef<ActionType>();

  // 表头数据
  const columns: ProColumns<RevenueShareDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender = (text: any, record: RevenueShareDataType, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            handleViewDetail(record);
          }}
        >
          查看
        </a>
      </li>
    </ul>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    if (item.dataIndex === 'action') {
      item.render = actionRender;
    }
  });

  /**
   * 查看详情
   */
  const handleViewDetail = async (record: RevenueShareDataType) => {
    const { serialNo } = record;
    try {
      const viewRes = await getRevenueShareDetail({ serialNo });
      setViewRecord(viewRes.data || record);
      setIsViewModalVisible(true);
    } catch (error) {
      console.error('获取详情失败:', error);
    }
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<RevenueShareDataType>
        headerTitle={<TableTitle title="分账明细" />}
        scroll={{ x: 1400 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          try {
            const res = await getRevenueSharePageList({
              ...params,
              pagination: {
                pageNum: params.current,
                pageSize: params.pageSize,
              },
            });
            
            const result = {
              data: res.data?.records || res.data || [],
              total: res.data?.total || res.pagination?.totalSize || 0,
            };
            return result;
          } catch (error) {
            console.error('获取分账明细失败:', error);
            return {
              data: [],
              total: 0,
            };
          }
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
        // 分页配置
        pagination={{
          defaultPageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
        }}
        // 工具栏配置
        toolBarRender={() => []}
        // 表格配置
        options={{
          density: true,
          fullScreen: true,
          reload: true,
          setting: true,
        }}
      />

      {/* 查看详情模态框 */}
      <Modal
        title="分账明细详情"
        open={isViewModalVisible}
        onCancel={() => setIsViewModalVisible(false)}
        footer={null}
        width={800}
      >
        <Descriptions column={2} bordered>
          <Descriptions.Item label="分账ID">{viewRecord.serialNo}</Descriptions.Item>
          <Descriptions.Item label="交易编号">{viewRecord.transactionNo}</Descriptions.Item>
          <Descriptions.Item label="租户ID">{viewRecord.tenantId}</Descriptions.Item>
          <Descriptions.Item label="交易金额">¥{viewRecord.amount}</Descriptions.Item>
          <Descriptions.Item label="运营平台分账">¥{viewRecord.superTenantAmount}</Descriptions.Item>
          <Descriptions.Item label="租户平台分账">¥{viewRecord.tenantAmount}</Descriptions.Item>
          <Descriptions.Item label="合伙人分账">¥{viewRecord.sysAgentAmount}</Descriptions.Item>
          <Descriptions.Item label="消费者返利">¥{viewRecord.customerAmount}</Descriptions.Item>
          <Descriptions.Item label="分销者分账">¥{viewRecord.distributorAmount}</Descriptions.Item>
          <Descriptions.Item label="数字积分兑换">¥{viewRecord.exchangeDigitalPointsAmount}</Descriptions.Item>
          <Descriptions.Item label="状态">{viewRecord.status}</Descriptions.Item>
          <Descriptions.Item label="创建时间">{viewRecord.createTime}</Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};

export default RevenueShareList;
