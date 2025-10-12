package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

public interface CustomerBillService {

    public IPage<?> getPageList(Map<String, Object> requestMap);

    public Map<String, Object> getHyperledgerSummaryData(Map<String, Object> requestMap) ;

}
