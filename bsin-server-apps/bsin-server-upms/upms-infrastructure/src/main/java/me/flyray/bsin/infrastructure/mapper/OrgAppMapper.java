package me.flyray.bsin.infrastructure.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.SysOrg;


@Repository
@Mapper
public interface OrgAppMapper {

    void authorizeApp(@Param("orgId") String orgId, @Param("appId") String appId,@Param("type") String type);

    void authorizeApps(@Param("orgId") String orgId ,@Param("appIds") List<String> appIds,@Param("type") String type);

    void unAuthorizeAppsByOrgId(@Param("orgId") String orgId,@Param("type") String type);

    List<String> getOrgIdsByAppId(String appId);

    List<SysOrg> getAppByOrgId(String orgId);

    void unAuthorizeAppsByOrgIdAndAppId(@Param("orgId") String orgId , @Param("appId")String appId);

    Integer selectOrgAppType(@Param("orgId") String orgId,@Param("appId") String appId);


}
