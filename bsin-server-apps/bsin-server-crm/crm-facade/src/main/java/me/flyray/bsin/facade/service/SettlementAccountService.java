package me.flyray.bsin.facade.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.Platform;
import me.flyray.bsin.domain.entity.SettlementAccount;
import me.flyray.bsin.domain.request.SettlementAccountDTO;

import java.util.List;
import java.util.Map;

/**
 * 用户（商户、客户、代理商）的结算提现账号
 */
public interface SettlementAccountService {

    public void setUp(SettlementAccountDTO settlementAccountDTO);

    public void edit(SettlementAccountDTO settlementAccountDTO);

    public void delete(SettlementAccountDTO settlementAccountDTO);

    public SettlementAccount getDetail(Map<String, Object> requestMap);

    public List<SettlementAccount> getList(SettlementAccountDTO settlementAccountDTO);

    public IPage<?> getPageList(SettlementAccountDTO settlementAccountDTO);

}
