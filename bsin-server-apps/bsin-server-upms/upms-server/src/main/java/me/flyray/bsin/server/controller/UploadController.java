package me.flyray.bsin.server.controller;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.utils.BsinResultEntity;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.apache.shenyu.plugin.api.result.DefaultShenyuEntity;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/upload")
@ShenyuSpringMvcClient("/upload/**")
@ApiModule(value = "upload")
public class UploadController {

    @Autowired
    private FileStorageService fileStorageService;

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

}
