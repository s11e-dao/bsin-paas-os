import React, { useState, useRef, useLayoutEffect } from 'react'
import { GridContent } from '@ant-design/pro-layout'
import { Menu, Button, Descriptions } from 'antd'
import BaseView from './components/base'
import ModelSetting from './components/modelSetting'
import KnowledgeBaseFile from './components/knowledgeBaseFile'
import DataRecallTest from './components/dataRecallTest'

import styles from './style.less'

const { Item } = Menu

type SettingsStateKeys =
  | 'base'
  | 'modelSetting'
  | 'knowledgeBaseFile'
  | 'dataRecallTest'
type SettingsState = {
  mode: 'inline' | 'horizontal'
  selectKey: SettingsStateKeys
}

export default ({
  setIsLoadKnowledgeBaseDetail,
  currentRecord,
  knowledgeBaseList,
  chatUIProps,
}) => {
  const menuMap: Record<string, React.ReactNode> = {
    knowledgeBaseFile: '知识库文件',
    dataRecallTest: '召回测试',
    base: '基本设置',
    modelSetting: '模型设置',
  }

  const [initConfig, setInitConfig] = useState<SettingsState>({
    mode: 'inline',
    selectKey: 'knowledgeBaseFile',
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
      setInitConfig((prevConfig) => ({ ...prevConfig, mode: mode as SettingsState['mode'] }))
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
        return <BaseView record={currentRecord} />
      case 'modelSetting':
        return <ModelSetting record={currentRecord} />
      case 'knowledgeBaseFile':
        return <KnowledgeBaseFile chatUIProps={chatUIProps} />
      case 'dataRecallTest':
        return <DataRecallTest chatUIProps={chatUIProps} />
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
            style={{ width: 140, borderInlineEnd: "none", paddingLeft: '10px' }}
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
                setIsLoadKnowledgeBaseDetail(false)
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
