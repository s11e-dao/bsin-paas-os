import { Navigate, Outlet, history } from 'umi'
import { getSessionStorageInfo } from '@/utils/localStorageInfo';

export default (props) => {
  
  const token = getSessionStorageInfo("token");
  if (token) {
    return <Outlet />;
  } else{
    window.location.href = "/#/login"
    // 跳转到基座登录
    // return <Navigate to="/login" />;
  }
}