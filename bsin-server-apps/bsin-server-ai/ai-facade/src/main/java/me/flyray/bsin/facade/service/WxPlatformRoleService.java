package me.flyray.bsin.facade.service;

import java.util.Map;

/**
* @author bolei
* @description 针对表【ai_tenant_wxmp_role】的数据库操作Service
* @createDate 2023-04-25 18:41:30
*/

public interface WxPlatformRoleService {

    /**
     *添加
     */
    Map<String,Object> add(Map<String,Object> requestMap);

    /**
     *删除
     */
    Map<String,Object> delete(Map<String,Object> requestMap);

    /**
     *编辑
     */
    Map<String,Object> edit(Map<String,Object> requestMap);

    /**
     *详情
     */
    Map<String,Object> detail(Map<String,Object> requestMap);

}
