package me.flyray.bsin.facade.service;

import java.util.Map;

public interface CustomerBillService {

    public Map<String, Object> getPageList(Map<String, Object> requestMap) ;

    public Map<String, Object> getHyperledgerSummaryData(Map<String, Object> requestMap) ;

}
