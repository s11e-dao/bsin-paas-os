package me.flyray.bsin.server.utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class HttpRequest {
  /**
   * 向指定URL发送GET方法的请求
   *
   * @param url 发送请求的URL
   * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
   * @return URL 所代表远程资源的响应结果
   */
  public static String sendGet(String url, String param) {
    String result = "";
    BufferedReader in = null;
    try {
      String urlNameString = url + "?" + param;
      URL realUrl = new URL(urlNameString);
      // 打开和URL之间的连接
      URLConnection connection = realUrl.openConnection();
      // 设置通用的请求属性
      connection.setRequestProperty("accept", "*/*");
      connection.setRequestProperty("connection", "Keep-Alive");
      connection.setRequestProperty(
          "user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
      // 建立实际的连接
      connection.connect();
      // 获取所有响应头字段
      Map<String, List<String>> map = connection.getHeaderFields();
      // 遍历所有的响应头字段
      for (String key : map.keySet()) {
        System.out.println(key + "--->" + map.get(key));
      }
      // 定义 BufferedReader输入流来读取URL的响应
      in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        result += line;
      }
    } catch (Exception e) {
      System.out.println("发送GET请求出现异常！" + e);
      e.printStackTrace();
    }
    // 使用finally块来关闭输入流
    finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
    return result;
  }

  /**
   * 向指定 URL 发送POST方法的请求
   *
   * @param url 发送请求的 URL
   * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
   * @return 所代表远程资源的响应结果
   */
  public static String sendPost(String url, String param) {
    PrintWriter out = null;
    BufferedReader in = null;
    String result = "";
    try {
      URL realUrl = new URL(url);
      // 打开和URL之间的连接
      URLConnection conn = realUrl.openConnection();
      // 设置通用的请求属性
      conn.setRequestProperty("accept", "application/json, text/javascript, */*; q=0.01");
      conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
      conn.setRequestProperty("Connection", "keep-alive");
      conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
      conn.setRequestProperty("Content-Length", "80");
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
      conn.setRequestProperty(
          "User-Agent",
          "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");

      conn.setRequestProperty(
          "user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
      // 发送POST请求必须设置如下两行
      conn.setDoOutput(true);
      conn.setDoInput(true);
      // 获取URLConnection对象对应的输出流
      OutputStreamWriter outWriter = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
      out = new PrintWriter(outWriter);
      // 发送请求参数
      out.print(param);
      // flush输出流的缓冲
      out.flush();
      // 定义BufferedReader输入流来读取URL的响应
      in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
      String line;
      while ((line = in.readLine()) != null) {
        result += line;
      }
    } catch (Exception e) {
      System.out.println("发送 POST 请求出现异常！" + e);
      e.printStackTrace();
    }
    // 使用finally块来关闭输出流、输入流
    finally {
      try {
        if (out != null) {
          out.close();
        }
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    return result;
  }

  // 发送xml格式
  public static String postXmlRequest(String url, String xml) throws Exception {
    HttpPost post = new HttpPost(url);
    post.setHeader("Content-type", "text/xml");
    post.setEntity(new StringEntity(xml));
    post.setEntity(new StringEntity(xml, "UTF-8"));
    CloseableHttpClient client = HttpClients.createDefault();
    CloseableHttpResponse response = client.execute(post);

    return response.getStatusLine().getStatusCode() == 200
        ? EntityUtils.toString(response.getEntity(), "UTF-8")
        : null;
  }

  public static void main(String[] args) {
    // 发送 GET 请求
    /* String s=HttpRequest.sendGet("http://localhost:6144/Home/RequestString", "key=123&v=456");
    System.out.println(s);*/

    // 发送 POST 请求
    /* String sr=HttpRequest.sendPost("http://www.cheshouye.com/api/weizhang/get_all_config","");

    JSONObject jsStr = JSONObject.fromObject(sr);
    JSONArray jsonarray = jsStr.getJSONArray("configs");

    for(int i=0;i<jsonarray.size();i++){
    	JSONObject ob1=(JSONObject)jsonarray.get(i);
    	Integer provinceId=ob1.getInt("provice_id");
    	String provinceName=ob1.getString("provice_name");
    	String provinceShortName=ob1.getString("provice_short_name");
    	JSONArray jsonarray2 = ob1.getJSONArray("citys");


    }
    System.out.println(jsonarray);*/
    // (JSONObject)jsonarray[i]
  }
}
