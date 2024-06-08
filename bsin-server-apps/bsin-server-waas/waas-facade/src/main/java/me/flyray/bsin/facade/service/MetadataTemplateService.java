package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.MetadataTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author bolei
 * @date 2023/8/13
 * @desc
 */

public interface MetadataTemplateService {

    /**
     * 添加
     */
    public MetadataTemplate add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 修改
     */
    public void edit(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public List<MetadataTemplate> getList(Map<String, Object> requestMap);

    /**
     * 分页查询
     */
    public IPage<MetadataTemplate> getPageList(Map<String, Object> requestMap);

    /**
     * 查询详情
     */
    public MetadataTemplate getDetail(Map<String, Object> requestMap);

}
