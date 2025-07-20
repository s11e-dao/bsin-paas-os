import React, { useState } from 'react';
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

export default () => {
  // 查看模态框
  const [isViewModal, setIsViewModal] = useState(false);
  // 查看记录
  const [viewRecord, setViewRecord] = useState<RevenueShareDataType>({} as RevenueShareDataType);

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<RevenueShareDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewDetail(record);
          }}
        >
          查看
        </a>
      </li>
    </ul>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  /**
   * 以下内容为操作相关
   */

  /**
   * 查看详情
   */
  const toViewDetail = async (record: RevenueShareDataType) => {
    let { serialNo } = record;
    let viewRes = await getRevenueShareDetail({ serialNo });
    setIsViewModal(true);
    console.log('viewRes', viewRes);
    setViewRecord(viewRes.data || record);
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
          let res = await getRevenueSharePageList({
            ...params,
            pagination: {
              pageNum: params.current,
              pageSize: params.pageSize,
            },
          });
          console.log('分账明细', res);
          const result = {
            data: res.data?.records || res.data || [],
            total: res.data?.total || res.pagination?.totalSize || 0,
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
      
      {/* 查看详情模态框 */}
      <Modal
        title="查看分账明细详情"
        width={800}
        centered
        open={isViewModal}
        onOk={() => setIsViewModal(false)}
        onCancel={() => setIsViewModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="分账明细信息" bordered>
          <Descriptions.Item label="分账ID" span={2}>
            {viewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="交易编号" span={2}>
            {viewRecord?.transactionNo}
          </Descriptions.Item>
          <Descriptions.Item label="租户ID" span={2}>
            {viewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="交易金额">
            ¥{viewRecord?.amount}
          </Descriptions.Item>
          <Descriptions.Item label="状态">
            {viewRecord?.status === 'SUCCESS' ? '成功' : 
             viewRecord?.status === 'PENDING' ? '待处理' : '失败'}
          </Descriptions.Item>
          <Descriptions.Item label="运营平台分账">
            ¥{viewRecord?.superTenantAmount}
          </Descriptions.Item>
          <Descriptions.Item label="租户平台分账">
            ¥{viewRecord?.tenantAmount}
          </Descriptions.Item>
          <Descriptions.Item label="合伙人分账">
            ¥{viewRecord?.sysAgentAmount}
          </Descriptions.Item>
          <Descriptions.Item label="消费者返利">
            ¥{viewRecord?.customerAmount}
          </Descriptions.Item>
          <Descriptions.Item label="分销者分账">
            ¥{viewRecord?.distributorAmount}
          </Descriptions.Item>
          <Descriptions.Item label="数字积分兑换">
            ¥{viewRecord?.exchangeDigitalPointsAmount}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间" span={2}>
            {viewRecord?.createTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
