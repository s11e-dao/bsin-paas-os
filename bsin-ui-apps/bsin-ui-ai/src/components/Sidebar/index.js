import React from 'react';
import { Collapse } from 'antd';

const { Panel } = Collapse;

import './sideBarStyle.css';

export default () => {
    const onDragStart = (event, nodeType) => {
        event.dataTransfer.setData('application/reactflow', nodeType);
        event.dataTransfer.effectAllowed = 'move';
    };

    const onChange = (key) => {
        console.log(key);
    };

    return (

        <Collapse defaultActiveKey={['1']} onChange={onChange} style={{ width: 180 }}>
            <div className="description">AI工作流组件</div>
            <Panel header="输入类组件" key="1">
                <aside>
                    <div className="dndnode input" onDragStart={(event) => onDragStart(event, 'custom')} draggable>
                        prompt
                    </div>
                    {/* 添加自定义组件 */}
                    <div className="dndnode" onDragStart={(event) => onDragStart(event, 'roleDefine')} draggable>
                        角色
                    </div>
                    <div className="dndnode" onDragStart={(event) => onDragStart(event, 'output')} draggable>
                        模拟点击事件
                    </div>
                    <div className="dndnode" onDragStart={(event) => onDragStart(event, 'output')} draggable>
                        进入网页
                    </div>
                    <div className="dndnode" onDragStart={(event) => onDragStart(event, 'output')} draggable>
                        内容读取
                    </div>
                </aside>
            </Panel>
            <Panel header="输出类组件" key="2">
                <aside>
                    <div className="dndnode input" onDragStart={(event) => onDragStart(event, 'input')} draggable>
                        json数据
                    </div>
                    <div className="dndnode" onDragStart={(event) => onDragStart(event, 'default')} draggable>
                        notion
                    </div>
                    <div className="dndnode output" onDragStart={(event) => onDragStart(event, 'output')} draggable>
                        openAPI
                    </div>
                </aside>
            </Panel>
            <Panel header="AI模型" key="3">
                <aside>
                    <div className="dndnode input" onDragStart={(event) => onDragStart(event, 'input')} draggable>
                        openAi
                    </div>
                    <div className="dndnode" onDragStart={(event) => onDragStart(event, 'default')} draggable>
                        claude
                    </div>
                    <div className="dndnode" onDragStart={(event) => onDragStart(event, 'output')} draggable>
                        midjourney
                    </div>
                    <div className="dndnode" onDragStart={(event) => onDragStart(event, 'output')} draggable>
                        Stable Diffusion
                    </div>
                </aside>
            </Panel>
            <Panel header="子流程组件" key="4">
                <aside>
                    <div className="dndnode input" onDragStart={(event) => onDragStart(event, 'input')} draggable>
                        子流程
                    </div>
                </aside>
            </Panel>
        </Collapse>


    );
};
