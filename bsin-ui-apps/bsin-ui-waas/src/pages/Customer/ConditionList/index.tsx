import React, { useState, useEffect } from 'react';
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
  getConditionPageList,
  addCondition,
  deleteCondition,
  getConditionDetail,
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
  const [isTemplateModal, setIsTemplateModal] = useState(false);
  // 查看模态框
  const [isViewTemplateModal, setIsViewTemplateModal] = useState(false);
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
          title="确定删除此条模板？"
          okText="是"
          cancelText="否"
          onConfirm={() => {
            toDelContractTemplate(record);
          }}
          // onCancel={cancel}
        >
          <a>删除</a>
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

  useEffect(() => {
    let params = {};
    // 查询等级
    getGradeList(params).then((res) => {
      setGradeList(res?.data);
    });
  }, []);

  /**
   * 以下内容为操作相关
   */

  // 新增模板
  const increaseTemplate = () => {
    setIsTemplateModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmTemplate = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        addCondition(response).then((res) => {
          console.log('add', res);
          if (res.code === 0 ) {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsTemplateModal(false);
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
  const onCancelTemplate = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsTemplateModal(false);
  };

  /**
   * 删除模板
   */
  const toDelContractTemplate = async (record) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteCondition({ serialNo });
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
    let viewRes = await getConditionDetail({ serialNo });
    setIsViewTemplateModal(true);
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
        if (res?.code == 0) {
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
        if (res?.code == 0) {
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
        if (res?.code == 0) {
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
        if (res?.code == 0) {
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
        if (res?.code == 0) {
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
      // let ccys = [
      //   {
      //     typeNo: 'bc',
      //     name: '曲线积分',
      //   },
      //   {
      //     typeNo: 'se',
      //     name: '平台积分',
      //   },
      // ];
      // setTypeNoList(ccys);
      let params = {
        // 1、
        assetsTypes: [],
      };
      getBondingCurveTokenList(params).then((res) => {
        console.log(res);
        let typeNoListTemp = [];
        if (res?.code == 0) {
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
        if (res?.code == 0) {
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
        headerTitle={<TableTitle title="条件列表" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getConditionPageList({
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
              increaseTemplate();
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            添加条件
          </Button>,
        ]}
      />
      {/* 新增合约模板模态框 */}
      <Modal
        title="添加条件"
        centered
        open={isTemplateModal}
        onOk={confirmTemplate}
        onCancel={onCancelTemplate}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{ type: '0', typeNo: '0', typeProtocol: 'ERC20' }}
        >
          <Form.Item
            label="条件名称"
            name="name"
            rules={[{ required: true, message: '请输入条件名称!' }]}
          >
            <Input />
          </Form.Item>
          {/* 1：数字资产 2：账户 3满减, 4:折扣 5权限 */}
          <Form.Item
            label="条件类型"
            name="type"
            rules={[{ required: true, message: '请选择条件类型!' }]}
          >
            <Select
              style={{ width: '100%' }}
              onChange={(value) => typeOnChange(value)}
            >
              <Option value="0">请选择条件类型</Option>
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
          {/*  
          <Form.Item
            label="条件类型协议"
            name="typeProtocol"
            rules={[{ required: true, message: '请选择条件类型协议!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="">请选择条件类型</Option>
              <Option value="ERC20">ERC20</Option>
              <Option value="ERC721">ERC721</Option>
              <Option value="ERC1155">ERC1155</Option>
              <Option value="ERC3525">ERC3525</Option>
            </Select>
          </Form.Item> */}
          <Form.Item
            label="条件"
            name="typeNo"
            rules={[{ required: true, message: '请选择条件!' }]}
          >
            <Select style={{ width: '100%' }}>
              <Option value="0">请选择条件</Option>
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
          {digitalAssetsType == '9' ? null : (
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
        title="查看条件"
        width={800}
        centered
        visible={isViewTemplateModal}
        onOk={() => setIsViewTemplateModal(false)}
        onCancel={() => setIsViewTemplateModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="条件信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="商户ID">
            {isViewRecord?.merchantId}
          </Descriptions.Item>
          <Descriptions.Item label="条件编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="条件类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="条件名称">
            {isViewRecord?.name}
          </Descriptions.Item>
          <Descriptions.Item label="条件类型编号">
            {isViewRecord?.typeNo}
          </Descriptions.Item>
          <Descriptions.Item label="条件资产类型协议">
            {isViewRecord?.typeProtocol}
          </Descriptions.Item>
          <Descriptions.Item label="条件资产tokenId">
            {isViewRecord?.typeTokenId}
          </Descriptions.Item>
          <Descriptions.Item label="会员等级">
            {isViewRecord?.grade}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="条件备注描述">
            {isViewRecord?.remark}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
