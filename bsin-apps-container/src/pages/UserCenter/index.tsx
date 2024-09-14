import React, { useState } from 'react';

export default function HomePage() {
  const [c, setC] = useState(0);
  return (
    <div>
      <h2>根据当前登录用户的业务角色，区分平台个人中心或是商户各种中心</h2>
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
      <h3>count:{c}</h3>
    </div>
  );
}
