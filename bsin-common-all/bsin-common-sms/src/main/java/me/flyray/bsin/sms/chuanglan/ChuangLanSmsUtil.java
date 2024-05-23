package me.flyray.bsin.sms.chuanglan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeMap;

import static me.flyray.bsin.sms.chuanglan.DES3Utils.encryptBase64;


/**
 * @author tianyh
 * @Description:HTTP 请求
 */

public class ChuangLanSmsUtil {
    public final String charset = "utf-8";
    // 用户平台API账号(非登录账号,示例:N1234567)
    public String account;
    // 用户平台API密码(非登录密码)
    public String password;


    public ChuangLanSmsUtil(String account, String password) {
        this.account = account;
        this.password = password;
    }

    /**
     * @param path
     * @param postContent
     * @return String
     * @throws
     * @author tianyh
     * @Description
     */
    public static String sendSmsByPost(String path, String postContent) {
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // 提交模式
            httpURLConnection.setRequestMethod("POST");
            //连接超时 单位毫秒
            httpURLConnection.setConnectTimeout(10000);
            //读取超时 单位毫秒
            httpURLConnection.setReadTimeout(10000);
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

//			PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
//			printWriter.write(postContent);
//			printWriter.flush();

            httpURLConnection.connect();
            OutputStream os = httpURLConnection.getOutputStream();
            os.write(postContent.getBytes("UTF-8"));
            os.flush();

            StringBuilder sb = new StringBuilder();
            int httpRspCode = httpURLConnection.getResponseCode();
            if (httpRspCode == HttpURLConnection.HTTP_OK) {
                // 开始获取数据
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                return sb.toString();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 创蓝云智 余量查询
     */
    public SmsBalanceResponse smsBalance() throws Exception {
        String smsBalanceRequestUrl = "http://smssh1.253.com/msg/balance/json";
        SmsBalanceRequest smsBalanceRequest = new SmsBalanceRequest(account, password);
        String requestJson = JSON.toJSONString(smsBalanceRequest);
        String response = ChuangLanSmsUtil.sendSmsByPost(smsBalanceRequestUrl, requestJson);
        return JSON.parseObject(response, SmsBalanceResponse.class);


    }

    /**
     * 创蓝云智 普通短信发送
     */
    public SmsSendResponse smsSend(String msg, String phone, String report) throws Exception {
        String smsSingleRequestServerUrl = "https://smssh1.253.com/msg/v1/send/json";
        SmsSendRequest smsSingleRequest = new SmsSendRequest(account, password, msg, phone, report);
        String requestJson = JSON.toJSONString(smsSingleRequest);
        String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);
        return JSON.parseObject(response, SmsSendResponse.class);
    }

    public SmsSendResponse smsSend(String msg, String phone) throws Exception {
        String smsSingleRequestServerUrl = "https://smssh1.253.com/msg/v1/send/json";
        SmsSendRequest smsSingleRequest = new SmsSendRequest(account, password, msg, phone, "true");
        String requestJson = JSON.toJSONString(smsSingleRequest);
        String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);
        return JSON.parseObject(response, SmsSendResponse.class);
    }


    /**
     * 创蓝云智 变量短信发送接口
     */
    public SmsSendResponse smsVariableSend(String msg, String params) throws Exception {
        String smsSingleRequestServerUrl = "https://smssh1.253.com/msg/variable/json";
        SmsVariableSendRequest smsVariableSendRequest = new SmsVariableSendRequest(account, password, msg, params, "", "true", "");
        String requestJson = JSON.toJSONString(smsVariableSendRequest);
        String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);
        return JSON.parseObject(response, SmsSendResponse.class);
    }


    /**
     * 创蓝云智 普通短信加密发送接口
     *
     * @param msg:    长度不能超过1000个字符，签名需要加在内容里， 如：【253云通讯】您的验证码是123456。  用营销账号提交短信时，末尾需带上退订回T等字眼
     * @param phone:  手机号码，多个手机号码使用英文逗号分隔
     * @param extend: 纯数字，需保证手机端口号加自定义扩展码总位数不超过20位，建议1-3位，如需上行短信推送或拉取则必填
     * @param uid:    一般可以设置订单号或者短信发送记录流水号，用于区分短信业务，总位数不超过64位
     */
    public SmsSendResponse smsSecretSend(String msg, String phone, String extend, String uid) throws Exception {
        String smsSingleRequestServerUrl = "https://smssh1.253.com/msg/v1/send/encrypt/json";
        JSONObject json = new JSONObject();
        String pwd = DigestUtils.md5Hex(password);
        json.put("account", account);
        json.put("extend", extend);
        json.put("msg", msg);
        json.put("phone", encryptBase64(phone, pwd));
        json.put("report", "true");
        json.put("timestamp", System.currentTimeMillis());
        json.put("uid", uid);
        TreeMap<String, Object> paramMap = JSONObject.parseObject(json.toJSONString(), new TypeReference<TreeMap<String, Object>>() {
        });
        StringBuffer buffer1 = new StringBuffer();
        paramMap.values().stream().forEach(obj -> {
            buffer1.append(obj.toString());
        });
        buffer1.append(pwd);
        String sign = DigestUtils.md5Hex(buffer1.toString());
        json.put("sign", sign);
        String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, json.toJSONString());
        return JSON.parseObject(response, SmsSendResponse.class);
    }


    /**
     * 创蓝云智 变量短信加密发送接口
     *
     * @param msg:    长度不能超过1000个字符，签名需要加在内容里， 如：【年画大熊猫】您的验证码为：{$var}    变量符号固定使用:“{$var}”，用营销账号提交短信时，末尾需带上退订回T等字眼
     * @param params: 手机号码和变量参数，多组参数使用英文分号;区分，必填， 如： "16675588381,1234"
     * @param extend: 纯数字，需保证手机端口号加自定义扩展码总位数不超过20位，建议1-3位，如需上行短信推送或拉取则必填，可为空值
     * @param uid:    一般可以设置订单号或者短信发送记录流水号，用于区分短信业务，总位数不超过64位，可为空值
     */
    public SmsSendResponse smsVariableSecretSend(String msg, String params, String extend, String uid) throws Exception {
        String smsSingleRequestServerUrl = "https://smssh1.253.com/msg/v1/variable/encrypt/json";
        JSONObject json = new JSONObject();
        String pwd = DigestUtils.md5Hex(password);
        json.put("account", account);
        json.put("extend", extend);
        json.put("msg", msg);
        json.put("params", encryptBase64(params, pwd));
        json.put("report", "true");
        json.put("timestamp", System.currentTimeMillis());
        json.put("uid", uid);
        TreeMap<String, Object> paramMap = JSONObject.parseObject(json.toJSONString(), new TypeReference<TreeMap<String, Object>>() {
        });
        StringBuffer buffer1 = new StringBuffer();
        paramMap.values().stream().forEach(obj -> {
            buffer1.append(obj.toString());
        });
        buffer1.append(pwd);
        String sign = DigestUtils.md5Hex(buffer1.toString());
        json.put("sign", sign);
        String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, json.toJSONString());
        return JSON.parseObject(response, SmsSendResponse.class);
    }

}


