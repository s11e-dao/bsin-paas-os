package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.ChatMemory;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.ChatMemoryService;
import me.flyray.bsin.infrastructure.mapper.ChatMemoryMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author leonard
 * @description 针对表【ai_chat_memory】的数据库操作Service实现
 * @createDate 2023-12-09 01:50:35
 */
@ShenyuDubboService(path = "/chatMemory", timeout = 6000)
@ApiModule(value = "chatMemory")
@Service
@Slf4j
public class ChatMemoryServiceImpl implements ChatMemoryService {

  @Autowired private ChatMemoryMapper chatMemoryMapper;

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public ChatMemory add(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String tenantId = loginUser.getTenantId();
    ChatMemory chatMemory = BsinServiceContext.getReqBodyDto(ChatMemory.class, requestMap);
    chatMemory.setSerialNo(BsinSnowflake.getId());
    chatMemory.setTenantId(tenantId);
    chatMemory.setMerchantNo(merchantNo);
    chatMemory.setCustomerNo(customerNo);
    chatMemoryMapper.insert(chatMemory);
    return chatMemory;
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public void delete(Map<String, Object> requestMap) {
    String chatMemoryNo = MapUtils.getString(requestMap, "serialNo");
    if (chatMemoryNo == null) {
      chatMemoryNo = MapUtils.getString(requestMap, "chatMemoryNo");
    }
    chatMemoryMapper.deleteById(chatMemoryNo);
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public void edit(Map<String, Object> requestMap) {
    ChatMemory ChatMemory = BsinServiceContext.getReqBodyDto(ChatMemory.class, requestMap);
    chatMemoryMapper.updateById(ChatMemory);
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public ChatMemory getDetail(Map<String, Object> requestMap) {
    String chatMemoryNo = MapUtils.getString(requestMap, "serialNo");
    if (chatMemoryNo == null) {
      chatMemoryNo = MapUtils.getString(requestMap, "chatMemoryNo");
    }
    ChatMemory chatMemory = chatMemoryMapper.selectById(chatMemoryNo);
    return chatMemory;
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public IPage<ChatMemory> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String tenantId = loginUser.getTenantId();

    ChatMemory chatMemory = BsinServiceContext.getReqBodyDto(ChatMemory.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<ChatMemory> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<ChatMemory> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(ChatMemory::getCreateTime);
    wrapper.eq(ChatMemory::getTenantId, tenantId);
    wrapper.eq(ChatMemory::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), ChatMemory::getCustomerNo, customerNo);
    wrapper.eq(
            StringUtils.isNotBlank(chatMemory.getType()), ChatMemory::getType, chatMemory.getType());
    wrapper.eq(
            StringUtils.isNotBlank(chatMemory.getStatus()),
        ChatMemory::getStatus,
        chatMemory.getStatus());
    IPage<ChatMemory> pageList = chatMemoryMapper.selectPage(page, wrapper);
    return pageList;
  }

  @ApiDoc(desc = "getList")
  @ShenyuDubboClient("/getList")
  @Override
  public List<ChatMemory> getList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String tenantId = loginUser.getTenantId();

    ChatMemory chatMemory = BsinServiceContext.getReqBodyDto(ChatMemory.class, requestMap);

    LambdaUpdateWrapper<ChatMemory> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(ChatMemory::getCreateTime);
    wrapper.eq(ChatMemory::getTenantId, tenantId);
    wrapper.eq(ChatMemory::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), ChatMemory::getCustomerNo, customerNo);
    wrapper.eq(
            StringUtils.isNotBlank(chatMemory.getType()), ChatMemory::getType, chatMemory.getType());
    wrapper.eq(
            StringUtils.isNotBlank(chatMemory.getStatus()),
        ChatMemory::getStatus,
        chatMemory.getStatus());
    List<ChatMemory> embeddingModelList = chatMemoryMapper.selectList(wrapper);
    return embeddingModelList;
  }

}
