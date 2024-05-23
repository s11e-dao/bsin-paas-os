import React from 'react';
import { useModel } from 'umi';
import { FormattedMessage } from 'umi';

export default function HomePage() {
  const masterProps = useModel('@@qiankunStateFromMaster');
  return <div>
    <FormattedMessage id="welcome" />
    <p>Slave Home Page</p>
    {JSON.stringify(masterProps)}
  </div>;
}
