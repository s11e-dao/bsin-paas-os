package me.flyray.bsin.server.biz;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import me.flyray.bsin.domain.domain.AiCustomerFunction;
import me.flyray.bsin.domain.enums.FunctionSubscribeStatus;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.infrastructure.mapper.AiCustomerFunctionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author leonard
 * @description: 判断账户拥有的权益： 会员充值信息， 可创建的智能体、知识库、知识库文件、wechatBot、Mp等
 * @createDate 2024/01/2024/1/29 /16/15
 */
@Component
public class AccountAvailableResourcesBiz {

  @Autowired private AiCustomerFunctionMapper aiCustomerFunctionMapper;

  public AiCustomerFunction functionServiceCheck(
      String tenantId, String merchantNo, String customerNo) {

    // TODO: 查询订阅的服务功能
    LambdaUpdateWrapper<AiCustomerFunction> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(AiCustomerFunction::getCreateTime);
    wrapper.eq(ObjectUtil.isNotNull(tenantId), AiCustomerFunction::getTenantId, tenantId);
    wrapper.eq(ObjectUtil.isNotNull(merchantNo), AiCustomerFunction::getMerchantNo, merchantNo);
    wrapper.eq(ObjectUtil.isNotNull(customerNo), AiCustomerFunction::getCustomerNo, customerNo);
    wrapper.eq(AiCustomerFunction::getStatus, FunctionSubscribeStatus.NORMAL.getCode());
    // 类型 0、租户上架的功能(可供客户订阅的功能模版) 1、客户订阅服务 2、客户订阅功能 3、系统自待基础服务
    wrapper.eq(AiCustomerFunction::getType, "2");
    // 系统自带基础服务
    wrapper.or().eq(AiCustomerFunction::getType, "3");
    List<AiCustomerFunction> functionServicelist = aiCustomerFunctionMapper.selectList(wrapper);
    if (functionServicelist == null || functionServicelist.size() < 1) {
      throw new BusinessException("100000", "请先订阅功能服务！！！");
    }
    AiCustomerFunction aiCustomerFunction = new AiCustomerFunction();
    Date validDate = new DateTime();
    Date dateNow = new DateTime();
    Integer copilotNum = 0;
    Integer knowledgeBaseNum = 0;
    Integer knowledgeBaseFileNum = 0;
    Integer mpNum = 0;
    Integer cpNum = 0;
    Integer wechatNum = 0;
    Integer menuTemplateNum = 0;
    Integer miniappNum = 0;
    boolean groupChat = false;
    boolean historyChatSummary = false;

    // 遍历订阅的所有功能，找出最长服务时间，最多可创建的资源数量
    for (AiCustomerFunction fuctionService : functionServicelist) {
      // 更新服务状态
      if (fuctionService.getEndTime().before(dateNow)) {
        // 剔除系统自带的功能
        if (!fuctionService.getType().equals("0")) {
          if (!fuctionService.getStatus().equals(FunctionSubscribeStatus.ARREARS_STOP.getCode())) {
            fuctionService.setStatus(FunctionSubscribeStatus.ARREARS_STOP.getCode());
            aiCustomerFunctionMapper.updateById(fuctionService);
          }
        }
      } else if (fuctionService.getEndTime().after(validDate)) {
        validDate = fuctionService.getEndTime();
        if (fuctionService.getCopilotNum() > copilotNum) {
          copilotNum = fuctionService.getCopilotNum();
        }
        if (fuctionService.getKnowledgeBaseNum() > knowledgeBaseNum) {
          knowledgeBaseNum = fuctionService.getKnowledgeBaseNum();
        }
        if (fuctionService.getKnowledgeBaseFileNum() > knowledgeBaseFileNum) {
          knowledgeBaseFileNum = fuctionService.getKnowledgeBaseFileNum();
        }
        if (fuctionService.getMpNum() > mpNum) {
          mpNum = fuctionService.getMpNum();
        }
        if (fuctionService.getCpNum() > cpNum) {
          cpNum = fuctionService.getCpNum();
        }
        if (fuctionService.getWechatNum() > wechatNum) {
          wechatNum = fuctionService.getWechatNum();
        }
        if (fuctionService.getMiniappNum() > miniappNum) {
          miniappNum = fuctionService.getMiniappNum();
        }
        if (fuctionService.getMenuTemplateNum() > menuTemplateNum) {
          menuTemplateNum = fuctionService.getMenuTemplateNum();
        }
        if (fuctionService.getGroupChat()) {
          groupChat = true;
        }
        if (fuctionService.getHistoryChatSummary()) {
          historyChatSummary = true;
        }
      }
    }
    if (validDate.before(dateNow)) {
      throw new BusinessException("100000", "订阅的服务功能已经过期，请及时续费！！！");
    }
    aiCustomerFunction.setCopilotNum(copilotNum);
    aiCustomerFunction.setKnowledgeBaseNum(knowledgeBaseNum);
    aiCustomerFunction.setKnowledgeBaseFileNum(knowledgeBaseFileNum);
    aiCustomerFunction.setMpNum(mpNum);
    aiCustomerFunction.setCpNum(cpNum);
    aiCustomerFunction.setWechatNum(wechatNum);
    aiCustomerFunction.setMenuTemplateNum(menuTemplateNum);
    aiCustomerFunction.setGroupChat(groupChat);
    aiCustomerFunction.setHistoryChatSummary(historyChatSummary);

    aiCustomerFunction.setExpirationTime(validDate);

    return aiCustomerFunction;
  }
}
