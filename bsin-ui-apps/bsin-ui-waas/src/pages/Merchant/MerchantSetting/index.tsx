import React, { useState } from 'react';
import Authentication from '@/components/Authentication/index';
import MerchantInfo from './merchantInfo';

export default () => {
  // 控制是否展示详情组件
  const [isLoadAuthentication, setIsLoadAuthentication] = useState(false);

  return (
    <div>
      {/* CastingDetail组件 Casting铸造组件*/}
      {isLoadAuthentication ? (
        <Authentication />
      ) : (
        <MerchantInfo
        setIsLoadAuthentication={setIsLoadAuthentication}
        />
      )}
    </div>
  );
};
