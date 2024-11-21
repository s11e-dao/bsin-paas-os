package me.flyray.bsin.mqtt.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.mqtt.domain.entity.MqttTopic;
import me.flyray.bsin.mqtt.facade.service.MqttTopicManageService;
import me.flyray.bsin.mqtt.infrastructure.mapper.MqttTopicMapper;
import me.flyray.bsin.mqtt.service.util.Pagination;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Service;
import cn.hutool.core.bean.BeanUtil;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.GRADE_NOT_EXISTS;

/**
 * @author leonard
 * @version 0.1
 * @date 2024/11/17
 * @description mqtt话题 CRUD
 */
@Slf4j
@ShenyuDubboService(path = "/mqttTopicManage", timeout = 6000)
@ApiModule(value = "mqttTopicManage")
@Service
public class MqttTopicManageServiceImpl implements MqttTopicManageService {

  @Autowired private MqttTopicMapper mqttTopicMapper;

  //  @DubboReference(version = "${dubbo.provider.version}")
  //  private TokenParamService tokenParamService;

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public MqttTopic add(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    MqttTopic mqttTopic = BsinServiceContext.getReqBodyDto(MqttTopic.class, requestMap);
    mqttTopic.setTenantId(loginUser.getTenantId());
    mqttTopic.setMerchantNo(loginUser.getMerchantNo());
    mqttTopic.setSerialNo(BsinSnowflake.getId());
    mqttTopicMapper.insert(mqttTopic);
    return mqttTopic;
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public void delete(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    if (mqttTopicMapper.deleteById(serialNo) == 0) {
      throw new BusinessException(GRADE_NOT_EXISTS);
    }
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public MqttTopic edit(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    MqttTopic mqttTopic = BsinServiceContext.getReqBodyDto(MqttTopic.class, requestMap);
    mqttTopic.setTenantId(loginUser.getTenantId());
    mqttTopic.setMerchantNo(loginUser.getMerchantNo());
    if (mqttTopicMapper.updateById(mqttTopic) == 0) {
      throw new BusinessException(GRADE_NOT_EXISTS);
    }
    return mqttTopic;
  }

  @ApiDoc(desc = "getList")
  @ShenyuDubboClient("/getList")
  @Override
  public List<MqttTopic> getList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    MqttTopic mqttTopic = BsinServiceContext.getReqBodyDto(MqttTopic.class, requestMap);
    LambdaQueryWrapper<MqttTopic> warapper = new LambdaQueryWrapper<>();
    warapper.orderByDesc(MqttTopic::getCreateTime);
    warapper.eq(MqttTopic::getTenantId, loginUser.getTenantId());
    warapper.eq(
        StringUtils.isNotEmpty(loginUser.getMerchantNo()),
        MqttTopic::getMerchantNo,
        loginUser.getMerchantNo());
    List<MqttTopic> mqttTopicList = mqttTopicMapper.selectList(warapper);
    return mqttTopicList;
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public IPage<?> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    Object paginationObj = requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj, pagination);
    Page<MqttTopic> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    MqttTopic mqttTopic = BsinServiceContext.getReqBodyDto(MqttTopic.class, requestMap);
    LambdaQueryWrapper<MqttTopic> warapper = new LambdaQueryWrapper<>();
    warapper.orderByDesc(MqttTopic::getCreateTime);
    warapper.eq(MqttTopic::getTenantId, loginUser.getTenantId());
    warapper.eq(
        StringUtils.isNotEmpty(loginUser.getMerchantNo()),
        MqttTopic::getMerchantNo,
        loginUser.getMerchantNo());

    IPage<MqttTopic> pageList = mqttTopicMapper.selectPage(page, warapper);
    return pageList;
  }

  /**
   * 等级条件和权益详情
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public MqttTopic getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    MqttTopic mqttTopic = mqttTopicMapper.selectById(serialNo);
    return mqttTopic;
  }
}
