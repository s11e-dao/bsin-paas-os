package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.MemberConfig;
import me.flyray.bsin.domain.request.MemberConfigBo;
import me.flyray.bsin.facade.service.MemberConfigService;
import me.flyray.bsin.infrastructure.mapper.MemberConfigMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.server.utils.Pagination;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 会员配置：会员模型(CrmMemberConfig)表服务实现类
 *
 * @author zth
 * @since 2024-12-18 13:33:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@ApiModule(value = "/crmMemberConfig")
@ShenyuDubboService(path = "/crmMemberConfig", timeout = 10000)
public class MemberConfigServiceImpl implements MemberConfigService {

    private final MemberConfigMapper baseMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param serialNo 主键
     * @return 实例对象
     */
    @Override
    @ShenyuDubboClient("/queryById")
    public MemberConfig queryById(String serialNo) {
        return this.baseMapper.selectById(serialNo);
    }

    /**
     * 新增数据
     *
     * @param bo 实例对象
     * @return 实例对象
     */
    @Override
    @ShenyuDubboClient("/add")
    public boolean add(MemberConfigBo bo) {
        MemberConfig bean = BeanUtil.toBean(bo, MemberConfig.class);
        bean.setCreateTime(new Date());
        bean.setTenantId(LoginInfoContextHelper.getTenantId());
        bean.setMerchantId(ObjectUtil.isEmpty(bo.getMerchantId()) ? LoginInfoContextHelper.getMerchantNo() : bo.getMerchantId());
        boolean flag = baseMapper.insert(bean) > 0;
        return flag;
    }

    /**
     * 修改数据
     *
     * @param bo 实例对象
     * @return 实例对象
     */
    @Override
    @ShenyuDubboClient("/edit")
    public boolean edit(MemberConfigBo bo) {
        MemberConfig bean = BeanUtil.toBean(bo, MemberConfig.class);
        bean.setUpdateTime(new Date());
        boolean flag = baseMapper.updateById(bean) > 0;
        return flag;
    }

    /**
     * 通过主键删除数据
     *
     * @param serialNo 主键
     * @return 是否成功
     */
    @Override
    @ShenyuDubboClient("/deleteById")
    public boolean deleteById(String serialNo) {
        return this.baseMapper.deleteById(serialNo) > 0;
    }

    @Override
    @ShenyuDubboClient("/list")
    public IPage<?> list(MemberConfigBo bo) {
        LambdaQueryWrapper<MemberConfig> lqw = buildQueryWrapper(bo);
        Pagination pagination = new Pagination();
        Page<MemberConfig> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        IPage<MemberConfig> pageList = baseMapper.selectPage(page, lqw);
        return pageList;
    }

    private LambdaQueryWrapper<MemberConfig> buildQueryWrapper(MemberConfigBo bo) {
        LambdaQueryWrapper<MemberConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(MemberConfig::getDelFlag, 0);
        lqw.orderByDesc(MemberConfig::getCreateTime);
        return lqw;
    }
}
