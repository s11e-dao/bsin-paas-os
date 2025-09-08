package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import me.flyray.bsin.domain.entity.BizRoleApp;

import java.util.List;
import java.util.Map;

/**
 * 渠道(节点、商户)添加访问服务的应用，该应用用于签名校验和计费服务
 * 渠道通过appId和appKey访问网络服务
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


    /**
     * 个人微信agent登录
     */
    public BizRoleApp wechatAgentLogin(Map<String, Object> requestMap) throws JsonProcessingException;

    /**
     * 更新微信登录状态
     */
    public BizRoleApp updateWechatLoginStatus(Map<String, Object> requestMap) throws JsonProcessingException;

    /**
     * 获取微信登录列表
     */
    public List<?> getWechatLoginList(Map<String, Object> requestMap);
}
