import React, { useState } from 'react';
import { useModel } from 'umi';

export default function HomePage() {
  const [c, setC] = useState(0);
  const { appId } = useModel('@@qiankunStateFromMaster');
  return (
    <div>
      <h2>slave Count</h2>
      <div>
        <button
          onClick={() => {
            setC((c) => c + 1);
          }}
        >
          +
        </button>
      </div>
        <p>用户中心</p>
        <p>{appId}</p>
      <h3>count:{c}</h3>
    </div>
  );
}
