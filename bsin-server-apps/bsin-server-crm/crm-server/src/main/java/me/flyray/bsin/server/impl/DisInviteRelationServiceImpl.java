package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.entity.DisInviteRelation;
import me.flyray.bsin.domain.entity.SysDict;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DisInviteRelationService;
import me.flyray.bsin.infrastructure.mapper.DisInviteRelationMapper;
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

import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
     * 邀请关系详情
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

    /**
     * 我的客户数据统计
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "inviteStatistics")
    @ShenyuDubboClient("/inviteStatistics")
    @Override
    public Map<String, Integer> inviteStatistics(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Map<String, Integer> map = new HashMap<>();

        // 查询今天的邀请数
        LambdaQueryWrapper<DisInviteRelation> warapper = new LambdaQueryWrapper<>();
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // 获取今天的开始时间 (00:00:00)
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX); // 获取今天的结束时间 (23:59:59.999999999)
        Instant startInstant = startOfDay.atZone(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endOfDay.atZone(ZoneId.systemDefault()).toInstant();
        // 将 Instant 转换为 Date
        Date startDate = Date.from(startInstant);
        Date endDate = Date.from(endInstant);
        warapper.between(DisInviteRelation::getCreateTime, startDate, endDate);
        warapper.eq(DisInviteRelation::getParentNo, loginUser.getCustomerNo());
        Long todayInvite = disInviteRelationMapper.selectCount(warapper);
        map.put("todayInvite", todayInvite.intValue());

        // 查询昨天的邀请数
        LambdaQueryWrapper<DisInviteRelation> yesterdayWarapper = new LambdaQueryWrapper<>();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfYesterday = yesterday.atStartOfDay();
        LocalDateTime endOfYesterday = yesterday.atTime(LocalTime.MAX);

        Instant startInstantYesterday = startOfYesterday.atZone(ZoneId.systemDefault()).toInstant();
        Instant endInstantYesterday = endOfYesterday.atZone(ZoneId.systemDefault()).toInstant();
        // 将 Instant 转换为 Date
        Date startYesterday = Date.from(startInstantYesterday);
        Date endYesterday = Date.from(endInstantYesterday);

        yesterdayWarapper.between(DisInviteRelation::getCreateTime, startYesterday, endYesterday);
        yesterdayWarapper.eq(DisInviteRelation::getParentNo, loginUser.getCustomerNo());
        Long yesterdayInvite = disInviteRelationMapper.selectCount(yesterdayWarapper);
        map.put("yesterdayInvite", yesterdayInvite.intValue());

        // 查询总的邀请数
        LambdaQueryWrapper<DisInviteRelation> totalWarapper = new LambdaQueryWrapper<>();
        totalWarapper.eq(DisInviteRelation::getParentNo, loginUser.getCustomerNo());
        Long totalInvite = disInviteRelationMapper.selectCount(totalWarapper);
        map.put("totalInvite", totalInvite.intValue());
        return map;
    }

    /**
     * 查询代理商下面的客户列表
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getSysAgentCustormerPageList")
    @ShenyuDubboClient("/getSysAgentCustormerPageList")
    @Override
    public IPage<?> getSysAgentCustormerPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<CustomerBase> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        IPage<CustomerBase> pageList = disInviteRelationMapper.selectSysAgentCustormerPageList(page, loginUser.getBizRoleTypeNo());
        return pageList;
    }

    /**
     * 查询我邀请的客户列表
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getMyInviteCustormerPageList")
    @ShenyuDubboClient("/getMyInviteCustormerPageList")
    @Override
    public IPage<?> getMyInviteCustormerPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<CustomerBase> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        IPage<CustomerBase> pageList = disInviteRelationMapper.selectMyInviteCustormerPageList(page, loginUser.getBizRoleTypeNo());
        return pageList;
    }


    /**
     * 获取分销角色和等级及分佣比列数据
     * @param requestMap
     * @return
     */
    public List<?> getDistributionRoleAndRateList(Map<String, Object> requestMap) {
        return null;
    }


}




