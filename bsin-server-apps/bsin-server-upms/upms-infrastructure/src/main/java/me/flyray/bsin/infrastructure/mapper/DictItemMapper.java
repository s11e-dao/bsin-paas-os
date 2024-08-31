/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */
package me.flyray.bsin.infrastructure.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import me.flyray.bsin.domain.entity.SysDictItem;

/**
 * 字典项
 *
 * @author 博羸
 * @date 2019/03/19
 */

@Mapper
public interface DictItemMapper extends BaseMapper<SysDictItem> {

    public int insert(SysDictItem dictItem);

    public void deleteById(String appId);

    public IPage<SysDictItem> selectPageList(@Param("page") IPage<?> page, @Param("tenantId") String tenantId, @Param("dictType") String dictType);

    public int updateById(SysDictItem dictItem);

    List<SysDictItem> selectList(@Param("tenantId") String tenantId, String dictType);

}
