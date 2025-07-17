package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.entity.DisInviteRelation;
import me.flyray.bsin.domain.entity.SysAgent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
* @description 针对表【crm_dis_invite_relation(邀请关系表)】的数据库操作Mapper
* @createDate 2024-10-25 17:14:05
* @Entity generator.domain.DisInviteRelation
*/
@Repository
@Mapper
public interface DisInviteRelationMapper extends BaseMapper<DisInviteRelation> {

    SysAgent selectSysAgent(@Param("customerNo") String customerNo);

    IPage<CustomerBase> selectSysAgentCustormerPageList(@Param("page") IPage<?> page, @Param("sysAgentNo") String sysAgentNo);

    IPage<CustomerBase> selectMyInviteCustormerPageList(@Param("page") IPage<?> page, @Param("customerNo") String customerNo);

}




