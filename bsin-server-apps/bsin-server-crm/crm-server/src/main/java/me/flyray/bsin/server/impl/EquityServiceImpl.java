package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Equity;
import me.flyray.bsin.domain.enums.EquityType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.BondingCurveTokenService;
import me.flyray.bsin.facade.service.DigitalAssetsItemService;
import me.flyray.bsin.facade.service.EquityService;
import me.flyray.bsin.infrastructure.mapper.EquityMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.EQUITY_NOT_EXISTS;
import static me.flyray.bsin.constants.ResponseCode.GRADE_NOT_EXISTS;

/**
 * @author bolei
 * @date 2023/7/26 16:46
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/equity", timeout = 6000)
@ApiModule(value = "equity")
@Service
public class EquityServiceImpl implements EquityService {

  @Autowired private EquityMapper equityMapper;

  @DubboReference(version = "${dubbo.provider.version}")
  private DigitalAssetsItemService digitalAssetsItemService;

  @DubboReference(version = "${dubbo.provider.version}")
  private BondingCurveTokenService bondingCurveTokenService;

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public Equity add(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    Equity equity = BsinServiceContext.getReqBodyDto(Equity.class, requestMap);
    if (equity.getTenantId() == null) {
      equity.setTenantId(loginUser.getTenantId());
      if (equity.getTenantId() == null) {
        throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
      }
    }
    if (equity.getMerchantNo() == null) {
      equity.setMerchantNo(loginUser.getMerchantNo());
      if (equity.getMerchantNo() == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    // TODO 条件的币种账户根据需要商户发行的数字积分的符号来添加，不能直接写死币种
    // typeNo 是数字资产编号 ccyType 不同类型找商户发行的不同币种
    equityMapper.insert(equity);
    return equity;
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public void delete(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    if (equityMapper.deleteById(serialNo) ==0){
      throw new BusinessException(EQUITY_NOT_EXISTS);
    }
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public void edit(Map<String, Object> requestMap) {
    Equity equity = BsinServiceContext.getReqBodyDto(Equity.class, requestMap);
    if (equityMapper.updateById(equity) ==0){
      throw new BusinessException(EQUITY_NOT_EXISTS);
    }
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public Equity getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    Equity equity = equityMapper.selectById(serialNo);
    if (equity == null) {
      throw new BusinessException(EQUITY_NOT_EXISTS);
    }
    return equity;
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public IPage<?> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    Equity equity = BsinServiceContext.getReqBodyDto(Equity.class, requestMap);
    Object paginationObj =  requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj,pagination);
    Page<Equity> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<Equity> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(Equity::getCreateTime);
    warapper.eq(Equity::getTenantId, loginUser.getTenantId());
    warapper.eq(Equity::getMerchantNo, loginUser.getMerchantNo());
    warapper.eq(StringUtils.isNotBlank(equity.getType()), Equity::getType, equity.getType());
    IPage<Equity> pageList = equityMapper.selectPage(page, warapper);
    return pageList;
  }

  @ApiDoc(desc = "grant")
  @ShenyuDubboClient("/grant")
  public Map<String, Object> grant(Map<String, Object> requestMap)
      throws UnsupportedEncodingException {

    Equity equity = (Equity) requestMap.get("equity");
    requestMap.put("value", equity.getValue());
    switch (EquityType.getInstanceById(equity.getType())) {
      case BADGE:
      case PFP:
      case DT:
      case PASS_CARD:
        // 数字资产铸造: ERC721/1155 等智能合约相关
        requestMap.put("serialNo", equity.getTypeNo()); // DigitalAssetsItem 资产编号
        try {
          digitalAssetsItemService.claim(requestMap);
        } catch (Exception e) {
          throw new BusinessException("100000", e.toString());
        }
        break;
      case BC:
        requestMap.put("bcCurveNo", equity.getTypeNo());
        // 捕获劳动价值的联合曲线积分若绑定了商户发行的数字积分，mint BC 积分是需要查询tokenParam配置参数，按照配置参数进行数字积分释放铸造
        bondingCurveTokenService.mint(requestMap);
        break;
      case DP:
        throw new BusinessException(ResponseCode.STATUS_NOT_EXISTS);
      case GRADE:
        throw new BusinessException(ResponseCode.STATUS_NOT_EXISTS);
      default:
        throw new BusinessException(ResponseCode.STATUS_NOT_EXISTS);
    }
    return requestMap;
  }

}
