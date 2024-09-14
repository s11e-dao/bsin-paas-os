import React, { useEffect, useState } from 'react';
import { Button, Space, Card } from 'antd';
import ElseComp from '../ElseComp';
import SoComp from '../SoComp';
import IfComp from '../IfComp';

import './index.less';

const CanvasComp = (props: any) => {
  const { canvasData } = props;
  // 下拉框选择
  const [selectedValue, setSelectedValue] = useState('and');
  // 如果 展示 list
  const [ifList, setIfList] = useState([]);
  // 那么 列表
  const [soList, setSoList] = useState([]);
  // 否则 列表
  const [elseList, setElseList] = useState([]);

  // 新增节点弹窗
  // 编辑弹窗
  const handleChange = (value: string = 'and') => {
    console.log('handleChange--value', value);
    setSelectedValue(value);
    // 如果节点 的展示
    const nextList = canvasData.ifList.filter((x) => x.selectd === value);

    setIfList(nextList);
  };

  // 获取container对象
  useEffect(() => {
    setSoList(canvasData?.soList);
    setElseList(canvasData?.elseList);
    // @ts-ignore
    handleChange();
  }, []);

  // 保存
  const handleSave = () => {
    // 下拉选择
    // ifList/soList/elseList
    const data = {
      selectedValue,
      ifList,
      soList,
      elseList,
    };
    console.log('saveData---', data);
  };

  return (
    <div>
      <Space size="middle" style={{ display: 'flex', marginBottom: '16px' }}>
        <Button
          onClick={() => {
            props?.closeHandle();
          }}
        >
          返回列表
        </Button>
        <Button
          type="primary"
          onClick={() => {
            handleSave();
          }}
        >
          保存
        </Button>
      </Space>

      <div className="rules-design">
        <div className="rules-design-name">{canvasData?.title}</div>

        <Space>
          <div className="rules-design-title-line">
            <div className="rules-design-indication-wrapper">
              <div className="rules-design-indication-one">
                <div className="rules-design-square-one" />
                <span>指标</span>
              </div>
              <div className="rules-design-indication-one">
                <div className="rules-design-square-two" />
                <span>条件</span>
              </div>
              <div className="rules-design-indication-one">
                <div className="rules-design-square-three" />
                <span>动作</span>
              </div>
            </div>
          </div>
        </Space>
      </div>
      <Card>
        <IfComp
          ifList={ifList}
          setIfList={setIfList}
          handleChange={handleChange}
        />
        <SoComp soList={soList} setSoList={setSoList} />
        <ElseComp elseList={elseList} setElseList={setElseList} />
      </Card>
    </div>
  );
};

export default CanvasComp;
