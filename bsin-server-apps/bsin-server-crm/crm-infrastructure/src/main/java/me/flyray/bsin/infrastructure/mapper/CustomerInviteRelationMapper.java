package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.domain.CustomerInviteRelation;

/**
 * <p>
 * 邀请关系表 Mapper 接口
 * </p>
 *
 * @author author
 */

@Repository
@Mapper
public interface CustomerInviteRelationMapper extends BaseMapper<CustomerInviteRelation> {

}