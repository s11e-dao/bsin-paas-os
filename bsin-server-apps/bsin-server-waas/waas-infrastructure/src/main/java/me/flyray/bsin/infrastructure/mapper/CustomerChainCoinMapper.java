package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.ChainCoin;
import me.flyray.bsin.domain.entity.CustomerChainCoin;
import me.flyray.bsin.domain.request.CustomerChainCoinDTO;
import me.flyray.bsin.domain.response.CustomerChainCoinVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author Admin
* @description 针对表【merchant_chain_coin(商户链上货币;)】的数据库操作Mapper
*/

@Repository
@Mapper
public interface CustomerChainCoinMapper extends BaseMapper<CustomerChainCoin> {

    List<ChainCoin> selectChainCoinList(@Param("params") CustomerChainCoin customerChainCoin);

    int updateDelFlag(@Param("params") CustomerChainCoin customerChainCoin);

    Page<CustomerChainCoinVO> pageList(Page page , @Param("params") CustomerChainCoinDTO customerChainCoinDTO);
}




