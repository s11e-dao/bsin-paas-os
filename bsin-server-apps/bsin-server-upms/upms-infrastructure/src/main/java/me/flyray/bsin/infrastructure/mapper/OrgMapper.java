package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.SysOrg;
import me.flyray.bsin.domain.response.OrgResp;


@Repository
@Mapper
public interface OrgMapper extends BaseMapper<SysOrg> {

    void deleteById(String orgId);

    void insertOrg(SysOrg record);

    List<OrgResp> selectOrgList(SysOrg sysOrg);

    List<SysOrg> selectOrgListByTenantId(@Param("tenantId")String tenantId);

    int updateById(SysOrg record);

    SysOrg selectInfoById(String orgId);

    SysOrg selectByOrgCode(String orgCode);

    SysOrg selectTopOrgByTenantId(@Param("tenantId")String tenantId);

    SysOrg selectOrg(@Param("tenantId")String tenantId, @Param("orgCode")String orgCode);

    List<SysOrg> selectOrgListById(@Param("orgId")String orgId);

    List<SysOrg> selectListByIds(@Param("orgIds")List<String> orgIds);

    List<SysOrg> selectOrgListByParentId(@Param("parentId")String parentId, @Param("tenantId")String tenantId);

    List<SysOrg> getSubOrgList(String tenantId);
}
