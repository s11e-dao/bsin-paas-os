package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/26 13:49
 * @desc
 */

public interface ContractProtocolService {

    /**
     * 添加合约协议
     */
    public Map<String, Object> add(Map<String, Object> requestMap);

    /**
     * 删除合约协议
     */
    public Map<String, Object> delete(Map<String, Object> requestMap);

    /**
     * 修改合约协议
     */
    public Map<String, Object> edit(Map<String, Object> requestMap);

    /**
     * 租户下所有合约协议
     */
    public Map<String, Object> getList(Map<String, Object> requestMap);

    /**
     * 分页查询合约协议
     */
    public Map<String, Object> getPageList(Map<String, Object> requestMap);

    /**
     * 查询合约协议详情
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);

}
