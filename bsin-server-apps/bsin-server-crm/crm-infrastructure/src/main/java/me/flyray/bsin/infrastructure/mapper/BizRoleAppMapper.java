package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.BizRoleApp;

@Mapper
@Repository
public interface BizRoleAppMapper extends BaseMapper<BizRoleApp> {

  List<BizRoleApp> getPageList(
      @Param("tenantId") String tenantId,
      @Param("appName") String appName,
      @Param("appId") String appId,
      @Param("appSecret") String appSecret);

  BizRoleApp getAppInfo(
      @Param("tenantId") String tenantId,
      @Param("bizRoleTypeNo") String bizRoleTypeNo,
      @Param("appId") String appId);

  BizRoleApp selectByAppId(@Param("tenantId") String tenantId, @Param("appId") String appId);

  BizRoleApp selectByCorpAgentId(String corpId, String agentId);
}
