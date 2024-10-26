package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisBrokerageJournal;

import java.util.Map;

import me.flyray.bsin.domain.entity.DisBrokerageJournal;

import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_dis_brokerage_journal】的数据库操作Service
* @createDate 2024-10-25 17:13:52
*/
public interface DisBrokerageJournalService {

    /**
     * 添加
     */
    public DisBrokerageJournal add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public DisBrokerageJournal getDetail(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);


    DisBrokerageJournal brokerage(Map<String, Object> requestMap);
}
