package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.MetadataFile;
import me.flyray.bsin.domain.enums.FileType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.MetadataFileService;
import me.flyray.bsin.infrastructure.mapper.MetadataFileMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author bolei
 * @date 2023/6/26 15:18
 * @desc
 */


@Slf4j
@ShenyuDubboService(path = "/metadataFile", timeout = 6000)
@ApiModule(value = "metadataFile")
@Service
public class MetadataFileServiceImpl implements MetadataFileService {

    @Autowired
    private MetadataFileMapper metadataFileMapper;

    @ShenyuDubboClient("/makeDirectory")
    @ApiDoc(desc = "makeDirectory")
    @Override
    public Map<String, Object> makeDirectory(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String merchantNo = loginUser.getMerchantNo();
        MetadataFile metadataFile = BsinServiceContext.getReqBodyDto(MetadataFile.class, requestMap);
        String serialNo = BsinSnowflake.getId();
        metadataFile.setSerialNo(serialNo);
        //文件类型： 6
        metadataFile.setFileType(FileType.FOLDER.getCode());
        //目录：1 文件： 0
        metadataFile.setDirFlag("1");
        // 文件夹也会有 ipfs 路径，这里可以填充为 服务器uri
        // nftMetadata.setFileAddress("https://ipfs.flyray.me/ipfs/QmWFkcDwJ2AcGVLbbfYb8TL1hGkwMsNG3hymXYNyhwv3oG");
        metadataFile.setTenantId(loginUser.getTenantId());
        metadataFile.setMerchantNo(loginUser.getMerchantNo());
        metadataFile.setCreateBy(merchantNo);
        metadataFileMapper.insert(metadataFile);
        return RespBodyHandler.setRespBodyDto(metadataFile);
    }

    @ShenyuDubboClient("/uploadFile")
    @ApiDoc(desc = "uploadFile")
    @Override
    public Map<String, Object> uploadFile(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String merchantNo = loginUser.getMerchantNo();
        MetadataFile metadataFile = BsinServiceContext.getReqBodyDto(MetadataFile.class, requestMap);
        String parentNo = (String) requestMap.get("parentNo");
        if (parentNo == null){
            parentNo = "0";
        }
        // 判断不能添加重复tokenId的元数据多媒体资源
        LambdaQueryWrapper<MetadataFile> warapper = new LambdaQueryWrapper<>();
        MetadataFile oldMetadataFile = metadataFileMapper.getByFolderNoAndTokenId(parentNo,metadataFile.getTokenId());
        if (oldMetadataFile != null){
            throw new BusinessException(ResponseCode.TOKEN_ID_METADATA_IMAGE_HAS_EXISTS);
        }
        //非文件夹类型需要 fileUrl ！= null
        if (!metadataFile.getFileType().equals("6")){
            if (metadataFile.getIpfsUrl() == null){
                throw new BusinessException("999999", "ipfsUrl is null");
            }
        }
        metadataFile.setParentNo(parentNo);
        metadataFile.setDirFlag("0");
        metadataFile.setTenantId(loginUser.getTenantId());
        metadataFile.setMerchantNo(loginUser.getMerchantNo());
        metadataFile.setCreateBy(merchantNo);
        String serialNo = BsinSnowflake.getId();
        metadataFile.setSerialNo(serialNo);
//        从请求参数中获取
//        metadataFile.setFileType(FileType.FOLDER.getCode());
        metadataFile.setDirFlag("0");
        metadataFileMapper.insert(metadataFile);
        return RespBodyHandler.setRespBodyDto(metadataFile);
    }


    @ShenyuDubboClient("/delete")
    @ApiDoc(desc = "delete")
    @Override
    public Map<String, Object> delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        metadataFileMapper.deleteById(serialNo);
        return RespBodyHandler.RespBodyDto();
    }

    @ShenyuDubboClient("/edit")
    @ApiDoc(desc = "edit")
    @Override
    public Map<String, Object> edit(Map<String, Object> requestMap) {
        MetadataFile metadataFile = BsinServiceContext.getReqBodyDto(MetadataFile.class, requestMap);
        metadataFileMapper.updateById(metadataFile);
        return RespBodyHandler.RespBodyDto();
    }

    @ShenyuDubboClient("/getFileList")
    @ApiDoc(desc = "getFileList")
    @Override
    public Map<String, Object> getFileList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String tenantId = loginUser.getTenantId();
        String merchantNo = loginUser.getMerchantNo();
        String parentNo = (String) requestMap.get("serialNo");
        if (StringUtils.isBlank(parentNo)){
            parentNo = "0";
        }
        LambdaQueryWrapper<MetadataFile> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(MetadataFile::getCreateTime);
        warapper.eq(ObjectUtil.isNotNull(tenantId),MetadataFile::getTenantId, tenantId);
        warapper.eq(ObjectUtil.isNotNull(merchantNo),MetadataFile::getMerchantNo, merchantNo);
        warapper.eq(MetadataFile::getParentNo, parentNo);
        List<MetadataFile> metadataFiles = metadataFileMapper.selectList(warapper);
        return RespBodyHandler.setRespBodyListDto(metadataFiles);
    }

    @ShenyuDubboClient("/getList")
    @ApiDoc(desc = "getList")
    @Override
    public Map<String, Object> getList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        LambdaUpdateWrapper<MetadataFile> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(MetadataFile::getCreateTime);
        warapper.eq(ObjectUtil.isNotNull(loginUser.getTenantId()),MetadataFile::getTenantId, loginUser.getTenantId());
        warapper.eq(ObjectUtil.isNotNull(loginUser.getMerchantNo()),MetadataFile::getMerchantNo, loginUser.getMerchantNo());
        List<MetadataFile> metadataFileList = metadataFileMapper.selectList(warapper);
        return RespBodyHandler.setRespBodyListDto(metadataFileList);
    }

    @ShenyuDubboClient("/getPageList")
    @ApiDoc(desc = "getPageList")
    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        MetadataFile metadataFile = BsinServiceContext.getReqBodyDto(MetadataFile.class, requestMap);
        Pagination pagination = (Pagination) requestMap.get("pagination");
        Page<MetadataFile> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<MetadataFile> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(MetadataFile::getCreateTime);
        warapper.eq(ObjectUtil.isNotNull(loginUser.getTenantId()),MetadataFile::getTenantId, loginUser.getTenantId());
        warapper.eq(ObjectUtil.isNotNull(loginUser.getMerchantNo()),MetadataFile::getMerchantNo, loginUser.getMerchantNo());
        IPage<MetadataFile> pageList = metadataFileMapper.selectPage(page,warapper);
        return RespBodyHandler.setRespPageInfoBodyDto(pageList);
    }

    @ShenyuDubboClient("/getDetail")
    @ApiDoc(desc = "getDetail")
    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        MetadataFile metadataFile = metadataFileMapper.selectById(serialNo);
        return RespBodyHandler.setRespBodyDto(metadataFile);
    }

}
