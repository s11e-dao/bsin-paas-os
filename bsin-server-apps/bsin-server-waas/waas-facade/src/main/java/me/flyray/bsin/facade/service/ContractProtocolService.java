package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.ContractProtocol;

import java.util.List;
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
    public List<ContractProtocol> getList(Map<String, Object> requestMap);

    /**
     * 分页查询合约协议
     */
    public IPage<ContractProtocol> getPageList(Map<String, Object> requestMap);

    /**
     * 查询合约协议详情
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);

}
