package me.flyray.bsin.facade.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.SettlementAccount;
import me.flyray.bsin.domain.request.SettlementAccountDTO;

/**
 * 管理商户的结算提现账号
 */
public interface SettlementAccountService {

    public void saveSettlementAccount(SettlementAccountDTO settlementAccountDTO);

    public void editSettlementAccount(SettlementAccountDTO settlementAccountDTO);

    public void deleteSettlementAccount(SettlementAccountDTO settlementAccountDTO);

    public IPage<?> pageList(SettlementAccountDTO settlementAccountDTO);

}
