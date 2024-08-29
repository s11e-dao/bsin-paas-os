import { Tabs, Card, Button, Popconfirm } from 'antd';
import React from 'react';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';

import LindeChart from './lindeChart';

import TableTitle from '../../../components/TableTitle';

import columnsData, { columnsDataType } from './data';

type ExcRateChangeList = {
  supply: number | string;
  excRate: number | string;
};

type AllData = {
  ccyPair: string;
  curExcRate: number;
  excRateChangeList: ExcRateChangeList[];
};

export default () => {
  // 表头数据
  const [allData, setAllData] = React.useState<AllData>();

  /**
   * 监听页面路径设置布局
   */
  React.useEffect(() => {
    // getDate(7)
    //getBondingCurveTokenJournal();
  }, []);

  const getBondingCurveTokenJournal = async () => {
    const reqParams = {
      merchantNo: '',
      pageSize: '100',
      current: '1',
      limit: '100',
    };
  };

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            // 调用方法
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
            // 调用方法
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

  // 获取报表数据
  const getDate = async (amount: number) => {
    let res = {
      body: {
        excRateChangeList: [
          {
            supply: 7472785520000000000000,
            excRate: 0.1336922435,
          },
          {
            supply: 167522907060000000000000,
            excRate: 0.138059241,
          },
          {
            supply: 325581590830000000000000,
            excRate: 0.1429301828,
          },
          {
            supply: 481672335450000000000000,
            excRate: 0.1483468416,
          },
          {
            supply: 635821714090000000000000,
            excRate: 0.1543682634,
          },
          {
            supply: 788059644590000000000000,
            excRate: 0.1610512487,
          },
          {
            supply: 938418917840000000000000,
            excRate: 0.1684565665,
          },
          {
            supply: 1486935206350000000000000,
            excRate: 0.2039348265,
          },
          {
            supply: 2028892000640000000000000,
            excRate: 0.2569915906,
          },
          {
            supply: 2564746571040000000000000,
            excRate: 0.3359547999,
          },
          {
            supply: 3095075715900000000000000,
            excRate: 0.453027127,
          },
          {
            supply: 3620521275890000000000000,
            excRate: 0.6259082209,
          },
          {
            supply: 4141735312230000000000000,
            excRate: 0.8803038931,
          },
          {
            supply: 4659337743340000000000000,
            excRate: 1.2532208731,
          },
        ],
        ccyPair: '',
        curExcRate: 1,
      },
    };

    let excRateChangeList = res.body.excRateChangeList.map(
      (item: ExcRateChangeList) => {
        return {
          supply: item.supply,
          excRate: Number(item.excRate),
        };
      },
    );
    let data = { ...res.body, excRateChangeList };
    setAllData(data);
  };

  return (
    <div>
      <Card>
        <Tabs defaultActiveKey="1">
          <Tabs.TabPane tab="店铺基本信息" key="1">
            店铺账本设置
          </Tabs.TabPane>
          <Tabs.TabPane tab="店铺社群" key="2">
            设置
          </Tabs.TabPane>
        </Tabs>
      </Card>
    </div>
  );
};
