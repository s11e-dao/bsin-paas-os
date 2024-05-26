package me.flyray.bsin.infrastructure.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import me.flyray.bsin.exception.BusinessException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class OkHttpUtils {


    private static final Logger log = LoggerFactory.getLogger(OkHttpUtils.class);

    /**
     * 同步的GET
     * @param url
     */
    public static String  httpGet(String url){
        OkHttpClient  client = new OkHttpClient();

        Request getRequest = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(getRequest).execute();
            System.out.println(response.body().string());
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("调用失败");
        }
    }

    /**
     *
     * @param url
     * @param jsonObject
     * @return
     */
    public static JSONObject  httpPost(String url, JSONObject jsonObject){
        OkHttpClient client = new OkHttpClient.Builder()
        .readTimeout(3000, TimeUnit.SECONDS)  // 设置读取超时时间为30秒
        .build();

        RequestBody requestJsonBody = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json")
        );

        Request postRequest = new Request.Builder()
                .url(url)
                .post(requestJsonBody)
                .build();

        try {
            Response response = client.newCall(postRequest).execute();
            String bodyStr = response.body().string();
            JSONObject jsonObject1 = (JSONObject) JSON.parse(bodyStr);
            if((Integer)jsonObject1.get("code")!=0){
                log.info("http调用失败，jsonObject：{},错误信息：{}",jsonObject,jsonObject1.get("msg"));
                throw new BusinessException("调用失败");
            }
            JSONObject data = (JSONObject)jsonObject1.get("data");
            if(data==null){
                throw new BusinessException("调用失败");
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("调用失败");
        }
    }



}
