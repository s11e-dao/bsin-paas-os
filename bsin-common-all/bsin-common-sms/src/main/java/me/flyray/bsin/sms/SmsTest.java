package me.flyray.bsin.sms;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.sms.chuanglan.*;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @author ：bolei
 * @date ：Created in 2022/4/12 14:56
 * @description：短信测试
 * @modified By：leonard
 */


@Slf4j
public class SmsTest {

    public static final String charset = "utf-8";
    // 用户平台API账号(非登录账号,示例:N1234567)
    public static String account = "YZM6946400";
    // 用户平台API密码(非登录密码)
    public static String password = "7YoC02w4Kr4a45";

    /**
     * 创蓝云智 余量查询
     */
    @Test
    public void chuangLanSmsBalance() throws Exception {

        //请求地址请登录253云通讯自助通平台查看或者询问您的商务负责人获取
        String smsBalanceRequestUrl = "http://smssh1.253.com/msg/balance/json";
        SmsBalanceRequest smsBalanceRequest = new SmsBalanceRequest(account, password);

        String requestJson = JSON.toJSONString(smsBalanceRequest);

        System.out.println("before request string is: " + requestJson);

        String response = ChuangLanSmsUtil.sendSmsByPost(smsBalanceRequestUrl, requestJson);

        System.out.println("response after request result is : " + response);

        SmsBalanceResponse smsVarableResponse = JSON.parseObject(response, SmsBalanceResponse.class);

        System.out.println("response  toString is : " + smsVarableResponse);

    }


    /**
     * 创蓝云智短信验证测试
     */
    @Test
    public void chuangLanSmsAddSignature() throws Exception {

        //请求地址请登录253云通讯自助通平台查看或者询问您的商务负责人获取
        String smsBalanceRequestUrl = "https://api.chuanglan.com/api/apis/signature/signatureAdd";
        SmsBalanceRequest smsBalanceRequest = new SmsBalanceRequest(account, password);

        String requestJson = JSON.toJSONString(smsBalanceRequest);

        System.out.println("before request string is: " + requestJson);

        String response = ChuangLanSmsUtil.sendSmsByPost(smsBalanceRequestUrl, requestJson);

        System.out.println("response after request result is : " + response);

        SmsBalanceResponse smsVarableResponse = JSON.parseObject(response, SmsBalanceResponse.class);

        System.out.println("response  toString is : " + smsVarableResponse);

    }




    /**
     * 创蓝云智短信验证测试
     */
    @Test
    public void chuangLanSmsSend() throws Exception {

        //请求地址请登录253云通讯自助通平台查看或者询问您的商务负责人获取
        String smsSingleRequestServerUrl = "https://smssh1.253.com/msg/v1/send/json";
        // 短信内容
        String msg = "【253云通讯】你好,你的验证码是123456";
        //手机号码
        String phone = "16675588381";
        //状态报告
        String report = "true";

        SmsSendRequest smsSingleRequest = new SmsSendRequest(account, password, msg, phone, report);

        String requestJson = JSON.toJSONString(smsSingleRequest);

        System.out.println("before request string is: " + requestJson);

        String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);

        System.out.println("response after request result is :" + response);

        SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);

        System.out.println("response  toString is :" + smsSingleResponse);
    }
}
