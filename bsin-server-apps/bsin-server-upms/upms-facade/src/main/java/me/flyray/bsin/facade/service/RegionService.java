package me.flyray.bsin.facade.service;


import org.springframework.validation.annotation.Validated;

import java.util.List;

import me.flyray.bsin.domain.entity.SysRegion;
import me.flyray.bsin.domain.response.RegionTree;

/**
 * @author ：bolei
 * @date ：Created in 2022/3/28 21:28
 * @description：行政区域服务
 */

@Validated
public interface RegionService {

    /**
     * 添加应用
     */
    SysRegion add(SysRegion region);

    /**
     * 删除应用
     */
    void delete(String regionId);

    /**
     * 编辑应用
     */
    SysRegion edit(SysRegion region);

    /**
     * 根据父级code查询子节点，子区域列表
     */
    List<SysRegion> getSubNodeList(String code);

    /**
     * 根据父级code查询子节点，树形目录
     */
    List<RegionTree> getSubNodeTree();

    public List<SysRegion> getAllList();

    /**
     * 查询顶级行政机构
     */
    List<SysRegion> getTopLayerRegions();

    /**
     * 查询详情
     */
    SysRegion getRegionById(String id);


    /**
     * 树形目录(一级二级)
     */
    List<RegionTree> getProvinceAndCityNodeTree();

}
