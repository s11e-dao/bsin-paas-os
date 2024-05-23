package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.SysTenant;
import me.flyray.bsin.domain.entity.SysUser;
import me.flyray.bsin.domain.entity.customer.Platform;
import me.flyray.bsin.domain.response.SysUserVO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.GoogleAuthenticatorService;
import me.flyray.bsin.facade.service.TenantService;
import me.flyray.bsin.facade.service.UserService;
import me.flyray.bsin.infrastructure.mapper.customer.PlatformMapper;
import me.flyray.bsin.infrastructure.utils.QrCodeUtils;
import me.flyray.bsin.infrastructure.utils.RedisClientUtil;
import me.flyray.bsin.security.authentication.AuthenticationProvider;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.AESUtils;
import me.flyray.bsin.utils.GoogleAuthenticator;
import me.flyray.bsin.utils.JsonUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@DubboService
@ApiModule(value = "googleAuthenticator")
@ShenyuDubboService("/googleAuthenticator")
public class GoogleAuthenticatorServiceImpl implements GoogleAuthenticatorService {
    @DubboReference(version = "dev")
    private UserService userService;

    @Value("${shenyu.security.authentication-secretKey}")
    private String authSecretKey;

    private int authExpiration = 3000;
    /**
     * 加密方式，HmacSHA1、HmacSHA256、HmacSHA512
     */
    private static final String CRYPTO = "HmacSHA1";

    @Autowired
    private PlatformMapper platformMapper;
    @DubboReference(version = "dev")
    private TenantService tenantService;


    @Override
    @ShenyuDubboClient("/getGoogleAuthToken")
    @ApiDoc(desc = "getGoogleAuthToken")
    public String getGoogleAuthToken(){
        String googleToken = AESUtils.AESEnCode(GoogleAuthenticator.getSecretKey());
        return googleToken;
    }

    @Override
    @ShenyuDubboClient("/getQrcode")
    @ApiDoc(desc = "getQrcode")
    public Map<String,Object> getQrcode(String userId){
        Map<String,Object> map = new HashMap<>();
        SysUser sysUser = userService.getByUserId(userId);
        String googleToken =sysUser.getGoogleSecretKey();
        String accountName = sysUser.getUsername();
        String secretKey = AESUtils.AESDeCode(googleToken);
//        String code = GoogleAuthenticator.getCode(secretKey);
//        map.put("code",code);
        String base64Pic = QrCodeUtils.creatRrCode(GoogleAuthenticator.getQrCodeText(secretKey,accountName,""), 200,200);
        map.put("qrcodeImg",base64Pic);
        map.put("secretKey",secretKey);
        return map;
    }

    @Override
    @ShenyuDubboClient("/checkCode")
    @ApiDoc(desc = "checkCode")
    public Map<String,Object> checkCode(String  tempToken, String authCode) {
        String userInfoStr = RedisClientUtil.get(tempToken);
        SysUserVO sysUserVO = JSONUtil.toBean(userInfoStr, SysUserVO.class);
        String googleSecretKey = sysUserVO.getSysUser().getGoogleSecretKey();
        String secretKey = AESUtils.AESDeCode(googleSecretKey);
        Boolean result = GoogleAuthenticator.checkCode(secretKey, Long.parseLong(authCode), System.currentTimeMillis());
        if(!result){
            throw new BusinessException("verification_code_error");
        }

        // 查询平台
        Integer bizRoleType;
        String bizRoleNo=null;
        SysUser sysUser = sysUserVO.getSysUser();
        SysTenant tenant = tenantService.getDetail(sysUser.getTenantId());
        if(tenant == null){
            throw new BusinessException("tenant_not_exist");
        }
//        if(tenant.getType() == 0){
//            bizRoleType = 0;  // 管理平台
//        }else {
//            QueryWrapper<Platform> platformQueryWrapper = new QueryWrapper<>();
//            platformQueryWrapper.eq("tenant_id",sysUser.getTenantId());
//            Platform platform = platformMapper.selectOne(platformQueryWrapper);
//            if(platform==null){
//                throw  new BusinessException("PLATFORM_NOT_EXIST");
//            }
//            bizRoleType = 1;
//            bizRoleNo = platform.getSerialNo();
//
//            // TODO 根据sysUser.orgId查询商户角色
//        }

        QueryWrapper<Platform> platformQueryWrapper = new QueryWrapper<>();
        platformQueryWrapper.eq("tenant_id",sysUser.getTenantId());
        Platform platform = platformMapper.selectOne(platformQueryWrapper);
        if(platform==null){
            throw  new BusinessException("PLATFORM_NOT_EXIST");
        }
        bizRoleType = 1;
        bizRoleNo = platform.getSerialNo();

        // 生成token
        LoginUser loginUser = new LoginUser();
        loginUser.setTenantId(sysUser.getTenantId());
        loginUser.setUserId(sysUser.getUserId());
        loginUser.setPlatformNo(bizRoleNo);
        loginUser.setUsername(sysUser.getUsername());
        loginUser.setPhone(sysUser.getPhone());
        loginUser.setOrgId(sysUser.getOrgId());
        loginUser.setBizRoleType(bizRoleType);
        loginUser.setBizRoleNo(bizRoleNo);
        String token = AuthenticationProvider.createToken(loginUser, authSecretKey, authExpiration);
        loginUser.setToken(token);
        Map<String, Object> sysUserMap = BeanUtil.beanToMap(loginUser);
        return sysUserMap;
    }

    public static void main(String[] args) {
//        // 生成秘钥
//        String secretKey = GoogleAuthenticator.getSecretKey();
//        System.out.println("secretKey:"+secretKey);
//
//        String googleToken = AESUtils.AESEnCode(secretKey);
//        System.out.println("googleToken:"+googleToken);

       String secretKey = AESUtils.AESDeCode("90eef9c666e2c63e6e5a14075ab4571235427af67d6ac34e5083681c4f66bae3");
        System.out.println("secretKey:"+secretKey);

        // 获取验证码
        String authCode = GoogleAuthenticator.getCode(secretKey);
        System.out.println("authCode："+authCode);

//        // 校验验证码
//        Boolean result = GoogleAuthenticator.checkCode(secretKey, Long.parseLong(authCode), System.currentTimeMillis());
//        System.out.println("result："+result);
    }


}
