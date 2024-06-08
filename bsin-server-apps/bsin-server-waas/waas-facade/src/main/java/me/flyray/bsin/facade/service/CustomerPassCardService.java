package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/27 19:46
 * @desc 客户通行证
 */

public interface CustomerPassCardService {

    /**
     * 品牌商户发pass card
     */
    public Map<String, Object> issue(Map<String, Object> requestMap) throws Exception;

    /**
     * 品牌会员开卡:按照顾数字资产上架流程开卡
     */
    public Map<String, Object> claim(Map<String, Object> requestMap) throws Exception;;

    /**
     * 查询客户的pass卡
     * 我加入的品牌
     */
    public Map<String, Object> getList(Map<String, Object> requestMap);

    /**
     * 查询商户的会员
     */
    public Map<String, Object> getMemberList(Map<String, Object> requestMap);

    /**
     * 查询会员用户
     * 我加入的品牌
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

    /**
     * 查询用户在某个商户下的通行证
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);

}
