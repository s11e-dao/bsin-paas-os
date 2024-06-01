package me.flyray.bsin.infrastructure.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import me.flyray.bsin.redis.provider.BsinCacheProvider;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AnywebBiz {


    public Map<String,String> getAnywebAccessToken(String code) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("appid", "ed6f4342-3a51-4b87-bd47-02ff13a04962")
                .addFormDataPart("secret", "1b70582-74b5-4708-ab0a-332706058407")
                .addFormDataPart("code", code)
                .build();
        Request request = new Request.Builder()
                .url("https://api.anyweb.cc/oauth/accessToken")
                .method("POST", body)
                .build();
        Response response = client.newCall(request).execute();
        String responseJson = response.body().string();
        System.out.println(responseJson);
        JSONObject jsonObj = (JSONObject) JSON.parse(responseJson);
        JSONObject data = (JSONObject) jsonObj.get("data");
        Map<String,String> resultMap = new HashMap();
        resultMap.put("unionid", (String) data.get("unionid"));
        resultMap.put("accessToken",(String) data.get("accessToken"));
        return resultMap;
    }

    public Map<String,String> getAnywebUserInfo(String code) throws IOException {

        Map accessTokenMap = getAnywebAccessToken(code);
        String unionid = (String) accessTokenMap.get("unionid");
        String accessToken = (String) accessTokenMap.get("accessToken");

        String userInfo = BsinCacheProvider.get("waas","anyweb:unionid:"+unionid);
        if (StringUtils.isNotBlank(userInfo)){
            JSONObject userInfoObj = (JSONObject) JSON.parse(userInfo);
            Map<String,String> resultMap = new HashMap();
            resultMap.put("phone", (String) userInfoObj.get("phone"));
            resultMap.put("name",(String) userInfoObj.get("name"));
            return resultMap;
        }

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("appid", "ed6f4342-3a51-4b87-bd47-02ff13a04962")
                .addFormDataPart("secret", "a1b70582-74b5-4708-ab0a-332706058407")
                .addFormDataPart("accessToken", accessToken)
                .addFormDataPart("unionid", unionid)
                .build();
        Request request = new Request.Builder()
                .url("https://api.anyweb.cc/oauth/userInfo")
                .method("POST", body)
                .build();
        Response response = client.newCall(request).execute();
        String responseJson = response.body().string();
        System.out.println(responseJson);
        JSONObject jsonObj = (JSONObject) JSON.parse(responseJson);

        JSONObject data = (JSONObject) jsonObj.get("data");
        // 将unionid与用户信息进行缓存 anyweb:unionid:XXX:
        BsinCacheProvider.put("waas","anyweb:unionid:"+unionid,data.toJSONString());
        Map<String,String> resultMap = new HashMap();
        resultMap.put("phone", (String) data.get("phone"));
        resultMap.put("name",(String) data.get("name"));
        return resultMap;
    }
}
