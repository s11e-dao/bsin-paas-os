package me.flyray.bsin.facade.service.customer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.customer.SettlementAccount;
import me.flyray.bsin.domain.request.customer.SettlementAccountDTO;
import me.flyray.bsin.utils.BsinResultEntity;

import java.util.List;

public interface SettlementAccountService {
    /**
     * 保存结算账户信息
     * @param settlementAccountDTO
     * @return
     * @
     */
    void saveSettlementAccount(SettlementAccountDTO settlementAccountDTO) ;

    /**
     * 编辑结算账户信息
     * @param settlementAccountDTO
     * @return
     * @
     */
    void editSettlementAccount(SettlementAccountDTO settlementAccountDTO) ;

    /**
     * 删除结算账户
     * @param settlementAccountDTO
     * @return
     * @
     */
    void deleteSettlementAccount(SettlementAccountDTO settlementAccountDTO) ;

    /**
     * 分页查询结算账户列表
     * @param settlementAccountDTO
     * @return
     */
    Page<SettlementAccount> pageList(SettlementAccountDTO settlementAccountDTO);
}
