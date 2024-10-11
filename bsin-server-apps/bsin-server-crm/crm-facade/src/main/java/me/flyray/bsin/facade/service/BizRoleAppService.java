package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.BizRoleApp;

import java.util.Map;

/**
 * 商户添加访问服务的应用，该应用用于签名校验和计费服务
 * 商户通过appId和appKey访问网络服务
 */

public interface BizRoleAppService {

    /**
     * 添加
     */
    public void add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 修改
     */
    public void edit(Map<String, Object> requestMap);

    BizRoleApp getDetail(Map<String, Object> requestMap);

    /**
     * 分页查询
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

}
