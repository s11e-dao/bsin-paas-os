import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Select,
  Divider,
  Popconfirm,
  Descriptions,
  Row,
  Col,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getEquityPageList,
  addEquity,
  deleteEquity,
  getEquityDetail,
  getDigitalAssetsItemList,
  getBondingCurveTokenList,
} from './service';

import { getGradeList } from '../../Grade/service';

import TableTitle from '../../../components/TableTitle';

interface Props {}

export default ({}: Props) => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isEquityModal, setIsEquityModal] = useState(false);
  // 查看模态框
  const [isViewEquityModal, setIsViewEquityModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState<any>({});
  const [typeNoList, setTypeNoList] = useState<any[]>([]);
  const [gradeList, setGradeList] = useState<any[]>([]);
  const [digitalAssetsType, setDigitalAssetsType] = useState('');
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
      <a onClick={() => toViewEquity(record)}>查看</a>
      <Divider type="vertical" />
      <Popconfirm
        title="确定删除此条数据？?"
        onConfirm={() => toDelEquity(record.id)}
        onCancel={() => {
          message.warning(`取消删除`);
        }}
        okText="是"
        cancelText="否"
      >
        <a>删除</a>
      </Popconfirm>
    </div>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  useEffect(() => {}, []);

  /**
   * 以下内容为操作相关
   */

  // 新增模板
  const increaseEquity = () => {
    setIsEquityModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmEquity = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        addEquity(response).then((res) => {
          console.log('add', res);
          if (res.code === 0 ) {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsEquityModal(false);
          } else {
            message.error(`失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  /**
   * 取消添加模板
   */
  const onCancelEquity = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsEquityModal(false);
  };

  /**
   * 删除模板
   */
  const toDelEquity = async (record: any) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteEquity({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === 0) {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewEquity = async (record: any) => {
    let { serialNo } = record;
    let viewRes = await getEquityDetail({ serialNo });
    setIsViewEquityModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfType = () => {
    let { type } = isViewRecord;
    if (type == `1`) {
      return '数字资产';
    } else if (type == `2`) {
      return 'PFP';
    } else if (type == `3`) {
      return '账户-DP';
    } else if (type == `4`) {
      return '数字门票';
    } else if (type == `5`) {
      return 'Pass卡';
    } else if (type == `6`) {
      return '账户-BC';
    } else if (type == `7`) {
      return '满减';
    } else if (type == `8`) {
      return '权限';
    } else if (type == `9`) {
      return '会员等级';
    } else {
      return type;
    }
  };

  const handleViewRecordOfCategory = () => {
    let { type } = isViewRecord;
    // 权益分类：1：等级 2 任务 3 活动
    if (type == `1`) {
      return '等级';
    } else if (type == `2`) {
      return '任务';
    } else if (type == `3`) {
      return '活动';
    } else {
      return type;
    }
  };

  // 根据资产类型获取对应的数字资产列表
  const assetTypeOnChange = (value: string) => {
    console.log(value);
    setDigitalAssetsType(value);
    
    // 根据资产类型映射到对应的数字资产类型
    let assetTypeMapping: { [key: string]: string[] } = {
      'BADGE': ['1'],      // 数字徽章
      'PFP': ['2'],        // PFP
      'POINTS': ['3'],     // 数字积分
      'TICKET': ['4'],     // 数字门票
      'PASS': ['5'],       // PASS卡
      'COIN': ['6'],       // 联合曲线
      'COUPON': [],        // 优惠券（不需要数字资产）
      'EXP': []            // 经验值（不需要数字资产）
    };

    const mappedTypes = assetTypeMapping[value] || [];
    
    if (mappedTypes.length > 0) {
      // 请求后台获取对应的数字资产
      let params = {
        assetsTypes: mappedTypes,
      };
      getDigitalAssetsItemList(params).then((res) => {
        console.log(res);
        let typeNoListTemp: any[] = [];
        if (res?.code == 0) {
          res?.data.map((item: any) => {
            console.log(item);
            let typeNoJson = {
              typeNo: item.serialNo,
              name: item.assetsName,
              tokenId: item.tokenId,
            };
            typeNoListTemp.push(typeNoJson);
          });
        }
        setTypeNoList(typeNoListTemp);
      });
    } else if (value === 'COUPON') {
      // 优惠券类型，不需要数字资产
      let typeNoListTemp: any[] = [];
      setTypeNoList(typeNoListTemp);
    } else if (value === 'EXP') {
      // 经验值类型，不需要数字资产
      let typeNoListTemp: any[] = [];
      setTypeNoList(typeNoListTemp);
    }
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="权益列表" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getEquityPageList({
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
              increaseEquity();
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            添加权益
          </Button>,
        ]}
      />
      {/* 新增合约模板模态框 */}
      <Modal
        title="添加权益"
        centered
        open={isEquityModal}
        onOk={confirmEquity}
        onCancel={onCancelEquity}
        width={800}
        okText="保存"
        cancelText="取消"
      >
        <Form
          name="basic"
          form={FormRef}
          layout="vertical"
          // 表单默认值
          initialValues={{ 
            typeNo: '0',
            assetType: 'POINTS',
            amountType: 'FIXED'
          }}
        >

          <Row gutter={16} style={{ marginTop: '16px' }}>
            <Col span={12}>
              <Form.Item
                label="权益类型"
                name="assetType"
                rules={[{ required: true, message: '请选择资产类型!' }]}
              >
                <Select 
                  style={{ width: '100%' }}
                  onChange={(value) => assetTypeOnChange(value)}
                >
                  <Option value="POINTS">积分</Option>
                  <Option value="COIN">金币</Option>
                  <Option value="EXP">经验值</Option>
                  <Option value="COUPON">优惠券</Option>
                  <Option value="BADGE">徽章</Option>
                  <Option value="PFP">PFP</Option>
                  <Option value="TICKET">门票</Option>
                  <Option value="PASS">Pass卡</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="权益资产"
                name="typeNo"
                rules={[{ required: true, message: '请选择权益资产!' }]}
              >
                <Select style={{ width: '100%' }}>
                  <Option value="0">请选择权益资产</Option>
                  {typeNoList.map((typeNo) => {
                    return (
                      <Option key={typeNo?.typeNo} value={typeNo?.typeNo}>
                        {(typeNo?.typeNo).slice(-4) +
                          '-' +
                          typeNo?.name +
                          '-' +
                          typeNo?.tokenId}
                      </Option>
                    );
                  })}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="奖励类型"
                name="amountType"
                rules={[{ required: true, message: '请选择奖励类型!' }]}
              >
                <Select style={{ width: '100%' }}>
                  <Option value="FIXED">固定数量</Option>
                  <Option value="RANDOM">随机区间</Option>
                  <Option value="FORMULA">公式计算</Option>
                  <Option value="PERCENT">按比例</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="奖励数量"
                name="amount"
                rules={[{ required: false, message: '请输入奖励数量!' }]}
              >
                <Input placeholder="请输入奖励数量" />
              </Form.Item>
            </Col>
          </Row>


          <Form.Item
            label="权益名称"
            name="name"
            rules={[{ required: true, message: '请输入权益名称!' }]}
          >
            <Input placeholder="请输入权益名称" />
          </Form.Item>

          <Form.Item
            label="备注"
            name="remark"
            rules={[{ required: true, message: '请输入备注描述!' }]}
          >
            <Input.TextArea rows={3} placeholder="请输入备注描述" />
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看详情模态框 */}
      <Modal
        title="查看权益详情"
        width={800}
        centered
        open={isViewEquityModal}
        onOk={() => setIsViewEquityModal(false)}
        onCancel={() => setIsViewEquityModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="权益详情">
          <Descriptions.Item label="协议编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="权益名称">
            {isViewRecord?.name}
          </Descriptions.Item>
          <Descriptions.Item label="权益类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="权益类型编号">
            {isViewRecord?.typeNo}
          </Descriptions.Item>
          <Descriptions.Item label="权益分类">
            {handleViewRecordOfCategory()}
          </Descriptions.Item>
          <Descriptions.Item label="权益分类编号">
            {isViewRecord?.categoryNo}
          </Descriptions.Item>
          <Descriptions.Item label="满减总金额">
            {isViewRecord?.totalAmount}
          </Descriptions.Item>
          <Descriptions.Item label="满减金额、赠送金额、赠送数量 折扣值">
            {isViewRecord?.amount}
          </Descriptions.Item>
          <Descriptions.Item label="叠加使用标识(1:是,2:否)">
            {isViewRecord?.overFlag}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="权益备注描述">
            {isViewRecord?.remark}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
