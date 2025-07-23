package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.Merchant;

import java.util.List;
import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/27 19:47
 * @desc 商户信息
 */

public interface MerchantService {

    /**
     * 商户注册
     * 1、校验商户信息是否重复
     * 2、保存商户信息
     * 3、创建钱包（
     *      1）、查询平台默认币种
     *      2）、创建币种的链上钱包，并保存钱包地址
     *      3）、创建钱包账户
     *  ）
     */
    public void register(Map<String, Object> requestMap);

    /**
     * 商户登录
     */
    public Map<String, Object> login(Map<String, Object> requestMap);

    /**
     * 商户订阅功能
     */
    public void subscribeFunction(Map<String, Object> requestMap);

    /**
     * 会员申请入住商户
     */
    public Merchant openMerchant(Map<String, Object> requestMap);

    /**
     * 添加商户
     */
    public Merchant add(Map<String, Object> requestMap);

    /**
     * 删除商户
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 修改商户
     */
    public Merchant edit(Map<String, Object> requestMap);

    /**
     * PC 管理后台查询
     * @param requestMap
     * @return
     */
    public IPage<Merchant> getPageListAdmin(Map<String, Object> requestMap);

    public List<Merchant> getList(Map<String, Object> requestMap);

    /**
     * C端分页查询商户
     */
    public IPage<Merchant> getPageList(Map<String, Object> requestMap);

    /**
     * 查询商户详情
     */
    public Merchant getDetail(Map<String, Object> requestMap);

    /**
     * 查询商户列表
     */
    List<?> getListByMerchantNos(Map<String, Object> requestMap);

}
