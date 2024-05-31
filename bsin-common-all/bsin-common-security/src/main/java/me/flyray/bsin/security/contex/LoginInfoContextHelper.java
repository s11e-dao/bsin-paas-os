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
        Assert.notNull(getLoginUser().getUserId(), "adminUserId is null");
        return getLoginUser().getUserId();
    }

    public static String getTenantId() {
        Assert.notNull(getLoginUser().getTenantId(), "tenantId is null");
        return getLoginUser().getTenantId();
    }

    public static String getMerchantNo() {
        return getLoginUser().getMerchantNo();
    }

    public static String getCustomerNo() {
        Assert.notNull(getLoginUser().getCustomerNo(), "customerNo is null");
        return getLoginUser().getCustomerNo();
    }

    public static String getCustomerType() {
        Assert.notNull(getLoginUser().getCustomerType(), "customerType is null");
        return getLoginUser().getCustomerType();
    }
    public static String getStoreNo(){
        Assert.notNull(getLoginUser().getStoreNo(), "storeNo is null");
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
        Assert.notNull(getLoginUser().getAppId(), "appId is null");
        return getLoginUser().getAppId();
    }

    public static String getJsonString() {
        return new JSONObject(LOGIN_INFO.get()).toJSONString();
    }

    public static void remove() {
        LOGIN_INFO.remove();
    }

}
