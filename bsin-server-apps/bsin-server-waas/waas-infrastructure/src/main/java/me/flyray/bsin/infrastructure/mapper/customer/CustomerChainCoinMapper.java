package me.flyray.bsin.infrastructure.mapper.customer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.ChainCoin;
import me.flyray.bsin.domain.entity.customer.CustomerChainCoin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.request.customer.CustomerChainCoinDTO;
import me.flyray.bsin.domain.response.CustomerChainCoinVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Admin
* @description 针对表【merchant_chain_coin(商户链上货币;)】的数据库操作Mapper
* @createDate 2024-04-29 15:44:26
* @Entity me.flyray.bsin.domain.entity.MerchantChainCoin
*/
public interface CustomerChainCoinMapper extends BaseMapper<CustomerChainCoin> {

    List<ChainCoin> selectChainCoinList(@Param("params") CustomerChainCoin customerChainCoin);

    int updateDelFlag(@Param("params") CustomerChainCoin customerChainCoin);

    Page<CustomerChainCoinVO> pageList(Page page , @Param("params") CustomerChainCoinDTO customerChainCoinDTO);
}




