package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.Transaction;
import me.flyray.bsin.domain.request.TransactionDTO;
import me.flyray.bsin.domain.response.TransactionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
* @author Admin
* @description 针对表【crm_transaction(交易记录;)】的数据库操作Mapper
*/

@Repository
@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {

    Page<TransactionVO>  pageList(Page page , @Param("params") TransactionDTO params);
}




