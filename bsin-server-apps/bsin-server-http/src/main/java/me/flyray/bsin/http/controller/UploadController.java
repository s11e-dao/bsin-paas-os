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

import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.http.config.BsinUploadProperties;
import me.flyray.bsin.http.result.ResultBean;
import me.flyray.bsin.utils.BsinResultEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

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
    private BsinUploadProperties config;

    /**
     * webFlux uploadFile.
     * @param file  the file you upload
     * @return  response
     */
    @PostMapping(value = "/aliOssUpload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @ApiDoc(desc = "aliOssUpload")
    public BsinResultEntity aliOssUpload(@RequestPart("file") final FilePart file) {
        System.out.println(file);
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

        String localPath = config.getUploadPath() + dev + "/" + tenantId + "/" + currentPath;
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
            String url = config.getPreImgUrl() + currentPath + fileName;
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
