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
     * 1、没有邀请关系直接组建团队成为老板代理商
     * 2、根据邀请关系加入代理商团队：邀请关系中有一个邀请人的代理商（可能是直接邀请人、或直接邀请人的上级代理商）
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
        System.out.println(tenantId);
        DisModel disModel = disModelMapper.selectById(tenantId);
        System.out.println(disModel);
        // 查询分销配置信息
        if (disModel == null) {
            throw new BusinessException(DIS_MODEL_NOT_EXISTS);
        }
        // 根据sysAgentNo查询代理信息
        SysAgent agent = SysAgentMapper.selectById(sysAgentNo);
        if (agent == null) {
            throw new BusinessException(SYS_AGENT_NOT_EXISTS);
        }

        // 查询邀请关系信息
        DisInviteRelation inviteRelation = disInviteRelationMapper.selectOne(
                new LambdaQueryWrapper<DisInviteRelation>()
                        .eq(DisInviteRelation::getCustomerNo, customerNo)
        );
        // 1、没有邀请关系直接组建团队成为老板代理商
        if (inviteRelation == null) {
            disTeamRelation.setDisAgentType(DisAgentType.BOSS.getCode());
            disTeamRelation.setSysAgentNo(agent.getSerialNo());
            disTeamRelation.setSerialNo(BsinSnowflake.getId());
            disTeamRelation.setTenantId(tenantId);

            // 插入分销团队关系到数据库
            disTeamRelationMapper.insert(disTeamRelation);
            return disTeamRelation;
        }
        // 根据邀请关系找到邀请人身份
        String parentCustomerNo = inviteRelation.getParentNo();

        // 查询(邀请人)父级客户身份信息
        CustomerIdentity parentIdentity = customerIdentityMapper.selectOne(
                new LambdaQueryWrapper<CustomerIdentity>()
                        .eq(CustomerIdentity::getCustomerNo, parentCustomerNo)
                        .eq(CustomerIdentity::getBizRoleType, BizRoleType.SYS_AGENT.getCode())
        );
        String parentSysAgentNo = null;
        // 2、根据邀请关系加入代理商团队：邀请关系中有一个邀请人的代理商（可能是直接邀请人、或直接邀请人的上级代理商）
        DisTeamRelation parantDisTeamRelation = disTeamRelationMapper.selectOne(
                new LambdaQueryWrapper<DisTeamRelation>()
                        .eq(DisTeamRelation::getSysAgentNo, parentSysAgentNo));
        // 判断(邀请人)父级是否是代理商，如果是则加入团队
        if(parentIdentity != null){
            parentSysAgentNo = parentIdentity.getBizRoleTypeNo();

            if (parantDisTeamRelation != null) {
                disTeamRelation.setPrarentSysAgentNo(parentSysAgentNo);
                disTeamRelation.setDisAgentType(DisAgentType.DISTRIBUTOR.getCode());
                disTeamRelation.setSysAgentNo(agent.getSerialNo());
                disTeamRelation.setSerialNo(BsinSnowflake.getId());
                disTeamRelation.setTenantId(tenantId);
                // 插入分销团队关系到数据库
                disTeamRelationMapper.insert(disTeamRelation);
            }
        }else {
            // 3、如果有邀请关系，并且邀请人不是代理商，则自己是老板
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
            // 如果(邀请人)父类不是老板，并且是代理商
            if (parantDisTeamRelation != null && !DisAgentType.BOSS.getCode().equals(parantDisTeamRelation.getDisAgentType())) {
                // 查询该用户的上级是否已经大于链动人数
                LambdaQueryWrapper<DisTeamRelation> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(DisTeamRelation::getPrarentSysAgentNo, parentSysAgentNo);
                List<DisTeamRelation> list = disTeamRelationMapper.selectList(wrapper);
                // 超过链动人数，则父级代理商进行走人并贡献两人给父级的上级
                if (list.size() == disModel.getQuitCurrentLimit()) {
                    DisTeamRelation topDisTeamRelation = disTeamRelationMapper.selectOne(
                            new LambdaQueryWrapper<DisTeamRelation>()
                                    .eq(DisTeamRelation::getPrarentSysAgentNo, parantDisTeamRelation.getPrarentSysAgentNo())
                    );
                    if (topDisTeamRelation == null) {
                        return disTeamRelation;
                    }
                    // 贡献两个人: 将父级代理的下级改为父级上级的下级
                    for (DisTeamRelation item : list) {
                        item.setPrarentSysAgentNo(topDisTeamRelation.getSysAgentNo());
                        disTeamRelationMapper.updateById(item);

                    }
                    // 父级走人成为老板，该用户改为老板身份
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




