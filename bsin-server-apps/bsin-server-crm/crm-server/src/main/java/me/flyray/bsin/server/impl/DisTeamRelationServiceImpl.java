package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DisTeamRelationService;
import me.flyray.bsin.infrastructure.mapper.*;
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

import static me.flyray.bsin.constants.ResponseCode.GRADE_NOT_EXISTS;
import static me.flyray.bsin.constants.ResponseCode.ACCOUNT_EXISTS;

/**
* @author bolei
* @description 针对表【crm_dis_team_relation(分销团队关系表)】的数据库操作Service实现
* @createDate 2024-10-25 17:14:20
*/
@Slf4j
@ShenyuDubboService(path = "/disTeamRelation", timeout = 6000)
@ApiModule(value = "disTeamRelation")
@Service
public class DisTeamRelationServiceImpl implements DisTeamRelationService {

    @Autowired
    private DisTeamRelationMapper disTeamRelationMapper;
    @Autowired
    private SysAgentMapper SysAgentMapper;
    @Autowired
    private CustomerIdentityMapper CustomerIdentityMapper;
    @Autowired
    private DisInviteRelationMapper DisInviteRelationMapper;
    @Autowired
    private DisBrokerageConfigMapper disBrokerageConfigMapper;
    /**
     * 添加分销团队关系
     *
     * 该方法主要用于处理分销团队关系的添加请求它首先检查请求中的参数，
     * 然后根据这些参数查询相关的配置和实体信息，最后在数据库中创建一个新的分销团队关系
     *
     * @param requestMap 包含请求参数的映射，包括"sysAgentNo"和"tenantId"等关键信息
     * @return 返回新创建的DisTeamRelation对象，如果请求失败或数据不合法则可能返回null
     */
    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public DisTeamRelation add(Map<String, Object> requestMap) {
        // 生成crm_sys_agent 数据后调用,requestMap ->{"sysAgentNo": 成为分销后的serial_no, "tenantId":租户ID}
        String sysAgentNo = MapUtils.getString(requestMap, "sysAgentNo");
        String tenantId = MapUtils.getString(requestMap, "tenantId");

        // 查询分销配置信息
        DisBrokerageConfig config = disBrokerageConfigMapper.selectOne(
                new LambdaQueryWrapper<DisBrokerageConfig>()
                        .eq(DisBrokerageConfig::getTenantId,  MapUtils.getString(requestMap, "tenantId"))
        );
        if (config == null){
            return null;
        }
        // 根据sysAgentNo查询代理信息
        SysAgent agent = SysAgentMapper.selectById(sysAgentNo);
        // 查询客户身份信息
        CustomerIdentity identity = CustomerIdentityMapper.selectOne(
                new LambdaQueryWrapper<CustomerIdentity>()
                        .eq(CustomerIdentity::getIdentityTypeNo, agent.getSerialNo()
                        )
        );

        // 查询邀请关系信息
        DisInviteRelation relation  = DisInviteRelationMapper.selectOne(new LambdaQueryWrapper<DisInviteRelation>()
                .eq(DisInviteRelation::getCustomerNo, identity.getCustomerNo()
                )
        );
        String parentNo = relation.getParentNo();

        // 查询父级客户身份信息
        CustomerIdentity parentIdentity = CustomerIdentityMapper.selectOne(new LambdaQueryWrapper<CustomerIdentity>()
                .eq(CustomerIdentity::getCustomerNo, parentNo
                )
        );

        // 创建分销团队关系对象
        DisTeamRelation disTeamRelation = BsinServiceContext.getReqBodyDto(DisTeamRelation.class, requestMap);
        if (parentIdentity.getIdentityTypeNo() != null){
            disTeamRelation.setPrarentSysAgentNo(parentIdentity.getIdentityTypeNo());
            disTeamRelation.setAgentType(0);
        }
        else{
            disTeamRelation.setAgentType(1);
        }
        disTeamRelation.setSysAgentNo(agent.getSerialNo());
        disTeamRelation.setSerialNo(BsinSnowflake.getId());
        disTeamRelation.setTenantId(tenantId);
        // 插入分销团队关系到数据库
        disTeamRelationMapper.insert(disTeamRelation);
        return disTeamRelation;
    }
    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        if (disTeamRelationMapper.deleteById(serialNo) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public DisTeamRelation edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisTeamRelation disTeamRelation = BsinServiceContext.getReqBodyDto(DisTeamRelation.class, requestMap);
        disTeamRelation.setTenantId(loginUser.getTenantId());
        if (disTeamRelationMapper.updateById(disTeamRelation) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
        return disTeamRelation;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<DisTeamRelation> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        DisTeamRelation disTeamRelation = BsinServiceContext.getReqBodyDto(DisTeamRelation.class, requestMap);
        LambdaQueryWrapper<DisTeamRelation> warapper = new LambdaQueryWrapper<>();
        warapper.eq(DisTeamRelation::getTenantId, loginUser.getTenantId());
        IPage<DisTeamRelation> pageList = disTeamRelationMapper.selectPage(page, warapper);
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
    public DisTeamRelation getDetail(Map<String, Object> requestMap){
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        DisTeamRelation disTeamRelation = disTeamRelationMapper.selectById(serialNo);
        return disTeamRelation;
    }









}




