import { NsJsonSchemaForm, XFlowNodeCommands } from '@antv/xflow';
import { controlMapService, ControlShapeEnum } from './form-controls';
import { MODELS, NsGraph, NsNodeCmd } from '@antv/xflow';
import { set } from 'lodash';

export function delay(ms: number) {
  return new Promise((resolve) => setTimeout(() => resolve(true), ms));
}

let i = 0;
export const formSchemaService: NsJsonSchemaForm.IFormSchemaService = async (
  args,
) => {
  const { ControlShape } = NsJsonSchemaForm;

  const { targetData, modelService, targetType } = args;
  /** 可以使用获取 graphMeta */
  const graphMeta = await MODELS.GRAPH_META.useValue(modelService);
  console.log('formSchemaService', graphMeta, args, targetData, targetType); //点击画布更新---
  // TODO 画布初始化展示判断
  // targetData 判断接口是否返回数据，如果返回用后端，没返回则 空
  if (!targetData) {
    return {
      tabs: [
        {
          /** Tab的title */
          name: '画布配置',
          groups: [
            {
              name: 'groupName',
              controls: [
                // {
                //   label: '图数据',
                //   name: 'Tab1',
                //   /** 使用自定义shape */
                //   shape: ControlShapeEnum.EDITOR,
                //   disabled: false,
                //   required: true,
                //   tooltip: 'JSON 数据',
                //   placeholder: 'please write something',
                //   value: '',
                //   defaultValue: '', // 可以认为是默认值
                //   hidden: false,
                //   options: [{ title: '', value: '' }],
                //   originData: {}, // 原始数据
                // },
                // {
                //   name: 'name1',
                //   label: 'label1',
                //   shape: ControlShapeEnum.EDITOR,
                //   value: '2222'
                // },
              ],
            },
          ],
        },
      ],
    };
  }

  return {
    /** 配置一个Tab */
    tabs: [
      {
        /** Tab的title */
        name: '节点配置',
        groups: [
          {
            name: 'group1',
            controls: [
              {
                name: 'label',
                label: '节点Label',
                shape: ControlShape.INPUT,
                value: targetData.label,
              },
              {
                name: 'x',
                label: 'x',
                shape: ControlShape.FLOAT,
                value: targetData.x,
              },
              {
                name: 'y',
                label: 'y',
                shape: ControlShape.FLOAT,
                value: targetData.y,
              },
              {
                label: '编辑图数据json',
                name: 'editJson',
                /** 使用自定义shape */
                shape: ControlShape.TEXTAREA,
                disabled: false,
                required: true,
                tooltip: 'hello world',
                placeholder: 'please write something',
                value: JSON.stringify(targetData),
                defaultValue: JSON.stringify(targetData), // 可以认为是默认值
                hidden: false,
                options: [{ title: '', value: '' }],
                originData: {}, // 原始数据
              },
            ],
          },
        ],
      },
    ],
  };
};

export const formValueUpdateService: NsJsonSchemaForm.IFormValueUpdateService =
  async (args) => {
    const { values, commandService, targetData } = args;
    const updateNode = (node: NsGraph.INodeConfig) => {
      return commandService.executeCommand<NsNodeCmd.UpdateNode.IArgs>(
        XFlowNodeCommands.UPDATE_NODE.id,
        { nodeConfig: node },
      );
    };
    console.log('formValueUpdateService  values:', values, args); //
    let nodeConfig: NsGraph.INodeConfig = {
      ...targetData,
    };
    values.forEach((val) => {
      set(nodeConfig, val.name, val.value);
    });
    // TODO 有个延迟 右击重命名之后，右侧配置表的信息（label）必须点击一下画布再点击节点，才能看到节点名称更新，
    // 通过配置表 json 修改节点信息之后 同上

    values.forEach((val) => {
      if (val.name[0] === 'editJson') {
        nodeConfig = JSON.parse(nodeConfig?.editJson);
      }
    });

    updateNode(nodeConfig);
  };

export { controlMapService };
