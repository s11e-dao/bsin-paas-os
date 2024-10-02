package me.flyray.bsin.infrastructure.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import me.flyray.bsin.domain.entity.WxPlatformUserTag;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author bolei
* @description 针对表【ai_wx_platform_user_tag】的数据库操作Mapper
* @createDate 2023-04-28 12:46:58
* @Entity generator.domain.AiTenantWxmpUserTag
*/

@Mapper
@Repository
public interface WxPlatformUserTagMapper extends BaseMapper<WxPlatformUserTag> {

    List<WxPlatformUserTag> selectByOpenId(String openId);


}
