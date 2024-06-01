package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.WalletAccount;
import me.flyray.bsin.domain.request.WalletAccountDTO;
import me.flyray.bsin.domain.response.WalletAccountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
* @author Admin
* @description 针对表【crm_wallet_account(钱包账户;)】的数据库操作Mapper
*/

@Repository
@Mapper
public interface WalletAccountMapper extends BaseMapper<WalletAccount> {

//    List<WalletAccount> selectByChainCoinIdAndCustomerId(@Param("chainCoinId") String chainCoinId,@Param("customerId") String customerId);

    Page<WalletAccountVO> pageList(@Param("page") Page<WalletAccount> page, @Param("params") WalletAccountDTO params);

    WalletAccountVO selectBySerialNo(@Param("serialNo") String serialNo);
}




