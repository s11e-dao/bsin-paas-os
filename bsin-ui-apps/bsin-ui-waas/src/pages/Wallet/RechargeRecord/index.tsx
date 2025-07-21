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
import {
  getWithdrawJournalPageList,
  doWithdraw,
  getWithdrawJournalDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isTemplateModal, setIsTemplateModal] = useState(false);
  // 查看模态框
  const [isViewTemplateModal, setIsViewTemplateModal] = useState(false);
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
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewContractTemplate(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
      <Popconfirm
          title="确定同意此条信息？"
          okText="是"
          cancelText="否"
          onConfirm={() => {
            toViewContractTemplate(record);
          }}
          // onCancel={cancel}
        >
          <a>同意</a>
        </Popconfirm>
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
   * 去支付提现
   */
  const toPayWithdraw = async (record) => {
    // 换起钱包支付，通知后台接口
    console.log('record', record);
    let { serialNo } = record;

    let delRes = await doWithdraw({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === 0) {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewContractTemplate = async (record) => {
    let { serialNo } = record;
    let viewRes = await getWithdrawJournalDetail({ serialNo });
    setIsViewTemplateModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfType = () => {
    let { type } = isViewRecord;
    let typeText = type;
    return typeText;
  };

  /**
   * 授权平台可操作金额
   */

  return (
    <div>

      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="充值订单" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getWithdrawJournalPageList({
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
        toolBarRender={() => [
        ]}
      />

      {/* 查看详情模态框 */}
      <Modal
        title="查看提现详情"
        width={800}
        centered
        open={isViewTemplateModal}
        onOk={() => setIsViewTemplateModal(false)}
        onCancel={() => setIsViewTemplateModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="订单号">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="协议编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="协议名称">
            {isViewRecord?.templateName}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议bytecode">
            {isViewRecord?.templateBytecode}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议abi字符">
            {isViewRecord?.templateAbi}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="协议描述">
            {isViewRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
