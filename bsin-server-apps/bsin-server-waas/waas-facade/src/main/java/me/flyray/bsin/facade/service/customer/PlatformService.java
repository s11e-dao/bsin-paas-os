package me.flyray.bsin.facade.service.customer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.customer.Platform;
import me.flyray.bsin.domain.request.customer.PlatformDTO;

import java.util.Map;

/**
* @author Admin
* @description 针对表【crm_platform(平台（租户,例：unionCash）;)】的数据库操作Service
* @createDate 2024-05-03 14:16:57
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
     void createPlatform(PlatformDTO platformDTO);

    /**
     * 编辑平台信息
     * @param platform
     * @return
     */
     void updatePlatform(Platform platform);

    /**
     * 平台登录
     */
    Map<String,Object> login(PlatformDTO platformDTO);

    /**
     * 分页查询平台列表
     * @param platformDTO
     * @return
     */
    Page<Platform> pageList(PlatformDTO platformDTO);
}
