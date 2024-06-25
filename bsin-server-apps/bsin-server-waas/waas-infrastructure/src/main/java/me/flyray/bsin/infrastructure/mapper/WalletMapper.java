package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.Wallet;
import me.flyray.bsin.domain.request.WalletDTO;
import me.flyray.bsin.domain.response.WalletVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
* @author Admin
* @description 针对表【crm_wallet(钱包;)】的数据库操作Mapper
*/

@Repository
@Mapper
public interface WalletMapper extends BaseMapper<Wallet> {

    Page<WalletVO> pageList(Page<Wallet> page, @Param("wallet") WalletDTO wallet);

    int updateDelFlag(@Param("params") Wallet wallet);

}




