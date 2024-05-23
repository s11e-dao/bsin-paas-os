package me.flyray.bsin.infrastructure.mapper;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.SysRole;

@Repository
@Mapper
public interface RoleMapper {

    void insert(SysRole role);

    SysRole getRoleInfoByRoleCode(@Param("roleCode") String roleCode,@Param("appId") String appId);

    void deleteById(String roleId);

    void updateById(SysRole role);

    IPage<SysRole> selectPageList(@Param("page") IPage<?> page ,
                                  @Param("roleCode") String roleCode,
                                  @Param("roleName") String roleName,
                                  @Param("appId") String appId,
                                  @Param("tenantId") String tenantId);

    List<SysRole> selectListByAppId(@Param("appId") String appId,
                                    @Param("tenantId") String tenantId);

    List<SysRole> selectAllRole();

    void deleteByAppId(String appId);

    List<SysRole> selectListByAppCode(@Param("appCode") String appCode);

    List<SysRole> getRoleListByPostId(String postId);

    List<SysRole> getRoleListByUserId(String userId);



}
