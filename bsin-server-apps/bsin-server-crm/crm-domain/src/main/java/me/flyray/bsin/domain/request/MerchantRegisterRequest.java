package me.flyray.bsin.domain.request;

import lombok.Data;
import me.flyray.bsin.domain.domain.Merchant;
import me.flyray.bsin.validate.AddGroup;

import javax.validation.constraints.NotBlank;

@Data
public class MerchantRegisterRequest extends Merchant {
    /**
     * 0、手机号 1、邮箱 2、QQ 3、微信4、用户名 5、微博  第三方登录获取
     */
    @NotBlank(message = "验证方法不能为空！", groups = AddGroup.class)
    private String authMethod;
    /**
     * 凭据，第三方标识
     */
    private String credential;
    /**
     * 登录用户名：注册设置
     */
    @NotBlank(message = "用户名不能为空！", groups = AddGroup.class)
    private String username;
    /**
     * 登录密码：注册设置
     */
    @NotBlank(message = "登录密码不能为空！", groups = AddGroup.class)
    private String password;
    /**
     * 客户类型 0、个人客户 1、租户商家客户 2、租户(dao)客户 3、顶级平台商家客户
     */
    private String customerType;
    /**
     * 邀请码
     */
    private String inviteCode;
}
