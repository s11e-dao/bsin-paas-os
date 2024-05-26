package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.domain.BlackWhiteListAddress;
import me.flyray.bsin.domain.request.BlackWhiteListAddressDTO;
import me.flyray.bsin.domain.response.BlackWhiteListAddressVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Admin
 * @description 针对表【crm_address_black_white_list(地址黑白名单;)】的数据库操作Mapper
 * @createDate 2024-04-24 20:37:18
 * @Entity xxxxx.domain.CrmAddressBlackWhiteList
 */
public interface BlackWhiteListAddressMapper extends BaseMapper<BlackWhiteListAddress> {

    List<BlackWhiteListAddressVO> selectList(@Param("dto") BlackWhiteListAddressDTO blackWhiteListAddressDTO);

    Page<BlackWhiteListAddressVO> pageList(Page page,@Param("dto") BlackWhiteListAddressDTO blackWhiteListAddressDTO);

    int updateDelFlag(@Param("params") BlackWhiteListAddress blackWhiteListAddress);
}




