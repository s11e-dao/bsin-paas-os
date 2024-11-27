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
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import { getAccountJournalPageList, getAccountJournalDetail } from './service';
import TableTitle from '../../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isAccountJournalModal, setIsAccountJournalModal] = useState(false);
  // 查看模态框
  const [isViewAccountJournalModal, setIsViewAccountJournalModal] = useState(
    false,
  );
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 获取表单
  const [FormRef] = Form.useForm();

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <div key={record.dictType}>
      <a onClick={() => toViewAccountJournal(record)}>查看</a>
    </div>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  /**
   * 查看详情
   */
  const toViewAccountJournal = async (record) => {
    let { serialNo } = record;
    let viewRes = await getAccountJournalDetail({ serialNo });
    setIsViewAccountJournalModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfOrderType = () => {
    let { orderType } = isViewRecord;
    // 订单类型 0、支付 1、退款 2、出售 3、充值 4、转账 5、提现
    if (orderType == '0') {
      return '支付';
    } else if (orderType == '1') {
      return '退款';
    } else if (orderType == '2') {
      return '出售';
    } else if (orderType == '3') {
      return '充值';
    } else if (orderType == '4') {
      return '转账';
    } else if (orderType == '5') {
      return '提现';
    } else {
      return orderType;
    }
  };

  const handleViewRecordOfAccountType = () => {
    let { accountType } = isViewRecord;
    // 账户类型 0、个人账户 1、企业账户
    if (accountType == '0') {
      return '个人账户';
    } else if (accountType == '1') {
      return '企业账户';
    } else {
      return accountType;
    }
  };

  const handleViewRecordOfInOutFlag = () => {
    let { inOutFlag } = isViewRecord;
    // 出账入账标志 0、出账 1、入账
    if (inOutFlag == '0') {
      return '出账';
    } else if (inOutFlag == '1') {
      return '入账';
    } else {
      return inOutFlag;
    }
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="账户流水" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getAccountJournalPageList({
            ...params,
            // pageNum: params.current,
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

      {/* 查看详情模态框 */}
      <Modal
        title="查看账号流水"
        width={800}
        centered
        open={isViewAccountJournalModal}
        onOk={() => setIsViewAccountJournalModal(false)}
        onCancel={() => setIsViewAccountJournalModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="账号流水信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="流水编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="账号流水类型">
            {handleViewRecordOfOrderType()}
          </Descriptions.Item>
          <Descriptions.Item label="账号类型">
            {handleViewRecordOfAccountType()}
          </Descriptions.Item>
          <Descriptions.Item label="出账入账类型">
            {handleViewRecordOfInOutFlag()}
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
