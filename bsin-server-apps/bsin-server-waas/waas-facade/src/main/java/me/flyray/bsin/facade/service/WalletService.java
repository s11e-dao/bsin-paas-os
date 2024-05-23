package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import me.flyray.bsin.domain.entity.Wallet;
import me.flyray.bsin.domain.request.WalletCreateRequest;
import me.flyray.bsin.domain.request.WalletDTO;
import me.flyray.bsin.domain.response.WalletVO;
import me.flyray.bsin.utils.BsinResultEntity;
import org.springframework.stereotype.Service;

/**
* @author Admin
* @description 针对表【crm_wallet(钱包;)】的数据库操作Service
* @createDate 2024-04-24 20:22:04
*/

public interface WalletService {

    /**
     * 创建钱包
     * 1、数据校验
     * 2、保存钱包信息
     * 3、根据钱包类型判断需创建的币种账户，默认钱包则根据客户设置币种创建钱包账户，自定义钱包只创建钱包环境的本币账户
     * 4、创建链上地址，创建钱包账户
     * @return
     */
    public void createWallet(WalletDTO walletDTO);

    /**
     * (对外)创建钱包
     * @param Wallet
     * walletName 钱包名称
     * businessRoleType 角色类型(必填);1、平台 2、商户 3、代理商 4、C端用户
     * businessRoleNo   角色序号（必填、唯一）
     * tenantId 租户ID（必填）
     */
    public void outCreateWallet(Wallet Wallet);

    /**
     * 分页查询钱包列表
     * @param walletDTO
     * @return
     */
    public Page<WalletVO> pageList(WalletDTO walletDTO);

    /**
     * 编辑用户钱包
     * 钱包状态为冻结，则钱包下所有地址状态变为禁用
     * @param walletDTO
     * @return
     */
    public void editWallet(WalletDTO walletDTO);

    /**
     * 删除钱包
     * @param walletDTO
     */
    public void delWallet(WalletDTO walletDTO);
}
