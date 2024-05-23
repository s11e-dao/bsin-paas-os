import React, { FC } from 'react';
import {
  Row,
  Col,
  Card,
  Avatar,
  Pagination,
  Calendar,
  List,
  message,
} from 'antd';
import { Link } from 'umi';
import { connect, Dispatch, Loading, AppsState, history } from 'umi';
import BsinLayoutContext from '@/layouts/stores/BsinLayoutContext';
import {
  setLocalStorageInfo,
  setSessionStorageInfo,
} from '@/utils/localStorageInfo';
import {
  ScheduleTwoTone,
  AppstoreTwoTone,
  ProfileTwoTone,
} from '@ant-design/icons';
import moment from 'moment';
import styles from './index.less';
import logo2 from '@/assets/logo3.png';
import 'moment/locale/zh-cn';
moment.locale('zh-cn');

interface PageProps {
  apps: AppsState;
  dispatch: Dispatch;
  loading: boolean;
}

const WorkPlaceBsinTheme: FC<PageProps> = ({ apps, dispatch }) => {
  // 引入公共数据
  const counter = BsinLayoutContext.useContainer();
  // 列表数据
  const data = [
    'bsin-paas新增智能决策引擎模块',
    'bsin-paas新增通用人工智能模块，集成chatGPT',
    'bsin-paas是企业数字化的技术中台解决方案.',
    's11eDao开启大社区、小平台战略',
  ];

  const [current, setCurrent] = React.useState(1);

  React.useEffect(() => {
    setTimeout(() => {
      if (window.localStorage.getItem('bsin-microAppMount') === '3') {
        message.warning('子应用加载失败，请检查子应用是否部署');
        setTimeout(() => {
          window.localStorage.setItem('bsin-microAppMount', '2');
        }, 500);
      }
    }, 500);
  }, []);

  // const dataJson = {};
  // add({...dataJson});

  /**
   * 点击跳转到子应用
   * @param app
   */
  const getMenu = async (app: any) => {
    setLocalStorageInfo('bsin-microAppMount', '0');
    // debugger
    history.push('/' + app.appCode);
  };

  /**
   * 页码、每页展示条数变动时调用
   * @param page 当前页
   * @param pageSize 每页展示应用个数
   */
  const pagination = (current: number, pageSize: number) => {
    setCurrent(current);
    dispatch({
      type: 'apps/getApps',
      payload: {
        current,
        pageSize,
      },
    });
  };
  const style = { padding: '16px 0', height: '100%' };

  return (
    <div className="work-place" style={{ marginTop: 2 }}>
      WorkPlaceBsinTheme
    </div>
  );
};

const mapStateToProps = ({
  apps,
  loading,
}: {
  apps: AppsState;
  loading: Loading;
}) => {
  return {
    apps,
    userListLoading: loading.models.users,
  };
};

export default connect(mapStateToProps)(WorkPlaceBsinTheme);
