import React, { useState, useEffect, ChangeEvent, MouseEvent } from 'react';
import { history } from 'umi';
import { Button, message, Select, Radio } from 'antd';
import { SwapOutlined } from '@ant-design/icons';
import type { RadioChangeEvent } from 'antd';

import logo from '@/assets/logo.png';
import loginBg from '@/assets/image/logo.png';
import loginbg1 from '@/assets/image/login-bg1.png';
import "./index.css"
import MerchantLogin from './MerchantLogin';
import styles from "./style.css"
import { hex_md5 } from '../../utils/md5';
import {
  setLocalStorageInfo,
  setSessionStorageInfo,
  getSessionStorageInfo
} from '@/utils/localStorageInfo';

const { Option } = Select;

import { userLogin, getTenantList, nodeUserLogin, sysAgentLogin } from '../../services/login';

export default function () {

  useEffect(() => {
    const token = getSessionStorageInfo("token");
    if (token) {
      history.push("/home")
    }
    getAllTenant();
  }, []);
  // 获取所有租户
  const getAllTenant = async () => {
    let res = await getTenantList({});
    if (res && res.code === 0) {
      setTenantList(res.data);
    } else {
      message.error('获取租户列表失败');
    }
  };
  // 租户列表
  const [tenantList, setTenantList] = useState<[]>();
  // 选中的租户
  const [tenantId, setTenantId] = useState<string>();

  // 选择租户
  function onChange(value: string) {
    setTenantId(value);
  }

  // 按钮点击动画
  const [loadings, setLoadings] = React.useState(false);

  // 不同角色登录切换切换
  const [pagesWitching, setPagesWitching] = React.useState('node');

  // 委员会登录
  async function rootLogin(event: MouseEvent) {
    event.preventDefault();
    if (!rootLoginState.username) return message.info('请输入用户名');
    if (!rootLoginState.password) return message.info('请输入密码');
    setLoadings(true);
    let res = await userLogin({
      ...rootLoginState,
      password: hex_md5(rootLoginState.password),
      // password: loginState.password,
      tenantId: "6345824413764157440",
    });
    console.log("-------------")
    console.log(res)
    if (res) {
      setLocalStorageInfo('userInfo', res.data?.sysUser);
      setSessionStorageInfo('token', { token: res.data?.token });
      message.success('登录成功！');
    }
    setLoadings(false)
    history.push("/home")
  }
  const [bizRoleType, setBizRoleType] = useState('2');

  const handleBizRoleTypeChange = (e: RadioChangeEvent) => {
    setBizRoleType(e.target.value);
  };

  // 节点登录
  async function nodeLogin(event: MouseEvent) {
    event.preventDefault();
    if (!tenantId) return message.info('请选择节点');
    if (!nodeLoginState.username) return message.info('请输入用户名');
    if (!nodeLoginState.password) return message.info('请输入密码');
    setLoadings(true);
    let res
    if(bizRoleType == "2"){
      res = await nodeUserLogin({
        ...nodeLoginState,
        password: hex_md5(nodeLoginState.password),
        tenantId,
      });
    }else{
      res = await sysAgentLogin({
        ...nodeLoginState,
        password: hex_md5(nodeLoginState.password),
        tenantId,
      });
    }

    if (res?.code === 0) {
      setLocalStorageInfo('userInfo', res.data?.sysUser);
      setSessionStorageInfo('token', { token: res.data?.token });
      message.success('登录成功！');
    }
    setLoadings(false)
    history.push("/home")
  }

  // 切换登录页面
  const [defaultLoginPage, setDefaultLoginPage] = useState(false);
  // 切换登录页面
  const changeLogin = () => {
    setDefaultLoginPage(!defaultLoginPage);
  };

  // 委员会登录表单
  const [rootLoginState, setRootLoginState] = useState({
    username: '',
    password: '',
  });

  // 节点登录表单
  const [nodeLoginState, setNodeLoginState] = useState({
    username: '',
    password: '',
  });

  // 委员会登录表单值变化时调用
  function rootLoginInputChange(event: ChangeEvent<HTMLInputElement>) {
    const value = event.target.value;
    const name = event.target.name;
    setRootLoginState({
      ...rootLoginState,
      [name]: value,
    });
  }

  // 节点登录表单值变化时调用
  function nodeLoginInputChange(event: ChangeEvent<HTMLInputElement>) {
    const value = event.target.value;
    const name = event.target.name;
    setNodeLoginState({
      ...nodeLoginState,
      [name]: value,
    });
  }

  return (
    <>
      <Button
        style={{
          position: 'absolute',
          top: '20px',
          left: '20px',
          zIndex: 999,
          color: '#fff',
        }}
        shape="round"
        type="primary"
        onClick={changeLogin}
        icon={<SwapOutlined />}
      >
        切换登录
      </Button>
      {defaultLoginPage ? (
        <main className={styles.main}>
          <div className={styles.box}>
            <div className={styles.inner_box}>
              <div className={styles.forms_wrap}>
                {pagesWitching === 'node' ? (
                  <form autoComplete="off" className={styles.form}>
                    <div className={styles.logo}>
                      {/* <i className='bx bxl-html5 bx-lg'></i> */}
                      <img src={logo} />
                      <h4 style={{ marginBottom: '0' }}>BsinPaaS一站式开发平台</h4>
                    </div>
                    <div className={styles.actual_form}>
                      <div className={styles.input_wrap_role}>
                        <Radio.Group value={bizRoleType} onChange={handleBizRoleTypeChange}>
                          <Radio.Button value="2">火源节点</Radio.Button>
                          <Radio.Button value="4">代理商</Radio.Button>
                        </Radio.Group>
                      </div>
                      <div className={styles.input_wrap}>
                        <Select
                          bordered={false}
                          style={{
                            background: "none",
                            outline: "none",
                            borderBottom: "1px solid #bbb",
                            border: 'none',
                            height: '38px',
                            margin: '8px 0',
                            width: '100%',
                            textAlign: 'left',
                          }}
                          showSearch
                          placeholder="请选择节点"
                          optionFilterProp="children"
                          onChange={onChange}
                          filterOption={(input, option) =>
                            option?.children
                              ?.toLowerCase()
                              .indexOf(input.toLowerCase()) >= 0
                          }
                        >
                          {tenantList?.map((item: any) => {
                            return (
                              <Option key={item.tenantId} value={item.tenantId}>
                                {item.tenantName}
                              </Option>
                            );
                          })}
                        </Select>
                      </div>
                      <div className={styles.input_wrap}>
                        <input type="text" minLength="4" className={styles.input_field} autoComplete="off" required
                          name="username"
                          placeholder="用户名"
                          onChange={nodeLoginInputChange} />
                      </div>
                      <div className={styles.input_wrap}>
                        <input type="password" className={styles.input_field} autoComplete="off" required
                          name="password"
                          placeholder="密码"
                          onChange={nodeLoginInputChange} />
                      </div>

                      <Button
                        className={styles['sign_btn']}
                        loading={loadings}
                        onClick={nodeLogin}
                      >
                        登录
                      </Button>
                      <p className={styles.text}>
                        s11e network web3品牌 <a className={styles.toggle} onClick={() => setPagesWitching('root')}>委员会登录</a>
                      </p>

                    </div>
                  </form>
                ) : (
                  <form autoComplete="off" className={styles.form}>
                    <div className={styles.logo}>
                      {/* <i className='bx bxl-html5 bx-lg'></i> */}
                      <img src={logo} />
                      <h4 style={{ marginBottom: '0' }}>BsinPaaS一站式开发平台</h4>
                    </div>
                    <div className={styles.actual_form}>

                      <div className={styles.input_wrap}>
                        <input type="text" minLength="4" className={styles.input_field} autoComplete="off" required
                          name="username"
                          placeholder="用户名"
                          onChange={rootLoginInputChange} />
                      </div>
                      <div className={styles.input_wrap}>
                        <input type="password" minLength="4" className={styles.input_field} autoComplete="off" required
                          name="password"
                          placeholder="密码"
                          onChange={rootLoginInputChange} />
                      </div>
                      <Button
                        className={styles['sign_btn']}
                        loading={loadings}
                        onClick={rootLogin}
                      >
                        登录
                      </Button>
                      <p className={styles.text}>
                        s11e network web3品牌 <a className={styles.toggle} onClick={() => setPagesWitching('node')}>节点登录</a>
                      </p>

                    </div>
                  </form>
                )}
              </div>
              <div className={styles.carousel}>
                <div className={styles.images_wrapper}>
                  <img src={loginbg1} className="image img1 show" alt="" />
                  <img src={loginBg} className="image img2" alt="" />
                  <img src={loginBg} className="image img3" alt="" />
                </div>
                <div className={styles.text_slider}>
                  <div className={styles.text_wrapper}>
                    <div className={styles.text_group}>
                      <h2>s11e network web3品牌</h2>
                      <h2>打造专属个人空间</h2>
                      <h2>分享多彩生活</h2>
                    </div>
                  </div>
                  <div className={styles.bullets}>
                    <span className={styles.active} data-value="1"></span>
                    <span data-value="2"></span>
                    <span data-value="3"></span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </main>
      ) : (
        <MerchantLogin />
      )}
    </>
  );
};
