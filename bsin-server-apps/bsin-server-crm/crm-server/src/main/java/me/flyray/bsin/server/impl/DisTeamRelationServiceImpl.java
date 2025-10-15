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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private SysAgentMapper sysAgentMapper;
    @Autowired
    private CustomerIdentityMapper customerIdentityMapper;
    @Autowired
    private DisInviteRelationMapper disInviteRelationMapper;
    @Autowired
    private DisModelMapper disModelMapper;


    /**
     * 添加分销团队关系
     * 用户成为合伙人时调用
     * requestMap - >{"sysAgentNo": 合伙人ID, "tenantId":租户ID, customerNo: 用户ID}
     * 1、没有邀请关系直接组建团队成为老板合伙人
     * 2、根据邀请关系加入合伙人团队：邀请关系中直接邀请人是合伙人（直接邀请人合伙人、或直接邀请人的上级合伙人（递归选找））
     * 3、（链动2+1）加入团队后判断父级是否给上级留人并走人
     */
    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DisTeamRelation add(Map<String, Object> requestMap) {
        // 参数校验
        String customerNo = MapUtils.getString(requestMap, "customerNo");
        String sysAgentNo = MapUtils.getString(requestMap, "sysAgentNo");
        String tenantId = MapUtils.getString(requestMap, "tenantId");
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");
        
        if (ObjectUtils.isEmpty(customerNo)) {
            throw new BusinessException("999", "客户编号不能为空");
        }
        if (ObjectUtils.isEmpty(sysAgentNo)) {
            throw new BusinessException("999", "合伙人编号不能为空");
        }
        if (ObjectUtils.isEmpty(tenantId)) {
            throw new BusinessException("999", "租户ID不能为空");
        }
        
        log.info("开始处理合伙人加入团队, customerNo={}, sysAgentNo={}, tenantId={}", customerNo, sysAgentNo, tenantId);

        DisTeamRelation disTeamRelation = BsinServiceContext.getReqBodyDto(DisTeamRelation.class, requestMap);
        // 查询分销模型配置信息
        LambdaQueryWrapper<DisModel> queryWrapper = new LambdaQueryWrapper<DisModel>()
                .eq(DisModel::getTenantId, tenantId)
                .eq(ObjectUtils.isNotEmpty(merchantNo),DisModel::getMerchantNo, merchantNo);
        DisModel disModel = disModelMapper.selectOne(queryWrapper);
        if (disModel == null) {
            throw new BusinessException(DIS_MODEL_NOT_EXISTS);
        }
        // 根据sysAgentNo查询代理信息
        SysAgent agent = sysAgentMapper.selectById(sysAgentNo);
        if (agent == null) {
            throw new BusinessException(SYS_AGENT_NOT_EXISTS);
        }
        // 查询新增合伙人的邀请关系信息,邀请关系是客户id关联
        DisInviteRelation inviteRelation = disInviteRelationMapper.selectOne(
                new LambdaQueryWrapper<DisInviteRelation>()
                        .eq(DisInviteRelation::getCustomerNo, customerNo)
        );
        // 1、没有邀请关系说明没有上级, 合伙人直接组建团队成为老板合伙人,返回新建团队信息
        if (inviteRelation == null) {
            log.info("客户无邀请关系, 直接组建团队成为老板合伙人, customerNo={}", customerNo);
            disTeamRelation.setDisAgentType(DisAgentType.BOSS.getCode());
            disTeamRelation.setSysAgentNo(agent.getSerialNo());
            disTeamRelation.setSerialNo(BsinSnowflake.getId());
            disTeamRelation.setTenantId(tenantId);
            // 插入分销团队关系到数据库
            disTeamRelationMapper.insert(disTeamRelation);
            log.info("合伙人加入团队成功(老板), teamRelationNo={}", disTeamRelation.getSerialNo());
            return disTeamRelation;
        }
        // 根据邀请关系找到邀请人ID
        String parentCustomerNo = inviteRelation.getParentNo();
        // 查询邀请人是否有合伙人身份
        CustomerIdentity parentIdentity = customerIdentityMapper.selectOne(
                new LambdaQueryWrapper<CustomerIdentity>()
                        .eq(CustomerIdentity::getCustomerNo, parentCustomerNo)
                        .eq(CustomerIdentity::getBizRoleType, BizRoleType.SYS_AGENT.getCode())
        );
        String parentSysAgentNo = null;
        DisTeamRelation parentDisTeamRelation = null;

        // 先判断邀请人是否有合伙人身份
        if(parentIdentity != null){
            parentSysAgentNo = parentIdentity.getBizRoleTypeNo();
            // 有了parentSysAgentNo后再查询团队关系
            parentDisTeamRelation = disTeamRelationMapper.selectOne(
                    new LambdaQueryWrapper<DisTeamRelation>()
                            .eq(DisTeamRelation::getSysAgentNo, parentSysAgentNo));
        }

        // 2、根据邀请关系加入合伙人团队：邀请关系中直接邀请人是合伙人（直接邀请人合伙人、或直接邀请人的上级合伙人（递归选找））
        if(parentIdentity != null && parentDisTeamRelation != null){
            // 加入邀请人的团队
            disTeamRelation.setPrarentSysAgentNo(parentSysAgentNo); // 邀请人为上级合伙人
            disTeamRelation.setDisAgentType(DisAgentType.DISTRIBUTOR.getCode());
            disTeamRelation.setSysAgentNo(agent.getSerialNo());
            disTeamRelation.setSerialNo(BsinSnowflake.getId());
            disTeamRelation.setTenantId(tenantId);
            disTeamRelationMapper.insert(disTeamRelation);
        }else {
            // 3、如果有邀请关系，并且邀请人不是合伙人或者邀请人没有团队，则创建团队自己是老板
            log.info("邀请人不是合伙人或无团队, 创建独立团队, customerNo={}, parentCustomerNo={}", customerNo, parentCustomerNo);
            disTeamRelation.setDisAgentType(DisAgentType.BOSS.getCode());
            disTeamRelation.setSysAgentNo(agent.getSerialNo());
            disTeamRelation.setSerialNo(BsinSnowflake.getId());
            disTeamRelation.setTenantId(tenantId);
            // 插入分销团队关系到数据库
            disTeamRelationMapper.insert(disTeamRelation);
            log.info("合伙人加入团队成功(老板), teamRelationNo={}", disTeamRelation.getSerialNo());
            return disTeamRelation;
        }

        // 不同的分销模型做不同的处理: 链动2+1，走人和留人
        if (DisModelEnum.DIS_LEVEL21.getCode().equals(disModel.getModel()) && parentSysAgentNo != null) {
            log.info("开始检查链动2+1走人机制, parentSysAgentNo={}, quitLimit={}", parentSysAgentNo, disModel.getQuitCurrentLimit());
            // 如果邀请人还不是老板,进行链路2+1逻辑
            if (!DisAgentType.BOSS.getCode().equals(parentDisTeamRelation.getDisAgentType())) {
                // 查询邀请人的下级是否已经大于设置的链动人数
                LambdaQueryWrapper<DisTeamRelation> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(DisTeamRelation::getPrarentSysAgentNo, parentSysAgentNo);
                List<DisTeamRelation> list = disTeamRelationMapper.selectList(wrapper);
                log.info("邀请人当前直推人数={}, 走人阈值={}", list.size(), disModel.getQuitCurrentLimit());
                // 超过链动设置的人数，则邀请人变为老板并退出团队,邀请人的下级给邀请人的上级, 人数判断必须大于等于,只判断等于,获得下级的下级后会有问题
                if (list.size() >= disModel.getQuitCurrentLimit()) {
                    log.info("触发链动2+1走人机制, 晋升老板={}, 转移下级数={}", parentSysAgentNo, list.size());
                    // 邀请人名下的所有人，都给邀请人的上级
                    String grandParentNo = parentDisTeamRelation.getPrarentSysAgentNo();
                    for (DisTeamRelation item : list) {
                        item.setPrarentSysAgentNo(grandParentNo);
                        disTeamRelationMapper.updateById(item);
                    }
                    // 邀请人走人成为老板
                    parentDisTeamRelation.setDisAgentType(DisAgentType.BOSS.getCode());
                    parentDisTeamRelation.setPrarentSysAgentNo("-1");
                    disTeamRelationMapper.updateById(parentDisTeamRelation);
                    log.info("链动2+1走人机制执行完成, 新老板={}, 转移给={}", parentSysAgentNo, grandParentNo);
                }
            }
        }

        log.info("合伙人加入团队成功, teamRelationNo={}, agentType={}", disTeamRelation.getSerialNo(), disTeamRelation.getDisAgentType());
        return disTeamRelation;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        if (ObjectUtils.isEmpty(serialNo)) {
            throw new BusinessException("999", "团队关系编号不能为空");
        }
        log.info("删除团队关系, serialNo={}", serialNo);
        if (disTeamRelationMapper.deleteById(serialNo) == 0){
            throw new BusinessException("999", "团队关系不存在");
        }
        log.info("删除团队关系成功, serialNo={}", serialNo);
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DisTeamRelation edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisTeamRelation disTeamRelation = BsinServiceContext.getReqBodyDto(DisTeamRelation.class, requestMap);
        
        if (ObjectUtils.isEmpty(disTeamRelation.getSerialNo())) {
            throw new BusinessException("999", "团队关系编号不能为空");
        }
        
        log.info("更新团队关系, serialNo={}", disTeamRelation.getSerialNo());
        disTeamRelation.setTenantId(loginUser.getTenantId());
        if (disTeamRelationMapper.updateById(disTeamRelation) == 0){
            throw new BusinessException("999", "团队关系不存在");
        }
        log.info("更新团队关系成功, serialNo={}", disTeamRelation.getSerialNo());
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




