import {
  Form,
  Tabs,
  Card,
  Button,
  Modal,
  message,
  Popconfirm,
  Descriptions,
  Input,
  Select,
} from 'antd';
import React, { useState } from 'react';
import type { ProColumns, ActionType } from '@ant-design/pro-table';

import SettingBondingCurve from './settingBondingCurve';
import BondingCurveMintRecord from './bondingCurveMintRecord';
import BondingCurveSimulate from './bondingCurveSimulate';

import columnsData, { columnsDataType } from './data';

import {
  getBondingCurveTokenJournalList,
  getTransactionDetail,
} from './service';
import SigmoidSimulate from './sigmoidSimulate';
import SegmentedCurveSimulate from './segmentedCurveSimulate';

type ExcRateChangeList = {
  supply: number | string;
  excRate: number | string;
  price?: number | string;
};

type AllData = {
  ccyPair: string;
  curExcRate: number;
  excRateChangeList: ExcRateChangeList[];
};

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;

  // 表头数据
  const [allData, setAllData] = React.useState<AllData>();
  // 添加activeTab状态来控制当前激活的Tab
  const [activeTab, setActiveTab] = React.useState<string>('1');
  // 添加刷新触发器，用于通知子组件刷新数据
  const [refreshTrigger, setRefreshTrigger] = React.useState<number>(0);

  /**
   * 监听页面路径设置布局
   */
  React.useEffect(() => {
    // getDate(7)
    getBondingCurveTokenJournal();
  }, []);

  const getBondingCurveTokenJournal = async () => {
    const reqParams = {
      merchantNo: '',
      pageSize: '100',
      current: '1',
      limit: '1000',
    };
    const res = await getBondingCurveTokenJournalList(reqParams);
    console.log(res);
    let excRateChangeList = res.data?.map((item: ExcRateChangeList) => {
      return {
        supply: item.supply,
        excRate: Number(item.price),
      };
    });
    let data = { ...res.data, excRateChangeList };
    setAllData(data);
  };

  // 控制新增模态框
  const [isTemplateModal, setIsTemplateModal] = useState(false);
  // 查看模态框
  const [isViewTemplateModal, setIsViewTemplateModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState({});
  // 获取表单
  const [FormRef] = Form.useForm();
  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            // 调用方法
            toViewTransaction(record);
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

  /**
   * 查看详情
   */
  const toViewTransaction = async (record: any) => {
    let { serialNo } = record;
    let viewRes = await getTransactionDetail({ serialNo });
    setIsViewTemplateModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * Tab切换处理函数
   */
  const handleTabChange = (key: string) => {
    setActiveTab(key);
    // 触发子组件刷新数据
    setRefreshTrigger(prev => prev + 1);
  };

  return (
    <div>
      <Card>
        <Tabs 
          defaultActiveKey="1" 
          activeKey={activeTab}
          onChange={handleTabChange}
        >
          <Tabs.TabPane tab="商户劳动价值捕获联合曲线" key="1">
            <SettingBondingCurve refreshTrigger={activeTab === '1' ? refreshTrigger : 0} />
          </Tabs.TabPane>
          <Tabs.TabPane tab="商户积分铸造释放曲线" key="2">
            <BondingCurveMintRecord refreshTrigger={activeTab === '2' ? refreshTrigger : 0} />
          </Tabs.TabPane>
          <Tabs.TabPane tab="Sigmoid联合曲线仿真系统" key="3">
            <SigmoidSimulate refreshTrigger={activeTab === '3' ? refreshTrigger : 0} />
          </Tabs.TabPane> 
          <Tabs.TabPane tab="分段联合曲线仿真系统" key="4">
            <SegmentedCurveSimulate refreshTrigger={activeTab === '4' ? refreshTrigger : 0} />
          </Tabs.TabPane>
          <Tabs.TabPane tab="劳动价值释放曲线仿真" key="5">
            <BondingCurveSimulate refreshTrigger={activeTab === '5' ? refreshTrigger : 0} />
          </Tabs.TabPane>
        </Tabs>
      </Card>{' '}
    </div>
  );
};
