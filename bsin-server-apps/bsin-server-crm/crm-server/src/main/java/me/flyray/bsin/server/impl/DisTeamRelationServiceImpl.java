package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.domain.enums.DisAgentType;
import me.flyray.bsin.domain.enums.DisModelEnum;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DisTeamRelationService;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.*;

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
    private CustomerIdentityMapper customerIdentityMapper;
    @Autowired
    private DisInviteRelationMapper disInviteRelationMapper;
    @Autowired
    private DisBrokerageConfigMapper disBrokerageConfigMapper;
    @Autowired
    private DisModelMapper disModelMapper;


    /**
     * 添加分销团队关系
     * 用户成为代理商时调用
     * requestMap - >{"sysAgentNo": 代理商ID, "tenantId":租户ID, customerNo: 用户ID}
     * 1、没有邀请关系直接组建团队成为老板代理商
     * 2、根据邀请关系加入代理商团队：邀请关系中直接邀请人是代理商（直接邀请人代理商、或直接邀请人的上级代理商（递归选找））
     * 3、（链动2+1）加入团队后判断父级是否给上级留人并走人
     */
    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public DisTeamRelation add(Map<String, Object> requestMap) {
        // 生成crm_sys_agent 数据后调用,requestMap ->{"sysAgentNo": 成为分销后的serial_no, "tenantId":租户ID}
        DisTeamRelation disTeamRelation = BsinServiceContext.getReqBodyDto(DisTeamRelation.class, requestMap);
        String customerNo = MapUtils.getString(requestMap, "customerNo");
        String sysAgentNo = MapUtils.getString(requestMap, "sysAgentNo");
        String tenantId = MapUtils.getString(requestMap, "tenantId");
        // 查询分销模型配置信息
        DisModel disModel = disModelMapper.selectById(tenantId);
        if (disModel == null) {
            throw new BusinessException(DIS_MODEL_NOT_EXISTS);
        }
        // 根据sysAgentNo查询代理信息
        SysAgent agent = SysAgentMapper.selectById(sysAgentNo);
        if (agent == null) {
            throw new BusinessException(SYS_AGENT_NOT_EXISTS);
        }
        // 查询新增代理商的邀请关系信息,邀请关系是客户id关联
        DisInviteRelation inviteRelation = disInviteRelationMapper.selectOne(
                new LambdaQueryWrapper<DisInviteRelation>()
                        .eq(DisInviteRelation::getCustomerNo, customerNo)
        );
        // 1、没有邀请关系说明没有上级, 代理商直接组建团队成为老板代理商,返回新建团队信息
        if (inviteRelation == null) {
            disTeamRelation.setDisAgentType(DisAgentType.BOSS.getCode());
            disTeamRelation.setSysAgentNo(agent.getSerialNo());
            disTeamRelation.setSerialNo(BsinSnowflake.getId());
            disTeamRelation.setTenantId(tenantId);
            // 插入分销团队关系到数据库
            disTeamRelationMapper.insert(disTeamRelation);
            return disTeamRelation;
        }
        // 根据邀请关系找到邀请人ID
        String parentCustomerNo = inviteRelation.getParentNo();
        // 查询邀请人是否有代理商身份
        CustomerIdentity parentIdentity = customerIdentityMapper.selectOne(
                new LambdaQueryWrapper<CustomerIdentity>()
                        .eq(CustomerIdentity::getCustomerNo, parentCustomerNo)
                        .eq(CustomerIdentity::getBizRoleType, BizRoleType.SYS_AGENT.getCode())
        );
        String parentSysAgentNo = null;
        // 2、根据邀请关系加入代理商团队：邀请关系中直接邀请人是代理商（直接邀请人代理商、或直接邀请人的上级代理商（递归选找））
        DisTeamRelation parantDisTeamRelation = disTeamRelationMapper.selectOne(
                new LambdaQueryWrapper<DisTeamRelation>()
                        .eq(DisTeamRelation::getSysAgentNo, parentSysAgentNo));
        // 邀请人有代理商身份并且当前代理商没有上级, 当前代理商则加入邀请人的团队
        if(parentIdentity != null && parantDisTeamRelation != null){
            parentSysAgentNo = parentIdentity.getBizRoleTypeNo();
            disTeamRelation.setPrarentSysAgentNo(parentSysAgentNo); // 邀请人为上级代理商
            disTeamRelation.setDisAgentType(DisAgentType.DISTRIBUTOR.getCode());
            disTeamRelation.setSysAgentNo(agent.getSerialNo());
            disTeamRelation.setSerialNo(BsinSnowflake.getId());
            disTeamRelation.setTenantId(tenantId);
            disTeamRelationMapper.insert(disTeamRelation);
        }else {
            // 3、如果有邀请关系，并且邀请人不是代理商或者邀请人没有团队，则创建团队自己是老板
            disTeamRelation.setDisAgentType(DisAgentType.BOSS.getCode());
            disTeamRelation.setSysAgentNo(agent.getSerialNo());
            disTeamRelation.setSerialNo(BsinSnowflake.getId());
            disTeamRelation.setTenantId(tenantId);
            // 插入分销团队关系到数据库
            disTeamRelationMapper.insert(disTeamRelation);
            return disTeamRelation;
        }

        // 不同的分销模型做不同的处理: 链动2+1，走人和留人
        if (DisModelEnum.DIS_LEVEL21.getCode().equals(disModel.getModel()) && parentSysAgentNo != null) {
            // 如果邀请人还不是老板,进行链路2+1逻辑
            if (!DisAgentType.BOSS.getCode().equals(parantDisTeamRelation.getDisAgentType())) {
                // 查询邀请人的下级是否已经大于设置的链动人数
                LambdaQueryWrapper<DisTeamRelation> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(DisTeamRelation::getPrarentSysAgentNo, parentSysAgentNo);
                List<DisTeamRelation> list = disTeamRelationMapper.selectList(wrapper);
                // 超过链动设置的人数，则邀请人变为老板并退出团队,邀请人的下级给邀请人的上级, 人数判断必须大于等于,只判断等于,获得下级的下级后会有问题
                if (list.size() >= disModel.getQuitCurrentLimit()) {
                    // 邀请人名下的所有人，都给邀请人的上级
                    for (DisTeamRelation item : list) {
                        item.setPrarentSysAgentNo(parantDisTeamRelation.getPrarentSysAgentNo());
                        disTeamRelationMapper.updateById(item);
                    }
                    // 邀请人走人成为老板
                    disTeamRelation.setDisAgentType(DisAgentType.BOSS.getCode());
                    disTeamRelation.setPrarentSysAgentNo("-1");
                    disTeamRelationMapper.updateById(disTeamRelation);
                }
            }
        }

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
     * 分销团队关系详情
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




