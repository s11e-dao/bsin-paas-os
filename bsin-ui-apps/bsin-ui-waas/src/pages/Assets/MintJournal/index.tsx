import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  Radio,
  Modal,
  message,
  Button,
  Select,
  Popconfirm,
  Descriptions,
  InputNumber,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getMintJournalPageList,
  addMintJournal,
  deleteMintJournal,
  getMintJournalDetail,
  getDigitalAssetsCollectionList,
  getDigitalAssetsItemList,
} from './service';
import TableTitle from '../../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isMintJournalModal, setIsMintJournalModal] = useState(false);
  // 查看模态框
  const [isViewMintJournalModal, setIsViewMintJournalModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 获取表单
  const [FormRef] = Form.useForm();

  const [
    digitalAssetsCollectionList,
    setDigitalAssetsCollectionList,
  ] = useState([]);

  const [digitalAssetsItemList, setDigitalAssetsItemList] = useState([]);

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
            toViewMintJournal(record);
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

  useEffect(() => {
    // 查询数字资产集合/ 数字资产Item
    let params = {
      current: '1',
      pageSize: '99',
    };
    getDigitalAssetsCollectionList(params).then((res) => {
      setDigitalAssetsCollectionList(res?.data);
      console.log(JSON.stringify(res?.data));
    });
    getDigitalAssetsItemList(params).then((res) => {
      setDigitalAssetsCollectionList(res?.data);
      console.log(JSON.stringify(res?.data));
    });
  }, []);

  /**
   * 以下内容为操作相关
   */

  // 新增模板
  const increaseMintJournal = () => {
    setIsMintJournalModal(true);
  };

  /**
   * Mint
   */
  const confirmMintJournal = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        addMintJournal(response).then((res) => {
          console.log('mint', res);
          if (res?.code == '000000') {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsMintJournalModal(false);
          } else {
            message.error(`Mint失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  /**
   * 取消添加模板
   */
  const onCancelMintJournal = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsMintJournalModal(false);
  };

  /**
   * 查看详情
   */
  const toViewMintJournal = async (record) => {
    let { serialNo } = record;
    let viewRes = await getMintJournalDetail({ serialNo });
    setIsViewMintJournalModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfMultimediaType = () => {
    let { multimediaType } = isViewRecord;
    // 多媒体类型： 1 图片  2 gif 3 视频 4 音频 5 json 6 文件夹
    let typeText = multimediaType;
    if (typeText == '1') {
      return '图片';
    } else if (typeText == '2') {
      return 'gif';
    } else if (typeText == '3') {
      return '视频';
    } else if (typeText == '4') {
      return '音频';
    } else if (typeText == '5') {
      return 'json';
    } else if (typeText == '6') {
      return '文件夹';
    }
    return typeText;
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="铸造记录" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getMintJournalPageList({
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
          <Button
            onClick={() => {
              increaseMintJournal();
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            铸造
          </Button>,
        ]}
      />
      {/* 新增Mint模板模态框 */}
      <Modal
        title="Mint"
        centered
        visible={isMintJournalModal}
        onOk={confirmMintJournal}
        onCancel={onCancelMintJournal}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ tokenId: 1, addPrivilege: '0' }}
        >
          <Form.Item
            label="数字资产编号"
            name="digitalAssetsCollectionNo"
            rules={[{ required: true, message: '请输入数字资产编号!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="tokenId"
            name="tokenId"
            rules={[{ required: true, message: '请输入tokenId!' }]}
          >
            <InputNumber min={1} />
          </Form.Item>
          <Form.Item
            label="amount"
            name="amount"
            rules={[{ required: true, message: '请输入amount!' }]}
          >
            <InputNumber min={1} />
          </Form.Item>
          //TODO: 支持search
          <Form.Item
            label="接受客户号"
            name="toCustomerNo"
            rules={[{ required: true, message: '请输入接收客户号!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="接收地址"
            name="toAddress"
            rules={[{ required: true, message: '请输入接收地址!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="是否将改地址加入白名单"
            name="addPrivilege"
            rules={[
              { required: true, message: '请选择是否将改地址加入白名单!' },
            ]}
          >
            <Radio.Group>
              <Radio value="0">是</Radio>
              <Radio value="1">否</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item
            label="描述"
            name="description"
            rules={[{ required: true, message: '请输入铸造描述!' }]}
          >
            <TextArea />
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看详情模态框 */}
      <Modal
        title="查看铸造详情"
        width={800}
        centered
        visible={isViewMintJournalModal}
        onOk={() => setIsViewMintJournalModal(false)}
        onCancel={() => setIsViewMintJournalModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="铸造记录">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="铸造编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="交易Hash">
            {isViewRecord?.txHash}
          </Descriptions.Item>
          <Descriptions.Item label="tokenId">
            {isViewRecord?.tokenId}
          </Descriptions.Item>
          <Descriptions.Item label="铸造数量">
            {isViewRecord?.amount}
          </Descriptions.Item>
          <Descriptions.Item label="多媒体类型">
            {handleViewRecordOfMultimediaType()}
          </Descriptions.Item>
          <Descriptions.Item label="资产名称">
            {isViewRecord?.itemName}
          </Descriptions.Item>
          <Descriptions.Item label="metadataImage">
            {isViewRecord?.metadataImage}
          </Descriptions.Item>
          <Descriptions.Item label="metadataUrl">
            {isViewRecord?.metadataUrl}
          </Descriptions.Item>
          <Descriptions.Item label="铸造人手机号">
            {isViewRecord?.toPhone}
          </Descriptions.Item>
          <Descriptions.Item label="铸造人姓名">
            {isViewRecord?.toMinterName}
          </Descriptions.Item>
          <Descriptions.Item label="接收地址">
            {isViewRecord?.toAddress}
          </Descriptions.Item>
          <Descriptions.Item label="接收客户号">
            {isViewRecord?.toCustomerNo}
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
