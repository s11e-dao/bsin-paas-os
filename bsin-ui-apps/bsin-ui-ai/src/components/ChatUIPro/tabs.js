import React, { useState } from 'react'
import '@chatui/core/es/styles/index.less'
// 引入组件
import {
  Tabs,
  Tab,
  Card,
  CardMedia,
  CardTitle,
  CardText,
  CardActions,
  Button,
} from '@chatui/core'
// 引入样式
import '@chatui/core/dist/index.css'

export default function ({ data, ctx, meta }) {
  console.log(data, 'data', ctx, meta) //传递给组件的信息
  return (
    <div>
      <h3>基础用法</h3>
      <Card>
        <Tabs>
          <Tab label="标签1">
            <p>内容1</p>
          </Tab>
          <Tab label="标签2">
            <p>内容2</p>
          </Tab>
          <Tab label="标签3">
            <p>内容3</p>
          </Tab>
        </Tabs>
      </Card>
      <Card size="xl">
        <CardMedia image="//gw.alicdn.com/tfs/TB1Xv5_vlr0gK0jSZFnXXbRRXXa-427-240.png" />
        <CardTitle>Card title</CardTitle>
        <CardText>Card content</CardText>
        <CardActions>
          <Button>Default button</Button>
          <Button color="primary">Primary button</Button>
        </CardActions>
      </Card>
    </div>
  )
}
