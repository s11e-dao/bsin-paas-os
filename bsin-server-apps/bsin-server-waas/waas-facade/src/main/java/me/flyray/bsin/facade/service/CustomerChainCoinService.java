package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.domain.CustomerChainCoin;
import me.flyray.bsin.domain.request.CustomerChainCoinDTO;
import me.flyray.bsin.domain.response.CustomerChainCoinVO;

import java.util.List;

/**
* @author Admin
* @description 针对表【merchant_chain_coin(商户链上货币;)】的数据库操作Service
* @createDate 2024-04-29 15:44:26
*/
public interface CustomerChainCoinService {

    /**
     * 添加商户链上货币
     * @param customerChainCoinDTO
     * @return
     */
    void saveCustomerChainCoin(CustomerChainCoinDTO customerChainCoinDTO);

    /**
     * 编辑商户链上货币
     * @param customerChainCoinDTO
     * @return
     */
    void updateCustomerChainCoin(CustomerChainCoinDTO customerChainCoinDTO);

    /**
     * 删除商户链上货币
     * @param customerChainCoinDTO
     * @return
     */
    void deleteCustomerChainCoin(CustomerChainCoinDTO customerChainCoinDTO);

    /**
     * 查询商户链上货币列表
     * @param customerChainCoinDTO
     * @return
     */
    List<CustomerChainCoin> getCustomerChainCoinList(CustomerChainCoinDTO customerChainCoinDTO);

    /**
     * 分页查询商户链上货币列表
     * @param customerChainCoinDTO
     * @return
     */
    Page<CustomerChainCoinVO> pageList(CustomerChainCoinDTO customerChainCoinDTO);
}
