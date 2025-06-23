package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.DisCommissionGoods;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DisCommissionGoodsService;
import me.flyray.bsin.infrastructure.mapper.DisCommissionGoodsMapper;
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

import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.GRADE_NOT_EXISTS;

/**
* @author bolei
* @description 针对表【crm_dis_brokerage_goods】的数据库操作Service实现
* @createDate 2024-10-25 17:13:47
*/
@Slf4j
@ShenyuDubboService(path = "/disBrokerageGoods", timeout = 6000)
@ApiModule(value = "disBrokerageGoods")
@Service
public class DisCommissionGoodsServiceImpl implements DisCommissionGoodsService {

    @Autowired
    private DisCommissionGoodsMapper disBrokerageGoodsMapper;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public DisCommissionGoods add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisCommissionGoods disBrokerageGoods = BsinServiceContext.getReqBodyDto(DisCommissionGoods.class, requestMap);
        disBrokerageGoods.setSerialNo(BsinSnowflake.getId());
        disBrokerageGoodsMapper.insert(disBrokerageGoods);
        return disBrokerageGoods;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        if (disBrokerageGoodsMapper.deleteById(serialNo) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public DisCommissionGoods edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        DisCommissionGoods disBrokerageGoods = BsinServiceContext.getReqBodyDto(DisCommissionGoods.class, requestMap);
        if (disBrokerageGoodsMapper.updateById(disBrokerageGoods) == 0){
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
        return disBrokerageGoods;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<DisCommissionGoods> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        DisCommissionGoods payChannelConfig = BsinServiceContext.getReqBodyDto(DisCommissionGoods.class, requestMap);
        LambdaQueryWrapper<DisCommissionGoods> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(DisCommissionGoods::getCreateTime);
        IPage<DisCommissionGoods> pageList = disBrokerageGoodsMapper.selectPage(page, warapper);
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
    public DisCommissionGoods getDetail(Map<String, Object> requestMap){
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        DisCommissionGoods disBrokerageGoods = disBrokerageGoodsMapper.selectById(serialNo);
        return disBrokerageGoods;
    }

}




