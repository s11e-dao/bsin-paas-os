import React, { FC, useEffect, useState, useMemo } from 'react';
import { history, useLocation } from 'umi';
import {
  PieChartOutlined,
  UserOutlined,
  AreaChartOutlined,
  NodeExpandOutlined,
  DotChartOutlined,
  UserSwitchOutlined,
  SubnodeOutlined,
  ShareAltOutlined,
  ScheduleOutlined,
  PrinterOutlined,
  IdcardOutlined,
  DollarOutlined,
  BgColorsOutlined,
} from '@ant-design/icons';

import './index.less';

const MenuNav = (props: any) => {
  const { appMenus } = props;
  const location = useLocation();

  const handleClick = (path: string) => {
    history.push(path);
  };
  const getMenuIon = (icon: any) => {
    let menuIon = <UserOutlined />;
    // console.log(icon == null);
    if (icon == 'UserOutlined') {
      menuIon = <UserOutlined />;
    } else if (icon == 'AreaChartOutlined') {
      menuIon = <AreaChartOutlined />;
    } else if (icon == 'NodeExpandOutlined') {
      menuIon = <NodeExpandOutlined />;
    } else if (icon == 'DotChartOutlined') {
      menuIon = <DotChartOutlined />;
    } else if (icon == 'UserSwitchOutlined') {
      menuIon = <UserSwitchOutlined />;
    } else if (icon == 'ScheduleOutlined') {
      menuIon = <ScheduleOutlined />;
    } else if (icon == 'ShareAltOutlined') {
      menuIon = <ShareAltOutlined />;
    } else if (icon == 'PrinterOutlined') {
      menuIon = <PrinterOutlined />;
    } else if (icon == 'IdcardOutlined') {
      menuIon = <IdcardOutlined />;
    } else if (icon == 'DollarOutlined') {
      menuIon = <DollarOutlined />;
    } else if (icon == 'BgColorsOutlined') {
      menuIon = <BgColorsOutlined />;
    }
    // else {
    //   menuIon = <span className={` ${icon} iconfont `} style={{ marginRight: '0.5rem' }}></span>;
    // }
    return menuIon;
  };

  return (
    <>
      <div className="submenu-nav">
        <h3 className="appName" onClick={() => handleClick(appMenus?.path)}>
          {appMenus?.menuName}
        </h3>
        <ul>
          {appMenus?.children?.map((item: any) => (
            <li
              key={item.menuName}
              className={
                location.pathname.includes(item.path) ? 'active' : ''
              }
              onClick={() => handleClick(item.path)}
            >
              {getMenuIon(item?.icon)}<span>{item?.menuName}</span>
              <i className="corner-top"></i>
              <i className="corner-bottom"></i>
            </li>
          ))}
        </ul>
      </div>
    </>
  );
};

const SubmenuNav = (props: { appMenus: any }) => {
  const { appMenus } = props;
  const location = useLocation();

  const handleClick = (path: string) => {
    history.push(path);
  };

  const links = useMemo(() => {
    let arr: any[] = [];
    if (appMenus?.children) {
      for (let item of appMenus?.children) {
        if (location.pathname.includes(item.path) && Array.isArray(item.children) && item.children.length > 0) {
          arr = item.children;
          break;
        }
      }
    }

    return arr;
  }, [location.pathname, appMenus]);

  return <>
    {links.length > 0 &&
      <div className='submenu-links'>
        <ul>
          {links.map(item => (
            <li key={item?.menuId} className={location.pathname.includes(item.path) ? 'active' : ''}
              onClick={() => handleClick(item.path)}>{item.menuName}</li>
          ))}
        </ul>
      </div>
    }
  </>
}

interface SubmenuProps {
  appMenus: any;
}

const MenuComp: FC<SubmenuProps> = ({ appMenus }) => {
  return <div className="submenu">
    <MenuNav appMenus={appMenus} />
    <SubmenuNav appMenus={appMenus} />
  </div>
}

export default MenuComp;
