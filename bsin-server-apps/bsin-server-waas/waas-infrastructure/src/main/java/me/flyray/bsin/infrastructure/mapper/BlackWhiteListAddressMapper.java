package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.BlackWhiteListAddress;
import me.flyray.bsin.domain.request.BlackWhiteListAddressDTO;
import me.flyray.bsin.domain.response.BlackWhiteListAddressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Admin
 */

@Repository
@Mapper
public interface BlackWhiteListAddressMapper extends BaseMapper<BlackWhiteListAddress> {

    List<BlackWhiteListAddressVO> selectList(@Param("dto") BlackWhiteListAddressDTO blackWhiteListAddressDTO);

    Page<BlackWhiteListAddressVO> pageList(Page page,@Param("dto") BlackWhiteListAddressDTO blackWhiteListAddressDTO);

    int updateDelFlag(@Param("params") BlackWhiteListAddress blackWhiteListAddress);
}




