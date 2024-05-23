import { Navigate, Outlet } from 'umi'
import { getSessionStorageInfo } from '@/utils/localStorageInfo';

export default (props) => {
  
  const token = getSessionStorageInfo("token");
  if (token) {
    return <Outlet />;
  } else{
    return <Navigate to="/login" />;
  }
}