package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.DisInviteRelation;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DisInviteRelationService;
import me.flyray.bsin.infrastructure.mapper.DisInviteRelationMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.*;

/**
* @author bolei
* @description 针对表【crm_dis_invite_relation(邀请关系表)】的数据库操作Service实现
* @createDate 2024-10-25 17:14:05
*/
@Slf4j
@ShenyuDubboService(path = "/disInviteRelation", timeout = 6000)
@ApiModule(value = "disInviteRelation")
@Service
public class DisInviteRelationServiceImpl implements DisInviteRelationService {

    @Autowired
    private DisInviteRelationMapper disInviteRelationMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public DisInviteRelation add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisInviteRelation disInviteRelation = BsinServiceContext.getReqBodyDto(DisInviteRelation.class, requestMap);
        disInviteRelation.setTenantId(loginUser.getTenantId());
        disInviteRelation.setSerialNo(BsinSnowflake.getId());
        disInviteRelationMapper.insert(disInviteRelation);
        return disInviteRelation;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        if (disInviteRelationMapper.deleteById(serialNo) == 0){
            throw new BusinessException(INVITE_RELATION_NOT_EXISTS);
        }
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public DisInviteRelation edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisInviteRelation disInviteRelation = BsinServiceContext.getReqBodyDto(DisInviteRelation.class, requestMap);
        disInviteRelation.setTenantId(loginUser.getTenantId());
        if (disInviteRelationMapper.updateById(disInviteRelation) == 0){
            throw new BusinessException(INVITE_RELATION_NOT_EXISTS);
        }
        return disInviteRelation;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<DisInviteRelation> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        DisInviteRelation disInviteRelation = BsinServiceContext.getReqBodyDto(DisInviteRelation.class, requestMap);
        LambdaQueryWrapper<DisInviteRelation> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(DisInviteRelation::getCreateTime);
        IPage<DisInviteRelation> pageList = disInviteRelationMapper.selectPage(page, warapper);
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
    public DisInviteRelation getDetail(Map<String, Object> requestMap){
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        DisInviteRelation disInviteRelation = disInviteRelationMapper.selectById(serialNo);
        return disInviteRelation;
    }

}




