import React, { useState } from 'react';
import MerchantAuditList from './list';
import MerchantAuditDetail from './detail';

interface EnterpriseRecord {
  serialNo: string;
  customerNo: string;
  status: string;
  authenticationStatus: string;
  enterpriseName?: string;
  businessNo?: string;
  phone?: string;
  netAddress?: string;
  enterpriseAddress?: string;
  legalPersonName?: string;
  legalPersonCredType?: string;
  legalPersonCredNo?: string;
  businessScope?: string;
  businessLicenceImg?: string;
  [key: string]: any;
}

export default () => {
  // 控制是否展示详情组件
  const [isLoadMerchantAuditDetail, setIsLoadMerchantAuditDetail] = useState(false);

  const [currentRecord, setCurrentRecord] = useState<EnterpriseRecord | null>(null);

  const addCurrentRecord = (record: EnterpriseRecord) => {
    setIsLoadMerchantAuditDetail(true);
    setCurrentRecord(record);
  }

  return (
    <div>
      {/* MerchantAuditDetail组件 商户审核详情组件*/}
      {isLoadMerchantAuditDetail ? (
        <MerchantAuditDetail
          currentRecord={currentRecord!}
          setIsLoadMerchantAuditDetail={setIsLoadMerchantAuditDetail}
        />
      ) : (
        <MerchantAuditList
          addCurrentRecord={addCurrentRecord}
        />
      )}
    </div>
  );
};
