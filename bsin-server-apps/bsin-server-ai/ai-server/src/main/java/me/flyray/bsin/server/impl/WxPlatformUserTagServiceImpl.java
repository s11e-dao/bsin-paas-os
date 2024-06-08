package me.flyray.bsin.server.impl;


import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.WxPlatformUserTag;
import me.flyray.bsin.facade.service.WxPlatformUserTagService;
import me.flyray.bsin.infrastructure.mapper.WxPlatformUserTagMapper;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author bolei
 * @description 针对表【ai_tenant_wxmp_user_tag】的数据库操作Service实现
 * @createDate 2023-04-28 12:46:58
 */

@ShenyuDubboService(path = "/wxPlatformUserTag", timeout = 6000)
@ApiModule(value = "wxPlatformUserTag")
@Service
@Slf4j
public class WxPlatformUserTagServiceImpl implements WxPlatformUserTagService {

  @Autowired private WxPlatformUserTagMapper wxPlatformUserTagMapper;

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public WxPlatformUserTag add(Map<String, Object> requestMap) {
    WxPlatformUserTag wxPlatformUserTag =
        BsinServiceContext.getReqBodyDto(WxPlatformUserTag.class, requestMap);
    wxPlatformUserTagMapper.insert(wxPlatformUserTag);
    return wxPlatformUserTag;
  }

  @ApiDoc(desc = "detail")
  @ShenyuDubboClient("/detail")
  @Override
  public List<WxPlatformUserTag> detail(Map<String, Object> requestMap) {
    String openId = (String) requestMap.get("openId");
    List<WxPlatformUserTag> wxPlatformUserTags = wxPlatformUserTagMapper.selectByOpenId(openId);
    return wxPlatformUserTags;
  }

}
