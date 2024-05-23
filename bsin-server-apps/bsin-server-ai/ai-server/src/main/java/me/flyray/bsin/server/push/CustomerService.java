package me.flyray.bsin.server.push;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerService {
  //    private static final Logger LOG = Logger.getLogger(CustomerService.class);
  //    protected Logger LOG = LoggerFactory.getLogger(getClass());

  public static void connectWeiXinInterface(String token, String toUser, String content) {
    URL url;
    try {
      String action = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + token;
      String json =
          "{\"touser\": \""
              + toUser
              + "\",\"msgtype\": \"text\", \"text\": {\"content\": \""
              + content
              + "\"}}";
      System.out.println("json:" + json);

      url = new URL(action);
      HttpURLConnection http = (HttpURLConnection) url.openConnection();
      http.setRequestMethod("POST");
      http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      http.setDoOutput(true);
      http.setDoInput(true);
      System.setProperty("sun.net.client.defaultConnectTimeout", "30000"); // 连接超时30秒
      System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
      http.connect();
      OutputStream os = http.getOutputStream();
      os.write(json.getBytes("UTF-8")); // 传入参数
      InputStream is = http.getInputStream();
      int size = is.available();
      byte[] jsonBytes = new byte[size];
      is.read(jsonBytes);
      String result = new String(jsonBytes, "UTF-8");
      System.out.println("请求返回结果:" + result);
      os.flush();
      os.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //    public String postMessage(String openid, String content) throws Exception {
  //        //String access_token=WxMessageUtil.obtainAccessToken();
  //        //appid和appsecret为公众号里面的
  //        String tokenData =
  // "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" +
  // environment.getProperty("wx.mp.configs[0].appId") + "&secret=" +
  // environment.getProperty("wx.mp.configs[0].secret");
  //        // 返回的用户信息json字符串
  //        System.out.println(tokenData);
  //        String result = HttpUtil.get(tokenData);
  //        System.out.println(result);
  //        JSONObject jsonObject = JSONObject.parseObject(result);
  //        //先获取access_token
  //        String access_token = String.valueOf(jsonObject.get("access_token"));
  //        System.out.println(access_token);
  //        //消息推送接口
  //        String path = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" +
  // access_token;
  //        JSONObject jsonData = new JSONObject();
  //        jsonData.put("touser", openid);
  //        jsonData.put("msgtype", "text");
  //        JSONObject text = new JSONObject();
  //        text.put("content", content);
  //        jsonData.put("text", text);
  //        System.out.println(jsonData);
  //        System.out.println(path);
  //        //HttpUtil.doPostJson(path, jsonData.toJSONString());
  //        HttpRequest.sendPost(path, jsonData.toJSONString());
  //        return "SUCCESS";
  //        //return null;
  //    }
}
