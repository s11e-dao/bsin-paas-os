package me.flyray.bsin.facade.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import me.flyray.bsin.domain.entity.MerchantConfig;
import me.flyray.bsin.validate.AddGroup;
import me.flyray.bsin.validate.EditGroup;
import org.springframework.validation.annotation.Validated;

import java.util.Map;


/**
 * 商户配置
 *
 * @since 2024-12-18 13:32:31
 */
@Validated
public interface MerchantConfigService {

    /**
     * 通过ID查询单条数据
     * @return 实例对象
     */
    MerchantConfig getDetail(Map<String, Object> requestMap);

    /**
     * 配置数据
     * @param bo 实例对象
     * @return 实例对象
     */
    @Validated(AddGroup.class)
    boolean config(@Valid MerchantConfig bo);

    /**
     * 修改数据
     *
     * @param bo 实例对象
     * @return 实例对象
     */
    @Validated(EditGroup.class)
    boolean edit(@Valid MerchantConfig bo);

    /**
     * 通过主键删除数据
     *
     * @param serialNo 主键
     * @return 是否成功
     */
    boolean delete(String serialNo);

    /**
     * 分页查询
     * @param bo
     * @return
     */
    IPage<?> getPageList(MerchantConfig bo);

}
