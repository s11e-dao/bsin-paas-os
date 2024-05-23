import { Outlet } from 'umi'
import { ConfigProvider } from 'antd';

export default (props) => {
  return (
    <ConfigProvider prefixCls="upms">
      <Outlet />
    </ConfigProvider>
  )
}