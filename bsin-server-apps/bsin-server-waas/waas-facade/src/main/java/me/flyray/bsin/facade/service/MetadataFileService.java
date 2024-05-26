package me.flyray.bsin.facade.service;


import java.util.Map;

/**
 * @author ：bolei
 * @date ：Created in 2023/06/26 16:19
 * @description：数字资产的元数据文件
 * @modified By：
 */


public interface MetadataFileService {


    /**
     * 创建文件夹
     */
    public Map<String, Object> makeDirectory(Map<String, Object> requestMap);

    /**
     * 上传文件
     */
    public Map<String, Object> uploadFile(Map<String, Object> requestMap);

    /**
     * 查询文件
     * 传了id则查询子文件
     */
    public Map<String, Object> getFileList(Map<String, Object> requestMap);


    /**
     * 删除
     */
    public Map<String, Object> delete(Map<String, Object> requestMap);

    /**
     * 修改
     */
    public Map<String, Object> edit(Map<String, Object> requestMap);


    /**
     * 租户下所有
     */
    public Map<String, Object> getList(Map<String, Object> requestMap);

    /**
     * 分页查询
     */
    public Map<String, Object> getPageList(Map<String, Object> requestMap);

    /**
     * 查询详情
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);

}
