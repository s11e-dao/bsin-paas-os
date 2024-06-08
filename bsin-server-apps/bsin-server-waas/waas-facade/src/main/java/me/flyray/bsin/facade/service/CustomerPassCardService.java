package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.CustomerPassCard;
import me.flyray.bsin.facade.response.DigitalAssetsDetailRes;

import java.util.List;
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
    public void issue(Map<String, Object> requestMap) throws Exception;

    /**
     * 品牌会员开卡:按照顾数字资产上架流程开卡
     */
    public void claim(Map<String, Object> requestMap) throws Exception;;

    /**
     * 查询客户的pass卡
     * 我加入的品牌
     */
    public List<CustomerPassCard> getList(Map<String, Object> requestMap);

    /**
     * 查询商户的会员
     */
    public List<?> getMemberList(Map<String, Object> requestMap);

    /**
     * 查询会员用户
     * 我加入的品牌
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

    /**
     * 查询用户在某个商户下的通行证
     */
    public DigitalAssetsDetailRes getDetail(Map<String, Object> requestMap);

}
