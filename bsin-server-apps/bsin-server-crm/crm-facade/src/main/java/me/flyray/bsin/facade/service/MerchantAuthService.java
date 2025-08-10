package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.MerchantAuth;
import me.flyray.bsin.domain.request.MerchantDTO;

import java.util.Map;

public interface MerchantAuthService {

    /**
     * 商户资料进件认证
     */
    public void apply(Map<String, Object> requestMap);

    /**
     * 审核
     * 审核通过可以访问具体功能
     */
    public void audit(Map<String, Object> requestMap) throws Exception;

    /**
     * 查询商户进件的资料信息
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap) throws Exception;


    public IPage<MerchantAuth> getPageList(Map<String, Object> requestMap);

}
