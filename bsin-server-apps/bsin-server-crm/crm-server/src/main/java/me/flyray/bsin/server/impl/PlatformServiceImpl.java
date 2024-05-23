package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


import me.flyray.bsin.facade.service.PlatformService;

/**
 * @author bolei
 * @date 2023/11/1
 * @desc
 */

public class PlatformServiceImpl implements PlatformService {

    @Override
    public Map<String, Object> openTenant(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> login(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> edit(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        return null;
    }
}
