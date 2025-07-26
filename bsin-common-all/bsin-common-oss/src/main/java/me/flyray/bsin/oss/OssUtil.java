package me.flyray.bsin.oss;


import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.exception.BusinessException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

@Slf4j
public class OssUtil {


    /**
     * 根据URL获取文件输入流
     *
     * @param url 文件URL地址
     * @return 文件输入流
     * @throws BusinessException 当URL无效或下载失败时抛出
     */
    public static InputStream getInputStreamByUrl(String url) {
        return getInputStreamByUrl(url, 30000, 30000);
    }

    /**
     * 根据URL获取文件输入流（指定超时时间）
     *
     * @param url 文件URL地址
     * @param connectTimeout 连接超时时间（毫秒）
     * @param readTimeout 读取超时时间（毫秒）
     * @return 文件输入流
     * @throws BusinessException 当URL无效或下载失败时抛出
     */
    public static InputStream getInputStreamByUrl(String url, int connectTimeout, int readTimeout) {
        // 参数验证
        if (url == null || url.trim().isEmpty()) {
            log.error("URL参数不能为空");
            throw new BusinessException("999", "URL参数不能为空");
        }

        log.debug("开始获取文件输入流，URL：{}", url);

        try {
            // 创建URL对象
            URL urlObj = new URL(url);
            
            // 打开连接
            URLConnection connection = urlObj.openConnection();
            
            // 设置连接参数
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            connection.setRequestProperty("Accept", "*/*");
            connection.setDoInput(true);
            connection.setUseCaches(false);
            
            // 如果是HTTP连接，设置额外参数
            if (connection instanceof HttpURLConnection) {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.setInstanceFollowRedirects(true);
                
                // 检查响应码
                int responseCode = httpConnection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    log.error("HTTP请求失败，URL：{}，响应码：{}", url, responseCode);
                    throw new BusinessException("999", "文件下载失败，HTTP响应码：" + responseCode);
                }
            }
            
            // 获取输入流
            InputStream inputStream = connection.getInputStream();
            if (inputStream == null) {
                log.error("无法获取文件输入流，URL：{}", url);
                throw new BusinessException("999", "无法获取文件输入流");
            }
            
            log.debug("成功获取文件输入流，URL：{}", url);
            return inputStream;
            
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("根据URL读取文件异常，URL：{}，错误：{}", url, e.getMessage(), e);
            throw new BusinessException("999", "文件下载失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("获取文件输入流时发生未知异常，URL：{}，错误：{}", url, e.getMessage(), e);
            throw new BusinessException("999", "文件下载失败：" + e.getMessage());
        }
    }

    /**
     * 根据URL获取文件字节数组
     *
     * @param url 文件URL地址
     * @return 文件字节数组
     * @throws BusinessException 当URL无效或下载失败时抛出
     */
    public static byte[] getBytesByUrl(String url) {
        return getBytesByUrl(url, 30000, 30000);
    }

    /**
     * 根据URL获取文件字节数组（指定超时时间）
     *
     * @param url 文件URL地址
     * @param connectTimeout 连接超时时间（毫秒）
     * @param readTimeout 读取超时时间（毫秒）
     * @return 文件字节数组
     * @throws BusinessException 当URL无效或下载失败时抛出
     */
    public static byte[] getBytesByUrl(String url, int connectTimeout, int readTimeout) {
        InputStream inputStream = null;
        try {
            inputStream = getInputStreamByUrl(url, connectTimeout, readTimeout);
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("读取文件字节数组失败，URL：{}，错误：{}", url, e.getMessage(), e);
            throw new BusinessException("999", "读取文件内容失败：" + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.warn("关闭输入流失败：{}", e.getMessage());
                }
            }
        }
    }

    /**
     * 验证URL是否可访问
     *
     * @param url 文件URL地址
     * @return 是否可访问
     */
    public static boolean isUrlAccessible(String url) {
        try {
            getInputStreamByUrl(url, 5000, 5000).close();
            return true;
        } catch (Exception e) {
            log.debug("URL不可访问，URL：{}，错误：{}", url, e.getMessage());
            return false;
        }
    }

}
