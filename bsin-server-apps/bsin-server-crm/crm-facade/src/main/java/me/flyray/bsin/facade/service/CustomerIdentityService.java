package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import me.flyray.bsin.domain.entity.CustomerIdentity;

/**
 * @author leonard
 * @description 针对表【crm_customer_identity(客户身份表)】的数据库操作Service
 * @createDate 2024-10-13 00:06:17
 */


public interface CustomerIdentityService {

    /**
     * 添加
     */
    public CustomerIdentity add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public CustomerIdentity edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public CustomerIdentity getDetail(Map<String, Object> requestMap);


    /**
     * 分页查询
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);


    /**
     * 查询所有
     */
    public List<CustomerIdentity> getList(Map<String, Object> requestMap);

}
