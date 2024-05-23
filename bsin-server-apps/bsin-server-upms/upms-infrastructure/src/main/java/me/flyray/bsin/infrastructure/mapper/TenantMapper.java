package me.flyray.bsin.infrastructure.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.SysTenant;


@Repository
@Mapper
public interface TenantMapper extends BaseMapper<SysTenant> {

    SysTenant getTenantByTenantCode(@Param("tenantId") String tenantId, @Param("tenantCode") String tenantCode);

    void deleteById(String tenantId);

    List<SysTenant> selectList(@Param("tenantCode") String tenantCode, @Param("tenantName") String tenantName);

    SysTenant selectTenantInfoByTenantId(String tenantId);

    List<SysTenant> selectAllList();

    IPage<SysTenant> selectPageList(@Param("page") IPage<?> page,
                                    @Param("tenantName") String tenantName,
                                    @Param("tenantCode") String tenantCode,
                                    @Param("tenantId") String tenantId);


    int getCountByTenantCode(@Param("tenantCode") String tenantCode, @Param("tenantId") String tenantId);


    @Select("select * from  sys_tenant where tenant_name = #{tenantName}")
    SysTenant getTenantByName(String tenantName);
}
