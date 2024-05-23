package me.flyray.bsin.blockchain.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import me.flyray.bsin.blockchain.enums.ChainEnv;
import me.flyray.bsin.blockchain.enums.ChainType;

/**
 * @author ：bolei
 * @date ：Created in 2022/5/11 10:53
 * @description：开通钱包请求对象
 * @modified By：
 */

@Data
public class ChainWalletDto {

    /**
     * 钱包地址
     */
    private String address;

    /**
     * 钱包私钥
     */
    private String privateKey;

    /**
     * 交易密码
     */
    @NotBlank(message = "交易密码不能为空！")
    private String password;

    /**
     * @see ChainType
     * 链类型
     */
    @NotBlank(message = "链类型不能为空！")
    private String chainType;

    /**
     * @see ChainEnv
     * 链环境 test测试网 prd 主网
     */
    @NotBlank(message = "链环境不能为空！")
    private String chainEnv;


}
