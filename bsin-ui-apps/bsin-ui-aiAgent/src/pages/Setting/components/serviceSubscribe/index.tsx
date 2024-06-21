import React, { useState } from 'react'
import ServiceSubscribe from './serviceSubscribe'
import FunctionSubscribe from './functionSubscribe'
import SubscribeList from './subscribeList'
import { Card } from '@chatui/core'

export default () => {
  // 控制是否展示详情组件S
  const [serviceAppRecord, setServiceAppRecord] = useState(null)
  const [currentContent, setCurrentContent] = useState('subscribeList')

  const subscribeFunction = (record) => {
    setServiceAppRecord(record)
    setCurrentContent('functionSubscribe')
  }

  const Conent = () => {
    let conentComp = (
      <Card>
        <SubscribeList
          setCurrentContent={setCurrentContent}
          subscribeFunction={subscribeFunction}
        />
      </Card>
    )
    if (currentContent == 'serviceSubscribe') {
      conentComp = (
        <Card>
          <ServiceSubscribe setCurrentContent={setCurrentContent} />
        </Card>
      )
    } else if (currentContent == 'functionSubscribe') {
      conentComp = (
        <FunctionSubscribe
          serviceAppRecord={serviceAppRecord}
          setCurrentContent={setCurrentContent}
        />
      )
    }

    return <>{conentComp}</>
  }

  return (
    <div>
      {/* CastingDetail组件 Casting铸造组件*/}
      <Conent />
    </div>
  )
}
