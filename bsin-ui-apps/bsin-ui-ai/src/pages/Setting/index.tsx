import React, { useState, useRef, useEffect, useLayoutEffect } from 'react'
import { GridContent } from '@ant-design/pro-layout'
import { Menu } from 'antd'
import BaseView from './components/base'
import EquityView from './components/equity'
import ServiceSubscribe from './components/serviceSubscribe'
import NotificationView from './components/notification'
import SecurityView from './components/security'
import styles from './style.less'
import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '@/utils/localStorageInfo'

const { Item } = Menu

type SettingsStateKeys = 'base' | 'security' | 'serviceSubscribe'
// | 'equity'
// | 'notification'
type SettingsState = {
  mode: 'inline' | 'horizontal'
  selectKey: SettingsStateKeys
}

const Settings: React.FC = () => {
  const menuMap: Record<string, React.ReactNode> = {
    base: '基本设置',
    security: '安全设置',
    serviceSubscribe: '服务订阅',
    // equity: '账号权益',
    // notification: '新消息通知',
  }

  const [initConfig, setInitConfig] = useState<SettingsState>({
    mode: 'inline',
    selectKey: 'base',
  })
  const dom = useRef<HTMLDivElement>()

  const resize = () => {
    requestAnimationFrame(() => {
      if (!dom.current) {
        return
      }
      let mode: 'inline' | 'horizontal' = 'inline'
      const { offsetWidth } = dom.current
      if (dom.current.offsetWidth < 641 && offsetWidth > 400) {
        mode = 'horizontal'
      }
      if (window.innerWidth < 768 && offsetWidth > 400) {
        mode = 'horizontal'
      }
      setInitConfig({ ...initConfig, mode: mode as SettingsState['mode'] })
    })
  }

  useLayoutEffect(() => {
    if (dom.current) {
      window.addEventListener('resize', resize)
      resize()
    }
    return () => {
      window.removeEventListener('resize', resize)
    }
  }, [dom.current])

  const getMenu = () => {
    return Object.keys(menuMap).map((item) => (
      <Item key={item}>{menuMap[item]}</Item>
    ))
  }

  const renderChildren = () => {
    const { selectKey } = initConfig
    var userInformation = getLocalStorageInfo('userInfo')
    console.log(userInformation)
    var merchantInfo = getLocalStorageInfo('merchantInfo')
    console.log(merchantInfo)
    var currentUser = getLocalStorageInfo('customerInfo')
    console.log(currentUser)
    if (currentUser == null) {
      currentUser = userInformation
    }
    switch (selectKey) {
      case 'base':
        return <BaseView customerInfo={currentUser} />
      case 'security':
        return <SecurityView />
      // case 'equity':
      //   return <EquityView />
      case 'serviceSubscribe':
        return <ServiceSubscribe />
      // case 'notification':
      //   return <NotificationView />
      default:
        return null
    }
  }

  return (
    <GridContent>
      <div
        className={styles.main}
        ref={(ref) => {
          if (ref) {
            dom.current = ref
          }
        }}
      >
        <div className={styles.leftMenu}>
          <Menu
            mode={initConfig.mode}
            selectedKeys={[initConfig.selectKey]}
            onClick={({ key }) => {
              setInitConfig({
                ...initConfig,
                selectKey: key as SettingsStateKeys,
              })
            }}
          >
            {getMenu()}
          </Menu>
        </div>
        <div className={styles.right}>
          <div className={styles.title}>{menuMap[initConfig.selectKey]}</div>
          {renderChildren()}
        </div>
      </div>
    </GridContent>
  )
}
export default Settings
