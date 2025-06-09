package me.flyray.bsin.security.contex;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import me.flyray.bsin.security.domain.LoginUser;
import org.springframework.util.Assert;


/**
 * Created by bolei on 2017/9/8.
 */
public class LoginInfoContextHelper {

    private static final ThreadLocal<Map<String, Object>> LOGIN_INFO = ThreadLocal.withInitial(HashMap::new);

    public static void set(String key, Object value) {
        Map<String, Object> map = LOGIN_INFO.get();
        map.put(key, value);
    }

    public static Object get(String key) {
        Map<String, Object> map = LOGIN_INFO.get();
        return map.get(key);
    }

    public static LoginUser getLoginUser() {
        LoginUser loginUser = new LoginUser();
        BeanUtil.fillBeanWithMap(LOGIN_INFO.get(), loginUser, Boolean.FALSE);
        return loginUser;
    }

    public static void setLoginUser(Map<String, Object> loginMap) {
        LOGIN_INFO.get().clear();
        LOGIN_INFO.get().putAll(loginMap);
    }


    public static String getAdminUserId() {
        return getLoginUser().getUserId();
    }

    public static String getTenantId() {
        return getLoginUser().getTenantId();
    }

    public static String getTenantMerchantNo() {
        return getLoginUser().getTenantMerchantNo();
    }

    public static String getBizRoleType() {
        return getLoginUser().getBizRoleType();
    }

    public static String getMerchantStoreNo() {
        return getLoginUser().getMerchantStoreNo();
    }

    public static String getMerchantNo() {
        return getLoginUser().getMerchantNo();
    }
    public static String getCredential() {
        return getLoginUser().getCredential();
    }
    public static String getCustomerNo() {
        return getLoginUser().getCustomerNo();
    }

    public static String getCustomerType() {
        return getLoginUser().getCustomerType();
    }
    public static String getStoreNo(){
        return getLoginUser().getStoreNo();
    }

    /**
     * 支持语言
     * @return
     */
    public static String getLocale() {
        Map<String, Object> map = LOGIN_INFO.get();
        return (String) map.get("locale");
    }

    public static String getAppId() {
        Map<String, Object> map = LOGIN_INFO.get();
        return (String) map.get("appKey");
    }

    public static String getJsonString() {
        return new JSONObject(LOGIN_INFO.get()).toJSONString();
    }

    public static void remove() {
        LOGIN_INFO.remove();
    }

}
