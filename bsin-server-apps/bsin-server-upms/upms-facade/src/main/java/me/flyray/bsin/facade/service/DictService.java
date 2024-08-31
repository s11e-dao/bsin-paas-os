package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import org.springframework.validation.annotation.Validated;

import java.util.List;

import javax.validation.Valid;

import me.flyray.bsin.domain.entity.SysDict;
import me.flyray.bsin.domain.entity.SysDictItem;
import me.flyray.bsin.domain.request.SysDictDTO;
import me.flyray.bsin.validate.AddGroup;

/**
 * @author ：bolei
 * @date ：Created in 2022/3/28 21:28
 * @description：数据字典服务
 */

@Validated
public interface DictService {

    /**
     *添加
     */
    @Validated(AddGroup.class)
    SysDict add(@Valid SysDict dict);

    /**
     * 删除
     * @param id
     */
    void delete(String id);

    /**
     *编辑
     */
    SysDict edit(SysDict dict);

    /**
     *分页查询
     */
    IPage<?> getPageList(SysDictDTO dictDTO);

    /**
     *通过ID查询字典信息
     */
    SysDict getDictById(String id);

    /**
     *通过字典类型查找字典
     * @return 同类型字典
     */
    List<SysDict> getDictByType(String dictType);

    /**
     * 新增字典项
     */
    SysDictItem addItem(SysDictItem dictItem);

    /**
     * 删除字典项
     */
    void deleteItem(String id);

    /**
     * 通过id查询字典项
     */
    SysDictItem getDictItemById(String id);

    /**
     * 修改字典项
     */
    SysDictItem editItem(SysDictItem dictItem);

    /**
     * 分页查询字典项集合
     */
    IPage<?> getDictItemPageList(SysDictDTO dictDTO);

    /**
     * 分页查询字典项集合
     */
    List<SysDictItem> getDictItemList(SysDict sysDict);


}
