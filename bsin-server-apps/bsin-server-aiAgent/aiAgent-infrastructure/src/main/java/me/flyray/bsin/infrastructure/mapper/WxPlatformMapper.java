package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.WxPlatform;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author bolei
 * @description 针对表【ai_wx_platform】的数据库操作Mapper
 * @createDate 2023-04-25 18:41:19 @Entity generator.domain.WxPlatform
 */
@Mapper
@Repository
public interface WxPlatformMapper extends BaseMapper<WxPlatform> {

  WxPlatform selectByAppId(String appId);

  WxPlatform selectByCorpAgentId(String corpId, String agentId);
}
