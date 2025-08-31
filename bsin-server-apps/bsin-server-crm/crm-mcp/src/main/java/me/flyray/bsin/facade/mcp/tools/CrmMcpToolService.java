package me.flyray.bsin.facade.mcp.tools;

import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.infrastructure.mapper.CustomerBaseMapper;
import me.flyray.bsin.utils.BsinSnowflake;
import me.flyray.bsin.utils.UniqueInviteCodeGenerator;
import me.flyray.bsin.domain.enums.CustomerType;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.Date;

@Service
public class CrmMcpToolService {

    @Autowired
    private CustomerBaseMapper customerBaseMapper;

    @Tool(description = "CRM系统新增客户: 必填字段：tenantId（租户号）、username（客户名称）、phone（手机号）、realName（真实姓名）、type（客户类型，默认为个人客户）等")
    public String add(@ToolParam(description = "客户基础信息对象，必须包含tenantId（租户号）、username（客户名称）、phone（手机号）、realName（真实姓名）、type（客户类型，默认为个人客户）等") CustomerBase customerBase) {
        System.out.println("开始新增客户：" + customerBase.getUsername());

        // 设置系统生成的字段
        customerBase.setCustomerNo(BsinSnowflake.getId());
        customerBase.setCreateTime(new Date());
        customerBase.setUpdateTime(new Date());
        customerBase.setDelFlag(0);
        customerBase.setVipFlag(0);
        customerBase.setCertificationStatus(false);

        // 设置默认值（如果未设置）
        if (!StringUtils.hasText(customerBase.getType())) {
            customerBase.setType(CustomerType.PERSONAL.getCode());
        }
        if (!StringUtils.hasText(customerBase.getTxPasswordStatus())) {
            customerBase.setTxPasswordStatus("1"); // 1表示未设置支付密码
        }

        // 生成邀请码（如果未设置）
        if (!StringUtils.hasText(customerBase.getInviteCode())) {
            customerBase.setInviteCode(UniqueInviteCodeGenerator.generateUniqueInviteCode(6));
        }

        // 插入数据库
        int result = customerBaseMapper.insert(customerBase);

        if (result > 0) {
            String successMessage = String.format("新增客户成功！客户编号：%s，客户名称：%s，邀请码：%s",
                customerBase.getCustomerNo(), customerBase.getUsername(), customerBase.getInviteCode());
            System.out.println(successMessage);
            return successMessage;
        } else {
            System.out.println("新增客户失败：数据库插入失败");
            return "新增客户失败：数据库操作异常";
        }
    }

}
