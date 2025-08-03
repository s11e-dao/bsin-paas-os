package me.flyray.bsin.server.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.oss.AliOssStsConfig;
import me.flyray.bsin.oss.FileStorageProperties;
import me.flyray.bsin.oss.StsResponse;
import me.flyray.bsin.oss.TokenResponse;
import me.flyray.bsin.utils.BsinResultEntity;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.apache.shenyu.plugin.api.result.DefaultShenyuEntity;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/upload")
@ShenyuSpringMvcClient("/upload/**")
@ApiModule(value = "upload")
public class UploadController {

    @Value("${bsin.security.aes-secretKey}")
    private String aesSecretKey;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private AliOssStsConfig stsConfig;

    @PostMapping("/getOssToken")
    @ShenyuSpringMvcClient("/getOssToken")
    public Map getOssToken(){
        try {

            FileStorageProperties.AliOssConfig aliOssConfig = fileStorageProperties.getAliyunOss().get(0);
            StsResponse response = this.getStsToken();
            // 从配置中获取OSS域名
            String ossDomain = stsConfig.getOssDomain();
            if (StringUtils.isBlank(ossDomain)) {
                throw new BusinessException("OSS_CONFIG_ERROR", "OSS配置错误：缺少域名配置");
            }
            
            // 构建OSS客户端 神坑，这个地方的 getAccessKey 是临时的
            OSS client = new OSSClientBuilder().build(ossDomain, response.getAccessKeyId(), response.getAccessKeySecret());
            
            // 设置30分钟的过期时间
            long expireEndTime = System.currentTimeMillis() + 30 * 60 * 1000;
            Date expiration = new Date(expireEndTime);
            
            // 设置上传策略
            PolicyConditions policyConditions = new PolicyConditions();
            policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1024 * 1024 * 1024); // 1GB限制
            
            // 生成上传策略
            String postPolicy = client.generatePostPolicy(expiration, policyConditions);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            
            // 生成签名
            String postSignature = client.calculatePostSignature(postPolicy);

            TokenResponse responseObj = TokenResponse.builder()
                    .encodedPolicy(encodedPolicy)
                    .aliyunAccessKeyId(response.getAccessKeyId())
                    .signature(postSignature)
                    .bucketName(aliOssConfig.getBucketName())
                    .securityToken(response.getSecurityToken())
                    .domain(aliOssConfig.getDomain())
                    .endpoint(aliOssConfig.getEndPoint())
                    .build();
            Map resultMap = new HashMap<>();
            resultMap.put("code", 0);
            resultMap.put("message", "操作成功");
            resultMap.put("data", responseObj);
            return resultMap;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取OSS token失败：{}", e.getMessage(), e);
            throw new BusinessException("OSS_TOKEN_ERROR", "获取OSS token失败：" + e.getMessage());
        }
    }

    /**
     * webFlux uploadFile.
     * @param file  the file you upload
     * @return  response
     */
    @PostMapping(value = "/aliOssSignUpload")
    @ApiDoc(desc = "aliOssSignUpload")
    public Map aliOssSignUpload(@RequestPart("file") final MultipartFile file, @RequestPart("sign") final String sign) {

        // 创建一个随机的初始化向量
        byte[] iv = new byte[16]; // AES块大小为16字节
        // 用一个确定的值填充IV，例如全0
        Arrays.fill(iv, (byte) 0);
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        AES aes = new AES(Mode.CBC, Padding.ZeroPadding, aesSecretKey.getBytes(), ivspec.getIV());
        // 解密签名获得内容
        String content;
        try {
            content = aes.decryptStr(sign);
        } catch (Exception e) {
            throw new BusinessException("999", "签名错误");
        }
        if (!"hello".equals(content)) {
            throw new BusinessException("999", "签名错误");
        }

        FileInfo fileInfo =
                fileStorageService
                        .of(file)
                        // .setPath("relativePath") // 保存到相对路径下，为了方便管理，不需要可以不写
                        //                        .setObjectId("0") // 关联对象id，为了方便管理，不需要可以不写
                        //                        .setObjectType("0") // 关联对象类型，为了方便管理，不需要可以不写
                        //                        .setPlatform("aliyun-oss") // 使用指定的存储平台
                        //                        .putAttr("role", "admin") //
                        // 保存一些属性，可以在切面、保存上传记录、自定义存储平台等地方获取使用，不需要可以不写
                        .upload(); // 将文件上传到对应地方
        //        return fileInfo == null ? "上传失败！" : fileInfo.getUrl();
        Map resultMap = new HashMap<>();
        resultMap.put("code", 0);
        resultMap.put("message", "操作成功");
        resultMap.put("data", fileInfo);
        return resultMap;
    }

    /**
     * webFlux uploadFile.
     * @param file  the file you upload
     * @return  response
     */
    @PostMapping(value = "/aliOssUpload")
    @ApiDoc(desc = "aliOssUpload")
    public Map aliOssUpload(@RequestPart("file") final MultipartFile file) {
        System.out.println(file);
        FileInfo fileInfo =
                fileStorageService
                        .of(file)
                        // .setPath("relativePath") // 保存到相对路径下，为了方便管理，不需要可以不写
                        //                        .setObjectId("0") // 关联对象id，为了方便管理，不需要可以不写
                        //                        .setObjectType("0") // 关联对象类型，为了方便管理，不需要可以不写
                        //                        .setPlatform("aliyun-oss") // 使用指定的存储平台
                        //                        .putAttr("role", "admin") //
                        // 保存一些属性，可以在切面、保存上传记录、自定义存储平台等地方获取使用，不需要可以不写
                        .upload(); // 将文件上传到对应地方
        //        return fileInfo == null ? "上传失败！" : fileInfo.getUrl();
        Map resultMap = new HashMap<>();
        resultMap.put("code", 0);
        resultMap.put("message", "操作成功");
        resultMap.put("data", fileInfo);
        return resultMap;
    }

    public StsResponse getStsToken() throws ClientException {
        DefaultProfile.addEndpoint(stsConfig.getRegionId(), "Sts", stsConfig.getEndpoint());
        // 构造default profile: 神坑，这个地方的 getAccessKey 是临时的
        IClientProfile profile = DefaultProfile.getProfile(stsConfig.getRegionId(), stsConfig.getAccessKey(), stsConfig.getAccessKeySecret());
        // 构造client。
        DefaultAcsClient client = new DefaultAcsClient(profile);
        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setSysMethod(MethodType.POST);
        request.setRoleArn(stsConfig.getArn());
        request.setRoleSessionName(stsConfig.getRoleSessionName());
        // 如果policy为空，则用户将获得该角色下所有权限。
        request.setPolicy(stsConfig.getPolicy());
        // 设置临时访问凭证的有效时间
        request.setDurationSeconds(stsConfig.getDurationSeconds());
        final AssumeRoleResponse response = client.getAcsResponse(request);
        StsResponse result = BeanUtil.copyProperties(response.getCredentials(), StsResponse.class);
        if (StringUtils.isNotBlank(result.getExpiration())) {
            Date expiration = DateUtil.parse(result.getExpiration());
            result.setExpiration(DateUtil.formatDateTime(expiration));
        }
        BeanUtil.copyProperties(response.getCredentials(), result,"expiration");
        return result;
    }

}
