package me.flyray.bsin.infrastructure.mapper;


import com.baomidou.mybatisplus.core.metadata.IPage;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.SysApp;
import me.flyray.bsin.domain.response.AppResp;

@Repository
@Mapper
public interface AppMapper {

    void insert(SysApp record);

    void deleteById(String appId);

    IPage<SysApp> selectPageList(@Param("page") IPage<?> page, @Param("tenantId") String  tenantId ,
                                 @Param("appId") String appId,
                                 @Param("appCode") String appCode,
                                 @Param("appName") String appName
    );

    void updateById(SysApp record);

    SysApp getAppInfoByAppCode(String appCode);

    SysApp getAppInfoByAppId(@Param("appId")String appId,@Param("tenantId") String tenantId);

    List<AppResp> selectListByTenantIdAndAppName(@Param("tenantId") String  tenantId , @Param("appName") String appName);

    List<AppResp> selectListByTenantId(@Param("tenantId") String  tenantId );

    List<String> selectAppIdsByTenantIdAndAppName(@Param("tenantId") String  tenantId , @Param("appName") String appName);

    List<String> selectAppIdsByOrgId(@Param("orgId") String orgId);

    IPage<SysApp> selectPageListByOrgId(@Param("page") IPage<?> page ,
                                   @Param("orgId") String  orgId );
    List<SysApp> selectListByOrgId(@Param("orgId") String  orgId);

    List<AppResp> selectOrgAppTypeListByOrgId(@Param("orgId") String  orgId );

    List<SysApp> selectPublishApps();

    SysApp selectOneByAppId(@Param("appId")String appId);

    List<AppResp> selectOrgAppListByOrgId(@Param("orgId") String  orgId );

    List<SysApp> selectListByAppIds(@Param("unauthorizedAppIds") List<String> unauthorizedAppIds);

}
