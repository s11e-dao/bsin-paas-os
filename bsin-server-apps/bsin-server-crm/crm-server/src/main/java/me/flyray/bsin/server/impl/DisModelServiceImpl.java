package me.flyray.bsin.server.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.flyray.bsin.facade.service.DisModelService;

import java.util.Map;

/**
* @author rednet
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

    @Override
    @ApiDoc(desc = "update")
    @ShenyuDubboClient("/update")
    public DisModel update(Map<String, Object> requestMap) {
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

