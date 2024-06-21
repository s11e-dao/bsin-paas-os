package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.WxPlatformUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author bolei
 * @description 针对表【ai_wx_platform_user】的数据库操作Mapper
 * @createDate 2023-04-28 14:02:38 @Entity generator.domain.WxPlatformUser
 */
@Mapper
@Repository
public interface WxPlatformUserMapper extends BaseMapper<WxPlatformUser> {
  WxPlatformUser selectByOpenId(String openId);
}
