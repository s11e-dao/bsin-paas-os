import React, { useState } from 'react';
import {
  Modal,
  message,
  Descriptions,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import columnsData, { ProfitSharingConfigDataType } from './data';
import {
  getProfitSharingConfigPageList,
  getProfitSharingConfigDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';

export default () => {
  // 查看模态框
  const [isViewModal, setIsViewModal] = useState(false);
  // 查看记录
  const [viewRecord, setViewRecord] = useState<ProfitSharingConfigDataType>({} as ProfitSharingConfigDataType);

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<ProfitSharingConfigDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewConfig(record);
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
  const toViewConfig = async (record: ProfitSharingConfigDataType) => {
    let { serialNo } = record;
    let viewRes = await getProfitSharingConfigDetail({ serialNo });
    setIsViewModal(true);
    console.log('viewRes', viewRes);
    setViewRecord(viewRes.data || record);
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<ProfitSharingConfigDataType>
        headerTitle={<TableTitle title="分账配置列表" />}
        scroll={{ x: 1200 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          let res = await getProfitSharingConfigPageList({
            ...params,
            pagination: {
              pageNum: params.current,
              pageSize: params.pageSize,
            },
          });
          console.log('分账配置列表', res);
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
        title="查看分账配置详情"
        width={800}
        centered
        open={isViewModal}
        onOk={() => setIsViewModal(false)}
        onCancel={() => setIsViewModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="分账配置信息" bordered>
          <Descriptions.Item label="配置ID" span={2}>
            {viewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="租户ID" span={2}>
            {viewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="运营平台分佣比例">
            {viewRecord?.superTenantRate}%
          </Descriptions.Item>
          <Descriptions.Item label="租户平台分佣比例">
            {viewRecord?.tenantRate}%
          </Descriptions.Item>
          <Descriptions.Item label="代理商分佣比例">
            {viewRecord?.sysAgentRate}%
          </Descriptions.Item>
          <Descriptions.Item label="消费者返利比例">
            {viewRecord?.customerRate}%
          </Descriptions.Item>
          <Descriptions.Item label="分销者分佣比例">
            {viewRecord?.distributorRate}%
          </Descriptions.Item>
          <Descriptions.Item label="数字积分兑换比例">
            {viewRecord?.exchangeDigitalPointsRate}%
          </Descriptions.Item>
          <Descriptions.Item label="创建时间" span={2}>
            {viewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="更新时间" span={2}>
            {viewRecord?.updateTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
