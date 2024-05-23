import React, { useState, useRef, useLayoutEffect } from 'react'
import { GridContent } from '@ant-design/pro-layout'
import { Menu, Button, Descriptions } from 'antd'
import BaseView from './components/base'
import ModelSetting from './components/modelSetting'
import styles from './style.less'

const { Item } = Menu

type SettingsStateKeys = 'base' | 'modelSetting'
type SettingsState = {
  mode: 'inline' | 'horizontal'
  selectKey: SettingsStateKeys
}

export default ({
  setIsLoadPromptTemplateDetail,
  currentRecord,
  promptTemplateList,
}) => {
  const menuMap: Record<string, React.ReactNode> = {
    base: '基本信息',
    modelSetting: '模版设置',
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
    switch (selectKey) {
      case 'base':
        return <BaseView promptTemplateInfo={currentRecord} />
      case 'modelSetting':
        return <ModelSetting promptTemplateInfo={currentRecord} />
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
            style={{ width: 140 }}
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
          <div>
            <Button
              onClick={() => {
                setIsLoadPromptTemplateDetail(false)
              }}
              className={styles.btn}
            >
              返回
            </Button>
          </div>
        </div>
        <div className={styles.right}>{renderChildren()}</div>
      </div>
    </GridContent>
  )
}
