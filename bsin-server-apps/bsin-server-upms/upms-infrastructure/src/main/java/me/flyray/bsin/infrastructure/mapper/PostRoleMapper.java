package me.flyray.bsin.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.SysPost;

@Repository
@Mapper
public interface PostRoleMapper {

    void assignRoles(@Param("postId")String postId , @Param("roleIds") List<String> roleIds,@Param("appId")String appId );

    void assignRole(@Param("postId")String postId , @Param("roleId") String roleId,@Param("appId")String appId );

    void unAssignRoles(@Param("postId") String postId, @Param("appId") String appId);

    List<SysPost> getRoleByPostId(@Param("postId") String postId);
}
