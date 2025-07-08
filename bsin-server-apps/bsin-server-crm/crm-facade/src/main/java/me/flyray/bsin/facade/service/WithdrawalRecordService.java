package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.WithdrawalRecord;

import java.util.Map;

public interface WithdrawalRecordService {

    /**
     * 提现申请
     */
    public WithdrawalRecord apply(Map<String, Object> requestMap);

    /**
     * 提现审核
     */
    public void audit(Map<String, Object> requestMap);

    /**
     * 提现详情
     */
    public WithdrawalRecord getDetail(Map<String, Object> requestMap);

    /**
     * 提现记录查询
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

}
