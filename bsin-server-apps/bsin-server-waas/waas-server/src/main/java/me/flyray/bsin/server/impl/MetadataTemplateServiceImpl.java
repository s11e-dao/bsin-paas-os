package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.MetadataTemplate;
import me.flyray.bsin.facade.service.MetadataTemplateService;
import me.flyray.bsin.infrastructure.mapper.MetadataTemplateMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author bolei
 * @date 2023/8/13
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/metadataTemplate", timeout = 6000)
@ApiModule(value = "metadataTemplate")
@Service
public class MetadataTemplateServiceImpl implements MetadataTemplateService {

    @Autowired
    private MetadataTemplateMapper metadataTemplateMapper;

    @Override
    public Map<String, Object> add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        MetadataTemplate metadataTemplate = BsinServiceContext.getReqBodyDto(MetadataTemplate.class, requestMap);
        metadataTemplate.setTenantId(loginUser.getTenantId());
        metadataTemplate.setMerchantNo(loginUser.getMerchantNo());
        metadataTemplate.setSerialNo(BsinSnowflake.getId());
        metadataTemplateMapper.insert(metadataTemplate);
        return RespBodyHandler.setRespBodyDto(metadataTemplate);
    }

    @Override
    public Map<String, Object> delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        metadataTemplateMapper.deleteById(serialNo);
        return RespBodyHandler.RespBodyDto();
    }

    @Override
    public Map<String, Object> edit(Map<String, Object> requestMap) {
        MetadataTemplate metadataTemplate = BsinServiceContext.getReqBodyDto(MetadataTemplate.class, requestMap);
        metadataTemplateMapper.updateById(metadataTemplate);
        return RespBodyHandler.RespBodyDto();
    }

    @Override
    public Map<String, Object> getList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        LambdaUpdateWrapper<MetadataTemplate> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(MetadataTemplate::getCreateTime);
        warapper.eq(ObjectUtil.isNotNull(loginUser.getTenantId()),MetadataTemplate::getTenantId, loginUser.getTenantId());
        warapper.eq(ObjectUtil.isNotNull(loginUser.getMerchantNo()),MetadataTemplate::getMerchantNo, loginUser.getMerchantNo());
        List<MetadataTemplate> metadataTemplateList = metadataTemplateMapper.selectList(warapper);
        return RespBodyHandler.setRespBodyListDto(metadataTemplateList);
    }

    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        MetadataTemplate metadataTemplate = BsinServiceContext.getReqBodyDto(MetadataTemplate.class, requestMap);
        Pagination pagination = (Pagination) requestMap.get("pagination");
        Page<MetadataTemplate> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<MetadataTemplate> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(MetadataTemplate::getCreateTime);
        warapper.eq(ObjectUtil.isNotNull(loginUser.getTenantId()),MetadataTemplate::getTenantId, loginUser.getTenantId());
        warapper.eq(ObjectUtil.isNotNull(loginUser.getMerchantNo()),MetadataTemplate::getMerchantNo, loginUser.getMerchantNo());
        IPage<MetadataTemplate> pageList = metadataTemplateMapper.selectPage(page,warapper);
        return RespBodyHandler.setRespPageInfoBodyDto(pageList);
    }

    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        MetadataTemplate metadataTemplate = metadataTemplateMapper.selectById(serialNo);
        return RespBodyHandler.setRespBodyDto(metadataTemplate);
    }

}
