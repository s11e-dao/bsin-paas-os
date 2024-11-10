package me.flyray.bsin.oss.ipfs;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.http.entity.ContentType;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import io.ipfs.api.Multipart;
import io.ipfs.api.NamedStreamable;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;

@Service
@Slf4j
public class BsinIpfsService {

  @Value("${bsin.oss.ipfs.api}")
  private String ipfsApi;

  @Value("${bsin.oss.ipfs.gateway}")
  private String ipfsGateway;

  @Value("${bsin.oss.message.upPath}")
  private String upPath;

  @Value("${bsin.oss.message.preImgUrl}")
  private String preImgUrl;

  @Autowired private FileStorageService fileStorageService;

  /**
   * 将文件上传到ipfs服务节点上
   *
   * @param bytes
   * @param fileName
   * @return
   * @throws IOException
   */
  public JSONObject ipfsAdd(byte[] bytes, String fileName) throws IOException {

    String apiName = "/add";
    String url = ipfsApi + apiName;
    String suffixName = fileName.substring(fileName.lastIndexOf("."));
    String newUUID = UUID.randomUUID().toString().replaceAll("-", "");
    String newFileName = newUUID + suffixName;
    Multipart m = new Multipart(url, "UTF-8");
    NamedStreamable.ByteArrayWrapper filew =
        new NamedStreamable.ByteArrayWrapper(newFileName, bytes);
    m.addFilePart(fileName, Paths.get(""), filew);
    String res = m.finish();
    JSONObject result = JSONObject.parseObject(res);
    String Hash = result.get("Hash").toString();
    result.put("fileUrl", ipfsGateway + Hash);
    result.put("fileHash", Hash);
    return result;
  }

  public String mkDir(String dirpath) throws IOException {
    // http://ipfsadmin.s11edao.com/api/v0/files/mkdir?parents=true&arg=%2Fjiujiu-ipfs%2F%E4%BD%9C%E4%B8%BA
    String encodedDdirpath = URLEncoder.encode(dirpath, "UTF-8");
    String apiName = "/files/mkdir";
    String argument = "parents=true" + "&arg=" + encodedDdirpath;
    String url = ipfsApi + apiName + "?" + argument;
    System.out.println(url);
    Multipart cpm = new Multipart(url, "UTF-8");
    return cpm.finish();
  }

  public JSONObject fileStat(String currentPath) throws IOException {

    String apiName = "/files/stat";
    String argument = "arg=" + currentPath;
    String url = ipfsApi + apiName + "?" + argument;
    System.out.println(url);
    Multipart cpm = new Multipart(url, "UTF-8");
    try {
      String req = cpm.finish();
      return JSONObject.parseObject(req);
    } catch (RuntimeException e) {
      this.mkDir(currentPath);
      String req = new Multipart(url, "UTF-8").finish();
      return JSONObject.parseObject(req);
    }
  }

  public JSONObject fileLS(String hashDir) throws IOException {
    String apiName = "/ls";
    String argument = "arg=" + hashDir;
    String url = ipfsApi + apiName + "?" + argument;
    // System.out.println(url);
    Multipart cpm = new Multipart(url, "UTF-8");
    String req = cpm.finish();
    JSONObject result = JSONObject.parseObject(req);
    Object objs = result.getJSONArray("Objects").get(0);
    if (objs instanceof JSONObject) {
      JSONArray links = ((JSONObject) objs).getJSONArray("Links");
      for (Object e : links) {
        if (e instanceof JSONObject) {
          ((JSONObject) e).put("Url", ipfsGateway + ((Map) e).get("Hash").toString());
        }
      }
    }
    return result;
  }

  public void ipfdCP(String hash, String currentPath, String filename) throws IOException {
    String cpTarget = currentPath + "/" + filename;
    String apiName = "/files/cp";
    String arguments = "arg=%s&arg=%s";
    String fileHash = "/ipfs/" + hash;
    String argument = String.format(arguments, fileHash, cpTarget);
    String url = ipfsApi + apiName + "?" + argument;
    // System.out.println(url);
    Multipart cpm = new Multipart(url, "UTF-8");
    cpm.finish();
  }

  /**
   * ipfsAndServiceUpload 1.IPFS存储： storeMethod = 1 需要同时指定 backup 存储平台 2.aliOSS存储： storeMethod = 2
   * 3.服务器本地存储： storeMethod = 4 4.both IPFS and aliOSS: storeMethod = 3 5.both IPFS and 服务器本地存储:
   * storeMethod = 5
   *
   * @param file
   * @param tenantId:
   * @param relativePath: 存储相对路径
   * @param storeMethod： 存储平台方式
   * @param tenantAppType： 软件平台
   * @return
   */
  public JSONObject ipfsAndServiceUpload(
      MultipartFile file,
      String tenantId,
      String relativePath,
      String storeMethod,
      String tenantAppType)
      throws IOException {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();

    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }
    String customerNo = loginUser.getCustomerNo();
    String merchantNo = loginUser.getMerchantNo();

    if (file.isEmpty()) {
      throw new IllegalArgumentException("File is empty");
    }
    String fileName = file.getOriginalFilename();
    if (fileName == null) {
      return null;
    }
    // 根据用户类型判断用户属于哪个平台
    String dev = "bigan-ipfs";
    if (tenantAppType != null) {
      dev = tenantAppType + "-ipfs";
    }

    String tmpRelativePath = "";
    if (tenantId != null) {
      tmpRelativePath += tenantId + "/";
    }
    if (merchantNo != null) {
      tmpRelativePath += merchantNo + "/";
    }
    if (customerNo != null) {
      tmpRelativePath += customerNo + "/";
    }
    if (relativePath != null) {
      relativePath = tmpRelativePath + relativePath + "/";
    } else {
      relativePath = tmpRelativePath;
    }
    String absolutePath = dev + "/" + relativePath;

    if (storeMethod == null) {
      storeMethod = "3"; // 默认备份在aliOSS
    }
    int storeMethodInt = Integer.parseInt(storeMethod);

    // IPFS存储
    JSONObject result = new JSONObject();
    byte[] fileByte = file.getBytes();
    // 上传文件到ipfs----会有 java.lang.RuntimeException: ipfsadmin.s11edao.com 异常，忽略
    result = ipfsAdd(fileByte, fileName);
    try {
      // 检查当前文件夹是否存在-不存在则创建
      String hashDir = fileStat("/" + absolutePath).get("Hash").toString();
      log.info("fileStat: ", hashDir);
      // 移植到一个目录文件夹
      ipfdCP(result.get("Hash").toString(), relativePath, fileName);
    } catch (Exception e) {
      // TODO 目录移植失败不影响使用，目录下文件存在则会抛出异常
      System.out.println("ipfdCP error...." + e.toString());
    }

    String url = null;
    String newFileName = null;
    // aliOSS 和 localServer 二选一
    if ((storeMethodInt & 0x02) == 0x02) {
      FileInfo fileInfo = fileStorageService.of(file).setPath(absolutePath).upload();
      url = fileInfo.getUrl();
      newFileName = fileInfo.getFilename();
    } else if ((storeMethodInt & 0x04) == 0x04) {
      url = localServerStore(file, absolutePath);
    }
    result.put(url, url);
    result.put(newFileName, newFileName);

    return result;
  }

  private String localServerStore(MultipartFile file, String relativePath) {
    String localPath = upPath + relativePath;
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
      String url = preImgUrl + relativePath + fileName;
      try {
        file.transferTo(dest);
      } catch (IOException e) {
        e.printStackTrace();
        return e.toString();
      }
      return url;
    } catch (Exception e) {
      System.out.println("存储本地服务器 error...." + e.toString());
      return e.toString();
    }
  }

  public MultipartFile string2multipartFile(String content, String fileName) throws IOException {
    MultipartFile multipartFile = null;
    String tmpPath = "./";
    File tmpFile = null;
    // 创建文件目录
    File dir = new File(tmpPath);
    if (!dir.exists() && !dir.isDirectory()) {
      dir.mkdirs();
    }

    BufferedOutputStream bos = null;
    java.io.FileOutputStream fos = null;
    try {
      byte[] bytes = content.getBytes();
      tmpFile = new File(tmpPath + fileName);
      fos = new java.io.FileOutputStream(tmpFile);
      bos = new BufferedOutputStream(fos);
      bos.write(bytes);
    } catch (Exception e) {
      log.error("string to bytes failed: ", e);
    } finally {
      if (bos != null) {
        try {
          bos.close();
          FileInputStream fileInputStream = new FileInputStream(tmpFile);
          // contentType根据需求调整
          multipartFile =
              new MockMultipartFile(
                  tmpFile.getName(),
                  tmpFile.getName(),
                  ContentType.TEXT_PLAIN.toString(),
                  fileInputStream);
          tmpFile.delete();
        } catch (IOException e) {
          log.error("bytesToFile close bos failed: ", e);
        }
      }
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          log.error("bytesToFile close fos failed: ", e);
        }
      }
    }
    return multipartFile;
  }

  public String getIpfsCid(String tokenURI) {
    String tmpStr = tokenURI.substring(0, tokenURI.indexOf("/ipfs/"));
    log.info(tmpStr);
    String metadataCID = tokenURI.substring(tmpStr.length() + 6, tokenURI.length());
    log.info(metadataCID);
    return metadataCID;
  }
}
