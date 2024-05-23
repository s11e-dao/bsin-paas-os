package me.flyray.bsin.facade.service;


import java.util.Map;


/**
* @author bolei
* @description 针对表【ai_wx_platform_user_tag】的数据库操作Service
* @createDate 2023-04-28 12:46:58
*/

public interface WxPlatformUserTagService {

    /**
     *添加w
     */
    Map<String,Object> add(Map<String,Object> requestMap);

    /**
     *详情
     */
    Map<String,Object> detail(Map<String,Object> requestMap);

}
