package me.flyray.bsin.facade.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.domain.SettlementAccount;
import me.flyray.bsin.domain.request.SettlementAccountDTO;

public interface SettlementAccountService {

    public void saveSettlementAccount(SettlementAccountDTO settlementAccountDTO);

    public void editSettlementAccount(SettlementAccountDTO settlementAccountDTO);

    public void deleteSettlementAccount(SettlementAccountDTO settlementAccountDTO);

    public Page<SettlementAccount> pageList(SettlementAccountDTO settlementAccountDTO);

}
