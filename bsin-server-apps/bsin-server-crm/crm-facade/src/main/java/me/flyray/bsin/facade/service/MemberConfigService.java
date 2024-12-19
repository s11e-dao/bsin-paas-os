package me.flyray.bsin.facade.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.MemberConfig;
import me.flyray.bsin.domain.request.MemberConfigBo;
import me.flyray.bsin.validate.AddGroup;
import me.flyray.bsin.validate.EditGroup;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * 会员配置：会员模型(CrmMemberConfig)表服务接口
 *
 * @author zth
 * @since 2024-12-18 13:32:31
 */
@Validated
public interface MemberConfigService {

    /**
     * 通过ID查询单条数据
     *
     * @param serialNo 主键
     * @return 实例对象
     */
    MemberConfig queryById(String serialNo);

    /**
     * 新增数据
     *
     * @param bo 实例对象
     * @return 实例对象
     */
    @Validated(AddGroup.class)
    boolean add(@Valid MemberConfigBo bo);

    /**
     * 修改数据
     *
     * @param bo 实例对象
     * @return 实例对象
     */
    @Validated(EditGroup.class)
    boolean edit(@Valid MemberConfigBo bo);

    /**
     * 通过主键删除数据
     *
     * @param serialNo 主键
     * @return 是否成功
     */
    boolean deleteById(String serialNo);

    /**
     * 分页查询
     *
     * @param bo
     * @return
     */
    IPage<?> list(MemberConfigBo bo);

}
