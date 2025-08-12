package me.flyray.bsin.server.impl;



import me.flyray.bsin.domain.entity.SysRegion;
import me.flyray.bsin.domain.response.RegionTree;
import me.flyray.bsin.facade.service.RegionService;
import me.flyray.bsin.infrastructure.mapper.RegionMapper;
import me.flyray.bsin.server.biz.RegionBiz;
import me.flyray.bsin.utils.BsinSnowflake;

import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ：bolei
 * @date ：Created in 2022/3/28 22:37
 * @description：行政区域服务
 * @modified By：
 */
@ShenyuDubboService(path ="/region",timeout = 15000)
@ApiModule(value = "region")
@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionMapper regionMapper;
    @Autowired
    private RegionBiz regionBiz;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public SysRegion add(SysRegion region) {
        String id = BsinSnowflake.getId();
        region.setRegionId(id);
        regionMapper.insert(region);
        return region;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(String regionId) {
        regionMapper.deleteById(regionId);
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public SysRegion edit(SysRegion region) {
        regionMapper.updateById(region);
        return region;
    }

    @ApiDoc(desc = "getSubNodeList")
    @ShenyuDubboClient("/getSubNodeList")
    @Override
    public List<SysRegion> getSubNodeList(String code) {
        return regionMapper.selectSubNodeList(code);
    }

    @ApiDoc(desc = "getTopLayerRegions")
    @ShenyuDubboClient("/getTopLayerRegions")
    @Override
    public List<SysRegion> getTopLayerRegions() {
        return regionMapper.selectTopLayerList();
    }

    @ApiDoc(desc = "getSubNodeTree")
    @ShenyuDubboClient("/getSubNodeTree")
    @Override
    public List<RegionTree> getSubNodeTree() {
        //等级行政区域
        List<SysRegion> regionList = regionMapper.selectAllList();
        return regionBiz.buildRegionTree(regionList);
    }

    @ApiDoc(desc = "getAllList")
    @ShenyuDubboClient("/getAllList")
    @Override
    public List<SysRegion> getAllList() {
        //等级行政区域
        List<SysRegion> regionList = regionMapper.selectAllList();
        return regionList;
    }

    @ApiDoc(desc = "getRegionById")
    @ShenyuDubboClient("/getRegionById")
    @Override
    public SysRegion getRegionById(String id) {
        return regionMapper.selectRegionById(id);
    }

    @ApiDoc(desc = "getProvinceAndCityNodeTree")
    @ShenyuDubboClient("/getProvinceAndCityNodeTree")
    @Override
    public List<RegionTree> getProvinceAndCityNodeTree() {
        List<SysRegion> regionList = regionMapper.selectProviceAndCityList();
        return regionBiz.buildRegionTree(regionList);
    }
}
