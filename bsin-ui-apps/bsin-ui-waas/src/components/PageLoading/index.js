import React, { useEffect } from 'react'
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
import { Spin } from 'antd';

// 页面加载时效果
function PageLoading() {
  useEffect(() => {
    const progress = NProgress.start();
    return () => {
      progress.done();
    }
  }, [])
  return(<div style={{ paddingTop: 100, textAlign: 'center' }}>
      <Spin size="large" spinning='false' />
    </div>)
 }

export default PageLoading