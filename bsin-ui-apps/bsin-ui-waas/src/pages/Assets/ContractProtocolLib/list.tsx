import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  Modal,
  Typography,
  List,
  Button,
  Space,
  Upload,
  Select,
  Card,
  Descriptions,
  Tabs,
} from 'antd';
import type { UploadProps } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import {
  EditOutlined,
  EllipsisOutlined,
  SettingOutlined,
} from '@ant-design/icons';

import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import {
  PlusOutlined,
  LoadingOutlined,
  InboxOutlined,
} from '@ant-design/icons';
import styles from './style.less';

import columnsData, { columnsDataType } from './data';

import {
  getContractProtocolPageList,
  getContractProtocolList,
  addContractProtocol,
  deleteContractProtocol,
  getContractProtocolDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';

const { Paragraph } = Typography;
// 上传组件
const { Dragger } = Upload;
const { Option } = Select;

export default ({ contactProtocolRouter, addCurrentRecord }) => {
  const [contractProtocolList, setContractProtocolList] = useState([]);
  const [loading, setLoading] = useState(true);

  const [contractCategoryList, setContractCategoryList] = useState([]);

  useEffect(() => {
    // 查询协议
    let params = {
      current: '1',
      pageSize: '99',
    };
    getContractProtocolList(params).then((res) => {
      if (res?.code == 0) {
        setContractProtocolList(res?.data);
        setLoading(false);

        let coreContractListTmp: any[] | ((prevState: never[]) => never[]) = [];
        let factoryContractListTmp:
          | any[]
          | ((prevState: never[]) => never[]) = [];
        let extensionContractListTmp:
          | any[]
          | ((prevState: never[]) => never[]) = [];
        let wrapperContractListTmp:
          | any[]
          | ((prevState: never[]) => never[]) = [];
        let proxyContractListTmp:
          | any[]
          | ((prevState: never[]) => never[]) = [];
        let otherContractListTmp:
          | any[]
          | ((prevState: never[]) => never[]) = [];

        res?.data?.map((contract) => {
          if (contract.category == '1') {
            coreContractListTmp.push(contract);
          } else if (contract.category == '2') {
            factoryContractListTmp.push(contract);
          } else if (contract.category == '3') {
            extensionContractListTmp.push(contract);
          } else if (contract.category == '4') {
            wrapperContractListTmp.push(contract);
          } else if (contract.category == '5') {
            proxyContractListTmp.push(contract);
          } else {
            otherContractListTmp.push(contract);
          }
        });
        const contractCategoryListTmp = [
          { id: 1, name: '核心合约', contractList: coreContractListTmp },
          { id: 2, name: '工厂合约', contractList: factoryContractListTmp },
          { id: 3, name: '插件合约', contractList: extensionContractListTmp },
          { id: 4, name: '装饰器合约', contractList: wrapperContractListTmp },
          { id: 5, name: '代理合约', contractList: proxyContractListTmp },
          { id: 6, name: '其他合约', contractList: otherContractListTmp },
        ];
        setContractCategoryList(contractCategoryListTmp);
      }
    });
  }, []);

  /**
   * 查看详情
   */
  const toViewContractProtocolDetail = async (record) => {
    console.log('toViewContractProtocolDetail');
    contactProtocolRouter(record, 'detail');
  };
  /**
   * 部署发行
   */
  const toDeployContractProtocol = async (record) => {
    console.log('toDeployContractProtocol');
    contactProtocolRouter(record, 'deploy');
  };
  /**
   * 帮助文档
   */
  const toViewContractProtocolDoc = async (record) => {
    console.log('toViewContractProtocolDoc');
    contactProtocolRouter(record, 'doc');
  };

  return (
    <Card title="我的合约协议库" bordered={false}>
      <Tabs
        defaultActiveKey="1"
        centered
        items={contractCategoryList?.map((category) => {
          return {
            label: category.name,
            key: category.id,
            children: <Space
              size="middle"
              direction={'vertical'}
              style={{ display: 'flex', flexWrap: 'wrap' }}
            >
              <List
                loading={loading}
                rowKey="id"
                grid={{ gutter: 16, column: 4 }}
                dataSource={category.contractList}
                renderItem={(item) => {
                  if (item && item.serialNo) {
                    return (
                      <List.Item key={item.serialNo}>
                        <Card
                          hoverable
                          className={styles.card}
                          cover={
                            <img
                              style={{ width: '100%', height: '180px' }}
                              alt="example"
                              src={item.coverImage}
                            />
                          }
                          actions={[
                            // <a
                            //   key="deploy"
                            //   onClick={() => {
                            //     toDeployContractProtocol(item);
                            //   }}
                            // >
                            //   发行
                            // </a>,

                            // <a
                            //   key="detail"
                            //   onClick={() => {
                            //     toViewContractProtocolDetail(item);
                            //   }}
                            // >
                            //   详情
                            // </a>,
                            // <a
                            //   key="help"
                            //   onClick={() => {
                            //     toViewContractProtocolDoc(item);
                            //   }}
                            // >
                            //   说明
                            // </a>,
                            <EditOutlined
                              key="setting"
                              onClick={() => {
                                toDeployContractProtocol(item);
                              }}
                            />,
                            <SettingOutlined
                              key="setting"
                              onClick={() => {
                                toViewContractProtocolDetail(item);
                              }}
                            />,
                            <EllipsisOutlined
                              key="setting"
                              onClick={() => {
                                toViewContractProtocolDoc(item);
                              }}
                            />,
                          ]}
                        >
                          <Card.Meta
                            avatar={
                              <img
                                alt=""
                                className={styles.cardAvatar}
                                src="https://s11edao.oss-cn-beijing.aliyuncs.com/jiujiu/1737841274272223232/1737841352147968001/s11eDao-logo.png"
                              />
                            }
                            title={<a>{item.protocolName}</a>}
                            description={
                              <Paragraph
                                className={styles.item}
                                ellipsis={{ rows: 3 }}
                              >
                                {item.description}
                              </Paragraph>
                            }
                          />
                        </Card>
                      </List.Item>
                    );
                  }
                }}
              />
            </Space>,
          };
        })}
      />
    </Card>
  );
};
