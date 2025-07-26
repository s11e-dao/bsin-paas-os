package me.flyray.bsin.payment.utils;

import cn.hutool.core.io.FileUtil;
import me.flyray.bsin.exception.BusinessException;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;

/*
 * 支付平台 获取系统文件工具类
 *
 */
@Component
public class ChannelCertResourceUtil {

    @Autowired
    private FileStorageService fileStorageService;

    public String getCertFilePath(String certFilePath) {
        return getCertFile(certFilePath).getAbsolutePath();
    }

    public File getCertFile(String certFilePath) {
        int lastIndex = certFilePath.lastIndexOf('/');
        String filename = certFilePath;
        if (lastIndex != -1) {
            filename = certFilePath.substring(lastIndex + 1);
        }
        File certFile = new File("../cert/" + filename);

        if (certFile.exists()) { // 本地存在直接返回
            return certFile;
        }

        // 当文件夹不存在时， 需要创建。
        if (!certFile.getParentFile().exists()) {
            if (!certFile.getParentFile().mkdirs()) {
                throw new BusinessException("999", "支付证书目录创建失败");
            }
        }

        // 请求下载并返回 新File
        return downloadFile(certFilePath, certFile);
    }


    /**
     * 下载文件
     **/
    private synchronized File downloadFile(String dbCertFilePath, File certFile) {
        File file = FileUtil.file(certFile.getAbsolutePath());
        // 使用Consumer方式处理下载的输入流
        fileStorageService.download(dbCertFilePath).inputStream(inputStream -> {
            try {
                FileUtil.writeFromStream(inputStream, file);
            } catch (Exception e) {
                throw new BusinessException("999", "支付证书下载失败：" + e.getMessage());
            }
        });
        
        return file;
    }

}
