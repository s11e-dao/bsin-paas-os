package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.Equity;

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
    public Equity add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public void edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public Equity getDetail(Map<String, Object> requestMap);


    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);


    /**
     * 授权权益
     */
    public Map<String, Object> grant(Map<String, Object> requestMap) throws UnsupportedEncodingException;

}
