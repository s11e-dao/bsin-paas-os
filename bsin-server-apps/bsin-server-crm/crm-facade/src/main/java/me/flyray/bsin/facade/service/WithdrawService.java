package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.WithdrawOrder;

import java.util.Map;


/**
* @author bolei
* @description 针对表【market_withdraw_journal】的数据库操作Service
* @createDate 2023-09-12
*/

public interface WithdrawService {

    /**
     * 提现申请列表
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

    /**
     * 提现
     * 用户向商户提现，无需审核
     */
    public WithdrawOrder withdraw(Map<String, Object> requestMap) throws Exception;

    /**
     * 提现申请
     */
    public WithdrawOrder withdrawApply(Map<String, Object> requestMap);


    /**
     * 提现审核
     */
    public Map<String, Object> audit(Map<String, Object> requestMap);

    /**
     * 查询详情
     */
    public WithdrawOrder getDetail(Map<String, Object> requestMap);

}
