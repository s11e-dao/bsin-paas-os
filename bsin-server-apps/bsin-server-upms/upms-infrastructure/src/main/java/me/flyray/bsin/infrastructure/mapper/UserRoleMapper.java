package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.SysRole;
import me.flyray.bsin.domain.entity.UserRole;

/**
* @author bolei
* @description 针对表【sys_user_role】的数据库操作Mapper
* @createDate 2023-10-07 12:25:07
* @Entity generator.domain.UserRole
*/

@Repository
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole>{

    void assignRoles(@Param("userId")String userId , @Param("roleIds") List<String> roleIds, @Param("appId")String appId );

    void assignRole(@Param("userId")String userId , @Param("roleId") String roleId,@Param("appId")String appId );

    void unAssignRoles(@Param("userId") String userId, @Param("appId") String appId);

    List<SysRole> selectListByUserId(@Param("userId") String userId, @Param("appId") String appId);

}




