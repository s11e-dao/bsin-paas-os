package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.MerchantApiFeeConfig;

import java.util.List;
import java.util.Map;

public interface MerchantAppApiFeeService {

    /**
     * 修改费用配置
     */
    public void edit(Map<String, Object> requestMap);

    /**
     * 查询费用配置
     */
    public MerchantApiFeeConfig getApiFeeConfigInfo(Map<String, Object> requestMap);

    /**
     * 分页查询费用配置
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

    /**
     * 分页查询
     */
    public List<?> getList(Map<String, Object> requestMap);

}
