package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.Wallet;
import me.flyray.bsin.domain.request.WalletDTO;
import me.flyray.bsin.domain.response.WalletVO;
import me.flyray.bsin.facade.response.DigitalAssetsDetailRes;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/26 13:51
 * @desc
 */

public interface WalletService {

    /**
     * 开链钱包（链账户开通）
     */
    Map<String, Object> createWallet(Map<String, Object> requestMap) throws Exception;

    /**
     * 创建钱包
     * 1、数据校验
     * 2、保存钱包信息
     * 3、根据钱包类型判断需创建的币种账户，默认钱包则根据客户设置币种创建钱包账户，自定义钱包只创建钱包环境的本币账户
     * 4、创建链上地址，创建钱包账户
     * @return
     */
    public Wallet createMPCWallet(WalletDTO walletDTO);

    /**
     * 基于钱包提现
     * @param walletDTO
     */
    public void withdraw(WalletDTO walletDTO);

    /**
     * 分页查询钱包列表
     * @param walletDTO
     * @return
     */
    public Page<WalletVO> getPageList(WalletDTO walletDTO);

    /**
     * 编辑用户钱包
     * 钱包状态为冻结，则钱包下所有地址状态变为禁用
     * @param walletDTO
     * @return
     */
    public void edit(WalletDTO walletDTO);

    /**
     * 删除钱包
     * @param walletDTO
     */
    public void delete(WalletDTO walletDTO);

    WalletVO getDetail(WalletDTO walletDTO);

}
