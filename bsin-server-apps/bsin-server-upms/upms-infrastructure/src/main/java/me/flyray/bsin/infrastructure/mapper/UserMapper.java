package me.flyray.bsin.infrastructure.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import me.flyray.bsin.domain.entity.SysUser;
import me.flyray.bsin.domain.request.SysUserDTO;


@Repository
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {

    void deleteById(String userId);

    void insertUser(SysUser userId);

    List<SysUser> selectByUsername(@Param("username") String username, @Param("nickname") String nickname, @Param("tenantId") String tenantId);

    SysUser getById(String userId);

    SysUser login(@Param("tenantId") String tenantId, @Param("username") String username, @Param("password") String password);

    void deleteUserById(String orgId);

    IPage<SysUser> selectPageList(@Param("page") IPage<?> page, @Param("query") SysUserDTO sysUserDTO);
    IPage<SysUser> selectPageAllList(@Param("page") IPage<?> page, @Param("query")SysUserDTO sysUserDTO);

    List<SysUser> selectUserList(@Param("tenantId") String tenantId,
                             @Param("nickname") String nickname,
                             @Param("username") String userName,
                             @Param("phone") String phone,
                             @Param("orgId") String orgId,
                             @Param("bizRoleType") String bizRoleType,
                             @Param("type") Integer type);

    List<SysUser> selectListByUserIds(@Param("userIds") List<String> userIds);

    List<SysUser> selectUserByPostIdAndOrgId(@Param("postId") String postId,
                                             @Param("orgId") String orgId);

    SysUser selectUserInfo(@Param("tenantId") String tenantId, @Param("userId") String userId, @Param("username") String username);

    SysUser selectEmail(String email);

    void updateByUserId(@Param("user") SysUser user);

    /**
     * 根据id查询用户（查询包括被逻辑删除的）
     *
     * @param userId
     * @return
     */
    SysUser selectByUserId(String userId);

    SysUser selectUserByUsername(@Param("username") String username);

    List<String> getUserIdByName(@Param("name") String name, @Param("tenantId") String tenantId);

    SysUser getUserByRoleCode(@Param("roleCode") String roleCode,@Param("tenantId")String tenantId);

    /**
     *
     * @param sysUserDTO
     * @return
     */
    List<String> getUserIdsByCondition(@Param("query") SysUserDTO sysUserDTO);

    List<Map<String,Object>> getUserInfoByIds(@Param("page")Page<?> page,@Param("userId")List<String> userIds);

    Integer verifyIfUserExists(SysUser sysUser);

}
