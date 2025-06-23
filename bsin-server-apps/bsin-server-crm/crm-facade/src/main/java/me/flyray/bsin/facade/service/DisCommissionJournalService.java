package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisCommissionJournal;

import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_dis_brokerage_journal】的数据库操作Service
* @createDate 2024-10-25 17:13:52
*/
public interface DisCommissionJournalService {

    /**
     * 添加
     */
    public DisCommissionJournal add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public DisCommissionJournal getDetail(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

}
