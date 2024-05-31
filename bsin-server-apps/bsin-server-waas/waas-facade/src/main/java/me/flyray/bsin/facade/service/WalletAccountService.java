package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.WalletAccount;
import me.flyray.bsin.domain.request.WalletAccountDTO;
import me.flyray.bsin.domain.response.WalletAccountVO;

import java.util.Map;

/**
* @author Admin
* @description 针对表【crm_wallet_account(钱包账户;)】的数据库操作Service
* @createDate 2024-04-24 20:35:43
*/
public interface WalletAccountService  {

    /**
     * 添加钱包账户
     * @param walletAccountDTO
     * @return
     */
    void add(WalletAccountDTO walletAccountDTO);

    /**
     * 修改账户状态（冻结/解冻）
     * @param walletAccount
     * @return
     */
    void updateAccountStatus(WalletAccount walletAccount);

    /**
     * 分页查询钱包账户
     */
    Page<WalletAccountVO> getPageList(WalletAccountDTO walletAccountDTO);

    /**
     * 获取钱包账户的地址二维码
     * @param serialNo
     * @return
     */
    Map<String,Object> getAddressQrCode(String serialNo);
}
