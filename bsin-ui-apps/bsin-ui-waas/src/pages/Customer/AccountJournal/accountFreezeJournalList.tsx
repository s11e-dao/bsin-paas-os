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
import columnsData, { columnsDataType } from './accountFreezeJournalData';
import {
  getAccountFreezeJournalPageList,
  getAccountFreezeJournalDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [
    isAccountFreezeJournalModal,
    setIsAccountFreezeJournalModal,
  ] = useState(false);
  // 查看模态框
  const [
    isViewAccountFreezeJournalModal,
    setIsViewAccountFreezeJournalModal,
  ] = useState(false);
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
      <a onClick={() => toViewAccountFreezeJournal(record)}>查看</a>
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
  const toViewAccountFreezeJournal = async (record) => {
    let { serialNo } = record;
    let viewRes = await getAccountFreezeJournalDetail({ serialNo });
    setIsViewAccountFreezeJournalModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfType = () => {
    let { type } = isViewRecord;
    // 冻结事件类型：（1.社区提案、2.订单 3.数字资产 4.任务)
    if (type == '1') {
      return '社区提案';
    } else if (type == '2') {
      return '订单';
    } else if (type == '3') {
      return '数字资产';
    } else if (type == '4') {
      return '任务质押';
    } else {
      return type;
    }
  };

  const handleViewRecordOfStatus = () => {
    let { status } = isViewRecord;
    if (status == '1') {
      return '已冻结';
    } else if (status == '2') {
      return '部分解冻';
    } else if (status == '3') {
      return '已解冻';
    } else {
      return status;
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
          let res = await getAccountFreezeJournalPageList({
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
        title="查看账号冻结流水"
        width={800}
        centered
        open={isViewAccountFreezeJournalModal}
        onOk={() => setIsViewAccountFreezeJournalModal(false)}
        onCancel={() => setIsViewAccountFreezeJournalModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="账号冻结流水信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="冻结流水编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="账号冻结流水类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="冻结状态">
            {handleViewRecordOfStatus()}
          </Descriptions.Item>
          <Descriptions.Item label="冻结金额">
            {isViewRecord?.freezeAmount}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="更新时间">
            {isViewRecord?.updateTime}
          </Descriptions.Item>
          <Descriptions.Item label="描述">
            {isViewRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
