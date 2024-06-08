package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_equity(客户等级权益)】的数据库操作Service
* @createDate 2023-07-26 16:04:25
*/

public interface EquityService{

    /**
     * 添加
     */
    public Map<String, Object> add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public Map<String, Object> delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public Map<String, Object> edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);


    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);


    /**
     * 租户下所有
     */
    public Map<String, Object> grant(Map<String, Object> requestMap) throws UnsupportedEncodingException;

}
