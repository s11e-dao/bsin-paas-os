package me.flyray.bsin.server.biz;

import me.flyray.bsin.domain.response.SysUserVO;
import me.flyray.bsin.log.event.LoginInfoEvent;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.SpringUtil;
import org.springframework.stereotype.Service;

@Service
public class LoginBiz {

    public void addLoginLog(SysUserVO userVO, LoginUser loginUser){
        try {
            LoginInfoEvent logininforEvent = new LoginInfoEvent();
            logininforEvent.setTenantId(loginUser.getTenantId());
            logininforEvent.setBizRoleType(loginUser.getBizRoleType());
            logininforEvent.setBizRoleTypeNo(loginUser.getBizRoleTypeNo());
            logininforEvent.setUsername(userVO.getSysUser().getUsername());
            logininforEvent.setStatus("1");
            logininforEvent.setMessage("登录成功");

            SpringUtil.context().publishEvent(logininforEvent);
        }catch (Exception e){
            System.out.println(e);
        }
    }

}
