import React, { useState } from 'react';

export default function HomePage() {
  const [c, setC] = useState(0);
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
        <p>{1}</p>
      <h3>count:{c}</h3>
    </div>
  );
}
