package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.DisPolicyMerchant;
import me.flyray.bsin.domain.entity.Grade;
import me.flyray.bsin.domain.entity.Merchant;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DisPolicyMerchantService;
import me.flyray.bsin.infrastructure.mapper.DisPolicyMerchantMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.GRADE_NOT_EXISTS;

/**
* @author bolei
* @description 针对表【crm_dis_policy_merchant】的数据库操作Service实现
* @createDate 2024-10-25 17:14:15
*/
@Slf4j
@ShenyuDubboService(path = "/disPolicyMerchant", timeout = 6000)
@ApiModule(value = "disPolicyMerchant")
@Service
public class DisPolicyMerchantServiceImpl implements DisPolicyMerchantService {

    @Autowired
    private DisPolicyMerchantMapper disPolicyMerchantMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public DisPolicyMerchant add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisPolicyMerchant disPolicyMerchant = BsinServiceContext.getReqBodyDto(DisPolicyMerchant.class, requestMap);
        if(disPolicyMerchant.getBrokeragePolicyNo() == null){
            throw new BusinessException("999","分佣政策编号不能为空!");
        }
        disPolicyMerchant.setTenantId(loginUser.getTenantId());
        disPolicyMerchant.setSerialNo(BsinSnowflake.getId());
        disPolicyMerchantMapper.insert(disPolicyMerchant);
        return disPolicyMerchant;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        // 根据政策ID和商户号删除
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        if (disPolicyMerchantMapper.deleteById(serialNo) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public DisPolicyMerchant edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisPolicyMerchant disPolicyMerchant = BsinServiceContext.getReqBodyDto(DisPolicyMerchant.class, requestMap);
        disPolicyMerchant.setTenantId(loginUser.getTenantId());
        if (disPolicyMerchantMapper.updateById(disPolicyMerchant) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
        return disPolicyMerchant;
    }

    @ApiDoc(desc = "getList")
    @ShenyuDubboClient("/getList")
    @Override
    public List<Merchant> getList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        List<Merchant> list = disPolicyMerchantMapper.selectListByBrokeragePolicyNo(MapUtils.getString(requestMap, "brokeragePolicyNo"));
        return list;
    }

    /**
     * 查询关联的商户详细信息
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<Merchant> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        DisPolicyMerchant disPolicyMerchant = BsinServiceContext.getReqBodyDto(DisPolicyMerchant.class, requestMap);
        IPage<Merchant> pageList = disPolicyMerchantMapper.selectPageListByBrokeragePolicyNo(page, disPolicyMerchant.getBrokeragePolicyNo());
        return pageList;
    }


    /**
     * 事件详情
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public DisPolicyMerchant getDetail(Map<String, Object> requestMap){
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        DisPolicyMerchant disPolicyMerchant = disPolicyMerchantMapper.selectById(serialNo);
        return disPolicyMerchant;
    }

    @ApiDoc(desc = "update")
    @ShenyuDubboClient("/update")
    @Override
    public void update(Map<String, Object> requestMap){
        String merchant_nos = MapUtils.getString(requestMap,"merchantNos");
        System.out.println(merchant_nos);
        // 检查是否为数组
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String[] array = merchant_nos.split(",");
        LambdaQueryWrapper<DisPolicyMerchant> warapper = new LambdaQueryWrapper<>();
        warapper.eq(DisPolicyMerchant::getBrokeragePolicyNo, MapUtils.getString(requestMap, "brokeragePolicyNo"));
        disPolicyMerchantMapper.delete(warapper);
        for(String merchantNo:array){
            DisPolicyMerchant disPolicyMerchant = new DisPolicyMerchant();
            disPolicyMerchant.setBrokeragePolicyNo(MapUtils.getString(requestMap, "brokeragePolicyNo"));
            disPolicyMerchant.setMerchantNo(merchantNo);
            disPolicyMerchant.setSerialNo(BsinSnowflake.getId());
            disPolicyMerchant.setTenantId(loginUser.getTenantId());
            disPolicyMerchantMapper.insert(disPolicyMerchant);
        }
    }
}




