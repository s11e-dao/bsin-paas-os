package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import me.flyray.bsin.domain.entity.SysDict;
import me.flyray.bsin.domain.entity.SysDictItem;
import me.flyray.bsin.domain.request.SysDictDTO;
import me.flyray.bsin.facade.service.DictService;
import me.flyray.bsin.infrastructure.mapper.DictItemMapper;
import me.flyray.bsin.infrastructure.mapper.DictMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.utils.BsinSnowflake;
import me.flyray.bsin.validate.QueryGroup;


/**
 * @author ：bolei
 * @date ：Created in 2022/3/28 22:38
 * @description：数据字典服务
 * @modified By：
 */
@ShenyuDubboService(path ="/dict")
@ApiModule(value = "dict")
@Service
public class DictServiceImpl implements DictService {

    @Autowired
    private DictMapper dictMapper;
    @Autowired
    private DictItemMapper dictItemMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public SysDict add(SysDict dict) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        dict.setTenantId(tenantId);
        dictMapper.insert(dict);
        return dict;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(String id) {
        dictMapper.deleteById(id);
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public SysDict edit(SysDict dict) {
        dictMapper.updateById(dict);
        return dict;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(@Validated(QueryGroup.class) SysDictDTO dictDTO) {
        String dictType = dictDTO.getDictType();
        String systemFlag = dictDTO.getSystemFlag();
        Pagination pagination = dictDTO.getPagination();
        Page<SysDict> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        IPage<SysDict> pageList = dictMapper.selectPageList(page, dictType, systemFlag);
        return pageList;
    }

    @ApiDoc(desc = "getDictById")
    @ShenyuDubboClient("/getDictById")
    @Override
    public SysDict getDictById(String id) {
        return dictMapper.selectListById(id);
    }

    @ApiDoc(desc = "getDictByType")
    @ShenyuDubboClient("/getDictByType")
    @Override
    public List<SysDict> getDictByType(String dictType) {
        return dictMapper.selectListByDictType(dictType);
    }

    @ApiDoc(desc = "addItem")
    @ShenyuDubboClient("/addItem")
    @Override
    public SysDictItem addItem(SysDictItem dictItem) {
        String id = BsinSnowflake.getId();
        dictItem.setId(id);
        dictItemMapper.insert(dictItem);
        return dictItem;
    }

    @ApiDoc(desc = "deleteItem")
    @ShenyuDubboClient("/deleteItem")
    @Override
    public void deleteItem(String id) {
        dictItemMapper.deleteById(id);
    }

    @ApiDoc(desc = "getDictItemById")
    @ShenyuDubboClient("/getDictItemById")
    @Override
    public SysDictItem getDictItemById(String id) {
        return dictItemMapper.selectById(id);
    }

    @ApiDoc(desc = "editItem")
    @ShenyuDubboClient("/editItem")
    @Override
    public SysDictItem editItem(SysDictItem dictItem) {
        dictItemMapper.updateById(dictItem);
        return dictItem;
    }

    @ApiDoc(desc = "getDictItemPageList")
    @ShenyuDubboClient("/getDictItemPageList")
    @Override
    public IPage<?> getDictItemPageList(@Validated(QueryGroup.class) SysDictDTO dictDTO) {
        String dictType = dictDTO.getDictType();
        Pagination pagination = dictDTO.getPagination();
        Page<SysDict> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        IPage<SysDictItem> pageList = dictItemMapper.selectPageList(page, dictType);
        return pageList;
    }

    @ApiDoc(desc = "getDictItemList")
    @ShenyuDubboClient("/getDictItemList")
    @Override
    public List<SysDictItem> getDictItemList(String dictType) {
        return dictItemMapper.selectList(dictType);
    }
}
