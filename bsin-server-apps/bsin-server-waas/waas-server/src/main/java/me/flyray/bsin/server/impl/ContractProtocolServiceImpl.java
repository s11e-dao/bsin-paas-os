package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import me.flyray.bsin.server.utils.Pagination;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.ContractProtocol;
import me.flyray.bsin.facade.service.ContractProtocolService;
import me.flyray.bsin.infrastructure.mapper.ContractProtocolMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.RespBodyHandler;

/**
 * @author bolei
 * @date 2023/6/26 15:13
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/contractProtocol", timeout = 6000)
@ApiModule(value = "contractProtocol")
@Service
public class ContractProtocolServiceImpl implements ContractProtocolService {

    @Autowired
    private ContractProtocolMapper contractProtocolMapper;

    @ShenyuDubboClient("/add")
    @ApiDoc(desc = "add")
    @Override
    public Map<String, Object> add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        log.info(loginUser.toString());
        ContractProtocol contractProtocol = BsinServiceContext.getReqBodyDto(ContractProtocol.class, requestMap);
        if (contractProtocol.getTenantId() == null) {
            contractProtocol.setTenantId(loginUser.getTenantId());
        }
        if (contractProtocol.getCreateBy() == null) {
            contractProtocol.setCreateBy(loginUser.getCustomerNo());
        }
        contractProtocolMapper.insert(contractProtocol);
        return RespBodyHandler.setRespBodyDto(contractProtocol);
    }

    @ShenyuDubboClient("/add")
    @ApiDoc(desc = "add")
    @Override
    public Map<String, Object> delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        contractProtocolMapper.deleteById(serialNo);
        return RespBodyHandler.RespBodyDto();
    }

    @ShenyuDubboClient("/edit")
    @ApiDoc(desc = "edit")
    @Override
    public Map<String, Object> edit(Map<String, Object> requestMap) {
        ContractProtocol contractProtocol = BsinServiceContext.getReqBodyDto(ContractProtocol.class, requestMap);
        contractProtocolMapper.updateById(contractProtocol);
        return RespBodyHandler.RespBodyDto();
    }

    @ShenyuDubboClient("/getPageList")
    @ApiDoc(desc = "getPageList")
    @Override
    public IPage<ContractProtocol> getPageList(Map<String, Object> requestMap) {
        ContractProtocol contractProtocol = BsinServiceContext.getReqBodyDto(ContractProtocol.class, requestMap);
        Object paginationObj =  requestMap.get("pagination");
        me.flyray.bsin.server.utils.Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<ContractProtocol> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<ContractProtocol> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(ContractProtocol::getCreateTime);
         warapper.eq(ContractProtocol::getTenantId, LoginInfoContextHelper.getTenantId());
        warapper.eq(ObjectUtil.isNotNull(contractProtocol.getProtocolName()), ContractProtocol::getProtocolName, contractProtocol.getProtocolName());
        warapper.eq(ObjectUtil.isNotNull(contractProtocol.getProtocolStandards()), ContractProtocol::getProtocolStandards, contractProtocol.getProtocolStandards());
        warapper.eq(ObjectUtil.isNotNull(contractProtocol.getType()), ContractProtocol::getType, contractProtocol.getType());
        warapper.eq(ObjectUtil.isNotNull(contractProtocol.getCategory()), ContractProtocol::getCategory, contractProtocol.getCategory());
        warapper.eq(ObjectUtil.isNotNull(contractProtocol.getProtocolCode()), ContractProtocol::getProtocolCode, contractProtocol.getProtocolCode());
        warapper.eq(ObjectUtil.isNotNull(contractProtocol.getChainType()), ContractProtocol::getChainType, contractProtocol.getChainType());
        IPage<ContractProtocol> pageList = contractProtocolMapper.selectPage(page,warapper);
        return pageList;
    }

    @ShenyuDubboClient("/getList")
    @ApiDoc(desc = "getList")
    @Override
    public List<ContractProtocol> getList(Map<String, Object> requestMap) {
        ContractProtocol contractProtocol = BsinServiceContext.getReqBodyDto(ContractProtocol.class, requestMap);
        LambdaQueryWrapper<ContractProtocol> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(ContractProtocol::getCreateTime);
        warapper.eq(ObjectUtil.isNotNull(contractProtocol.getProtocolName()), ContractProtocol::getProtocolName, contractProtocol.getProtocolName());
        warapper.eq(ObjectUtil.isNotNull(contractProtocol.getProtocolStandards()), ContractProtocol::getProtocolStandards, contractProtocol.getProtocolStandards());
        warapper.eq(ObjectUtil.isNotNull(contractProtocol.getType()), ContractProtocol::getType, contractProtocol.getType());
        warapper.eq(ObjectUtil.isNotNull(contractProtocol.getCategory()), ContractProtocol::getCategory, contractProtocol.getCategory());
        warapper.eq(ObjectUtil.isNotNull(contractProtocol.getProtocolCode()), ContractProtocol::getProtocolCode, contractProtocol.getProtocolCode());
        List<ContractProtocol> contractProtocolList = contractProtocolMapper.selectList(warapper);
        return contractProtocolList;
    }

    @ShenyuDubboClient("/getDetail")
    @ApiDoc(desc = "getDetail")
    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        ContractProtocol contractProtocol = contractProtocolMapper.selectById(serialNo);
        return RespBodyHandler.setRespBodyDto(contractProtocol);
    }

}
