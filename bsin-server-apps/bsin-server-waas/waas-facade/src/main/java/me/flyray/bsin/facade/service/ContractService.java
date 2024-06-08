package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

/**
 * @author leonard
 * @date 2023/10/13
 * @desc
 */

public interface ContractService {

    /**
     * 部署合约：
     * s11e protocol合约部署： Core|Extension|Wrapper|Factory。。。
     */
    public Map<String, Object> deploy(Map<String, Object> requestMap) throws Exception;


    /**
     * 添加合约
     */
    public Map<String, Object> add(Map<String, Object> requestMap);

    /**
     * 删除合约
     */
    public Map<String, Object> delete(Map<String, Object> requestMap);

    /**
     * 修改合约
     */
    public Map<String, Object> edit(Map<String, Object> requestMap);

    /**
     * 租户下所有合约
     */
    public Map<String, Object> getList(Map<String, Object> requestMap);

    /**
     * 分页查询合约
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

    /**
     * 查询合约详情
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);

}
