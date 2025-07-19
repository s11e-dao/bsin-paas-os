package me.flyray.bsin.server.biz;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.entity.DisInviteRelation;
import me.flyray.bsin.domain.entity.SysAgent;
import me.flyray.bsin.infrastructure.mapper.DisInviteRelationMapper;
import me.flyray.bsin.utils.BsinSnowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class InviteRelationBiz {

    @Autowired
    private DisInviteRelationMapper disInviteRelationMapper;

    public void addInvite(CustomerBase customerBase, CustomerBase parentCustomer, SysAgent sysAgent) {
        // 添加邀请关系
        DisInviteRelation disInviteRelation = new DisInviteRelation();
        disInviteRelation.setTenantId(customerBase.getCustomerNo());
        // 被邀请人序列号
        disInviteRelation.setCustomerNo(customerBase.getCustomerNo());
        // 父级邀请人序列号
        disInviteRelation.setParentNo(parentCustomer.getCustomerNo());
        disInviteRelation.setInviteLevel(1);
        // 邀请人合伙人
        if(sysAgent.getSerialNo() != null){
            disInviteRelation.setSysAgentNo(sysAgent.getSerialNo());
        }else {
            disInviteRelation.setSysAgentNo("-1");
        }
        addInviteRelation(disInviteRelation);
    }

    public void addInviteRelation(DisInviteRelation disInviteRelation) {
        // 查看当前邀请人是否还有父级邀请人
        LambdaQueryWrapper<DisInviteRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DisInviteRelation::getCustomerNo, disInviteRelation.getParentNo());
        // 根据时间排序，确保查出来的第一个是第一父级
        queryWrapper.orderByDesc(DisInviteRelation::getCreateTime);
        List<DisInviteRelation> inviteRelations = disInviteRelationMapper.selectList(queryWrapper);
        // 如果没有就直接将邀请关系存入
        if (inviteRelations.isEmpty()) {
            disInviteRelationMapper.insert(disInviteRelation);
        } else {
            disInviteRelationMapper.insert(disInviteRelation);
            // 添加第二、第三父级 (只有三级，所以最多只取前面2个)
            int count = 2;
            for (DisInviteRelation inviteRelation : inviteRelations) {
                if (count > 3) {
                    return;
                }
                inviteRelation.setCustomerNo(disInviteRelation.getCustomerNo());
                // 针对当前被邀请人父级等级上升
                inviteRelation.setInviteLevel(count);
                inviteRelation.setSerialNo(BsinSnowflake.getId());
                inviteRelation.setCreateTime(new Date());
                disInviteRelationMapper.insert(inviteRelation);
                count++;
            }
        }
    }

}
