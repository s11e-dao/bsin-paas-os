package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.entity.DisInviteRelation;

/**
 * 邀请关系表 Mapper 接口
 * @TableName crm_dis_invite_relation
 * @author author
 */
@Repository
@Mapper
public interface CustomerInviteRelationMapper extends BaseMapper<DisInviteRelation> {}
