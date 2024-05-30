import React, { useState } from 'react';
import CustomerList from './list';
import CustomerProfile from './profile';

export default () => {
  // 控制是否展示详情组件
  const [isLoadCustomerProfile, setIsLoadCustomerProfile] = useState(false);

  const [currentRecord, setCurrentRecord] = useState(false);

  const addCurrentRecord = (record) => {
    setIsLoadCustomerProfile(true);
    setCurrentRecord(record);
  }

  return (
    <div>
      {/* CastingDetail组件 Casting铸造组件*/}
      {isLoadCustomerProfile ? (
        <CustomerProfile
        currentRecord={currentRecord}
        setIsLoadCustomerProfile={setIsLoadCustomerProfile}
        />
      ) : (
        <CustomerList
        addCurrentRecord={addCurrentRecord}
        />
      )}
    </div>
  );
};
