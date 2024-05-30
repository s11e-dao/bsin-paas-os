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
import { getMemberAssetsPageList, getMemberAssetsDetail } from './service';
import TableTitle from '../../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isMemberAssetsModal, setIsMemberAssetsModal] = useState(false);
  // 查看模态框
  const [isViewMemberAssetsModal, setIsViewMemberAssetsModal] = useState(false);
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
            toViewMemberAssets(record);
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

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  /**
   * 以下内容为操作相关
   */

  // 新增模板
  const increaseMemberAssets = () => {
    setIsMemberAssetsModal(true);
  };

  /**
   * 查看详情
   */
  const toViewMemberAssets = async (record) => {
    let { serialNo } = record;
    let viewRes = await getMemberAssetsDetail({ serialNo });
    setIsViewMemberAssetsModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="客户资产信息" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getMemberAssetsPageList({
            ...params,
            // pageNum: params.current,
          });
          console.log('😒', res);
          const result = {
            data: res.data,
            total: res.pagination?.totalSize,
          };
          return result;
        }}
        rowKey="customerNo"
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
          <Button
            onClick={() => {
              increaseMemberAssets();
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            新增
          </Button>,
        ]}
      />
      {/* 查看详情模态框 */}
      <Modal
        title="查看客户资产信息"
        width={800}
        centered
        visible={isViewMemberAssetsModal}
        onOk={() => setIsViewMemberAssetsModal(false)}
        onCancel={() => setIsViewMemberAssetsModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="客户资产信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="商户编号">
            {isViewRecord?.merchantNo}
          </Descriptions.Item>
          <Descriptions.Item label="客户编号">
            {isViewRecord?.customerNo}
          </Descriptions.Item>
          <Descriptions.Item label="数字资产编号">
            {isViewRecord?.digitalAssetsCollectionNo}
          </Descriptions.Item>
          <Descriptions.Item label="tokenId">
            {isViewRecord?.tokenId}
          </Descriptions.Item>
          <Descriptions.Item label="amount">
            {isViewRecord?.amount}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="资产描述">
            {isViewRecord?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
