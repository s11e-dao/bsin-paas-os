package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.MetadataTemplate;

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
    public Map<String, Object> add(Map<String, Object> requestMap);

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
    public IPage<MetadataTemplate> getPageList(Map<String, Object> requestMap);

    /**
     * 查询详情
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);

}
