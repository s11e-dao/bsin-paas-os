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
  getDigitalAssetsCollectionPageList,
  deleteDigitalAssetsCollection,
  getDigitalAssetsCollectionDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';

export default ({ setCurrentContent, putOnShelves }) => {
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
        <a
          onClick={() => {
            putOnShelves(record);
          }}
        >
          上架
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
   * 删除模板
   */
  const toDelContractTemplate = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteDigitalAssetsCollection({ serialNo });
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
    let viewRes = await getDigitalAssetsCollectionDetail({ serialNo });
    setIsViewTemplateModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfCollectionType = () => {
    let { collectionType } = isViewRecord;
    if (collectionType == `1`) {
      return '数字资产';
    } else if (collectionType == `2`) {
      return 'PFP';
    } else if (collectionType == `3`) {
      return '账户-DP';
    } else if (collectionType == `4`) {
      return '数字门票';
    } else if (collectionType == `5`) {
      return 'Pass卡';
    } else if (collectionType == `6`) {
      return '账户-BC';
    } else if (collectionType == `7`) {
      return '满减';
    } else if (collectionType == `8`) {
      return '权限';
    } else if (collectionType == `9`) {
      return '会员等级';
    } else {
      return collectionType;
    }
  };

  const handleViewRecordOfSponsorFlag = () => {
    let { sponsorFlag } = isViewRecord;
    if (sponsorFlag == '0') {
      return '否';
    } else if (sponsorFlag == '1') {
      return '是';
    } else {
      return sponsorFlag;
    }
  };
  const handleViewRecordOfBondingCurveFlag = () => {
    let { bondingCurveFlag } = isViewRecord;
    if (bondingCurveFlag == '0') {
      return '否';
    } else if (bondingCurveFlag == '1') {
      return '是';
    } else {
      return bondingCurveFlag;
    }
  };

  const handleViewRecordOfMetadataImageSameFlag = () => {
    let { metadataImageSameFlag } = isViewRecord;
    if (metadataImageSameFlag == '0') {
      return '否';
    } else if (metadataImageSameFlag == '1') {
      return '是';
    } else {
      return 'null';
    }
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="数字资产集合" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getDigitalAssetsCollectionPageList({
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
            onClick={async () => {
              console.log('res');
              setCurrentContent('issueAssets');
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            发行
          </Button>,
          <Button
            onClick={async () => {
              console.log('res');
              setCurrentContent('editor');
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            editor
          </Button>,
        ]}
      />

      {/* 查看详情模态框 */}
      <Modal
        title="查看数字资产"
        width={800}
        centered
        open={isViewTemplateModal}
        onOk={() => setIsViewTemplateModal(false)}
        onCancel={() => setIsViewTemplateModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="数字资产信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="资产编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="数字资产类型">
            {handleViewRecordOfCollectionType()}
          </Descriptions.Item>
          <Descriptions.Item label="资产名称">
            {isViewRecord?.name}
          </Descriptions.Item>
          <Descriptions.Item label="资产合约地址">
            {isViewRecord?.contractAddress}
          </Descriptions.Item>
          <Descriptions.Item label="资产符号">
            {isViewRecord?.symbol}
          </Descriptions.Item>
          <Descriptions.Item label="资产总供应量">
            {isViewRecord?.totalSupply}
          </Descriptions.Item>
          <Descriptions.Item label="库存">
            {isViewRecord?.stock}
          </Descriptions.Item>
          <Descriptions.Item label="合约协议">
            {isViewRecord?.contractProtocolNo}
          </Descriptions.Item>
          <Descriptions.Item label="资产链类型">
            {isViewRecord?.chainType}
          </Descriptions.Item>
          <Descriptions.Item label="资产链链网络环境">
            {isViewRecord?.chainEnv}
          </Descriptions.Item>
          <Descriptions.Item label="资产赞助">
            {/* {isViewRecord?.sponsorFlag} */}
            {handleViewRecordOfSponsorFlag()}
          </Descriptions.Item>
          <Descriptions.Item label="是否基于联合曲线铸造">
            {/* {isViewRecord?.bondingCurveFlag} */}
            {handleViewRecordOfBondingCurveFlag()}
          </Descriptions.Item>
          <Descriptions.Item label="是否是同质化铸造NFT">
            {/* {isViewRecord?.metadataImageSameFlag} */}
            {handleViewRecordOfMetadataImageSameFlag()}
          </Descriptions.Item>

          <Descriptions.Item label="创建者">
            {isViewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
