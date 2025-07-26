package me.flyray.bsin.payment.utils;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.oss.OssUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;

/*
 * 支付平台 获取系统文件工具类
 *
 */
@Slf4j
@Component
public class ChannelCertResourceUtil {

    public String getCertFilePath(String certFilePath) {
        return getCertFile(certFilePath).getAbsolutePath();
    }

    public File getCertFile(String certFilePath) {
        log.info("获取证书文件，路径：{}", certFilePath);
        
        // 提取文件名
        int lastIndex = certFilePath.lastIndexOf('/');
        String filename = certFilePath;
        if (lastIndex != -1) {
            filename = certFilePath.substring(lastIndex + 1);
        }
        
        File certFile = new File("../cert/" + filename);
        log.debug("证书文件本地路径：{}", certFile.getAbsolutePath());

        // 本地存在直接返回
        if (certFile.exists()) {
            log.info("证书文件已存在本地，直接返回：{}", certFile.getAbsolutePath());
            return certFile;
        }

        // 创建目录
        if (!certFile.getParentFile().exists()) {
            log.info("创建证书目录：{}", certFile.getParentFile().getAbsolutePath());
            if (!certFile.getParentFile().mkdirs()) {
                log.error("支付证书目录创建失败：{}", certFile.getParentFile().getAbsolutePath());
                throw new BusinessException("999", "支付证书目录创建失败");
            }
        }

        // 下载文件
        log.info("证书文件不存在本地，开始下载：{}", certFilePath);
        return downloadFile(certFilePath, certFile);
    }


    /**
     * 下载文件
     **/
    private synchronized File downloadFile(String dbCertFilePath, File certFile) {
        File file = FileUtil.file(certFile.getAbsolutePath());
        InputStream inputStream = null;
        
        try {
            log.info("开始下载证书文件，URL：{}，目标文件：{}", dbCertFilePath, file.getAbsolutePath());
            
            // 获取输入流
            inputStream = OssUtil.getInputStreamByUrl(dbCertFilePath);
            if (inputStream == null) {
                throw new BusinessException("999", "无法获取证书文件输入流");
            }
            
            // 写入文件
            FileUtil.writeFromStream(inputStream, file);
            
            log.info("证书文件下载成功，文件大小：{} bytes", file.length());
            return file;
            
        } catch (BusinessException e) {
            log.error("证书文件下载失败（业务异常），URL：{}，错误：{}", dbCertFilePath, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("证书文件下载失败，URL：{}，错误：{}", dbCertFilePath, e.getMessage(), e);
            throw new BusinessException("999", "支付证书下载失败：" + e.getMessage());
        } finally {
            // 关闭输入流
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    log.warn("关闭证书文件输入流失败：{}", e.getMessage());
                }
            }
        }
    }

}
