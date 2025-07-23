import React from 'react';
import {
  message,
  Button,
  Space,
  Tag,
  Popconfirm,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { EyeOutlined, CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getCustomerEnterprisePageList,
  auditCustomerEnterprise,
} from './service';
import TableTitle from '../../../components/TableTitle';

interface EnterpriseRecord {
  serialNo: string;
  customerNo: string;
  status: string;
  authenticationStatus: string;
  enterpriseName?: string;
  businessNo?: string;
  phone?: string;
  netAddress?: string;
  enterpriseAddress?: string;
  legalPersonName?: string;
  legalPersonCredType?: string;
  legalPersonCredNo?: string;
  businessScope?: string;
  businessLicenceImg?: string;
  [key: string]: any;
}

interface MerchantAuditListProps {
  addCurrentRecord: (record: EnterpriseRecord) => void;
}

export default ({ addCurrentRecord }: MerchantAuditListProps) => {

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  /**
   * 处理审核操作
   */
  const handleAudit = async (record: EnterpriseRecord, auditFlag: string) => {
    try {
      const res = await auditCustomerEnterprise({
        merchantNo: record.serialNo,
        customerNo: record.customerNo,
        auditFlag,
      });

      if (res.code === 0) {
        message.success(auditFlag === '1' ? '审核通过成功' : '审核拒绝成功');
        // 刷新表格数据
        actionRef.current?.reload();
      } else {
        message.error(`操作失败：${res?.message}`);
      }
    } catch (error) {
      console.error('审核操作失败:', error);
      message.error('操作失败，请稍后重试');
    }
  };

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: EnterpriseRecord, index: number) => (
    <Space key={record.serialNo}>
      {/* 只有待审核状态才显示审核按钮 */}
      {record.authenticationStatus === '1' && (
        <>
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => addCurrentRecord(record)}
          >
            微信支付进件
          </Button>
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => addCurrentRecord(record)}
          >
            进件状态查询
          </Button>
        </>
      )}


    </Space>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    if (item.dataIndex === 'action') {
      item.render = actionRender;
    }
    // 添加状态列的渲染
    if (item.dataIndex === 'authenticationStatus') {
      item.render = (status: string) => {
        const statusMap = {
          '0': { text: '未认证', color: 'default' },
          '1': { text: '待审核', color: 'processing' },
          '2': { text: '认证成功', color: 'success' },
          '3': { text: '认证失败', color: 'error' },
        };
        const config = statusMap[status as keyof typeof statusMap] || { text: '未知', color: 'default' };
        return <Tag color={config.color}>{config.text}</Tag>;
      };
    }
  });

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="商户支付进件列表" />}
        scroll={{ x: 1200 }}
        bordered
        columns={columns}
        actionRef={actionRef}
        request={async (params) => {
          try {
            const res = await getCustomerEnterprisePageList({
              ...params,
              // 只显示待审核的商户
              authenticationStatus: '1',
            });

            return {
              data: res.data || [],
              total: res.pagination?.totalSize || 0,
              success: res.code === 0,
            };
          } catch (error) {
            console.error('获取商户列表失败:', error);
            return {
              data: [],
              total: 0,
              success: false,
            };
          }
        }}
        rowKey="customerNo"
        search={{
          labelWidth: 'auto',
        }}
        form={{
          ignoreRules: false,
        }}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
        }}
        options={{
          reload: true,
          density: false,
          setting: false,
        }}
      />
    </div>
  );
}; 