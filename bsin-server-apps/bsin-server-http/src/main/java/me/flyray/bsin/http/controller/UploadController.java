/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.flyray.bsin.http.controller;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.http.config.BsinUploadProperties;
import me.flyray.bsin.http.result.ResultBean;
import me.flyray.bsin.oss.ipfs.BsinIpfsService;
import me.flyray.bsin.utils.BsinResultEntity;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UploadController.
 */

@Slf4j
@RestController
@RequestMapping("/upload")
@ShenyuSpringMvcClient("/upload/**")
@ApiModule(value = "upload")
public class UploadController {

    @Autowired
    private BsinUploadProperties uploadProperties;

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private BsinIpfsService bsinIpfsService;

    /**
     * webFlux uploadFile.
     * @param file  the file you upload
     * @return  response
     */
    @PostMapping(value = "/aliOssUpload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @ApiDoc(desc = "aliOssUpload")
    public BsinResultEntity aliOssUpload(@RequestPart("file") final FilePart file, String relativePath) {
        System.out.println(file);
        FileInfo fileInfo =
                fileStorageService
                        .of(file)
                        .setPath(relativePath) // 保存到相对路径下，为了方便管理，不需要可以不写
                        //                        .setObjectId("0") // 关联对象id，为了方便管理，不需要可以不写
                        //                        .setObjectType("0") // 关联对象类型，为了方便管理，不需要可以不写
                        //                        .setPlatform("aliyun-oss") // 使用指定的存储平台
                        //                        .putAttr("role", "admin") //
                        // 保存一些属性，可以在切面、保存上传记录、自定义存储平台等地方获取使用，不需要可以不写
                        .upload(); // 将文件上传到对应地方
        //        return fileInfo == null ? "上传失败！" : fileInfo.getUrl();
        ResultBean response = new ResultBean();
        return BsinResultEntity.ok();
    }

    /**
     * webFlux uploadFile.
     * @param file  the file you upload
     * @return  response
     */
    @PostMapping(value = "/localUpload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @ApiDoc(desc = "localUpload")
    public ResultBean localUpload(@RequestPart("file") final FilePart file,
                                  @RequestParam(required = false) String currentPath,
                                  @RequestParam("tenantAppType") String tenantAppType,
                                  HttpServletRequest request) {

        ResultBean response = new ResultBean();

        String token = request.getHeader("Authorization");
        String tenantId = request.getHeader("tenantId");
        String fileName = file.filename();
        // 根据用户类型判断用户属于哪个平台
        String dev = "bigan-local";
        if (tenantAppType != null) {
            dev = tenantAppType + "-local";
        }

        String tmpCurrentPath = "";
        if (currentPath != null) {
            currentPath = tmpCurrentPath + currentPath + "/";
        } else {
            currentPath = tmpCurrentPath;
        }

        String localPath = uploadProperties.getUploadPath() + dev + "/" + tenantId + "/" + currentPath;
        currentPath = "/" + dev + "/" + tenantId + "/" + currentPath;

        // 上传文件到服务器
        Map<String, Object> resMap = new HashMap<String, Object>();
        try {
            log.debug("currentPath: " + currentPath);
            log.debug("localPath: " + localPath);
            Path path = Paths.get(localPath);
            Path pathCreate = Files.createDirectories(path);
            System.out.println("上传的文件名为：" + fileName);
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            System.out.println("上传的后缀名为：" + suffixName);
            System.out.println(localPath);
            File dest = new File(localPath + fileName);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            String url = uploadProperties.getPreImgUrl() + currentPath + fileName;
            try {
                file.transferTo(dest);
                resMap.put("oldFileName", fileName);
                resMap.put("url", url);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("存储本地服务器 error...." + e.toString());
        }
        response.setCode(200);
        response.setMsg("pass");
        return response;
    }

    /**
     * BsinPaas 文件上传接口 通过 storeMethod 选择 1.IPFS存储： storeMethod = 1 需要同时指定 backup 存储平台 2.aliOSS存储：
     * storeMethod = 2 3.服务器本地存储： storeMethod = 4 4.both IPFS and aliOSS: storeMethod = 3 5.both IPFS
     * and 服务器本地存储: storeMethod = 5
     *
     * @param file
     * @param relativePath: 存储相对路径
     * @param storeMethod： 存储平台方式
     * @param tenantAppType： 软件平台
     * @return
     */
    @PostMapping(value = "/bsinUpload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @ApiDoc(desc = "bsinUpload")
    public BsinResultEntity bsinFileUpload(
            @RequestParam(value = "file", required = true) MultipartFile file,
            @RequestParam(required = false) String relativePath,
            @RequestParam(required = false) String storeMethod,
            @RequestParam(required = false) String tenantAppType,
            @RequestParam(required = false) String thumbnailSize, // 缩略图尺寸
            @RequestParam(required = false) String imgSize, // 尺寸
            HttpServletRequest request)
            throws IOException {
        if (file.isEmpty()) {
            throw new BusinessException(ResponseCode.UPLOAD_PICTURE_NOT_EMPTY);
        }
        String token = request.getHeader("Authorization");
        JWT jwt = JWTUtil.parseToken(token);
        String tenantId = (String) jwt.getPayload("tenantId");
        String customerNo = (String) jwt.getPayload("customerNo");
        String merchantNo = (String) jwt.getPayload("merchantNo");

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return BsinResultEntity.ok();
        }

        // 根据用户类型判断用户属于哪个平台
        String dev = "bigan";
        if (tenantAppType != null) {
            dev = tenantAppType;
        }

        String tmpRelativePath = "";
        if (StringUtil.isNotEmpty(tenantId)) {
            tmpRelativePath += tenantId + "/";
        }
        if (StringUtil.isNotEmpty(merchantNo)) {
            tmpRelativePath += merchantNo + "/";
        }
        if (StringUtil.isNotEmpty(customerNo)) {
            tmpRelativePath += customerNo + "/";
        }
        if (StringUtil.isNotEmpty(relativePath)) {
            relativePath = tmpRelativePath + relativePath + "/";
        } else {
            relativePath = tmpRelativePath;
        }

        // 上传文件到服务器
        Map<String, Object> resMap = new HashMap<String, Object>();
        String url = null;
        String localPath = ""; // 不能为空
        String localUrl = null;
        String thUrl = null;
        String ipfsUrl = null;
        String newFileName = null;
        String ipfsHash = null;

        if (storeMethod == null) {
            storeMethod = "2";
        }
        int storeMethodInt = Integer.parseInt(storeMethod);

        String absolutePath = dev + "/" + relativePath;

        // IPFS存储
        if ((storeMethodInt & 0x01) == 0x01) {
            dev = "bigan-ipfs";
            if (tenantAppType != null) {
                dev = tenantAppType + "-ipfs";
            }
            // OSS path
            absolutePath = dev + "/" + relativePath;
            // /开头，绝对路径
            String ipfsAbsolutePath = "/" + dev + "/" + relativePath;
            byte[] fileByte = file.getBytes();
            // 上传文件到ipfs----会有 java.lang.RuntimeException: ipfsadmin.s11edao.com 异常，忽略
            JSONObject result = bsinIpfsService.ipfsAdd(fileByte, fileName);
            try {
                // 检查当前文件夹是否存在-不存在则创建
                String hashDir = bsinIpfsService.fileStat(ipfsAbsolutePath).get("Hash").toString();
                log.info("fileStat: ", hashDir);
                // 移植到一个目录文件夹
                bsinIpfsService.ipfdCP(result.get("Hash").toString(), ipfsAbsolutePath, fileName);
            } catch (Exception e) {
                // TODO 目录移植失败不影响使用，目录下文件存在则会抛出异常
                System.out.println("ipfdCP error...." + e.toString());
            }
            ipfsUrl = (String) result.get("fileUrl");
            ipfsHash = (String) result.get("fileHash");
        }
        // aliOSS 和 localServer 二选一
        if ((storeMethodInt & 0x02) == 0x02) {
            FileInfo fileInfo;
            if ((storeMethodInt & 0x01) == 0x01) {
                // ipfs使用原文件名存储
                fileInfo =
                        fileStorageService.of(file).setPath(absolutePath).setSaveFilename(fileName).upload();
            } else {
                int imgWidth;
                int imgHeight;
                int thumbnailWidth;
                int thumbnailHeight;
                if (imgSize != null) {
                    String[] temp;
                    temp = imgSize.split(",");
                    imgWidth = Integer.parseInt(temp[0]);
                    imgHeight = Integer.parseInt(temp[1]);
                } else {
                    imgWidth = 0;
                    imgHeight = 0;
                }
                if (thumbnailSize != null) {
                    String[] temp;
                    temp = thumbnailSize.split(",");
                    thumbnailWidth = Integer.parseInt(temp[0]);
                    thumbnailHeight = Integer.parseInt(temp[1]);
                } else {
                    thumbnailWidth = 0;
                    thumbnailHeight = 0;
                }
                if (imgWidth != 0 && thumbnailWidth != 0) {
                    fileInfo =
                            fileStorageService
                                    .of(file)
                                    .setPath(absolutePath)
                                    .image(img -> img.size(imgWidth, imgHeight)) // 将图片大小调整到
                                    .thumbnail(th -> th.size(thumbnailWidth, thumbnailHeight)) // 再生成一张  的缩略图
                                    .upload();
                } else if (imgWidth != 0 && thumbnailWidth == 0) {
                    fileInfo =
                            fileStorageService
                                    .of(file)
                                    .setPath(absolutePath)
                                    .image(img -> img.size(imgWidth, imgHeight)) // 将图片大小调整到
                                    .upload();
                } else if (imgWidth == 0 && thumbnailWidth != 0) {
                    fileInfo =
                            fileStorageService
                                    .of(file)
                                    .setPath(absolutePath)
                                    .thumbnail(th -> th.size(thumbnailWidth, thumbnailHeight)) // 再生成一张  的缩略图
                                    .upload();
                } else {
                    fileInfo = fileStorageService.of(file).setPath(absolutePath).upload();
                }
            }
            url = fileInfo.getUrl();
            thUrl = fileInfo.getThUrl();
            newFileName = fileInfo.getFilename();
        }
        Map<String, String> localResp = new HashMap<String, String>();
        if ((storeMethodInt & 0x04) == 0x04) {
            localResp = localServerStore(file, absolutePath);
            if (localResp.get("code").equals("000000")) {
                localPath = localResp.get("localPath");
                localUrl = localResp.get("localUrl");
            } else {
                return BsinResultEntity.fail(localResp.get("message"));
            }
        }
        resMap.put("oldFileName", fileName);
        resMap.put("newFileName", newFileName);
        resMap.put("url", url);
        resMap.put("localPath", localPath);
        resMap.put("localUrl", localUrl);
        resMap.put("ipfsUrl", ipfsUrl);
        resMap.put("ipfsHash", ipfsHash);
        resMap.put("thUrl", thUrl); // 缩略图url
        return BsinResultEntity.ok(resMap);
    }

    private Map<String, String> localServerStore(MultipartFile file, String relativePath) {
        Map<String, String> resMap = new HashMap<String, String>();
        String localPath = uploadProperties.getUploadPath() + relativePath;
        try {
            Path path = Paths.get(localPath);
            Path pathCreate = Files.createDirectories(path);
            String fileName = file.getOriginalFilename();
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            File dest = new File(localPath + fileName);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            String url = uploadProperties.getPreImgUrl() + relativePath + fileName;
            resMap.put("localUrl", url);
            resMap.put("localPath", localPath + fileName);
            try {
                file.transferTo(dest);
            } catch (IOException e) {
                e.printStackTrace();
                resMap.put("code", "100000");
                resMap.put("message", e.toString());
                return resMap;
            }
            resMap.put("code", "000000");
            return resMap;
        } catch (Exception e) {
            System.out.println("存储本地服务器 error...." + e.toString());
            resMap.put("code", "100000");
            resMap.put("message", e.toString());
        }
        return resMap;
    }

    /**
     * webFlux uploadFile.
     * @param file  the file you upload
     * @return  response
     */
    @PostMapping(value = "/webFluxSingle", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @ApiDoc(desc = "webFluxSingle")
    public Mono<String> webFluxSingle(@RequestPart("file") final FilePart file) {
        System.out.println(file.filename());
        return Mono.just(file.filename());
    }

    /**
     * webFlux uploadFiles.
     * @param files  the file you upload
     * @return response
     */
    @PostMapping(value = "/webFluxFiles", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @ApiDoc(desc = "webFluxFiles")
    public Mono<String> webFluxFiles(@RequestPart(value = "files", required = false) final Flux<FilePart> files) {
        return files.map(FilePart::filename).collect(Collectors.joining(","));
    }

}
