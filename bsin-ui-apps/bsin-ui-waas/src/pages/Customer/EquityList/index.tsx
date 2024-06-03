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
} from './service';

import {
  getDigitalAssetsItemPageList,
  getDigitalAssetsItemList,
  getBondingCurveTokenList,
} from '../../Assets/AssetsList/service';

import { getGradeList } from '../Grade/service';

import TableTitle from '../../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isEquityModal, setIsEquityModal] = useState(false);
  // 查看模态框
  const [isViewEquityModal, setIsViewEquityModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  const [typeNoList, setTypeNoList] = useState([]);
  const [gradeList, setGradeList] = useState([]);
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
          if (res.code === '000000') {
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
  const toDelEquity = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteEquity({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === '000000') {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewEquity = async (record) => {
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

  const typeOnChange = (value) => {
    console.log(value);
    setDigitalAssetsType(value);
    // 1、数字徽章 2、PFP 3、数字积分 4、数字门票 5、pass卡 6、账户-联合曲线(BC)  7：满减 8：权限
    if (value == '1') {
      // 请求后台获取商户上架的数字资产： ERC1155...
      let params = {
        assetsTypes: ['1'],
      };
      getDigitalAssetsItemList(params).then((res) => {
        console.log(res);
        let typeNoListTemp = [];
        if (res?.code == '000000') {
          res?.data.map((item) => {
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
    } else if (value == '2') {
      // 请求后台获取商户上架的数字资产： PFP(ERC721)
      let params = {
        assetsTypes: ['2'],
      };
      getDigitalAssetsItemList(params).then((res) => {
        console.log(res);
        let typeNoListTemp = [];
        if (res?.code == '000000') {
          res?.data.map((item) => {
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
    }
    // 账户-DP（数字积分）
    else if (value == '3') {
      // 请求后台获取商户上架的数字资产： ERC20
      let params = {
        // 1、数字徽章 2、PFP 3、数字积分 4、数字门票 5、pass卡 6、徽章/门票
        assetsTypes: ['3'],
      };
      getDigitalAssetsItemList(params).then((res) => {
        console.log(res);
        let typeNoListTemp = [];
        if (res?.code == '000000') {
          res?.data.map((item) => {
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
    } else if (value == '4') {
      // 请求后台获取商户上架的数字资产： 数字门票(ERC1155)
      let params = {
        assetsTypes: ['4'],
      };
      getDigitalAssetsItemList(params).then((res) => {
        console.log(res);
        let typeNoListTemp = [];
        if (res?.code == '000000') {
          res?.data.map((item) => {
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
    } else if (value == '5') {
      // 请求后台获取商户上架的数字资产： PASS卡(ERC1155)
      let params = {
        assetsTypes: ['5'],
      };
      getDigitalAssetsItemList(params).then((res) => {
        console.log(res);
        let typeNoListTemp = [];
        if (res?.code == '000000') {
          res?.data.map((item) => {
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
    }
    // 账户-BC
    else if (value == '6') {
      // 请求后台获取商户上架的联合曲线积分： bondingCure
      let params = {
        // 1、
        assetsTypes: [],
      };
      getBondingCurveTokenList(params).then((res) => {
        console.log(res);
        let typeNoListTemp = [];
        if (res?.code == '000000') {
          res?.data.map((item) => {
            console.log(item);
            let typeNoJson = {
              typeNo: item?.serialNo,
              name: item?.name,
            };
            typeNoListTemp.push(typeNoJson);
          });
        }
        setTypeNoList(typeNoListTemp);
      });
    }
    // 满减
    else if (value == '7') {
      //TODO: query 满减
      setTypeNoList([]);
    }
    // 权限
    else if (value == '8') {
      //TODO: query 权限
      setTypeNoList([]);
    } else if (value == '9') {
      // 查询等级
      let params = {
        // 1、
        assetsTypes: [],
      };
      getGradeList(params).then((res) => {
        console.log(res);
        let typeNoListTemp = [];
        if (res?.code == '000000') {
          res?.data.map((item) => {
            console.log(item);
            let typeNoJson = {
              typeNo: item?.serialNo,
              name: item?.name,
              gradeNum: item?.gradeNum,
            };
            typeNoListTemp.push(typeNoJson);
          });
        }
        setTypeNoList(typeNoListTemp);
      });
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
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ type: '0', typeNo: '0' }}
        >
          <Form.Item
            label="权益名称"
            name="name"
            rules={[{ required: true, message: '请输入权益名称!' }]}
          >
            <Input />
          </Form.Item>
          {/* 1：数字资产 2：账户 3满减, 4:折扣 5权限 */}
          <Form.Item
            label="权益类型"
            name="type"
            rules={[{ required: true, message: '请选择权益类型!' }]}
          >
            <Select
              style={{ width: '100%' }}
              onChange={(value) => typeOnChange(value)}
            >
              <Option value="0">请选择权益类型</Option>
              <Option value="1">数字徽章</Option>
              <Option value="2">PFP</Option>
              <Option value="3">账户-DP</Option>
              <Option value="4">数字门票</Option>
              <Option value="5">Pass卡</Option>
              <Option value="6">账户-BC</Option>
              <Option value="7">满减</Option>
              <Option value="8">权限</Option>
              <Option value="9">会员等级</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="权益"
            name="typeNo"
            rules={[{ required: true, message: '请选择权益!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="0">请选择权益</Option>
              {typeNoList.map((typeNo) => {
                return (
                  <Option value={typeNo?.typeNo}>
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
          {digitalAssetsType == '6' ? (
            <Form.Item
              label="劳动价值"
              name="amount"
              rules={[{ required: true, message: '请输入数量!' }]}
            >
              <Input />
            </Form.Item>
          ) : (
            <Form.Item
              label="数量"
              name="amount"
              rules={[{ required: false, message: '请输入数量!' }]}
            >
              <Input />
            </Form.Item>
          )}

          {/* <Form.Item
            label="会员等级"
            name="grade"
            rules={[{ required: false, message: '请选择会员等级!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="0">请选择会员等级</Option>
              {gradeList.map((grade) => {
                return (
                  <Option value={grade?.serialNo}>
                    {grade?.name + '-' + grade?.gradeNum}
                  </Option>
                );
              })}
            </Select>
          </Form.Item> */}

          <Form.Item
            label="备注"
            name="remark"
            rules={[{ required: true, message: '请输入备注描述!' }]}
          >
            <Input />
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
