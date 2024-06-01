import React, { useState } from 'react';
import {
  Form,
  Input,
  Modal,
  message,
  Divider,
  Select,
  Popconfirm,
  Descriptions,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getCustomerEnterprisePageList,
  getCustomerEnterpriseInfo,
  auditCustomerEnterprise,
} from './service';
import TableTitle from '../../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制编辑模态框
  const [editModal, setEditModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 获取表单
  const [editFormRef] = Form.useForm();

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <div key={record.dictType}>
      {record.authenticationStatus == '1' ? (
        <>
          <Popconfirm
            title="确认此账号通过审核 ？"
            onConfirm={async () => {
              let res = await auditCustomerEnterprise({
                merchantNo: record.serialNo,
                auditFlag: '1',
              });
              if (res.code === '000000') {
                message.success('已通过审核');
              }
              // 表格重新渲染
              actionRef.current?.reload();
            }}
            okText="确认"
            cancelText="取消"
          >
            <a>通过</a>
          </Popconfirm>
          <Divider type="vertical" />
          <Popconfirm
            title="确认拒绝开通DAO ？"
            onConfirm={async () => {
              let res = await auditCustomerEnterprise({
                customerNo: record.customerNo,
                auditFlag: '2',
              });
              if (res.code === '000000') {
                message.success('已拒绝');
              }
              // 表格重新渲染
              actionRef.current?.reload();
            }}
            okText="确认"
            cancelText="取消"
          >
            <a>拒绝</a>
          </Popconfirm>
        </>
      ) : null}
      <Divider type="vertical" />
      <a onClick={() => toEditCustomerEnterpriseInfo(record)}>查看</a>
      <Divider type="vertical" />
    </div>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  /**
   * 确认配置
   */
  const confirmApiFeeConfig = () => {
    // 刷新proTable
    actionRef.current?.reload();
    // 获取输入的表单值
    editFormRef
      .validateFields()
      .then(async () => {
        // 获取表单结果
        let response = editFormRef.getFieldsValue();
        console.log(response);
        auditCustomerEnterprise(response).then((res) => {
          console.log('审核', res);
          if (res.code === '000000') {
            // 重置输入的表单
            editFormRef.resetFields();
            setEditModal(false);
          } else {
            message.error(`失败： ${res?.message}`);
          }
        });
      })
      .catch(() => { });
  };

  /**
   * 取消添加模板
   */
  const onCancelApiFeeConfig = () => {
    // 重置输入的表单
    editFormRef.resetFields();
    setEditModal(false);
  };

  // 点击编辑
  const toEditCustomerEnterpriseInfo = async (record) => {
    let { customerNo } = record;
    let res = await getCustomerEnterpriseInfo({ customerNo });
    console.log(res);
    setIsViewRecord(res.data);
    setEditModal(true);
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="商户认证信息" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getCustomerEnterprisePageList({
            ...params,
            status: '2',
          });
          console.log('😒', res);
          const result = {
            data: res.data,
            total: res.pagination.totalSize,
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
      />
      {/* 新增合约模板模态框 */}
      <Modal
        title="商户认证审核"
        centered
        width={1000}
        visible={editModal}
        footer={null}
        onOk={confirmApiFeeConfig}
        onCancel={onCancelApiFeeConfig}
      >
        <Descriptions>
          <Descriptions.Item label="客户号">
            {isViewRecord?.customerNo}
          </Descriptions.Item>
          <Descriptions.Item label="账号状态">
            {isViewRecord.status == '0'
              ? '正常'
              : isViewRecord.status == '1'
                ? '冻结'
                : '暂无'}
          </Descriptions.Item>
          <Descriptions.Item label="认证状态">
            {isViewRecord.authenticationStatus == '0'
              ? '未认证'
              : isViewRecord.authenticationStatus == '1'
                ? '待审核'
                : isViewRecord.authenticationStatus == '2'
                  ? '认证成功'
                  : isViewRecord.authenticationStatus == '3'
                    ? '认证失败'
                    : '暂无'}
          </Descriptions.Item>
          <Descriptions.Item label="企业名称">
            {isViewRecord?.enterpriseName}
          </Descriptions.Item>
          <Descriptions.Item label="企业工商号">
            {isViewRecord?.businessNo}
          </Descriptions.Item>
          <Descriptions.Item label="联系电话">
            {isViewRecord?.phone}
          </Descriptions.Item>
          <Descriptions.Item label="公司网址">
            {isViewRecord?.netAddress}
          </Descriptions.Item>
          <Descriptions.Item label="企业地址">
            {isViewRecord?.enterpriseAddress}
          </Descriptions.Item>
          <Descriptions.Item label="法人姓名">
            {isViewRecord?.legalPersonName}
          </Descriptions.Item>
          <Descriptions.Item label="法人证件类型">
            {isViewRecord.legalPersonCredType == '0'
              ? '大陆居民身份证'
              : '军官证'}
          </Descriptions.Item>
          <Descriptions.Item label="法人证件号">
            {isViewRecord?.legalPersonCredNo}
          </Descriptions.Item>
          <Descriptions.Item label="经营范围">
            {isViewRecord?.businessScope}
          </Descriptions.Item>
          <Descriptions.Item label="营业执照">
            <img
              style={{ width: '100px', height: '100px' }}
              src={isViewRecord.businessLicenceImg}
              alt="这是营业执照"
            />
          </Descriptions.Item>
          {/* <Descriptions.Item label="Address">
            {DAOInfo?.}
          </Descriptions.Item> */}
        </Descriptions>
      </Modal>
    </div>
  );
};
