package me.flyray.bsin.server.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.flyray.bsin.facade.service.DisModelService;

import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.*;

/**
* @author bolei
* @description 针对表【crm_dis_model(分销模型表)】的数据库操作Service实现
* @createDate 2024-10-25 17:14:10
*/
@Slf4j
@ShenyuDubboService(path = "/dismodel", timeout = 6000)
@ApiModule(value = "dismodel")
@Service
public class DisModelServiceImpl implements DisModelService {

    @Autowired
    private DisModelMapper disModelMapper;

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        if (disModelMapper.deleteById(serialNo) == 0){
            throw new BusinessException(DIS_MODEL_NOT_EXISTS);
        }
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public DisModel edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisModel disModel = BsinServiceContext.getReqBodyDto(DisModel.class, requestMap);
        disModel.setTenantId(loginUser.getTenantId());
        if (disModelMapper.updateById(disModel) == 0){
            throw new BusinessException(DIS_MODEL_NOT_EXISTS);
        }
        return disModel;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<DisModel> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        DisModel disModel = BsinServiceContext.getReqBodyDto(DisModel.class, requestMap);
        LambdaQueryWrapper<DisModel> warapper = new LambdaQueryWrapper<>();
        warapper.eq(DisModel::getTenantId, loginUser.getTenantId());
        IPage<DisModel> pageList = disModelMapper.selectPage(page, warapper);
        return pageList;
    }

    /**
     * 事件详情
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public DisModel getDetail(Map<String, Object> requestMap){
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisModel disModel = disModelMapper.selectOne(new LambdaQueryWrapper<DisModel>().eq(DisModel::getTenantId, loginUser.getTenantId()));
        return disModel;
    }

    @Override
    @ApiDoc(desc = "config")
    @ShenyuDubboClient("/config")
    public DisModel config(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        System.out.println(requestMap);

        // 将请求参数转换为 DisModel 对象
        DisModel dismodel = BsinServiceContext.getReqBodyDto(DisModel.class, requestMap);
        dismodel.setTenantId(loginUser.getTenantId());
        // 构建查询条件
        LambdaQueryWrapper<DisModel> wrapper = new LambdaQueryWrapper<DisModel>()
                .eq(DisModel::getTenantId, loginUser.getTenantId());
        // 查询记录
        DisModel existingModel = disModelMapper.selectOne(wrapper);

        if (existingModel == null) {
            // 记录不存在，插入新记录
            disModelMapper.insert(dismodel);
        } else {
            // 记录存在，更新记录
            dismodel.setTenantId(existingModel.getTenantId());
            disModelMapper.updateById(dismodel);
        }
        return dismodel;
    }


}

