import React, { useState } from 'react';
import QRCode from 'qrcode.react';

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
import columnsObtainCodeData, {
  columnsObtainCodeDataType,
} from './obtaincodeData';

import {
  getDigitalAssetsItemPageList,
  addDigitalAssetsItem,
  deleteDigitalAssetsItem,
  getDigitalAssetsItemDetail,
  getDigitalAssetsItemObtainCodePageList,
} from './service';
import TableTitle from '../../../components/TableTitle';

import sloganLogo from '../../../assets/s11e-slogan.png';

import styles from './index.css';

export default ({ setCurrentContent, configAssetsItem }) => {
  let biganH5 = process.env.biganH5Url;

  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isAssetsItemModal, setIsAssetsItemModal] = useState(false);
  // 查看模态框
  const [isViewAssetsItemModal, setIsViewAssetsItemModal] = useState(false);

  // 查看领取二维码模态框
  const [
    isViewAssetsItemObtainQRCodeModal,
    setIsViewAssetsItemObtainQRCodeModal,
  ] = useState(false);

  // 查看领取码模态框
  const [
    isViewAssetsItemObtainCodeModal,
    setIsViewAssetsItemObtainCodeModal,
  ] = useState(false);

  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});

  // 查看领取码
  const [isViewObtainCodeRecord, setIsViewObtainCodeRecord] = useState({});

  // 查看领取码
  const [isViewObtainQRCodeRecord, setIsViewObtainQRCodeRecord] = useState({});

  // 获取表单
  const [FormRef] = Form.useForm();

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 表头数据
  const columnsObtainCode: ProColumns<
    columnsObtainCodeDataType
  >[] = columnsObtainCodeData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewAssetsItem(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <a
          onClick={() => {
            toConfigActivityEquity(record);
          }}
        >
          权益配置
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <a
          onClick={() => {
            toViewAssetsItemClaimQRCode(record);
          }}
        >
          领取二维码
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <a
          onClick={() => {
            toViewAssetsItemClaimCode(record);
          }}
        >
          领取口令
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

  // 领取码 Table action 的引用，便于自定义触发
  const obtainCodeActionRef = React.useRef<ActionType>();

  /**
   * 以下内容为操作相关
   */

  // 新增模板
  const increaseAssetsItem = () => {
    setIsAssetsItemModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmAssetsItem = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        addDigitalAssetsItem(response).then((res) => {
          console.log('add', res);
          if (res?.code == 0) {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsAssetsItemModal(false);
          } else {
            message.error(`添加数字资产失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  /**
   * 取消添加模板
   */
  const onCancelAssetsItem = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsAssetsItemModal(false);
  };

  /**
   * 删除模板
   */
  const toDelAssetsItem = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteDigitalAssetsItem({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === 0) {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewAssetsItem = async (record) => {
    let { serialNo } = record;
    let viewRes = await getDigitalAssetsItemDetail({ serialNo });
    setIsViewAssetsItemModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 去配置权益
   */
  const toConfigActivityEquity = async (record) => {
    // 条件分类：1、会员等级 2 数字资产 3 任务 4 活动
    record.category = '2';
    configAssetsItem(record, 'configEquity');
  };

  /**
   * 查看领取二维码
   */
  const toViewAssetsItemClaimQRCode = async (record) => {
    let { serialNo } = record;
    let viewRes = await getDigitalAssetsItemDetail({ serialNo });
    console.log('viewObtainCodeRes', viewRes);
    setIsViewObtainQRCodeRecord(viewRes.data);
    setIsViewAssetsItemObtainQRCodeModal(true);
  };

  /**
   * 查看领取口令
   */
  const toViewAssetsItemClaimCode = async (record) => {
    setIsViewObtainCodeRecord(record);
    setIsViewAssetsItemObtainCodeModal(true);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfAssetsType = () => {
    let { digitalAssetsItem } = isViewRecord;
    let assetsType = digitalAssetsItem?.assetsType;
    if (assetsType == `1`) {
      return '数字资产';
    } else if (assetsType == `2`) {
      return 'PFP';
    } else if (assetsType == `3`) {
      return '账户-DP';
    } else if (assetsType == `4`) {
      return '数字门票';
    } else if (assetsType == `5`) {
      return 'Pass卡';
    } else if (assetsType == `6`) {
      return '账户-BC';
    } else if (assetsType == `7`) {
      return '满减';
    } else if (assetsType == `8`) {
      return '权限';
    } else if (assetsType == `9`) {
      return '会员等级';
    } else {
      return assetsType;
    }
  };

  const handleViewRecordOfObtainMethod = () => {
    let { obtainMethod } = isViewRecord;
    if (obtainMethod == '1') {
      return '免费领取/空投';
    } else if (obtainMethod == '2') {
      return '购买';
    } else if (obtainMethod == '3') {
      return '固定口令领取';
    } else if (obtainMethod == '4') {
      return '随机口令';
    } else if (obtainMethod == '5') {
      return '盲盒';
    } else if (obtainMethod == '6') {
      return '活动';
    } else {
      return obtainMethod;
    }
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="数字资产" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          params.assetsTypes = ['1', '2', '3', '4', '5', '6'];
          // console.log(params);
          let res = await getDigitalAssetsItemPageList({
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
      {/* 新增合约模板模态框 */}
      <Modal
        title="添加数字资产"
        centered
        open={isAssetsItemModal}
        onOk={confirmAssetsItem}
        onCancel={onCancelAssetsItem}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ type: 'ERC20' }}
        >
          <Form.Item
            label="资产类型"
            name="type"
            rules={[{ required: true, message: '请选择资产类型!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="ERC20">ERC20</Option>
              <Option value="ERC721">ERC721</Option>
              <Option value="ERC1155">ERC1155</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="资产名称"
            name="protocolName"
            rules={[{ required: true, message: '请输入资产名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="资产编号"
            name="protocolCode"
            rules={[{ required: true, message: '请输入资产编号!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="资产描述"
            name="description"
            rules={[{ required: true, message: '请输入资产描述!' }]}
          >
            <TextArea />
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看详情模态框 */}
      <Modal
        title="查看数字资产"
        width={800}
        centered
        visible={isViewAssetsItemModal}
        onOk={() => setIsViewAssetsItemModal(false)}
        onCancel={() => setIsViewAssetsItemModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="数字资产信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.digitalAssetsItem?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="资产编号">
            {isViewRecord?.digitalAssetsItem?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="数字资产类型">
            {handleViewRecordOfAssetsType()}
          </Descriptions.Item>
          <Descriptions.Item label="资产名称">
            {isViewRecord?.digitalAssetsItem?.assetsName}
          </Descriptions.Item>
          <Descriptions.Item label="tokenId">
            {isViewRecord?.digitalAssetsItem?.tokenId}
          </Descriptions.Item>
          <Descriptions.Item label="数字资产数量">
            {isViewRecord?.digitalAssetsItem?.quantity}
          </Descriptions.Item>
          <Descriptions.Item label="数字资产库存">
            {isViewRecord?.digitalAssetsItem?.inventory}
          </Descriptions.Item>
          <Descriptions.Item label="数字资产价格">
            {isViewRecord?.digitalAssetsItem?.price}
          </Descriptions.Item>
          <Descriptions.Item label="获取方式">
            {handleViewRecordOfObtainMethod()}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewRecord?.digitalAssetsItem?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.digitalAssetsItem?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="资产描述">
            {isViewRecord?.digitalAssetsItem?.description}
          </Descriptions.Item>
        </Descriptions>
      </Modal>

      {/* 查看领取码模态框 */}
      <Modal
        title="查看领取码"
        width={800}
        centered
        open={isViewAssetsItemObtainCodeModal}
        onOk={() => setIsViewAssetsItemObtainCodeModal(false)}
        onCancel={() => setIsViewAssetsItemObtainCodeModal(false)}
      >
        <ProTable<columnsObtainCodeDataType>
          headerTitle={<TableTitle title="领取码" />}
          scroll={{ x: 900 }}
          bordered
          // 表头
          columns={columnsObtainCode}
          actionRef={obtainCodeActionRef}
          // 请求获取的数据
          request={async (params) => {
            // console.log(params);
            let res = await getDigitalAssetsItemObtainCodePageList({
              ...params,
              assetsNo: isViewObtainCodeRecord?.serialNo,
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
          search={false}
          // 搜索表单的配置
          form={{
            ignoreRules: false,
          }}
          pagination={{
            pageSize: 10,
          }}
        />
      </Modal>

      {/* 查看领取二维码模态框 */}
      <Modal
        title="查看领取二维码"
        // width={800}
        footer={[]}
        centered
        open={isViewAssetsItemObtainQRCodeModal}
        onOk={() => setIsViewAssetsItemObtainQRCodeModal(false)}
        onCancel={() => setIsViewAssetsItemObtainQRCodeModal(false)}
      >
        <div className="">
          <div className={styles.frame}>
            <div className={styles.header}>
              <div>
                <img
                  className={styles.nftImg}
                  src={isViewObtainQRCodeRecord?.digitalAssetsItem?.coverImage}
                />
              </div>
            </div>

            <div className={styles.content}>
              <span className={styles.content_top}>
                {isViewObtainQRCodeRecord?.digitalAssetsItem?.assetsName}
              </span>
              <div style={{ height: '175px' }}>
                <div className={styles.content_left}>
                  <span className={styles.first_section}>
                    {isViewObtainQRCodeRecord?.digitalAssetsItem?.description}
                  </span>
                </div>
                <div className={styles.content_rigth}>
                  <div className={styles.qrcode}>
                    <QRCode
                      className={styles.obtainCode}
                      //TODO: add chainType field
                      value={
                        biganH5 +
                        '?chainEnv=' +
                        isViewObtainQRCodeRecord?.digitalAssetsItem?.chainEnv +
                        '&serialNo=' +
                        isViewObtainQRCodeRecord?.digitalAssetsItem?.serialNo
                      }
                      size={230}
                      id="qrCode"
                    />
                  </div>
                  <span>扫码领取NFT</span>
                </div>
              </div>
              <img className={styles.logo} src={sloganLogo} />
            </div>
          </div>
        </div>
      </Modal>
    </div>
  );
};
