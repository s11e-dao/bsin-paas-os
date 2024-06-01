import React, { ChangeEvent, FC, useState } from 'react';
import graphic2 from '@/assets/picture/graphic2.svg';
import { connect, Dispatch, Loading, AppsState, history } from 'umi';
import { message, Button, Select, Modal } from 'antd';
import { merchantLogin, getTenantList, getTenantBaseApp } from '../../services/login';
import { userRegister } from '../../services/register';
import {
  setLocalStorageInfo,
  setSessionStorageInfo,
  getSessionStorageInfo
} from '@/utils/localStorageInfo';
import styles from './iofrm-theme5.less';
import { hex_md5 } from '../../utils/md5';

import huoyuanshequQr from '../../assets/huoyuanshequMpQr.png';

const { Option } = Select;


const Login2: FC = () => {

let registerNotNeedAudit = process.env.registerNotNeedAudit;

  React.useEffect(() => {
    const token = getSessionStorageInfo("token");
    if(token){
      history.push("/home")
    }
    getAllTenant();
  }, []);

  // 租户列表
  const [tenantList, setTenantList] = useState<[]>();
  // 租户岗位
  const [tenantBaseApp, setTenantBaseApp] = useState<{}>();
  // 选中的租户
  const [tenantId, setTenantId] = useState<string>();
  // 选中的租户的默认岗位
  const [postId, setPostId] = useState<string>();

  // 获取所有租户
  const getAllTenant = async () => {
    let res = await getTenantList({});
    if (res && res.code === 0) {
      setTenantList(res.data);
    } else {
      message.error('获取租户列表失败');
    }
  };


  // 登录、注册切换
  const [pagesWitching, setPagesWitching] = React.useState('login');
  // 登录表单值
  const [loginState, setLoginState] = React.useState({
    tenantId: '',
    username: '',
    password: '',
  });
  // 注册表单
  const [registerState, setRegisterState] = React.useState({
    registerUsername: '',
    registerMerchantName: '',
    registerPassword: '',
    registerVerifyCode: '',
    phone: '',
  });


  const [vcModalShow, setVcModalShow] = React.useState(false);
  // 登录选择租户
  const getVerifyCode = () => {
    // 获取用户的用户名，请求后台
    if (!tenantId) {
      return message.info('请选择节点');
    }
    const {
      registerMerchantName: merchantName,
      registerUsername: username,
      registerPassword: password,
      registerVerifyCode: verifyCode,
      phone,
    } = registerState;

    if (!merchantName) {
      return message.info('请输入商户名！');
    }
    if (!username) {
      return message.info('请输入用户名！');
    }
    // 弹出弹框，扫二维码关注公众号获取验证码
    setVcModalShow(true)

  };

  const handleCancel = () => {
    setVcModalShow(false);
  };

  // 按钮点击动画
  const [loadings, setLoadings] = React.useState(false);

  // 登录选择租户
  const handleChange = (value: string) => {
    setLoginState({
      ...loginState,
      tenantId: value,
    });
  };

  // 登录表单值变化调用
  const onChange = (event: ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    const name = event.target.name;
    setLoginState({
      ...loginState,
      [name]: value,
    });
  };

  // 选择租户
  function onChangeTenant(value: string) {
    setTenantId(value);
  }

  // 选择租户
  function onChangePost(value: string) {
    setPostId(value);
  }

  // 登录按钮调用
  const login = async (event: any) => {
    if (!tenantId) {
      return message.info('请选择节点');
    }
    // 如果用户名和密码为空，则提示
    if (!loginState.username) {
      return message.info('请输入用户名');
    }
    if (!loginState.password) {
      return message.info('请输入密码');
    }
    setLoadings(true);
    let res = await merchantLogin({
      ...loginState,
      tenantId: tenantId,
      password: hex_md5(loginState.password),
    });
    if (res && res.code == 0) {
      if (!res.data?.sysUser) {
        setLocalStorageInfo('userInformation', res.data?.merchantInfo);
      } else {
        setLocalStorageInfo('userInformation', res.data?.sysUser);
      }
      setLocalStorageInfo('merchantInfo', res.data?.merchantInfo);
      setSessionStorageInfo('token', { token: res.data?.token });
      
      message.success('登录成功！');

      getTenantBaseApp({ tenantId }).then((res) => {
        console.log(res)
        setTenantBaseApp(res?.data)
        history.push('/home');
      })
    }
    setLoadings(false);
  };

  // 注册表单值变化调用
  const onRegisterChange = (event: ChangeEvent<HTMLInputElement>) => {

    const value = event.target.value;
    const name = event.target.name;
    setRegisterState({
      ...registerState,
      [name]: value,
    });
  };

  // 注册按钮调用
  const register = async (event: any) => {
    if (!tenantId) {
      return message.info('请选择节点');
    }
    const {
      registerMerchantName: merchantName,
      registerUsername: username,
      registerPassword: password,
      registerVerifyCode: verifyCode,
      phone,
    } = registerState;
    // 如果用户名和密码为空，则提示

    if (!merchantName) {
      return message.info('请输入商户名！');
    }
    if (!username) {
      return message.info('请输入用户名！');
    }
    if (!password) {
      return message.info('请输入密码！');
    }
    if (!verifyCode) {
      return message.info('请输入验证码！');
    }
    if (!phone) {
      return message.info('请输入手机号！');
    }
    let telTest =
      /^1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\d{8}$/;
    if (!telTest.test(phone)) {
      return message.info('请输入正确的手机号！');
    }
    setLoadings(true);
    // console.log(hex_md5(registerState.password));

    let res = await userRegister({
      merchantName,
      username,
      phone,
      verifyCode,
      registerNotNeedAudit: registerNotNeedAudit,
      //tenantId: process.env.jiujiu.tenantId,
      tenantId: tenantId,
      postId: postId,
      password: hex_md5(password),
    });
    if (res?.code === 0) {
      setLoginState({
        ...loginState,
        username,
        password,
      });
      message.success('恭喜您，注册成功');
      setPagesWitching('login');
    }
    setLoadings(false);
  };

  return (
    <div className={styles['form-body']}>
      <div className={styles['row']}>
        <div className={styles['img-holder']}>
          <img src={graphic2} />
        </div>
        <div className={styles['form-holder']}>
          <div className={styles['form-content']}>
            <h3>bigan</h3>
            <p>一站式的Web3.0品牌构建服务网络</p>
            {pagesWitching === 'register' ? (
              <form className={styles['form']} >
                <Select
                  bordered={false}
                  className={styles['form-control']}
                  style={{
                    // marginTop: 20,
                    backgroundColor: '#eee',
                    border: 'none',
                    height: '38px',
                    margin: '14px 0',
                    width: '100%',
                    textAlign: 'left',
                  }}
                  showSearch
                  placeholder="请选择节点"
                  optionFilterProp="children"
                  onChange={onChangeTenant}
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
                <input
                  onChange={onRegisterChange}
                  className={styles['form-control']}
                  type="text"
                  name="registerMerchantName"
                  placeholder="商户名"
                  required
                />
                <input
                  onChange={onRegisterChange}
                  className={styles['form-control']}
                  type="text"
                  name="registerUsername"
                  placeholder="用户名"
                  required
                />
                <div style={{ display: "flex" }}>
                  <input
                    onChange={onRegisterChange}
                    className={styles['vc-form-control']}
                    type="text"
                    name="registerVerifyCode"
                    placeholder="验证码"
                    required
                  />
                  <button style={{ marginLeft: "20px" }} className={styles['vc-ibtn']}
                    onClick={getVerifyCode}
                  >获取验证码</button>
                </div>
                <input
                  onChange={onRegisterChange}
                  className={styles['form-control']}
                  type="password"
                  name="registerPassword"
                  placeholder="密码"
                  required
                />
                <input
                  onChange={onRegisterChange}
                  className={styles['form-control']}
                  type="text"
                  name="phone"
                  placeholder="手机号"
                  required
                />
                <div className={styles['form-button']}>
                  <Button
                    className={styles['ibtn']}
                    loading={loadings}
                    onClick={register}
                  >
                    注册
                  </Button>
                  <span className={styles['login-link']}>
                    已注册 bigan 账号了 ?
                    <a
                      onClick={() => setPagesWitching('login')}
                      className={styles['login-link']}
                    >
                      点击登录
                    </a>
                  </span>
                </div>
              </form>
            ) : (
              <form className={styles['form']}>
                <Select
                  bordered={false}
                  className={styles['form-control']}
                  style={{
                    // marginTop: 20,
                    backgroundColor: '#eee',
                    border: 'none',
                    height: '38px',
                    margin: '14px 0',
                    width: '100%',
                    textAlign: 'left',
                  }}
                  showSearch
                  placeholder="请选择节点"
                  optionFilterProp="children"
                  onChange={onChangeTenant}
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
                <input
                  onChange={onChange}
                  className={styles['form-control']}
                  type="text"
                  name="username"
                  placeholder="用户名"
                  required
                />
                <input
                  onChange={onChange}
                  className={styles['form-control']}
                  type="password"
                  name="password"
                  placeholder="密码"
                  required
                />
                <div className={styles['form-button']}>
                  <Button
                    className={styles['ibtn']}
                    loading={loadings}
                    onClick={login}
                  >
                    登录
                  </Button>
                  <span className={styles['login-link']}>
                    还没有自己的 bigan ?
                    <a
                      onClick={() => setPagesWitching('register')}
                      className={styles['login-link']}
                    >
                      点击注册
                    </a>
                  </span>
                </div>
              </form>
            )}
          </div>
        </div>
      </div>

      <Modal
        open={vcModalShow}
        title="请扫码关注获取验证码"
        onCancel={handleCancel}
        footer={null}
      >
        <div style={{ display: "flex" }}>
          <img style={{ margin: "0 auto", width: "300px", display: "inline-block" }} src={huoyuanshequQr} />
        </div>
        <p style={{ textAlign: "center" }}>请扫描关注获取验证码.</p>
      </Modal>

    </div>
  );
};

export default Login2;
