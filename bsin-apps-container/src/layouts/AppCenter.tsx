import React, { useEffect, useState } from 'react';
import { css } from '@emotion/css';
import {
  CaretDownFilled
} from '@ant-design/icons';
import {
  Button,
  ConfigProvider,
  Divider,
  Dropdown,
  Input,
  Popover,
  theme,
  Drawer,
  Modal,
  Switch,
  FloatButton
} from 'antd';

import { history } from 'umi'

export default ({ userApps }) => {

  const [hovered, setHovered] = useState(false);

  const [appName, setAppName] = useState("");

  const appClick = (e: any, app: any) => {
    // 初始化应用点击状态
    window.localStorage.setItem('bsin-microAppMountStatus', '1');
    e.stopPropagation();
    console.log(app)
    history.push("/" + app.path)
    setHovered(false);
    setAppName(app.name)
  };

  const handleHoverChange = (open: boolean) => {
    setHovered(open);
  };

  const { token } = theme.useToken();
  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
      }}
    >
      <Divider
        style={{
          height: '1.5em',
        }}
        type="vertical"
      />
      <Popover
        trigger="hover"
        open={hovered}
        onOpenChange={handleHoverChange}
        placement="bottom"
        overlayStyle={{
          width: 'calc(100vw - 24px)',
          padding: '24px',
          paddingTop: 8,
          height: '307px',
          borderRadius: '0 0 6px 6px',
        }}
        content={
          <div style={{ display: 'flex', padding: '32px 40px' }}>

            <div
              style={{
                // borderInlineStart: '1px solid ' + token.colorBorder,
                display: "flex",
                flexWrap: "wrap",
                paddingInlineStart: 16,
              }}
            >
              {userApps?.map((app, index) => {
                return (
                  <div
                    onClick={(e) => {
                      // 根据当前登录的用户类型判断跳转不同的中心：租户个人中心，商户个人中心
                      appClick(e,app)
                    }}
                    key={index}
                    className={css`
                        border-radius: 4px;
                        padding: 16px;
                        margin-top: 4px;
                        display: flex;
                        min-width: 210px;
                        cursor: pointer;
                        &:hover {
                          background-color: ${token.colorBgTextHover};
                        }
                      `}
                  >
                    <img src="https://gw.alipayobjects.com/zos/antfincdn/6FTGmLLmN/bianzu%25252013.svg" />
                    <div
                      style={{
                        marginInlineStart: 14,
                      }}
                    >
                      <div
                        className={css`
                            font-size: 14px;
                            color: ${token.colorText};
                            line-height: 22px;
                          `}
                      >
                        {app.name}
                      </div>
                      <div
                        className={css`
                            font-size: 12px;
                            color: ${token.colorTextSecondary};
                            line-height: 20px;
                          `}
                      >
                        {app.description}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        }
      >
        <div
          style={{
            color: token.colorTextHeading,
            fontWeight: 500,
            cursor: 'pointer',
            display: 'flex',
            gap: 4,
            paddingInlineStart: 8,
            paddingInlineEnd: 12,
            alignItems: 'center',
          }}
          className={css`
              &:hover {
                background-color: ${token.colorBgTextHover};
              }
            `}
        >
          <span style={{ color: '#fff' }}> { appName || "企业应用中心"}</span>
          <CaretDownFilled style={{ color: '#fff' }} />
        </div>
      </Popover>
    </div>
  );
};