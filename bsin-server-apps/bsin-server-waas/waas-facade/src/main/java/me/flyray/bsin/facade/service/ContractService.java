package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.Contract;

import java.util.List;
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
    public Contract deploy(Map<String, Object> requestMap) throws Exception;


    /**
     * 添加合约
     */
    public Contract add(Map<String, Object> requestMap);

    /**
     * 删除合约
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 修改合约
     */
    public void edit(Map<String, Object> requestMap);

    /**
     * 租户下所有合约
     */
    public List<Contract> getList(Map<String, Object> requestMap);

    /**
     * 分页查询合约
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

    /**
     * 查询合约详情
     */
    public Contract getDetail(Map<String, Object> requestMap);

}
