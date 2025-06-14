package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.entity.Platform;
import me.flyray.bsin.domain.request.PlatformDTO;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/11/1
 * @desc 租户平台信息
 */

public interface PlatformService {

    /**
     * 创建平台
     * 1、校验平台信息是否重复
     * 2、添加租户信息
     * 3、保存平台信息
     * 4、创建钱包（
     *      1）、查询平台默认币种
     *      2）、创建币种的链上钱包，并保存钱包地址
     *      3）、创建钱包账户
     *  ）
     */
    public void createPlatform(PlatformDTO platformDTO);

    /**
     * 节点租户登录
     */
    public Map<String, Object> login(Map<String, Object> requestMap);


    public Map<String, Object> edit(Map<String, Object> requestMap);

    /**
     * 根据tenantId获取租户详情
     * @return
     */
    public Platform getDetail(Map<String, Object> requestMap);

    /**
     * 查询系统租户平台
     * 场景：查询各业态场景的平台账户
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

    /**
     * 获取平台的生态价值分配模型
     * @param requestMap
     */
    public Platform getEcologicalValueAllocationModel(Map<String, Object> requestMap);

}
