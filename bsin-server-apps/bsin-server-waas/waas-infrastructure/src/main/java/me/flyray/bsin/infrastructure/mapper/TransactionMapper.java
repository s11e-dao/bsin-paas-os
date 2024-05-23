package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.Transaction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.request.transaction.TransactionDTO;
import me.flyray.bsin.domain.response.TransactionVO;
import org.apache.ibatis.annotations.Param;

/**
* @author Admin
* @description 针对表【crm_transaction(交易记录;)】的数据库操作Mapper
* @createDate 2024-04-24 20:36:00
* @Entity xxxxx.domain.CrmTransaction
*/
public interface TransactionMapper extends BaseMapper<Transaction> {

    Page<TransactionVO>  pageList(Page page , @Param("params") TransactionDTO params);
}




