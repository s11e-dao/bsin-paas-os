package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.Equity;
import me.flyray.bsin.domain.entity.Member;
import me.flyray.bsin.domain.entity.MerchantConfig;
import me.flyray.bsin.facade.service.MerchantConfigService;
import me.flyray.bsin.infrastructure.mapper.MerchantConfigMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.server.utils.Pagination;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

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
@ApiModule(value = "/merchantConfig")
@ShenyuDubboService(path = "/merchantConfig", timeout = 10000)
public class MerchantConfigServiceImpl implements MerchantConfigService {

    private final MerchantConfigMapper merchantConfigMapper;

    @ShenyuDubboClient("/getDetail")
    @Override
    public MerchantConfig getDetail(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");
        MerchantConfig merchantConfig = null;
        LambdaQueryWrapper<MerchantConfig> warapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(serialNo)){
            warapper.eq(MerchantConfig::getSerialNo, serialNo);
        }else {
            warapper.eq(MerchantConfig::getMerchantNo, merchantNo);
        }
        merchantConfig = merchantConfigMapper.selectOne(warapper);
        return merchantConfig;
    }

    /**
     * 商户让利配置和商户会员模型配置
     * @return 实例对象
     */
    @Override
    @ShenyuDubboClient("/config")
    public MerchantConfig config(MerchantConfig merchantConfigRequest) {
        merchantConfigRequest.setCreateTime(new Date());
        merchantConfigRequest.setTenantId(LoginInfoContextHelper.getTenantId());
        merchantConfigRequest.setMerchantNo(LoginInfoContextHelper.getMerchantNo());
        // 先查询更新
        LambdaQueryWrapper<MerchantConfig> warapper = new LambdaQueryWrapper<>();
        MerchantConfig merchantConfig = merchantConfigMapper.selectOne(warapper);
        if(merchantConfig != null){
            merchantConfig.setProfitSharingRate(merchantConfigRequest.getProfitSharingRate());
            merchantConfigMapper.updateById(merchantConfig);
        }else {
            merchantConfigMapper.insert(merchantConfigRequest);
        }
        return merchantConfigRequest;
    }

    /**
     * 修改数据
     *
     * @param bo 实例对象
     * @return 实例对象
     */
    @Override
    @ShenyuDubboClient("/edit")
    public boolean edit(MerchantConfig bo) {
        MerchantConfig bean = BeanUtil.toBean(bo, MerchantConfig.class);
        bean.setUpdateTime(new Date());
        boolean flag = merchantConfigMapper.updateById(bean) > 0;
        return flag;
    }

    @Override
    public boolean delete(String serialNo) {
        return false;
    }
    @Override
    @ShenyuDubboClient("/getPageList")
    public IPage<?> getPageList(MerchantConfig bo) {
        LambdaQueryWrapper<MerchantConfig> lqw = buildQueryWrapper(bo);
        Pagination pagination = new Pagination();
        Page<MerchantConfig> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        IPage<MerchantConfig> pageList = merchantConfigMapper.selectPage(page, lqw);
        return pageList;
    }

    private LambdaQueryWrapper<MerchantConfig> buildQueryWrapper(MerchantConfig bo) {
        LambdaQueryWrapper<MerchantConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantConfig::getDelFlag, 0);
        lqw.orderByDesc(MerchantConfig::getCreateTime);
        return lqw;
    }

}
